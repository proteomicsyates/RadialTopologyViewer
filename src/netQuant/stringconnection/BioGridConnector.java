package netQuant.stringconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import netQuant.graph.Edge;
import netQuant.graph.Graph;

public class BioGridConnector implements Runnable {

	private Thread me;
    private boolean done;
    private int progress;
    Graph g;
    
    public BioGridConnector (Graph graph) {
		g = graph;
		progress=0;
		done = false;
	}

    public BioGridConnector() {
    	progress=0;
		done = false;
    }

    public void setGraph(Graph graph) {
    	g = graph;
    }
    
	public int getProgress() {
		return progress;
	}
    
    public boolean isDone() {
		return done;
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
    
    
    
	@Override
	public void run() {
		
		Thread myThread=Thread.currentThread();
		done=false;
		
		if (me==myThread){
		    
		    getDataFromBioGrid();
			
		    done=true;
		}
		
	}
	
	private void getDataFromBioGrid() {
		
	//	Vector<Edge> edges = (Vector<Edge>) g.getEdges();
		
		List<Edge> edges = g.getEdges();
		Graph tmp = new Graph();
		
		
		for (int i=0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			
			String n1 = e.getFromName();
			String n2 = e.getToName();
			
			String urlStr= "http://webservice.thebiogrid.org/resources/interactions/?searchNames=true&geneList="+n1+"|"+n2+"&taxId=4932";
			
			try {
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				Vector <String> buffer = new Vector<String>();
				String line;
				while ((line=rd.readLine())!=null) {
					buffer.add(line);
				}
				rd.close();
				
				double orientation = getOrientation(n1,n2,buffer);
				
				e.setOrientation(orientation);
				edges.set(i, e);
				
				tmp.addEdge(e);
				
							
			} catch (MalformedURLException exp) {
				// TODO Auto-generated catch block
				exp.printStackTrace();
			} catch (IOException exp) {
				// TODO Auto-generated catch block
				exp.printStackTrace();
			}
			
			progress++;
		}

		g = tmp;
		
		done = true;
				
	}
    
	private double getOrientation(String n1, String n2, Vector<String> orient) {
		
		double orientation=1.0;
		
		boolean from = false;
		boolean to = false;
		
		for (int i=0 ; i<orient.size(); i++) {
			
			String [] tmp = orient.elementAt(i).split("\t");
			
			if (tmp[12].equals("physical")) {
				if (tmp[7].equals(n1) && tmp[8].equals(n2)) from=true;
				if (tmp[7].equals(n2) && tmp[8].equals(n1)) to=true;
			}
 			
		}
		
		if (from&&to) {
			orientation = 0;
		} else {
			if (from) {
				orientation = 1;
			} else {
				orientation = -1;
			}
		}
		
		return orientation;
		
	}
	
	public Graph getGraph() {
		return g;
	}
	
	
}
