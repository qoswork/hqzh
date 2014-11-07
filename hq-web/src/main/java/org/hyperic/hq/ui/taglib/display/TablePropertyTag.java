/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
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

/*
 * TablePropertyTag.java
 *
 * Created on January 2, 2003, 12:47 PM
 */

package org.hyperic.hq.ui.taglib.display;

import java.util.List;

import org.hyperic.hq.ui.Constants;

/**
 * Bean properties for TableTag
 * 
 */
public class TablePropertyTag extends TemplateTag {
	private static final long serialVersionUID = 1L;

	private List items = null;
	private String property = null;
	private Integer length = new Integer(0);
	private Integer offset = new Integer(0);
	private String scope = null;
	private String decorator = null;
	private String export = null;
	private String paramId = null;
	private String paramName = null;
	private String paramProperty = null;
	private String paramScope = null;
	private String width = null;
	private String border = null;
	private String cellspacing = null;
	private String cellpadding = null;
	private String align = null;
	private String nowrapHeader = null;
	private String background = null;
	private String bgcolor = null;
	private String frame = null;
	private String height = null;
	private String hspace = null;
	private String rules = null;
	private String summary = null;
	private String vspace = null;
	private String styleClass = null;
	private String styleId = null;

	/** Holds value of property pageValue. */
	private String pageValue = Constants.PAGENUM_PARAM;

	/** Holds value of property orderValue. */
	private String orderValue = Constants.SORTORDER_PARAM;

	/** Holds value of property pageSizeValue. */
	private String pageSizeValue = Constants.PAGESIZE_PARAM;
	private String sortValue = Constants.SORTCOL_PARAM;
	private Integer page;
	private Integer pageSize = null;
	private String order = null;
	private Integer sort = null;

	/**
	 * This variable hold the value of the data has to be displayed without the
	 * table.
	 */
	private String display = null;
	private String action = null;

	/* should we include adornments for the "add to list" table? */
	private boolean padRows = false;

	/* if there are no rows, present this as a message */
	private String emptyMsg = null;

	/* should we include the right-side sidebar? */
	private boolean rightSidebar = false;

	/* should we include the left-side sidebar? */
	private boolean leftSidebar = false;

	/**
	 * Holds value of property includeTotals. should the tage generate total
	 * list size for the user.
	 */
	private boolean includeTotals;

	/**
	 * This variable always points to the current data cell
	 */
	private String var = null;

	/**
	 * Getter for property includeTotals.
	 * 
	 * @return Value of property includeTotals.
	 * 
	 */
	public boolean getIncludeTotals() {
		return this.includeTotals;
	}

	public void setIncludeTotals(boolean includeTotals) {
		this.includeTotals = includeTotals;
	}

	public void setPadRows(boolean padRows) {
		this.padRows = padRows;
	}

	public boolean isPadRows() {
		return this.padRows;
	}

	public void setEmptyMsg(String emptyMsg) {
		this.emptyMsg = emptyMsg;
	}

	public String getEmptyMsg() {
		return this.emptyMsg;
	}

	public void setRightSidebar(boolean rightSidebar) {
		this.rightSidebar = rightSidebar;
	}

	public boolean isRightSidebar() {
		return this.rightSidebar;
	}

	public void setLeftSidebar(boolean leftSidebar) {
		this.leftSidebar = leftSidebar;
	}

	public boolean isLeftSidebar() {
		return this.leftSidebar;
	}

	/**
	 * getter for var. var is a value that points to the current data cell in
	 * the page scope, very jstl-esque.
	 */
	public String getVar() {
		return this.var;
	}

	/**
	 * setter for var. var is a value that points to the curent data cell in the
	 * page scope, very jstl-esque.
	 */
	public void setVar(String var) {
		this.var = var;
	}

	public void setParamId(String v) {
		this.paramId = v;
	}

	public void setParamName(String v) {
		this.paramName = v;
	}

	public void setParamProperty(String v) {
		this.paramProperty = v;
	}

	public void setParamScope(String v) {
		this.paramScope = v;
	}

	public String getParamId() {
		return this.paramId;
	}

	public String getParamName() {
		return this.paramName;
	}

	public String getParamProperty() {
		return this.paramProperty;
	}

	public String getParamScope() {
		return this.paramScope;
	}

	public void setPage(Integer v) {
		page = v;
	}

	public void setPageSize(Integer v) {
		pageSize = v;
	}

	public void setOrder(String v) {
		order = v;
	}

