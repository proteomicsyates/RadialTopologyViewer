// Medusa is a graph viewer which allows interactive editing of networks
// (edges and nodes) and also connects to databases.
//
// Copyright (C) 2006 Sean Hooper
//
// This program is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published by 
// the Free Software Foundation; either version 2 of the License, or (at 
// your option) any later version.
//
// This program is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along 
// with this program; if not, write to the 
// Free Software Foundation, Inc., 
// 59 Temple Place, Suite 330, 
// Boston, MA 02111-1307 USA

/*
 * SamplePanel.java
 *
 * Created on den 24 september 2006, 20:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netQuant.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import netQuant.NetQuantSettings;
import netQuant.graph.Edge;
import netQuant.graph.Node;

/**
 *
 * @author sean
 */
public class SamplePanel extends BasicGraphPanel{
    
    /**
     * Creates a new instance of SamplePanel
     */
    public SamplePanel(NetQuantSettings ms) {
        super(ms);
    }
    
    public void paintEdge(Graphics2D g, Edge e){   
        
        g.setPaint(basicEdgeColor);
        
        paintPathWithConfLabel(g,e);
        //super.paintEdge(g,e);
        
    }
    
    public void paintPathWithConfLabel(Graphics2D g2d, Edge e){
        // get the values
        Node from =graph.getNode(e.getFromName());
        Node to = graph.getNode(e.n2);
        int x1 = (int)from.getX();
        int y1 = (int)from.getY();
        int x2 = (int)to.getX();
        int y2 = (int)to.getY();
        
        // show conf as string
        float conf=e.getConf();
        String conf_string=Float.toString(conf);
        int lastIndex=conf_string.length();
        int last=Math.min(lastIndex,3);
        conf_string=conf_string.substring(0,last);
        
        // just draw the line
        g2d.drawLine(x1,y1,x2,y2);
        // on half points, add conf label
        // provide the dimensions in each direction,
        // i.e from halfx-width to halfx+width
        int width=11;
        int height=6;
        int halfx=(int)(x1+x2)/2;
        int halfy=(int)(y1+y2)/2;
        
        
        g2d.setColor(Color.YELLOW.brighter());
        g2d.fillRect(halfx-width,halfy-height,width*2,height*2);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(halfx-width,halfy-height,width*2,height*2);
        g2d.setColor(Color.BLACK);
        g2d.drawString(conf_string,halfx-width+1,halfy+height-1);
        
    }
    
//       public void paintNode(Graphics2D g, Node n){
//        
//        g.setPaint(n.getColor());
//        int x=(int) n.getX() - correct;
//        int y=(int) n.getY() - correct;
//        
//        Shape shape = medusa.display.PaintTools.getShape(n.getShape(),x,y,nodeSize);
//        g.fill(shape);
//        g.setPaint(Color.black);
//        if (n.isFixed())
//            g.setPaint(Color.yellow);
//        g.draw(shape);
//        g.setPaint(fontColor);
//        g.setFont(nodeFont);
//        if (label)
//            g.drawString(n.getLabel(),x-2,y-2);
//    }
}
