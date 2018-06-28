/*
 * BasicGraphRenderer.java Created on February 15, 2008, 1:38 PM To change this
 * template, choose Tools | Template Manager and open the template in the
 * editor.
 */

package netQuant.display;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.Iterator;

import javax.swing.JPanel;

import netQuant.NetQuantSettings;
import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

/**
 *
 * @author Sean
 */
public class BasicGraphRenderer extends JPanel {

	/** Creates a new instance of BasicGraphRenderer */
	public BasicGraphRenderer() {

		typecorrections = new double[10];

		typecorrections[0] = 1;
		typecorrections[1] = -4;
		typecorrections[2] = 3;
		typecorrections[3] = -2;
		typecorrections[4] = -0;
		typecorrections[5] = -3;
		typecorrections[6] = 2;
		typecorrections[7] = 4;
		typecorrections[8] = -1;
		typecorrections[9] = -5;

	}

	public boolean alpha = true;

	public boolean arrow = true; // show arrow or not

	private final double[] typecorrections;

	/**
	 * The default color of edges
	 */
	public Color basicEdgeColor = Color.gray;

	// Node size, was final, but we decided to make it settable
	public int nodeSize = 10;

	// correction for node size
	/**
	 * The correction for nodes, i.e if the node size is changed, the node will
	 * still center on its location
	 */
	protected int correct = (int) (nodeSize / 2.);

	/**
	 * The default font color
	 */
	public Color fontColor = Color.black;
	public Color fadedfontColor = Color.LIGHT_GRAY;
	public Color diffColor = Color.WHITE;
	public Color mediatorColor = Color.GRAY;

	/**
	 * The current font size
	 */
	public int fontSize = 5;

	// the graph object
	public Graph graph;

	public boolean hideWhenMove = false;

	public boolean label = true;

	// the initial font
	public Font nodeFont = new Font("TimesNewRoman", Font.PLAIN, 10);

	public boolean showAnnotation = false;

	public boolean showConfidence = true;

	// the attributes holder class
	private NetQuantSettings stringSettings = null;

	// popup menu for editing
	// PopupMenu popup;

	/**
	 * Clear graphics
	 *
	 * @param g
	 *            Graphics g
	 */
	public void clear(Graphics g) {
		super.paintComponent(g);
	}

	/**
	 * Returns the font color
	 *
	 * @return Font color
	 */
	public Color getFontColor() {
		return fontColor;
	}

	public Graph getGraph() {
		return graph;

	}

	/**
	 * Returns number of nodes and edges
	 *
	 * @return returns number of [nodes, edges, unique edges]
	 */
	public String[] getGraphData() {
		String[] result = new String[3];
		result[0] = (new Integer(graph.getNodeSize())).toString();
		result[1] = (new Integer(graph.getEdgeSize())).toString();
		result[2] = (new Integer(graph.getUniqueEdgeSize())).toString();
		return result;
	}

	// the applications dimensions

	final int defaultSize = 600;
	protected int panelWidth = defaultSize;
	protected int panelHeight = defaultSize;

	// Set new dimension
	public Dimension dims = new Dimension(defaultSize, defaultSize);

	@Override
	public Dimension getMinimumSize() {
		return dims;

	}

	public int getPanelHeight() {
		return panelHeight;
	}

	public int getPanelWidth() {
		return panelWidth;
	}

	/**
	 * Apparently these functions must be overridden, according to
	 * http://www.javaworld.com/javaworld/jw-09-2000/jw-0922-javatraps.html
	 */
	@Override
	public Dimension getPreferredSize() {
		return dims;
	}

