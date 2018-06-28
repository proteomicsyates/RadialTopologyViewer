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

import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

/**
 * Create a demo network
 * @author diego
 *
 */
public class DemoTools{

	/** create a random graph
	 * 
	 * @param nNodes
	 * @param nEdges
	 * @return
	 */
    public Graph randomGraph(int nNodes, int nEdges){
	// based on a limited number of nodes
		Graph graph=new Graph();
		Edge temp;
		String n1;
		String n2;
		for (int i=0; i<nEdges;i++){
		    n1= randomNode(nNodes);
		    n2= randomNode(nNodes);
		    temp=new Edge(n1,n2,(float)Math.random(),randomInteraction());
		    graph.addEdge(temp);
		}
		setNodes(graph);
		return graph;
    }

    /**
     * set the parameters of a node
     * @param graph
     */
    private void setNodes(Graph graph){
		int shape=0;
		int red=255;
		int green=255;
		int blue=255;
		for (java.util.Iterator <Node> it = graph.nodesIterator(); it.hasNext();) {
	            Node node = (Node)it.next();
		    shape = (int)(Math.random()*4); //4 shapes possibles
		    red = (int)(Math.random()*255);
		    green = (int)(Math.random()*255);
		    blue = (int)(Math.random()*255);
		    node.setShape(shape);
		    node.setColor(new java.awt.Color(red,green,blue));
	            
		}
    }
    
    /** 
     * gives a random name to the node
     * @param nNodes
     * @return
     */
    private String randomNode(int nNodes){
		int index = (int)(Math.random()*nNodes);
		return "n"+index;
    }
    
    /**
     * creates a random connection
     * @return
     */
    private int randomInteraction(){
       int inter= (int)(Math.random()*6+1);
       return inter;
    }
    
    /**
     * manage the creation of a random demo network
     * @param args
     */
    public static void main(String[] args){
		DemoTools dt = new DemoTools();
		int nNodes=40;
		int nEdges=300;
		Graph g = dt.randomGraph(nNodes, nEdges);
		System.out.println(g.report());
	
    }

}
