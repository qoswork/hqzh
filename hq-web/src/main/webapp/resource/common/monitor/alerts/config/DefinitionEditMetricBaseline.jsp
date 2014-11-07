<%@ page language="java" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/hq.tld" prefix="hq" %>
<%@ taglib tagdir="/WEB-INF/tags/jsUtils" prefix="jsu" %>
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


<tiles:importAttribute name="BaselineValue" ignore="true"/>
<jsu:importScript path="/js/schedule.js" />
<jsu:script>
	var imagePath="/images/";
	var jsPath="<html:rewrite page="/js/"/>";
	var cssPath="<html:rewrite page="/css/"/>";
	var isMonitorSchedule = false;

	function recalc() {
		document.EditMetricBaselineForm.recalc.value='y';
		document.EditMetricBaselineForm.submit();
	}
</jsu:script>
<link rel=stylesheet href="<html:rewrite page="/css/win.css"/>" type="text/css">
<%-- end vit: delete this block --%>

<!-- Content Block Title -->
<tiles:insert definition=".header.tab">
  <tiles:put name="tabKey" value="monitoring.baseline.BlockTitle.MetricBaseline"/>
  <tiles:put name="tabName" beanName="BaselineValue" beanProperty="metricName"/>
</tiles:insert>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="20%" class="BlockLabel"><b><fmt:message key="monitoring.baseline.BlockLabel.BaselineValue"/></b></td>
      <td width="30%" class="BlockContent">
            <html:text size="15" maxlength="15" property="mean"/>
      <td width="20%" class="BlockLabel"><b><fmt:message key="monitoring.baseline.BlockLabel.HostMetric"/></b></td>
      <td width="30%" class="BlockContent"><bean:write name="BaselineValue" property="metricName" scope="request"/></td>
    </tr>
    <tr>
    </tr>
    <tr>
      <td class="BlockLabel"><b><fmt:message key="monitoring.baseline.BlockLabel.Calc"/></b></td>
      <td class="BlockContent" colspan="3"><fmt:message key="monitoring.baseline.BlockContent.DateRange"/> <c:if test="${ 0 < BaselineValue.fromDate}"><hq:dateFormatter  value="${BaselineValue.fromDate}"/></c:if> - <c:if test="${0 < BaselineValue.toDate}"> <hq:dateFormatter value="${BaselineValue.toDate}"/></c:if> <fmt:message key="leftparan" />&nbsp;<bean:write name="BaselineValue" property="numOfPts"/>&nbsp;<fmt:message key="monitoring.baseline.BlockContent.Collection"/>&nbsp;<fmt:message key="rightparan"/></td>
    </tr>
    <tr>
      <td colspan="4" class="BlockBottomLine"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
    </tr>
