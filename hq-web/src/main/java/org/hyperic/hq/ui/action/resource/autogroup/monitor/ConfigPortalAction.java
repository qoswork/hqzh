/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
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

package org.hyperic.hq.ui.action.resource.autogroup.monitor;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.bizapp.shared.ControlBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.Portal;
import org.hyperic.hq.ui.action.resource.common.monitor.config.ResourceConfigPortalAction;
import org.hyperic.hq.ui.exception.ParameterNotFoundException;
import org.hyperic.hq.ui.util.BizappUtils;
import org.hyperic.hq.ui.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * This action prepares the portal for configuring autogroup monitoring.
 */
public class ConfigPortalAction
    extends ResourceConfigPortalAction {

    private static final String CONFIG_METRICS_PORTAL = ".resource.autogroup.monitor.config.ConfigMetrics";

    private static final String CONFIG_METRICS_TITLE = "resource.autogroup.monitor.visibility.config.ConfigureVisibility.Title";

    private static final String ADD_METRICS_PORTAL = ".resource.autogroup.monitor.config.AddMetrics";

    private static final String ADD_METRICS_TITLE = "resource.autogroup.monitor.visibility.config.AddMetrics.Title";

    private static final String EDIT_AVAILABILITY_PORTAL = ".resource.autogroup.monitor.config.EditAvailability";

    private static final String EDIT_AVAILABILITY_TITLE = "resource.autogroup.monitor.visibility.config.ConfigureVisibility.EditGroupAvail.Title";

    @Autowired
    public ConfigPortalAction(AppdefBoss appdefBoss, AuthzBoss authzBoss, ControlBoss controlBoss) {
        super(appdefBoss, authzBoss, controlBoss);
    }

    /*
     * (non javadoc)
     * 
     * @see org.hyperic.hq.ui.action.BaseDispatchAction#getKeyMethodMap()
     */
    protected Properties getKeyMethodMap() {
        Properties map = new Properties();
        map.setProperty(Constants.MODE_ADD_METRICS, "addMetrics");
        map.setProperty(Constants.MODE_CONFIGURE, "configMetrics");
        map.setProperty(Constants.MODE_EDIT, "editAvailability");
        map.setProperty(Constants.MODE_LIST, "configMetrics");
        return map;
    }

    /** mode=configure || mode=view */
    public ActionForward configMetrics(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {

        setResource(request, response);

        super.configMetrics(mapping, form, request, response);

        Portal portal = Portal.createPortal(CONFIG_METRICS_TITLE, CONFIG_METRICS_PORTAL);
        request.setAttribute(Constants.PORTAL_KEY, portal);
        return null;

    }

    /** mode=addMetrics */
    public ActionForward addMetrics(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {

        setResource(request, response);
        Portal portal = Portal.createPortal(ADD_METRICS_TITLE, ADD_METRICS_PORTAL);
        portal.setDialog(true);
        request.setAttribute(Constants.PORTAL_KEY, portal);
        return null;

    }

    /** mode=edit */
    public ActionForward editAvailability(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {

        setResource(request, response);
        Portal portal = Portal.createPortal(EDIT_AVAILABILITY_TITLE, EDIT_AVAILABILITY_PORTAL);
        portal.setDialog(true);
        request.setAttribute(Constants.PORTAL_KEY, portal);
        return null;
    }

    protected AppdefEntityID getAppdefEntityID(HttpServletRequest request) throws ParameterNotFoundException {

        // when we've fully migrated CAM to eid, this method can go away

        try {
            AppdefEntityID[] eids = RequestUtils.getEntityIds(request);

            // take this opportunity to cache the entity ids in the request
            request.setAttribute(Constants.ENTITY_IDS_ATTR, BizappUtils.stringifyEntityIds(eids));

            return eids[0];
        } catch (ParameterNotFoundException e) {
            log.trace("No entity ids -- must be auto-group of platforms.");
            return null;
        }
    }
}
