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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import netQuant.graph.Node;

/**
 * Convenience class containing methods of painting nodes and edges. Medusa will
 * most often use these methods. If you want to implement your own methods,
 * simply modify the methods <CODE>paintNode</CODE> and <CODE>paintEdge</CODE>
 * in the graph panels.
 */
public class PaintTools {

	private static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, double orientation, double offset) {
		double theta2;
		int lengthdeltaX;
		int lengthdeltaY;
		int widthdeltaX;
		int widthdeltaY;
		int headLength = 9;
		int headWidth = 6;

		theta2 = getTheta(x1, x2, y1, y2);

		// theta+=orientation*offset;
		theta2 -= orientation * offset;
		lengthdeltaX = -(int) (Math.cos(theta2) * headLength);
		lengthdeltaY = -(int) (Math.sin(theta2) * headLength);
		widthdeltaX = (int) (Math.sin(theta2) * headWidth);
		widthdeltaY = (int) (Math.cos(theta2) * headWidth);
		x2 -= (int) (Math.cos(theta2) * headLength);
		y2 -= (int) (Math.sin(theta2) * headLength);
		// g.drawLine(x1,y1,x2,y2);
		// g.drawLine(x2,y2,x2+lengthdeltaX+widthdeltaX,y2+lengthdeltaY-widthdeltaY);
		// g.drawLine(x2,y2,x2+lengthdeltaX-widthdeltaX,y2+lengthdeltaY+widthdeltaY);
		int[] xpoints = { x2 + lengthdeltaX + widthdeltaX, x2, x2 + lengthdeltaX - widthdeltaX };
		int[] ypoints = { y2 + lengthdeltaY - widthdeltaY, y2, y2 + lengthdeltaY + widthdeltaY };
		int npoints = 3;
		g.fillPolygon(xpoints, ypoints, npoints);

	}

	private static void drawOrientedArrow(Graphics2D g, int x1, int y1, int x2, int y2, double orientation,
			double offset) {

		double theta2;
		int lengthdeltaX;
		int lengthdeltaY;
		int widthdeltaX;
		int widthdeltaY;
		int headLength = 9;
		int headWidth = 6;

		if (orientation == 1.0) {
			theta2 = getTheta(x1, x2, y1, y2);

			theta2 -= orientation * offset;
			lengthdeltaX = -(int) (Math.cos(theta2) * headLength);
			lengthdeltaY = -(int) (Math.sin(theta2) * headLength);
			widthdeltaX = (int) (Math.sin(theta2) * headWidth);
			widthdeltaY = (int) (Math.cos(theta2) * headWidth);
			x2 -= (int) (Math.cos(theta2) * headLength);
			y2 -= (int) (Math.sin(theta2) * headLength);
			// g.drawLine(x1,y1,x2,y2);
			// g.drawLine(x2,y2,x2+lengthdeltaX+widthdeltaX,y2+lengthdeltaY-widthdeltaY);
			// g.drawLine(x2,y2,x2+lengthdeltaX-widthdeltaX,y2+lengthdeltaY+widthdeltaY);
			int[] xpoints = { x2 + lengthdeltaX + widthdeltaX, x2, x2 + lengthdeltaX - widthdeltaX };
			int[] ypoints = { y2 + lengthdeltaY - widthdeltaY, y2, y2 + lengthdeltaY + widthdeltaY };
			int npoints = 3;
			g.fillPolygon(xpoints, ypoints, npoints);
		}
		if (orientation == -1.0) {

			theta2 = getTheta(x2, x1, y2, y1);

			theta2 -= Math.abs(orientation) * offset;
			lengthdeltaX = -(int) (Math.cos(theta2) * headLength);
			lengthdeltaY = -(int) (Math.sin(theta2) * headLength);
			widthdeltaX = (int) (Math.sin(theta2) * headWidth);
			widthdeltaY = (int) (Math.cos(theta2) * headWidth);
			x1 -= (int) (Math.cos(theta2) * headLength);
			y1 -= (int) (Math.sin(theta2) * headLength);
			int[] xpoints = { x1 + lengthdeltaX + widthdeltaX, x1, x1 + lengthdeltaX - widthdeltaX };
			int[] ypoints = { y1 + lengthdeltaY - widthdeltaY, y1, y1 + lengthdeltaY + widthdeltaY };
			int npoints = 3;
			g.fillPolygon(xpoints, ypoints, npoints);
		}

	}

	/**
	 * The theta parameter is used when drawing bezier curves.
	 *
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @return theta: the "attack" parameter of bezier curves
	 */
	public static double getTheta(int x1, int x2, int y1, int y2) {
		int deltaX = (x2 - x1);
		int deltaY = (y2 - y1);
		double theta;
		theta = Math.atan((double) (deltaY) / (double) (deltaX));

		if (deltaX < 0.0) {
			theta = theta += Math.PI;
		}
		return theta;
	}

	// this draws a bar at the end of the line
	// this is at the moment a hack!
	private static void drawStop(Graphics2D g, int x1, int y1, int x2, int y2, double orientation, double offset) {
		double theta2;
		int lengthdeltaX;
		int lengthdeltaY;
		int widthdeltaX;
		int widthdeltaY;
		int headLength = 9;
		int headWidth = 6;

		theta2 = getTheta(x1, x2, y1, y2);

		// theta+=orientation*offset;
		theta2 -= orientation * offset;
		lengthdeltaX = -(int) (Math.cos(theta2) * headLength);
		lengthdeltaY = -(int) (Math.sin(theta2) * headLength);
		widthdeltaX = (int) (Math.sin(theta2) * headWidth);
		widthdeltaY = (int) (Math.cos(theta2) * headWidth);
		x2 -= (int) (Math.cos(theta2) * headLength);
		y2 -= (int) (Math.sin(theta2) * headLength);
		g.drawLine(x2 + lengthdeltaX + widthdeltaX, y2 + lengthdeltaY - widthdeltaY, x2 + lengthdeltaX - widthdeltaX,
				y2 + lengthdeltaY + widthdeltaY);
	}

	/**
	 * Draws a rhomboid node shape.
	 *
	 * @param x
	 * @param y
	 * @param nodeSize
	 * @return
	 */
	public static Polygon rhomb(int x, int y, int nodeSize) {
		// Polygon rhombP = new Polygon();
		int offset = 2;
		int x1 = x + nodeSize / 2;
		int x2 = x + nodeSize + offset;
		int y1 = y + nodeSize / 2;
		int y2 = y + nodeSize;
		int[] xpoints = { x - offset, x1, x2, x1 };
		int[] ypoints = { y1, y2, y1, y };
		int npoints = 4;
		return new Polygon(xpoints, ypoints, npoints);
	}

	public static Polygon triangle(int x, int y, int nodeSize) {
		int x1 = x + nodeSize / 2;
		int x2 = x + nodeSize;
		int y2 = y + nodeSize;
		int[] xpoints = { x, x2, x1 };
		int[] ypoints = { y2, y2, y };
		int npoints = 3;
		return new Polygon(xpoints, ypoints, npoints);
	}

	public static Shape getShape(int i, int x, int y, int nodeSize) {
		switch (i) {

		case 0:
			return new Ellipse2D.Double(x, y, nodeSize, nodeSize);
		case 1:
			return new Rectangle2D.Double(x, y, nodeSize, nodeSize);
		case 2:
			return PaintTools.triangle(x, y, nodeSize);
		case 3:
			return PaintTools.rhomb(x, y, nodeSize);
		}
		return new Ellipse2D.Double(x, y, nodeSize, nodeSize);
	}

	/**
	 * A composite node has two or more colors. Nodes have additional data
	 * storage for additional colors. A composite node will be split into two
	 * halves with each half in a different color.
	 *
	 * @param g2d
	 * @param n
	 * @param nodeSize
	 */
	public static void drawCompositeNode(Graphics2D g2d, final Node n, final int nodeSize) {
		int corr = (int) (nodeSize / 2.);
		int x = (int) n.getX();
		int y = (int) n.getY();
		Color c2 = n.getColor2();
		Color c3 = n.getColor3();
		Rectangle2D.Double nodeR = new Rectangle2D.Double(x - corr, y - corr, nodeSize, nodeSize);
		// draw basic shape
		g2d.setColor(n.getColor());
		g2d.fill(nodeR);
		// draw remaining colors
		if (c2 != null) {
			g2d.setColor(c2);
			g2d.fill(new Rectangle2D.Double(x, y - corr, nodeSize - corr, nodeSize));
		}
		if (c3 != null) {
			g2d.setColor(c3);
			g2d.fill(new Rectangle2D.Double(x - corr, y, nodeSize, nodeSize - corr));
		}
		if (n.isFixed())
			g2d.setColor(Color.yellow);
		else
			g2d.setColor(Color.black);
		g2d.draw(nodeR);

	}

	// hack!
	private static void drawCircle(Graphics2D g, int x1, int y1, int x2, int y2, double orientation, double offset) {
		double theta2;
		int lengthdeltaX;
		int lengthdeltaY;
		// int widthdeltaX;
		// int widthdeltaY;
		int headLength = 12;
		int headWidth = 6;
		int circleWidth = 6;

		theta2 = getTheta(x1, x2, y1, y2);

		// theta+=orientation*offset;
		theta2 -= orientation * offset;
		lengthdeltaX = -(int) (Math.cos(theta2) * headLength);
		lengthdeltaY = -(int) (Math.sin(theta2) * headLength);
		// widthdeltaX=(int)(Math.sin(theta2)*headWidth);
		// widthdeltaY=(int)(Math.cos(theta2)*headWidth);
		x2 -= (int) (Math.cos(theta2) * headLength);
		x2 -= circleWidth / 2;
		y2 -= (int) (Math.sin(theta2) * headLength);
		y2 -= circleWidth / 2;
		Ellipse2D.Double circle = new Ellipse2D.Double(x2, y2, circleWidth, circleWidth);
		g.fill(circle);
	}

	/**
	 * Paints the cubic curve between (x1,y1) and (x2,y2). The offset parameter
	 * allows multiple edges to be shown concurrently. The orientation decides
	 * how steep the curve should be, and the offset parameter is just a tweak
	 * to make the curve less "blunt"
	 *
	 * @param g2d
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param orientation
	 * @param offset
	 * @param arrow
	 */
	public static void paintPath(Graphics2D g2d, int x1, int y1, int x2, int y2, double orientation, double offset,
			boolean arrow) {
		paintPath(g2d, x1, y1, x2, y2, orientation, offset, arrow, false);
	}

	public static void paintPath(Graphics2D g2d, int x1, int y1, int x2, int y2, double orientation, double offset,
			boolean arrow, boolean label) {

		double deltaX = (x2 - x1);
		double deltaY = (y2 - y1);
		double len = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		double radius = len / 4.0;
		// First, the line should be drawn. When this is done,
		// calculate the angle of the arrow
		if (orientation != 0.) {

			double[] cPoints = controlPoints(x1, y1, x2, y2, orientation, offset);

			CubicCurve2D.Double path = new CubicCurve2D.Double(x1, y1, cPoints[0], cPoints[1], cPoints[2], cPoints[3],
					x2, y2);

			g2d.draw(path);
		} else {
			g2d.drawLine(x1, y1, x2, y2);
			// if (label){

		}
		if (arrow)
			// drawArrow(g2d, x1,y1,x2,y2,orientation, offset);
			drawOrientedArrow(g2d, x1, y1, x2, y2, orientation, offset);

	}

	/**
	 *
	 * @param g2d
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param orientation
	 * @param offset
	 * @param arrow
	 * @param label
	 */
	public static void PaintPathWithConfLabel(Graphics2D g2d, int x1, int y1, int x2, int y2, double orientation,
			double offset, boolean arrow, boolean label) {
		// just draw the line
		g2d.drawLine(x1, y1, x2, y2);
		// on half points, add conf label
		int halfx = (x1 + x2) / 2;
		int halfy = (y1 + y2) / 2;
		g2d.setColor(Color.WHITE);
		g2d.drawRect(halfx, halfy, 10, 10);

	}

	// this returns the control points for e.g. drawing paths in postscript
	// as int
	/**
	 * Calculates the control points between (x1,y1) and (x2,y2) for the cubic
	 * curve
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param orientation
	 * @param offset
	 * @return
	 */
	public static double[] controlPoints(int x1, int y1, int x2, int y2, double orientation, double offset) {
		double deltaX = (x2 - x1);
		double deltaY = (y2 - y1);
		double len = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		double radius = len / 4.0;
		double theta, theta2;
		theta = Math.atan(deltaY / deltaX);
		if (deltaX < 0.0) {
			theta = theta += Math.PI;
		}
		// else theta2=theta;
		theta2 = theta + Math.PI;
		theta += orientation * offset;
		theta2 -= orientation * offset;
		double[] result = new double[4];
		result[2] = x2 + Math.cos(theta2) * radius;
		result[3] = y2 + Math.sin(theta2) * radius;
		result[0] = x1 + Math.cos(theta) * radius;
		result[1] = y1 + Math.sin(theta) * radius;

		return result;
	}

	public static int[] intControlPoints(int x1, int y1, int x2, int y2, double orientation, double offset) {
		double[] db = controlPoints(x1, y1, x2, y2, orientation, offset);
		int[] ib = new int[4];
		ib[0] = (int) db[0];
		ib[1] = (int) db[1];
		ib[2] = (int) db[2];
		ib[3] = (int) db[3];
		return ib;
	}

	/**
	 * Not implemented. An example of the kind of edges that can be drawn. This
	 * method draws the standard "inhibition"/"activation" interactions. set
	 * shape = 1 for an arrow shape = 2 for bar shape = 3 for circle
	 */
	public static void paintRegulatoryPath(Graphics2D g2d, int x1, int y1, int x2, int y2, double orientation,
			double offset, int shape) {

		double deltaX = (x2 - x1);
		double deltaY = (y2 - y1);
		double len = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		double radius = len / 4.0;
		// First, the line should be drawn. When this is done,
		// calculate the angle of the arrow
		if (orientation != 0.) {
			double theta, theta2;
			theta = Math.atan(deltaY / deltaX);

			if (deltaX < 0.0) {
				theta = theta += Math.PI;
			}
			// else theta2=theta;
			theta2 = theta + Math.PI;

			theta += orientation * offset;
			theta2 -= orientation * offset;
			// System.out.println(theta+" "+theta2);
			double cx2 = x2 + Math.cos(theta2) * radius;
			double cy2 = y2 + Math.sin(theta2) * radius;
			double cx1 = x1 + Math.cos(theta) * radius;
			double cy1 = y1 + Math.sin(theta) * radius;

			CubicCurve2D.Double path = new CubicCurve2D.Double(x1, y1, cx1, cy1, cx2, cy2, x2, y2);
			g2d.draw(path);

		} else {
			g2d.drawLine(x1, y1, x2, y2);
		}
		switch (shape) {
		case 1:
			drawArrow(g2d, x1, y1, x2, y2, orientation, offset);
			break;
		case 2:
			drawStop(g2d, x1, y1, x2, y2, orientation, offset);
			break;
		case 3:
			drawCircle(g2d, x1, y1, x2, y2, orientation, offset);
			break;
		}

	}

	// for creating postscript
	/**
	 * PostScript method for drawing rhomboids
	 *
	 * @param x
	 * @param y
	 * @param nodeSize
	 * @return
	 */
	public static String psRhomb(int x, int y, int nodeSize) {
		// Polygon rhombP = new Polygon();
		int correct = (int) (nodeSize / 2.0);
		StringBuffer sb = new StringBuffer();
		int offset = 2;
		int x1 = x - correct;
		int x2 = x;
		int x3 = x + correct;

		int y1 = y;
		int y2 = y + correct;
		int y3 = y - correct;

		sb.append("newpath\n");
		sb.append(x1 + " " + y1 + " moveto\n");
		sb.append(x2 + " " + y2 + " lineto\n");
		sb.append(x3 + " " + y1 + " lineto\n");
		sb.append(x2 + " " + y3 + " lineto\n");
		sb.append("closepath\n");
		sb.append("gsave\n");
		sb.append("fill\n");
		sb.append("grestore\n");
		sb.append("0 0 0 setrgbcolor\n");
		sb.append("1 setlinewidth\n");

		sb.append("stroke\n");

		return sb.toString();
	}

	/**
	 * PostScript method for drawing triangles
	 */
	public static String psTriangle(int x, int y, int nodeSize) {
		StringBuffer sb = new StringBuffer();
		int correct = (int) (nodeSize / 2.0);
		int x1 = x - correct;
		int x2 = x;
		int x3 = x + correct;
		int y1 = y - correct;
		int y2 = y + correct;
		sb.append("newpath\n");
		sb.append(x1 + " " + y1 + " moveto\n");
		sb.append(x2 + " " + y2 + " lineto\n");
		sb.append(x3 + " " + y1 + " lineto\n");
		sb.append("closepath\n");
		sb.append("gsave\n");

		sb.append("fill\n");
		sb.append("grestore\n");
		sb.append("1 setlinewidth\n");
		sb.append("0 0 0 setrgbcolor\n");
		sb.append("stroke\n");

		return sb.toString();
	}

	/**
	 * PostScript method for rectangular nodes
	 */
	public static String psRectangle(int x, int y, int nodeSize) {
		StringBuffer sb = new StringBuffer();
		int correct = (int) (nodeSize / 2.0);
		int x1 = x - correct;
		int x2 = x + correct;

		int y1 = y - correct;
		int y2 = y + correct;
		sb.append("newpath\n");
		sb.append(x1 + " " + y1 + " moveto\n");
		sb.append(x1 + " " + y2 + " lineto\n");
		sb.append(x2 + " " + y2 + " lineto\n");
		sb.append(x2 + " " + y1 + " lineto\n");
		sb.append("closepath\n");
		sb.append("gsave\n");

		sb.append("fill\n");
		sb.append("grestore\n");
		sb.append("1 setlinewidth\n");
		sb.append("0 0 0 setrgbcolor\n");
		sb.append("stroke\n");

		return sb.toString();
	}

	public static String psCircle(int x, int y, int nodeSize) {
		StringBuffer sb = new StringBuffer();
		int correct = (int) (nodeSize / 2.0);
		// int x1 = x-correct;
		// int x2 = x+correct;
		sb.append("newpath\n");
		sb.append(x + " " + y + " " + correct + " 0 360 arc closepath fill stroke\n");

		// draw the outer line of the circle
		// black
		sb.append("0 0 0 setrgbcolor\n");
		// widht of the line
		// sb.append("1 setlinewidth\n");
		// draw the line
		// sb.append(x + " " + y + " " + correct + " 0 360 arc closepath
		// stroke\n");

		return sb.toString();
	}

}
