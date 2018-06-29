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

package netQuant;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetQuantSettings {

	// Table of colors, which allows colors to be changed
	Hashtable<Integer, Color> colorTable = new Hashtable<Integer, Color>();
	// And also the interaction names.
	Hashtable<Integer, String> interactionTable = new Hashtable<Integer, String>();

	Color[] colorList = { // new Color(255, 100, 180), // Neighbor
			new Color(130, 130, 130), // Neighbor
			// new Color(150, 255, 150), // gene fusion
			new Color(130, 130, 130), // gene fusion
			new Color(200, 200, 200), // cooccurence
			// new Color(255, 0, 255), // homology
			new Color(130, 130, 130), // homology
			new Color(0, 255, 255), // coexpression
			new Color(0, 0, 255), // experiments
			// new Color(0,255,0), //databases
			new Color(130, 130, 130), // databases
			new Color(255, 255, 0), // text mining
			new Color(255, 0, 0), // predicted
			new Color(255, 255, 255) // score links

	};

	/**
	 * Return a random color
	 *
	 * @return Color
	 */
	public Color randomColor() {

		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = Math.min(255, 255 * 2 - r - g);

		return new Color(r, g, b);
	}

	/**
	 * constructor
	 */
	public NetQuantSettings() {

		init();
	}

	/**
	 * get color from a string list
	 *
	 * @param param
	 * @return new color
	 */
	public Color getColorFromParam(String param) {

		String[] rgb = param.split(",");
		;
		int red, green, blue;
		red = Integer.parseInt(rgb[0]);
		green = Integer.parseInt(rgb[1]);
		blue = Integer.parseInt(rgb[2]);
		return new Color(red, green, blue);

	}

	/**
	 * constructor
	 *
	 * @param settingsParam
	 */
	public NetQuantSettings(String settingsParam) {

		if (settingsParam != null) {
			readParam(settingsParam);
		} else {
			init();
		}
	}

	/**
	 * read parameters list
	 *
	 * @param settingsParam
	 */
	public void readParam(String settingsParam) {
		String[] colors = settingsParam.split(";");
		Color c;
		for (int i = 0; i < colors.length; i++) {
			c = getColorFromParam(colors[i]);
			System.out.println(c);
			colorTable.put(new Integer(i + 1), c);
		}
	}

	/**
	 * clear interactions
	 */
	public void clear() {

		colorTable.clear();
		interactionTable.clear();

	}

	/**
	 * initialize protein interactions
	 */
	public void init() {
		// clear();
		initColorTable();
		initInteractionTable();
	}

	/**
	 * initialize COG interactions
	 */
	public void initCOGS() {
		// clear();
		initColorTable();
		initCOGInteractionTable();
	}

	/**
	 * initialize color table
	 */
	public void initColorTable() {
		for (int i = 0; i < colorList.length; i++)
			colorTable.put(new Integer(i + 1), colorList[i]);
	}

	/**
	 * initialize protein interaction table
	 */
	public void initInteractionTable() {
		interactionTable.put(new Integer(1), "Neighbourhood");
		interactionTable.put(new Integer(2), "Gene Fusion");
		interactionTable.put(new Integer(3), "Cooccurence");
		interactionTable.put(new Integer(4), "Homology");
		interactionTable.put(new Integer(5), "Coexpression");
		interactionTable.put(new Integer(6), "Experiments");
		interactionTable.put(new Integer(7), "Databases");
		interactionTable.put(new Integer(8), "Text Mining");
		interactionTable.put(new Integer(9), "Predicted");
		// interactionTable.put(new Integer(10), "Score");

		/*
		 * interactionTable.put(new Integer(1), "Mutual");
		 * interactionTable.put(new Integer(2), "Mutual - Positive");
		 * interactionTable.put(new Integer(3), "Mutual - Negative");
		 * interactionTable.put(new Integer(4), "Directed");
		 * interactionTable.put(new Integer(5), "Directed - Positive");
		 * interactionTable.put(new Integer(6), "Directed - Negative");
		 * interactionTable.put(new Integer(8), "Inferred - Positive");
		 * interactionTable.put(new Integer(9), "Inferred - Negative");
		 */
	}

	/**
	 * initialize COG table
	 */
	public void initCOGInteractionTable() {
		interactionTable.put(new Integer(1), "Neighbourhood");
		interactionTable.put(new Integer(2), "Gene Fusion");
		interactionTable.put(new Integer(3), "Homology");
		interactionTable.put(new Integer(4), "Coexpression");
		interactionTable.put(new Integer(5), "Experiments");
		interactionTable.put(new Integer(6), "Databases");
		interactionTable.put(new Integer(7), "Text Mining");
	}

	/**
	 * load pattern
	 *
	 * @param fileName
	 * @throws IOException
	 */
	public void load(String fileName) throws IOException {

		File loadFile = new File(fileName);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile)));
		String inLine;
		// clear settings
		Pattern xyPattern = Pattern.compile("(\\d+)[, ](\\d+)[, ](\\d+)");

		interactionTable = new Hashtable<Integer, String>();
		colorTable = new Hashtable<Integer, Color>();
		initColorTable();
		// load new settings
		while ((inLine = in.readLine()) != null) {
			String result[] = inLine.split("\\t");
			Matcher matcher = xyPattern.matcher(inLine);
			// set names
			interactionTable.put(new Integer(result[0]), result[1]);
			// set color, if present
			if (matcher.find()) {
				int red = Integer.parseInt(matcher.group(1));
				int green = Integer.parseInt(matcher.group(2));
				int blue = Integer.parseInt(matcher.group(3));
				colorTable.put(new Integer(result[0]), new Color(red, green, blue));
			}

		}
		in.close();
	}

	/**
	 * get size of the interactions table
	 *
	 * @return
	 */
	public int getSize() {
		return interactionTable.size();
	}

	/**
	 * report and print out the interactions table
	 */
	public void report() {
		for (int i = 1; i < interactionTable.size(); i++) {
			System.out.println(getName(new Integer(i), 0));
		}
	}

	/**
	 * parse a list of rgb colors
	 *
	 * @param rgb
	 * @return
	 */
	public Color parseColor(String rgb) {
		String result[] = rgb.split(",");
		if (result.length < 3)
			return randomColor();
		int r = Integer.parseInt(result[0]);
		int g = Integer.parseInt(result[1]);
		int b = Integer.parseInt(result[2]);
		return new Color(r, g, b);
	}

	/**
	 * get color i from color table
	 *
	 * @param i
	 * @return
	 */
	public Color getColor(Integer i) {
		return colorTable.get(i);
	}

	/**
	 * set at position i of color table Color color
	 *
	 * @param i
	 * @param color
	 */
	public void setColor(Integer i, Color color) {
		colorTable.put(i, color);
	}

	/**
	 * get interaction i from interaction table
	 *
	 * @param i
	 * @param len
	 * @return
	 */
	public String getName(Integer i, int len) {
		String name = interactionTable.get(i);
		if (name == null)
			return "null!?";
		// System.out.println(name.length());
		if ((name.length() < len) || (len == 0))
			return name;
		return name.substring(0, len - 1);
	}

	/**
	 * get interaction from interaction table
	 *
	 * @param i
	 * @return
	 */
	public String getName(Integer i) {

		String name = interactionTable.get(i);
		if (name == null)
			return "null!?";

		return name;

	}

	/**
	 * Set name in position i in protein interaction table
	 *
	 * @param i
	 * @param name
	 */
	public void setName(Integer i, String name) {
		interactionTable.put(i, name);
	}

}
