/**
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 *  "derived work".
 *
 *  Copyright (C) [2004-2010], VMware, Inc.
 *  This file is part of HQ.
 *
 *  HQ is free software; you can redistribute it and/or modify
 *  it under the terms version 2 of the GNU General Public License as
 *  published by the Free Software Foundation. This program is distributed
 *  in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 *
 */

package org.hyperic.hq.appdef.server.session;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.hyperic.hq.appdef.AppService;
import org.hyperic.hq.appdef.AppSvcDependency;
import org.hyperic.hq.dao.HibernateDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AppSvcDependencyDAO
    extends HibernateDAO<AppSvcDependency> {
    @Autowired
    public AppSvcDependencyDAO(SessionFactory f) {
        super(AppSvcDependency.class, f);
    }

    public AppSvcDependency create(AppService appSvc, AppService depSvc) {
        AppSvcDependency a = new AppSvcDependency();
        a.setAppService(appSvc);
        a.setDependentService(depSvc);
        save(a);

        appSvc.getAppSvcDependencies().add(depSvc);
        return a;
    }

    public AppSvcDependency findByDependentAndDependor(Integer appsvcId, Integer depAppSvcId) {
        String sql = "from AppSvcDependency " + "where appService.id=? and dependentService.id=?";
        return (AppSvcDependency) getSession().createQuery(sql).setInteger(0, appsvcId.intValue())
            .setInteger(1, depAppSvcId.intValue()).uniqueResult();
    }

    public Collection findByDependents(AppService entity) {
        String sql = "from AppSvcDependency where dependentService = :appSvc";
        return getSession().createQuery(sql).setEntity("appSvc", entity).list();
    }

    public Collection findByApplication(Integer appId) {
        String sql = "select distinct a from AppSvcDependency a " + " join fetch a.appService asv "
                     + " where asv.application.id=?";
        return getSession().createQuery(sql).setInteger(0, appId.intValue()).list();
    }

    public Collection findByAppAndService(Integer appId, Integer serviceId) {
        String sql = "select distinct a from AppSvcDependency a " + " join fetch a.appService asv "
                     + " where asv.application.id=? and asv.service.id=?";
        return getSession().createQuery(sql).setInteger(0, appId.intValue()).setInteger(1,
            serviceId.intValue()).list();
    }

}
