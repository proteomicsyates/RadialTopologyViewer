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

package netQuant.display;

import java.util.Iterator;

import netQuant.graph.Graph;
import netQuant.graph.Node;
import netQuant.graph.UniqueEdge;

/*
 * An implementation of the Fruchterman Reingold
 * graph layout algorithm
 * Coded by Sean Hooper for STRING 2003
 */

/* This class will assign new positions for the nodes
 * depending on the energy constraints
 */

public class FRspring implements Runnable{
   
	private int width;
    private int height;
    private double critXDistance;
    private double critYDistance;

    private int area;
    private double k;
    private Graph g;
    
    
    
    private double temp;
    private Thread me;
    private boolean done=true;

    private static final double LIMIT=1e-10;
    

    public FRspring (int width, int height){
	
    	this.width = width;
    	this.height = height;
    	area = width*height;
    	temp = width/10;
    	critXDistance = width*3/4;
    	critYDistance = height*3/4;
	
    }

    public FRspring (Graph g,int width, int height){
	
	this.g=g;
	this.width = width;
	this.height = height;
	area = width*height;
	temp = width/10;
	critXDistance = width*3/4;
	critYDistance = height*3/4;
	setK();
	//System.out.println(width+" "+height);
	//System.out.println("optimal length: "+k);
    }

    

    private void setK(){
	k = 0.8*Math.sqrt(area / this.g.getNodeSize());
	//k=0.1;
    }
    //static final double MAX_STEP=0.05;

    private double[] attractDistance(Node aNode, Node bNode){
		double xDelta = aNode.getX() - bNode.getX();
		double yDelta = aNode.getY() - bNode.getY();
		//System.out.println("attract"+a.x+" "+b.x);
		double delta =  Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
		double result[] = new double[2];
		delta = Math.max(delta,LIMIT);
		double force = delta*delta/k;
		result[0] = xDelta / delta*force;
		result[1]= yDelta / delta*force;
		return result;
    }

    private double[] repelDistance(Node aNode, Node bNode){
	double xDelta = aNode.getX() - bNode.getX();
	double yDelta = aNode.getY() - bNode.getY();
	
		if (xDelta > critXDistance) // repel over the x border right
		    xDelta = aNode.getX() - bNode.getX() - width; 
		if (xDelta < -critXDistance) // repel over the x border left
		    xDelta = aNode.getX() - bNode.getX() + width; 

		if (yDelta > critYDistance)// repel over the y border down
		    yDelta = aNode.getY() - bNode.getY() - height;
		if (yDelta < -critYDistance)// repel over the y border up
		    yDelta = aNode.getY() - bNode.getY() + height;

	//System.out.println("repel "+a.x+" "+b.x);
	double delta =  Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
	delta = Math.max(delta, LIMIT);
	double result[] = new double[2];
	double force = k*k/delta;
	result[0] = xDelta / delta*force;
	result[1] = yDelta / delta*force;
	return result;
    }

    
    static int MAX_ITERATIONS=300;
    private int iterations=0;
    
    public void iterateAll(){
	done=false;
	for (int i=0; i<MAX_ITERATIONS; i++){
	    moveNodes();
	}
	done=true;
    }
    
    public void start(){
	if (me==null) {
	    me=new Thread(this);
        }
	me.start();
	//System.out.println("thread started");
    }
    
    public void stop(){
	if (me!=null) {
	    me=null;
	    
        }
    }

    public void run(){
	Thread myThread=Thread.currentThread();
	if (me==myThread){
	    //System.out.println("thread running");
	    iterateAll();
	}
    }

    public int getCurrent(){
	return iterations;
    }
    
    

    public boolean isDone(){
	return done;
    }

    // returns true if an iteration has been performed successfully
    public boolean iterate(){
	if (iterations<MAX_ITERATIONS){
	    moveNodes();
	    return true;
	}
	return false;
    }

    
    /*public Node[] getNodes(){
	return nodes;
    }*/
     
    public void moveNodes(){

	iterations++;
	Node node, node2;
        UniqueEdge edge;
	double displacement[] = new double[2];

	// find the displacement from the repulsion
	for(Iterator it = g.nodesIterator(); it.hasNext();) {
            node = (Node)it.next();
            node.setDX(0);
            node.setDY(0);
            for(Iterator it2 = g.nodesIterator(); it2.hasNext();) {
                node2 = (Node)it2.next();
                if(node.equals(node2)) {
                    continue;
                }
                displacement = repelDistance(node, node2);
                node.setDX(node.getDX() + displacement[0]);
		node.setDY(node.getDY() + displacement[1]);
            }
        }
        
	// find the displacement from edge attraction
	for(Iterator it = g.uniqueEdgesIterator();it.hasNext();) {
            edge = (UniqueEdge)it.next();
            node = g.getNode(edge.getFromName());
            node2 = g.getNode(edge.getToName());
            displacement=attractDistance(node,node2);
            node.setDX(node.getDX() - displacement[0]);
	    node.setDY(node.getDY() - displacement[1]);
	    node2.setDX(node2.getDX() + displacement[0]);
	    node2.setDY(node2.getDY() + displacement[1]);
        }
	
	// update the motion, for unfixed nodes?
        for(Iterator it = g.nodesIterator(); it.hasNext();) {
            node = (Node)it.next();
            if(!node.isFixed()) {
                node.setX(node.getX() + Math.max(-temp, Math.min(node.getDX(),temp)));
                node.setY(node.getY() + Math.max(-temp, Math.min(node.getDY(), temp)));
                
                if (node.getX() > width) {
                    node.setX(width - borderSize - 10*Math.random());
                }
                if (node.getX() < borderSize) {
                    node.setX(borderSize + 10*Math.random()); 
                }
                if (node.getY() > height) {
                    node.setY(height - borderSize - 10*Math.random());
                }
                if (node.getY() < (borderSize+10)) {
                    // remember that the label also needs space
                    node.setY(2*borderSize + 10*Math.random());
                }
            }
        }
	// decrease the temperature
	//System.out.println("temp: "+temp);
	cool();
    }

    int borderSize=20;
    
    // for setting the border size
    public void setBorderSize(int borderSize){
	this.borderSize=borderSize;
    }
    
    private void cool(){
	temp*=1-(double)iterations/(double)MAX_ITERATIONS;
    }


    public static void main (String [] args){
	FRspring frSpring = new FRspring(400,400);
	
	frSpring.setK();
	for (int i=0; i<18;i++){
	    System.out.println("move "+i);
	    frSpring.moveNodes();
	}
    }

}
