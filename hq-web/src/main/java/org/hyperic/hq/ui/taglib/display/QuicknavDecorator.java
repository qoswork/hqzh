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

package org.hyperic.hq.ui.taglib.display;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.appdef.shared.AppdefResourceValue;
import org.hyperic.hq.ui.taglib.QuicknavUtil;

/**
 * This class is a two in one decorator/tag for use within the
 * <code>TableTag</code>; it is a <code>ColumnDecorator</code> tag that that
 * creates a row of quicknav icons for the resource hub.
 */
public class QuicknavDecorator extends ColumnDecorator implements Tag {
	private static Log log = LogFactory.getLog(QuicknavDecorator.class.getName());

	private AppdefResourceValue resource;
	private PageContext context;
	private Tag parent;

	public AppdefResourceValue getResource() {
		return resource;
	}

	public void setResource(AppdefResourceValue s) {
		resource = s;
	}

	public String decorate(Object obj) throws Exception {
		try {
			AppdefResourceValue rv = getResource();

			if (rv.getEntityId() == null) {
				return QuicknavUtil.getNA();
			}

			return QuicknavUtil.getOutput(rv, context);
		} catch (NullPointerException npe) {
			log.debug(npe);
		}
		
		return QuicknavUtil.getNA();
	}

	public int doStartTag() throws JspTagException {
		ColumnTag ancestorTag = (ColumnTag) TagSupport.findAncestorWithClass(this, ColumnTag.class);
		
		if (ancestorTag == null) {
			throw new JspTagException("An QuicknavDecorator must be used within a ColumnTag.");
		}
		
		ancestorTag.setDecorator(this);
		
		return SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

	public Tag getParent() {
		return parent;
	}

	public void setParent(Tag t) {
		parent = t;
	}

	public void setPageContext(PageContext pc) {
		context = pc;
	}

	public void release() {
		parent = null;
		context = null;
		resource = null;
	}
}
