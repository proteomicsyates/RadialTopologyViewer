package netQuant.display;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;

import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

public class cExtractor implements Runnable {

	private Graph g;
	private Thread me;
	private Vector<String> results;
	private double [][] pathMatrix;
	private boolean done=true;
	private List<Node> nodes;
	private List<Edge> edges;
	private int iterations=0;
	private String root;
	private int root_id;
	
	public cExtractor(Graph graph, String root) {
	
	//	this.root=root;
		g = graph;
		results = new Vector<String>();
		pathMatrix = new double [this.g.getNodeSize()][this.g.getNodeSize()];
		
		edges = new ArrayList<Edge>(g.getEdges());
		nodes = new ArrayList<Node>();

		for (Iterator<Node> n = g.nodesIterator(); n.hasNext();) {
			nodes.add(n.next());
		}
		
		for (int i=0; i < nodes.size(); i++) {
			if (nodes.get(i).getLabel().equals(root)) {
				root_id = i;
				break;
			}
		}
	}

	private void extract() {
		
		done=false;
		
		calculatePathMatrix();
		
		createResultList();
		
		done=true;
	}
	
	private void createResultList() {
		
		for (int i=0; i < g.getNodeSize(); i++) {
			
			String result = nodes.get(root_id).getLabel() + "\t" + nodes.get(i).getLabel() + "\t" + Math.sqrt(pathMatrix[root_id][i]);
			results.add(result);
		}
		
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnval = fc.showSaveDialog(null);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			report(file);
		}
		
		
	}

	private void report(File f) {
		
		try {
			Writer output = new BufferedWriter(new FileWriter(f));
			
			String outstring = "Node1\tNode2\tConfidence\n";
			
			output.write(outstring);
			
			for (int i=0; i < results.size(); i++) {
				outstring = results.elementAt(i) + "\n";
				output.write(outstring);
			}
									
			output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	private void calculatePathMatrix() {
		
	//	ThreadPool tpool = new ThreadPool();
	//	tpool.getEnded();
		Dijkstra pd = new Dijkstra(nodes,edges,root_id,pathMatrix);
		pd.run();
		
	}

	@Override
	public void run() {
		Thread myThread=Thread.currentThread();
		if (me==myThread){
		    extract();
		}
		
	}

	public int getCurrent() {
		return iterations;
	}

	public boolean isDone() {
		return done;
	}

	public void start() {
		if (me==null) {
		    me=new Thread(this);
	        }
		me.start();
		
	}

	public void stop() {
		if (me!=null) {
		    me=null;
		    
		}
		
	}

	private class ThreadPool {
		
		Runtime runtime = Runtime.getRuntime();
		
		ExecutorService execSvc = Executors.newFixedThreadPool(runtime.availableProcessors());
		
		public ThreadPool() {
				
			for (int i=0; i < g.getNodeSize(); i++) {
				++iterations;
				execSvc.execute(new ParallelDijkstra(nodes,edges,i,pathMatrix));
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
