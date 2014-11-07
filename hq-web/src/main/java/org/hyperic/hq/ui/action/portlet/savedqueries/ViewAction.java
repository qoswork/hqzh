/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006, 2007], Hyperic, Inc.
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

package org.hyperic.hq.ui.action.portlet.savedqueries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.WebUser;
import org.hyperic.hq.ui.server.session.DashboardConfig;
import org.hyperic.hq.ui.shared.DashboardManager;
import org.hyperic.hq.ui.util.CheckPermissionsUtil;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.hq.ui.util.SessionUtils;
import org.hyperic.util.StringUtil;
import org.hyperic.util.config.ConfigResponse;
import org.hyperic.util.timer.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An <code>Action</code> that loads the <code>Portal</code> identified by the
 * <code>PORTAL_PARAM</code> request parameter (or the default portal, if the
 * parameter is not specified) into the <code>PORTAL_KEY</code> request
 * attribute.
 */
public class ViewAction
    extends TilesAction {

    private final Log log = LogFactory.getLog(ViewAction.class.getName());
    private AuthzBoss authzBoss;
    private AppdefBoss appdefBoss;
    private DashboardManager dashboardManager;
    private final Log timingLog = LogFactory.getLog("DASHBOARD-TIMING");

    @Autowired
    public ViewAction(AuthzBoss authzBoss, AppdefBoss appdefBoss, DashboardManager dashboardManager) {
        super();
        this.authzBoss = authzBoss;
        this.appdefBoss = appdefBoss;
        this.dashboardManager = dashboardManager;
    }

    public ActionForward execute(ComponentContext context, ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        StopWatch timer = new StopWatch();

        HttpSession session = request.getSession();

        WebUser user = SessionUtils.getWebUser(session);
        DashboardConfig dashConfig = dashboardManager.findDashboard((Integer) session
            .getAttribute(Constants.SELECTED_DASHBOARD_ID), user, authzBoss);
        ConfigResponse dashPrefs = dashConfig.getConfig();

        // get all the displayed subtypes

        List<String> chartList = null;

        try {
            chartList = StringUtil.explode(dashPrefs.getValue(Constants.USER_DASHBOARD_CHARTS),
                Constants.DASHBOARD_DELIMITER);
        } catch (RuntimeException e) {

        }

        if (chartList != null) {
            List<KeyValuePair> charts = new ArrayList<KeyValuePair>();

            for (String chartListStr : chartList) {

                List<String> chart = StringUtil.explode(chartListStr, ",");

                // the saved chart preference should have exactly two
                // elements: the name of the chart and the URL... so there
                // are things that can break stuff: commas or pipes in the name
                // of the chart -- these will be encoded in the action that
                // saves
                // the preference but just to be safe, well defend against
                // bogosity

                // if something bjorked the preference stringification
                // scheme, we can't display diddly squat about the preference
                if (chart.size() != 2) {
                    // it's amazing but true: bogosity has been found
                    if (log.isTraceEnabled()) {
                        log.trace("chart preference not understood: " + chart);
                    }
                    continue;
                }

                // Determine what entityIds can be viewed by this user
                // This code probably should be in the boss somewhere but
                // for now doing it here...
                if (CheckPermissionsUtil.canUserViewChart(RequestUtils.getSessionId(request).intValue(), chart.get(1),
                    appdefBoss)) {
                    Iterator<String> j = chart.iterator();
                    String name = j.next();
                    String url = j.next();
                    // the name might be generated by user input, we need to
                    // make
                    // sure
                    // their delimiters' presence in the names are deserialized
                    // from
                    // the
                    // preference system
                    name = StringUtil.replace(name, "&#124;", Constants.DASHBOARD_DELIMITER);
                    name = StringUtil.replace(name, "&#44;", ",");

                    charts.add(new KeyValuePair(name, url));
                }
            }

            context.putAttribute("charts", charts);
        } else {
            context.putAttribute("charts", new ArrayList<KeyValuePair>());
        }
        timingLog.trace("SavedQueries - timing [" + timer.toString() + "]");

        return null;
    }
}