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


package netQuant.stringconnection;

import java.awt.Component;

import netQuant.NetQuantDataConnection;
import netQuant.graph.Graph;


/**
 * implementer of NetQuantDataConnection
 * @author diego
 *
 */
public class Main implements NetQuantDataConnection{
    
    /** Creates a new instance of Main */
    public Main() {
    }

    /**
     * overload the definition of getgraph
     * @param nodes 
     * @param c 
     * @return 
     */
    public Graph getGraph(String nodes, Component c) {
        
          Graph graph = DataConnection.showDialog(nodes,c, null);
          return graph;
    }
    
    /**
     * overload the definition of getGraph
     */
    public Graph getGraph(){
        return getGraph(null,null);
    }
    
    
}
