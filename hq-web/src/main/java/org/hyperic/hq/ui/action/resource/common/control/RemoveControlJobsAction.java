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

package org.hyperic.hq.ui.action.resource.common.control;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.bizapp.shared.ControlBoss;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.hq.ui.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An Action that removes a control event from a server.
 */
public class RemoveControlJobsAction
    extends BaseAction {

    private final Log log = LogFactory.getLog(RemoveControlJobsAction.class.getName());
    private ControlBoss controlBoss;

    @Autowired
    public RemoveControlJobsAction(ControlBoss controlBoss) {
        super();
        this.controlBoss = controlBoss;
    }

    /**
     * Removes control jobs from a server.
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        HashMap<String, Object> parms = new HashMap<String, Object>(2);

        try {
            RemoveControlJobsForm rmForm = (RemoveControlJobsForm) form;
            Integer[] jobs = rmForm.getControlJobs();

            AppdefEntityID aeid = RequestUtils.getEntityId(request);

            parms.put(Constants.RESOURCE_PARAM, aeid.getId());
            parms.put(Constants.RESOURCE_TYPE_ID_PARAM, new Integer(aeid.getType()));

            if (jobs == null || jobs.length == 0) {
                return this.returnSuccess(request, mapping, parms);
            }

            Integer sessionId = RequestUtils.getSessionId(request);

            controlBoss.deleteControlJob(sessionId.intValue(), jobs);

            log.trace("Removed resource control jobs.");
            SessionUtils.setConfirmation(request.getSession(false), "resource.common.control.confirm.ScheduledRemoved");
            return this.returnSuccess(request, mapping, parms);

        } catch (PluginException cpe) {
            log.debug("There was a problem removing control jobs: ", cpe);
            SessionUtils.setError(request.getSession(false), "resource.common.control.error.CouldNotRemoveScheduled");
            return returnFailure(request, mapping, parms);
        }
    }
}
