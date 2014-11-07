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

package org.hyperic.hq.ui.action.portlet.autoDisc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.appdef.shared.AIIpValue;
import org.hyperic.hq.appdef.shared.AIPlatformValue;
import org.hyperic.hq.appdef.shared.AIQueueConstants;
import org.hyperic.hq.appdef.shared.AIServerValue;
import org.hyperic.hq.bizapp.shared.AIBoss;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.WebUser;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.server.session.DashboardConfig;
import org.hyperic.hq.ui.shared.DashboardManager;
import org.hyperic.hq.ui.util.BizappUtils;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.util.config.ConfigResponse;
import org.hyperic.util.pager.PageControl;
import org.hyperic.util.pager.PageList;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessQueueAction
    extends BaseAction {

    private AIBoss aiBoss;
    private AppdefBoss appdefBoss;
    private AuthzBoss authzBoss;
    private DashboardManager dashboardManager;

    @Autowired
    public ProcessQueueAction(AIBoss aiBoss, AppdefBoss appdefBoss, AuthzBoss authzBoss, DashboardManager dashboardManager) {
        super();
        this.aiBoss = aiBoss;
        this.appdefBoss = appdefBoss;
        this.authzBoss = authzBoss;
        this.dashboardManager = dashboardManager;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        WebUser user = RequestUtils.getWebUser(request);
        int sessionId = user.getSessionId().intValue();

        AIQueueForm queueForm = (AIQueueForm) form;
        Integer[] aiPlatformIds = queueForm.getPlatformsToProcess();
        Integer[] aiServerIds = queueForm.getServersToProcess();
        int queueAction = queueForm.getQueueAction();
        boolean isApproval = (queueAction == AIQueueConstants.Q_DECISION_APPROVE);
        boolean isIgnore = (queueAction == AIQueueConstants.Q_DECISION_IGNORE);

        List<Integer> aiPlatformList = new ArrayList<Integer>();
        List<Integer> aiIpList = new ArrayList<Integer>();
        List<Integer> aiServerList = new ArrayList<Integer>();

        // Refresh the queue items this user can see.
        HttpSession session = request.getSession();
        PageControl page = new PageControl();

        DashboardConfig dashConfig = dashboardManager.findDashboard((Integer) session
            .getAttribute(Constants.SELECTED_DASHBOARD_ID), user, authzBoss);
        ConfigResponse dashPrefs = dashConfig.getConfig();
        page.setPagesize(Integer.parseInt(dashPrefs.getValue(".dashContent.autoDiscovery.range")));

        PageList<AIPlatformValue> aiQueue = aiBoss.getQueue(sessionId, true, false, true, page);

        // Walk the queue. For each platform in the queue:
        // 
        // 1. If it's selected for processing, add all of its IPs (and later,
        // all of it's virtual servers) for processing. If it's selected
        // for removal, remove all servers, not just virtual ones.
        //
        // 2. If any of its servers are selected for APPROVAL, then select
        // the platform for approval as well.
        // 
        int pidx, sidx;
        for (int i = 0; i < aiQueue.size(); i++) {
            AIPlatformValue aiPlatform = (AIPlatformValue) aiQueue.get(i);
            pidx = isSelectedForProcessing(aiPlatform, aiPlatformIds);
            if (pidx == -1) {
                // platform isnt selected
                continue;
            }

            aiPlatformList.add(aiPlatformIds[pidx]);

            AIIpValue[] ips = aiPlatform.getAIIpValues();
            for (int j = 0; j < ips.length; j++)
                aiIpList.add(ips[j].getId());

            AIServerValue[] aiServers = aiPlatform.getAIServerValues();
            // Now check servers on this platform
            for (int j = 0; j < aiServers.length; j++) {
                sidx = isSelectedForProcessing(aiServers[j], aiServerIds);
                if (sidx != -1) {
                    // If we're approving stuff, and this platform's not
                    // already in the list, add it
                    if (isApproval && !aiPlatformList.contains(aiPlatform.getId())) {
                        aiPlatformList.add(aiPlatform.getId());
                    }

                    // Add the server (XXX: Maybe we shouldn't add it if the
                    // server
                    // is ignored?)
                    aiServerList.add(aiServers[j].getId());

                    // Set error flag if the server is modified and the user
                    // tries to ignore it.
                    if (isIgnore && aiServers[j].getQueueStatus() != AIQueueConstants.Q_STATUS_ADDED) {

                        request.getSession().setAttribute(Constants.IMPORT_IGNORE_ERROR_ATTR, Boolean.TRUE);
                    }
                } else if (isApproval && BizappUtils.isAutoApprovedServer(sessionId, appdefBoss, aiServers[j])) {
                    // All virtual servers are approved when their platform
                    // is approved. The HQ agent is also auto-approved.
                    aiServerList.add(aiServers[j].getId());
                }
            }
        }

        if (aiServerList.isEmpty() && isIgnore) {
            // Change to purge
            queueAction = AIQueueConstants.Q_DECISION_PURGE;
        }

        try {
            aiBoss.processQueue(sessionId, aiPlatformList, aiServerList, aiIpList, queueAction);
        } catch (Exception e) {
            request.getSession().setAttribute(Constants.IMPORT_ERROR_ATTR, e);
        }

        return returnSuccess(request, mapping);
    }

    private int isSelectedForProcessing(AIPlatformValue aiPlatform, Integer[] platformsToProcess) {
        Integer id = aiPlatform.getId();
        for (int i = 0; i < platformsToProcess.length; i++) {
            if (platformsToProcess[i].equals(id))
                return i;
        }
        return -1;
    }

    private int isSelectedForProcessing(AIServerValue aiServer, Integer[] serversToProcess) {
        Integer id = aiServer.getId();
        for (int i = 0; i < serversToProcess.length; i++) {
            if (serversToProcess[i].equals(id))
                return i;
        }
        return -1;
    }
}
