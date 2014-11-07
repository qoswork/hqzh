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

package org.hyperic.hq.ui.action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.actions.TilesAction;
import org.apache.struts.tiles.ComponentContext;

import org.hyperic.hq.ui.util.SessionUtils;

/**
 * This Action is designed to be the start of a workflow. It is for prepare
 * actions. It duplicates code in WorkflowPrepareAction.
 */
public abstract class WorkflowPrepareAction
    extends TilesAction {

    /**
     * Starts workflow by taking what is currently stored in the session as
     * returnPath (one of many possible origins of this workflow), and pushing
     * it onto our workflow stack.
     * 
     * Passes call to workflow() so that the child class can overload.
     */
    public final ActionForward execute(ComponentContext context, ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (!(mapping instanceof BaseActionMapping)) {
            throw new ServletException("mapping " + mapping.getName() + "is not an instance of BaseActionMapping.");
        }
        BaseActionMapping smap = (BaseActionMapping) mapping;

        String workflowId = smap.getWorkflow();
        if (workflowId == null || "".equals(workflowId.trim())) {
            throw new ServletException("workflow " + smap.getName() + " has a null or invalid workflow attribute.");
        }

        // takes the current returnPath in the session and stores it
        // in the workflow.
        SessionUtils.pushWorkflow(request.getSession(false), mapping, workflowId);

        return workflow(context, mapping, form, request, response);
    }

    /**
     * To participate in a workflow, simply implement this the same as you would
     * execute(). This will be called by WorkflowActions's execute() method
     * after it saves the workflow context.
     */
    public abstract ActionForward workflow(ComponentContext context, ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception;
}
