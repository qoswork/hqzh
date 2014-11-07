<%@ page language="java" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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


<c:url var="selfUrl" value="/resource/server/Inventory.do">
	<c:param name="mode" value="changeOwner"/>
   	<c:param name="rid" value="${Resource.id}"/>
   	<c:param name="type" value="${Resource.entityId.type}"/>
</c:url>

<html:form action="/resource/server/inventory/ChangeOwner">

<tiles:insert definition=".page.title.resource.server">
  <tiles:put name="titleKey" value="common.title.Edit"/>
  <tiles:put name="titleName" beanName="Resource" beanProperty="name"/>
</tiles:insert>

<tiles:insert definition=".resource.common.inventory.changeResourceOwner">
  <tiles:put name="users" beanName="AllUsers"/>
  <tiles:put name="userCount" beanName="NumUsers"/>
  <tiles:put name="formName" value="ChangeResourceOwnerForm"/>
  <tiles:put name="selfUrl" beanName="selfUrl"/>
</tiles:insert>
      &nbsp;<br>

<tiles:insert definition=".form.buttons">
    <tiles:put name="cancelOnly" value="true"/>
</tiles:insert>

<tiles:insert definition=".page.footer"/>

<html:hidden property="rid"/>
<html:hidden property="type"/>
</html:form>
