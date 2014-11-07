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

package org.hyperic.hq.ui.server.session;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.hyperic.hq.authz.server.session.AuthzSubject;
import org.hyperic.hq.authz.server.session.Role;
import org.hyperic.hq.dao.HibernateDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardConfigDAO
    extends HibernateDAO<DashboardConfig> {

    @Autowired
    DashboardConfigDAO(SessionFactory f) {
        super(DashboardConfig.class, f);
    }

    UserDashboardConfig findDashboard(AuthzSubject user) {
        String sql = "from UserDashboardConfig where user = :user";

        return (UserDashboardConfig) getSession().createQuery(sql).setParameter("user", user)
            .setCacheable(true).setCacheRegion("UserDashboardConfig.findDashboard").uniqueResult();
    }

    RoleDashboardConfig findDashboard(Role role) {
        String sql = "from RoleDashboardConfig where role = :role";

        return (RoleDashboardConfig) getSession().createQuery(sql).setParameter("role", role)
            .setCacheable(true).setCacheRegion("RoleDashboardConfig.findDashboard").uniqueResult();
    }

    @SuppressWarnings("unchecked")
    Collection<RoleDashboardConfig> findAllRoleDashboards() {
        return getSession().createQuery("from RoleDashboardConfig order by name")
            .setCacheable(true).setCacheRegion("RoleDashboardConfig.findAllRoleDashboards").list();
    }

    @SuppressWarnings("unchecked")
    Collection<RoleDashboardConfig> findRolesFor(AuthzSubject me) {
        String sql = "select rc from RoleDashboardConfig rc " + "join rc.role r "
                     + "join r.subjects s " + "where s = :subject";

        return getSession().createQuery(sql).setParameter("subject", me).list();
    }

    void handleSubjectRemoval(AuthzSubject s) {
        getSession().createQuery("delete UserDashboardConfig where user = :user").setParameter(
            "user", s).executeUpdate();
    }

    void handleRoleRemoval(Role r) {
        getSession().createQuery("delete RoleDashboardConfig where role= :role").setParameter(
            "role", r).executeUpdate();
    }
}
