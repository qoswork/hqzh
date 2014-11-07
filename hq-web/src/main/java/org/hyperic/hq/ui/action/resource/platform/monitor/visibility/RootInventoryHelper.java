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

package org.hyperic.hq.ui.action.resource.platform.monitor.visibility;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.appdef.server.session.AppdefResourceType;
import org.hyperic.hq.appdef.shared.AppdefEntityNotFoundException;
import org.hyperic.hq.appdef.shared.AppdefEntityTypeID;
import org.hyperic.hq.appdef.shared.AppdefResourceValue;
import org.hyperic.hq.appdef.shared.PlatformNotFoundException;
import org.hyperic.hq.appdef.shared.PlatformValue;
import org.hyperic.hq.auth.shared.SessionNotFoundException;
import org.hyperic.hq.auth.shared.SessionTimeoutException;
import org.hyperic.hq.authz.shared.PermissionException;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.common.NotFoundException;
import org.hyperic.hq.ui.action.resource.common.monitor.visibility.InventoryHelper;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.util.pager.PageControl;

/**
 * A class that provides an implementation of some auto-group monitoring
 * functions for auto-groups of platforms. We call it "root" because there is no
 * parent resource(s) in this case.
 * 
 */
public class RootInventoryHelper
    extends InventoryHelper {

    private final Log log = LogFactory.getLog(RootInventoryHelper.class.getName());
    
    private AppdefBoss appdefBoss;

    public RootInventoryHelper(AppdefBoss appdefBoss) {
        super(null);
        this.appdefBoss = appdefBoss;
    }

    /**
     * Get the set of server types representing a platform's servers.
     * 
     * @param request the http request
     * @param ctx the servlet context
     * @param resource the platform
     */
    public List getChildResourceTypes(HttpServletRequest request, ServletContext ctx, AppdefResourceValue resource/* ignored */)
        throws PermissionException, AppdefEntityNotFoundException, RemoteException, SessionNotFoundException,
        SessionTimeoutException, ServletException {
        int sessionId = RequestUtils.getSessionId(request).intValue();
      
        log.trace("finding all platform types");
        return appdefBoss.findAllPlatformTypes(sessionId, PageControl.PAGE_ALL);
    }

    /**
     * Get a platform type from the Bizapp.
     * 
     * @param request the http request
     * @param ctx the servlet context
     * @param id the id of the server type
     */
    public AppdefResourceType getChildResourceType(HttpServletRequest request, ServletContext ctx, AppdefEntityTypeID id)
        throws PermissionException, AppdefEntityNotFoundException, RemoteException, SessionNotFoundException,
        SessionTimeoutException, ServletException {
        int sessionId = RequestUtils.getSessionId(request).intValue();
       

        log.trace("finding platform type [" + id + "]");
        return appdefBoss.findPlatformTypeById(sessionId, id.getId());
    }

    /**
     * Get from the Bizapp the numbers of children of the given resource.
     * Returns a <code>Map</code> of counts keyed by child resource type.
     * 
     * @param request the http request
     * @param resource the appdef resource whose children we are counting
     */
    public Map getChildCounts(HttpServletRequest request, ServletContext ctx, AppdefResourceValue resource/* ignored */)
        throws PermissionException, AppdefEntityNotFoundException, RemoteException, SessionNotFoundException,
        SessionTimeoutException, ServletException {
        int sessionId = RequestUtils.getSessionId(request).intValue();
      
        try {
            Collection<PlatformValue> arvs = appdefBoss.findAllPlatforms(sessionId, PageControl.PAGE_ALL);
            return AppdefResourceValue.getPlatformTypeCountMap(arvs);
        } catch (NotFoundException e) {
            throw new PlatformNotFoundException("couldn't find all platforms");
        }
    }
}
