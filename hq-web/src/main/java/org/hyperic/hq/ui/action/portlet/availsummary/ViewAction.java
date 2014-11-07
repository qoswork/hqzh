/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004-2008], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package org.hyperic.hq.ui.action.portlet.availsummary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.appdef.shared.AppdefEntityNotFoundException;
import org.hyperic.hq.appdef.shared.AppdefResourceTypeValue;
import org.hyperic.hq.appdef.shared.AppdefResourceValue;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.bizapp.shared.MeasurementBoss;
import org.hyperic.hq.measurement.MeasurementConstants;
import org.hyperic.hq.measurement.server.session.Measurement;
import org.hyperic.hq.product.MetricValue;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.WebUser;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.exception.ParameterNotFoundException;
import org.hyperic.hq.ui.server.session.DashboardConfig;
import org.hyperic.hq.ui.shared.DashboardManager;
import org.hyperic.hq.ui.util.DashboardUtils;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.util.config.ConfigResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This action class is used by the Availability Summary portlet. It's main use
 * is to generate the JSON objects required for display into the UI.
 */
public class ViewAction
    extends BaseAction {

    private final Log log = LogFactory.getLog(ViewAction.class.getName());
    private final AuthzBoss authzBoss;
    private final MeasurementBoss measurementBoss;
    private final AppdefBoss appdefBoss;
    private final DashboardManager dashboardManager;

    @Autowired
    public ViewAction(AuthzBoss authzBoss, MeasurementBoss measurementBoss, AppdefBoss appdefBoss, DashboardManager dashboardManager) {
        super();
        this.authzBoss = authzBoss;
        this.measurementBoss = measurementBoss;
        this.appdefBoss = appdefBoss;
        this.dashboardManager = dashboardManager;
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        WebUser user = RequestUtils.getWebUser(session);
        DashboardConfig dashConfig = dashboardManager.findDashboard((Integer) session
            .getAttribute(Constants.SELECTED_DASHBOARD_ID), user, authzBoss);
        
        if (dashConfig == null) {
        	return new ActionRedirect("/Dashboard.do");
        }
        
        ConfigResponse dashPrefs = dashConfig.getConfig();

        String token;
        long ts = System.currentTimeMillis();

        try {
            token = RequestUtils.getStringParameter(request, "token");
            if (token != null){
               //token should be alpha-numeric
               if (!token.matches("^[\\w-]*$")){
                   log.warn("Token cleared by xss filter: "+token);
                   token=null;
               }
            }
        } catch (ParameterNotFoundException e) {
            token = null;
        }

        String resKey = PropertiesForm.RESOURCES;
        String numKey = PropertiesForm.NUM_TO_SHOW;
        String titleKey = PropertiesForm.TITLE;
        
        if (token != null) {
            resKey += token;
            numKey += token;
            titleKey += token;
        }

        List<AppdefEntityID> entityIds = DashboardUtils.preferencesAsEntityIds(resKey, dashPrefs);

        // Can only do Platforms, Servers, and Services
        for (Iterator<AppdefEntityID> it = entityIds.iterator(); it.hasNext();) {
            AppdefEntityID aeid = it.next();

            if (aeid.isPlatform() || aeid.isServer() || aeid.isService()) {
                continue;
            }

            it.remove();
        }

        AppdefEntityID[] arrayIds = entityIds.toArray(new AppdefEntityID[entityIds.size()]);
        int count = Integer.parseInt(dashPrefs.getValue(numKey, "10"));
        int sessionId = user.getSessionId().intValue();

        CacheEntry[] ents = new CacheEntry[arrayIds.length];
        List<Integer> measurements = new ArrayList<Integer>(arrayIds.length);
        Map<String, AvailSummary> res = new HashMap<String, AvailSummary>();
        long interval = 0;
        ArrayList<String> toRemove = new ArrayList<String>();
        for (int i = 0; i < arrayIds.length; i++) {
            AppdefEntityID id = arrayIds[i];
            try {
                ents[i] = loadData(sessionId, id);
            } catch (AppdefEntityNotFoundException e) {
                toRemove.add(id.getAppdefKey());
            }

            if (ents[i] != null && ents[i].getMeasurement() != null) {
                measurements.add(i, ents[i].getMeasurement().getId());
                if (ents[i].getMeasurement().getInterval() > interval) {
                    interval = ents[i].getMeasurement().getInterval();
                }
            } else {
                measurements.add(i, null);
            }
        }

        MetricValue[] vals = measurementBoss.getLastMetricValue(sessionId, measurements, interval);

        for (int i = 0; i < ents.length; i++) {
            CacheEntry ent = ents[i];
            MetricValue val = vals[i];

            // If no avail measurement is scheduled, skip this resource
            if (ent != null && val != null) {
                if (ent.getType() == null) {
                    AppdefResourceValue resVal = appdefBoss.findById(sessionId, arrayIds[i]);
                    if (resVal == null) {
                        continue;
                    }
                    ent.setType(resVal.getAppdefResourceTypeValue());
                }

                String name = ent.getType().getName();
                AvailSummary summary = res.get(name);
                if (summary == null) {
                    summary = new AvailSummary(ent.getType());
                    res.put(name, summary);
                }
                summary.setAvailability(val.getValue());
            }
        }

        JSONObject availSummary = new JSONObject();
        List<JSONObject> types = new ArrayList<JSONObject>();

        TreeSet<AvailSummary> sortedSet = new TreeSet<AvailSummary>(new AvailSummaryComparator());
        sortedSet.addAll(res.values());

        for (Iterator<AvailSummary> i = sortedSet.iterator(); i.hasNext() && count-- > 0;) {
            AvailSummary summary = i.next();
            JSONObject typeSummary = new JSONObject();
            typeSummary.put("resourceTypeName", summary.getTypeName());
            typeSummary.put("numUp", summary.getNumUp());
            typeSummary.put("numDown", summary.getNumDown());
            typeSummary.put("appdefType", summary.getAppdefType());
            typeSummary.put("appdefTypeId", summary.getAppdefTypeId());

            types.add(typeSummary);
        }

        availSummary.put("availSummary", types);

        if (token != null) {
            availSummary.put("token", token);
        } else {
            availSummary.put("token", JSONObject.NULL);
        }
        availSummary.put("title", dashPrefs.getValue(titleKey, ""));

        response.getWriter().write(availSummary.toString());

        log.debug("Availability summary loaded in " + (System.currentTimeMillis() - ts) + " ms");

        if (toRemove.size() > 0) {
            log.debug("Removing " + toRemove.size() + " missing resources.");
            DashboardUtils.removeResources(toRemove.toArray(new String[toRemove.size()]), resKey, dashPrefs);
        }

        return null;
    }

    private class AvailSummary {
        private final AppdefResourceTypeValue _type;
        private int _numUp = 0;
        private int _numDown = 0;

        public AvailSummary(AppdefResourceTypeValue type) {
            _type = type;
        }

        public void setAvailability(double avail) {
            if (avail == MeasurementConstants.AVAIL_UP) {
                _numUp++;
            } else {
                _numDown++;
            }
        }

        public String getTypeName() {
            return _type.getName();
        }

        public int getAppdefType() {
            return _type.getAppdefType();
        }

        public Integer getAppdefTypeId() {
            return _type.getId();
        }

        public int getNumUp() {
            return _numUp;
        }

        public int getNumDown() {
            return _numDown;
        }

        public double getAvailPercentage() {
            return (double) _numUp / (_numDown + _numUp);
        }
    }

    private class AvailSummaryComparator implements Comparator<AvailSummary> {

        public int compare(AvailSummary s1, AvailSummary s2) {

            if (s1.getAvailPercentage() == s2.getAvailPercentage()) {
                // Sort on the actual number
                if (s1.getNumDown() != s2.getNumDown()) {
                    return s1.getNumDown() < s2.getNumDown() ? 1 : -1;
                }
                // Sort on type name if equal avail percentage
                return s1.getTypeName().compareTo(s2.getTypeName());
            } else if (s1.getAvailPercentage() < s2.getAvailPercentage()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private CacheEntry loadData(int sessionId, AppdefEntityID id) throws AppdefEntityNotFoundException {
        Cache cache = CacheManager.getInstance().getCache("AvailabilitySummary");
        Element e = cache.get(id);

        if (e != null) {
            return (CacheEntry) e.getObjectValue();
        }

        // Otherwise, load from the backend

        try {
            Measurement m = measurementBoss.findAvailabilityMetric(sessionId, id);

            CacheEntry res = new CacheEntry(m);
            cache.put(new Element(id, res));
            return res;
        } catch (Exception ex) {
            log.debug("Caught exception loading data: " + ex, ex);
            return null;
        }
    }

    // Classes for caching dashboard data
    private class CacheEntry {
        private AppdefResourceTypeValue _type;
        private final Measurement _m;

        public CacheEntry(Measurement m) {
            _m = m;
        }

        public AppdefResourceTypeValue getType() {
            return _type;
        }

        public void setType(AppdefResourceTypeValue type) {
            _type = type;
        }

        public Measurement getMeasurement() {
            return _m;
        }
    }
}
