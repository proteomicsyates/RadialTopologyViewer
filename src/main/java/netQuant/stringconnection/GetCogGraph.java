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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netQuant.graph.Edge;
import netQuant.graph.Graph;




/**
 * Gets a graph of COGS from STRING
 * 
 * www.string.embl.de is maintained by Christian von Mering (mering@embl.de)
 */
public class GetCogGraph implements Runnable {
    
    private Thread me;
    private boolean done;

    private String url;
    private int port;
    private String[] queries;
    private Graph g;
    private float cutoff;
    private String status="ok";

    /**
     * Talks to the STRING serverside socket
     * @param url 
     * @param port 
     * @param queries 
     * @param cutoff 
     */
    public GetCogGraph(String url, int port, String[] queries, float cutoff){
	this.url=url;
	this.port=port;
	this.queries=queries;
	this.cutoff=cutoff;
    }

    /**
     * Tells the other threads what <CODE>GetCogGraph</CODE> is doing at
     * the moment.
     * @return Status
     */
    public String getStatus(){
	return status;
    }

    /**
     * Returns the graph
     * @return the data from STRING
     */
    public Graph getGraph(){
	return g;
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
		getCogGraphFromString();
		//System.out.println("should be done getting graph");
	    }
	    catch (IOException ie){
		g=new Graph();
		status="Could not read from STRING";
	    }
	    catch (NumberFormatException ne){
		g=new Graph();
		status="No matches found";
	    }
	    
	    done=true;
	}
	//System.out.println("thread is d"+done);
    }
    
    int progress=0;
    public int getProgress(){
	return progress;
	
    }
    /**
     * Returns <CODE>true</CODE> if the data transfer is finished
     * @return <CODE>true</CODE> if done
     */
    public boolean isDone(){
	return done;
    }

      /* this does the same for cogs, bypassing the select thingy
     * and returns a graph */
    public void getCogGraphFromString() throws IOException, NumberFormatException{
	//String[] queries = nodeTextArea.getText().split("\\n");
    progress=0;
	//System.out.println(l+" queries");
	g = new Graph();
	Socket stringSocket = new Socket(url,port);
	//Socket stringSocket = new Socket("string.embl.de",4444);
	PrintWriter out = new PrintWriter(stringSocket.getOutputStream(),true);
	BufferedReader in = new BufferedReader
	    (new InputStreamReader
	     (stringSocket.getInputStream() ));
	String inLine;
//	String result[];
	StringBuffer sb = new StringBuffer();
	// build the query string
	for (int n=0; n<queries.length; n++){
	sb.append("COG:");
	    sb.append(queries[n]);
	    sb.append("\n");
	}
	sb.append("bye");
	//for (int n=0; n<queries.length; n++){
	out.println(sb.toString());
	//System.out.println("COG:"+queries[n]);
	//System.out.println(" wait for stream");
	while (!in.ready()) {}
	//System.out.println("stream ready");
	while( (inLine = in.readLine())!=null){
	    //result=inLine.split("\\t");
	    //for (int i=0; i<result.length; i++){
	    //System.out.println(inLine);
	    if (inLine.length()>5)
		parseCogEdge(g,inLine);
	}
	//}
		    
    
	//System.out.println("done");
	//out.print("bye");
	out.close();
	in.close();
	stringSocket.close();
	//g.rescaleNodes(600);
	//System.out.println(g.report());
	//return g;
    }
  
    private void parseCogEdge(Graph g, String inLine) throws NumberFormatException{
	String[] result = inLine.split("\\t+");
	float combined=Float.parseFloat(result[result.length-1])/1000f;
	
	if (combined<cutoff)
	    return;
	if (inLine.compareTo("Not found")==0)
	    return;
	//System.out.println(inLine+"\n"+result.length);
	Pattern genePattern=Pattern.compile("^(COG\\d+)");
	Matcher matcher;
	// get nodes
	String n1=result[0];
	String n2=result[1];
	matcher=genePattern.matcher(result[0]);
	if (matcher.find())
	    n1=matcher.group(1);
	matcher=genePattern.matcher(result[1]);
	if (matcher.find())
	    n2=matcher.group(1);
	float confidence;
	for (int i=2; i<result.length-1; i++){
	    //System.out.println(result[i]);
	    confidence=Float.parseFloat(result[i])/1000;
	    // is there an interaction?
	    if (confidence>0.){
		Edge e = new Edge(n1,n2,confidence,i-1);
		//System.out.println(e);
		g.addEdge(e);
		progress++;
		//System.out.println(g.report());
	    }
	}
    }
    


}
