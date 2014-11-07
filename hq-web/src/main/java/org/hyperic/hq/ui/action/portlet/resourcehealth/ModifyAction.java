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

package org.hyperic.hq.ui.action.portlet.resourcehealth;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.StringConstants;
import org.hyperic.hq.ui.WebUser;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.server.session.DashboardConfig;
import org.hyperic.hq.ui.shared.DashboardManager;
import org.hyperic.hq.ui.util.ConfigurationProxy;
import org.hyperic.hq.ui.util.DashboardUtils;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.util.StringUtil;
import org.hyperic.util.config.ConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An <code>Action</code> that loads the <code>Portal</code> identified by the
 * <code>PORTAL_PARAM<    /code> request parameter (or the default portal, if
 * the parameter is not specified) into the <code>PORTAL_KEY</code> request
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
        WebUser user = RequestUtils.getWebUser(request);

        DashboardConfig dashConfig = dashboardManager.findDashboard((Integer) session
            .getAttribute(Constants.SELECTED_DASHBOARD_ID), user, authzBoss);
        ConfigResponse dashPrefs = dashConfig.getConfig();
        String forwardStr = "success";

        if (pForm.isRemoveClicked()) {
            DashboardUtils.removeResources(pForm.getIds(), Constants.USERPREF_KEY_FAVORITE_RESOURCES, dashPrefs);
            forwardStr = "review";
            configurationProxy.setDashboardPreferences(session, user, dashPrefs);
        }

        ActionForward forward = checkSubmit(request, mapping, form);

        if (forward != null) {
            return forward;
        }

        // Set the order of resources
        String order = StringUtil.replace(pForm.getOrder(), "%3A", ":");
        StringTokenizer orderTK = new StringTokenizer(order, "=&");
        ArrayList<String> resources = new ArrayList<String>();
        while (orderTK.hasMoreTokens()) {
            orderTK.nextToken();
            resources.add(orderTK.nextToken());
        }
        configurationProxy.setPreference(session, user, Constants.USERPREF_KEY_FAVORITE_RESOURCES, StringUtil
            .listToString(resources, StringConstants.DASHBOARD_DELIMITER));

        LogFactory.getLog("user.preferences").trace(
            "Invoking setUserPrefs" + " in resourcehealth/ModifyAction " + " for " + user.getId() + " at " +
                System.currentTimeMillis() + " user.prefs = " + dashPrefs.getKeys().toString());

        session.removeAttribute(Constants.USERS_SES_PORTAL);
        return mapping.findForward(forwardStr);
    }
}
