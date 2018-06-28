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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class BasicGraph {
    
    // we will mostly add or remove to edges?
    private List<Edge> edges = new ArrayList<Edge>();
    private List<UniqueEdge> uniqueEdges = new ArrayList<UniqueEdge>();
    // nodes, on the other hand, need fast searching
    private Map<String, Node> nodes = new HashMap<String, Node>();
    // comment lines
    private String comment;
    /**
     * Sets the value of comment
     *
     * @param argComment Value to assign to this.comment
     */
    public final void setComment(final String argComment) {
        this.comment = argComment;
    }
    
    
    /** Creates a new instance of BasicGraph */
    public BasicGraph() {
    }
    
    
    //String representative;
    
    
    /**
     * Add <CODE>Edge</CODE> to <CODE>Graph</CODE>
     * @param e <CODE>Edge</CODE> to add
     */
    public void addEdge(Edge e){
        if ((!getEdges().contains(e)) & (e!=null)){
            //System.out.println("debug: adding edge "+e.report());
            getEdges().add(e);
            addNode(e.getFromName());
            addNode(e.getToName());
            // add unique edge, if it doesnt already exist
            
            if (e.getType()!=10) {
               	UniqueEdge ue = new UniqueEdge(e);
            //System.out.println("debug: adding unique edge "+ue.report()+" "+ue.getClass());
            	if (!uniqueEdges.contains(ue))
            		uniqueEdges.add(ue);
            }
        }
    }
    
    
    //    public double[][] getSimilarityMatrix(){
    //
    //
    //    }
    
    /**
     * Adds an edge by creating it from two <CODE>Nodes</CODE>.
     * @param n1
     * @param n2
     * @param conf
     * @param type
     * @see <CODE>Node</CODE>
     */
    public void addEdge(Node n1, Node n2, float conf, int type){
        Edge e = new Edge(n1.getLabel(), n2.getLabel(), conf, type);
        if ((!getEdges().contains(e)) & (e!=null)){
            //System.out.println("debug: adding edge "+e.report());
            getEdges().add(e);
            addNode(n1);
            addNode(n2);
            // add unique edge, if it doesnt already exist
            UniqueEdge ue = new UniqueEdge(e);
            //System.out.println("debug: adding unique edge "+ue.report()+" "+ue.getClass());
            if (!uniqueEdges.contains(ue))
                uniqueEdges.add(ue);
        }
    }
    
    
    /*public UniqueEdge[] uniqueEdgeArray(){
      return (UniqueEdge[]) uniqueEdges.toArray(new UniqueEdge[0]);
      }*/
    
    
    
    public void addNode(Node n){
        
        // check if graph contains a node with the same name. If not, add it.
        if (!nodes.containsKey(n.getLabel())){
            // set connections to 1 and put in hashmap
            n.addConnection();
            
            nodes.put(n.getLabel(),n);
            //System.out.println("added node "+n.getLabel());
        }  else {
            // if contains, add connection and set node to new settings
            Node n1 = getNode(n.getLabel());
            n1.addConnection();
            //n1.copyPos(n);
            nodes.put(n.getLabel(),n);
        }
    }
    
    /**
     *
     * @param name
     * @return
     */
    public Node getNode(String name){
        return (Node) nodes.get(name);
    }
    
    //    public void renameNodes(String oldName, String newName){
    //        for (Iterator i = edgesIterator(); i.hasNext();){
    //            Edge e = (Edge) i.next();
    //            e.renameNode(oldName,newName);
    //        }
    //        renameNode(oldName,newName);
    //    }
    
    public void addNode(String name){
        Node n = new Node(name);
        n.addConnection();
        if (!nodes.containsKey(name)){
            nodes.put(name,n);
            //System.out.println("added node "+n.getLabel());
        }  else{
            n = getNode(name);
            //System.out.println("increased linkage in node "+n.getLabel());
            n.addConnection();
            nodes.put(name,n);
            
        }
    }
    
    
    public String getComment(){
        return comment;
    }
    
    public void clear(){
        getEdges().clear();
        nodes.clear();
        uniqueEdges.clear();
        comment = "";
    }
    
    public void addComment(String line){
        if (line==null)
            return;
        if (line.length()>1)
            comment=line;
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
    
    
    public void removeEdge(Edge e){
        if (!getEdges().contains(e))
            return;
        getEdges().remove(e);
        removeNode(e.getFromName());
        removeNode(e.getToName());
        UniqueEdge ue = new UniqueEdge(e);
        if (!getEdges().contains(ue))
            uniqueEdges.remove(ue);
    }
    private void removeNode(String name){
        //System.out.println("removing node "+name);
        Node n = getNode(name);
        nodes.remove(name);
        if (n.removeConnection()){
            //System.out.println("replacing node "+n.name);
            nodes.put(name,n);
        }
    }
    
    private void removeNode(Node n){
        removeNode(n.getLabel());
    }
    
    public void removeNodefromQuant(Node n) {
    	
    	nodes.remove(n.getLabel());
    	
    }
    
    /**
     * Gets an iterator of all Nodes in the graph
     * @return
     */
    public Iterator<Node> nodesIterator(){
        
        return nodes.values().iterator();
    }
    
    /**
     * Returns an iterator of all Edges in the graph
     * @return
     */
    public Iterator<Edge> edgesIterator(){
        return getEdges().iterator();
    }
    /**
     * Copies all node settings from one graph to another
     * @param temp the graph with the node settings
     */
    public void copyNodeSettings(BasicGraph temp){
        if (temp==null)
            return;
        for (Iterator <Node>i = temp.nodesIterator(); i.hasNext();){
            Node n = (Node)i.next();
            setNode(n);
        }
    }
    
    public void setNode(Node n){
        if (nodes.containsKey(n.getLabel())){
            Node n2 = getNode(n.getLabel());
            /*System.out.println("color changed from "+n2.color+" to "+
              n.color);*/
            n2.setColor(n.getColor());
            n2.setShape(n.getShape());
            //System.out.println("Setting "+n2.getLabel()+" to "+n.getX()
            //		       +" "+n.getY());
            n2.setX(n.getX());
            n2.setY(n.getY());
            n2.setAnnotation(n.getAnnotation());
        }
    }
    
    public Iterator<UniqueEdge> uniqueEdgesIterator(){
        return uniqueEdges.iterator();
    }
    
    public List<Edge> getEdges() {
        return edges;
    }
    
    public int getNodeSize(){
        return nodes.size();
    }
    public int getEdgeSize(){
        return getEdges().size();
    }
    public int getUniqueEdgeSize(){
        return uniqueEdges.size();
    }
    
}
