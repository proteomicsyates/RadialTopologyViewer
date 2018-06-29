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


import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import netQuant.graph.Edge;
import netQuant.graph.Graph;



public class GetGraph implements Runnable {
    
    private Thread me;
    private boolean done;

    private String url;
    private int port;
    private ArrayList <String>selection;
    private Graph g;
    private float cutoff;

    public GetGraph(String url, int port, ArrayList<String> selection, float cutoff){
	this.url=url;
	this.port=port;
	this.selection=selection;
	this.cutoff=cutoff;
    }


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
		getGraphFromString();
		//System.out.println("should be done getting graph");
	    }
	    catch (IOException ie){
		g=new Graph();
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

    public void getGraphFromString() throws IOException{
	// for each selection in the selectionPanel, go to string and
	// retrieve the network
		progress = 0;
		g= new Graph();
		Socket stringSocket = new Socket(url,port);
		
		PrintWriter out = new PrintWriter(stringSocket.getOutputStream(),true);
		BufferedReader in = new BufferedReader
		    (new InputStreamReader
		     (stringSocket.getInputStream() ));
		String inLine;
		StringBuffer temp = new StringBuffer();
		String send;
		
		for (Iterator <String>select = selection.iterator(); select.hasNext();){
		    // add edge to graph
		    temp.append("GI:");
		    send=(String)select.next();
		    temp.append(send);
		    temp.append("\n");
		    //System.out.println(send+": edge sent");
		}
		temp.append("bye");
		out.println(temp.toString());
		// ---------------------------------------------------------++++++++++++++++++++++++++++++++++++++++++++++++++
		// debugging!
		//System.out.println("wait for stream");
		while (!in.ready()) {}
		//System.out.println(" stream ready");
		while( (inLine=in.readLine())!=null){
		    //System.out.println("line: "+inLine);
		    if (inLine.length()>10){
			progress++;
			parseEdge(g,inLine);
			//System.out.println(inLine);
		    }
		    //System.out.println(progress);
		}
		//System.out.println("closing everything");
		out.close();
		in.close();
		stringSocket.close();
		//System.out.println(progress);
    }
    
    //-----------------------------------------------------------------------------
    /* this is where a line from the socketListener is
     * converted into edges
     Table "public.precomputed_gene_links"
     Column             |         Type          | Modifiers
     --------------------------------+-----------------------+-----------
     gene_id_a                      | character varying(40) | not null
     gene_id_b                      | character varying(40) | not null
     equiv_nscore                   | integer               |
     equiv_nscore_transferred       | integer               |
     equiv_fscore                   | integer               |
     equiv_pscore                   | integer               |
     equiv_hscore                   | integer               |
     array_score                    | integer               |
     array_score_transferred        | integer               |
     experimental_score             | integer               |
     experimental_score_transferred | integer               |
     database_score                 | integer               |
     database_score_transferred     | integer               |
     textmining_score               | integer               |
     textmining_score_transferred   | integer               |
     combined_score                 | integer               |

     final String linkQuery1="SELECT gene_id_a, gene_id_b, equiv_nscore, equiv_fscore, "
     +"equiv_pscore, equiv_hscore, array_score, experimental_score,"
     +" database_score, textmining_score, combined_score "
     +"FROM precomputed_gene_links WHERE gene_id_a = '";
    */
    private void parseEdge(Graph g, String inLine){

    	String[] result = inLine.split("\\t+");
		// check if combined score allows edge 
		float combined=Float.parseFloat(result[result.length-1])/1000f;
		//System.out.println(combined);
		if (combined<cutoff)
		    return;
	
		if (inLine.compareTo("Not found")==0)
		    return;
		//System.out.println(inLine+"\n"+result.length);
		Pattern genePattern=Pattern.compile("^\\d+\\.([\\w\\d]+)\\.*");
		//Pattern genePattern=Pattern.compile("^(\\w+)");
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
		    confidence=Float.parseFloat(result[i])/1000;
		    // is there an interaction?
		    if (confidence>0.){
			Edge e = new Edge(n1,n2,confidence,i-1);
			//System.out.println(e);
			g.addEdge(e);
			//System.out.println(g.report());
		    }
		}
    }

}
