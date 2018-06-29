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

package netQuant.graph;


import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class <code>Graph</code>:
 * The Graph object is basically a collection of edges with
 * some utilities
 * @author <a href="mailto:hooper@embl.de">Sean Hooper</a>
 * @version 1.0
 */
public class Graph extends BasicGraph implements Serializable,Cloneable {
    

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1363767010807074469L;
	
	public void addGraph(Graph g){
        
        for (Iterator <Edge>i = g.edgesIterator(); i.hasNext(); ){
            Edge e = (Edge) i.next();
            //edges.add(e);
            addEdge(e);
        }
    }
    
    public void addGraph(BasicGraph g){
        addGraph((Graph) g);
        
    }
    
    public Object clone() throws CloneNotSupportedException {
        Graph g = new Graph();
//        Graph) super.clone();
//        g.setEdges((ArrayList)edges.clone());
//        g.setUniqueEdges((ArrayList)uniqueEdges.clone());
//        g.setNodes((HashMap)nodes.clone());
        g.addGraph(this);
        g.copyNodeSettings(this);
        return g;
    }
    
    
    
    /*public Graph(Graph g){
      edges=g.getEdgeAL();
      uniqueEdges=g.getUniqueEdges();
     */
    
    
    // add an internal edge, i.e, add edge only if
    // both nodes are already connected
    /*public void addInternalEdge(Edge e){
      if (edges.contains(e))
      return;
      Object n1 = nodes.get(new Node(e.n1));
      Object n2 = nodes.get(new Node(e.n2));
      if ( (n1!=null) & (n2!=null) )
      addEdge(e);
      }*/
    
    
    /* Copy the color settings (and other stuff eventually)
     * from another graph
     */
    
    
    
    
    
//    public void renameNode(String oldName,String newName){
//        Node n = (Node) nodes.remove(oldName);
//        if (n!=null){
//            n.setLabel(newName);
//            nodes.put(newName,n);
//        }
//
//    }
    
    
    /**
     *
     * @param lbl
     * @return
     */
    public Iterator<Edge> edgesIterator(String lbl){
        ArrayList<Edge> temp = new ArrayList<Edge>();
        for (Iterator<Edge> i = edgesIterator();i.hasNext();){
            Edge e = (Edge) i.next();
            if (e.contains(lbl))
                temp.add(e);
        }
        return temp.iterator();
    }
    
    
    
    /**
     * Returns an ArrayList of the fixed nodes
     * @return
     */
    public ArrayList<Node> getFixed(){
        ArrayList<Node> fixedNodes=new ArrayList<Node>();
        for (Iterator<Node> i = nodesIterator();i.hasNext();){
            Node n = (Node) i.next();
            //edges.add(e);
            if (n.isFixed()){
                fixedNodes.add(n);
                //System.out.println("added "+n.getLabel()+" to fixed list");
            }
        }
        return fixedNodes;
    }
    

    public void removeFixed(){
        ArrayList<Node>  a = getFixed();
        
        for (Iterator<Node> i = a.iterator();i.hasNext();){
            Node n = (Node) i.next();
            removeEdgeByLabel(n.getLabel());
        }
    }
    
    
//    public Node[] nodeArray(){
//        Collection c = getNodes().values();
//        return (Node[])c.toArray(new Node[0]);
//    }
    
    public void rescaleNodes(int scale){
        for (Iterator<Node> i = nodesIterator();i.hasNext();){
            Node n = i.next();
            n.rescale(scale);
        }
    }
    
    public void rescaleNodes(int xScale, int yScale){
        for (Iterator<Node> i = nodesIterator();i.hasNext();){
            Node n = i.next();
            n.rescale(xScale, yScale);
        }
    }
    
    public HashMap<String, Integer> nodesMap(){
        // we need indexes for nodes for the adjacency matrix
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        int count=0;
        for (Iterator i = nodesIterator();i.hasNext();count++){
            Node n = (Node) i.next();
            hm.put(n.getLabel(),new Integer(count));
            
        }
        return hm;
    }
    
    
    /**
     * Divides node positions by a double value
     * Useful for batch operations after using FRSpring
     */
    public void divideNodePosition(double d){
        double max=0.0;
        for (Iterator i = nodesIterator();i.hasNext();){
            Node n = (Node) i.next();
            n.setX(n.getX()/d);
            n.setY(n.getY()/d);
            
        }
        
    }
    
