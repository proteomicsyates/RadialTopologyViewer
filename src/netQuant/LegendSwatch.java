//NetQuant is a graph viewer which allows interactive editing of networks
//(edges and nodes) and connects to databases and provides quantitative analysis.
//NetQuant is based on Medusa developed by Sean Hopper <http://www.bork.embl.de/medusa>
//
//Copyright (C) 2011 Diego Calzolari
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but 
//WITHOUT ANY WARRANTY; without even the implied warranty of 
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
//Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package netQuant;

import java.awt.*;
import javax.swing.*;

/**
 * class to paint the icon
 * @author diego
 *
 */
class LegendSwatch implements Icon { 
    Color c;
   

    public LegendSwatch(Color c) { 
    	this.c=c;
    } 
    
    public int getIconWidth() { 
    	return 11; 
    } 
    
    public int getIconHeight() { 
    	return 11; 
    } 
    
    public void paintTriangle(Graphics2D g, int x, int y){

    	int[]xPoints={x,x+getIconWidth(),x};
		int[]yPoints={y, y+getIconHeight(), y+getIconHeight()};
		int nPoints=3;
		//g2d.setPaint(c);
		g.fillPolygon(xPoints,yPoints,nPoints);
    }

    public void paintIcon(Component comp, Graphics g, int x, int y) { 
	
    	Graphics2D g2d=(Graphics2D) g;
		g2d.setStroke(new BasicStroke(2));
		g2d.setPaint(c); 
		//g2d.drawLine(x,y,x+getIconWidth(),y+getIconHeight());
		paintTriangle(g2d,x,y);
	
    } 
} 