<!-- second chunk -->
    <tr>
      <td class="BlockLabel" valign="top"><b><fmt:message key="monitoring.baseline.BlockLabel.Recalc"/></b></td>
      <td class="BlockContent" colspan="3"> 
	  <fmt:message key="monitoring.baseline.BlockContent.WithinRange"/><br>
	  
		<table width="100%" border="0" cellspacing="0" cellpadding="2">
          <tr> 
            <td><html:img page="/images/spacer.gif" width="20" height="20" border="0"/></td>
            <td align="right">
			<fmt:message key="monitoring.baseline.BlockContent.From"/></td>
            <td width="100%">
		<html:select property="startMonth" styleId="startMonth">
	  	<option value="1">01 (Jan)</option>
		<option value="2">02 (Feb)</option>
		<option value="3">03 (Mar)</option>
		<option value="4">04 (Apr)</option>
		<option value="5">05 (May)</option>
		<option value="6">06 (Jun)</option>
		<option value="7">07 (Jul)</option>
		<option value="8">08 (Aug)</option>
		<option value="9">09 (Sep)</option>
		<option value="10">10 (Oct)</option>
		<option value="11">11 (Nov)</option>
		<option value="12">12 (Dec)</option>
		</html:select>&nbsp;/&nbsp;
		<html:select property="startDay" styleId="startDay"> 
	  	<option value="1">01</option>
		<option value="2">02</option>
		<option value="3">03</option>
		<option value="4">04</option>
		<option value="5">05</option>
		<option value="6">06</option>
		<option value="7">07</option>
		<option value="8">08</option>
		<option value="9">09</option>
		<option value="10">10</option>
		<option value="11">11</option>
		<option value="12">12</option>
		<option value="13">13</option>
		<option value="14">14</option>
		<option value="15">15</option>
		<option value="16">16</option>
		<option value="17">17</option>
		<option value="18">18</option>
		<option value="19">19</option>
		<option value="20">20</option>
		<option value="21">21</option>
		<option value="22">22</option>
		<option value="23">23</option>
		<option value="24">24</option>
		<option value="25">25</option>
		<option value="26">26</option>
		<option value="27">27</option>
		<option value="28">28</option>
		<option value="29">29</option>
		<option value="30">30</option>
		<option value="31">31</option>
		</html:select>&nbsp;/&nbsp;
		<html:select property="startYear" styleId="startYear" styleClass="logo">
			<jsu:script>
		        for (i=0; i<SEL_NUMYEARS; i++) {
                	var startYear =  document.getElementById('startYear');
                	startYear.options[i] = new Option(yearArr[i], yearArr[i]);
				}
			</jsu:script>
		</html:select>&nbsp;<html:link href="#" onclick="cal('startMonth','startDay','startYear');"><html:img page="/images/schedule_iconCal.gif" width="19" height="17" alt="" border="0"/></html:link>
		&nbsp;@&nbsp;
		<input name="startHour" styleId="startHour" size="2" maxlength="2" value="08">&nbsp;:&nbsp;<input name="startMin" styleId="styleMin" size="2" maxlength="2" value="00">&nbsp;
		<html:select property="startAmPm"> 
	  	<option value="am">AM</option>
		<option value="pm">PM</option>
		</html:select>&nbsp;
		<html:select property="startTimeZone"> 
	  	<option value="EST">EST</option>
	  	<option value="CST">CST</option>
	  	<option value="MST">MST</option>
	  	<option value="PST">PST</option>
		<option value="GMT">GMT</option>
		</html:select>&nbsp;</td>
          </tr>
          <tr> 
            <td>&nbsp;</td>
            <td align="right">
			<fmt:message key="monitoring.baseline.BlockContent.To"/>&nbsp;</td>
            <td width="100%">
		<html:select property="endMonth" styleId="endMonth"> 
	  	<option value="1">01 (Jan)</option>
		<option value="2">02 (Feb)</option>
		<option value="3">03 (Mar)</option>
		<option value="4">04 (Apr)</option>
		<option value="5">05 (May)</option>
		<option value="6">06 (Jun)</option>
		<option value="7">07 (Jul)</option>
		<option value="8">08 (Aug)</option>
		<option value="9">09 (Sep)</option>
		<option value="10">10 (Oct)</option>
		<option value="11">11 (Nov)</option>
		<option value="12">12 (Dec)</option>
		</html:select>&nbsp;/&nbsp;
		<html:select property="endDay" styleId="endDay"> 
	  	<option value="1">01</option>
		<option value="2">02</option>
		<option value="3">03</option>
		<option value="4">04</option>
		<option value="5">05</option>
		<option value="6">06</option>
		<option value="7">07</option>
		<option value="8">08</option>
		<option value="9">09</option>
		<option value="10">10</option>
		<option value="11">11</option>
		<option value="12">12</option>
		<option value="13">13</option>
		<option value="14">14</option>
		<option value="15">15</option>
		<option value="16">16</option>
		<option value="17">17</option>
		<option value="18">18</option>
		<option value="19">19</option>
		<option value="20">20</option>
		<option value="21">21</option>
		<option value="22">22</option>
		<option value="23">23</option>
		<option value="24">24</option>
		<option value="25">25</option>
		<option value="26">26</option>
		<option value="27">27</option>
		<option value="28">28</option>
		<option value="29">29</option>
		<option value="30">30</option>
		<option value="31">31</option>
		</html:select>&nbsp;/&nbsp;
		<html:select property="endYear" styleId="endYear"> 
			<jsu:script>
		        for (i=0; i<SEL_NUMYEARS; i++) {
                	var endYear =  document.getElementById('endYear');
                	endYear.options[i] = new Option(yearArr[i], yearArr[i]);
				}
			</jsu:script>
		</html:select>&nbsp;<html:link href="#" onclick="cal('endMonth','endDay','endYear');"><html:img page="/images/schedule_iconCal.gif" width="19" height="17" alt="" border="0"/></html:link>
		&nbsp;@&nbsp;
		<input name="endHour" styleId="endHour" size="2" maxlength="2" value="08">&nbsp;:&nbsp;<input name="endMin" styleId="endMin" size="2" maxlength="2" value="00">&nbsp;
		<html:select property="endAmPm"> 
	  	<option value="am">AM</option>
		<option value="pm">PM</option>
		</html:select>&nbsp;
		<html:select property="endTimeZone"> 
	  	<option value="EST">EST</option>
	  	<option value="CST">CST</option>
	  	<option value="MST">MST</option>
	  	<option value="PST">PST</option>
		<option value="GMT">GMT</option>
		</html:select>&nbsp;</td>
          </tr>
        </table>
	  
	  </td>
    </tr>
  <tr>
    <td colspan="4" class="BlockContent"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
  </tr>
    <tr>
    </tr>
    <tr>
      <td class="BlockLabel">&nbsp;</td>
      <td class="BlockContent" colspan="3"><html:link href="javascript:recalc();"><fmt:message key="monitoring.baseline.BlockContent.Recalc"/></html:link></td>
    </tr>
  <tr>
    <td colspan="4" class="BlockBottomLine"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
  </tr>
</table>

<tiles:insert page="/common/components/FormButtons.jsp"/>	
<html:hidden property="m"/>
<html:hidden property="ad"/>
<html:hidden property="rid"/>
<html:hidden property="type"/>
<html:hidden property="numOfPts"/>
<html:hidden property="oldMode"/>
<html:hidden property="metricName"/>
<html:hidden property="recalc"/>
