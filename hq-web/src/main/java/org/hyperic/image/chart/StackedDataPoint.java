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

package org.hyperic.image.chart;

import org.hyperic.util.data.IStackedDataPoint;

public class StackedDataPoint extends DataPoint implements IStackedDataPoint
{
    private static final String NO_SET_VALUE = "setValue not support for" +
                                               " StackedDataPoint object";
                                               
    private double[] m_dValues;
    
    /**
     * Constructs a StackedDataPoint object with the specified value and an
     * empty label.
     *
     * @param value
     *      A floating point value for the object data point.
     */
    public StackedDataPoint(double[] values) {
        this(values, null);
    }
    
    /**
     * Constructs a DataPoint object with the specified value and and
     * specified label.
     *
     * @param value
     *      A floating point value for the object's data point.
     * @param label
     *      A String label for the object's data point.
     */
    public StackedDataPoint(double[] values, String label) {
        super(values[0], label);
        this.m_dValues = values;

        // Calculate and set the sum value
        double max = 0;
                
        if(values.length > 0) {
            for(int i = 0;i < values.length;i++)
                max = Math.max(max, values[i]);
        } else {
            max = Double.NaN;
        }
            
        super.setValue(max);
    }

    /**
     * Retrieves the value of a chart data point. The label is displayed on the
     * X axis for stacked performance charts.
     *
     * @return A floating point value for a chart data point.
     */
    public double[] getValues() {
        return m_dValues;
    }

    public void setValue(double value) {
        throw new IllegalArgumentException(NO_SET_VALUE);
    }
}
