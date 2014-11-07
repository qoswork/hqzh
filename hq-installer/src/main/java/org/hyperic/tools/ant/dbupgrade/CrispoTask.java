/**
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 *  "derived work".
 *
 *  Copyright (C) [2009-2010], VMware, Inc.
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

package org.hyperic.tools.ant.dbupgrade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.hibernate.dialect.Dialect;
import org.hyperic.util.config.ConfigResponse;
import org.hyperic.util.jdbc.DBUtil;

public class CrispoTask extends SchemaSpecTask {

    public static final Class LOGCTX = CrispoTask.class;

    private static final String CRISPO_TABLE      = "EAM_CRISPO";
    private static final String CRISPO_OPT_TABLE  = "EAM_CRISPO_OPT";
    private static final String CRISPO_ARR_TABLE  = "EAM_CRISPO_ARRAY";
    
    private static final String CRISPO_ID_SEQ     = "EAM_CRISPO_ID_SEQ";
    private static final String CRISPO_OPT_ID_SEQ = "EAM_CRISPO_OPT_ID_SEQ";
    
    private static final String ARRAY_DELIMITER = "|";

    public int createCrispo(Dialect d, ConfigResponse cr)
        throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        int crispoId;

        try {
            String sql = d.getSequenceNextValString(CRISPO_ID_SEQ);
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            rs.next();
            crispoId = rs.getInt(1);

            sql = "insert into " + CRISPO_TABLE + " (id, version_col) VALUES ("
                    + crispoId + ", 1)";
            log(sql);
            stmt.execute(sql);
        } finally {
            DBUtil.closeJDBCObjects(LOGCTX, null, stmt, rs);
        }

        for (Iterator i = cr.getKeys().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String val = cr.getValue(key);
            createCrispoOpt(d, crispoId, key, val);
        }

        return crispoId;
    }

    public void createCrispoOpt(Dialect d, int crispoId, String key, String val)
        throws SQLException
    {
        if (val == null || val.trim().equals(""))
            return;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id;
        String sql;

        try {
            sql = d.getSequenceNextValString(CRISPO_OPT_ID_SEQ);
            stmt = getConnection().prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            id = rs.getInt(1);

            DBUtil.closeJDBCObjects(LOGCTX, null, stmt, rs);

            // See if the value is too long
            String[] elem = null;
            if (val.length() > 4000) {
                elem = val.split("\\" + ARRAY_DELIMITER);
            }
            
            if (elem != null && elem.length > 1) {
                sql = "insert into " + CRISPO_OPT_TABLE
                        + " (id, version_col, propkey, crispo_id) "
                        + "VALUES (" + id + ", 1, '" + key + "', "
                        + crispoId + ")";
                stmt = getConnection().prepareStatement(sql);
            } else {
                if (elem != null)                    // No split, narrow string
                    val = val.substring(0, 3999);

                elem = null;
                sql = "insert into " + CRISPO_OPT_TABLE
                        + " (id, version_col, propkey, val, crispo_id) "
                        + "VALUES (" + id + ", 1, '" + key + "', ?, " + crispoId
                        + ")";
                stmt = getConnection().prepareStatement(sql);
                stmt.setString(1, val);
            }

            log("executing query: " + sql);
            stmt.executeUpdate();
            DBUtil.closeStatement(LOGCTX, stmt);
            
            if (elem != null) {
                stmt = getConnection()
                    .prepareStatement("insert into " + CRISPO_ARR_TABLE
                                      + " (opt_id, val, idx) VALUES (" + id
                                      + ", ?, ?)");
                for (int i = 0; i < elem.length; i++) {
                    stmt.setString(1, elem[i]);
                    stmt.setInt(2, i);
                    stmt.executeUpdate(sql);
                }
            }
        } finally {
            // Make sure everything's closed
            DBUtil.closeJDBCObjects(LOGCTX, null, stmt, rs);
        }
    }
}
