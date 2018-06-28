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
 * EdgeTableModel.java
 *
 * Created on August 7, 2006, 7:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netQuant.graphedit;

import java.util.Iterator;
import javax.swing.table.AbstractTableModel;

import netQuant.graph.Edge;
import netQuant.graph.Graph;

/**
 *
 * @author hooper
 */
public class EdgeTableModel extends AbstractTableModel{
    
    private Graph g;
    protected Object[][] data;
    public String[] columnNames={"Node 1","Node 2",
    "Type","Orient.","Confidence","Mark"};
    protected int columnCount;
    protected int rowCount;
    /** Creates a new instance of EdgeTableModel */
    public EdgeTableModel(Graph g) {
        
        this.setG(g);
        
        init();
    }
    public EdgeTableModel(){
        
    }
    
    public void init(){
        // rows and columns
        columnCount=6;
        rowCount=getG().getEdgeSize();
        
        // set up the data array
        data=new Object[rowCount][columnCount];
        int count=0;
        // add the edges
        //Iterator<Edge> edgeI = g.edgesIterator();
        for (Iterator edgeI = getG().edgesIterator(); edgeI.hasNext();){
            Edge e = (Edge) edgeI.next();
            data[count][0]=e.getFromName();
            data[count][1]=e.getToName();
            data[count][2]=new Integer(e.getType());
            data[count][3]=new Double(e.getOrientation());
            data[count][4]=new Float(e.getConf());
            data[count][5]=new Boolean(false);
            count++;
        }
    }
    
    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex	the row whose value is to be queried
     * @param columnIndex 	the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        return data[rowIndex][columnIndex];
    }
    
    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return columnCount;
    }
    
    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return rowCount;
    }
    
    /**
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     *
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        String retValue;
        
        retValue = columnNames[column];
        return retValue;
    }
    
    public boolean isCellEditable(int row, int col) {
        return (col>1);
    }
    
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
    public void removeEdges(){
        Edge e;
        Boolean marked;
        for (int i=0; i<getRowCount();i++){
            //e=new Edge(data[i][0],)
            
            marked=(Boolean)data[i][5];
            if (marked.booleanValue()){
                System.out.println("removing "+data[i][0]+" "+data[i][1]);
                e= new Edge((String)data[i][0],
                        (String)data[i][1],
                        (Integer)data[i][2],
                        
                        (Double)data[i][3],
                        (Float)data[i][4]);
                g.removeEdge(e);
            }
        }
        init();
        
    }
    
    public Graph getG() {
        return g;
    }
    
    public void setG(Graph g) {
        
        this.g = g;
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
    
    final Object[] reference={new String(),new String(), new Integer(0),
    new Double(0.0), new Float(1f), new Boolean(false)};
    
    public Graph getGraph(){
        Graph g= new Graph();
        Edge e;
        for (int i=0; i<getRowCount();i++){
            //e=new Edge(data[i][0],)
            //System.out.println(data[i][0]+" "+data[i][1]);
            e= new Edge((String)data[i][0],
                    (String)data[i][1],
                    (Integer)data[i][2],
                    (Double)data[i][3],
                    (Float)data[i][4]);
            g.addEdge(e);
        }
        return g;
    }
    
}



