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

package org.hyperic.hq.ui.action.resource.common.monitor.alerts.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.authz.shared.AuthzSubjectValue;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.bizapp.shared.EventsBoss;
import org.hyperic.hq.bizapp.shared.MeasurementBoss;
import org.hyperic.hq.escalation.server.session.Escalation;
import org.hyperic.hq.escalation.server.session.EscalationAlertType;
import org.hyperic.hq.events.AlertPermissionManager;
import org.hyperic.hq.events.server.session.ClassicEscalationAlertType;
import org.hyperic.hq.galerts.server.session.GalertEscalationAlertType;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.exception.ParameterNotFoundException;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.util.pager.PageControl;
import org.hyperic.util.pager.PageList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewEscalationAction
    extends ViewDefinitionAction {

   

    @Autowired
    public ViewEscalationAction(EventsBoss eventsBoss, MeasurementBoss measurementBoss, AuthzBoss authzBoss, 
                                AlertPermissionManager alertPermissionManager) {
        super(eventsBoss, measurementBoss, authzBoss, alertPermissionManager);
      
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        Integer sessionID = RequestUtils.getSessionId(request);
        int sessionId = sessionID.intValue();

        // Get alert type
        EscalationAlertType mat = ClassicEscalationAlertType.CLASSIC;
        try {
            RequestUtils.getEntityTypeId(request);
        } catch (ParameterNotFoundException e) {
            AppdefEntityID aeid = RequestUtils.getEntityId(request);
            if (aeid.isGroup()) {
                mat = GalertEscalationAlertType.GALERT;
            }
        }

        // Get the list of escalations

        if (request.getAttribute("escalations") == null) {
            JSONArray arr = eventsBoss.listAllEscalationName(sessionId);
            request.setAttribute("escalations", arr);
        }

        // Get the list of users

        PageList<AuthzSubjectValue> availableUsers = authzBoss.getAllSubjects(sessionID, null, PageControl.PAGE_ALL);
        request.setAttribute(Constants.AVAIL_USERS_ATTR, availableUsers);

        EscalationSchemeForm eForm = (EscalationSchemeForm) form;
        if (eForm.getEscId() == null) {
            eForm.setEscId(eventsBoss.getEscalationIdByAlertDefId(sessionId, new Integer(eForm.getAd()), mat));
        } else {
            if (eForm.getEscId().intValue() == 0) {
                // Unset current escalation scheme
                eventsBoss.unsetEscalationByAlertDefId(sessionId, new Integer(eForm.getAd()), mat);
                eForm.setEscId(null);
            } else {
                // We actually need to set the escalation scheme for alert
                // definition
                eventsBoss.setEscalationByAlertDefId(sessionId, new Integer(eForm.getAd()), eForm.getEscId(), mat);
            }
        }

        // Look for the escalation request parameter
        try {
            if (eForm.getEscId() != null) {
                JSONObject escalation = Escalation.getJSON(eventsBoss.findEscalationById(sessionId, eForm.getEscId()));
                request.setAttribute("escalationJSON", escalation.toString());
            }
        } catch (ParameterNotFoundException e) {
            // No big deal, assume new
        }

        return super.execute(mapping, form, request, response);
    }
}
