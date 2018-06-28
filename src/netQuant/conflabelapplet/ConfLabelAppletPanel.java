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
 * ConfLabelAppletPanel.java
 *
 * Created on September 25, 2006, 10:35 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netQuant.conflabelapplet;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import netQuant.NetQuantSettings;
import netQuant.applet.ClickLinker;
import netQuant.display.SamplePanel;
import netQuant.graph.Graph;

/**
 *
 * @author hooper
 */
public class ConfLabelAppletPanel extends SamplePanel{
    
    ClickLinker clickLinker;
    //MedusaSettings stringSettings;
    
    public ConfLabelAppletPanel(NetQuantSettings stringSettings, ConfLabelApplet parent, String linkStart, String linkEnd){
        super(stringSettings);
        graph = new Graph();
  
        clickLinker = new ClickLinker(linkStart,linkEnd, parent);
        setBackground(Color.white);
        setOpaque(true);
        
        setEdgeLen(100); 
        //setPreferredSize(new Dimension(panelWidth,panelHeight));
        addMouseListener(this);
        //this.stringSettings=stringSettings;
        //start();
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(1000000);
        setToolTipText("");
        
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame();
        
        ConfLabelAppletPanel p = new ConfLabelAppletPanel(new NetQuantSettings(),
                null,null,null);
        Graph g=new Graph();
        g.defaultGraph();
        p.setGraph(g);
        f.getContentPane().add(p);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
