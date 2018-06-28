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
 

package netQuant.graph;

import java.io.Serializable;

/**
 * Basic representation of an Edge as a connection between two 
 * nodes. Note that a node will only exist if there is an edge between
 * them. This Graph representation is not based on nodes but edges.
 * @author unto
 */
public abstract class BasicEdge implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 5614120500831721511L;
	/**
     * Node 1
     */
    public String n1;
    /**
     * Node 2
     */
    public String n2;
    
    /**
     * Name of the first node
     * @return String name
     */
    public String getFromName(){
	return n1;
    }
    /**
     * Name of the second node
     * @return String name
     */
    public String getToName(){
	return n2;
    }
    /**
     * Quick report of the two nodes
     * @return String
     */
    public String report(){
	return n1+"\t"+n2;
    }
    /**
     * Check if two edges have the same nodes
     * @param o <CODE>Object</CODE> representation of <CODE>BasicEdge</CODE>
     * @return <CODE>True</CODE> if same nodes occur in the two edges
     */
    public boolean sameName(Object o){
	
	BasicEdge be = (BasicEdge) o;	
	    
	int c12 = n1.compareTo(be.n2);
	int c21 = n2.compareTo(be.n1);
	int c11 = n1.compareTo(be.n1);
	int c22 = n2.compareTo(be.n2);
	
	if ((c12==0)&(c21==0))
	    return true;
	if ((c11==0)&(c22==0))
	    return true;
	return false;
    }
    
    /**
     * Checks if <CODE>Node</CODE> <I>n</I> is in the edge
     * @param lbl label of node
     * @return True if edge contains the label
     */
    public boolean contains(String lbl){
	return ((n1.compareTo(lbl)==0) |
		(n2.compareTo(lbl)==0) );
    }

    /**
     * Check if edge if opposite to another edge, i.e if
     * edge <I>a</I> contains nodes (<I>n1</I>,<I>n2</I>) and edge <I>b</I> 
     * contains (<I>n2</I>,<I>n1</I>)/
     * @param o <CODE>BasicEdge</CODE> cast as <CODE>Object</CODE>
     * @return <CODE>True</CODE> if oppposites
     */
    public boolean oppositeName(Object o){
	BasicEdge be = (BasicEdge) o;	
	    
	int c12 = n1.compareTo(be.n2);
	int c21 = n2.compareTo(be.n1);
		
	if ((c12==0)&(c21==0))
	    return true;
	return false;
    }
    /**
     * Node 1
     * @return 
     */
    public String[] getNodes(){
	String[] result = new String[2];
	result[0]=n1;
	result[1]=n2;
	return result;
    }
}
