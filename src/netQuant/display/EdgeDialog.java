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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import netQuant.NetQuantSettings;
import netQuant.graph.Edge;
import netQuant.graph.Node;

public class EdgeDialog extends JDialog implements ActionListener {

	private static EdgeDialog edgeDialog;

	private EdgeDialog(java.awt.Frame frame, java.awt.Component locationComp) {
		this(frame, locationComp, new NetQuantSettings());

	}

	private EdgeDialog(java.awt.Frame frame, java.awt.Component locationComp, NetQuantSettings ss) {
		super(frame, "", true);
		setDefaultLookAndFeelDecorated(true);
		this.ss = ss;
		// ss.report();
		init();
		setLocationRelativeTo(locationComp);
	}

	NetQuantSettings ss;

	final java.awt.Color stringColor = Color.black;
	final int preferredHeight = 20;
	final int preferredWidth = 50;
	final java.awt.Dimension textAreaSize = new java.awt.Dimension(preferredWidth, preferredHeight);

	private final JButton submitButton = new JButton("Submit");
	private final JButton cancelButton = new JButton("Cancel");
	private final JLabel node1Label = new JLabel("Node 1");
	private final JTextArea node1Area = new JTextArea();
	private final JLabel node2Label = new JLabel("Node 2");
	private final JTextArea node2Area = new JTextArea();
	private final JPanel mainPanel = new JPanel();
	private final JLabel titleLabel = new JLabel();
	private final JLabel sideLabel = new JLabel();
	private final JPanel bottomPanel = new JPanel();
	// interaction type
	private final JLabel interactionLabel = new JLabel("Type");
	// private String[] numbers = {"1","2","3"};
	private JComboBox interactionCB;
	private final JLabel confidenceLabel = new JLabel("Conf");
	private final JTextArea confidenceArea = new JTextArea("1.0");
	private final JLabel orientationLabel = new JLabel("Ori");
	private final JTextArea orientationArea = new JTextArea("0.0");

	private String[] getInteractions() {
		int i = ss.getSize();
		// System.out.println(i);
		String[] numbers = new String[i];
		for (int j = 0; j < i; j++) {
			// System.out.println(ss.getLabel(j+1));
			numbers[j] = ss.getName(new Integer(j + 1));
		}
		return numbers;
	}

	private void init() {
		interactionCB = new JComboBox(getInteractions());
		java.awt.Container content = getContentPane();
		content.setLayout(new java.awt.BorderLayout());
		titleLabel.setBackground(stringColor);
		// titleLabel.setFont(new java.awt.Font("Times New Roman", 1, 24));
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		titleLabel.setText("Edit edge");
		titleLabel.setOpaque(true);
		content.add(titleLabel, java.awt.BorderLayout.NORTH);

		sideLabel.setBackground(stringColor);
		sideLabel.setIcon(new javax.swing.ImageIcon("logo_still_p.gif"));
		sideLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		sideLabel.setOpaque(true);
		content.add(sideLabel, java.awt.BorderLayout.WEST);

		submitButton.addActionListener(this);
		cancelButton.addActionListener(this);

		/*
		 * bottomPanel.setLayout(new java.awt.FlowLayout());
		 * bottomPanel.add(submitButton); bottomPanel.add(cancelButton);
		 * content.add(bottomPanel, java.awt.BorderLayout.SOUTH);
		 */

		mainPanel.setLayout(new java.awt.GridLayout(0, 2, 5, 5));
		// mainPanel.add(titleLabel);
		node1Area.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		node2Area.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		confidenceArea.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		mainPanel.add(node1Label);
		mainPanel.add(node1Area);
		mainPanel.add(node2Label);
		mainPanel.add(node2Area);
		mainPanel.add(interactionLabel);
		mainPanel.add(interactionCB);
		mainPanel.add(confidenceLabel);
		mainPanel.add(confidenceArea);
		mainPanel.add(orientationLabel);
		mainPanel.add(orientationArea);
		mainPanel.add(submitButton);
		mainPanel.add(cancelButton);
		content.add(mainPanel, java.awt.BorderLayout.CENTER);
	}

	/**
	 * Actions when pressing buttons in dialog
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();

		// submit/done button action
		if (obj.equals(submitButton)) {
			submitButtonActionPerformed();
		}
		// finish/cancel button action
		if (obj.equals(cancelButton)) {
			cancelButtonActionPerformed();
		}
	}

	private void setNodes(String n1, String n2) {
		node1Area.setText(n1);
		node2Area.setText(n2);
	}

	private void setText1(String lbl) {
		node1Area.setText(lbl);
		node1Area.setEnabled(false);
	}

	private void setText2(String lbl) {
		node2Area.setText(lbl);
		node2Area.setEnabled(false);
	}

	public static Edge showDialog(java.awt.Component frameComp, java.awt.Component locationComp, Node node1, Node node2,
			NetQuantSettings ss) {
		java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
		edgeDialog = new EdgeDialog(frame, locationComp, ss);
		if (node1.getLabel().compareTo("unnamed") != 0) {
			edgeDialog.setText1(node1.getLabel());

		}
		if (node2.getLabel().compareTo("unnamed") != 0) {
			edgeDialog.setText2(node2.getLabel());
			// edgeDialog.setNodes(node1.lbl,node2.lbl);
		}
		edgeDialog.pack();
		edgeDialog.setVisible(true);
		return e;
	}

	public static Edge showDialog(java.awt.Component frameComp, java.awt.Component locationComp, Node node1,
			Node node2) {
		return EdgeDialog.showDialog(frameComp, locationComp, node1, node2, new NetQuantSettings());
	}

	public static String showDialog(java.awt.Component frameComp, java.awt.Component locationComp) {
		java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
		edgeDialog = new EdgeDialog(frame, locationComp);
		edgeDialog.pack();
		edgeDialog.setVisible(true);
		return "ok";
	}

	// handle events here
	private void cancelButtonActionPerformed() {
		// TODO add your handling code here:

		e = null;
		setVisible(false);

	}

	// handle events here
	private void submitButtonActionPerformed() {
		// TODO add your handling code here:
		e = getEdge();
		// System.out.println(e.report());
		setVisible(false);
	}

	private static Edge e = null;

	private float getConf() {
		float conf = 1.0f;
		try {
			conf = Float.parseFloat(confidenceArea.getText());
		} catch (NumberFormatException ne) {
			conf = 1.0f;
		}
		return conf;
	}

	private double getOrientation() {
		double orientation = 0.0;
		try {
			orientation = Double.parseDouble(orientationArea.getText());
		} catch (NumberFormatException ne) {
			orientation = 0.0;
		}
		return orientation;
	}

	private Edge getEdge() {
		String n1 = node1Area.getText();
		String n2 = node2Area.getText();
		int type = interactionCB.getSelectedIndex() + 1;

		return new Edge(n1, n2, getConf(), type, getOrientation());
	}

}
