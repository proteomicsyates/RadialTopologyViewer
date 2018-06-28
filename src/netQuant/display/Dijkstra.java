package netQuant.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import netQuant.graph.Edge;
import netQuant.graph.Node;

public class Dijkstra {
	
	private List<Node> nodes;
	private List<Edge> edges;
	private Map<String, List<Edge>> edgs;
	private Set<Node> settledNodes;
	private Set<Node> unSettledNodes;
	private Map<Node,Node> predecessors;
	private Map<Node,Double> distance;
	private int source;
	private double [][] mat;
	
	public Dijkstra(List<Node> n, List<Edge> ed, int i, double [][] matrix) {
	
		
		edgs = new Hashtable<String, List<Edge>>();
		
		source = i;
		mat = matrix;
		
		for (Edge e : ed) {
			
			if (e.getType()==6) {

				String key = e.n1;
				
				if (edgs.containsKey(key)) {
					ArrayList<Edge> edg = (ArrayList<Edge>) edgs.get(key);
					edg.add(e);
					edgs.put(key, edg);
				} else {
					ArrayList<Edge> edg = new ArrayList<Edge>();
					edg.add(e);
					edgs.put(key, edg);
				}
				key = e.n2;
				if (edgs.containsKey(key)) {
					ArrayList<Edge> edg = (ArrayList<Edge>) edgs.get(key);
					edg.add(e);
					edgs.put(key, edg);
				} else {
					ArrayList<Edge> edg = new ArrayList<Edge>();
					edg.add(e);
					edgs.put(key, edg);
				}

			}
			
						
		}
		
		edges = ed;
		nodes = n;
		
	}

	public void run() {
		
		settledNodes = new HashSet<Node>();
		unSettledNodes = new HashSet<Node>();
		distance = new HashMap<Node, Double>();
		predecessors = new HashMap<Node, Node>();
		distance.put(nodes.get(source), 0.0);
		unSettledNodes.add(nodes.get(source));
		while (unSettledNodes.size()>0) {
			Node n = getMinimum(unSettledNodes);
			settledNodes.add(n);
			unSettledNodes.remove(n);
			findMinimalDistance(n);
			
		}
		
	//	createNodeList();
		insertIntoPathMatrix();

		
		
	}
	
	private void findMinimalDistance(Node n) {
		
		List<Node> adjacentNodes = getNeighbors(n);
		for (Node target : adjacentNodes) {
			
			double old_short_distance_target = getShortestDistance(target);
			double old_short_distance_source = getShortestDistance(n);
			double old_distances = getDistance(n, target);
			
			if (old_short_distance_target > old_short_distance_source + old_distances) {
				distance.put(target, old_short_distance_source + old_distances);
				predecessors.put(target, n);
				unSettledNodes.add(target);
			}
		}
		
	}
	
	static int stk11_count=0;
	static int stk11_count1=0;
	
	private double getDistance(Node n, Node target) {
	
		if (n.getLabel().equals("STK11") && target.getLabel().equals("STRADA")) {
			stk11_count++;
			System.out.println(stk11_count);
		}
		
		for (Edge edge : neighbor_edges) {
			if (edge.containsNode(n) && edge.containsNode(target)) {
				
				if (n.getLabel().equals("STK11") && target.getLabel().equals("STRADA")) {
					stk11_count1++;
					System.out.println(stk11_count1 + "\t" + edge.getConf() + "\t" + (1.0/edge.getConf()));
				}
				
				return (1.0/edge.getConf());
				
				//return 1;
			}
		}
		throw new RuntimeException("Should not happen");
	}
	
	private double getShortestDistance(Node n) {
		
		Double d = distance.get(n);
		if ( d == null ) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
		
	}

	List<Edge> neighbor_edges;
	
	private List<Node> getNeighbors(Node n) {
	
		List<Node> neighbors = new ArrayList<Node>();
		neighbor_edges = new ArrayList<Edge>();
		
		neighbor_edges = edgs.get(n.getLabel());
		
		for (Edge edge : neighbor_edges) {
			if (edge.containsNode(n) && !isSettled(edge.getComplement(n))) {
				neighbors.add(edge.getComplement(n));
			
			} 
		
		}
		return neighbors;
	}

	private boolean isSettled(Node node) {
		
		return settledNodes.contains(node);
				
	}

	private Node getMinimum(Set<Node> vertexes) {
		
		Node minimum = null;
		for (Node n : vertexes) {
			if (minimum == null) {
				minimum = n;
			} else {
				if (getShortestDistance(n) < getShortestDistance(minimum)) {
					minimum = n;
				}
			}
		}
				
		return minimum;
	}
	
	public LinkedList<Node> getPath(Node target) {
		
		LinkedList<Node> path = new LinkedList<Node>();
		Node step = target;
		if (predecessors.get(step) == null) {
			return null;
		} 
		path.add(step);
		while (predecessors.get(step) !=null) {
			step = predecessors.get(step);
			path.add(step);
		}
		Collections.reverse(path);
		return path;
		
	}
	
	List<String> node_names = new ArrayList<String>();
	
//	private void createNodeList() {
		
//		for (Node n : nodes) {
//			String name = n.getLabel();
//			node_names.add(name);
//		}
		
//		Collections.sort(node_names);
		
//	}
	
	
	private void insertIntoPathMatrix() {
			
		
	//	int source_index = node_names.indexOf(nodes.get(source).getLabel());
		
		for (int i=0; i < nodes.size(); i++ ) {
			
			Node n = nodes.get(i);
					
			double d=0;
			
			if (nodes.get(source).getLabel().equals("STK11") && n.getLabel().equals("STRADA")) {
				stk11_count++;
				System.out.println(stk11_count);
			}
					
			try {
				d = distance.get(n);
			} catch (java.lang.NullPointerException e) {
			//	System.out.println(source_index + "\t" + target_index);
				d = 0;//Integer.MAX_VALUE;
			}
						
			if (d!=0) mat[source][i] = 1.0/d;//1.0/Math.pow(d,2);
			else mat[source][i]=0;
			
		}
	}
	
		
}
