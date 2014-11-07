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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import org.hyperic.util.data.IDisplayDataPoint;
import org.hyperic.util.units.FormattedNumber;
import org.hyperic.util.units.UnitsFormat;

public class HorizontalChart extends Chart
{
    private Rectangle m_rect;
    
    protected HorizontalChart() {
        this.init();
    }

    protected HorizontalChart(int width, int height) {
        super(width, height);
        this.init();
    }
    
    protected HorizontalChart(int width, int height,int charts) {
        super(width, height, charts);
        init();
    }
    
    protected void init() {        
        this.showUnitLines = true;
    }

    protected Rectangle adjustRectangle(Graphics2D g, Rectangle rect) {
        int cDataPts = this.getDataPoints().size();
        int iSpread  = this.getUnitSpread(g, rect);
        rect.height  = (iSpread * (cDataPts - 1)) + (this.valueIndent * 2) + this.lineWidth;
                       
        this.m_rect = rect;
        return rect;
    }

    protected Rectangle getInteriorRectangle(ChartGraphics g) {
        return m_rect;
    }
    
    protected String[] getUnitLabels() {
        DataPointCollection coll   = this.getDataPoints();
        Iterator            iter   = coll.iterator();        
        String[]            result = new String[coll.size()];

        for(int i = 0;iter.hasNext() == true;i++)
            result[i] = ((IDisplayDataPoint)iter.next()).getLabel();
        
        return result;
    }

    protected String[] getXLabels() {
        if(this.m_adRangeMarks == null)
            return null;
            
        FormattedNumber[] fmtValueLabels =
            UnitsFormat.formatSame(m_adRangeMarks, m_fmtType, m_fmtScale);

        String[] result = new String[fmtValueLabels.length];
        
        for(int i = 0;i < fmtValueLabels.length;i ++)
            result[i] = fmtValueLabels[i].toString();    

        return result;
    }
    
    private int getUnitSpread(Graphics2D g, Rectangle rect) {
        int cDataPts = this.getDataPoints().size();
        int iSpread  = rect.height - (this.valueIndent * 2);
        
        return (cDataPts > 1) ? (iSpread / (cDataPts - 1)) : iSpread;
    }

    protected int getYLabelWidth(Graphics2D g) {
        int maxWidth = 0;
        
        String[] labels = this.getUnitLabels();
        
        for(int i = 0;i < labels.length;i ++) {
            int width = this.m_metricsLabel.stringWidth(labels[i]);
            
            if(width > maxWidth)
                maxWidth = width;
        }

        return maxWidth;
    }

    protected Rectangle draw(ChartGraphics g)
    {
        ///////////////////////////////
        // Paint the chart background

        Rectangle rect = super.draw(g);
        
        if(this.hasData() == false)
            return rect;

        ///////////////////////////////////////
        // Paint the chart exterior and lines
        
        // Calculate points
        double dScale = this.scale(rect.width);
        int    lineWidth = this.lineWidth;
        
        int x2 = rect.x + rect.width;
        int y2 = rect.y + rect.height;
        
        int yHorzMarks = rect.y + this.valueIndent;

        //////////////////////////////////////////////////////////
        // Draw the Value (Y) Legend
        
        if(this.showLeftLegend == true)
            g.drawYLegendString(this.getUnitLegend());
        
        //////////////////////////////////////////////////////////
        // Draw the unit (Y) axis cross lines and labels

        DataPointCollection coll = this.getDataPoints();
        
        int[]    lines  = new int[coll.size()];
        String[] labels = this.getUnitLabels();
        
        int spread = this.getUnitSpread(g.graphics, rect);
        
        for(int i = 0, y = rect.y + rect.height - this.valueIndent;
          i < coll.size();i++, y -= spread) 
            lines[i] = y;

        g.drawXLines(lines, labels, false);

        //////////////////////////////////////////////////////////
        // Draw the unit (X) axis tick marks and labels

        labels = this.getXLabels();
        lines  = new int[this.m_adRangeMarks.length];
        
        for(int i = 0;i < this.m_adRangeMarks.length;i ++) {
            lines[i]  = rect.x + (int)Math.round(
                             (this.m_adRangeMarks[i] - this.m_floor) * dScale);
        }
        
        g.drawYLines(lines, labels, true, xLabelsSkip);
        
        ////////////////////////////////////////////////////////////
        // Draw the Top & Bottom Legend

        g.drawXLegendString(this.getValueLegend());        

        ///////////////////////////////
        // Paint the chart interior
        
        if(this.showValues == true)
            paint(g, rect);
            
        return rect;
    }
           
    protected void paint(ChartGraphics g, Rectangle rect) {
    }

    protected Point getDataPoint(Rectangle rect, int datapoint) { 
        return this.getDataPoint(rect, datapoint, this.getDataPoints());
    }

    protected Point getDataPoint(Rectangle rect, int datapoint,
                                 DataPointCollection coll)
    {
        Point ptResult = super.getDataPoint(rect.width, rect.height, datapoint,
                                            coll);
        
        // Add & Flip the units
        if(ptResult != null)
            ptResult = new Point(rect.x + (rect.width - ptResult.y), rect.y + ptResult.x);

        return ptResult;            
    }

//    protected boolean hasXLegend() {
//        return this.showValueLegend;
//    }
//    
//    protected boolean hasYLegend() {
//        return this.showUnitLegend;
//    }
}

