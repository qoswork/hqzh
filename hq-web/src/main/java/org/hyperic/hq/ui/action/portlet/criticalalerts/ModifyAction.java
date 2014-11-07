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

package org.hyperic.hq.ui.action.portlet.criticalalerts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.WebUser;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.server.session.DashboardConfig;
import org.hyperic.hq.ui.shared.DashboardManager;
import org.hyperic.hq.ui.util.ConfigurationProxy;
import org.hyperic.hq.ui.util.DashboardUtils;
import org.hyperic.hq.ui.util.SessionUtils;
import org.hyperic.util.config.ConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An <code>Action</code> that loads the <code>Portal</code> identified by the
 * <code>PORTAL_PARAM</code> request parameter (or the default portal, if the
 * parameter is not specified) into the <code>PORTAL_KEY</code> request
 * attribute.
 */
public class ModifyAction
    extends BaseAction {

    private ConfigurationProxy configurationProxy;
    private AuthzBoss authzBoss;
    private DashboardManager dashboardManager;

    @Autowired
    public ModifyAction(ConfigurationProxy configurationProxy, AuthzBoss authzBoss, DashboardManager dashboardManager) {
        super();
        this.configurationProxy = configurationProxy;
        this.authzBoss = authzBoss;
        this.dashboardManager = dashboardManager;
    }

    /**
     * 
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * 
     * @exception Exception if the application business logic throws an
     *            exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        PropertiesForm pForm = (PropertiesForm) form;
        HttpSession session = request.getSession();
        WebUser user = SessionUtils.getWebUser(session);

        String forwardStr = Constants.SUCCESS_URL;

        String token = pForm.getToken();

        // For multi-portlet configurations
        String resKey = ViewAction.RESOURCES_KEY;
        String countKey = PropertiesForm.ALERT_NUMBER;
        String priorityKey = PropertiesForm.PRIORITY;
        String timeKey = PropertiesForm.PAST;
        String selOrAllKey = PropertiesForm.SELECTED_OR_ALL;
        String titleKey = PropertiesForm.TITLE;

        if (token != null) {
            resKey += token;
            countKey += token;
            priorityKey += token;
            timeKey += token;
            selOrAllKey += token;
            titleKey += token;
        }
        DashboardConfig dashConfig = dashboardManager.findDashboard((Integer) session
            .getAttribute(Constants.SELECTED_DASHBOARD_ID), user, authzBoss);
        ConfigResponse dashPrefs = dashConfig.getConfig();

        if (pForm.isRemoveClicked()) {
            DashboardUtils.removeResources(pForm.getIds(), resKey, dashPrefs);
            configurationProxy.setDashboardPreferences(session, user, dashPrefs);
        }

        ActionForward forward = checkSubmit(request, mapping, form);

        if (forward != null) {
            return forward;
        }

        Integer numberOfAlerts = pForm.getNumberOfAlerts();
        String past = String.valueOf(pForm.getPast());
        String prioritity = pForm.getPriority();
        String selectedOrAll = pForm.getSelectedOrAll();

        dashPrefs.setValue(countKey, numberOfAlerts.toString());
        dashPrefs.setValue(timeKey, past);
        dashPrefs.setValue(priorityKey, prioritity);
        dashPrefs.setValue(selOrAllKey, selectedOrAll);
        dashPrefs.setValue(titleKey, pForm.getTitle());

        configurationProxy.setDashboardPreferences(session, user, dashPrefs);

        session.removeAttribute(Constants.USERS_SES_PORTAL);
        return mapping.findForward(forwardStr);
    }
}
