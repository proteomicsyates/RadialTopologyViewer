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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.ToolTipManager;

import netQuant.DemoTools;
import netQuant.NetQuantSettings;
import netQuant.graph.Graph;
import netQuant.graph.Node;
import netQuant.graph.UniqueEdge;

/**
 * A panel that handles graphs, loads and saves them (I may move this to a
 * higher level soon) and displays them
 *
 * Most often, any extending classes will override the display methods,
 * especially <CODE>paintNode</CODE> and <CODE>paintEdge</CODE>.
 *
 * @author Sean Hooper
 */
public class BasicGraphPanel extends BasicGraphRenderer implements MouseListener, MouseMotionListener, Runnable {

	/**
	 * Starts the relax thread, applying repelling forces between nodes and
	 * attracting forces between nodes connected by edges.
	 */
	protected Thread relaxThread;

	// the graph object

	/**
	 * todo: move all load functions to the frame!!! enough to set the graph
	 */
	public void setGraph(Graph g) {
		graph = g;
		updateNodes();
	}

	/**
	 * Updates the nodes for quicker repainting
	 */
	public void updateNodes() {
		calculateEdgeLength();
		// nodeArray=graph.nodeArray();
		// nnodes=graph.getNodeSize();
	}

	// The State switches

	public boolean running = false;

	/**
	 * If true, only the moving nodes will be displayed. This can be good if you
	 * have a large graph, since repainting will occur.
	 *
	 * @param trueOrFalse
	 *            True if hide, False (default) if show all nodes while moving
	 */
	public void setHideWhenMove(boolean trueOrFalse) {
		hideWhenMove = trueOrFalse;
	}

	/**
	 * Setting this to true results in a composite effect on edges based on the
	 * confidence level of the edge.
	 *
	 * @param trueOrFalse
	 *            True if edges are displayed with alpha
	 */
	public void setAlpha(boolean trueOrFalse) {
		alpha = trueOrFalse;
	}

	// used for marking a node with a pointer
	Node marked = null;

	// the initial font

	// change font size
	/**
	 * Set font size
	 *
	 * @param size
	 *            Font size
	 */
	public void changeFont(int size) {
		nodeFont = new Font("TimesNewRoman", Font.PLAIN, size);
		fontSize = size;
	}

	// Node size, was final, but we decided to make it settable

