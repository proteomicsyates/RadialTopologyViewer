// Medusa is a graph viewer which allows interactive editing of networks
// (edges and nodes) and also connects to databases.
//
// Copyright (C) 2006 Sean Hooper
//
// This program is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published by 
// the Free Software Foundation; either version 2 of the License, or (at 
// your option) any later version.
//
// This program is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along 
// with this program; if not, write to the 
// Free Software Foundation, Inc., 
// 59 Temple Place, Suite 330, 
// Boston, MA 02111-1307 USA

/*
 * Node.java
 *
 * Created on den 9 februari 2005, 23:16
 */

package netQuant.graph;

import java.awt.Color;
import java.io.Serializable;

/**
 * The Node object
 * @author sean
 */
public class Node  implements Serializable{
    
    private double x;
    private double y;
    private Color color = Color.red;
    private Color color2 = null;
    private Color color3 = null;
    private double dx;
    private double dy;
    private int connections;
    private String lbl;
    String annotation="no annotation";
    private boolean fixed = false;
    private int shape=1;
    private int dataSpace1;
    //private int internalID;
    
    private int size=10;
    
    /**
     * String representation of node
     * @return String representation of node
     */
    public String toString(){
	return lbl+" "+x+" "+y+" "+color;
    }     



    /**
     * Reports the label, colors and annotation. Mostly for debugging
     * @return debug info
     */
    public String report(){
	String result=lbl+"\t"+x+"\t"+y+"\t"+getColorEntry();
	result+="\t"+getColor2Entry();
	result+="\t"+getColor3Entry();
	result+="\ts " +shape+"\ta \""+getAnnotation()+"\"";
	return result;
    }

    /**
     * Empty constructor
     */
    public Node (){
	this.lbl="unlabeled";
	x=Math.random()*600;
	y=Math.random()*600;
	connections=0;
    }  
    /**
     * Constructor
     * @param name Label of node
     */
    public Node (String name){
	this.lbl=name;
	x=Math.random();
	y=Math.random();
	connections=0;
    }  
    /**
     * Constructor
     * @param name label
     * @param x x position (0.0-1.0)
     * @param y y position (0.0-1.0)
     */
    public Node (String name, double x, double y){
	this.lbl=name;
	this.x=x;
	this.y=y;
	connections=0;
    }  
    
    /**
     * Constructor
     * @param name 
     * @param x 
     * @param y 
     * @param shape Shape of node 
     */
    public Node (String name, double x, double y, int shape){
	this.lbl=name;
	this.x=x;
	this.y=y;
	this.shape=shape;
	connections=0;
    }  
    
    public Node (String name, Double x, Double y, Integer shape, Color color){
        this.lbl=name;
        this.x=x.doubleValue();
        this.y=y.doubleValue();
        this.shape=shape.intValue();
        this.color=color;
    }
    
    /**
     * Get x position
     * @return x position
     */
    public double getX() {
        return x;
    }

    /**
     * Rescales the x and y positions.
     * @param scale the scaling factor
     */
    public void rescale(int scale){
	x*=scale;
	y*=scale;
    }
      public void rescale(int xScale, int yScale){
	x*=xScale;
	y*=yScale;
    } 
    
    
    /**
     * Sets x position
     * @param x position
     */
    public void setX(double x) {
        this.x =x;
    }
    
    /**
     * Get y position
     * @return y position
     */
    public double getY() {
        return y;
    }
    
    /**
     * Sets y position
     * @param y position
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Get delta x. This is the current velocity of the node
     * @return dX
     */
    public double getDX() {
        return dx;
    }
    
    /**
     * Sets velocity
     * @param dx velocity
     */
    public void setDX(double dx) {
        this.dx = dx;
    }
    
    /**
     * Get y velocity
     * @return velocity
     */
    public double getDY() {
        return dy;
    }
    
    /**
     * Set velocity
     * @param dy y velocity
     */
    public void setDY(double dy) {
        this.dy = dy;
    }
    
    /**
     * get primary color
     * @return Color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Set primary color
     * @param color primary color
     */
    public void setColor(Color color){
        this.color = color;
    }

    // color 2
    /**
     * Secondary color.
     * To make use of this color, you have to extend the node drawing
     * method of the BasicGraphPanel.
     * You can set the secondary and tertiary color by the switches
     * <CODE>c2</CODE> and <CODE>c3</CODE> in the graph data file
     * @return secondary color
     */
    public Color getColor2() {
        return color2;
    }
    
