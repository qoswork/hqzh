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

package org.hyperic.tools.ant.dbupgrade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.hyperic.util.jdbc.DBUtil;

public class SST_AlterColumn extends SchemaSpecTask {
    private String      _table;
    private String      _column;
    private String      _columnType;
    private String      _precision;
    private String      _nullable;
    private String      _defval;
    private String      _targetDB;
    private boolean     _quoteDefault = true; // Defaults to true
    private Initializer _initializer;
    private ForeignKey  _foreignKey;

    public SST_AlterColumn () {}

    public void setTargetDB (String t) {
        _targetDB = t;
    }

    public void setTable (String t) {
        _table = t;
    }
    
    public void setColumn (String c) {
        _column = c;
    }
    
    public void setColumnType (String ct) {
        _columnType = ct;
    }
    
    public void setPrecision (String p) {
        _precision = p;
    }
    
    public void setNullable (String n) {
        _nullable = n;
    }
    
    public void setDefault (String d) {
        _defval = d;
    }
    
    public void setQuoteDefault (String d) {
        _quoteDefault = d.equalsIgnoreCase("t") || d.equalsIgnoreCase("true") ||
                        d.equalsIgnoreCase("y") || d.equalsIgnoreCase("yes");
    }
    
    public Initializer createInitializer () {
        if ( _initializer != null ) {
            throw new IllegalStateException("Multiple initializers "
                                            + "not permitted");
        }
        _initializer = new Initializer();
        return _initializer;
    }
    
    public ForeignKey createForeignKey () {
        if ( _foreignKey != null ) {
            throw new IllegalStateException("Multiple foreignKeys "
                                            + "not permitted");
        }
        _foreignKey = new ForeignKey();
        return _foreignKey;
    }

    public void execute () throws BuildException {
        validateAttributes();

        Connection c = getConnection();
        try {
            if (!targetDbIsValid(_targetDB)) {
                return;
            }
            if (DBUtil.isOracle(c))
                alter_oracle(c);
            else if (DBUtil.isMySQL(c))
                alter_mysql(c);
            else if (DBUtil.isPostgreSQL(c))
                alter_pgsql(c);
            else {
                int dbtype = DBUtil.getDBType(c);
                throw new BuildException("Unsupported database: " + dbtype);
            }
        } catch (SQLException e) {
            throw new BuildException("Error determining dbtype: " + e, e);
        }
    }

    private void alter_mysql (Connection c) throws BuildException {
        alterMySQLTable(c, false);
    }

    private void alter_oracle (Connection c) throws BuildException {
        alterOracleTable(c, true);
    }

    private void alterOracleTable (Connection c, boolean withParen)
        throws BuildException
    {
        String columnTypeName = null;
        String alterSql =
            "ALTER TABLE " + _table + " MODIFY " +
            ( (withParen) ? "(" : "" ) + _column;

        if (_columnType != null) {
            columnTypeName =  getDBSpecificTypeName(_columnType);
            alterSql += " " + columnTypeName;
        }

        if (_defval != null) {
            alterSql += " DEFAULT '" + _defval + "'";
        }
        
        if ( _precision != null ) { 
            alterSql += " (" + _precision + ")";
        }

        if (_nullable != null) {
            alterSql += " " + getNullable(c);
        }
        alterSql += (withParen) ? ")" : "";

        List sql = new ArrayList();
        sql.add(alterSql);
        doAlter(c, sql);
    }

