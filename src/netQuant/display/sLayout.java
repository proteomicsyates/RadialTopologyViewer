package netQuant.display;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

public class sLayout implements Runnable {
	
	private Graph g;
	private int width;
    private int height;
    private Thread me;
    private int iterations=0;
    private boolean done=true;
    

	public sLayout(Graph g,int width, int height) {
		
		this.g = g;
		this.width = width;
		this.height = height;
		
		
	}
	
	public void start(){
		
		if (me==null) {
		    me=new Thread(this);
	        }
		me.start();
		
	    
	}
	    
	public void stop(){
		if (me!=null) {
		    me=null;
		    
		}
	    
	}
	
	private Node getBait() {
		
		Node b = null;
		int max=-1;
		
		for (Iterator<Node> nodes=g.nodesIterator(); nodes.hasNext(); ) {
			Node tmp = nodes.next();
			if (tmp.getConnections()>max) {
				max=tmp.getConnections();
				b = tmp;
			}
		}
		
		return b;
	}
	
	NodesMatrix nm;
	Hashtable<String, NodeHolder> nholder;
	double xradius;
	double yradius;
	
	private void calculatePositions() {
		
		Random r = new Random();
		
		done = false;
		
		ArrayList<Edge> edge = (ArrayList<Edge>) g.getEdges();
		
		Node bait = getBait();
		
		bait.setXY(width/2, height/2);
		
		xradius = width * 0.3;
		yradius = height * 0.3;
		
		nm = new NodesMatrix();
		nholder = new Hashtable<String,NodeHolder>();
	//	int [][] nodesmatrix = new int[g.getNodeSize()][g.getNodeSize()];
		Hashtable<String, Integer> nodestable = getNodesTable();
		
		for (int i=0; i < edge.size(); i++) {
			
			++iterations;
			
			if (edge.get(i).getType()==10) {
				
				Node from = edge.get(i).getComplement(edge.get(i).getToName());
				Node to = edge.get(i).getComplement(from);
				float conf = edge.get(i).getConf();
				
				if (!from.equals(bait)) {
					
					double x,y;
					
					double angle = Math.toRadians(r.nextInt(360));
					
					x = width/2 + xradius * Math.cos(angle)*(1-Math.pow(conf,2));
					y = height/2 + yradius *Math.sin(angle)*(1-Math.pow(conf,2));
					
					from.setX(x);
					from.setY(y);
					
					NodeHolder nh = new NodeHolder(from.getLabel(), angle, conf);
					nholder.put(from.getLabel(), nh);
					
					g.getNode(from.getLabel()).setXY(x, y);
					g.getNode(from.getLabel()).setAnnotation(to.getLabel() + " " + conf);
					
				} else {
					
					double x,y;
					
					double angle = Math.toRadians(r.nextInt(360));
					
					x = width/2 + xradius * Math.cos(angle)*(1-Math.pow(conf,2));
					y = height/2 + yradius *Math.sin(angle)*(1-Math.pow(conf,2));
					
					to.setX(x);
					to.setY(y);
					
					NodeHolder nh = new NodeHolder(to.getLabel(), angle, conf);
					nholder.put(to.getLabel(), nh);
					
					g.getNode(to.getLabel()).setXY(x, y);
					g.getNode(to.getLabel()).setAnnotation(to.getLabel() + " " + conf);
					
				}
				
				
			} else {
				
				Node from = edge.get(i).getComplement(edge.get(i).getToName());
				Node to = edge.get(i).getComplement(from);
				
				// maybe add score?
				
				nm.addValue(nodestable.get(from.getLabel()),nodestable.get(to.getLabel()), edge.get(i).getConf());
				nm.addValue(nodestable.get(to.getLabel()),nodestable.get(from.getLabel()), edge.get(i).getConf());
				
			}
		}
		
		moveNodes2(bait.getLabel());
		
		done = true;
		
	}
	
	private Hashtable<String,Integer> getNodesTable() {
		
		Hashtable<String, Integer> table = new Hashtable<String,Integer>();
		
		String [] nlist = g.getNodeList().toString().split("\n");
		
		for (int i=0; i < nlist.length; i++) {
			table.put(nlist[i], i);
		}
		
		
		return table;
	}
	
	Hashtable<String,String> nodeslinked;
	
	private void moveNodes2(String nbait) {
		
		Random r = new Random();
		
		nodeslinked = new Hashtable<String, String>();
		
		String [] nlist = g.getNodeList().toString().split("\n");
		
		nm.findMax();
		
		do {
						
			String s1 = nlist[nm.getMax_x()];
			String s2 = nlist[nm.getMax_y()];
			
			if (s1.equals(nbait) || s2.equals(nbait)) {
				
				nm.setToZero();
				
			} else {
				
				Node n1 = g.getNode(s1);
				Node n2 = g.getNode(s2);
				
				if (nm.getConn(nm.getMax_x())>nm.getConn(nm.getMax_y())) {
			//	if (n1.getConnections()>n2.getConnections()) {
					
					movingNode(n1, n2);
		//			movingConnectedNodes(n2, s2, s1);
					nm.cancelOldMax(0);
		
				} else {
					
					movingNode(n2, n1);
		//			movingConnectedNodes(n1, s1, s2);
					nm.cancelOldMax(1);
						
				}
							
			}
			
			nm.findMax();
			
		} while (nm.getConnections()>0.2);
				
	}

