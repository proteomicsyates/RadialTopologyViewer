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

import java.awt.Component;

import netQuant.graph.Graph;


/**
 * Interface for all generic data connections. 
 * If you want to write your own data connections, all you need to 
 * do is to implement the <CODE>getGraph()</CODE> method.
 * Useful????
 * @author hooper
 */
public interface NetQuantDataConnection {
    
    
    /**
     * This is all you need. Medusa will just ask the connection
     * for the resulting graph. No statuses or anything - it 
     * is up to you to show progress etc.
     * @return the graph 
     */
    public Graph getGraph();
  
    public Graph getGraph(String nodes, Component c);
}
