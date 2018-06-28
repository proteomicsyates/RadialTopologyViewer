package netQuant.stringconnection;

import java.awt.Cursor;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import netQuant.graph.Edge;
import netQuant.graph.Graph;

public class ConnectorManagerLite extends JPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5367553372215530358L;
	Vector<String> proteins = new Vector<String>();
	Vector<String> protid = new Vector<String>();
	ArrayList<String> baits = new ArrayList<String>();
	ArrayList<String> baitid = new ArrayList<String>();
	Vector<String> edges = new Vector<String>();
	Hashtable<String,String> namelut = new Hashtable<String,String>();
	
	JFileChooser fc;
	Graph g;
	
	JProgressBar progressbar;
	JPanel mainpanel;
	Timer time1;
	boolean done_prot, done_string, done_biogrid;
	
	public ConnectorManagerLite(File file) {
	
//		g = new Graph();
//		g.setNodeShape(4);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
		FileInputStream fis;
		BufferedInputStream bis;
		BufferedReader dis;
		
		// put the request in a single string
		// check the dimension of the network
		
		try {
			fis =new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new BufferedReader(new InputStreamReader(bis));
				
			String data;
			while ((data = dis.readLine())!=null) {
				data = data.split("-")[0];
				proteins.add(data);
				
			}
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setBaits(File f) {
		
		FileInputStream fis;
		BufferedInputStream bis;
		BufferedReader dis;
			
		try {
			fis =new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			dis = new BufferedReader(new InputStreamReader(bis));
				
			String data;
			while ((data = dis.readLine())!=null) {
				baits.add(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private ArrayList<String> getBaits() {
		return baits;
	}
	
	private void createlut() {
		
		for (int i=0; i < proteins.size(); i++) {
			
			String urlStr= "http://string-db.org/api/tsv-no-header/resolve?identifier="+proteins.get(i)+"&species=9606";
			URL url;
			try {
				url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				
				while ((line=rd.readLine())!=null) {
					
					String [] data = line.split("\t");
					
					namelut.put(data[0], data[3]);
					
					protid.add(data[0]);
					
					if (data[3].equals(baits.get(0))) {
						baitid.add(data[0]);
					}
					
				}
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
	}
	
	private void resolveNames() {
		
		for (int i=0; i < edges.size(); i++) {
			
			String [] names = edges.get(i).split("\t");
			
			if (namelut.containsKey(names[0]) && namelut.containsKey(names[1])) {
				
				names[0] = namelut.get(names[0]);
				names[1] = namelut.get(names[1]);
				
				String newvalue = names[0] + "\t" + names[1];
				
				edges.set(i, newvalue);
				
			} else {
				edges.remove(i);
				i--;
			}
			
		}
		
	}
	
	public void run() {
		
		createlut();
		
		ThreadPool tpool = new ThreadPool();
		tpool.getEnded();
	
		resolveNames();
		
		System.out.println("*edges");
		
		for (int i=0; i < edges.size(); i++) {
			
			System.out.println(edges.get(i));
			
		}
		
		System.out.println("*nodes");
		
		Random rand = new Random();
		
		for (int i=0; i < proteins.size(); i++) {
			
			String node = proteins.get(i) + "\t" + rand.nextDouble() + "\t" + rand.nextDouble() + "\tc " + rand.nextInt(255) + "," + rand.nextInt(255) +  "," + rand.nextInt(255) + "\t" + proteins.get(i);
			System.out.println(node);
		}
		
		
	}
	
	private Vector<String> resolveName(Vector<String> n ) {
		
		
		for (int i=1; i < n.size(); i++) {
			
			String [] node = n.elementAt(i).split("\\.");
			
			//String urlStr= "http://string-db.org/api/tsv/resolve?identifier="+node[1]+"&species=4932";
			String urlStr= "http://string-db.org/api/tsv/resolve?identifier="+node[0]+"&species=9606";
			
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
	
	private class ThreadPool {
		
		ExecutorService execSvc = Executors.newFixedThreadPool(10);
		
		public ThreadPool() {
				
			for (int i=0; i < proteins.size(); i++) {
				execSvc.execute(new ParallelConnections(i,protid,baitid,edges));
				
			}
			
			execSvc.shutdown();
			
		}
		
		public boolean getEnded() {
			
			boolean ended = false;
			
			try {
				ended =  execSvc.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ended;
			
		}
		
	}
	
}


