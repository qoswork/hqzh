<%--
  NOTE: This copyright does *not* cover user programs that use HQ
  program services by normal system calls through the application
  program interfaces provided as part of the Hyperic Plug-in Development
  Kit or the Hyperic Client Development Kit - this is merely considered
  normal use of the program, and does *not* fall under the heading of
  "derived work".
  
  Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
  This file is part of HQ.
  
  HQ is free software; you can redistribute it and/or modify
  it under the terms version 2 of the GNU General Public License as
  published by the Free Software Foundation. This program is distributed
  in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE. See the GNU General Public License for more
  details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
  USA.
 --%>

<%--
Generic JDBC-SQL Client.  Adapted from java-tools/jsputils/directsql.jsp

$Header: /var/cvsroot/hyperic_hq/web/admin/sql.jsp,v 1.3 2005/04/21 20:38:26 dougm Exp $
--%>

<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="javax.naming.Context" %>
<%@ page import="javax.naming.InitialContext" %>

<%@ page import="javax.servlet.ServletRequest" %>
<%@ page import="org.hyperic.util.StringUtil" %>
<%@ page import="org.hyperic.util.jdbc.DBUtil" %>
<%@ page import="org.hyperic.hq.context.Bootstrap" %>

<%@ page import="org.hyperic.hq.bizapp.shared.AuthzBoss" %>
<%@ page import="org.hyperic.hq.common.shared.HQConstants" %>
<%@ page import="org.hyperic.hq.ui.util.SessionUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>

<%!public static final SimpleDateFormat DBDATEFORMAT = new SimpleDateFormat("dd-MMM-yyyy");
public static final SimpleDateFormat DBDATETIMEFORMAT = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

public static final String NULL = "null";

private String stripSQLComments ( String sql ) {
    if ( sql == null ) return null;
    StringBuffer rstr = new StringBuffer("");
    StringTokenizer st = new StringTokenizer(sql, "\n");
    String line = null;
    while ( st.hasMoreTokens() ) {
        line = st.nextToken().trim();
        rstr.append(line).append(" ");
    }
    return rstr.toString();
}

private static final String[] TIME_COLUMN_CHECKS = { "time", "date", "timestamp" };
private boolean mightBeTimeColumn ( String colName ) {
    colName = colName.toLowerCase();
    for ( int i=0; i<TIME_COLUMN_CHECKS.length; i++ ) {
        if ( colName.indexOf(TIME_COLUMN_CHECKS[i]) != -1 ) {
            return true;
        }
    }
    return false;
}

private boolean isLongField ( String val ) {
    try {
        long longval = Long.parseLong(val);
        return ( longval > 900000000000L );
    } catch ( Exception e ) {
        return false;
    }
}

