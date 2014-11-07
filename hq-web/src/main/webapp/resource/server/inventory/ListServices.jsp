<%@ page language="java" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/hq.tld" prefix="hq" %>
<%@ taglib uri="/WEB-INF/tld/display.tld" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/jsUtils" prefix="jsu" %>
<%--
  NOTE: This copyright does *not* cover user programs that use HQ
  program services by normal system calls through the application
  program interfaces provided as part of the Hyperic Plug-in Development
  Kit or the Hyperic Client Development Kit - this is merely considered
  normal use of the program, and does *not* fall under the heading of
  "derived work".
  
  Copyright (C) [2004-2008], Hyperic, Inc.
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

<hq:pageSize var="pageSize"/>

<tiles:importAttribute name="serviceCount"/>
<tiles:importAttribute name="serviceTypeMap"/>
<tiles:importAttribute name="selfAction"/>

<c:set var="newServiceUrl" value="/resource/service/Inventory.do?mode=new&rid=${Resource.id}&type=${Resource.entityId.type}" />
<c:set var="widgetInstanceName" value="listServices"/>

<c:url var="pssAction" value="${selfAction}">
  <c:if test="${not empty param.fs}">
    <c:param name="fs" value="${param.fs}"/>
  </c:if>
  <c:if test="${not empty param.pns}">
    <c:param name="pns" value="${param.pns}"/>
  </c:if>
  <c:if test="${not empty param.sos}">
    <c:param name="sos" value="${param.sos}"/>
  </c:if>
  <c:if test="${not empty param.scs}">
    <c:param name="scs" value="${param.scs}"/>
  </c:if>
  <c:if test="${not empty param.png}">
    <c:param name="png" value="${param.png}"/>
  </c:if>
  <c:if test="${not empty param.psg}">
    <c:param name="psg" value="${param.psg}"/>
  </c:if>
  <c:if test="${not empty param.sog}">
    <c:param name="sog" value="${param.sog}"/>
  </c:if>
  <c:if test="${not empty param.scg}">
    <c:param name="scg" value="${param.scg}"/>
  </c:if>
</c:url>

<c:url var="ssAction" value="${selfAction}">
  <c:if test="${not empty param.fs}">
    <c:param name="fs" value="${param.fs}"/>
  </c:if>
  <c:if test="${not empty param.pss}">
    <c:param name="pss" value="${param.pss}"/>
  </c:if>
  <c:if test="${not empty param.pns}">
    <c:param name="pns" value="${param.pns}"/>
  </c:if>
  <c:if test="${not empty param.png}">
    <c:param name="png" value="${param.png}"/>
  </c:if>
  <c:if test="${not empty param.psg}">
    <c:param name="psg" value="${param.psg}"/>
  </c:if>
  <c:if test="${not empty param.sog}">
    <c:param name="sog" value="${param.sog}"/>
  </c:if>
  <c:if test="${not empty param.scg}">
    <c:param name="scg" value="${param.scg}"/>
  </c:if>
</c:url>

<c:url var="pnAction" value="${selfAction}">
</c:url>
<jsu:importScript path="/js/listWidget.js" />
<jsu:script>
	initializeWidgetProperties('<c:out value="${widgetInstanceName}"/>');
	widgetProperties = getWidgetProperties('<c:out value="${widgetInstanceName}"/>');
</jsu:script>
<!--  SERVICE COUNTS CONTENTS -->
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td width="20%" class="BlockLabel"><fmt:message key="resource.server.inventory.serviceCounts.TotalServices"/></td>
		<td width="30%" class="BlockContent"><c:out value="${serviceCount}"/></td>
    <td width="20%" class="BlockLabel">&nbsp;</td>
    <td width="30%" class="BlockContent">&nbsp;</td>
  </tr>
  <tr valign="top">
    <td width="20%" class="BlockLabel"><fmt:message key="resource.server.inventory.serviceCounts.ServicesByType"/></td>
    <td width="30%" class="BlockContentNoPadding" colspan="3">
      <table width="66%" cellpadding="0" cellspacing="0" border="0" class="BlockContent">
        <tr valign="top">
<c:forEach var="entry" varStatus="status" items="${serviceTypeMap}">
          <td width="50%"><c:out value="${entry.key}"/> (<c:out value="${entry.value}"/>)</td>
  <c:choose>
    <c:when test="${status.count % 2 == 0}">
        </tr>
        <tr>
    </c:when>
    <c:otherwise>
      <c:if test="${status.last}">
        <c:forEach begin="${(status.count % 2) + 1}" end="2">
          <td width="50%">&nbsp;</td>
        </c:forEach>
      </c:if>
    </c:otherwise>
  </c:choose>
</c:forEach>
        </tr>
      </table> 
    </td>
  </tr>
  <tr>
    <td colspan="4" class="BlockBottomLine"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
  </tr>
</table>
<html:form action="/resource/server/inventory/RemoveService">
<html:hidden property="eid"/>

<!-- tiles:insert page="View_FilterToolbar.jsp"/ -->

<!--  SERVICES CONTENTS -->
<div id="listDiv">
  <display:table items="${Services}" cellspacing="0" cellpadding="0" width="100%" action="${ssAction}" var="service" pageSizeValue="pss" pageSize="${param.pss}" pageValue="pns" page="${param.pns}" orderValue="sos" order="${param.sos}" sortValue="scs" sort="${param.scs}">

    <display:column width="1%" property="id" 
                    title="<input type=\"checkbox\" onclick=\"ToggleAll(this, widgetProperties)\" name=\"listToggleAll\">"  
		    isLocalizedTitle="false" styleClass="ListCellCheckbox" headerStyleClass="ListHeaderCheckbox" >
      <display:checkboxdecorator name="resources" onclick="ToggleSelection(this, widgetProperties)" styleClass="listMember"/>
    </display:column>
    <display:column width="20%" property="name" sort="true" sortAttr="5"
                    defaultSort="true" title="resource.server.inventory.services.ServiceTH" 
                    href="/resource/service/Inventory.do?mode=view&rid=${service.id}&type=${service.entityId.type}" />
       
    <display:column width="20%" property="serviceType.name" sort="true" sortAttr="23"
                    defaultSort="false" title="resource.server.inventory.services.TypeTH" /> 

    <display:column width="20%" property="description" title="common.header.Description" />
    <display:column property="id" title="resource.common.monitor.visibility.AvailabilityTH" width="20%" styleClass="ListCellCheckbox" headerStyleClass="ListHeaderCheckbox" valign="middle">
      <display:availabilitydecorator resource="${service}"/>
    </display:column>
  </display:table>

</div>
<!--  /  -->
<tiles:insert definition=".toolbar.list">
  <tiles:put name="listNewUrl" beanName="newServiceUrl"/>
  <tiles:put name="deleteOnly"><c:out value="${!useroperations['createService']}"/>"</tiles:put>
  <c:if test="${autoInventory == true}">
  <tiles:put name="deleteOnly" value="true"/>
  </c:if>
  <tiles:put name="listItems" beanName="Services"/>
  <tiles:put name="listSize" beanName="Services" beanProperty="totalSize"/>
  <tiles:put name="pageSizeAction" beanName="pssAction" />
  <tiles:put name="pageSizeParam" value="pss"/>
  <tiles:put name="pageNumAction" beanName="pnAction"/>    
  <tiles:put name="pageNumParam" value="pns"/>
  <tiles:put name="widgetInstanceName" beanName="widgetInstanceName"/>
  <tiles:put name="defaultSortColumn" value="5"/>
</tiles:insert>

</html:form>

