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
package netQuant.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netQuant.DataFormatException;
import netQuant.NetQuantSettings;
import netQuant.dataio.DataLoader;
import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

/**
 * Describe class <code>EditableGraphPanel</code> here. This class extends
 * BasicGraphPanel and allows mouse interactions, and also data manipulation.
 *
 * @author diego
 * @version 1.0
 */
public class EditableGraphPanel extends BasicGraphPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1852196773267444659L;
	// Image image=null;
	BufferedImage image = null;
	Image tempImage = null;

	// constructor
	/**
	 * Constructor with settings
	 *
	 * @param stringSettings
	 *            settings
	 */
	public EditableGraphPanel(NetQuantSettings stringSettings) {
		super(stringSettings);
		initPopup();
		tryImage();
		initShow();
		// setBackground(Color.grey);
	}

	/**
	 * Constructor with default settings
	 */
	public EditableGraphPanel() {
		super();
		initPopup();
		tryImage();
		initShow();
	}

	private void tryImage() {
		try {
			setImage();
			tempImage = image;
		} catch (Exception e) {
		}
	}

	/**
	 * Sets the default image
	 *
	 * @throws java.io.IOException
	 *             if default file
	 */
	public void setImage() throws IOException {
		setImage("netview_default.png");
	}

	/**
	 * Sets the background image
	 *
	 * @param imageURL
	 *            path to image
	 * @throws java.io.IOException
	 *             if image is not found or cannot be read.
	 */
	public void setImage(final String imageURL) throws IOException {
		// System.out.println(imageURL);
		File imageFile = new File(imageURL);
		image = javax.imageio.ImageIO.read(imageFile);

	}

	/**
	 *
	 * @param myImage
	 * @throws java.lang.InterruptedException
	 */
	public void setImage(BufferedImage myImage) throws InterruptedException {
		image = myImage;
		// scaledCopy();
	}

	public void clearImage() {
		image = null;
		// System.out.println("image cleared");
	}

	/*
	 * ------------------------------------------------------------- -more
	 * overridden methods of BasicGraphPanel-------------------
	 * -------------------------------------------------------------
	 */

	public void drawBackgroundImage(Graphics2D g2d) {
		if (image == null)
			return;
		Dimension d = getPreferredSize();
		// System.out.println(d.width+" : "+d.height);
		g2d.drawImage(image, 0, 0, d.width, d.height, null);
	}

	@Override
	public void paintEdge(Graphics2D g, Edge e) {
		if (!showEdges[e.getType() - 1])
			return;
		super.paintEdge(g, e);

	}

	public boolean showBorder = true;

	public void setShowBorder(boolean show) {
		showBorder = show;
	}

	@Override
	public void paintNet(Graphics2D g2d) {
		// if we have an image, paint it
		// drawBackgroundImage(g2d);
		// draw the border
		// g2d.setColor(Color.gray);
		if (showBorder) {
			Rectangle2D.Double border = new Rectangle2D.Double(2, 2, getPanelWidth(), getPanelHeight());
			g2d.draw(border);
		}

		for (Iterator edges = graph.edgesIterator(); edges.hasNext();) {
			Edge e = (Edge) edges.next();
			int col = e.getType();
			if (showEdges[col - 1])
				paintEdge(g2d, e);
		}

		// make sure confidence is restored
		g2d.setComposite(makeComposite(1.0f));
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			paintNode(g2d, node);
		}
		// PaintTools.paintPath(g2d,10);
	}

	@Override
	public void saveImage(String path, int param) {
		setShowBorder(false);
		super.saveImage(path, param);
		setShowBorder(true);
	}

	/**
	 * Paints the component
	 *
	 * @param g
	 *            current graphics
	 */
	@Override
	public synchronized void paintComponent(Graphics g) {

		Graphics2D g2d = prePaint(g);

		// if we have an image, paint it
		drawBackgroundImage(g2d);

		if (hideWhenMove) {
			if ((!running) & (pick != null)) {

				paintVisibleNet(g2d);
			} else {
				paintNet(g2d);
			}
		} else {
			paintNet(g2d);
		}

		// paintNet(g2d);
		// markNode(g2d, marked);
		// for selection boxes
		drawBox(g2d);
		if (drawTempLine) {
			float[] dashPattern = { 2, 3, 2, 3 };
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
			g2d.drawLine(startx, starty, endx, endy);
		}
		g2d.setStroke(new BasicStroke(1));

	}

	public void paintVisibleNet(Graphics2D g2d) {

		// draw the border
		// g2d.setColor(Color.gray);
		if (showBorder) {
			Rectangle2D.Double border = new Rectangle2D.Double(2, 2, getPanelWidth(), getPanelHeight());
			g2d.draw(border);
		}

		for (Iterator edges = visibleGraph.edgesIterator(); edges.hasNext();) {
			Edge e = (Edge) edges.next();
			int col = e.getType();
			if (showEdges[col - 1])
				paintEdge(g2d, e);
		}

		// make sure confidence is restored
		g2d.setComposite(makeComposite(1.0f));
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			paintNode(g2d, node);
		}
		// PaintTools.paintPath(g2d,10);
	}

	// public void loadingMessage(){
	// Graphics2d g2d =

	protected void drawBox(Graphics2D g2d) {
		if (currentRect != null) {
			// Draw a rectangle on top of the image.
			g2d.setXORMode(Color.red); // Color of line varies
			// depending on image colors
			float[] dashPattern = { 2, 3, 2, 3 };
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
			g2d.drawRect(rectToDraw.x, rectToDraw.y, rectToDraw.width - 1, rectToDraw.height - 1);
		}

	}

	protected boolean showName = true;

	/**
	 * Switches between showing node name and annotation
	 *
	 * @param b
	 *            true if names are to be shown, false if annotation
	 */
	public void setShowName(boolean b) {
		showName = b;
		showAnnotation = (!b);
	}

	/**********************
	 * // handle editing events (right mouse button)
	 ************************/
	private void handleRightMouseDragged(MouseEvent e) {
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {

			// set line drawing to true, and repaint
			drawTempLine = true;
			endx = e.getX();
			endy = e.getY();
			repaint();
		}

		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		handleLeftMouseDragged(e);
		handleRightMouseDragged(e);

	}

	public void handleReleaseRightButton(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON3)
			return;
		removeMouseMotionListener(this);
		// int i=getClosest(e);
		Node node = getClosest(e);
		if (node != null) {
			// prompt for new node
			temp2 = node;
		} else
			temp2 = new Node("unnamed", e.getX(), e.getY());
		drawTempLine = false;
		endx = e.getX();
		endy = e.getY();
		if (temp1 != temp2) {
			// System.out.println(temp1+" "+temp2);
			addEdge();
			repaint();
			temp1 = null;
			temp2 = null;
		} else {
			// System.out.println(temp1.getLabel());
			showPopup(e);
		}

		// System.out.println(temp1+"\n ---> "+temp2);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		handleReleaseLeftButton(e);
		handleReleaseRightButton(e);
	}

	private void handlePressRightButton(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {

			addMouseMotionListener(this);
			Node node = getClosest(e);
			// int index=getClosest(e);
			if (node != null) {
				temp1 = node;
				// System.out.println("dragging from "+nodeArray[index]);
				startx = (int) temp1.getX();
				starty = (int) temp1.getY();
			} else {
				startx = e.getX();
				starty = e.getY();
				temp1 = new Node("unnamed", startx, starty);
			}

			e.consume();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

		handlePressLeftButton(e);
		handlePressRightButton(e);
	}

	// the edge dialog
	Frame f = new Frame();

	protected void addEdge() {
		// Frame f = new Frame();
		// System.out.println(temp1+" "+temp2);
		/*
		 * Edge e = EdgeDialog.showDialog(f,null,temp1,temp2,stringSettings); if
		 * (e!=null){ temp1.setLabel(e.getFromName());
		 * temp2.setLabel(e.getToName()); graph.addEdge(e);
		 * graph.setNode(temp1); graph.setNode(temp2); updateNodes(); }
		 */
	}

	// to add edges, we need startx, starty, endx and endy. When complete,
	// open the dialog to specify the params
	int startx;
	int starty;
	int endx;
	int endy;
	boolean drawTempLine = false;
	Node temp1 = null;
	Node temp2 = null;

	// an array to remember which edges to show/hide
	boolean[] showEdges;

	public void initShow() {
		final int max = 40;
		showEdges = new boolean[max];
		for (int i = 0; i < max; i++) {
			showEdges[i] = true;
		}
	}

	/**
	 * Shows the interaction type <I>i</I>
	 *
	 * @param i
	 *            interaction type to show
	 * @param b
	 *            true: paint interaction type. false: hide interaction type
	 */
	public void setShowEdge(int i, boolean b) {
		showEdges[i] = b;
	}

	public void setHideEdge(int i, boolean b) {
		showEdges[i] = (!b);
	}

	int border = 10;

	/**
	 * Sets the shape for fixed nodes
	 *
	 * @param shape
	 *            0-3: implemented shapes in PaintTools
	 */
	public void setShape(int shape) {
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			if (node.isFixed()) {
				node.setShape(shape);
			}
		}
	}

	/*
	 * popup!!!!!
	 */
	javax.swing.JPopupMenu popup;
	javax.swing.JMenuItem removeItem;
	javax.swing.JMenuItem editColorItem;
	javax.swing.JMenuItem editShapeItem;
	javax.swing.JMenuItem setAnnotationItem;
	javax.swing.JMenuItem getPositionItem;

	// show a popup in certain cases
	private void initPopup() {
		// System.out.println("initializing popup");
		popup = new javax.swing.JPopupMenu("Edit data");
		removeItem = new javax.swing.JMenuItem("Delete");
		editColorItem = new javax.swing.JMenuItem("Color");
		editShapeItem = new javax.swing.JMenuItem("Shape");
		setAnnotationItem = new javax.swing.JMenuItem("Annotation");
		getPositionItem = new javax.swing.JMenuItem("Position");
		removeItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (temp1 != null) {
					// System.out.println("removing node "+temp1.getLabel());
					removeEdgeByLabel(temp1.getLabel());
					temp1 = null;
					temp2 = null;
					repaint();
				} else
					System.out.println("node is null, for some strange reason");
			}
		});
		editColorItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Color newColor = javax.swing.JColorChooser.showDialog(getRootPane(), "Choose node color",
						temp1.getColor());
				if (newColor != null) {
					temp1.setColor(newColor);

				}
				repaint();
			}
		});

		editShapeItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {

				temp1.setShape(chooseShape());
				repaint();
			}
		});
		setAnnotationItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setAnnotation(temp1);
			}
		});
		getPositionItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getPosition(temp1);
			}
		});
		popup.add(removeItem);
		popup.addSeparator();
		popup.add(editColorItem);
		popup.add(editShapeItem);
		popup.add(setAnnotationItem);
		popup.add(getPositionItem);

	}

	private void getPosition(Node temp) {
		javax.swing.JOptionPane.showMessageDialog(this, "x: " + temp.getX() + " y:" + temp.getY());
	}

	private void setAnnotation(Node temp) {
		String s = javax.swing.JOptionPane.showInputDialog("View or edit node annotation:\n", temp.getAnnotation());
		if ((s != null) && (s.length() > 0)) {
			temp.setAnnotation(s);
		}
	}

	private int chooseShape() {
		Object[] shapes = { "circle", "rectangle", "triangle", "diamond" };
		int s = javax.swing.JOptionPane.showOptionDialog(this, "Choose a node shape", "Shape",
				javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, shapes,
				"circle");
		return s;
	}

	private void showPopup(MouseEvent e) {
		popup.show(e.getComponent(), e.getX(), e.getY());
		e.consume();
	}

	/*
	 * manipulate data
	 */
	public java.util.ArrayList<Node> getFixed() {
		return graph.getFixed();
	}

	public void copyNodeSettings(Graph g) {
		graph.copyNodeSettings(g);
	}

	public void removeEdgeByLabel(String label) {
		// System.out.println("removing "+label);
		graph.removeEdgeByLabel(label);
	}

	public void removeFixedNodes() {
		graph.removeFixed();
	}

	public void crop(double confidence) {
		graph.cropEdges(confidence);
		calculateEdgeLength();
	}
	/*
	 * A regular expression search, for finding nodes matching a string. The
	 * string should be sent from the front-end.
	 */

	/*
	 * Set a method of marking nodes that users search for by displaying for
	 * instance an arrow
	 */
	public void markNode(java.awt.Graphics2D g2d, Node markedNode) {
		if (markedNode == null) {
			return;
		}
		// start by just drawing an arrow
		// from ref point 0,0
		int refX = getPanelWidth() / 2;
		int refY = getPanelHeight() / 2;
		double r45 = Math.PI / 7.0;
		double deltaLength = 15.0;
		double headLength = 13.0;

		// get theta
		double x = markedNode.getX();
		double y = markedNode.getY();
		double deltaX = (x - refX);
		double deltaY = -(y - refY);
		double theta = Math.atan(deltaY / deltaX);
		if (deltaX < 0.0)
			theta += Math.PI;

		// get x,y for the head point
		int xh = (int) (x - deltaLength * Math.cos(theta));
		int yh = (int) (y + deltaLength * Math.sin(theta));

		// get clockwise shoulder point
		int xc = (int) (xh - headLength * Math.cos(theta - r45));
		int yc = (int) (yh + headLength * Math.sin(theta - r45));

		// get anti-clockwise shoulder point
		int xa = (int) (xh - headLength * Math.cos(theta + r45));
		int ya = (int) (yh + headLength * Math.sin(theta + r45));

		// the shadow
		int shad = 3;
		// build a polygon from this
		int[] xpoints = { xc, xh, xa };
		int[] ypoints = { yc, yh, ya };
		int[] shadowX = { xc + shad, xh + shad, xa + shad };
		int[] shadowY = { yc + shad, yh + shad, ya + shad };
		int npoints = 3;

		// draw it
		// g2d.setColor(Color.magenta);
		g2d.setColor(Color.black);
		g2d.fillPolygon(shadowX, shadowY, npoints);
		g2d.setColor(Color.orange);
		g2d.fillPolygon(xpoints, ypoints, npoints);

		g2d.setColor(Color.black);

	}

	public int selectNodeByRegExp(final String regexp) {
		int matches = 0;
		Pattern labelPattern = Pattern.compile(regexp);
		Matcher matcher;
		// loop through and fix nodes that match the expression
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			matcher = labelPattern.matcher(node.getLabel());
			if (matcher.find()) {
				node.setFixed(true);
				matches++;
			}
		}
		return matches;
	}

	/**
	 * Reads a file of node names and selected those nodes in the graph.
	 *
	 * @param fileName
	 * @throws java.io.IOException
	 * @return
	 */
	public int selectNodeFromFile(final String fileName) throws IOException {
		int matches = 0;
		String inLine;
		File loadFile = new File(fileName);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile)));
		while ((inLine = in.readLine()) != null) {
			for (Iterator it = graph.nodesIterator(); it.hasNext();) {
				Node node = (Node) it.next();
				if (node.getLabel().compareTo(inLine) == 0) {
					matches++;
					node.setFixed(true);
					continue;
				}
			}
		}
		in.close();
		return matches;
	}

	public void manipulateColor(int a, int b, boolean doSwitch) {
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			if (node.isFixed()) {
				node.manipulateColorElement(a, b, doSwitch);
			}
		}
	}

	public void addGradientX(int channel) {
		int value;
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			if (node.isFixed()) {
				value = (int) (node.getX() / getPanelWidth() * 255.0);
				// System.out.println(node.getLabel()+" "+value);
				node.manipulateChannel(channel, value);
			}
		}
	}

	public void addGradientY(int channel) {
		int value;
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			if (node.isFixed()) {
				value = (int) (node.getY() / getPanelWidth() * 255.0);
				// System.out.println(node.getLabel()+" "+value);
				node.manipulateChannel(channel, value);
			}
		}
	}

	public void clearGraph() {
		graph.clear();
		updateNodes();
	}

	// Timer loadTimer;
	// public void loadGraphThread(){
	// loadTimer = new Timer(200,loadGraphThreadActionEvent);

	DataLoader dl = new DataLoader(getPanelWidth(), getPanelHeight());

	public void loadGraph(String fileName) throws IOException, DataFormatException {
		graph = dl.load(fileName);
		updateNodes();
	}

	public void saveGraph(String fileName) throws IOException {
		dl.save(graph, fileName, scale);
		updateNodes();
	}

	public void writePajek(String fileName) throws IOException {
		dl.saveAsPajek(graph, fileName);
	}

	public void writePS(String fileName) throws IOException {
		dl.saveAsPS(graph, fileName, getStringSettings(), nodeSize, fontSize);
		updateNodes();
	}

	public void writeEPS(String fileName) throws IOException {
		dl.saveAsEPS(graph, fileName, getStringSettings(), nodeSize, fontSize);
		updateNodes();
	}
	/*
	 * Data manipulation tools
	 */

	public void appendGraph(String fileName) throws IOException, DataFormatException {
		Graph temp = dl.load(fileName);
		graph.addGraph(temp);
		copyNodeSettings(temp);
		calculateEdgeLength();
		// System.out.println(graph.report());
		updateNodes();
	}

	public void addGraph(Graph g) {
		if (g == null)
			return;
		g.rescaleNodes(600);
		graph.addGraph(g);
		copyNodeSettings(g);
	}

	QuantDataHashTable qdhash = new QuantDataHashTable();
	QuantDataHashTable baits = new QuantDataHashTable();

	double inpmax = -1;
	double conmax = -1;
	double expmax = -1;
	double inpmin = Double.MAX_VALUE;
	double conmin = Double.MAX_VALUE;
	double expmin = Double.MAX_VALUE;
	double inpsum = 0;
	double consum = 0;
	double expsum = 0;
	Hashtable<String, Integer> protein_onthology = new Hashtable<String, Integer>();

	/**
	 * append the ontology terms to the proteins
	 *
	 * @param data1
	 */
	public void setProteinOnthology(Vector<String> data1) {

		for (int i = 2; i < data1.size(); i++) {
			String[] buf = data1.elementAt(i).split("\t");

			buf[0] = buf[0].toLowerCase();
			buf[0] = buf[0].split("/")[0];
			buf[0] = buf[0].trim();

			if (!protein_onthology.containsKey(buf[0])) {
				protein_onthology.put(buf[0], Integer.parseInt(buf[1]));
			}
		}

	}

	/**
	 * this method parse and fill the information for the topomapper graph
	 *
	 * @param data1
	 * @return
	 */
	public String setRadialData(Vector<String> data1) {

		String root;
		Graph g = new Graph();

		Hashtable<String, Node> protid = new Hashtable<String, Node>();

		String[] first = data1.elementAt(2).split("\t");
		Node rootnode = new Node();
		rootnode.setLabel(first[4]);

		protid.put(first[0], rootnode);

		root = first[4];

		int k;
		if (protein_onthology.isEmpty()) {
			k = 1;
		} else {
			k = protein_onthology.size();
		}

		Vector<String[]> protein_annotations = new Vector<String[]>();

		for (int i = 3; i < data1.size(); i++) {

			String[] buf = data1.elementAt(i).split("\t");

			String[] prt_ann = new String[2];

			if (buf.length == 5) {

				buf[3] = buf[3].toLowerCase();
				buf[3] = buf[3].split("/")[0];

				// if (buf[3].endsWith(" ")) {
				buf[3] = buf[3].trim();// String.valueOf(buf[3].subSequence(0,
										// buf[3].length()-1));
				// }

				// System.out.println(buf[3]);

				if (!protid.containsKey(buf[0])) {
					Node node = new Node();
					node.setLabel(buf[4]);
					node.setAnnotation(buf[3]);
					protid.put(buf[0], node);
				}

				prt_ann[0] = buf[4];
				prt_ann[1] = buf[3];

				protein_annotations.add(prt_ann);

				g.addEdge(protid.get(buf[1]), protid.get(buf[0]), Float.parseFloat(buf[2]), 1);

				if (!protein_onthology.containsKey(buf[3])) {
					protein_onthology.put(buf[3], k);
					prot_list.add(buf[3]);
					k++;
				}

			} else {

				if (buf.length == 2) {
					if (protid.containsKey(buf[0]) && protid.containsKey(buf[1])) {

						g.addEdge(protid.get(buf[1]), protid.get(buf[0]), 0.9f, 2);
						;

					}
				}
			}
		}

		/*
		 * if (data1.elementAt(data1.size()-1).equals("")) {
		 * data1.remove(data1.size()-1); } for (int i=0; i < data1.size();i++) {
		 * String [] entry = data1.elementAt(i).split("\t"); if
		 * (entry.length==1) { root = entry[0]; } else { Edge e = new
		 * Edge(root,entry[0], Float.parseFloat(entry[1]),1 ); g.addEdge(e); } }
		 */
		graph.addGraph(g);

		for (int i = 0; i < protein_annotations.size(); i++) {

			graph.setNodeAnnotation(protein_annotations.elementAt(i)[0], protein_annotations.elementAt(i)[1]);

		}

		// graph.setNodeAnnotation("XRCC5", "questo");

		return root;

	}

	public void setNodesQuantData(Vector<String> data1) {

		for (int i = 0; i < data1.size(); i++) {

			String[] entry = data1.elementAt(i).split("\t");
			if (entry.length > 1) {
				qdhash.addQuantData(entry[0], Double.parseDouble(entry[1]));
			}

		}

	}

	public void clearQuantInformation() {
		qdhash.clear();
		baits.clear();
	}

	public void setBait(Vector<String> data1) {

		for (int i = 0; i < data1.size(); i++) {
			baits.addQuantData(data1.elementAt(i), 1);
		}

	}

	public void getExp() {

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());
			double val = qdhash.getExp(node.getLabel());

			if (baits.cointains(node.getLabel())) {
				Color c = getColor(val, 2, 0);
				node.setColor(c);
				node.setShape(2);
			} else {
				Color c = getColor(val, 2, 1);
				node.setColor(c);
				node.setShape(1);
			}
			graph.setNode(node);
		}

		updateNodes();

	}

	public void getControl() {

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());
			double val = qdhash.getControl(node.getLabel());

			if (baits.cointains(node.getLabel())) {
				Color c = getColor(val, 1, 0);
				node.setColor(c);
				node.setShape(2);
			} else {
				Color c = getColor(val, 1, 1);
				node.setColor(c);
				node.setShape(1);
			}
			graph.setNode(node);

		}
		updateNodes();

	}

	public void getInput() {

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {

			Node node = graph.getNode(it.next().getLabel());
			double val = qdhash.getInput(node.getLabel());

			if (baits.cointains(node.getLabel())) {
				Color c = getColor(val, 0, 0);
				node.setColor(c);
				node.setShape(2);
			} else {
				Color c = getColor(val, 0, 1);
				node.setColor(c);
				node.setShape(1);
			}
			graph.setNode(node);
		}

		updateNodes();

	}

	private Color getColor(double v, int t, int b) {

		double max = -1;

		switch (t) {
		case 0: {
			max = inpmax;
			break;
		}
		case 1: {
			max = conmax;
			break;
		}
		case 2: {
			max = expmax;
			break;
		}

		}

		max = Math.log10(max + 1);
		v = Math.log10(v + 1);

		double ratio = v / max;
		Color c;

		Color c1 = Color.RED;
		Color c2 = Color.YELLOW;
		Color c3 = Color.GREEN;
		Color c4 = Color.BLUE;

		int red, blue, green;

		if (ratio >= 0.66) {
			red = (int) (c1.getRed() * ratio + c2.getRed() * (1 - ratio));
			green = (int) (c1.getGreen() * ratio + c2.getGreen() * (1 - ratio));
			blue = (int) (c1.getBlue() * ratio + c2.getBlue() * (1 - ratio));
		} else {
			if (ratio >= 0.33) {
				red = (int) (c2.getRed() * ratio + c3.getRed() * (1 - ratio));
				green = (int) (c2.getGreen() * ratio + c3.getGreen() * (1 - ratio));
				blue = (int) (c2.getBlue() * ratio + c3.getBlue() * (1 - ratio));
			} else {
				red = (int) (c3.getRed() * ratio + c4.getRed() * (1 - ratio));
				green = (int) (c3.getGreen() * ratio + c4.getGreen() * (1 - ratio));
				blue = (int) (c3.getBlue() * ratio + c4.getBlue() * (1 - ratio));
			}
		}

		c = new Color(red, green, blue);

		return c;

	}

	double thrs = 0.000001;

	public void computePathMap() {

		Node bait = null;

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {

			Node node = graph.getNode(it.next().getLabel());

			if (baits.cointains(node.getLabel())) {
				bait = node;
				break;
			}
		}

		ArrayList<String> activatedNodes = new ArrayList<String>();
		ArrayList<String> connectedNodes = new ArrayList<String>();
		ArrayList<String> disconnectedNodes = new ArrayList<String>();
		ArrayList<String> intermediateNodes = new ArrayList<String>();

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());
			if (node.getSize() > 0) {
				activatedNodes.add(node.getLabel());
			}
		}

		for (int i = 0; i < activatedNodes.size(); i++) {

			if (connected(bait.getLabel(), activatedNodes.get(i))) {
				connectedNodes.add(activatedNodes.get(i));
			} else {
				disconnectedNodes.add(activatedNodes.get(i));
			}

		}

		int addconn = 0;

		do {

			addconn = 0;

			for (int i = 0; i < disconnectedNodes.size(); i++) {
				for (int j = 0; j < connectedNodes.size(); j++) {
					if (connected(disconnectedNodes.get(i), connectedNodes.get(j))) {
						connectedNodes.add(disconnectedNodes.get(i));
						disconnectedNodes.remove(i);
						i--;
						addconn++;
						break;
					}
				}
			}

		} while (addconn > 0);

		disconnectedNodes.trimToSize();
		connectedNodes.trimToSize();

		for (int i = 0; i < disconnectedNodes.size(); i++) {

			ArrayList<String> buffer = getConnector(disconnectedNodes.get(i));

			for (int j = 0; j < buffer.size(); j++) {
				for (int k = 0; k < connectedNodes.size(); k++) {
					if (connected(buffer.get(j), connectedNodes.get(k))) {
						connectedNodes.add(disconnectedNodes.get(i));
						connectedNodes.add(buffer.get(j));
						intermediateNodes.add(buffer.get(j));
						disconnectedNodes.remove(i);
						i--;

						k = connectedNodes.size() + 10;
						j = buffer.size() + 10;

					}
				}
			}

		}

		disconnectedNodes.trimToSize();
		connectedNodes.trimToSize();

		for (int i = 0; i < disconnectedNodes.size(); i++) {
			for (int j = 0; j < connectedNodes.size(); j++) {
				if (connected(disconnectedNodes.get(i), connectedNodes.get(j))) {
					connectedNodes.add(disconnectedNodes.get(i));
					disconnectedNodes.remove(i);
					i--;
					addconn++;
					break;
				}
			}
		}

		disconnectedNodes.trimToSize();
		connectedNodes.trimToSize();

		for (int i = 0; i < intermediateNodes.size(); i++) {

			Node node = graph.getNode(intermediateNodes.get(i));

			Color c = Color.GREEN;
			node.setSize(7);
			node.setColor(c);
			node.setAnnotation(node.getLabel());

			graph.setNode(node);

		}

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());
			node.setShape(4);

			if (qdhash.cointains(node.getLabel())) {

				double v1 = qdhash.getInput(node.getLabel());

				if (v1 > 0) {

					int red = (int) (v1 * 100) + 150;
					if (red > 255)
						red = 255;

					int size = (int) (red / 51.6) + 7;

					Color c = new Color(red, 0, 0);
					node.setSize(size);
					node.setColor(c);
					node.setAnnotation(node.getLabel());

				} else {

					int blue = (int) (v1 * -100) + 150;
					if (blue > 255)
						blue = 255;

					int size = (int) (blue / 51.6) + 7;

					Color c = new Color(0, 0, blue);
					node.setColor(c);
					node.setSize(size);
					node.setAnnotation(node.getLabel());
				}
			}
			if (baits.cointains(node.getLabel())) {
				node.setColor(Color.YELLOW);
				node.setSize(18);
			}

			graph.setNode(node);
			// i++;
		}

		updateNodes();

	}

	private ArrayList<String> getConnector(String s) {

		ArrayList<String> list = new ArrayList<String>();

		for (Iterator<Edge> ed = graph.edgesIterator(s); ed.hasNext();) {

			Edge e = ed.next();

			list.add(e.getComplement(s).getLabel());

		}

		return list;
	}

	private boolean connected(String n, String s) {

		for (Iterator<Edge> ed = graph.edgesIterator(); ed.hasNext();) {

			Edge e = ed.next();

			if (e.contains(n) && e.contains(s)) {
				return true;
			}

		}

		return false;
	}

	public void computeOverlayMap() {

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());
			node.setShape(4);

			if (qdhash.cointains(node.getLabel())) {

				double v1 = qdhash.getInput(node.getLabel());

				if (v1 == 0) {
					Color c = new Color(2, 132, 130);
					node.setColor(c);
					node.setAnnotation(node.getLabel() + "Both Conditions");
				}
				if (v1 > 0) {
					Color c = new Color(255, 204, 0);
					node.setColor(c);
					node.setAnnotation(node.getLabel() + "Condition 1");
				}
				if (v1 < 0) {
					Color c = new Color(101, 153, 255);
					node.setColor(c);
					node.setAnnotation(node.getLabel() + "Condition 2");
				}

				if (baits.cointains(node.getLabel())) {
					node.setColor(Color.YELLOW);
					node.setSize(18);
				}

				graph.setNode(node);
			}

			updateNodes();

		}

	}

	public void computeDifferentialMap() {

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());
			node.setShape(4);

			if (qdhash.cointains(node.getLabel())) {

				double v1 = qdhash.getInput(node.getLabel());

				if (v1 > 0) {

					int red = (int) (v1 * 100) + 150;
					if (red > 255)
						red = 255;

					int size = (int) (red / 51.6) + 7;

					Color c = new Color(red, 0, 0);
					node.setSize(size);
					node.setColor(c);
					node.setAnnotation(node.getLabel() + "\t" + v1);

				} else {

					int blue = (int) (v1 * -100) + 150;
					if (blue > 255)
						blue = 255;
					int size = (int) (blue / 51.6) + 7;

					Color c = new Color(0, 0, blue);
					node.setColor(c);
					node.setSize(size);
					node.setAnnotation(node.getLabel() + "\t" + v1);
				}
			} else {

				Color c = Color.WHITE;
				node.setSize(0);
				node.setColor(c);
				node.setAnnotation(node.getLabel() + "\t" + 0);
			}

			if (baits.cointains(node.getLabel())) {
				node.setColor(Color.YELLOW);
				node.setSize(18);
			}

			graph.setNode(node);
			// i++;
		}
		updateNodes();

	}

	public void computeDifferentialNetwork() {

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());
			node.setShape(4);

			if (qdhash.cointains(node.getLabel())) {

				double v1 = qdhash.getInput(node.getLabel()); // maybe check to
																// see if value
																// is available

				if (v1 > 0) {

					int red = (int) (v1 * 100) + 150;
					if (red > 255)
						red = 255;

					int size = (int) (red / 51.6) + 7;

					Color c = new Color(red, 0, 0);
					node.setSize(size);
					node.setColor(c);
					node.setAnnotation(node.getLabel());

				} else {

					int blue = (int) (v1 * -100) + 150;
					if (blue > 255)
						blue = 255;

					int size = (int) (blue / 51.6) + 7;

					Color c = new Color(0, 0, blue);
					node.setColor(c);
					node.setSize(size);
					node.setAnnotation(node.getLabel());
				}
			} else {

				Color c = new Color(0, 255, 0);
				node.setSize(7);
				node.setColor(c);
				node.setAnnotation(node.getLabel());
			}

			if (baits.cointains(node.getLabel())) {
				node.setColor(Color.YELLOW);
				node.setSize(18);
			}

			graph.setNode(node);
			// i++;
		}
		updateNodes();

	}

	public double normalizeValues(double v1, double v2) {

		double val = ((v2 - v1) / (v2 + v1 + 1)) * (v1 / inpmax + v2 / expmax) * (v1 / inpsum + v2 / expsum);
		return val;

	}

	public void computeDifferentialEdges() {

		List<Edge> edges = graph.getEdges();
		double orientation;
		double n1oldval, n2oldval, n1newval, n2newval;
		boolean sign1, sign2;

		for (int i = 0; i < edges.size(); i++) {
			Edge edg = edges.get(i);

			orientation = edg.getOrientation();

			if (orientation == 0.0) {

			}
			if (orientation == 1.0) {
				n1oldval = qdhash.getInput(edg.n1);
				n1newval = qdhash.getExp(edg.n1);

				n2oldval = qdhash.getInput(edg.n2);
				n2newval = qdhash.getExp(edg.n2);

				if (n1newval - n1oldval > 0)
					sign1 = true;
				else
					sign1 = false;

				if (n2newval - n2oldval > 0)
					sign2 = true;
				else
					sign2 = false;

				if (sign1 && sign2) {
					edg.setType(5);
				}
				if (sign1 && !sign2) {
					edg.setType(6);
				}
				if (!sign1 && sign2) {
					edg.setType(5);
				}
				if (!sign1 && !sign2) {
					edg.setType(6);
				}

			}
			if (orientation == -1.0) {

				n1oldval = qdhash.getInput(edg.n2);
				n1newval = qdhash.getExp(edg.n2);

				n2oldval = qdhash.getInput(edg.n1);
				n2newval = qdhash.getExp(edg.n1);

				if (n1newval - n1oldval > 0)
					sign1 = true;
				else
					sign1 = false;

				if (n2newval - n2oldval > 0)
					sign2 = true;
				else
					sign2 = false;

				if (sign1 && sign2) {
					edg.setType(5);
				}
				if (sign1 && !sign2) {
					edg.setType(6);
				}
				if (!sign1 && sign2) {
					edg.setType(5);
				}
				if (!sign1 && !sign2) {
					edg.setType(6);
				}

			}

		}

	}

	public Color getColor(double val, double min, double max, int b) {

		double ratio = 1;
		Color c;
		int R, G, B;

		/*
		 * if (b==1) { Color c1 = Color.YELLOW; Color c2 = Color.GREEN; val =
		 * Math.log10(val+min+1); max = Math.log10(max+min+1); int red =
		 * (int)(c1.getRed()*ratio + c2.getRed()*(1-ratio)); int green =
		 * (int)(c1.getGreen()*ratio + c2.getGreen()*(1-ratio)); int blue =
		 * (int)(c1.getBlue()*ratio + c2.getBlue()*(1-ratio)); c = new
		 * Color(red, green, blue); } else { Color c1 = Color.RED; Color c2 =
		 * Color.BLUE; val = val+Math.abs(min); max = max+Math.abs(min); ratio =
		 * val/max; int red = (int)(c1.getRed()*ratio + c2.getRed()*(1-ratio));
		 * int green = (int)(c1.getGreen()*ratio + c2.getGreen()*(1-ratio)); int
		 * blue = (int)(c1.getBlue()*ratio + c2.getBlue()*(1-ratio)); c = new
		 * Color(red, green, blue); // }
		 */

		// val = Math.abs(val)+Math.abs(min);
		// max = max + Math.abs(min);

		/*
		 * if (b==1) { if (val>=0) { ratio = val/max; R = (int)(ratio * 255.0);
		 * G = (int)((1-ratio)*255.0); c = new Color(R,G,0); } else { ratio =
		 * val/min; B = (int)(ratio * 255.0); G = (int)((1-ratio)*255.0); c =
		 * new Color(0,G,B); } } else { if (val>=0) { ratio = val/max; R =
		 * (int)(ratio * 255.0); G = (int)((1-ratio)*255.0); c = new
		 * Color(R,G,255); } else { ratio = val/min; B = (int)(ratio * 255.0); G
		 * = (int)((1-ratio)*255.0); c = new Color(255,G,B); } }
		 */

		Color c1 = Color.RED;
		Color c2 = Color.YELLOW;
		Color c3 = Color.GREEN;
		Color c4 = Color.BLUE;

		int red, blue, green;

		if (val > 0) {
			ratio = val / max;
			if (ratio >= 0.33) {
				red = (int) (c1.getRed() * ratio + c2.getRed() * (1 - ratio));
				green = (int) (c1.getGreen() * ratio + c2.getGreen() * (1 - ratio));
				blue = (int) (c1.getBlue() * ratio + c2.getBlue() * (1 - ratio));
			} else {
				red = (int) (c2.getRed() * ratio + c3.getRed() * (1 - ratio));
				green = (int) (c2.getGreen() * ratio + c3.getGreen() * (1 - ratio));
				blue = (int) (c2.getBlue() * ratio + c3.getBlue() * (1 - ratio));
			}
		} else {
			ratio = val / min;
			if (ratio >= 0.33) {
				red = (int) (c4.getRed() * ratio + c3.getRed() * (1 - ratio));
				green = (int) (c4.getGreen() * ratio + c3.getGreen() * (1 - ratio));
				blue = (int) (c4.getBlue() * ratio + c3.getBlue() * (1 - ratio));
			} else {
				red = (int) (c3.getRed() * ratio + c2.getRed() * (1 - ratio));
				green = (int) (c3.getGreen() * ratio + c2.getGreen() * (1 - ratio));
				blue = (int) (c3.getBlue() * ratio + c2.getBlue() * (1 - ratio));
			}
		}

		c = new Color(red, green, blue);

		return c;

	}

	Vector<Graph> backup_graph = new Vector<Graph>();
	Vector<Node> backup_nodes = new Vector<Node>();
	Vector<Edge> backup_edge = new Vector<Edge>();

	public void analyzeGraph() {

		for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
			Node node = graph.getNode(it.next().getLabel());

			if (!qdhash.cointains(node.getLabel())) {

				Color c = Color.LIGHT_GRAY;
				node.setColor(c);
				node.setSize(5);

				graph.setNode(node);

			}

			if (baits.cointains(node.getLabel())) {
				node.setSize(18);
				node.setColor(Color.YELLOW);
				graph.setNode(node);
			}

		}

		updateNodes();

	}

	public void report(File f) {

		try {
			Writer output = new BufferedWriter(new FileWriter(f));

			String outstring = "Nodes\tCond1\tCond2\n";

			output.write(outstring);

			for (Iterator<Node> it = graph.nodesIterator(); it.hasNext();) {
				Node node = graph.getNode(it.next().getLabel());
				double v1 = qdhash.getInput(node.getLabel());
				double v2 = qdhash.getExp(node.getLabel());

				outstring = node.getLabel() + "\t" + v1 + "\t" + v2 + "\n";

				output.write(outstring);

			}

			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void removeNode(Node n) {

		backup_nodes.add(n);
		Graph g = graph.subGraph(n);
		backup_edge.addAll(g.getEdges());

		for (int i = 0; i < backup_edge.size(); i++) {

			Edge e = backup_edge.get(i);
			graph.getEdges().remove(e); // need to update node connections

		}

		graph.removeNodefromQuant(n);

	}

	public void resetGraph() {

		for (int i = 0; i < backup_graph.size(); i++) {
			graph.addGraph(backup_graph.elementAt(i));
		}
		updateNodes();

		backup_edge.clear();
		backup_nodes.clear();

	}

	public Hashtable<String, int[]> getRadiusRanges() {

		Hashtable<String, int[]> ranges = new Hashtable<String, int[]>();

		int total = graph.getNodeSize();
		int previous = 0;
		int fullcircle = protein_onthology.size();

		for (Enumeration<String> e = protein_onthology.keys(); e.hasMoreElements();) {

			String function = e.nextElement();

			int portion = protein_onthology.get(function);
			double ratio = (double) portion / (double) total;

			int[] range = new int[2];
			range[0] = (int) (ratio * fullcircle);
			range[1] = previous;

			if (range[0] < 1)
				range[0] = 1;

			ranges.put(function, range);

			previous += range[0];

		}

		return ranges;

	}

	Vector<String> prot_list = new Vector<String>();

	private void sortradial(String root) {

		int[][] interactions = new int[protein_onthology.size() + 1][protein_onthology.size() + 1];
		int k = 0;

		String[] prot_ont_list = new String[protein_onthology.size() + 1];

		for (Edge e : graph.getEdges()) {

			if (!e.contains(root)) {

				String a = graph.getNode(e.n1).getAnnotation();
				int val = protein_onthology.get(a);
				if (val >= protein_onthology.size()) {
					int g = 1;
				}

				String b = graph.getNode(e.n2).getAnnotation();
				int val1 = protein_onthology.get(b);
				if (val1 >= protein_onthology.size()) {
					int j = 1;
				}

				if (!a.equals(b)) {
					interactions[val][val1]++;
					k++;
					prot_ont_list[val] = a;
					prot_ont_list[val1] = b;
				}
			}

		}

		initializeMatrix(interactions);

		double[] sorts = new double[3];

		do {
			sorts = findMax();
			mergeMatrix(sorts);
		} while (sorts[2] > 1.0);

		int i = 1;
		for (int j = 0; j < order.size(); j++) {
			String[] clust = order.elementAt(j).split(",");
			for (int c = 0; c < clust.length; c++) {
				protein_onthology.put(clust[c], i + c);
			}
			for (int t = 0; t < prot_list.size(); t++) {
				if (prot_list.elementAt(t).equals(clust[0])) {
					prot_list.remove(t);
					t--;
				}
			}
			i = clust.length;
		}

		for (int j = 0; j < prot_list.size(); j++) {
			protein_onthology.put(prot_list.elementAt(j), i + j);
		}

		i = 10;

	}

	Vector<String> order = new Vector<String>();
	ArrayList<ArrayList> cluster;

	private void initializeMatrix(int[][] interactions) {

		cluster = new ArrayList<ArrayList>();

		for (int i = 0; i < interactions.length; i++) {

			ArrayList<Integer> tmp = new ArrayList<Integer>();

			for (int j = 0; j < interactions[i].length; j++) {

				tmp.add(interactions[i][j]);
			}

			cluster.add(tmp);

		}

	}

	@SuppressWarnings("unchecked")
	private void mergeMatrix(double[] val) {

		ArrayList<Integer> tmp = cluster.get((int) val[0]);
		ArrayList<Integer> tmp1 = cluster.get((int) val[1]);

		String name1 = prot_list.elementAt((int) val[0]);
		String name2 = prot_list.elementAt((int) val[1]);

		boolean inserted = false;
		boolean already = false;
		for (int i = 0; i < order.size(); i++) {
			if (order.elementAt(i).contains(name1)) {
				String element = order.elementAt(i);

				for (int j = 0; j < i; j++) {
					if (order.elementAt(j).contains(name2)) {
						element += "," + order.elementAt(j);
						order.set(i, element);
						inserted = true;
						already = true;
						prot_list.remove((int) val[1]);
						order.remove(j);
						i--;
						break;
					}
				}
				if (!already) {
					element += "," + name2;//// order.elementAt(i-1);//name2;
					order.set(i, element);
					inserted = true;
					prot_list.remove((int) val[1]);
					break;
				}
				break;
			}
			if (order.elementAt(i).contains(name2)) {
				String element = order.elementAt(i);

				for (int j = 0; j < i; j++) {
					if (order.elementAt(j).contains(name1)) {
						element += "," + order.elementAt(j);
						order.set(i, element);
						inserted = true;
						already = true;
						prot_list.remove((int) val[1]);
						order.remove(j);
						i--;
						break;
					}
				}
				if (!already) {
					element += "," + name1;//// order.elementAt(i-1);//name2;
					order.set(i, element);
					inserted = true;
					prot_list.remove((int) val[0]);
					break;
				}
				break;
			}
		}
		if (!inserted) {
			String element = name1 + "," + name2;
			order.add(element);
			prot_list.remove((int) val[1]);
		}

		for (int i = 0; i < tmp.size(); i++) {

			int value = tmp.get(i) + tmp1.get(i);

			tmp.set(i, value);
		}

		cluster.remove((int) val[1]);

		for (int i = 0; i < cluster.size(); i++) {

			cluster.get(i).remove((int) val[1]);

		}

		int k = 1;

	}

	private double[] findMax() {

		double[] max = new double[3];
		max[2] = -100;

		for (int i = 1; i < cluster.size(); i++) {

			ArrayList<Integer> tmp = cluster.get(i);

			for (int j = 1; j < tmp.size(); j++) { // skip ones!!!!

				if (i != j) {
					int value = tmp.get(j);

					if (value > max[2]) {
						max[0] = i;
						max[1] = j;
						max[2] = value;

					}
				}

			}
		}

		if (max[0] > max[1]) {
			double tmp = max[0];
			max[0] = max[1];
			max[1] = tmp;
		}

		return max;
	}

	/**
	 * find the position of the node in the radial graph
	 *
	 * @param w
	 * @param h
	 * @param root
	 */
	public void radial(int w, int h, String root) {

		// Node n = graph.getNode(root);
		// n.setX(w/2);
		// n.setY(h/2);
		double radius = 10;
		Random r = new Random();

		// Hashtable <String, int[] > radius_range = new Hashtable<String,
		// int[]>();

		// radius_range = getRadiusRanges();

		// sortradial(root);

		int slices = 360 / (protein_onthology.size());

		List<Edge> edg = graph.getEdges();

		for (int i = 0; i < edg.size(); i++) {

			if (edg.get(i).getType() == 1) {

				double conf = edg.get(i).getConf();
				if (conf > 0) {
					radius = (conf * -1.0 + 1.1) * 300;

				} else {
					radius = 305;
				}

				// Node n = edg.get(i).getComplement(root);

				String a = graph.getNode(edg.get(i).n2).getAnnotation();

				double val = protein_onthology.get(a);

				val = val * slices;
				val = Math.toRadians(val);

				// double val = r.nextInt(range[0])+range[1];
				// double val = r.nextInt(245);
				double x = 3 * w / 8 + radius * Math.cos(val);
				double y = 3 * h / 8 + radius * Math.sin(val);

				graph.getNode(edg.get(i).n1).setXY(3 * w / 8, 3 * h / 8);
				graph.getNode(edg.get(i).n2).setXY(x, y);

				graph.getNode(edg.get(i).n1).setShape(4);
				graph.getNode(edg.get(i).n2).setShape(4);

				int R, G, B;

				R = (int) (255 * conf);
				G = 0;
				B = (int) (255 * (1 - conf));

				graph.getNode(edg.get(i).n2).setColor(new Color(R, G, B));

			}

			// to do else

			//

			// Node n1 = e.getComplement(e.n1);

			// radius = 1.0;//3.0/(Math.log10(conf+1));

			//

			// n1.setX(w/2);
			// n1.setY(h/2);

			// n1.setX(radius * Math.cos(val));
			// n1.setY(radius * Math.sin(val));

			// }

		}

	}

	public boolean centerAround(String s) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean connBait(String s) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean connnode(String s, String s1) {
		// TODO Auto-generated method stub
		return false;
	}
}