private StringBuffer processSQL ( Connection conn, String sql, int index, int numStatements, boolean continueonerr ) throws SQLException {

    StringBuffer rstr = new StringBuffer("");
    PreparedStatement statement = null;
    ResultSet rs = null;
    int columnCount = -1;
    int i;
    String aValue = null;
    long markTime = 0;
    int numRowsAffected = 0;
    boolean couldBeTimes[] = null;

    try {
        if ( sql == null ) return rstr;

        if ( sql.length() > 0 ) {
            String LCsql = sql.trim().toLowerCase();
            if ( LCsql.startsWith("#") || LCsql.startsWith("--") ) {
                rstr.append("<font color=\"#555555\">Command (statement ")
                    .append(index+1).append(" of ").append(numStatements)
                    .append(") ").append("Was commented-out:<br>").append(sql)
                    .append("<br></font>");
                return rstr;
            }

            try {
                statement = conn.prepareStatement(sql);
            } catch (SQLException e) {
                if ( continueonerr ) {
                    rstr.append("<font color=\"#ff0000\">Command (statement ").append(index+1)
                        .append(" of ").append(numStatements).append(") ")
                        .append("Had an error:<br>").append(e)
                        .append(" (error code=").append(e.getErrorCode()).append(")<br>")
                        .append(sql).append("<br></font>");
                    return rstr;
                } else { 
                    throw e;
                }
            }
            
            if ( LCsql.startsWith("select") || 
                 LCsql.startsWith("values") ) {
                try {
                    markTime = System.currentTimeMillis();
                    rs = statement.executeQuery();
                } catch (SQLException e) {
                    if ( continueonerr ) {
                        rstr.append("<font color=\"#ff0000\">Command (statement ").append(index+1)
                            .append(" of ").append(numStatements).append(") ")
                            .append("Had an error:<br>").append(e)
                            .append(" (error code=").append(e.getErrorCode()).append(")<br>")
                            .append(sql).append("<br></font>");
                        return rstr;
                    } else { 
                        throw e;
                    }
                }

                ResultSetMetaData rsMD = rs.getMetaData();
                columnCount = rsMD.getColumnCount();
                couldBeTimes = new boolean[columnCount];
                
                // Generate title row
                String aHeaderRow = "<tr>";
                String aColumnName = null;
                for ( i=1; i<=columnCount; i++ ) {
                    aColumnName = rsMD.getColumnName(i);
                    aHeaderRow += "<th><font face=\"Verdana,Arial,Helvetica\" size=\"-2\">"
                        + aColumnName
                        + "</font></th>"
                        ;
                    couldBeTimes[i-1] = mightBeTimeColumn(aColumnName);
                }
                aHeaderRow += "</tr>";
                
                rstr.append("Results for statement ").append(index+1).append(" of ")
                    .append(numStatements).append(":<br>").append(sql)
                    .append(" <table border=1>").append(aHeaderRow);
                
                // Output data rows
                boolean hasResults = false;
                while ( rs.next() ) {
                    hasResults = true;
                    rstr.append("<tr>");
                    for ( i=1; i<=columnCount; i++ ) {
                        aValue = rs.getString(i);
                        if ( aValue == null || rs.wasNull() ) {
                            aValue = NULL;
                        } else if ( couldBeTimes[i-1] && isLongField(aValue) ) {
                            aValue = DBDATETIMEFORMAT.format(new Date(Long.parseLong(aValue)));
                        }
                        rstr.append("<td valign=\"top\"><font face=\"Verdana,Arial,Helvetica\" size=\"-2\" style=\"font-weight: normal;\">")
                 	 		.append(StringEscapeUtils.escapeHtml(aValue)).append("</font></td>");
                    }
                    rstr.append("</tr>");
                }
                if ( !hasResults ) {
                    rstr.append("<tr>").append("<td colspan=").append(columnCount)
                        .append("><b><font face=\"Verdana,Arial,Helvetica\">Query returned empty set.</font></b></td></tr>");
                }

                rstr.append(aHeaderRow).append("<tr><td colspan=").append(columnCount)
                    .append("><b><font size=\"-2\" face=\"Verdana,Arial,Helvetica\">Query Time=")
                    .append(System.currentTimeMillis() - markTime).append("</font></b></td></tr></table>");
                
            } else if ( LCsql.startsWith("update") || 
                        LCsql.startsWith("insert") || 
                        LCsql.startsWith("delete") ||
                        LCsql.startsWith("create") ||
                        LCsql.startsWith("drop")   ||
                        LCsql.startsWith("alter")   ||
                        LCsql.startsWith("grant")   ||
                        LCsql.startsWith("set") ) {
                try { 
                    markTime = System.currentTimeMillis();
                    numRowsAffected = statement.executeUpdate();

                } catch (SQLException e) {
                    if ( continueonerr ) {
                        rstr.append("<font color=\"#ff0000\">Command (statement ")
                            .append(index+1).append(" of ").append(numStatements)
                            .append(")  Had an error:<br>").append(e)
                            .append(" (error code=").append(e.getErrorCode()).append(")<br>")
                            .append(sql).append("<br></font>");
                        return rstr;
                    } else { 
                        throw e;
                    }
                }
                rstr.append("Command (statement ").append(index+1).append(" of ")
                    .append(numStatements).append(")  Executed Successufully:<br>")
                    .append(sql).append("<br>Number of Rows Affected=")
                    .append(numRowsAffected).append("<br>Exec Time=")
                    .append(System.currentTimeMillis() - markTime)
                    .append("<br>");
                
            } else {
                rstr.append("Invalid SQL Command: ").append(sql)
                    .append("<br>The first word must be SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, GRANT or SET.");
            }
        }
    } finally {
        DBUtil.closeStatement("directsql.jsp", statement);
        DBUtil.closeResultSet("directsql.jsp", rs);
    }

    return rstr;
}

