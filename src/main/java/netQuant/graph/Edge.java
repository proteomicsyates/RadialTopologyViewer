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


public class Edge extends BasicEdge{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5599050605136395657L;
	private float conf;
    private double orientation=0.;
    private int type;
    
    /**
     * Constructor
     * @param n1 Node 1
     * @param n2 Node 2
     * @param conf Confidence level
     * @param type <CODE>int</CODE> type
     */
    public Edge(String n1, String n2, float conf, int type){
	this.n1=n1;
	this.n2=n2;
	
	this.conf=conf;
	this.type=type;
    }
    /**
     * Constructor
     * @param n1 Node 1
     * @param n2 Node 2
     * @param conf <CODE>float</CODE> Confidence level
     * @param type <CODE>int</CODE> type
     * @param orientation <CODE>double</CODE> orientation
     */
    public Edge(String n1, String n2, float conf, int type, double orientation){
	this.n1=n1;
	this.n2=n2;
	
	this.conf=conf;
	this.type=type;
	this.orientation=orientation;
    }
     public Edge(String n1, String n2,  Integer type, Double orientation,
             Float conf){
         //System.out.println("constructor A");
	this.n1=n1;
	this.n2=n2;
	
	this.conf=conf.floatValue();
	this.type=type.intValue();
	this.orientation=orientation.doubleValue();
        
    }
     
     public Edge(Edge e){ // essentially clones the edge
         this(e.getFromName(),e.getToName(),e.getConf(),e.getType(),e.getOrientation());
     }
     
   public Edge(String n1, String n2,  Integer type, 
           Float conf, Double orientation){
       
       this(n1,n2,type,orientation,conf);
       //System.out.print("relayed to ");
	
    }
    /**
     * Constructor
     * @param n1 Node 1
     * @param n2 Node 2
     * @param type <CODE>int</CODE> type
     * @param conf Confidence level
     * @param orientation <CODE>double</CODE> orientation
     */
    public Edge(String n1, String n2, int type, float conf, double orientation){
	this(n1,n2,conf,type,orientation);
    }

    /**
     * Constructor
     * @param n1 Node 1
     * @param n2 Node 2
     */
    public Edge(String n1, String n2){
	this(n1,n2,1.0f,1);	
    }
    /**
     * Constructor
     * @param n1 Node 1
     * @param n2 Node 2
     * @param type <CODE>int</CODE> type
     */
    public Edge(String n1, String n2, int type){
	this(n1,n2,1.0f,type);
    }
    /**
     * Constructor
     * @param n1 Node 1
     * @param n2 Node 2
     * @param conf Confidence level
     */
    public Edge(String n1, String n2, float conf){
	this(n1,n2,conf,1);
    }	
    /**
     * Set interaction type. Edges with different types of interactions will 
     * be displayed as multiple edges
     * @param type <CODE>int</CODE> type
     */
    public void setType(int type){
	this.type=type;
    }
    
    /**
     * Get the interaction type
     * @return <CODE>int</CODE> type
     */
    public int getType() {
        return type;
    }
    
    /**
     * Set confidence level
     * @param conf <CODE>float</CODE> confidence 0.0 to 1.0
     */
    public void setConf(float conf){
	this.conf=conf;
    }
    
    /**
     * Gets the confidence level
     * @return <CODE>float</CODE> confidence 0.0 to 1.0
     */
    public float getConf() {
        return conf;
    }
    
    // if an edge contains a node, return the other node
    /**
     * if an edge contains a node, return the other node
     * @param name <CODE>String</CODE> name of node
     * @return <CODE>String</CODE> name of other node
     */
    public Node getComplement(String name){
	if (n1.compareTo(name)==0)
	    return new Node(n2);
	if (n2.compareTo(name)==0)
	    return new Node(n1);
	return null;
    }
    /**
     * Returns the complementary node
     * @param n <CODE>Node</CODE> first node
     * @return <CODE>Node</CODE> other node
     */
    public Node getComplement(Node n){
	return getComplement(n.getLabel());
    }
   
    /**
     * Compares two edges
     * @param o other edge
     * @return <CODE>True</CODE> if edges are the same
     */
    public boolean equals(Object o){
	//System.out.println("equals running..."+o.getClass());
	if (o.getClass() == this.getClass()){
	    //System.out.println("testing edge");
	    Edge e = (Edge) o;
	    return sameName(o)&&(type==e.type);
	}
     
	//System.out.println("testing uniqueEdge");
	    return sameName(o);
    }
    
    public String toString(){
	String result=n1+"---"+n2;
	return result;
    }
    
    /**
     * 
     * @return 
     */
    public String report(){
	return super.report()+"\ti "+type+"\tc "+conf+"\to "+orientation;
    }
    
    
    //public Node getNode1(){
	

    /**
     * 
     * @see BasicEdge
     * @param n 
     * @return 
     */
    public boolean containsNode(Node n){
	String name = n.getLabel();
	if (name.compareTo(n1)==0)
	    return true;
	if (name.compareTo(n2)==0)
	    return true;
	return false;
    }
    
    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }
    
    /**
     * Gets the current orientation
     * @return <CODE>double</CODE> orientation
     */
    public double getOrientation() {
        return orientation;
    }
    
    public void renameNode(String oldName, String newName){
        if (n1.compareTo(oldName)==0){
            n1=newName;
        }
        if (n2.compareTo(oldName)==0){
            n2=newName;
        }
    }
    
}