	public void setSort(Integer v) {
		sort = v;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public String getOrder() {
		return order;
	}

	public Integer getSort() {
		return sort;
	}

	/**
	 * Getter for property pageValue.
	 * 
	 * @return Value of property pageValue.
	 * 
	 */
	public String getPageValue() {
		return this.pageValue;
	}

	/**
	 * Setter for property pageValue.
	 * 
	 * @param pageValue
	 *            New value of property pageValue.
	 * 
	 */
	public void setPageValue(String pageValue) {
		this.pageValue = pageValue;
	}

	/**
	 * Getter for property orderValue.
	 * 
	 * @return Value of property orderValue.
	 * 
	 */
	public String getOrderValue() {
		return this.orderValue;
	}

	/**
	 * Setter for property orderValue.
	 * 
	 * @param orderValue
	 *            New value of property orderValue.
	 * 
	 */
	public void setOrderValue(String orderValue) {
		this.orderValue = orderValue;
	}

	/**
	 * Getter for property sortValue.
	 * 
	 * @return Value of property sortValue.
	 * 
	 */
	public String getSortValue() {
		return sortValue;
	}

	/**
	 * Setter for property sortValue.
	 * 
	 * @param sortValue
	 *            New value of property sortValue.
	 * 
	 */
	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
	}

	/**
	 * Getter for property pageSizeValue.
	 * 
	 * @return Value of property pageSizeValue.
	 * 
	 */
	public String getPageSizeValue() {
		return this.pageSizeValue;
	}

	/**
	 * Setter for property pageSizeValue.
	 * 
	 * @param pageSizeValue
	 *            New value of property pageSizeValue.
	 * 
	 */
	public void setPageSizeValue(String pageSizeValue) {
		this.pageSizeValue = pageSizeValue;
	}

	public void setItems(List v) {
		this.items = v;
	}

	public void setProperty(String v) {
		this.property = v;
	}

	public void setLength(Integer v) {
		this.length = v;
	}

	public void setOffset(Integer v) {
		this.offset = v;
	}

	public void setScope(String v) {
		this.scope = v;
	}

	public void setDecorator(String v) {
		this.decorator = v;
	}

	public void setExport(String v) {
		this.export = v;
	}

	public void setWidth(String v) {
		this.width = v;
	}

	public void setBorder(String v) {
		this.border = v;
	}

	public void setCellspacing(String v) {
		this.cellspacing = v;
	}

	public void setCellpadding(String v) {
		this.cellpadding = v;
	}

	public void setAlign(String v) {
		this.align = v;
	}

	public void setNowrapHeader(String v) {
		this.nowrapHeader = v;
	}

	public void setBackground(String v) {
		this.background = v;
	}

	public void setBgcolor(String v) {
		this.bgcolor = v;
	}

	public void setFrame(String v) {
		this.frame = v;
	}

	public void setHeight(String v) {
		this.height = v;
	}

	public void setHspace(String v) {
		this.hspace = v;
	}

	public void setRules(String v) {
		this.rules = v;
	}

	public void setSummary(String v) {
		this.summary = v;
	}

	public void setVspace(String v) {
		this.vspace = v;
	}

	public void setStyleClass(String v) {
		this.styleClass = v;
	}

	public void setStyleId(String v) {
		this.styleId = v;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public List getItems() {
		return this.items;
	}

	public String getProperty() {
		return this.property;
	}

	public Integer getLength() {
		return this.length;
	}

	public Integer getOffset() {
		return this.offset;
	}

	public String getScope() {
		return this.scope;
	}

	public String getDecorator() {
		return this.decorator;
	}

	public String getExport() {
		return this.export;
	}

	public String getWidth() {
		return this.width;
	}

	public String getBorder() {
		return this.border;
	}

	public String getCellspacing() {
		return this.cellspacing;
	}

	public String getCellpadding() {
		return this.cellpadding;
	}

	public String getAlign() {
		return this.align;
	}

	public String getNowrapHeader() {
		return this.nowrapHeader;
	}

	public String getBackground() {
		return this.background;
	}

	public String getBgcolor() {
		return this.bgcolor;
	}

	public String getFrame() {
		return this.frame;
	}

	public String getHeight() {
		return this.height;
	}

	public String getHspace() {
		return this.hspace;
	}

	public String getRules() {
		return this.rules;
	}

	public String getSummary() {
		return this.summary;
	}

	public String getVspace() {
		return this.vspace;
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public String getStyleId() {
		return this.styleId;
	}

	public String getDisplay() {
		return this.display;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}