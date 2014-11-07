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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * usage <display:column property="priority" width=="10"
 * title"alerts.alert.listheader.priority"/> <display:prioritydecorator
 * flagKey="application.properties.key.prefix"/>
 * 
 */
public class AlternateDecorator extends BaseDecorator {
	private static Log log = LogFactory.getLog(AlternateDecorator.class
			.getName());

	/** Holds value of property secondChoice. */
	private String secondChoice;

	// our ColumnDecorator

	/**
	 * If string column value exists, use that. Otherwise, return 2nd choice.
	 * 
	 * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
	 */
	public String decorate(Object columnValue) {
		String firstChoice = (String) columnValue;

		if (firstChoice == null || "".equals(firstChoice.trim())) {
			secondChoice = getSecondChoice();

			return secondChoice;
		} else {
			return firstChoice;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		super.release();
		secondChoice = null;
	}

	/**
	 * Getter for property secondChoice.
	 * 
	 * @return Value of property secondChoice.
	 * 
	 */
	public String getSecondChoice() {
		return this.secondChoice;
	}

	/**
	 * Setter for property secondChoice.
	 * 
	 * @param secondChoice
	 *            New value of property secondChoice.
	 * 
	 */
	public void setSecondChoice(String secondChoice) {
		this.secondChoice = secondChoice;
	}
}