	public void setNodeSize(int nodeSize, String n) {

		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			if (node.getLabel().equals(n)) {
				node.setSize(nodeSize);
			}
		}

	}

	/**
	 * Set node size
	 *
	 * @param nodeSize
	 *            Node size in pixels
	 */
	public void setNodesSize(int nodeSize) {
		if (nodeSize < 1)
			nodeSize = 10;
		this.nodeSize = nodeSize;

		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			node.setSize(this.nodeSize);

		}

		correct = (int) (nodeSize / 2.);

	}

	// correction for node size
	// protected Color

	// the progress bar for use in spring
	ProgressMonitor energyBar;
	ActionListener updateEnergyBar;
	javax.swing.Timer timer;

	// popup menu for editing
	// PopupMenu popup;

	/**
	 * Background color
	 *
	 * @param c
	 *            Color c
	 */
	public void setBackgroundColor(Color c) {
		setBackground(c);
	}

	/*
	 * Composite is used for fading effects
	 */

	/*
	 * returns the color as a string for the save file
	 */
	/**
	 * Gets a color as a string
	 *
	 * @param color
	 *            Color color
	 * @return String representation of color
	 */
	public String colorToString(Color color) {
		return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
	}

	/* These settings can be toggled ----------------------- */
	/**
	 * If <I>pretty</I> is selected, multiple edges will be drawn in colors. If
	 * not selected, edges are drawn as unique edges and in grey.
	 *
	 * @param trueOrFalse
	 *            Boolean pretty
	 */
	public void setPretty(boolean trueOrFalse) {
		pretty = trueOrFalse;
	}

	/**
	 * Set <CODE>true</CODE> if you want to show edges as directed.
	 *
	 * @param trueOrFalse
	 *            Boolean arrows or not
	 */
	public void setArrows(boolean trueOrFalse) {
		arrow = trueOrFalse;
	}

	/**
	 * Show labels or hide them
	 *
	 * @param trueOrFalse
	 *            Boolean true if show labels
	 */
	public void setLabel(boolean trueOrFalse) {
		label = trueOrFalse;
	}

	/**
	 * Show confidence of edges using either alpha or patterns
	 *
	 * @param trueOrFalse
	 *            true if confidence is to be shown
	 */
	public void setConfidence(boolean trueOrFalse) {
		showConfidence = trueOrFalse;
	}

	/**
	 * Show annotation instead of node names
	 *
	 * @param trueOrFalse
	 *            true: shows annotation. false: shows node label
	 */
	public void setAnnotation(boolean trueOrFalse) {
		showAnnotation = trueOrFalse;
	}

	/**
	 * Sets the basic edge color.
	 *
	 * @param color
	 *            The color that will be used when interactions are not shown
	 */
	public void setBasicEdgeColor(Color color) {
		basicEdgeColor = color;
	}

	/**
	 * Sets the font color
	 *
	 * @param color
	 *            Font color
	 */
	public void setFontColor(Color color) {
		fontColor = color;
	}

	/**
	 * Offset for drawing labels
	 */
	public static final double offset = 0.3;

	// These functions communicate with the StringletPanelSettings object
	/**
	 * Returns the color for interaction <I>i</I>
	 *
	 * @param i
	 *            Interaction type
	 * @return The color as defined
	 */
	public Color getColor(Integer i) {
		return getStringSettings().getColor(i);
	}

	/**
	 * Sets the color for interaction <I>i</I> in <CODE>MedusaSettings</CODE>.
	 * This should really be handled at a higher level...
	 *
	 * @param i
	 *            Interaction type
	 * @param color
	 *            Edge color for interaction i
	 */
	public void setColor(Integer i, Color color) {
		getStringSettings().setColor(i, color);
	}

	double edgeLen; // the length for relaxation

	/**
	 * Returns the current edge length
	 *
	 * @return edge length
	 */
	public double getEdgeLen() {
		return edgeLen;
	}

	// calculate the length
	public void calculateEdgeLength() {
		int nnodes = graph.getNodeSize();
		if (nnodes > 0)
			edgeLen = Math.sqrt(getPanelWidth() * getPanelHeight() / nnodes);
	}

	/**
	 * Manually sets the edge length. This is the steady-state length between
	 * two nodes. Don't expect this as an exact value, since most graphs are
	 * more complex than two nodes connected by an edge.
	 *
	 * @param newEdgeLen
	 *            New edge length
	 */
	public void setEdgeLen(double newEdgeLen) {
		if (newEdgeLen > 0)
			edgeLen = newEdgeLen;
		else
			calculateEdgeLength();
	}

	// scaler variable
	protected double scale = 1.5;
	protected double absoluteScale = 1.0;

	/**
	 * Sets the scale of the graph. large scales will zoom in on the graph,
	 * while scales < 1 zoom out.
	 *
	 * @param scale
	 *            scale factor
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setAbsoluteScale(double scale) {
		absoluteScale = scale;
	}

	/**
	 * Sets the x and y parameters for the drawing area
	 *
	 * @param x
	 *            width
	 * @param y
	 *            height
	 */
	public void setTimeFrameXY(int x, int y) {

		setTimeFrameXY(new Dimension(x, y));
	}

	/**
	 *
	 * @param d
	 */
	public void setTimeFrameXY(Dimension d) {
		dims = d;
		// setSize(d);
		setPanelWidth((int) (getPanelWidth() * scale));
		setPanelHeight((int) (getPanelHeight() * scale));
		panelWidth = (int) (d.getWidth() * scale);
		panelHeight = (int) (d.getHeight() * scale);
		rescaleNodes();
		calculateEdgeLength();
		// setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		dims = new Dimension(panelWidth, panelHeight);
		setPreferredSize(new Dimension(panelWidth, panelHeight));
		setSize(new Dimension(panelWidth, panelHeight));
		// setMinimumSize(new Dimension(getPanelWidth(), getPanelHeight()));
	}

	// Tool tips, if labels are off
	@Override
	public String getToolTipText(MouseEvent e) {
		Node node = getClosest(e);
		if (node == null)
			return null;
		// System.out.println(label+" "+showAnnotation);
		/*
		 * if (!label) { return node.getLabel(); }
		 */
		if (showAnnotation) {
			return node.getLabel();
		}

		return node.getAnnotation();
	}

	// Set orientations, etc in one go
	/**
	 * Inits edges, fixes orientation
	 */
	public void initEdges() {
		autoFixOrientation();
		// was fixOrientation();
		calculateEdgeLength();
	}

	// adjust the node x and y according to the new panel sizes
	private void rescaleNodes() {
		graph.rescaleNodes(scale);
	}

	/**
	 * Sets new <CODE>MedusaSettings</CODE>
	 *
	 * @param ss
	 *            new settings
	 */
	public void updateStringSettings(NetQuantSettings ss) {
		setStringSettings(ss);
	}

	/*
	 * Constructor
	 */
	/**
	 * Constructs a new <CODE>BasicGraphPanel</CODE> with settings
	 *
	 * @param stringSettings
	 *            settings
	 */
	public BasicGraphPanel(NetQuantSettings stringSettings) {
		graph = new Graph();
		setBackground(Color.white);
		// setOpaque(true);
		setOpaque(false);

		edgeLen = 100.; // play safe!
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		addMouseListener(this);
		setStringSettings(stringSettings);

		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(1000000);
		setToolTipText("");

	}

	/*
	 * Constructor with default medusasettings
	 */
	public BasicGraphPanel() {
		this((new NetQuantSettings()));

	}

	// The run function for relaxThread
	@Override
	public void run() {
		Thread myThread = Thread.currentThread();
		temperature = INITIAL_TEMP;
		resetDelta();
		// double rand=Math.random();
		// boolean grav=(rand<0.2);
		running = true;
		while (relaxThread == myThread) {

			relax2();

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				running = false;
				break; // back to work
			}
		}
		running = false;
		// repaint();
	}

	// thread is not running. start it here
	public void start() {
		if (relaxThread == null)
			relaxThread = new Thread(this);
		relaxThread.start();
	}

	// kill it
	public void stop() {
		if (relaxThread != null)
			relaxThread = null;
	}

	/*
	 * Implement the FR algorithm. This uses the FRspring class Starts the
	 * automatic layout
	 */
	public synchronized void energy() {
		// Dimension d = getSize();
		energyBar = new ProgressMonitor(this, "Fruchterman Rheingold Algorithm", "", 0, 200);
		frSpring = new FRspring(graph, getPanelWidth(), getPanelHeight());
		timer = new javax.swing.Timer(500, runFR);
		energyBar.setProgress(0);
		energyBar.setMillisToDecideToPopup(1000);
		frSpring.start();
		// this timer will update the map
		timer.start();

	}

	protected FRspring frSpring;
	/*
	 * Implement an actionPerformed that updates the progress bar
	 */
	protected ActionListener runFR = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			energyBar.setProgress(frSpring.getCurrent());
			// System.out.println(frSpring.getCurrent());
			if (energyBar.isCanceled() || frSpring.isDone()) {
				timer.stop();
				frSpring.stop();
				energyBar.close();
				// energyBar.setProgress(0);
				// nodes=frSpring.getNodes();
				setCursor(null);
				repaint();

			}
		}
	};

	public synchronized void scoreLayout() {

		energyBar = new ProgressMonitor(this, "Score Layout", "", 0, graph.getEdgeSize());
		sLayout = new sLayout(graph, getPanelWidth(), getPanelHeight());
		timer = new javax.swing.Timer(0, runSL);
		energyBar.setProgress(0);
		energyBar.setMillisToDecideToPopup(1000);
		sLayout.start();
		timer.start();

	}

	protected sLayout sLayout;

	protected ActionListener runSL = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			energyBar.setProgress(sLayout.getCurrent());
			if (energyBar.isCanceled() || sLayout.isDone()) {
				timer.stop();
				sLayout.stop();
				energyBar.close();
				// energyBar.setProgress(0);
				// nodes=frSpring.getNodes();
				setCursor(null);
				repaint();
			}

		}
	};

	public synchronized void complexLayout() {

		energyBar = new ProgressMonitor(this, "Protein Complex Layout", "", 0, graph.getNodeSize() * 3);
		cLayout = new CLayout(graph, getPanelWidth(), getPanelHeight());
		timer = new javax.swing.Timer(500, runCL);
		energyBar.setProgress(0);
		energyBar.setMillisToDecideToPopup(1000);
		cLayout.start();
		timer.start();

	}

	protected CLayout cLayout;

	protected ActionListener runCL = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			energyBar.setProgress(cLayout.getCurrent());
			if (energyBar.isCanceled() || cLayout.isDone()) {
				timer.stop();
				cLayout.stop();
				energyBar.close();
				// energyBar.setProgress(0);
				// nodes=frSpring.getNodes();
				setCursor(null);
				repaint();
			}

		}
	};

	public synchronized void confidenceExtractor() {

		energyBar = new ProgressMonitor(this, "Protein Confidence Extractor", "", 0, graph.getNodeSize() * 3);

		String s = JOptionPane.showInputDialog("Insert confidence root node:\n" + "e.g. \"CAB39\"", "");

		cExtractor = new cExtractor(graph, s);
		timer = new javax.swing.Timer(500, runCE);
		energyBar.setProgress(0);
		energyBar.setMillisToDecideToPopup(1000);
		cExtractor.start();
		timer.start();

	}

	protected cExtractor cExtractor;

	protected ActionListener runCE = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			energyBar.setProgress(cExtractor.getCurrent());
			if (energyBar.isCanceled() || cExtractor.isDone()) {
				timer.stop();
				cExtractor.stop();
				energyBar.close();
				// energyBar.setProgress(0);
				// nodes=frSpring.getNodes();
				setCursor(null);
				repaint();
			}

		}
	};

	/*
	 * Paint the panel. is called with repaint. Try without synch
	 */

	// fix all or none
	/**
	 * Fix or unfix all nodes
	 *
	 * @param trueOrFalse
	 *            true fixes all nodes. false unfixes all
	 */
	public void setFix(boolean trueOrFalse) {
		for (Iterator nodes = graph.nodesIterator(); nodes.hasNext();) {
			Node n = (Node) nodes.next();
			n.setFixed(trueOrFalse);
		}
		repaint();
	}

	// Invert fixation
	public void invertFix() {
		for (Iterator nodes = graph.nodesIterator(); nodes.hasNext();) {
			Node n = (Node) nodes.next();
			n.setFixed(!n.isFixed());
		}
		repaint();
	}

	protected void edgeAttract() {
		for (Iterator uniqueEdges = graph.uniqueEdgesIterator(); uniqueEdges.hasNext();) {
			UniqueEdge e = (UniqueEdge) uniqueEdges.next();
			Node n1 = graph.getNode(e.getFromName());
			Node n2 = graph.getNode(e.getToName());
			double vx = n2.getX() - n1.getX();
			double vy = n2.getY() - n1.getY();
			double len = Math.sqrt(vx * vx + vy * vy);
			// double len = Point.distance(n1.getX(),n1.getY(), n2.getX(),
			// n2.getY());
			len = (len == 0) ? .0001 : len;
			// try this (len * 3)
			double f = (edgeLen - len) / (len * 3);
			double dx = f * vx;
			double dy = f * vy;

			n2.setDX(n2.getDX() + dx);
			n2.setDY(n2.getDY() + dy);
			n1.setDX(n1.getDX() + -dx);
			n1.setDY(n1.getDY() + -dy);
		}
	}

	// edges attract
	protected void edgeAttract2() {
		for (Iterator uniqueEdges = graph.uniqueEdgesIterator(); uniqueEdges.hasNext();) {
			UniqueEdge e = (UniqueEdge) uniqueEdges.next();
			Node n1 = graph.getNode(e.getFromName());
			Node n2 = graph.getNode(e.getToName());
			double vx = n2.getX() - n1.getX();
			double vy = n2.getY() - n1.getY();
			double len = Math.sqrt(vx * vx + vy * vy);
			// double len = Point.distance(n1.getX(),n1.getY(), n2.getX(),
			// n2.getY());
			len = (len == 0) ? .00001 : len;
			// try this (len * 3)
			// double f = (edgeLen - len);// / (len*3);
			// double dx = f * vx;
			// double dy = f * vy;
			double dx = -len * vx;
			double dy = -len * vy;
			// System.out.println("len: "+len);
			// System.out.println("vx: "+vx+" vy:"+vy);
			// System.out.println("dx: "+dx+" dy:"+dy);
			n2.setDX(n2.getDX() + dx);
			n2.setDY(n2.getDY() + dy);
			n1.setDX(n1.getDX() + -dx);
			n1.setDY(n1.getDY() + -dy);
		}
	}

	protected void nodesRepel2(final double critXDistance, final double critYDistance) {

		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			double dx = 0;
			double dy = 0;
			for (Iterator it2 = graph.nodesIterator(); it2.hasNext();) {
				Node node2 = (Node) it2.next();
				if (node.equals(node2)) {
					continue;
				}
				double vx = node.getX() - node2.getX();
				double vy = node.getY() - node2.getY();

				// put in four cases here, corresponding to how the
				// distance is measured

				if (vx > critXDistance) { // repel over the x border right
					vx = node.getX() - node2.getX();// - panelWidth;
				}
				if (vx < -critXDistance) {// repel over the x border left
					vx = node.getX() - node2.getX();// + panelWidth;
				}

				if (vy > critYDistance) {// repel over the y border down
					vy = node.getY() - node2.getY();// - panelHeight;
				}
				if (vy < -critYDistance) {// repel over the y border up
					vy = node.getY() - node2.getY();// + panelHeight;
				}

				double len = Math.sqrt(vx * vx + vy * vy);

				// move to "move nodes"
				if (len == 0.0) { // don't allow nodes on top of eachother
					dx += Math.random() / 100;
					dy += Math.random() / 100;
				} else
					// if (len<panelWidth*panelHeight/9){ // end repelling
					if (len < 0.1) {
					// when nodes are far apart
					dx += vx / len;
					dy += vy / len;
				}
			}
			// double dlen = dx * dx + dy * dy;
			// if (dlen > 0.) {
			// was / 2
			// dlen = Math.sqrt(dlen) / 3;
			// node.setDX(node.getDX() + dx / dlen);
			// node.setDY(node.getDY() + dy / dlen);
			// }
			node.setDX(node.getDX() + dx);
			node.setDY(node.getDY() + dy);
		}
	}

	protected void nodesRepel(final double critXDistance, final double critYDistance) {

		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			double dx = 0;
			double dy = 0;
			for (Iterator it2 = graph.nodesIterator(); it2.hasNext();) {
				Node node2 = (Node) it2.next();
				if (node.equals(node2)) {
					continue;
				}
				double vx = node.getX() - node2.getX();
				double vy = node.getY() - node2.getY();

				// put in four cases here, corresponding to how the
				// distance is measured

				if (vx > critXDistance) { // repel over the x border right
					vx = node.getX() - node2.getX() - getPanelWidth();
				}
				if (vx < -critXDistance) {// repel over the x border left
					vx = node.getX() - node2.getX() + getPanelWidth();
				}

				if (vy > critYDistance) {// repel over the y border down
					vy = node.getY() - node2.getY() - getPanelHeight();
				}
				if (vy < -critYDistance) {// repel over the y border up
					vy = node.getY() - node2.getY() + getPanelHeight();
				}

				double len = vx * vx + vy * vy;

				if (len == 0) { // don't allow nodes on top of eachother
					dx += Math.random();
					dy += Math.random();
				} else if (len < getPanelWidth() * getPanelHeight() / 9) { // end
																			// repelling
					// when nodes are far apart
					dx += vx / len;
					dy += vy / len;
				}
			}
			double dlen = dx * dx + dy * dy;
			if (dlen > 0.) {
				// was / 2
				dlen = Math.sqrt(dlen) / 3;
				node.setDX(node.getDX() + dx / dlen);
				node.setDY(node.getDY() + dy / dlen);
			}
		}
	}

	/*
	 * This relax funtion repels nodes over borders in order to get a fairly
	 * even distribution
	 */
	protected synchronized void relax2() {
		int border = 10;
		int labelPadding = 10;
		double critXDistance = getPanelWidth() * 3 / 4;
		double critYDistance = getPanelHeight() * 3 / 4;

		// attraction from edges
		edgeAttract();

		// repellation from nodes
		nodesRepel(critXDistance, critYDistance);

		// move nodes
		moveNodes(border, labelPadding);

		repaint();
		if ((cool) & (coolToggle)) {
			temperature -= 0.02;
			temperature = Math.max(temperature, 0.0);
		}
	}

	protected void moveNodes2(int border, int labelPadding) {
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			// quick cool down if too small
			double limit = 0.001;
			if (Math.abs(node.getDX()) < limit) {
				node.setDX(0);
			}
			if (Math.abs(node.getDY()) < limit) {
				node.setDY(0);
			}
			if ((cool) & (coolToggle)) {
				node.setDX(node.getDX() * temperature);
				node.setDY(node.getDY() * temperature);
			}

			// move all unfixed nodes
			if (!node.isFixed()) {
				node.setX(node.getX() + node.getDX());
				node.setY(node.getY() + node.getDY());
			}

			node.setDX(node.getDX() / 2);
			node.setDY(node.getDY() / 2);
		}

	}

	protected void moveNodes(int border, int labelPadding) {
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			// quick cool down if too small
			if (Math.abs(node.getDX()) < 1) {
				node.setDX(0);
			}
			if (Math.abs(node.getDY()) < 1) {
				node.setDY(0);
			}
			if ((cool) & (coolToggle)) {
				node.setDX(node.getDX() * temperature);
				node.setDY(node.getDY() * temperature);
			}

			// move all unfixed nodes
			if (!node.isFixed()) {
				node.setX(node.getX() + Math.max(-5, Math.min(5, node.getDX())));
				node.setY(node.getY() + Math.max(-5, Math.min(5, node.getDY())));
			}

			// make sure they stay in their borders
			if (node.getX() < border) {
				node.setX(border);
			} else if (node.getX() > getPanelWidth() - 2 * border) {
				node.setX(getPanelWidth() - 2 * border);
			}
			if (node.getY() < border + labelPadding) {
				node.setY(border + labelPadding);
			} else if (node.getY() > getPanelHeight() - border) {
				node.setY(getPanelHeight() - border);
			}

			node.setDX(node.getDX() / 2);
			node.setDY(node.getDY() / 2);
		}
	}

	static final double INITIAL_TEMP = 1.0;
	double temperature = INITIAL_TEMP;
	boolean cool = true;
	// settable option for cooling
	boolean coolToggle = false;

	/**
	 * Allows cooling of relaxation. If cooling is on, attraction and
	 * repellation energies will slow if the mouse is not dragging nodes.
	 *
	 * @param trueOrFalse
	 *            true: cooling
	 */
	public void setCool(boolean trueOrFalse) {
		coolToggle = trueOrFalse;
	}

	private void resetDelta() {
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			node.setDY(0);
			node.setDX(0);
		}
	}

	protected synchronized void gravity() {
		// calculate dy
		double g = 9.81;
		double dt = 0.1;
		double temp = g * dt;
		double cooldown = 0.9;
		int yLimit = 600;
		// get velocity & move nodes
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			if (!node.isFixed()) {
				node.setDY(node.getDY() + temp);
				node.setY(node.getY() + 0.5 * temp * dt + node.getDY() * dt);
				if (node.getY() > 600) {
					// n1.dy =- n1.dy * cooldown;
					node.setDY(-node.getDY() * cooldown);
					node.setY(600);
				}
			}
		}
		repaint();
	}

	/**
	 * Rotates graph
	 *
	 * @param t
	 *            rotation factor
	 */
	public void rotate(double t) {
		// loop through all nodes and rotate
		double x, y, cos, sin;
		double pw2 = getPanelWidth() / 2;
		double ph2 = getPanelHeight() / 2;
		cos = Math.cos(t);
		sin = Math.sin(t);
		// System.out.println(cos+ " "+sin);
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			x = node.getX() - pw2;
			y = node.getY() - ph2;
			node.setX(x * cos - y * sin + pw2);
			node.setY(x * sin + y * cos + ph2);
			// System.out.println(x+" "+y+" "+" "+node.getX());
		}

		// repaint();
	}

	/**
	 * Mirrors graph vertically or horizontally (or both)
	 *
	 * @param xFlip
	 *            true if flip
	 * @param yFlip
	 *            true if flip
	 */
	public void mirror(boolean xFlip, boolean yFlip) {
		// loop through all nodes and rotate
		double x, y;

		// flip x
		if (xFlip)
			for (Iterator it = graph.nodesIterator(); it.hasNext();) {
				Node node = (Node) it.next();
				node.setX(getPanelWidth() - node.getX());
			}
		// flip y
		if (yFlip)
			for (Iterator it = graph.nodesIterator(); it.hasNext();) {
				Node node = (Node) it.next();
				node.setY(getPanelWidth() - node.getY());
			}

		// repaint();
	}

	// change the color of the selected nodes
	public void changeNodeColor(int red, int green, int blue) {
		Color color = new Color(red, green, blue);
		for (Iterator nodes = graph.nodesIterator(); nodes.hasNext();) {
			Node n = (Node) nodes.next();
			if (n.isFixed())
				n.setColor(color);
		}
		// graph.setNodeColor(
	}

	public void changeNodeColor(Color color) {
		for (Iterator nodes = graph.nodesIterator(); nodes.hasNext();) {
			Node n = (Node) nodes.next();
			if (n.isFixed())
				n.setColor(color);
		}

	}

	/**
	 * Saves graph as an image
	 *
	 * @param path
	 *            file to save as
	 * @param param
	 *            type of picture. Currently, 0=jpg 1=png
	 */
	public void saveImage(String path, int param) {
		// param = 0 for jpg, = 1 for png
		Component comp = this;
		// int w = panelWidth;
		// int h = panelHeight;
		Dimension d = getSize();
		int w = (int) d.getWidth();
		int h = (int) d.getHeight();
		BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = im.createGraphics();
		comp.paint(g2);
		g2.dispose();
		// im contains pixel data, now

		// standard code:
		try {
			File file = new File(path);
			// write to jpg
			if (param == 0) {
				javax.imageio.ImageIO.write(im, "jpg", file);
			}
			// write to png
			if (param == 1) {
				javax.imageio.ImageIO.write(im, "png", file);
			}

			im.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setNodeFixed(String lbl, boolean fixed) {
		graph.getNode(lbl).setFixed(fixed);
	}

	// return the index of the closest node

	protected Node getClosest(MouseEvent e) {
		int radialDistance = 50;
		int index = -1;
		Node bestNode = null;
		double bestdist = Double.MAX_VALUE;
		int x = e.getX();
		int y = e.getY();

		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			double dist = (node.getX() - x) * (node.getX() - x) + (node.getY() - y) * (node.getY() - y);

			if ((dist < radialDistance) & (dist < bestdist)) {
				// pick = n;
				bestdist = dist;
				// index=i;
				bestNode = node;
			}
		}
		return bestNode;
	}

	// Take care of events, listen to mouse actions
	Node pick;
	boolean pickfixed;

	@Override
	public void mouseClicked(MouseEvent e) {
		// System.out.println("click!");

	}

	// two ints for movement of clusters
	double oldX = 0.;
	double oldY = 0.;
	// boolean shift=false;

	// Set up Rectangle objects for drag-selection
	Rectangle currentRect = null;
	Rectangle rectToDraw = null;
	Rectangle previousRectDrawn = new Rectangle();
	boolean box; // is the user creating a box selection?

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			handlePressLeftButton(e);
		}

	}

	public void handlePressLeftButton(MouseEvent e) {
		// If we have picked a node, we will not pick it again.
		if (pick != null) {
			return;
		}
		Node node = getClosest(e);
		// Several actions are possible.
		// --------------------------
		// Check control to toggle pick
		if (e.isControlDown()) {
			if (node != null) {
				node.setFixed((!node.isFixed()));
			}
			pick = null;
			e.consume();
			return;
		}

		if (e.isShiftDown()) {
			addMouseMotionListener(this);
			if (node != null) {
				setPick(node, e);
			}
			e.consume();
			return;
		}

		// --------------------------
		// Check that we have no modifiers

		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			addMouseMotionListener(this);
			// now check if we have clicked a node
			if (node != null) {

				// pick up this node
				setPick(node, e);

			} else {
				// we have no node. draw a selection box
				int x = e.getX();
				int y = e.getY();
				currentRect = new Rectangle(x, y, 0, 0);
				updateDrawableRect(getWidth(), getHeight());
				repaint();
			}
			e.consume();
			return;
		}

	}

	private void setPick(final Node node, final MouseEvent e) {
		// pick up this node
		pick = node;
		// if hideWhenMove is selected, set the visibleGraph
		if (hideWhenMove)
			visibleGraph = graph.subGraph(node);

		pickfixed = pick.isFixed();
		pick.setFixed(true);
		pick.setX(e.getX());
		pick.setY(e.getY());
		oldX = pick.getX();
		oldY = pick.getY();
		temperature = INITIAL_TEMP;
		cool = false;
	}

	public void handlePressLeftButton_backup(MouseEvent e) {
		if (pick == null) {
			// System.out.println(e.getButton());
			if (e.getButton() == MouseEvent.BUTTON1) {
				// System.out.println("shift: "+InputEvent.SHIFT_DOWN_MASK);

				// System.out.println("modifier: "+e.getModifiers()+"
				// "+e.getMouseModifiersText(e.getModifiers()));

				// sort out the actions once and for all...
				// first check for shift.
				// ****** Shift: move all fixed nodes

				if ((e.getModifiers() == 16) | (e.getModifiers() == 17) | (e.getModifiers() == 18)) {
					// no modifier | or shift
					// prepare to move nodes
					// box=false;
					addMouseMotionListener(this);
					Node node = getClosest(e);
					// int index=getClosest(e);
					if (node != null) {
						// the case where we want to toggle fix
						if (e.getModifiers() == 18) {
							node.setFixed((!node.isFixed()));
							pick = null;
						} else {
							// pick up the pressed node

							pick = node;
							pickfixed = pick.isFixed();
							pick.setFixed(true);
							pick.setX(e.getX());
							pick.setY(e.getY());
							oldX = pick.getX();
							oldY = pick.getY();
							temperature = INITIAL_TEMP;
							cool = false;
						}
						repaint();
					} else { //
						// System.out.println("start a new selection
						// rectangle");
						// box=true;
						int x = e.getX();
						int y = e.getY();
						currentRect = new Rectangle(x, y, 0, 0);
						updateDrawableRect(getWidth(), getHeight());
						repaint();
					}

					e.consume();
				}
			}
		}

	}

	/**
	 * Finds the node matching the regular expression
	 *
	 * @param expression
	 *            pattern to match
	 * @return true if nodes were found, false if no nodes matched the pattern
	 */
	public boolean findLabel(String expression) {
		// marked = null;
		Pattern labelPattern = Pattern.compile(expression);
		Matcher matcher;
		// if selection is "", remove mark
		if (expression.compareTo("") == 0) {
			return false;
		}
		// Else, look for a match
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();

			matcher = labelPattern.matcher(node.getLabel());
			node.setFixed(false);
			if (matcher.find()) {
				// marked = node;
				node.setFixed(true);
				node.setSize(20);
				// return node;
			}
		}
		return true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		handleReleaseLeftButton(e);
		// handleReleaseRightButton(e);
	}

	protected void handleReleaseLeftButton(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		removeMouseMotionListener(this);
		// System.out.println("Released");
		// if (relaxThread!=null)
		// relaxThread.interrupt();

		if (e.getModifiers() == 16) { // no mod
			if (pick != null) {
				// if ((e.getX()<panelWidth) & (e.getY()<panelHeight)){

				pick.setX(e.getX());
				pick.setY(e.getY());
				pick.setFixed(pickfixed);
				cool = true;

			} else { // set rectangle
				updateSize(e);
				fixNodesInRect(rectToDraw);
				currentRect = null;
			}
		}
		if (e.isShiftDown()) { // shift
			if (pick != null) {

				double xMove = e.getX() - oldX;
				double yMove = e.getY() - oldY;
				// move all nodes which are fixed
				moveFixed(xMove, yMove);
				pick.setFixed(pickfixed);

			}
		}
		if (e.isControlDown()) { // ctrl
			if ((pick == null) & (currentRect != null)) {
				// System.out.println("no pick drag");
				updateSize(e);
				unFixNodesInRect(rectToDraw);
				currentRect = null;
			}
			// new
			else if (pick != null) {
				// toggle fix
				// System.out.println("toggle from "+pick.fixed);
				pick.setX(e.getX());
				pick.setY(e.getY());
				pick.setFixed(pick.isFixed() == false);
			}
		}
		pick = null;
		// System.out.println("released: pick null");
		repaint();
		e.consume();
	}

	protected void fixNodesInRect(Rectangle rect) {
		if (rect == null)
			return;
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			// System.out.println("checking nodes");
			// System.out.println("rect: "+rect.toString());
			if (rect.contains(node.getX(), node.getY())) {
				// System.out.println(nodes[i].lbl);
				node.setFixed(true);
			}
		}
	}

	protected void unFixNodesInRect(Rectangle rect) {
		// if (rect==null)
		// return;
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			// System.out.println("checking nodes");
			// System.out.println("rect: "+rect.toString());
			if (rect.contains(node.getX(), node.getY())) {
				// System.out.println(nodes[i].lbl);
				node.setFixed(false);
			}
		}
	}

	/*
	 * updates the size of the selection rectangle. also sends a partial
	 * repaint. there is no need to repaint everything
	 */
	private void updateSize(MouseEvent e) {
		if (currentRect == null)
			return;
		int x = e.getX();
		int y = e.getY();
		currentRect.setSize(x - currentRect.x, y - currentRect.y);
		updateDrawableRect(getPanelWidth(), getPanelHeight());
		Rectangle totalRepaint = rectToDraw.union(previousRectDrawn);
		repaint(totalRepaint.x, totalRepaint.y, totalRepaint.width, totalRepaint.height);
		// repaint();
	}

	/*
	 * updates the drawable rectangle
	 */
	private void updateDrawableRect(int compWidth, int compHeight) {
		int x = currentRect.x;
		int y = currentRect.y;
		int width = currentRect.width;
		int height = currentRect.height;

		// Make the width and height positive, if necessary.
		if (width < 0) {
			width = 0 - width;
			x = x - width + 1;
			if (x < 0) {
				width += x;
				x = 0;
			}
		}
		if (height < 0) {
			height = 0 - height;
			y = y - height + 1;
			if (y < 0) {
				height += y;
				y = 0;
			}
		}

		// Update rectToDraw after saving old value.
		if (rectToDraw != null) {
			previousRectDrawn.setBounds(rectToDraw.x, rectToDraw.y, rectToDraw.width, rectToDraw.height);
			rectToDraw.setBounds(x, y, width, height);
		} else {
			rectToDraw = new Rectangle(x, y, width, height);
		}
	}

	protected void moveFixed(double xMove, double yMove) {
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();

			if ((node.isFixed()) & (node != pick)) {
				// System.out.println("moving node "+i+" from ("+nodes[i].x+
				// ") to + ("+xMove+")");
				node.setX(node.getX() + xMove);
				node.setY(node.getY() + yMove);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// System.out.println(e.paramString() );
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// pick=null;

		// System.out.println(e.paramString() );
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		handleLeftMouseDragged(e);

	}

	public void handleLeftMouseDragged(MouseEvent e) {
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {

			if ((pick != null) & (e.getModifiers() != 18)) {
				pick.setX(e.getX());
				pick.setY(e.getY());
				// pick=null; // check this

				repaint();
			} else if (pick == null) { // selection rectangle
				updateSize(e);
			}

		}
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// System.out.println(e.paramString());
	}

	/**
	 * Automatically fixes orientations
	 */
	public void autoFixOrientation() {
		graph.autoFixOrientation();
	}
	// Set orientations, etc in one go

	/**
	 * Creates a random graph
	 *
	 * @param nNodes
	 *            number of nodes
	 * @param nEdges
	 *            number of edges
	 */
	public void randomGraph(int nNodes, int nEdges) {
		DemoTools dt = new DemoTools();
		graph = dt.randomGraph(nNodes, nEdges);
		graph.rescaleNodes(600);
		// System.out.println(graph.report());
		updateNodes();
	}

	Graph visibleGraph = null;

	/**
	 * Paints the component
	 *
	 * @param g
	 *            current graphics
	 */
	@Override
	public synchronized void paintComponent(Graphics g) {
		Graphics2D g2d = prePaint(g);
		// setSize(dims);
		paintNet(g2d);

		if (currentRect != null) {
			// Draw a rectangle on top of the image.

			float[] dashPattern = { 2, 3, 2, 3 };
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
			g2d.drawRect(rectToDraw.x, rectToDraw.y, rectToDraw.width - 1, rectToDraw.height - 1);
		}

		g2d.setStroke(new BasicStroke(1));
	}

	/**
	 * Paints the selected node
	 *
	 * @param g
	 *            Graphics2D
	 */
	public void paintPick(Graphics2D g) {
		Node n = pick;
		g.setPaint(n.getColor());
		int x = (int) n.getX() - correct;
		int y = (int) n.getY() - correct;
		Shape shape = PaintTools.getShape(n.getShape(), x, y, nodeSize);
		g.fill(shape);
		g.setPaint(Color.black);
		if (n.isFixed())
			g.setPaint(Color.yellow);
		g.draw(shape);
	}

}