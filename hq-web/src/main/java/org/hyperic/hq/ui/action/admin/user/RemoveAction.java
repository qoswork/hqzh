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

package org.hyperic.hq.ui.action.admin.user;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An Action that removes a user from the application.
 */
public class RemoveAction
    extends BaseAction {

    private final Log log = LogFactory.getLog(RemoveAction.class.getName());
    private AuthzBoss authzBoss;

    @Autowired
    public RemoveAction(AuthzBoss authzBoss) {
        super();
        this.authzBoss = authzBoss;
    }

    /**
     * Removes a user identified by the value of the request parameter
     * <code>Constants.USER_PARAM</code> from the BizApp.
     * @return
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        RemoveForm nwForm = (RemoveForm) form;

        Integer[] users = nwForm.getUsers();

        // maintain sort after remove
        HashMap<String, Object> params = new HashMap<String, Object>();
        RequestUtils.propogateParam(request, params, Constants.SORTORDER_PARAM);
        RequestUtils.propogateParam(request, params, Constants.SORTCOL_PARAM);

        if (users == null || users.length == 0) {
            return returnSuccess(request, mapping, params, false);
        }

        Integer sessionId = RequestUtils.getSessionId(request);

        log.trace("removing users");
        try {
            authzBoss.removeSubject(sessionId, users);
        } catch (Exception e) {
            log.error("Error deleting user", e);
            throw e;
	}

        RequestUtils.setConfirmation(request, "admin.role.confirm.RemoveUsers");
        return returnSuccess(request, mapping, params, false);
    }
}