    /**
     * Set secondary color
     * @param color seconday color
     */
    public void setColor2(Color color){
        this.color2 = color;
    }
    // color 3
    /**
     * Get tertiary color
     * @return tertiary color
     */
     public Color getColor3() {
        return color3;
    }
    
    /**
     * Set tertiary color. 
     * See <CODE>getColor2</CODE> for details on how to use 
     * this color
     */
    public void setColor3(Color color){
        this.color3 = color;
    }

    public boolean isFixed() {
        return fixed;
    }
    
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    /**
     * Get the current shape integer of the node
     * @return 
     */
    public int getShape() {
        return shape;
    }
    
    /**
     * Sets the shape
     * @param shape 
     */
    public void setShape(int shape) {
        this.shape = shape;
    }
    
    public void setSize(int size) {
    	this.size=size;
    }
    
    public int getSize() {
    	return size;
    }
    
    /**
     * 
     * @return 
     */
    protected boolean removeConnection(){
	connections--;
	return (connections>=0);
    }
    protected void addConnection(){
	connections++;
    }
    /**
     * Changes the color channels. Channels are R, G and B,
     * channels can be copied or switched
     * @param a channel to copy to.
     * 0: red
     * 1: green
     * 2: blue
     * @param b channel to copy from.
     * @param doSwitch select true to switch channels a and b, false to copy
     * intensity from channel b to channel a.
     */
    public void manipulateColorElement(int a, int b, boolean doSwitch){
	int[] elements=new int[3];
	elements[0]=color.getRed();
	elements[1]=color.getGreen();
	elements[2]=color.getBlue();
	if (doSwitch){
	    int temp=elements[a];
	    elements[a]=elements[b];
	    elements[b]=temp;
	}
	else
	    elements[a]=elements[b];
	color = new java.awt.Color(elements[0],elements[1],elements[2]);
    }
    /**
     * 
     * @param channel 
     * @param value 
     */
    public void manipulateChannel(int channel,int value){
	int[] elements=new int[3];
	elements[0]=color.getRed();
	elements[1]=color.getGreen();
	elements[2]=color.getBlue();
	elements[channel]=value;
	color = new java.awt.Color(elements[0],elements[1],elements[2]);
    }

    /**
     * Compare two nodes
     * @param o object node
     * @return true if nodes are equal
     */
    public boolean equals(Object o){
	if (o.getClass() == this.getClass()){
	Node n = (Node) o;
	return (lbl.compareTo(n.lbl)==0);
	}
	return false;
    }

    /**
     * Get node label
     * @return current label
     */
    public String getLabel(){
	return lbl;
    }
    
    /**
     * Sets the node label.
     * This should not be used directly on nodes that
     * are connected in a graph. Use the methods in Graph to
     * rename nodes.
     * @param lbl new label
     */
    public void setLabel(String lbl) {
        this.lbl = lbl;
    }
    public void setXY(double x, double y){
	this.x=x;
	this.y=y;
    }


    /**
     * Get number of nodes connected to this node
     * @return number of nodes
     */
    public int getConnections(){
	return connections;
    }
    public int hashCode() {
	int code = lbl.hashCode();
	return code;
    }
    
    /**
     * Copies x and y parameters from node n.
     * @param n node position to copy
     */
    public void copyPosition(Node n){
	this.x=n.x;
	this.y=n.y;
    }

    public String getColorEntry(){
	String code = "c "+color.getRed()
	    +","+color.getGreen()+","
	    +color.getBlue();
	return code;
    }
    
    public String getAppletColorEntry(){
	String code = color.getRed()
	    +","+color.getGreen()+","
	    +color.getBlue();
	return code;
    } 
    
    public String getColor2Entry(){
	if (color2==null)
	    return "";
	String code = "c2 "+color2.getRed()
	    +","+color2.getGreen()+","
	    +color2.getBlue();
	return code;
    }
    public String getColor3Entry(){
	if (color3==null)
	    return "";
	String code = "c3 "+color3.getRed()
	    +","+color3.getGreen()+","
	    +color3.getBlue();
	return code;
    }

    /**
     * Additional data. 
     * Should be overridden if you need more data
     * @param space any integer
     */
    public void setDataSpace1(int space){
	dataSpace1=space;
    }
    /**
     * Gets the optional data
     * @return data integer
     */
    public int getDataSpace1(){
	return dataSpace1;
    }
    /**
     * Sets the annotation string
     * @param ann annotation 
     */
    public void setAnnotation(String ann){
	annotation=ann;
    }
    /**
     * Gets the current annotation
     * @return annotation
     */
    public String getAnnotation(){
	return annotation;
    }

}