    private String getNullable(Connection conn) throws BuildException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (!DBUtil.isOracle(conn)) {
                return _nullable;
            }
            String sql = "select nullable from ALL_TAB_COLS" +
                         " WHERE table_name = ?" +
                         " AND column_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, _table.toUpperCase());
            pstmt.setString(2, _column.toUpperCase());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String nullable = rs.getString(1);
                if (nullable.equals("N") &&
                    _nullable.equalsIgnoreCase("NOT NULL")) {
                    return "";
                } else if (nullable.equals("Y") &&
                    _nullable.equalsIgnoreCase("NULL")) {
                    return "";
                }
                return _nullable;
            }
        } catch (SQLException e) {
            throw new BuildException(
                "Error while determining nullable value for oracle: "+e.getMessage(),
                e);
        }
        return null;
    }

    private void alterMySQLTable (Connection c, boolean withParen)
        throws BuildException
    {
        String columnTypeName = null;
        String alterSql =
            "ALTER TABLE " + _table + " MODIFY " +
            ( (withParen) ? "(" : "" ) + _column;

        if (_columnType == null) {
            columnTypeName = getMySQLColumnType(c);
        } else {
            columnTypeName =  getDBSpecificTypeName(_columnType);
            if (_precision != null) {
                columnTypeName = columnTypeName + "(" + _precision + ")";
            }
        }
        alterSql += " " + columnTypeName;

        if (_defval != null) {
            alterSql += " DEFAULT '" + _defval + "'";
        }

        if (_nullable != null) {
            alterSql += " " + _nullable;
        }
        alterSql += (withParen) ? ")" : "";

        List sql = new ArrayList();
        sql.add(alterSql);
        doAlter(c, sql);
    }

    private String getMySQLColumnType(Connection conn) throws BuildException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select database()");
            String db = null;
            if (rs.next()) {
                db = rs.getString(1);
            }
            String sql = "select data_type, column_type, character_maximum_length," +
                         " column_type, numeric_precision, numeric_scale" +
                         " FROM information_schema.columns" +
                         " WHERE lower(table_name) = '" + _table.toLowerCase() + "'" +
                         " AND lower(column_name) = '" + _column.toLowerCase() + "'" +
                         " AND lower(table_schema) = '" + db.toLowerCase() + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String type = rs.getString("data_type");
                if (type.equalsIgnoreCase("int")) {
                    return "INTEGER";
                } else if (type.equalsIgnoreCase("decimal")) {
                    int scale = rs.getInt("numeric_scale");
                    int precision = rs.getInt("numeric_precision");
                    return "NUMERIC("+precision+ ((scale>0) ? ","+scale : "") +")";
                } else if (type.equalsIgnoreCase("varchar")) {
                    String len = rs.getString("character_maximum_length");
                    return "VARCHAR(" + len + ")";
                } else if (type.equalsIgnoreCase("char")) {
                    String len = rs.getString("character_maximum_length");
                    return "CHAR(" + len + ")";
                }
                return rs.getString("column_type");
            }
            throw new SQLException();
        } catch (SQLException e) {
            throw new BuildException("Error retrieving mysql columntype from " +
                "table, " + _table + " column, " + _column);
        } finally {
            DBUtil.closeJDBCObjects(getClass().getName(), null, stmt, rs);
        }
    }

    private String getPgSQLColumnType(Connection conn) throws BuildException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "select udt_name, data_type, numeric_scale," +
                " numeric_precision, character_maximum_length" +
                " FROM information_schema.columns" +
                " WHERE lower(table_name) = '" + _table.toLowerCase() + "'" +
                " AND lower(column_name) = '" + _column.toLowerCase() + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String type = rs.getString("data_type");
                if (type.equalsIgnoreCase("numeric")) {
                    String scale = rs.getString("numeric_scale");
                    String precision = rs.getString("numeric_precision");
                    return type + "(" + scale + "," + precision + ")";
                } else if (type.equalsIgnoreCase("character varying")) {
                    type = rs.getString("udt_name");
                    String len = rs.getString("character_maximum_length");
                    return type + "(" + len + ")";
                } else if (type.equalsIgnoreCase("character")) {
                    String len = rs.getString("character_maximum_length");
                    return "char(" + len + ")";
                }
                return type;
            }
            throw new SQLException();
        } catch (SQLException e) {
            throw new BuildException("Error retrieving pg columntype from " +
                "table, " + _table + " column, " + _column);
        } finally {
            DBUtil.closeJDBCObjects(getClass().getName(), null, stmt, rs);
        }
    }

    private void alter_pgsql (Connection c) throws BuildException {
        String columnTypeName = null;
        List sqlList = new ArrayList();

        if (_columnType != null) {
            columnTypeName =  getDBSpecificTypeName(_columnType);
            String currColType = getPgSQLColumnType(c);
            if (!currColType.replaceAll("\\s+", "").equalsIgnoreCase(columnTypeName))
            {
                if ( _precision != null ) { 
                    columnTypeName += " (" + _precision + ")";
                }
                sqlList.add("ALTER TABLE " + _table + " ALTER COLUMN " + _column +
                            " TYPE " + columnTypeName);
            }
        }

        if (_defval != null) {
            if (_quoteDefault) {
                sqlList.add("ALTER TABLE " + _table + " ALTER " + _column +
                            " SET DEFAULT '" + _defval + "'");
            } else {
                sqlList.add("ALTER TABLE " + _table + " ALTER " + _column +
                            " SET DEFAULT " + _defval);
            }
        }
        
        if (_nullable != null) {
            if (_nullable.equalsIgnoreCase("NOT NULL")) {
                sqlList.add("ALTER TABLE " + _table 
                            + " ALTER " + _column + " SET NOT NULL");
            } else if (_nullable.equalsIgnoreCase("NULL")) {
                sqlList.add("ALTER TABLE " + _table
                            + " ALTER " + _column + " DROP NOT NULL");
            } else {
                throw new BuildException("Invalid nullable attribute: " +
                    _nullable);
            }
        }

        doAlter(c, sqlList);
    }

    private void doAlter (Connection c, List sqlList) {
        PreparedStatement ps = null;
        String sql;
        try {
            // Check to see if the column exists.  If it doesn't exist
            // then can't alter it
            boolean foundColumn = DBUtil.checkColumnExists(_ctx, c, 
                                                           _table, _column);
            if ( !foundColumn ) {
                log(">>>>> Not altering column: " + _column
                    + " because it does not exist in table " + _table);
                return;
            }

            // Alter the column.
            for (int i=0; i<sqlList.size(); i++) {
                sql = (String) sqlList.get(i);
                log(">>>>> Altering with statement: " + sql);
                ps = c.prepareStatement(sql);
                ps.executeUpdate();
            }

            // Initialize the column
            if ( _initializer != null ) {
                _initializer.init(c);
                _initializer.execute();
            }
            if ( _foreignKey != null ) {
                _foreignKey.init(c);
                _foreignKey.execute();
            }

        } catch ( Exception e ) {
            throw new BuildException("Error updating " + _table + "." + _column 
                                     + ": " + e, e);
        } finally {
            DBUtil.closeStatement(_ctx, ps);
        }
    }

    private void validateAttributes () throws BuildException {
        if ( _table == null )
            throw new BuildException(
                    "SchemaSpec: update: No 'table' attribute specified.");
        if ( _column == null )
            throw new BuildException(
                    "SchemaSpec: update: No 'column' attribute specified.");
        if ( _columnType == null && _nullable == null && _defval == null)
            throw new BuildException(
                    "SchemaSpec: update: No 'columnType', 'default, or " +
                    "'nullable' attribute specified.");
    }

    public class Initializer extends Task {

        private String     _initSql;
        private Connection _conn;

        public Initializer () {}

        public void init (Connection conn) {
            _conn = conn;
        }

        public void addText(String msg) {
            if ( _initSql == null ) _initSql = "";
            _initSql += project.replaceProperties(msg);
        }

        public void execute() throws BuildException {

            if ( _initSql == null ) return;

            PreparedStatement ps = null;
            try {
                ps = _conn.prepareStatement(_initSql);
                log(">>>>> Initializing " + _table + "." + _column 
                    + " with " + _initSql);
                ps.executeUpdate();

            } catch ( Exception e ) {
                throw new BuildException("Error initializing " 
                                         + _table + "." + _column 
                                         + " (sql=" + _initSql + ")");
            } finally {
                DBUtil.closeStatement(_ctx, ps);
            }
        }
    }

    public class ForeignKey extends Task {
        private String     _constraintName;
        private String     _refs;
        private Connection _conn;

        public ForeignKey () {}

        public void init (Connection conn) {
            _conn = conn;
        }

        public void setConstraintName (String constraintName) {
            _constraintName = constraintName;
        }

        public void setReferences (String refs) {
            _refs = refs;
        }

        public void execute () throws BuildException {
            String fkSql
                = "ALTER TABLE " + _table + " "
                + "ADD CONSTRAINT " + _constraintName + " "
                + "FOREIGN KEY (" + _column + ") REFERENCES " + _refs;
            PreparedStatement ps = null;
            try {
                ps = _conn.prepareStatement(fkSql);
                log(">>>>> Adding foreign key constraint " + _constraintName 
                    + " on " + _table + "." + _column + "->" + _refs);
                ps.executeUpdate();

            } catch ( Exception e ) {
                throw new BuildException("Error adding foreign key for "
                                         + _table + "." + _column 
                                         + " (sql=" + fkSql + ")");
            } finally {
                DBUtil.closeStatement(_ctx, ps);
            }
        }
    }
}
