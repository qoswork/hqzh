<%@ page language="java" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/hq.tld" prefix="hq" %>

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


<tiles:importAttribute name="summaries" ignore="true"/>
<tiles:importAttribute name="appdefResourceType" ignore="true"/>
<tiles:importAttribute name="childResourcesHealthKey"/>
<tiles:importAttribute name="childResourcesTypeKey"/>
<tiles:importAttribute name="childResourceType" ignore="true"/>

<hq:constant
classname="org.hyperic.hq.bizapp.shared.uibeans.UIConstants"
symbol="SUMMARY_TYPE_AUTOGROUP" var="AUTOGROUP" />
<hq:constant
classname="org.hyperic.hq.bizapp.shared.uibeans.UIConstants"
symbol="SUMMARY_TYPE_CLUSTER" var="CLUSTER" />
<hq:constant
classname="org.hyperic.hq.bizapp.shared.uibeans.UIConstants"
symbol="SUMMARY_TYPE_SINGLETON" var="SINGLETON" />

<c:set var="useAvailStoplightDimensions" value="false" />
<c:if test="${useAvailStoplightDimensions}">
  <c:set var="availStoplightDimensions" value=" width=\"106\" " />
</c:if>


<table width="100%" cellpadding="0" cellspacing="0" border="0" id="listTable">
  <tr class="ListHeaderDark">
    <td width="30%" colspan="2" class="ListHeaderInactiveSorted"><fmt:message key="${childResourcesHealthKey}"/></td>
    <td width="30%" class="ListHeaderInactiveSorted"><fmt:message key="${childResourcesTypeKey}"/></td>
    <td width="6%" class="ListHeaderCheckboxLeftLine"><fmt:message key="resource.common.monitor.visibility.TotalNumTH"/></td>
    <td width="25%" class="ListHeaderCheckboxLeftLine"><fmt:message key="resource.common.monitor.visibility.AVAILTH"/></td>
    <td width="9%" class="ListHeaderCheckboxLeftLine"><fmt:message key="resource.common.monitor.visibility.USAGETH"/></td>
    <!--
    <td width="7%" class="ListHeaderCheckboxLeftLine"><fmt:message key="resource.common.monitor.visibility.PERFTH"/></td>
      -->
  </tr>

    <c:forEach var="summary" items="${summaries}">
    <c:choose>
      <c:when test="${summary.summaryType == CLUSTER}">
     <c:url var="stoplightUrl" value="/resource/AvailStoplight">
       <c:param name="rid" value="${summary.entityId.ID}" />
       <c:param name="type" value="${summary.entityId.type}" />
       <c:param name="ctype" value="${summary.resourceType.appdefType}:${summary.resourceType.id}" />
     </c:url>
      </c:when>
      <c:otherwise>
     <c:url var="stoplightUrl" value="/resource/AvailStoplight">
       <c:param name="rid" value="${Resource.id}" />
       <c:param name="type" value="${Resource.entityId.type}" />
       <c:param name="ctype" value="${summary.resourceType.appdefType}:${summary.resourceType.id}" />
     </c:url>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <c:when test="${summary.summaryType == AUTOGROUP}">
        <c:url var="url" value="/resource/autogroup/monitor/Visibility.do">
          <c:param name="mode" value="currentHealth" />
          <c:param name="eid" value="${Resource.entityId.type}:${Resource.id}"/>
          <c:choose>
            <c:when test="${not empty appdefResourceType && 
            		appdefResourceType == 4}"> <!-- AppdefEntityConstants.APPDEF_TYPE_APPLICATION-->
              <c:param name="ctype" value="3:${summary.resourceType.id}" />
            </c:when>
            <c:otherwise>
              <c:choose>
              	<c:when test="${not empty childResourceType}">
              		<c:param name="ctype" value="${childResourceType}:${summary.resourceType.id}" />
              	</c:when>
              	<c:otherwise>
              	    <c:param name="ctype" value="${summary.resourceType.id}"/>
              	</c:otherwise>
              </c:choose>	
            </c:otherwise>
          </c:choose>
        </c:url>
      </c:when>
      <c:otherwise>
        <c:url var="url" value="/resource/${summary.entityId.typeName}/monitor/Visibility.do">
          <c:param name="mode" value="currentHealth" />
          <c:param name="rid" value="${summary.entityId.ID}" />
          <c:param name="type" value="${summary.entityId.type}" />
        </c:url>
      </c:otherwise>
    </c:choose>

  <tr class="ListRow">
    <td width="1%" class="ListCellPrimary">
    <c:choose>
      <c:when test="${summary.summaryType == AUTOGROUP}">
      <html:img page="/images/icon_auto-group.gif" height="10" width="11" border="0" alt=""/>
      </c:when>
      <c:when test="${summary.summaryType == CLUSTER}">
      <html:img page="/images/icon_cluster.gif" height="10" width="11" border="0" alt=""/>
      </c:when>
      <c:otherwise>
      <html:img page="/images/spacer.gif" height="10" width="11" border="0" alt=""/>
      </c:otherwise>
    </c:choose>
    </td>
    <td class="ListCellPrimary">
    <c:choose>
      <c:when test="${empty url}">
        <c:out value="${summary.resourceType.name}"/>
      </c:when>
      <c:when test="${summary.summaryType == AUTOGROUP}">
        <a href="${url}">${summary.resourceType.name}</a>
      </c:when>
      <c:otherwise>
        <a href="${url}">${summary.entityName}</a>
      </c:otherwise>
    </c:choose>
    </td>
    <td class="ListCellLeftLine">
    <c:choose>
      <c:when test="${summary.summaryType == AUTOGROUP}">
        <fmt:message key="resource.common.monitor.health.autoGroupType"><fmt:param value="${summary.resourceType.name}"/></fmt:message>
      </c:when>
      <c:when test="${summary.summaryType == CLUSTER}">
        <fmt:message key="resource.common.monitor.health.clusterGroupType"><fmt:param value="${summary.resourceType.name}"/></fmt:message>
      </c:when>
      <c:otherwise>
        <c:out value="${summary.resourceType.name}"/>
      </c:otherwise>
    </c:choose>
    </td>
    <td class="ListCellCheckboxLeftLine">
        <c:out value="${summary.numResources}" default="0"/>
    </td>
    <td class="ListCellCheckboxLeftLine">
    <img src="<c:out value="${stoplightUrl}" escapeXml="false" />" <c:out value="${availStoplightDimensions}" escapeXml="false" /> border="0" height="12">
    </td>
    <td class="ListCellCheckboxLeftLine">
      <hq:metric metric="${summary.throughput}" unit="${summary.throughputUnits}"  defaultKey="common.value.notavail" />
    </td>
  </tr>
    </c:forEach>

</table>
<!--  /  -->

<c:if test="${empty summaries}">
<tiles:insert definition=".resource.common.monitor.visibility.noHealths"/>
</c:if>