private boolean getParamIsChecked ( String name, ServletRequest r, StringBuffer b ) {

    String s = r.getParameter(name); 
    if (s != null && s.equals("CHECKED")) {
        b.append("CHECKED");
        return true;
    } else {
        b.delete(0, 1024);
        b.append("");
        return false;
    }
}

private static String changeNull ( String val, String nullval ) {
    return ( val == null ) ? nullval : val;
}%>
<%
    String anErr = null;
Connection conn = null;
String sql = request.getParameter("sql");

StringBuffer results = new StringBuffer("");
List sqlList = new ArrayList();
boolean continueonerr = true;
String continueonerrString = "CHECKED";
StringBuffer continueonerrBuf = null;
boolean isAdmin = true;

try {
    // Before doing anything, check for admin permissions
    AuthzBoss authzBoss = (AuthzBoss)Bootstrap.getBean(AuthzBoss.class);
    if (!authzBoss.hasAdminPermission(SessionUtils.getWebUser(session).getSessionId().intValue())) {
        isAdmin = false;
        throw new IllegalAccessException("You do not have admin permissions");
    }

    if ( request.getParameter("ok") != null ) {
        continueonerrBuf = new StringBuffer("");
        continueonerr = getParamIsChecked("continueonerr", request, continueonerrBuf);
        continueonerrString = continueonerrBuf.toString();

        String fullsql = sql;
        StringTokenizer st = new StringTokenizer(fullsql, ";");
        while ( st.hasMoreTokens() ) {
    sql = stripSQLComments(st.nextToken()).trim();
    if ( sql.endsWith(";") ) sql = sql.substring(0,sql.length()-1);
    if ( sql.length() > 0 ) {
        sqlList.add(sql);
    }
        }

        conn = ((DBUtil)Bootstrap.getBean(DBUtil.class)).getConnection();

        int numStatements = sqlList.size();
        for ( int i=0; i<numStatements; i++ ) {
    sql = sqlList.get(i).toString();
    results.append(processSQL(conn, sql, i, numStatements, continueonerr) + "<hr>");
        }
    }
        
} catch ( Exception e ) {
    anErr = "Error: " + e.toString() + "<br>"
        + "SQL was:" + sql + "<br>"
        + "StackTrace: " + StringUtil.getStackTrace(e);

} finally {
    DBUtil.closeConnection("sql.jsp", conn);
}
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Direct SQL Access</title>
</head>
<body>


<font face="Verdana,Arial,Helvetica" size="-2">
<b><%=results.toString()%></b>
<br>

<% if ( anErr != null && isAdmin ) {
    %>
    <hr>
    <h5><%=anErr%></h5>
    <%
}
%>
</font>

<% if ( isAdmin ) { %>

<i>You can enter multiple commands, separated by semi-colons.</i>
<form action="<%= response.encodeUrl(request.getRequestURI()) %>" method="post">
<table>

<tr>
<td align="right" valign="top"><font face="Verdana,Arial,Helvetica">SQL:</font></td>
<td><font face="Verdana,Arial,Helvetica" size="-2"> <textarea rows=20 cols=80 name="sql"><%=changeNull(request.getParameter("sql"),"")%></textarea><br>
</tr>

<tr>
<td colspan=2><font face="Verdana,Arial,Helvetica"><input type="submit" name="ok" value="Execute SQL"></font></td>
</tr>

<tr><td colspan="2"><hr></td></tr>

<tr>
<td align="left" colspan="2"><font face="Verdana,Arial,Helvetica"><input type="checkbox" name="continueonerr" size="40" value="CHECKED" <%=continueonerrString%>> Continue if statements fail? </font></td>
</tr>

</table>
</form>

<% } else { %>
<h1>You do not have admin permissions on the HQ server</h1> 
<% } %>

</body>
</html>
