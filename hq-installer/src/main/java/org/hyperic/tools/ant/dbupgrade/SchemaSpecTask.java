/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004-2007], Hyperic, Inc.
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

package org.hyperic.tools.ant.dbupgrade;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.hyperic.tools.db.TypeMap;
import org.hyperic.util.jdbc.DBUtil;
import org.jasypt.encryption.pbe.PBEStringEncryptor;

public abstract class SchemaSpecTask extends Task {
    protected String _ctx = SchemaSpecTask.class.getName();

    protected Connection _conn;
    private DBUpgrader _upgrader;

    private static Map SQL_TYPES = new HashMap();

    static {
        Field[] fields = Types.class.getDeclaredFields();
        int mods;
        for ( int i=0; i<fields.length; i++ ) {
            mods = fields[i].getModifiers();
            if ( Modifier.isPublic(mods) &&
                 Modifier.isStatic(mods) &&
                 Modifier.isFinal(mods) ) 
            {
                try {
                    SQL_TYPES.put(fields[i].getName(), 
                                  new Integer(fields[i].getInt(null)));
                } catch (IllegalAccessException iae) {
                    throw new IllegalStateException("Fatal error: can't "
                                                    + "initialize SQL_TYPES: "
                                                    + iae);
                }
            }
        }
    }

    public SchemaSpecTask () {}

    public void initialize(Connection conn, DBUpgrader upgrader) {
        _conn     = conn;
        _upgrader = upgrader;
    }

    public Connection getConnection () { return _conn; }
    
    public Connection getNewConnection () throws SQLException {
        return _upgrader.getConnection(); 
    }
    
    public final PBEStringEncryptor newEncryptor() { 
        return this._upgrader.newEncryptor() ; 
    }//EOM 

    public int getDBType () { return _upgrader.getDBType(); }
    
    public int getDBUtilType () { return _upgrader.getDBUtilType(); }

    public int translateSqlType ( String typeName ) throws BuildException {
        String mappedType = TypeMap.getMappedType(_upgrader.getTypeMaps(), 
                                                  typeName, "java");
        Integer sqlTypeInteger = (Integer) SQL_TYPES.get(mappedType);
        if ( sqlTypeInteger == null ) {
            throw new BuildException("No type mapping for: " + typeName);
        }
        return sqlTypeInteger.intValue();
    }

    public String getDBSpecificTypeName( String typeName ) 
        throws BuildException 
    {
        String mappedType = TypeMap.getMappedType(_upgrader.getTypeMaps(),
                                                  typeName, 
                                                  _upgrader.getDBType());
        if ( mappedType == null ) {
            throw new BuildException("No type mapping for: " + typeName);
        }
        return mappedType;
    }

    boolean targetDbIsValid(String targetDB)
        throws SQLException
    {
        Connection c = getConnection();
        if (targetDB != null && targetDB.trim().length() != 0)
        {
            targetDB = targetDB.toLowerCase();
            if (targetDB.equals("oracle")) {
                if (!DBUtil.isOracle(c)) {
                    log("target was oracle, but this is not oracle, returning.");
                    return false;
                } else {
                    log("target is oracle.");
                    return true;
                }
            } else if (-1 != targetDB.indexOf("postgres")) {
                if (!DBUtil.isPostgreSQL(c)) {
                    log("target was postgresql, but this is not pgsql, returning.");
                    return false;
                } else {
                    log("target is postgres.");
                    return true;
                }
            } else if (targetDB.equals("mysql")) {
                if (!DBUtil.isMySQL(c)) {
                    log("target was mysql, but this is not mysql, returning.");
                    return false;
                } else {
                    log("target is mysql.");
                    return true;
                }
            }
        }
        log("NOTE:  No DB target was specified, allowing task to proceed.");
        return true;
    }
}
