package netQuant.stringconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import netQuant.graph.Edge;
import netQuant.graph.Graph;

public class ParallelConnections implements Runnable {

	private String urlStr;
	private String pname;
	private Vector<String> edges;
	private ArrayList<String> bait;
	private Vector<String> prot;
	
	public ParallelConnections(int i, Vector<String> s, ArrayList<String> b, Vector<String> e) {
		
		pname = s.elementAt(i);
		bait = b;
		urlStr= "http://string-db.org/api/tsv-no-header/interactorsList?identifiers="+s.elementAt(i)+"&species=9606";
		edges = e;
		prot=s;
	}
	
	
	@Override
	public void run() {
		
		URL url=null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			System.err.println("error in url");
			e.printStackTrace();
		}
		URLConnection conn=null;
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			System.err.println("error in url connection");
			e.printStackTrace();
		}
				
		BufferedReader rd=null;
		try {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e) {
			System.err.println("error in url streaming");
			e.printStackTrace();
		}
		Vector <String> buffer = new Vector<String>();
		String line;
		try {
			while ((line=rd.readLine())!=null) {
						
				buffer.add(line);
			}
		} catch (IOException e) {
			System.err.println("erron in reading line");
			e.printStackTrace();
		}
		try {
			rd.close();
		} catch (IOException e) {
			System.err.println("erron in reader closing");
			e.printStackTrace();
		}
		addEdge(buffer);
			
		
	}
	
		
		
	private void addEdge(Vector<String> nodes) {
		
		nodes.remove(0); // remove the link with itself
		
		for (int i=0; i < nodes.size(); i++) {
			
			if (prot.contains(nodes.elementAt(i))) {
				String edg = pname + "\t" + nodes.elementAt(i);
				edges.add(edg);
			} else {
				nodes.remove(i);
				i--;
			}
		}
				
		if (nodes.size()==0) {
			for (int i=0; i < bait.size(); i++) {
				String edg = bait.get(i) + "\t" + pname;
				edges.add(edg);
			}
		}
				
	}
	

}
