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
package netQuant.applet;


import java.awt.Font;
import javax.swing.ProgressMonitor;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Shape;
import java.awt.Dimension;
import javax.swing.ToolTipManager;

import netQuant.*;
import netQuant.dataio.DataLoader;
import netQuant.display.BasicGraphPanel;
import netQuant.display.FRspring;
import netQuant.display.PaintTools;
import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;
import netQuant.graph.UniqueEdge;

import java.awt.event.ActionEvent;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.util.Iterator;
import java.awt.event.MouseEvent;


// This panel handles all data and drawing
/**
 * Lighter version of the <CODE>BasicGraphPanel</CODE> for use in web services
 */
public class NetQuantAppletPanel extends BasicGraphPanel
        {
    
    // the attributes holder class
   // MedusaSettings stringSettings;
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7763503460585808005L;

	// the parent applet
    NetQuantLite parent;
    
    // ClickLinker
    private ClickLinker clickLinker;
    
    // the progress bar for use in spring
//    ProgressMonitor energyBar;
//    ActionListener updateEnergyBar;
//    javax.swing.Timer timer;
    

    
    
    /* The main paint method. Paints edges and nodes
     */
//    
//    public void paintNet(Graphics2D g2d){
//        
//        for (Iterator edges = graph.edgesIterator(); edges.hasNext() ;) {
//            Edge e = (Edge) edges.next();
//            paintEdge(g2d, e);
//        }
//        //make sure confidence is restored
//        g2d.setComposite(makeComposite(1.0f));
//        for(Iterator it = graph.nodesIterator(); it.hasNext();) {
//            Node node = (Node)it.next();
//            paintNode(g2d, node);
//        }
//    }
    
    
    public void setBackgroundColor(Color c){
        setBackground(c);
    }
    
    
   
    
    
//    public void paintNode(Graphics2D g, Node n){
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
    

    
    
    private Color basicEdgeColor=java.awt.Color.gray;
    private Color fontColor = java.awt.Color.black;
    
    public void setBasicEdgeColor(Color color){
        basicEdgeColor=color;
    }
    public void setFontColor(Color color){
        fontColor=color;
    }
    public Color getFontColor(){
        return fontColor;
    }
    
    
    
    
//    public void paintEdge(Graphics2D g, Edge e){
//        //if (!e.visible)
//        //    return;
//        Node from =graph.getNode(e.getFromName());
//        Node to = graph.getNode(e.n2);
//        int x1 = (int)from.getX();
//        int y1 = (int)from.getY();
//        int x2 = (int)to.getX();
//        int y2 = (int)to.getY();
//        
//        // handle confidence
//        if (showConfidence)
//            g.setComposite(makeComposite(e.getConf()));
//        g.setPaint(basicEdgeColor);
//        //if (e.visible)
//        //g.setPaint(Color.red);
//        if (pretty){
//            g.setPaint(getColor(new Integer(e.getType()) ));
//            //System.out.println(e.getOrientation());
//            
//            PaintTools.paintPath(g, x1, y1,
//                    x2, y2, e.getOrientation(), 0.3, arrow);
//        } else{
//            
//            g.drawLine(x1,y1,x2,y2);
//        }
//    }

    
    
    /*
     * Constructor
     */
    public NetQuantAppletPanel(NetQuantSettings stringSettings, NetQuantLite parent,
            String linkStart, String linkEnd){
        stop();
        graph = new Graph();
        this.parent=parent;
        clickLinker = new ClickLinker(linkStart,linkEnd, parent);
        setBackground(Color.white);
        setOpaque(true);
        
        setEdgeLen(100); 
        //setPreferredSize(new Dimension(panelWidth,panelHeight));
        //addMouseListener(this);
        setStringSettings(stringSettings);
        //start();
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(1000000);
        setToolTipText("");
        
    }
    
    public NetQuantAppletPanel(NetQuantSettings stringSettings, NetQuantLite parent){
        this(stringSettings, parent,null, null);
    }
    
    /*
     * Constructor without a reference to the parent
     */
    public NetQuantAppletPanel(){
        setPreferredSize(new Dimension(600,600));
        setOpaque(true);
        //addMouseListener(this);
        setStringSettings(new NetQuantSettings());
        
        //start();
    }
    
//    public void setTimeFrameXY(Dimension d){
//        dims=d;
//        setPanelWidth(d.width);
//        setPanelHeight(d.height);
//        setPreferredSize(dims);
//        
//    }
    
    // thread is not running. start it here
    public void start(){
        if (relaxThread==null)
            relaxThread=new Thread(this);
        relaxThread.start();
    }
    
    // kill it
    public void stop(){
        if (relaxThread!=null)
            relaxThread=null;
    }

    public void handlePressLeftButton(MouseEvent e) {
        //System.out.println(e.getButton()+" "+e.getModifiersEx()+" "+e.getModifiersExText(e.getModifiers())+
          //      " "+e.getMouseModifiersText(e.getModifiers()));
        if ((e.isShiftDown() & (clickLinker.isActive())) ){
           // if we are clickLinking, check if we have a node nearby
            Node n=getClosest(e);
            if (n!=null){
                clickLinker.linkOut(n.getLabel());
                
            }
            e.consume();
            return;
        }
        super.handlePressLeftButton(e);
    }
  
      
   
}
