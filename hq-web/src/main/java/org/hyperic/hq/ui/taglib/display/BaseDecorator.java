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
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is an abstract class for decorators to inherit from for
 * implementing decorators for columns.
 */
public abstract class BaseDecorator extends ColumnDecorator implements Tag {
	private static Log log = LogFactory.getLog(BaseDecorator.class.getName());

	/**
	 * The main method to override here. This should look something like this:
	 * <code>
	 * 
	 *  String name = null;
	 *  try {
	 *       name = (String) evalAttr("name", this.name, String.class);
	 *  }
	 *  catch (NullAttributeException ne) {
	 *       log.debug("bean " + this.name + " not found");
	 *       return "";
	 *   }
	 *   catch (JspException je) {
	 *       log.debug("can't evaluate name [" + this.name + "]: ", je);
	 *       return "";
	 *   }
	 *  StringBuffer buf = new StringBuffer(1024);
	 *  buf.append("<td>");
	 *  buf.append(obj.toString());
	 *  buf.append("</td>");
	 * 
	 *  return buf.toString()
     * </code>
	 */
	abstract public String decorate(Object obj);

	public int doStartTag() throws JspTagException {
		Object parent = getParent();

		if (parent == null || !(parent instanceof ColumnTag)) {
			throw new JspTagException(
					"A BaseDecorator must be used within a ColumnTag.");
		}

		((ColumnTag) parent).setDecorator(this);

		return SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

	/**
	 * The name="foo" property.
	 */
	private Tag parent;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public Tag getParent() {
		return parent;
	}

	public void setParent(Tag t) {
		this.parent = t;
	}

	public void release() {
		super.release();
		name = null;
		parent = null;
	}

	protected String generateErrorComment(String exc, String attrName,
			String attrValue, Throwable t) {
		log
				.debug(attrName + " expression [" + attrValue
						+ "] not evaluated", t);
		StringBuffer sb = new StringBuffer("<!-- ");
		sb.append(" failed due to ");
		sb.append(exc);
		sb.append(" on ");
		sb.append(attrName);
		sb.append(" = ");
		sb.append(attrValue);
		sb.append(" -->");
		return sb.toString();
	}
}