	protected BasicStroke getStroke(float conf) {
		float[] dashPattern1 = { 3, 2, 3, 2 };
		float[] dashPattern2 = { 1, 4, 1, 4 };
		if (conf > 0.67)
			return new BasicStroke();
		if (conf > 0.33)
			return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern1, 0);
		return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern2, 0);
		// return new BasicStroke(conf);
	}

	protected BasicStroke getStroke(float conf, int type) {

		float[] dashPattern = { 3, 2, 3, 2 };

		if (type < 3)
			return new BasicStroke(conf * 5);
		else
			return new BasicStroke(conf * 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0);

	}

	public void setPanelWidth(int panelWidth) {
		this.panelWidth = panelWidth;
	}

	public void setPanelHeight(int panelHeight) {
		this.panelHeight = panelHeight;
	}

	/*
	 * Composite is used for fading effects
	 */
	/**
	 * Get the composite ranging from 1.0f (opaque) to 0.0f (invisible)
	 *
	 * @param alpha
	 *            Float 0.0 to 1.0
	 * @return The composite
	 */
	protected AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

	/*
	 * Paint the panel. is called with repaint. Try without synch
	 */

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

	}

	protected Graphics2D prePaint(final Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(1));

		clear(g2d);
		return g2d;
	}

	public void paintEdge(Graphics2D g, Edge e) {
		// if (!e.visible)
		// return;
		Node from = graph.getNode(e.getFromName());
		Node to = graph.getNode(e.n2);

		if (e.getType() != 10) {

			int type = e.getType() - 1;

			int x1 = (int) from.getX();
			int y1 = (int) (from.getY() + typecorrections[type]);
			int x2 = (int) to.getX();
			int y2 = (int) (to.getY() + typecorrections[type]);

			// handle confidence
			if (showConfidence) {
				if (alpha) {

					g.setComposite(makeComposite(e.getConf()));

					if (from.getSize() == 5 || to.getSize() == 5) {
						g.setComposite(makeComposite(0.12f));
					}

					if (from.getSize() == 0 || to.getSize() == 0) {
						g.setComposite(makeComposite(0.0f));
					}

					// g.setStroke(getStroke(e.getConf()*2));

				} else {
					g.setStroke(getStroke(e.getConf()));

				}

			}

			// if (from.getSize()==5 || to.getSize()==5) {
			// g.setPaint(Color.LIGHT_GRAY);
			// } else {
			g.setPaint(basicEdgeColor);
			// }

			// if (e.visible)
			// g.setPaint(Color.red);
			if (pretty) {
				g.setPaint(getStringSettings().getColor(new Integer(e.getType())));
				// System.out.println(stringSettings.getLabel(e.color));

				PaintTools.paintPath(g, x1, y1, x2, y2, e.getOrientation(), BasicGraphPanel.offset, arrow);
			} else {

				g.drawLine(x1, y1, x2, y2);
			}
		}
	}

	// paint simple or more "pretty"
	public boolean pretty = false;

	/*
	 * The main paint method. Paints edges and nodes
	 */

	/**
	 * Paints nodes and edges
	 *
	 * @param g2d
	 *            Graphics2D g2d
	 */
	public void paintNet(Graphics2D g2d) {

		for (Iterator edges = graph.edgesIterator(); edges.hasNext();) {
			Edge e = (Edge) edges.next();
			paintEdge(g2d, e);
		}
		// make sure confidence is restored
		g2d.setComposite(makeComposite(1.0f));
		for (Iterator it = graph.nodesIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			paintNode(g2d, node);
		}
	}

	/**
	 * Paints out Node n
	 *
	 * @param g
	 *            Graphics2D
	 * @param n
	 *            Node n
	 */
	public void paintNode(Graphics2D g, Node n) {
		g.setStroke(new BasicStroke(1));
		g.setPaint(n.getColor());
		int x = (int) n.getX() - correct;
		int y = (int) n.getY() - correct;

		Shape shape = PaintTools.getShape(n.getShape(), x, y, n.getSize());// nodeSize);
		g.fill(shape);

		g.setPaint(Color.BLACK);

		if (n.getSize() == 5)
			g.setPaint(Color.GRAY);
		if (n.getSize() == 0)
			g.setPaint(Color.WHITE);
		// } else {
		// g.setPaint(Color.black);
		// }

		if (n.isFixed())
			g.setPaint(Color.yellow);
		g.draw(shape);

		g.setPaint(fontColor);

		// if (n.getSize()==7) g.setPaint(mediatorColor);
		if (n.getSize() == 5)
			g.setPaint(fadedfontColor);
		if (n.getSize() == 0)
			g.setPaint(diffColor);
		// } else {
		// g.setPaint(fontColor);
		// }
		g.setFont(nodeFont);
		if (label)
			if (showAnnotation) {

				// g.drawString(n.getAnnotation(), x - 2, y - 2);
				g.drawString(n.getAnnotation(), x - 6, y - 4);
			} else
				g.drawString(n.getLabel(), x - 6, y - 4);
		// g.drawString(n.getLabel(), x - 2, y + 2);

	}

	public NetQuantSettings getStringSettings() {
		return stringSettings;
	}

	public void setStringSettings(NetQuantSettings stringSettings) {
		this.stringSettings = stringSettings;
	}

}