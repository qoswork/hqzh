<%@ page language="java" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
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


<tiles:importAttribute name="availableNarrowPortlets"/>
<tiles:importAttribute name="availableWidePortlets"/>
<tiles:importAttribute name="narrowPortlets"/>
<tiles:importAttribute name="widePortlets"/>
<tiles:importAttribute name="userPortal"/>
<jsu:importScript path="/js/pageLayout.js" />
<jsu:script>
  	var noDelete = false;      
  	var help = "<hq:help/>";
  	var imagePath = "/images/";

	/*-- start initialize --*/
	<c:set var="array" value="leftArr"/>
	<c:forEach var="portlets" items="${userPortal.portlets}" >
	  <c:set var="i" value="0"/>  
	  var <c:out value="${array}"/> = new Array();
	  <c:forEach var="portlet" items="${portlets}">        
	    <c:out value="${i}"/>
	    <c:out value="${array}"/>[<c:out value="${i}"/>] = new Option("<fmt:message key="${portlet.url}"/>", "<c:out value="${portlet.url}"/>");
	    <c:set var="i" value="${i + 1}"/>  
	  </c:forEach>
	  <c:set var="array" value="rightArr"/>  
	</c:forEach>   
	/*-- end initialize --*/
</jsu:script>  
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr class="PageTitle"> 
    <td rowspan="99"><html:img page="/images/spacer.gif" width="5" height="1" alt="" border="0"/></td>
    <td><html:img page="/images/spacer.gif" width="15" height="1" alt="" border="0"/></td>
    <td width="67%" class="PortletTitle"><fmt:message key="dash.home.ChangeLayout.Title"/></td>
    <td width="32%"><html:img page="/images/spacer.gif" width="202" height="32" alt="" border="0"/></td>
    <td width="1%"><html:link href="" onclick="window.open(help,'help','width=800,height=650,scrollbars=yes,toolbar=yes,left=80,top=80,resizable=yes'); return false;"><html:img page="/images/title_pagehelp.gif" width="20" height="20" alt="" border="0" hspace="10"/></html:link></td>
  </tr>
  <tr> 
    <td valign="top" align="left" rowspan="99"></td>
    <td colspan="2"><html:img page="/images/spacer.gif" width="1" height="10" alt="" border="0"/></td>
  </tr>
  <tr valign="top"> 
    <td colspan="2">
      <html:form action="/dashboard/ModifyLayout" onsubmit="selectAllOptions('leftSel'); selectAllOptions('rightSel');">
      <!-- Content Block Title: Display Settings -->
      <tiles:insert definition=".header.tab">
        <tiles:put name="tabKey" value="dash.settings.DisplaySettings"/>
      </tiles:insert>
      <!-- fixme: This block should only have a help icon, no minimize functionality -->

      <!-- Display Settings Content -->
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr valign="top">
          <td width="1%" class="BlockContent" rowspan="5"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
          <td width="30%" class="BlockContent" colspan="2"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
          <td width="1%" class="BlockContent" rowspan="5"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
          <td width="1%" class="BlockContent" rowspan="5"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
          <td width="67%" class="BlockContent" colspan="2"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
        </tr>
        <tr valign="top">
          <td width="25%" class="BlockContent"><fmt:message key="dash.settings.FormLabel.Left"/></td>
          <td width="5%" class="BlockContent">&nbsp;</td>
          <td width="60%" class="BlockContent"><fmt:message key="dash.settings.FormLabel.Right"/></td>
          <td width="5%" class="BlockContent">&nbsp;</td>
        </tr>
         <tr valign="top">
          <td class="BlockContent">
            <table width="100%" cellspacing="0" cellpadding="0" border="0">
              <tr>
                <td>
                  <select name="leftSel" id="leftSel" multiple size="7" style="WIDTH: 200px;" onChange="replaceButtons(this, 'left')" onClick="replaceButtons(this, 'left')">
                  </select>
                </td>
                <td>&nbsp;</td>
                <td width="100%" id="leftNav">
                  <div id="leftUp"><html:img page="/images/dash_movecontent_up-off.gif" width="20" height="20" alt="" border="0"/></div>
                  <html:img page="/images/spacer.gif" width="1" height="10" border="0"/>
                  <div id="leftDown"><html:img page="/images/dash_movecontent_dn-off.gif" width="20" height="20" alt="" border="0"/></div>
                  <html:img page="/images/spacer.gif" width="1" height="20" border="0"/>
                  <div id="leftDelete"><html:img page="/images/dash_movecontent_del-off.gif" width="20" height="20" alt="" border="0"/></div>
                </td>
              </tr>
            </table>
          </td>
          <td class="BlockContent">&nbsp;</td>
          <td class="BlockContent" width="30%">
            <table width="100%" cellspacing="0" cellpadding="0" border="0">
              <tr>
                <td>
                  <select name="rightSel" id="rightSel" multiple size="7" style="WIDTH: 300px;" onChange="replaceButtons(this, 'right')" onClick="replaceButtons(this, 'right')">               
                  </select>
                </td>
                <td>&nbsp;</td>
                <td width="100%" id="rightNav">
                  <div id="rightUp"><html:img page="/images/dash_movecontent_up-off.gif" width="20" height="20" alt="Click to Save Changes" border="0"/></div>
                  <html:img page="/images/spacer.gif" width="1" height="10" border="0"/>
                  <div id="rightDown"><html:img page="/images/dash_movecontent_dn-off.gif" width="20" height="20" alt="Click to Save Changes" border="0"/></div>
                  <html:img page="/images/spacer.gif" width="1" height="20" border="0"/>
                  <div id="rightDelete"><html:img page="/images/dash_movecontent_del-off.gif" width="20" height="20" alt="Click to Save Changes" border="0"/></div>
                </td>
              </tr>
            </table>
          </td>
          <td class="BlockContent">&nbsp;</td>
        </tr>
        <tr valign="top">
          <td class="BlockContent" colspan="2">&nbsp;</td>
          <td class="BlockContent" colspan="2">&nbsp;</td>
        </tr>
         <tr valign="top">
          <td class="BlockContent">
            <table cellpadding="0" cellspacing="0" border="0" width="100%">
              <tr valign="top">
                <td colspan="3" class="ToolbarLine"><html:img page="/images/spacer.gif" width="20" height="1" border="0"/></td>
              </tr>
              <tr valign="top">
                <td colspan="3"><html:img page="/images/spacer.gif" width="20" height="1" border="0"/></td>
              </tr>
              <tr>
                <td colspan="3" class="FormLabel"><fmt:message key="dash.home.FormLabel.AddContent"/></td>
              </tr>
              <tr>
                <td valign="center">
                  <select id="leftContent" name="leftContent"  onchange="replaceAddButton(this, 'left');">
                    <c:choose>
                      <c:when test="${empty availableNarrowPortlets}">
                        <option value="null"><fmt:message key="portal.admin.layout.noContent"/></option>
                      </c:when>
                      <c:otherwise>
                        <option value="null"><fmt:message key="dash.home.AddContent.select"/></option>
                        <c:forEach var="portlet" items="${availableNarrowPortlets}" >  
                          <option value="<c:out value="${portlet}"/>"><fmt:message key="${portlet}"/></option>
                        </c:forEach>
                      </c:otherwise>
                    </c:choose>
                  </select>
                </td>
                <td>&nbsp;</td>
                <td width="100%" id="leftContentTd"><div id="leftContentDiv"><html:img page="/images/dash_movecontent_add-off.gif" width="20" height="20" alt="" border="0"/></div></td>
              </tr>
            </table>
          </td>
          <td class="BlockContent">&nbsp;</td>
          <td class="BlockContent">
            <table cellpadding="0" cellspacing="0" border="0" width="100%">
              <tr valign="top">
                <td colspan="3" class="ToolbarLine"><html:img page="/images/spacer.gif" width="20" height="1" border="0"/></td>
              </tr>
              <tr valign="top">
                <td colspan="3"><html:img page="/images/spacer.gif" width="20" height="1" border="0"/></td>
              </tr>
              <tr>
                <td colspan="3" class="FormLabel"><fmt:message key="dash.home.FormLabel.AddContent"/></td>
              </tr>
              <tr>
                <td valign="center">
                  <select id="rightContent" name="rightContent" onchange="replaceAddButton(this, 'right');">
                    <c:choose>
                      <c:when test="${empty availableWidePortlets}">
                        <option value="null"><fmt:message key="portal.admin.layout.noContent"/></option>  
                      </c:when>
                      <c:otherwise>
                        <option value="null"><fmt:message key="dash.home.AddContent.select"/></option>
                        <c:forEach var="portlet" items="${availableWidePortlets}" >  
                          <option value="<c:out value="${portlet}"/>"><fmt:message key="${portlet}"/></option>
                        </c:forEach>
                      </c:otherwise>
                    </c:choose>
                  </select>
                </td>
                <td>&nbsp;</td>
                <td width="100%" id="rightContentTd"><div id="rightContentDiv"><html:img page="/images/dash_movecontent_add-off.gif" width="20" height="20" alt="" border="0"/></div></td>
              </tr>
            </table>		
          </td>
          <td class="BlockContent">&nbsp;</td>
        </tr>
        <tr>
          <td colspan="7" class="BlockBottomLine"><html:img page="/images/spacer.gif" width="1" height="1" border="0"/></td>
        </tr>
      </table>
	<jsu:script>
      	var leftSel = document.getElementById("leftSel");
      	var rightSel = document.getElementById("rightSel");
      
      	for(i=0; i<leftArr.length; i++) {
        	leftSel.options[i] = leftArr[i];
      	}
      	for(i=0; i<rightArr.length; i++) {
              rightSel.options[i] = rightArr[i];
      	}
	</jsu:script>
      <tiles:insert definition=".form.buttons"/>
      </html:form>
    </td>
  </tr>
  <tr> 
    <td colspan="4"><html:img page="/images/spacer.gif" width="1" height="13" alt="" border="0"/></td>
  </tr>
</table>
