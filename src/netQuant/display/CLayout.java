package netQuant.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;
import netQuant.stringconnection.ParallelConnections;

public class CLayout implements Runnable {

	private Graph g;
	private int width;
    private int height;
    private int area;
    private double temp;
    private Thread me;
    private boolean done=true;
    private double critXDistance;
    private double critYDistance;
    private double k;
    private int iterations=0;

    private double [][] pathMatrix;
    private double [][] corrMatrix;
	 
    private List<Node> nodes;
	private List<Edge> edges;
	
	public CLayout (Graph g,int width, int height){
		
		this.g=g;
		this.width = width;
		this.height = height;
		area = width*height;
		temp = width/10;
		critXDistance = width*3/4;
		critYDistance = height*3/4;
		
		pathMatrix = new double [this.g.getNodeSize()][this.g.getNodeSize()];
		corrMatrix = new double [this.g.getNodeSize()][this.g.getNodeSize()];
		
		for (int i=0; i < g.getNodeSize(); i++) {
			corrMatrix[i][i] = 1; 
		}
	
		edges = new ArrayList<Edge>(g.getEdges());
		nodes = new ArrayList<Node>();

		for (Iterator<Node> n = g.nodesIterator(); n.hasNext();) {
			nodes.add(n.next());
		}
		
		//System.out.println(width+" "+height);
		//System.out.println("optimal length: "+k);
	    
	}
	
	public void iterateAll(){
		
		done=false;
		
		calculatePathMatrix();
		
	//	for (int i=0; i<g.getNodeSize(); i++){
	//	    moveNodes(i);
	//	}
		for (Enumeration<String> e = protein_complexes.keys(); e.hasMoreElements() ;) {
			
			String root = e.nextElement();
			
			String [] tmp = protein_complexes.get(root).split("\t");
			double x = g.getNode(root).getX();
			double y = g.getNode(root).getY();
			
			for (int i=0; i < tmp.length; i++) {
				
				moveNodes(tmp[i],x,y);
				
			}
			
		}
		
		
		done=true;
	    
	}
	
	
	private void moveNodes(String name, double x, double y) {
		++iterations;
		
		double epsilon = 0.05;
		
		Random r = new Random();
		
		Node n = g.getNode(name);
		
		x = x + (r.nextDouble()*70*(r.nextInt(2)*2-1));
		y = y + (r.nextDouble()*70*(r.nextInt(2)*2-1));
		
		n.setX(x);
		n.setY(y);
		
		
	}

	
	/**
	 * search the network fo find the shortest path between each node
	 */
	
//	private List<Node> nodes;
//	private List<Edge> edges;
//	private Set<Node> settledNodes;
//	private Set<Node> unSettledNodes;
//	private Map<Node,Node> predecessors;
//	private Map<Node,Integer> distance;
	
	
	private void calculatePathMatrix() {
			
		// calculate path matrix
		ThreadPool tpool = new ThreadPool();
		tpool.getEnded();
	
		// calculate correlation matrix
		ThreadPool2 tpool2 = new ThreadPool2();
		tpool2.getEnded();
		
		findClusters();
		
	}

	ArrayList <ArrayList> cluster;
	
	Hashtable <String, String> protein_complexes = new Hashtable<String, String>();
	
	List<String> node_names;
	
	public void findClusters() {
		
		
		node_names = new ArrayList<String>();
		
		for (Iterator<Node> n = g.nodesIterator(); n.hasNext();) {
			String name = n.next().getLabel();
			node_names.add(name);
		}
		
		Collections.sort(node_names);
			
		double [] nearest = new double[3];
		nearest[2]=1;
		
		initializeMatrix();
		
		do  {
			nearest = findMax(); 
			mergeMatrix(nearest);
		} while( nearest[2]>=0.1);
		
				
	}
	
	private void initializeMatrix() {
		
		cluster = new ArrayList<ArrayList>();
		
		for (int i=0; i < corrMatrix.length; i++) {
			
			ArrayList <Double> tmp = new ArrayList<Double>();
			
			for (int j=0; j < corrMatrix[i].length; j++) {
				
				tmp.add(corrMatrix[i][j]);
			}
			
			cluster.add(tmp); 
			
		}
		
	}
	
	private void mergeMatrix(double [] nearest) {
		
		ArrayList <Double> tmp = cluster.get((int)nearest[0]);
		ArrayList <Double> tmp1 = cluster.get((int)nearest[1]);
		
		String name1 = node_names.get((int)nearest[0]);
		String name2 = node_names.get((int)nearest[1]);
		
		if (protein_complexes.containsKey(name1)) {
			
			String name;
			
			if (protein_complexes.containsKey(name1)) {
				name = protein_complexes.get(name1);
				name = name + "\t" + name2;
				if (protein_complexes.containsKey(name2)) {
					name = name + "\t" + protein_complexes.get(name2);
					protein_complexes.remove(name2);
				}
				protein_complexes.put(name1, name);
			} 
			
		} else {
			protein_complexes.put(name1, name2);
		}
		
		for (int i=0; i < tmp.size(); i++) {
				
			double value = (tmp.get(i) + tmp1.get(i))/2;
			
			tmp.set(i, value);
		}
		
		cluster.remove((int)nearest[1]);
		
		for (int i=0; i < cluster.size(); i++) {
			
			cluster.get(i).remove((int)nearest[1]);
			
		}
		
		node_names.remove((int)nearest[1]);
		
	}

	
	
	
	private double[] findMax() {
		
		double [] max = new double[3];
		max[2]=-100;
		
		for (int i=0; i < cluster.size(); i++) {
			
			ArrayList <Double> tmp = cluster.get(i);
						
			for (int j=0; j < tmp.size(); j++) { // skip ones!!!!
				
				if (i!=j) {
					double value = tmp.get(j);
					
					if (value > max[2]) {
						max[0] = i;
						max[1] = j;
						max[2] = value;
									
					}
				}
				
			}
		}
		
		if (max[0]>max[1]) {
			double tmp = max[0];
			max[0]=max[1];
			max[1]=tmp;
		}
		
		return max;
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
	
	private class ThreadPool2 {
		
		Runtime runtime = Runtime.getRuntime();
		
		ExecutorService execSvc = Executors.newFixedThreadPool(runtime.availableProcessors());
		
		public ThreadPool2() {
				
			for (int i=0; i < g.getNodeSize(); i++) {
				++iterations;
				for (int j=i+1; j < g.getNodeSize(); j++) {
					
					double [] e1 = pathMatrix[i];
					double [] e2 = pathMatrix[j];
					
					execSvc.execute(new ParallelPearson(e1 ,e2 ,corrMatrix,i,j));
				}
				
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
