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

package org.hyperic.hq.ui.action.resource.application.inventory;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.appdef.server.session.ApplicationType;
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.appdef.shared.ApplicationValue;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.action.resource.application.ApplicationForm;
import org.hyperic.hq.ui.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class handles saving edit operations performed on Application Properties
 * (screen 2.1.6.2)
 */
public class EditApplicationPropertiesAction
    extends BaseAction {

    private final Log log = LogFactory.getLog(EditApplicationPropertiesAction.class.getName());
    private AppdefBoss appdefBoss;

    @Autowired
    public EditApplicationPropertiesAction(AppdefBoss appdefBoss) {
        super();
        this.appdefBoss = appdefBoss;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        ApplicationForm appForm = (ApplicationForm) form;
        AppdefEntityID aeid = new AppdefEntityID(appForm.getType().intValue(), appForm.getRid());

        HashMap<String, Object> forwardParams = new HashMap<String, Object>(2);
        forwardParams.put(Constants.ENTITY_ID_PARAM, aeid.getAppdefKey());
        forwardParams.put(Constants.ACCORDION_PARAM, "1");

        ActionForward forward = checkSubmit(request, mapping, form, forwardParams);
        if (forward != null) {
            return forward;
        }

        Integer sessionId = RequestUtils.getSessionId(request);

        Integer applicationTypeId = appForm.getResourceType();
        // XXX there is no findApplicationTypeById(...) boss signature, so
        // we'll hope for the best .... when the api is updated, we'll use it
        /*
         */
        // List applicationTypes =
        // appdefBoss.findAllApplicationTypes(sessionId.intValue());
        log.trace("finding application type [" + applicationTypeId + "]");
        ApplicationType applicationType = appdefBoss.findApplicationTypeById(sessionId.intValue(), applicationTypeId);

        // now set up the application
        ApplicationValue appVal = appdefBoss.findApplicationById(sessionId.intValue(), aeid.getId());
        if (appVal == null) {
            RequestUtils.setError(request, "resource.application.error.ApplicationNotFound");
            return returnFailure(request, mapping, forwardParams);
        }

        appForm.updateResourceValue(appVal);
        appVal.setApplicationType(applicationType);

        log.trace("updating general properties of application [" + appVal.getName() + "]" + " with attributes " +
                  appVal);
        appdefBoss.updateApplication(sessionId.intValue(), appVal);

        RequestUtils.setConfirmation(request, "resource.application.inventory.confirm.EditGeneralProperties", appVal
            .getName());
        return returnSuccess(request, mapping, forwardParams);

    }
}
