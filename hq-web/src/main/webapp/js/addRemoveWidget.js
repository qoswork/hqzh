// NOTE: This copyright does *not* cover user programs that use HQ
// program services by normal system calls through the application
// program interfaces provided as part of the Hyperic Plug-in Development
// Kit or the Hyperic Client Development Kit - this is merely considered
// normal use of the program, and does *not* fall under the heading of
// "derived work".
// 
// Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
// This file is part of HQ.
// 
// HQ is free software; you can redistribute it and/or modify
// it under the terms version 2 of the GNU General Public License as
// published by the Free Software Foundation. This program is distributed
// in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// even the implied warranty of MERCHANTABILITY or FITNESS FOR A
// PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA.


/*-- START addRemoveWidget.js --*/
function ReplaceAddRemoveButton(divId, tdId, tdState) {
	var newDiv = document.createElement("DIV");
	newDiv.setAttribute("id", divId);
	
	var newImg;
	var imgPath;
	var imgState = "";
	if (tdState=="off")
		imgState = "_gray";
	var imgName;
	var width = "33";
	var height = "38";
	
	if (divId == "AddButtonDiv") {
		imgName = imagePath + "fb_addarrow";
		divAlign = "left";
		inputName = "add";
		inputValue = "add";
	}
	else {
		imgName = imagePath + "fb_removearrow";
		divAlign = "right";
		inputName = "remove";
		inputValue = "remove";
	}
	
	newDiv.setAttribute("align", divAlign);
	imgPath = imgName + imgState + ".gif";
	newImg = getNewImage (imgPath, width, height, "0");
	
	if (tdState == "on") {
		var newInput = document.createElement("INPUT");
		newInput.setAttribute("type", "image");
		newInput.setAttribute("src", imgPath);
		newInput.setAttribute("name", inputName);
		newDiv.appendChild(newInput);
	}
	
	else {
		newDiv.appendChild(newImg);
	}
	
	var td = document.getElementById(tdId);
	var oldDiv = document.getElementById(divId);
	if (td!=null)
		td.replaceChild(newDiv,oldDiv);
}

function ToggleAddRemoveButtons(numSelected, buttonDiv, prefix) {
	if (numSelected < 0) {
		alert("Error: Number of selected checkboxes is less then zero!");
	}
	
	else if (numSelected == 0) {
		ReplaceAddRemoveButton(buttonDiv, prefix + "AddRemoveButtonTd", "off");
	}
	
	else if (numSelected > 0) {
		ReplaceAddRemoveButton(buttonDiv, prefix + "AddRemoveButtonTd", "on");
	}
}

function ToggleSelection(e, widgetProperties) {
	if (isIE)
		e = event.srcElement;

	widgetInstanceName = widgetProperties["name"];
	
	var prefix = widgetInstanceName;	
	var form = e.form;
	
	if (e.checked) {
		highlight(e);
		
		if (e.getAttribute(classStr)=="availableListMember") {
			var numFromSelected = getWidgetProperty(widgetInstanceName, "numFromSelected");
			setWidgetProperty(widgetInstanceName, "numFromSelected", ++numFromSelected);
			ToggleAddRemoveButtons(numFromSelected, "AddButtonDiv", prefix);
		}
		else {
			var numToSelected = getWidgetProperty(widgetInstanceName, "numToSelected");
			setWidgetProperty(widgetInstanceName, "numToSelected", ++numToSelected);
			ToggleAddRemoveButtons(numToSelected, "RemoveButtonDiv", prefix);
		}
	}
	else {
		unhighlight(e);
		
		if (e.getAttribute(classStr)=="availableListMember") {
			var numFromSelected = getWidgetProperty(widgetInstanceName, "numFromSelected");
			setWidgetProperty(widgetInstanceName, "numFromSelected", --numFromSelected);
			ToggleAddRemoveButtons(numFromSelected, "AddButtonDiv", prefix);
			form.fromToggleAll.checked = false;
		}
		else {
			var numToSelected = getWidgetProperty(widgetInstanceName, "numToSelected");
			setWidgetProperty(widgetInstanceName, "numToSelected", --numToSelected);
			ToggleAddRemoveButtons(numToSelected, "RemoveButtonDiv", prefix);
			form.toToggleAll.checked = false;
		}
	}
}

function ToggleAll(e, widgetProperties) {
	if (isIE)
		e = event.srcElement;
	
	widgetInstanceName = widgetProperties["name"];
	
	var prefix = widgetInstanceName;
	var form = e.form;
	var div = getDivName(e);
	var buttonDiv;
	var listName;

	if (div==widgetInstanceName + "FromDiv") {
		buttonDiv = "AddButtonDiv";
		listName = "available";
	}
	else {
		buttonDiv = "RemoveButtonDiv";
		listName = "pending";
	}
	
	if (e.checked) {
		CheckAll(listName, widgetInstanceName);
		ReplaceAddRemoveButton(buttonDiv, prefix + "AddRemoveButtonTd", "on");
	}
	else {
		ClearAll(listName, widgetInstanceName);
		ReplaceAddRemoveButton(buttonDiv, prefix + "AddRemoveButtonTd", "off");
	}
	
}

function CheckAll(listName, widgetInstanceName) {
	var uList = document.getElementsByTagName("input");
	var len = uList.length;
	var numCheckboxes = 0;

	for (var i = 0; i < len; i++) {
		var e = uList.item(i);
		if (e.getAttribute(classStr) == listName + "ListMember") {
			Check(e);
			numCheckboxes++;
		}
	}
	
	if (listName=="available") {
		setWidgetProperty(widgetInstanceName, "numFromSelected", numCheckboxes);
	}
	else {
		setWidgetProperty(widgetInstanceName, "numToSelected", numCheckboxes);
	}
}
	
function ClearAll(listName, widgetInstanceName) {
	var uList = document.getElementsByTagName("input");
	var len = uList.length;
	
	for (var i = 0; i < len; i++) {
		var e = uList.item(i);
		if (e.getAttribute(classStr) == listName + "ListMember") {
			Clear(e);
		}
	}
	if (listName=="available") {
		setWidgetProperty(widgetInstanceName, "numFromSelected", 0);
	}
	else {
		setWidgetProperty(widgetInstanceName, "numToSelected", 0);
	}
}

/*-- END addRemoveWidget.js --*/

