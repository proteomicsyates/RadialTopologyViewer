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


package netQuant;

import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Show a label and color on an edge
 * @author diego
 *
 */
public class ShowEdgePanel extends javax.swing.JPanel implements ItemListener{

	private static final long serialVersionUID = 3166480230116054475L;

	private java.awt.Color labelColor;
    private String text;
    private int labelWidth=40;
    private int labelHeight=40;
    private int number=0;
    private ShowEdgeMultiPanel parent;
    
    /** 
     *	constructor
     */
    public ShowEdgePanel() {
        labelColor=new java.awt.Color(200,200,200);
        text="empty";
        initComponents();
    }
    
    /**
     * constructor
     * @param labelColor
     * @param text
     * @param labelWidth
     * @param labelHeight
     * @param number
     * @param parent
     */
    public ShowEdgePanel(java.awt.Color labelColor, String text,
            int labelWidth, int labelHeight, int number, ShowEdgeMultiPanel parent){
    	this.parent=parent;
        this.labelColor=labelColor;
        this.text=text;
        this.labelWidth=labelWidth;
        this.labelHeight=labelHeight;
        this.number=number;
	
        initComponents();
	
    }
 
    /**
     * handle state change
     */
    public void itemStateChanged(ItemEvent e){
    	 parent.handleEdgeEvent(getNumber(), edgeNameCheckBox.isSelected());
    }
   
    /**
     * initialize components
     */
    private void initComponents() {
        numberLabel = new javax.swing.JLabel();
        edgeColorLabel = new javax.swing.JLabel();
        edgeColorLabel.setBackground(labelColor);
        Dimension d = new Dimension(labelWidth,labelHeight);
        edgeColorLabel.setMaximumSize(d);
        edgeColorLabel.setMinimumSize(d);
        edgeColorLabel.setOpaque(true);
        edgeColorLabel.setPreferredSize(d);
        edgeNameCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        numberLabel.setText("jLabel1");
        
        if (number>0) {     
        		numberLabel.setText(Integer.toString(number)); 
        } else {
        	numberLabel.setText(" "); 
        }
        add(numberLabel);

        edgeColorLabel.setBorder(new javax.swing.border.SoftBevelBorder
				 (javax.swing.border.BevelBorder.RAISED));
        add(edgeColorLabel);

        //edgeNameCheckBox.setText("sample interaction");
        edgeNameCheckBox.setOpaque(false);
        edgeNameCheckBox.setSelected(true);
        edgeNameCheckBox.setText(text);
        edgeNameCheckBox.addItemListener(this);
        add(edgeNameCheckBox);

    }
    
    
    
    private javax.swing.JLabel edgeColorLabel;
    private javax.swing.JCheckBox edgeNameCheckBox;
    private javax.swing.JLabel numberLabel;
    
    /**
     * return the label text
     * @return text
     */
    public String toString(){
        return text;
    }

    /**
     * return the edge number
     * @return number
     */
    public int getNumber(){
    	return number;
    }
}


