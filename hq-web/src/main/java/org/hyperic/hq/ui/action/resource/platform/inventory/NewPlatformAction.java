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

package org.hyperic.hq.ui.action.resource.platform.inventory;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.appdef.Agent;
import org.hyperic.hq.appdef.server.session.Platform;
import org.hyperic.hq.appdef.server.session.PlatformType;
import org.hyperic.hq.appdef.shared.AppdefDuplicateFQDNException;
import org.hyperic.hq.appdef.shared.AppdefDuplicateNameException;
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.appdef.shared.PlatformValue;
import org.hyperic.hq.bizapp.shared.AIBoss;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.common.ApplicationException;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.action.resource.platform.PlatformForm;
import org.hyperic.hq.ui.util.BizappUtils;
import org.hyperic.hq.ui.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A <code>BaseAction</code> subclass that creates a platform in the BizApp.
 */
public class NewPlatformAction
    extends BaseAction {

    private final Log log = LogFactory.getLog(NewPlatformAction.class.getName());
    private AppdefBoss appdefBoss;
    private AIBoss aiBoss;

    @Autowired
    public NewPlatformAction(AppdefBoss appdefBoss, AIBoss aiBoss) {
        super();
        this.appdefBoss = appdefBoss;
        this.aiBoss = aiBoss;
    }

    /**
     * Create the platform with the attributes specified in the given
     * <code>PlatformForm</code>.
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        PlatformForm newForm = (PlatformForm) form;

        try {
            ActionForward forward = checkSubmit(request, mapping, form);
            if (forward != null) {
                return forward;
            }

            Integer sessionId = RequestUtils.getSessionId(request);

            // first make sure the form's "machine type" represents a
            // valid platform type
            Integer platformTypeId = newForm.getResourceType();
            log.trace("finding platform type [" + platformTypeId + "]");
            PlatformType platformType = appdefBoss.findPlatformTypeById(sessionId.intValue(), platformTypeId);

            // now set up the new platform
            PlatformValue platform = new PlatformValue();
            newForm.updatePlatformValue(platform);
            platform.setCpuCount(new Integer(1)); // at least

            log.trace("creating platform [" + platform.getName() + "]" + " with attributes " + newForm);

            Agent agent = BizappUtils.getAgentConnection(sessionId.intValue(), appdefBoss, request, newForm);

            Platform newPlatform = appdefBoss.createPlatform(sessionId.intValue(), platform, platformType.getId(),
                agent.getId());

            Integer platformId = newPlatform.getId();
            newForm.setRid(platformId);

            AppdefEntityID entityId = newPlatform.getEntityId();

            ServletContext ctx = getServlet().getServletContext();
            BizappUtils.startAutoScan(ctx, sessionId.intValue(), entityId, aiBoss);

            Integer entityType = new Integer(newPlatform.getEntityId().getType());
            newForm.setType(entityType);

            RequestUtils.setConfirmation(request, "resource.platform.inventory.confirm.Create", platform.getName());

            HashMap<String, Object> forwardParams = new HashMap<String, Object>(2);
            forwardParams.put(Constants.RESOURCE_PARAM, platformId);
            forwardParams.put(Constants.RESOURCE_TYPE_ID_PARAM, entityType);

            return returnNew(request, mapping, forwardParams);
        } catch (AppdefDuplicateNameException e1) {
            RequestUtils.setError(request, "resource.platform.inventory.error.DuplicateName");
            return returnFailure(request, mapping);
        } catch (AppdefDuplicateFQDNException e1) {
            RequestUtils.setError(request, "resource.platform.inventory.error.DuplicateFQDN");
            return returnFailure(request, mapping);
        } catch (ApplicationException e) {
            RequestUtils.setErrorObject(request, "dash.autoDiscovery.import.Error", e.getMessage());
            return returnFailure(request, mapping);
        }
    }
}
