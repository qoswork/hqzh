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

package org.hyperic.hq.ui.action.admin.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.authz.server.session.AuthzSubject;
import org.hyperic.hq.authz.server.session.Operation;
import org.hyperic.hq.authz.shared.AuthzConstants;
import org.hyperic.hq.bizapp.shared.AuthBoss;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An <code>BaseAction</code> subclass that edit's a user's passworrd in the
 * BizApp.
 */
public class EditPasswordAction
    extends BaseAction {

    private final Log log = LogFactory.getLog(NewAction.class.getName());
    private AuthBoss authBoss;
    private AuthzBoss authzBoss;
    

    @Autowired
    public EditPasswordAction(AuthBoss authBoss, AuthzBoss authzBoss) {
        super();
        this.authBoss = authBoss;
        this.authzBoss = authzBoss;
    }

    /**
     * Edit the user's password. Make sure that the <code>currentPassword</code>
     * is valid.
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        log.trace("Editing password for user.");

        EditPasswordForm pForm = (EditPasswordForm) form;
        ActionForward forward = checkSubmit(request, mapping, form, Constants.USER_PARAM, pForm.getId());

        if (forward != null) {
            return forward;
        }

        Integer sessionId = RequestUtils.getSessionId(request);

        AuthzSubject user = authzBoss.findSubjectById(RequestUtils.getSessionId(request), pForm.getId());

        log.trace("Editing user's password.");

      
        try {
            authBoss.authenticate(RequestUtils.getWebUser(request).getName(), pForm.getCurrentPassword());
        } catch (Exception e) {
            RequestUtils.setError(request, "admin.user.error.WrongPassword", "currentPassword");
            return returnFailure(request, mapping, Constants.USER_PARAM, pForm.getId());
        }
        authBoss.changePassword(sessionId.intValue(), user.getName(), pForm.getNewPassword());

        return returnSuccess(request, mapping, Constants.USER_PARAM,

        pForm.getId());

    }
}