	/**
	 * 
	 * @param to
	 * @param from
	 */
	private void movingNode(Node to, Node from) {
		
		Random r = new Random();
		
		double newangle = nholder.get(to.getLabel()).getAngle() + (2*((r.nextInt(2)-0.5))*r.nextDouble()/10.0);
		double newx,newy;
		float conf = nholder.get(from.getLabel()).getConf();
		
		newx = width/2 + xradius * Math.cos(newangle)*(1-Math.pow(conf,2));
		newy = height/2 + yradius *Math.sin(newangle)*(1-Math.pow(conf,2));
		
		g.getNode(from.getLabel()).setXY(newx, newy);
		
		NodeHolder nh = new NodeHolder(from.getLabel(), newangle, conf);
		nholder.put(from.getLabel(), nh);
		
		String toattach;
		// need to add to queque of new node and move the previously attached nodes
		if (nodeslinked.containsKey(from.getLabel())) {
			String attached = nodeslinked.get(from.getLabel());
			
			String [] nlist = attached.split("\t");
			
			for (int i=0; i < nlist.length; i++) {
				movingNode(from, g.getNode(nlist[i]));
			}
			
			attached = from.getLabel() + "\t" + attached;
			toattach = attached;
			
		} else {
			toattach = from.getLabel();
		}
		
		if (nodeslinked.containsKey(to.getLabel())) {
			String alreadyattached = nodeslinked.get(to.getLabel());
			toattach = alreadyattached + "\t" + toattach;
		}
		
		nodeslinked.put(to.getLabel(), toattach);
				
	//	System.out.println("moved node " + from.getLabel() + " towards " + to.getLabel());
		
	}
		
	public void run() {
		Thread myThread=Thread.currentThread();
		if (me==myThread){
			calculatePositions();
		}
	}
	
	
	
	public int getCurrent(){
		
		return iterations;
	    
	}
	    
	    
	public boolean isDone(){
		
		return done;
	    
	}
	
	private class NodesMatrix {
		
		double [][] nodesmatrix = new double[g.getNodeSize()][g.getNodeSize()];
		int xmax;
		int ymax;
		double connections;
		
		public void addValue(int x, int y, double val) {
			nodesmatrix[x][y]+=val;
		}
		
		public int getMax_x() {
			return xmax;
		}
		
		public int getMax_y() {
			return ymax;
		}
		
		public void findMax() {
					
			double max=-1.0;
			
			for (int i=0; i < nodesmatrix.length; i++) {
				for (int j=i+1; j < nodesmatrix[i].length; j++) {
					if (nodesmatrix[i][j]>max) {
						xmax = i;
						ymax = j;
						max = nodesmatrix[i][j];
					}
				}
			}
			
			connections = max;
			
		}
		
		public double getConnections() {
			return connections;
		}
		
		public double getConn(int id) {
			
			double con=0;
			
			for (int i=0; i < nodesmatrix[id].length; i++) {
				con += nodesmatrix[id][i];
			}
			
			return con;
		}
		
		/**
		 * 
		 * @param mode 0 from n2 to n1, 1 from n1 to n2
		 */
		public void cancelOldMax(int mode) {
		
			setToZero();
			
			// problem depending from order in which the nodes are moved!!!!
			
			if (mode==1) {
				for (int i=0; i < nodesmatrix[xmax].length; i++) {
					nodesmatrix[i][ymax] += nodesmatrix[i][xmax];
					nodesmatrix[ymax][i] = nodesmatrix[i][ymax];
					
					nodesmatrix[xmax][i]=0;
					nodesmatrix[i][xmax]=0;
				}
			} else {
				for (int i=0; i < nodesmatrix[xmax].length; i++) {
					nodesmatrix[i][xmax] += nodesmatrix[i][ymax];
					nodesmatrix[xmax][i] = nodesmatrix[i][xmax];
					
					nodesmatrix[ymax][i]=0;
					nodesmatrix[i][ymax]=0;
				}
			}
					
		}
		
		public void setToZero() {
			nodesmatrix[xmax][ymax]=0;
			nodesmatrix[ymax][xmax]=0;
		
		}
		
	}
	
	private class NodeHolder {
		
		String name;
		double angle;
		float confidence;
		
		public NodeHolder(String n, double d, float f) {
			name = n;
			angle = d;
			confidence = f;
		}
		
		public double getAngle() {
			return angle;
		}
		
		public String getName() {
			return name;
		}
		
		public float getConf() {
			return confidence;
		}
	}
	
}