    private Edge[] edgeArray(){
        return (Edge[]) getEdges().toArray(new Edge[0]);
    }
    
    /*public UniqueEdge[] uniqueEdgeArray(){
      return (UniqueEdge[]) uniqueEdges.toArray(new UniqueEdge[0]);
      }*/
    
    /*public Graph clone(){
      Graph g = new Graph();
      g=this;
      return g;
      }*/
    
    public void subtractGraph(Graph g){
        for (Iterator <Edge> i = g.edgesIterator();i.hasNext();){
            Edge e = (Edge) i.next();
            
            removeEdge(e);
        }
    }
    


    
    protected List<Edge> cloneEdges(){
        List<Edge> list=new ArrayList<Edge>();
        for (Iterator<Edge> i=edgesIterator();i.hasNext();){
            Edge e= i.next();
            list.add(new Edge(e));
        }
        return list;
    }
    
    public void cropEdges(double confidenceCutoff){
        // cut out all edges which fail to reach the cutoff
        // copy array - never modify the structure you are iterating over
        List <Edge>list = cloneEdges();
        for(Iterator <Edge>i = list.iterator(); i.hasNext();){
            Edge temp = (Edge) i.next();
            if (temp.getConf() < confidenceCutoff)
                removeEdge(temp);
        }
    }
    public void removeEdgeByLabel(String lbl){
        List list = cloneEdges();
        for(Iterator i = list.iterator(); i.hasNext();){
            Edge temp = (Edge) i.next();
            if (temp.contains(lbl)){
                //System.out.println("found "+lbl);
                removeEdge(temp);
            }
        }
    }
    

    
    public void tracePath(ArrayList unresolved, Node n){
        Edge e;
        while ((e=getAdjacent(unresolved,n))!=null ){
            unresolved.remove(e);
            Node n2 = e.getComplement(n.getLabel());
            tracePath(unresolved,n2);
            System.out.print(n2.getLabel()+"-");
        }
    }
    
// get the next edge that contains the node
    public Edge getAdjacent(ArrayList unresolved,Node node){
        for(Iterator i = unresolved.iterator();i.hasNext();){
            Edge e = (Edge)i.next();
            Node n = e.getComplement(node.getLabel());
            if (n!=null)
                return e;
        }
        return null;
    }
    
    public String reportUnique(){
        StringBuffer sb=new StringBuffer();
        for(Iterator i = uniqueEdgesIterator();i.hasNext();){
            UniqueEdge e = (UniqueEdge)i.next();
            sb.append("\n");
            sb.append(e.getFromName());
            sb.append("\t");
            sb.append(e.getToName());
        }
        return sb.toString();
    }
    
