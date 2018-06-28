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


package netQuant.example;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import netQuant.DemoTools;
import netQuant.NetQuantSettings;
import netQuant.display.BasicGraphPanel;
import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

/**
 * This is an example of how to extend the <CODE>BasicGraphPanel</CODE> 
 * to draw nodes and edges any way you want. Refer to the <CODE>Node</CODE> and 
 * <CODE>Edge</CODE> documentation to see which parameters you can use. In
 * this example, paintNode is extended and uses <CODE>Node.getColor()</CODE>
 * to create a button-like effect.
 * @author hooper
 */
public class ExamplePanel extends BasicGraphPanel{
    
    
    /**
     * Constructor
     * @param ms default settings
     */
    public ExamplePanel(NetQuantSettings ms) {
        super(ms);
    }
    
    /**
     * Overridden <CODE>paintEdge</CODE> method.
     * This version just draws a line. You can try paths, 
     * icons or anything you like.
     * @param g graphics2d context
     * @param e the current edge
     */
    public void paintEdge(Graphics2D g, Edge e) {
        
        g.setPaint(basicEdgeColor);
        
        paintPath(g,e);
    }
    
    private void paintPath(Graphics2D g2d, Edge e){
        // get the values
        Node from =graph.getNode(e.getFromName());
        Node to = graph.getNode(e.n2);
        int x1 = (int)from.getX();
        int y1 = (int)from.getY();
        int x2 = (int)to.getX();
        int y2 = (int)to.getY();
        
        
        
        // just draw the line
        g2d.drawLine(x1,y1,x2,y2);
        
        
    }
    /**
     * Overridden <CODE>paintNode</CODE> method. 
     * Paints two ellipses with a gradient based on the node
     * color. The button effect is created by using opposite gradients.
     * If you want the dragged node to be painted differently, override
     * <CODE>paintPick</CODE>. If you want the fixed nodes to be painted 
     * differently, check for <CODE>Node.isFixed</CODE>.
     * @param g Graphics2D
     * @param n Node n
     */
    public void paintNode(Graphics2D g, Node n) {
        paintShadedNode(g,n);
    }
    
    private void paintShadedNode(Graphics2D g, Node n){
        Color c=n.getColor();
        int nodeSize=16;
        
        int border=2;
        int x=(int) n.getX() - (int)(nodeSize/2);
        int y=(int) n.getY() - (int)(nodeSize/2);
        float fx=(float)x;
        float fy=(float)y;
        Point2D p1=new Point2D.Float(x,y);
        Point2D p2=new Point2D.Float(x+nodeSize,y+nodeSize);
        GradientPaint outside=
                new GradientPaint(p1,c,
                p2,Color.white);
        GradientPaint inside=
                new GradientPaint(p1,Color.white,
                p2,c);
        
        
        Shape outsideShape =
                new Ellipse2D.Double(x, y, nodeSize, nodeSize);
        Shape insideShape =
                new Ellipse2D.Double(x+border, y+border,
                nodeSize-2*border, nodeSize-2*border);
        g.setPaint(outside);
        
        g.fill(outsideShape);
        g.setPaint(inside);
        g.fill(insideShape);
        
        g.setPaint(Color.black);
        if (label)
            g.drawString(n.getLabel(),x-2,y-2);
    }
    
   
    
}
