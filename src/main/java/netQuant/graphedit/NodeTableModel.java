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
 * NodeTableModel.java
 *
 * Created on August 8, 2006, 12:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netQuant.graphedit;

import java.awt.Color;
import java.util.Iterator;

import netQuant.graph.Graph;
import netQuant.graph.Node;

/**
 *
 * @author hooper
 */
public class NodeTableModel extends EdgeTableModel{
    
    /** Creates a new instance of NodeTableModel */
    public NodeTableModel(Graph g) {
        
        this.setG(g);
        init();
        
    }
    
    
    public String[] columnNames={"Name","x",
    "y","Shape","Color"};
    
    public void init(){
        // rows and columns
        columnCount=5;
        rowCount=getG().getNodeSize();
        
        // set up the data array
        data=new Object[rowCount][columnCount];
        int count=0;
        // add the edges
        //Iterator<Edge> nodeI = g.edgesIterator();
        for (Iterator nodeI = getG().nodesIterator(); nodeI.hasNext();){
            Node n = (Node)nodeI.next();
            data[count][0]=n.getLabel();
            data[count][1]=new Double(n.getX());
            data[count][2]=new Double(n.getY());
            data[count][3]=new Integer(n.getShape());
            //JLabel j=new JLabel();
            //j.setBackground(n.getColor());
            data[count][4]=n.getColor();
            count++;
        }
    }
    
    /**
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     *
     *
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        String retValue;
        
        retValue = columnNames[column];
        return retValue;
    }
    
    
    
    /**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex  the column being queried
     * @return the Object.class
     */
    public Class getColumnClass(int columnIndex) {
        Class retValue;
        
        retValue = reference[columnIndex].getClass();
        //super.getColumnClass(columnIndex);
        //System.out.println(columnIndex+" - "+retValue);
        return retValue;
    }
    
    final Object[] reference={new String(),new Double(0.0), new Double(0.0),
    new Integer(1), new Color(0,0,0)};
    
    public Node[] getNodes(){
        Node[] n=new Node[getRowCount()];
        
        for (int i=0; i<getRowCount();i++){
            //e=new Edge(data[i][0],)
            //System.out.println(data[i][0]+" "+data[i][1]);
            n[i]= new Node((String)data[i][0],
                    (Double)data[i][1],
                    (Double)data[i][2],
                    (Integer)data[i][3],
                    (Color)data[i][4]);
            
        }
        return n;
        
    }
    
    public boolean isCellEditable(int row, int col) {
        if (col>0)
        return true;
        return false;
    }
    
}