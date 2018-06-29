// NetQuant is a graph viewer which allows interactive editing of networks
// (edges and nodes) and connects to databases and provides quantitative
// analysis.
// NetQuant is based on Medusa developed by Sean Hopper
// <http://www.bork.embl.de/medusa>
//
// Copyright (C) 2011 Diego Calzolari
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package netQuant.dataio;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netQuant.DataFormatException;
import netQuant.NetQuantSettings;
import netQuant.RadialTopologyViewer;
import netQuant.display.BasicGraphPanel;
import netQuant.display.PaintTools;
import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

// import clusterBlast.Graph;

/**
 * Class for saving, loading and exporting Graph objects to text file formats.
 * Also writes graphs to postscript and pajek formats.
 */
public class DataLoader implements Runnable {

	private final int x;
	private final int y;
	private String fileName;
	// private MedusaSettings ss;
	private int nodeSize = 10;

	public static final int LOAD_NET = 0;
	public static final int LOAD_TABBED = 1;
	private int loadType;

	public void setLoadType(int loadType) {
		this.loadType = loadType;
	}

	/**
	 * Constructs a new DataLoader and passes the graph size information.
	 * Usually, graphs are 600x600 in size.
	 *
	 * @param x
	 * @param y
	 */
	public DataLoader(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param fileName
	 */
	public DataLoader(int x, int y, String fileName) {
		this.x = x;
		this.y = y;
		this.fileName = fileName;
	}

	public DataLoader() {
		x = 600;
		y = 600;
	}

	// threaded methods, like load data
	Thread me = null;

	public void start() {
		if (me == null) {
			me = new Thread(this);
		}
		me.start();
		// System.out.println("thread started");
	}

	public void stop() {
		if (me != null) {
			me = null;
		}
	}

	private Graph g;

	/**
	 *
	 * @return the graph object
	 */
	public Graph getGraph() {
		return g;
	}

	@Override
	public void run() {
		Thread myThread = Thread.currentThread();
		if (me == myThread) {
			// System.out.println("thread running");
			g = new Graph();
			busy = true;
			done = false;
			try {
				switch (loadType) {
				case LOAD_NET:
					g = load(fileName);
					break;
				case LOAD_TABBED:
					g = loadSimplest(fileName);
					break;

				}
				success = true;

			} catch (IOException ie) {
				success = false;

			} catch (DataFormatException ie) {
				success = false;

			}

			busy = false;
		}
		done = true;
	}

	private boolean done;
	private boolean success;

	public boolean isDone() {
		return done;
	}

	private String status = "idle";

	public String getStatus() {
		return status;
	}

	private int progress;

	public int getProgress() {
		return progress;
	}

	private boolean busy = false;

	/**
	 * Lets other methods know if the DataLoader is busy.
	 *
	 * @return true if busy
	 */
	public boolean isBusy() {
		return busy;
	}

	/**
	 * The file to load. Set this before running the load thread.
	 *
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private final String separator = "[\\t\\s]+";

	// structure patterns
	Pattern structurePattern = Pattern.compile("\\*nodes");
	Pattern startPattern = Pattern.compile("\\*edges");

	// edge patterns
	Pattern basicEdgePattern = Pattern.compile("^([^\\t]+)\\t([^\\t]+)");
	Pattern confEdgePattern = Pattern.compile("\\tc +([\\.\\d]+)");
	Pattern orientationEdgePattern = Pattern.compile("\\to +([\\-\\.\\d]+)");
	Pattern visibleEdgePattern = Pattern.compile("\\tf +(\\w+)");
	Pattern interactionEdgePattern = Pattern.compile("\\ti +(\\d+)");

	// node patterns
	Pattern basicNodePattern = Pattern.compile("^([^\\t]+)");
	Pattern xyNodePattern = Pattern.compile("\\t([\\.\\d]+)\\t([\\.\\d]+)");
	Pattern colorNodePattern = Pattern.compile("\\tc *\\D*(\\d+)\\D*(\\d+)\\D*(\\d+)\\D*");
	Pattern color2NodePattern = Pattern.compile("\\tc2 *\\D*(\\d+)\\D*(\\d+)\\D*(\\d+)\\D*");
	Pattern color3NodePattern = Pattern.compile("\\tc3 *\\D*(\\d+)\\D*(\\d+)\\D*(\\d+)\\D*");
	Pattern fixedNodePattern = Pattern.compile("\\tf +(\\w+)");
	Pattern shapeNodePattern = Pattern.compile("\\ts +(\\d)");
	Pattern dataspace1Pattern = Pattern.compile("\\td1 +(\\d+)");
	// Pattern annotationPattern = Pattern.compile("\\ta \\\"([^\\\"]+)\\\"");
	Pattern annotationPattern = Pattern.compile("\\ta \\\"(.+)\\\"");

	// parameter patterns
	// edge: n1:n2:i:c:o
	Pattern edgeParaPattern = Pattern.compile("\\s*(\\w[^:^\\s]+):([^:]+):(\\d+):([\\d\\.]+):([\\-\\d\\.]+)");
	Pattern testEdgeParaPattern = Pattern.compile("\\s*(\\w[^:]+):");
	// node: n1:x:y:c:s
	Pattern nodeParaPattern = Pattern.compile("\\s*(\\w[^:]+):([\\d\\.]+):([\\d\\.]+):(\\d+),(\\d+),(\\d+):(\\d)");

	/*
	 * save data to file
	 */
	/**
	 * Writes the graph to file
	 *
	 * @param graph
	 * @param fileName
	 * @throws java.io.IOException
	 */
	public void save(Graph graph, String fileName) throws IOException {
		File saveFile = new File(fileName);
		FileWriter out = new FileWriter(saveFile);
		out.write(graph.report());
		out.close();
	}

	// this hack is horrid!
	/**
	 * This method is not very nice... will need to be rewritten. The scale
	 * function is generally messy.
	 */
	public void save(Graph graph, String fileName, double scale) throws IOException {
		// System.out.println("scaling to "+1.0/scale);
		// try {
		// Graph g = (Graph)graph.clone();
		// double scaler = scale*600;
		// graph.rescaleNodes(1.0/scaler);
		// System.out.println("rescaled to "+scale);
		File saveFile = new File(fileName);
		FileWriter out = new FileWriter(saveFile);
		out.write(graph.report());
		// graph.rescaleNodes(scaler);
		out.close();

		// catch (CloneNotSupportedException cs){
		// }
	}

	/*
	 * load data from a file
	 */
	String currentFileName = null;

	/**
	 * Loads a netquant format file.
	 *
	 * @param fileName
	 *            File to load
	 * @throws java.io.IOException
	 * @throws netQuant.DataFormatException
	 * @return returns a Graph object which is passed to netQuant
	 */
	public Graph load(String fileName) throws IOException, DataFormatException {
		busy = true;
		max = 600.0;
		progress = 0;
		currentFileName = fileName;
		Graph graph = new Graph();

		String inLine;
		Matcher matcher;
		String comment = "";
		File loadFile = new File(fileName);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile)));
		// read to where the edges start
		while ((inLine = in.readLine()) != null) {

			matcher = startPattern.matcher(inLine);
			if (matcher.find())
				break;
			// if not edges, add to comment line
			comment += inLine;
			comment += "\n";
			graph.addComment(comment);
		}

		// read the edges
		status = "Reading edges";
		while ((inLine = in.readLine()) != null) {
			matcher = structurePattern.matcher(inLine);
			if (matcher.find()) {
				// System.out.println("node section found");
				// containsNodes=true;
				status = "Reading nodes";
				break;
			}
			Edge temp = readEdge(inLine);
			String from = temp.getFromName();
			String to = temp.getToName();
			System.out.println(from + "\t" + to);

			if (temp != null) {
				// graph.addEdge(readEdge(inLine));
				graph.addEdge(temp);
				progress++;
			}
		}

		// read nodes
		status = "Reading nodes";
		while ((inLine = in.readLine()) != null) {
			readNode(graph, inLine);
		}

		in.close();
		busy = false;
		/*
		 * if (max>normalSize) graph.rescaleNodes(normalSize/max);
		 */
		status = "idle";

		graph = checkGraph(graph);

		return graph;
	}

	private Graph checkGraph(Graph g) {

		for (Iterator<Node> it = g.nodesIterator(); it.hasNext();) {

			Node node = g.getNode(it.next().getLabel());

			double x = node.getX();
			double y = node.getY();

			if (x < 30) {
				x += 30;
				node.setX(x);
			}
			if (y < 30) {
				y += 30;
				node.setY(y);
			}

			node.setShape(4);

		}

		return g;

	}

	/**
	 * Loads the simplest possible file of three columns: node1 node2 identity
	 *
	 * This can be used for instance by biologists who run BLAST searches with
	 * the -m 8 option.
	 *
	 * If identity is greater than 1.0, it is rescaled.
	 *
	 * @param fileName
	 * @throws java.io.IOException
	 * @throws netQuant.DataFormatException
	 * @return
	 */
	public Graph loadSimplest(String fileName) throws IOException, DataFormatException {
		Graph graph = new Graph();
		// max=0;
		progress = 0;
		String inLine;
		File loadFile = new File(fileName);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile)));
		String[] result;
		while ((inLine = in.readLine()) != null) {
			result = inLine.split("\t");
			if (result.length > 2) {
				graph.addEdge(new Node(result[0]), new Node(result[1]), Float.parseFloat(result[2]), 1);
				progress++;
			} else {
				throw new DataFormatException("File does not have three columns");
			}

		}
		in.close();
		graph.rescaleNodePercentage();
		// if (max>normalSize)
		// graph.rescaleNodes(normalSize/max);
		if (graph.getEdgeSize() == 0) {
			throw new DataFormatException("No edges found");
		}
		return graph;
	}

	final double normalSize = 600.0;
	double max;

	private void readNode(Graph g, String inLine) {
		// System.out.println(inLine);
		Matcher matcher = basicNodePattern.matcher(inLine);

		if (!matcher.find())
			return;
		// System.out.println(matcher.group(1));
		Node n = g.getNode(matcher.group(1));
		if (n == null)
			return;

		matcher = xyNodePattern.matcher(inLine);
		if (matcher.find()) {
			double nodeX = Double.parseDouble(matcher.group(1));
			double nodeY = Double.parseDouble(matcher.group(2));
			// System.out.println("Found coords "+nodeX+" "+nodeY);
			if (nodeX <= 1.0)
				nodeX *= x;
			if (nodeY <= 1.0)
				nodeY *= y;
			// System.out.println("--- transformed to "+nodeX+" "+nodeY);
			n.setXY(nodeX, nodeY);
			// changed this
			// max=Math.max(max,Math.max(nodeX,nodeY));

		}

		// get color
		matcher = colorNodePattern.matcher(inLine);
		if (matcher.find()) {
			java.awt.Color color = new java.awt.Color(Integer.parseInt(matcher.group(1)),
					Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
			n.setColor(color);
		}

		matcher = color2NodePattern.matcher(inLine);
		if (matcher.find()) {
			java.awt.Color color = new java.awt.Color(Integer.parseInt(matcher.group(1)),
					Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
			n.setColor2(color);
		}
		matcher = color3NodePattern.matcher(inLine);
		if (matcher.find()) {
			java.awt.Color color = new java.awt.Color(Integer.parseInt(matcher.group(1)),
					Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
			n.setColor3(color);
		}

		matcher = shapeNodePattern.matcher(inLine);
		if (matcher.find()) {
			int shape = Integer.parseInt(matcher.group(1));
			n.setShape(shape);
		}

		matcher = dataspace1Pattern.matcher(inLine);
		if (matcher.find()) {
			int ds1 = Integer.parseInt(matcher.group(1));
			n.setDataSpace1(ds1);
		}
		matcher = annotationPattern.matcher(inLine);
		if (matcher.find()) {

			n.setAnnotation(matcher.group(1));
		}
		// get fixed
		matcher = fixedNodePattern.matcher(inLine);
		if (matcher.find())
			n.setFixed(Boolean.getBoolean(matcher.group(1)));

	}

	private Edge readEdge(String inLine) throws DataFormatException {
		// MRPL9 GON5 i 5 c 0.784 o 0 v true
		// get basic fields
		Edge e;
		Matcher matcher = basicEdgePattern.matcher(inLine);
		if (!matcher.find()) {
			// return null;
			throw new DataFormatException("Error in data file at line:\n " + inLine
					+ ".\nEdge must contain basic node data." + "If you are using an old data file version,"
					+ "consider using the conversion utility\n" + "Offending line: \n" + inLine);
		}

		e = new Edge(matcher.group(1), matcher.group(2));
		float conf;
		matcher = confEdgePattern.matcher(inLine);
		if (matcher.find()) {
			// System.out.println("conf: "+matcher.group(1));
			conf = Float.parseFloat(matcher.group(1));
			if ((conf < 0.0f) | (conf > 1.0f))
				throw new DataFormatException("Confidence may not be less than" + " zero or greater than one.\n"
						+ matcher.group(1) + " in line: \n" + inLine);
			e.setConf(conf);
		}

		// get orientation
		matcher = orientationEdgePattern.matcher(inLine);
		if (matcher.find()) {
			// System.out.println("orientation: "+matcher.group(1));

			double d = Double.parseDouble(matcher.group(1));
			e.setOrientation(d);
		}
		/*
		 * // get visible matcher=visibleEdgePattern.matcher(inLine); if
		 * (matcher.find()) edges[nedges-1].visible=Boolean.valueOf
		 * (matcher.group(1)).booleanValue();
		 */
		// get the interaction type, ie the 'color'
		matcher = interactionEdgePattern.matcher(inLine);
		if (matcher.find())
			e.setType(Integer.parseInt(matcher.group(1)));

		return e;
	}

	// load from parameter lines
	// the format can be stricter, since the server admin has control over
	// the parameter output. No user friendliness anymore :)
	/**
	 * For applets. Get parameter lines, e.g nodes and edges, from the applet
	 * context and create a new graph.
	 *
	 * @param edges
	 * @param nodes
	 * @return
	 */
	public Graph readParameters(String edges, String nodes) {
		Graph g = new Graph();
		Edge temp;
		// System.out.println(edges+"\n "+nodes);
		String edgeResult[] = edges.split("[;\\n?]");
		String[] edgeItems;
		int lines = edgeResult.length;
		System.out.println("checking " + edgeResult.length + " edges");
		for (int i = 0; i < lines; i++) {
			// System.out.println(edgeResult[i]);
			// edgeItems=edgeResult[i].split(":");
			// System.out.println(edgeResult[i]+" \t"+edgeItems.length);
			// if (edgeItems.length==5){
			temp = readEdgeParameter(edgeResult[i]);
			// System.out.println(temp.toString());
			g.addEdge(temp);
			// }

		}
		System.out.println("checking nodes");
		if (nodes != null) {
			String nodeResult[] = nodes.split(";\\n?");
			lines = nodeResult.length;

			for (int i = 0; i < lines; i++) {
				try {
					// System.out.println(nodeResult[i]);
					// System.out.println(nodeResult[i]);
					readNodeParameter(g, nodeResult[i]);
				} catch (Exception ex) {
					System.out.println("Error in line " + nodeResult[i]);
					System.out.println(ex.getMessage());
				}

			}
		}
		System.out.println("All done");
		return g;
	}

	/**
	 * normalize position to show them in ratio of the dimensions
	 *
	 * @param g
	 */
	public void normalizePositions(Graph g) {
		for (Iterator nodes = g.nodesIterator(); nodes.hasNext();) {
			Node n = (Node) nodes.next();
			n.setXY(n.getX() / x, n.getY() / y);
		}

	}

	// edge line should be
	// n1:n2:type:conf:orient

	private String chomp(String text) {
		text = text.replaceAll("\\s", "");
		return text;
	}

	private Edge readEdgeParameter(String[] result) {
		Edge e = new Edge(result[0], result[1], Float.parseFloat(result[3]), Integer.parseInt(result[2]),
				Double.parseDouble(result[4]));
		return e;
	}

	private Edge readEdgeParameter(String edgeLine) {
		edgeLine = chomp(edgeLine);
		String[] result = edgeLine.split(":");
		Edge e = new Edge(result[0], result[1], Float.parseFloat(result[3]), Integer.parseInt(result[2]),
				Double.parseDouble(result[4]));
		return e;
	}

	// node line
	// n1:x:y:r,g,b:shape:annotation

	private void readNodeParameter(Graph g, String nodeLine) throws Exception {
		// split the line
		nodeLine = chomp(nodeLine);
		// System.out.println(nodeLine);
		String[] result = nodeLine.split(":");
		if (result.length > 4) {
			Node n = g.getNode(result[0]);
			double nodeX = Double.parseDouble(result[1]);
			double nodeY = Double.parseDouble(result[2]);
			String[] colorComponents = result[3].split(",");
			java.awt.Color color = new java.awt.Color(Integer.parseInt(colorComponents[0]),
					Integer.parseInt(colorComponents[1]), Integer.parseInt(colorComponents[2]));
			n.setXY(nodeX, nodeY);
			n.setColor(color);
			int shape = Integer.parseInt(result[4]);
			n.setShape(shape);

			if (result.length == 6) {
				// check for annotation line
				// System.out.println(result[5]);
				// Pattern anno=Pattern.compile("\\'([^\\']+)\\'");
				// Matcher m=anno.matcher(result[5]);
				// if (m.find()){
				n.setAnnotation(result[5]);
				// }
			}
		}

	}

	private void readNodeParameter_backup(Graph g, String nodeLine) {
		Matcher matcher = nodeParaPattern.matcher(nodeLine);
		if (!matcher.find())
			return;
		Node n = g.getNode(matcher.group(1));
		// System.out.println("<"+matcher.group(0)+">");
		if (n == null) {
			System.out.println("node not found!");

			return;
		}
		// System.out.println(n);
		double nodeX = Double.parseDouble(matcher.group(2));
		double nodeY = Double.parseDouble(matcher.group(3));
		// System.out.println(nodeX+" "+nodeY);
		if (nodeX <= 1.0)
			nodeX *= x;
		if (nodeY <= 1.0)
			nodeY *= x;
		// System.out.println("transform: "+nodeX+" "+nodeY);
		try {
			System.out.println("Setting " + n.getLabel() + " to " + nodeX + "," + nodeY);
			n.setXY(nodeX, nodeY);
			// System.out.println(n.toString());
		} catch (NullPointerException ne) {
			System.out.println("Problem with line " + nodeLine);
			ne.printStackTrace();
		}

		// get color
		// System.out.println("getting color");
		// try {
		java.awt.Color color = new java.awt.Color(Integer.parseInt(matcher.group(4)),
				Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)));
		// System.out.println(color.toString());
		n.setColor(color);
		// }
		// catch (IllegalArgumentException ie){
		// throw new DataFormatException("Color ranges are 0-255"+
		// "\n"+
		// "Offending line: \n"+nodeLine);
		// }

		int shape = Integer.parseInt(matcher.group(7));
		n.setShape(shape);

		// check if annotation is available. Should be enclosed with string
		// literals
		Pattern annotationPattern = Pattern.compile("\\'([^\\']+)\\'");
		matcher = annotationPattern.matcher(nodeLine);
		if (matcher.find()) {
			// add this annotation
			System.out.println(nodeLine);
			n.setAnnotation(matcher.group(1));
		}
		return;

	}

	/**
	 * Draw the graph to postscript format
	 */
	public void saveAsPS(Graph g, String saveFileName, NetQuantSettings ss) throws IOException {
		saveAsPS(g, saveFileName, ss, nodeSize, fontSize);
	}

	private int fontSize = 10;

	public void saveAsPS(Graph g, String saveFileName, NetQuantSettings ss, int nodeSize, int fontSize)
			throws IOException {
		this.nodeSize = nodeSize;
		this.fontSize = fontSize;
		File saveFile = new File(saveFileName);
		FileWriter out = new FileWriter(saveFile);
		out.write("%!PS\n");

		drawGraph(g, out, ss);

		// finish the PS
		out.write("showpage\n");
		out.close();
	}

	public void saveAsEPS(Graph g, String saveFileName, NetQuantSettings ss, int nodeSize, int fontSize)
			throws IOException {
		this.nodeSize = nodeSize;
		this.fontSize = fontSize;
		File saveFile = new File(saveFileName);
		FileWriter out = new FileWriter(saveFile);
		out.write("%!PS-Adobe EPSF-3.0\n");
		out.write("%%Creator: Medusa\n");
		// out.write("%%BoundingBox: 0 0 " + docWidth + " " + docHeight + "\n");
		out.write("%%BoundingBox: 0 0 " + "3000" + " " + "3000" + "\n");
		drawGraph(g, out, ss);

		// finish the PS
		// out.write("showpage\n");
		out.close();
	}

	private void drawGraph(Graph g, FileWriter out, NetQuantSettings ss) throws IOException {

		// out.write("50 50 translate\n");
		out.write("-150 300 translate\n");

		out.write("0.2 setlinewidth\n");
		out.write("0 0 0 setrgbcolor\n");

		// draw the edges
		drawEdges(out, g, ss);

		// draw nodes
		drawNodes(out, g);

		// draw labels
		if (RadialTopologyViewer.printLabelEPS) {
			drawLabels(out, g);
		}

		// finish the PS
		// out.write("showpage\n");
		// out.close();
	}

	// public Color getColor(Integer i){
	// return stringSettings.getColor(i);
	// }
	final int docWidth = 500;
	final int docHeight = 500;

	private int psY(double y) {
		return (int) ((600.0 - y) / 600.0 * docHeight);
	}

	private int psX(double x) {
		return (int) (x / 600.0 * docWidth);
	}

	private void drawEdges(FileWriter out, Graph g, NetQuantSettings ss) throws IOException {
		int x1, x2;
		int y1, y2;
		String[] nodes = new String[2];
		Edge e;
		Node n1, n2;
		Integer colorType;
		Color color;
		float alpha;
		out.write("%% drawing edges\n");
		for (java.util.Iterator i = g.edgesIterator(); i.hasNext();) {
			e = (Edge) i.next();
			nodes = e.getNodes();
			n1 = g.getNode(nodes[0]);
			x1 = psX(n1.getX());

			y1 = psY(n1.getY());
			n2 = g.getNode(nodes[1]);
			x2 = psX(n2.getX());

			y2 = psY(n2.getY());

			colorType = new Integer(e.getType());
			color = ss.getColor(colorType);
			alpha = e.getConf();
			out.write(colorToString(color));
			out.write(alpha + " setalpha\n");
			// check if we want a path or a line
			if (e.getOrientation() != 0.0) {
				int[] cPoints = PaintTools.intControlPoints(x1, y1, x2, y2, e.getOrientation(), BasicGraphPanel.offset);
				out.write(x1 + " " + y1 + " moveto\n");
				out.write(cPoints[0] + " " + cPoints[1] + " " + cPoints[2] + " " + cPoints[3] + " " + x2 + " " + y2
						+ " curveto stroke\n");
			} else {
				out.write(x1 + " " + y1 + " moveto\n");
				out.write(x2 + " " + y2 + " lineto stroke\n");
			}

		}
		out.write("1.0 setalpha\n");
	}

	private String colorToString(final Color color) {
		double red = color.getRed() / 255.;
		double green = color.getGreen() / 255.;
		double blue = color.getBlue() / 255.;

		return red + " " + green + " " + blue + " setrgbcolor\n";
	}

	private void drawLabels(FileWriter out, Graph g) throws IOException {
		int labelCorrect = 5;
		int x1;
		int y1;
		Node n;
		out.write("%% drawing labels\n");
		out.write("0 0 0 setrgbcolor\n");
		for (java.util.Iterator i = g.nodesIterator(); i.hasNext();) {
			n = (Node) i.next();
			x1 = psX(n.getX());
			y1 = psY(n.getY());
			// y1=600-(int)n.getY();
			// out.write("/Times-Roman findfont\n" + fontSize + "
			// scalefont\nsetfont\nnewpath\n");
			out.write("/Arial findfont\n" + fontSize + " scalefont\nsetfont\nnewpath\n");

			// out.write((x1 - labelCorrect) + " " + (y1 + labelCorrect) + "
			// moveto\n");
			out.write((x1 - 6) + " " + (y1 + 7) + " moveto\n");
			out.write("(" + n.getLabel() + ") show\n");

		}
	}

	private void drawNodes(FileWriter out, Graph g) throws IOException {
		Node n;

		out.write("%% drawing nodes\n");

		for (java.util.Iterator i = g.nodesIterator(); i.hasNext();) {
			n = (Node) i.next();
			drawNodeShape(out, n);

		}
	}

	private void drawNodeShape(FileWriter out, Node n) throws IOException {
		int x1 = psX(n.getX());
		int y1 = psY(n.getY());
		java.awt.Color color = n.getColor();
		int shape = n.getShape();
		// int nodeSize=8;

		out.write(colorToString(color));
		switch (shape) {
		case 1:
			out.write(PaintTools.psRectangle(x1, y1, nodeSize));
			break;
		case 2:
			out.write(PaintTools.psTriangle(x1, y1, nodeSize));
			break;
		case 3:
			out.write(PaintTools.psRhomb(x1, y1, nodeSize));
			break;

		default:
			out.write(PaintTools.psCircle(x1, y1, nodeSize));
			break;
		}
	}

	public void saveAsPajek(Graph g, String saveFileName) throws IOException {
		File saveFile = new File(saveFileName);
		FileWriter out = new FileWriter(saveFile);
		int nnodes = g.getNodeSize();
		out.write("*Vertices\t" + nnodes + "\n");
		int count = 1;
		String cr = "\r\n";

		// we need to translate node names into indexes... add nodes to
		// a hashmap
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		for (Iterator i = g.nodesIterator(); i.hasNext(); count++) {
			Node n = (Node) i.next();
			out.write(count + "\t\"" + n.getLabel() + "\"\t" + n.getX() + "\t" + n.getY() + cr);
			hm.put(n.getLabel(), new Integer(count));

		}

		String[] nodes = new String[2];
		Integer n1, n2;

		// the hashmap is a dictionary
		out.write("*Edges\n");
		for (Iterator i = g.edgesIterator(); i.hasNext();) {
			Edge e = (Edge) i.next();
			nodes = e.getNodes();
			n1 = hm.get(nodes[0]);
			n2 = hm.get(nodes[1]);
			out.write(n1 + "\t" + n2 + "\t" + e.getType() + cr);
		}
		out.close();
	}

	public void saveHTMLParameters(Graph g, String fileName) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<!-- edge parameters generated by Medusa -->\n");
		sb.append("<param name=\"edges\" value=\"\n");
		for (Iterator i = g.edgesIterator(); i.hasNext();) {
			Edge e = (Edge) i.next();

			sb.append(e.getFromName());
			sb.append(":");
			sb.append(e.getToName());
			sb.append(":");
			sb.append(e.getType());
			sb.append(":");
			sb.append(e.getConf());
			sb.append(":");
			sb.append(e.getOrientation());
			// sb.append(":");
			// sb.append(e.)
			sb.append(";\n");

		}
		sb.append("\"/>\n");
		sb.append("<!-- node parameters generated by Medusa -->\n");
		sb.append("<param name=\"nodes\" value=\"\n");
		for (Iterator i = g.nodesIterator(); i.hasNext();) {
			Node n = (Node) i.next();

			sb.append(n.getLabel());
			sb.append(":");
			sb.append(n.getX());
			sb.append(":");
			sb.append(n.getY());
			sb.append(":");
			sb.append(n.getAppletColorEntry());
			sb.append(":");
			sb.append(n.getShape());
			sb.append(":");
			sb.append(n.getAnnotation());
			sb.append(";\n");
		}
		sb.append("\"/>\n");
		FileWriter out = new FileWriter(fileName);
		out.write(sb.toString());
		out.close();

	}

}