    public String report(){
        StringBuffer sb=new StringBuffer();
        if (getComment()!=null){
            sb.append(getComment());
            sb.append("\n");
        }
        
        sb.append("*edges");
        for(Iterator i = edgesIterator();i.hasNext();){
            Edge e = (Edge)i.next();
            sb.append("\n");
            sb.append(e.report());
            
        }
        
        sb.append("\n*nodes\n");
        for(Iterator i = nodesIterator();i.hasNext();){
            Node n = (Node)i.next();
            sb.append(n.report());
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    
    
    public void defaultGraph(){
        Edge e1=new Edge("n1","n2",4);
        Edge e2=new Edge("n2","n3",5);
        Edge e3=new Edge("n1","n2",5);
        Edge e4=new Edge("n1","n4",1);
        Edge e5=new Edge("n2","n3",1);
        addEdge(e1);
        addEdge(e2);
        addEdge(e3);
        addEdge(e4);
        addEdge(e5);
    }
    
    /*  // scans for multiple edges and fixes orientation
        public void fixOrientation(Edge e1){
     
        for(Iterator i = edges.iterator();i.hasNext();){
        Edge e2 = (Edge)i.next();
        if (e1.sameName(e2)==true)
        System.out.println(e2);
     
        }
        }*/
    
    public void setNodeColor(java.awt.Color color){
        for(Iterator<Node> i = nodesIterator();i.hasNext();){
            Node n = i.next();
            n.setColor(color);
            
        }
    }
    
    public void setNodeShape(int shape){
        for(Iterator<Node> i = nodesIterator();i.hasNext();){
            Node n = i.next();
            n.setShape(shape);
            
        }
    }
    
    
//    public void setNodeAnnotation(StringBuffer annotation){
//        String[] result=annotation.toString().split("\n");
//        String[] parts;
//        Node n;
//        for (int i=0; i<result.length;i++){
//            parts=result[i].split("\t");
//            n = (Node) nodes.get(parts[0]);
//            if (n!=null){
//                n.setAnnotation(parts[1]);
//                //System.out.println(n.getLabel()+" set to "+n.getAnnotation());
//            }
//        }
//    }
    
    public void setNodeAnnotation(String name, String annotation){
        Node n = getNode(name);
        if (n!=null){
            n.setAnnotation(annotation);
            //System.out.println(n.getLabel()+" set to "+n.getAnnotation());
        }
    }
    
    public void printNodeAnnotation(){
        for(Iterator i = nodesIterator();i.hasNext();){
            Node n = (Node)i.next();
            System.out.println(n.getAnnotation() );
        }
    }
    
// return string list of nodes
    public StringBuffer getNodeList(){
        StringBuffer sb = new StringBuffer();
        for(Iterator i = nodesIterator();i.hasNext();){
            Node n = (Node)i.next();
            //sb.append("node:");
            sb.append(n.getLabel());
            sb.append("\n");
        }
        return sb;
    }
    
    
// get the subgraph that contains the node
    public Graph subGraph(Node node){
        Graph sub = new Graph();
        Edge e;
        Node n;
        
        for(Iterator i = edgesIterator();i.hasNext();){
            e = (Edge) i.next();
            if (e.containsNode(node)){
                sub.addEdge(e);
                n=e.getComplement(node);
                //setNode(n);
            }
        }
        return sub;
    }
    
    public void rescaleConfidence(){
        Edge e;
        float max=0.0f;
        for(Iterator i = edgesIterator();i.hasNext();){
            e = (Edge) i.next();
            if(e.getConf()>max)
                max=e.getConf();
        }
        for(Iterator i = edgesIterator();i.hasNext();){
            e = (Edge) i.next();
            e.setConf(e.getConf()/max);
            
        }
    }
    /**
     * Rescales nodes to 0 to 1.0, assuming they are given in
     * values 0 to 100 %.
     */
    public void rescaleNodePercentage(){
        Edge e;
        float max=100.0f;
        
        for(Iterator i = edgesIterator();i.hasNext();){
            e = (Edge) i.next();
            e.setConf(e.getConf()/max);
            
        }
    }
    /*
    // get number of edges from this node
    // since this is a sparse graph, this
    // is not very efficient. In an adjacency
    // matrix, it would be much faster
    public int numberOfEdges(Node node){
        UniqueEdge e;
        Node n;
        int neighbours=0;
        for(Iterator i = edgesIterator();i.hasNext();){
            e = (UniqueEdge) i.next();
            if (e.containsNode(node)){
                neighbours++;
            }
        }
        return neighbours;
    }
     */
    
    final int MAX_PARALLEL=20;
// improved but slower orientation algorithm
    public void autoFixOrientation(){
        
        Edge[] edgeArray=edgeArray();
        int nedges = edgeArray.length;
        // a list of edges already sorted out
        boolean[] edgeDone=new boolean[nedges];
        for (int i=0; i<nedges; i++)
            edgeDone[i]=false;
        
        // location of multiple edges
        int[] edgeList=new int[MAX_PARALLEL];
        for (int i=0; i<MAX_PARALLEL; i++)
            edgeList[i]=0;
        double alpha=1.; // the bezier attack point
        double startAlpha; // initial alpha point
        int matches=0;
        // go through the edges and look for parallel edges
        for (int i=0;i<nedges;i++){
            // skip if edge already done
            //System.out.println("Checking edge "+i+" : "+edges[i]);
            if (edgeDone[i]){
                //System.out.println("edge "+i+" done: "+edges[i]);
                continue;
            }
            matches=0;
            edgeList[0]=i;
            for (int j=0; j<nedges;j++){
                if (i==j)
                    continue;
                // check for a match
                if (edgeArray[i].sameName(edgeArray[j])){
                    // put the edge number in the list
                    //System.out.println("edge "+i+" matches "+j);
                    //System.out.println(edges[i]+" =  "+edges[j]);
                    matches++;
                    edgeList[matches]=j;
                    edgeDone[j]=true;
                }
            }
            // no match? then do nothing
            if (matches==0){
                edgeArray[i].setOrientation(0.);
                continue;
            }
            // match=1 means 2 edges... adjust for this
            
            // start with the case where there is an even number of
            // edges. NOTE that 3 matches = 4 edges!
            //System.out.println("   Found "+matches+" matches");
            if (matches%2==1)
                startAlpha=matches*-alpha/2;
            
            else // uneven number of edges
                startAlpha=matches*-alpha/2;
            
            //System.out.println("Setting start alpha to "+startAlpha);
            for (int k=0; k<=matches; k++){
                edgeArray[edgeList[k]].setOrientation
                        (isOppositeEdge(edgeArray[edgeList[k]],
                        edgeArray[i])*(startAlpha+k*alpha));
                //System.out.println("orientation set to "+edges[edgeList[k]].orientation);
            }
        }
        
    }
// return a modifier of -1 if edges are opposite
    public double isOppositeEdge(Edge e1, Edge e2){
        if (e1.oppositeName(e2))
            return -1.;
        return 1.;
    }
    
    
// adjust the node x and y according to the new panel sizes
    /**
     *
     * @param scale
     */
    public void rescaleNodes(double scale){
        //double xr=(double) x / (double) defaultSize;
        //double yr=(double) y / (double) defaultSize;
        //double reScale=scale/oldScale;
        for(Iterator<Node> it = nodesIterator(); it.hasNext();) {
            Node node = it.next();
            //node.setX(node.getX() * xr);
            //node.setY(node.getY() * yr);
            node.setX(node.getX() * scale);
            node.setY(node.getY() * scale);
        }
    }
    
    /**
     * Returns a string buffer with all the connection numbers of nodes
     * @return report
     */
    public StringBuffer nodesConnectivityReport(){
        StringBuffer sb=new StringBuffer();
        for (Iterator<Node> i=nodesIterator();i.hasNext();){
            Node n = i.next();
            sb.append(n.getLabel()).append("\t");
            sb.append(n.getConnections());
            sb.append("\n");
        }
        return sb;
    }
    
    public StringBuffer generalStats(){
        double connectionDegree = connectionDegree();
        // the minimum number of edges is equal to the number of nodes
        StringBuffer sb = new StringBuffer();
        sb.append("Connection degree: "+connectionDegree());
        
        return sb;
    }
    
    private double connectionDegree() {
        int totalUniqueEdges=getUniqueEdgeSize();
        int totalNodes=getNodeSize();
        // calculate the maximum possible number of edges
        // 1 3 6 10 15
        // 2 3 4  5 6
        // the total increases with n-1
        int maximum=0;
        for (int i=1; i<totalNodes; i++){
            maximum+=i-1;
        }
        double connectionDegree=(double) totalUniqueEdges/ (double) maximum;
        return connectionDegree;
    }
    
    
    /**
     * Assigns colors to nodes according to a map of node labels and colors.
     * This will be used in for instance clustering
     * @param map Map<String,Color>
     */
    public void assignColorByMap(Map<String,Color> map){
        
        //node map <string, integer>
        // colormap <integer, color>
        for (Iterator<String> i=map.keySet().iterator(); i.hasNext();){
            String label=i.next();
            getNode(label).setColor(map.get(label));
        }
    }
    
}
