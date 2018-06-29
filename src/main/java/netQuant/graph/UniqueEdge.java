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
 * UniqueEdge.java
 *
 * Created on den 9 februari 2005, 23:19
 */

package netQuant.graph;

/**
 *
 * @author  unto
 */
public class UniqueEdge extends BasicEdge{
    public UniqueEdge(String n1, String n2){
	this.n1=n1;
	this.n2=n2;
    }
    public UniqueEdge(Edge e){
	this.n1=e.n1;
	this.n2=e.n2;
	}
    public boolean equals(Object o){
	//if (o instanceof UniqueEdge){
	    return sameName(o);
	    //	}
	    //return false;
    }
   
    public String report(){
	return super.report();
    }
    
}
