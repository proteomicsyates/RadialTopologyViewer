package netQuant.stringconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import netQuant.graph.Edge;
import netQuant.graph.Graph;

public class StringConnector implements Runnable {

	private Thread me;
    private boolean done;
	private Vector<String> proteins;
	private int progress;
	Graph g;
	
	public StringConnector (Vector<String> v) {
		proteins = v;
		progress=0;
		done = false;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public int getSize() {
		return proteins.size();
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
		    
		    getDataFromString();
			
		    done=true;
		}
				
	}

	public void getDataFromString() {
		
		
		for (int i=0; i < proteins.size(); i++) {
			
			String urlStr= "http://string-db.org/api/tsv/interactors?identifier="+proteins.elementAt(i)+"&species=4932";
			
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
				
				buffer = resolveName(buffer);
				addEdge(buffer);
						
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			progress++;
			
		}
		done = true;
	
	}
	
	private Vector<String> resolveName(Vector<String> n ) {
		
		
		for (int i=1; i < n.size(); i++) {
			
			String [] node = n.elementAt(i).split("\\.");
			
			String urlStr= "http://string-db.org/api/tsv/resolve?identifier="+node[1]+"&species=4932";
			
			URL url;
			try {
				url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				String buffer=null;
				while ((line=rd.readLine())!=null) {
					
					buffer = line;
					
				}
				
				String [] data = buffer.split("\t");
				
				n.set(i, data[3]);
				
				rd.close();
				
								
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
			
		return n;
	}
	
	private void addEdge(Vector<String> nodes) {
		
		for (int i=1; i < nodes.size(); i++) {
			
			Edge e = new Edge(nodes.elementAt(1), nodes.elementAt(i), 1, 1);
			g.addEdge(e);
		}
		
		
	}
	
	public Graph getGraph() {
		return g;
	}

	public void setGraph(Graph webg) {
		g = webg;
		
	}
	
}

