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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

import netQuant.graph.Graph;
import netQuant.graph.Node;




/**
 * Annotates graphs retrieved by stringconnection
 */
public class Annotator implements Runnable {
    
    private Thread me;
    private boolean done;

    private String url;
    private int port;
    private Graph selection;
    private StringBuffer sb;
    private float cutoff;

    public Annotator (String url, int port, Graph selection){
	this.url=url;
	this.port=port;
	this.selection=selection;
//	this.cutoff=cutoff;
    }

    public int getMax(){
	return selection.getNodeSize();
    }

    public StringBuffer getAnnotation(){
	return sb;
    }

    public void start(){
	if (me==null) {
	    me = new Thread(this);
	}
	me.start();
    }
    
    public void stop(){
	if (me!=null) {
	    me=null;
        }
    }

    public void run(){
	Thread myThread=Thread.currentThread();
	done=false;
	if (me==myThread){
	    //System.out.println("thread running");
	    try {
		getAnnotationFromString();
		//System.out.println("should be done getting graph");
	    }
	    catch (IOException ie){
		sb=new StringBuffer();
	    }
	    done=true;
	}
	//System.out.println("thread is d"+done);
    }
    
    int progress=0;
    
    public int getProgress(){
    	return progress;
	}
    
    public boolean isDone(){
    	return done;
    }

    // get annotation from string
    public void getAnnotationFromString() throws IOException{
	//System.out.println("annotating ");
	sb=new StringBuffer();
	Socket stringSocket = new Socket(url,port);
	//System.out.println("socket opened ");
	PrintWriter out = new PrintWriter(stringSocket.getOutputStream(),true);
	BufferedReader in = new BufferedReader
	    (new InputStreamReader
	     (stringSocket.getInputStream() ));
	String inLine;
		
	// prepare the transfer
	StringBuffer transferSB=new StringBuffer();
        Node node;
	for (Iterator <Node>i = selection.nodesIterator();  i.hasNext() ;) {
	    node =(Node) i.next();
	    transferSB.append("NODE:");
	    transferSB.append(node.getLabel());
	    transferSB.append("\n");
	}
	transferSB.append("bye");

	out.println(transferSB);
	//System.out.println("sending to thread: "+transferSB);
	while (!in.ready()) {}
	String[] parts;
	while( (inLine=in.readLine())!=null){
	    //System.out.println(inLine);
	    if (inLine.length()>10){
		
		parts=inLine.split("\t");
		//sb.append(inLine);
		//System.out.println(parts[0]+" "+parts[1]);
		node = (Node) selection.getNode(parts[0]); 
		if (node!=null){
		    node.setAnnotation(parts[1]);
		    
		}
	    }
	    progress++;
	}
	
	out.close();
	in.close();
	stringSocket.close();
    }
    

}
