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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import org.hyperic.util.data.IHighLowDataPoint;

/**
 * HighLowChart draws a horizontal chart with columns that display the high,
 * low and average values for each time unit on the chart. For a description
 * of how to use LineChart, see net.hyperic.chart.Chart.
 *
 * @see net.hyperic.chart.Chart
 */
public class HighLowChart extends ColumnChart {
    protected static final Color DEFAULT_HIGHLOW_COLOR = new Color(0x00, 0x00, 0x80);
    protected static final int   DEFAULT_HIGHLOW_HEIGHT = 125;
    
    /**
     * Color of High-Low bars.
     */
    public Color HighLowColor = DEFAULT_HIGHLOW_COLOR;

    public HighLowChart() {
        this(Chart.DEFAULT_WIDTH);
    }

    public HighLowChart(int width) {
        super(width, DEFAULT_HIGHLOW_HEIGHT);
    }
    
    public HighLowChart(int width, int height) {
        super(width, height);
    }

    protected void init() {
        super.init();
        this.showLeftLabels = false;
        this.showBottomLabels = false;
        this.showBottomLegend = false;
        this.valueLines = 5;
        this.showAverage = false;
        this.showPeak = false;
        this.showLow = false;
        this.showBaseline = false;
        this.showHighRange = true;
        this.showLowRange = true;
    }

    /**
     * Retrieves the color of the high-low line.
     *
     * @return
     *      A java.awt.Color object that contains the color of the columns.
     *
     * @see java.awt.Color
     */
    public Color getHighLowLineColor() {
        return this.HighLowColor;
    }

    /**
     * Sets the color of the columns.
     *
     * @param value
     *      A java.awt.Color object that contains the color of the high-low line.
     *
     * @exception IllegalArgumentException
     *      If the value parameter is null.
     *
     * @see java.awt.Color
     */
    public void setHighLowLineColor(Color value) {
        if(value == null) throw new IllegalArgumentException();
        this.HighLowColor = value;
    }

    protected void paint(ChartGraphics g, Rectangle rect) {
        /////////////////////////////////////////////////////////
        // Draw the High-Low Column Bars

        // Calculate Bar Width
        int halfcol = this.columnWidth / 2;
        
        Rectangle rectBar = new Rectangle();
        rectBar.width     = this.columnWidth;

        DataPointCollection coll = this.getDataPoints();
        int                 collSize = coll.size();

        for (int index = 0; index < collSize; index++) {
            Object obj = coll.get(index);
            
            if(obj instanceof IHighLowDataPoint) {
                IHighLowDataPoint datapt = (IHighLowDataPoint)obj;
                
                Point ptHigh = this.adjustBorders( this.getDisplayPoint(rect.height, rect.width, collSize, datapt.getHighValue(), index) );
                Point ptLow  = this.adjustBorders( this.getDisplayPoint(rect.height, rect.width, collSize, datapt.getLowValue(), index) );
                Point ptAvg  = this.adjustBorders( this.getDisplayPoint(rect.height, rect.width, collSize, datapt.getValue(), index) );

                if (ptHigh == null || ptLow == null || ptAvg == null)
                    continue;

                ptHigh.x -= halfcol;
                ptLow.x  -= halfcol;
                ptAvg.x  -= halfcol;
                
                rectBar.x      = ptHigh.x;
                rectBar.y      = ptHigh.y;
                rectBar.height = (ptLow.y - ptHigh.y);
                
                // Draw Bar
                g.graphics.setColor(DEFAULT_COLUMN_COLOR);
                g.graphics.fillRect(rectBar.x, rectBar.y, rectBar.width, rectBar.height);
                
                // Draw the High, Low Average Lines
                g.graphics.setColor(this.HighLowColor);
                g.graphics.drawLine(ptAvg.x,  ptAvg.y,  ptAvg.x + rectBar.width - 1,  ptAvg.y);
                
                // Draw the dot in the middle
                g.graphics.drawOval(ptAvg.x + halfcol - 1, ptAvg.y - 1, 2, 2);
            }
        }
    }
    
    protected void calcRanges() {
        // We are going to set the peak and low values first
        DataPointCollection coll = this.getDataPoints();
        int                 collSize = coll.size();

        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            
            if(obj instanceof IHighLowDataPoint) {
                IHighLowDataPoint datapt = (IHighLowDataPoint)obj;
                if(Double.isNaN(datapt.getHighValue()) ||
                   Double.isNaN(datapt.getLowValue()))
                      continue;

                this.m_dPeakValue =
                    Math.max(this.m_dPeakValue, datapt.getHighValue());
                this.m_dLowValue =
                    Math.min(this.m_dLowValue, datapt.getLowValue());
            }
        }

        super.calcRanges();
    }
}
