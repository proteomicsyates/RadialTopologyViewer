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


/*
 * colorJDialog.java
 *
 * Created on den 31 maj 2005, 21:40
 */
/**
 * use???
 */
package netQuant;

//import javax.swing.JDialog;
//import java.awt.Component;
//import javax.swing.JOptionPane;
//import java.awt.GridBagConstraints;
//import javax.swing.ButtonGroup;
//import javax.swing.JPanel;
//import javax.swing.JLabel;
//import javax.swing.JRadioButton;
//import javax.swing.JButton;
//import javax.swing.WindowConstants;
//import java.awt.GridBagLayout;
//import javax.swing.border.LineBorder;
//import java.awt.Color;
//import java.awt.BorderLayout;
//import java.awt.EventQueue;
//import javax.swing.JFrame;
//import java.awt.Frame;
//
import java.awt.Dimension;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;


public class ColorJDialog extends javax.swing.JDialog {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -543245158971231376L;

	/** Creates new form colorJDialog */
    public ColorJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public ColorJDialog(java.awt.Frame frame, 
			java.awt.Component locationComp,
			String label) {
		super(frame, "Color", true);
		this.label=label;
		setDefaultLookAndFeelDecorated(true);
		//super("Data retrieval");
	        initComponents();
		setLocationRelativeTo(locationComp);
    }
    
    private String label;
    private static ColorJDialog cjd;

    public static int[] showDialog(java.awt.Component frameComp,
				   java.awt.Component locationComp,
				   String label
				   ){
		java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
		cjd=new ColorJDialog(frame, locationComp, label);
		//cjd.setSize(450,300);
		cjd.setVisible(true);
		int[] cols = new int[2];
		cols[0]=cjd.getColor1();
		cols[1]=cjd.getColor2();
		return cols;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        color1Label = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
	
	Dimension buttonSize = new Dimension(40,20);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        color1Label.setText("Channel 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipady = 10;
        jPanel1.add(color1Label, gridBagConstraints);

        jRadioButton1.setBackground(new java.awt.Color(255, 0, 0));
        buttonGroup1.add(jRadioButton1);
        
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		button1ActionPerformed(evt);
        	}
	    });
        
        jRadioButton1.setPreferredSize(buttonSize);
        jRadioButton1.setText("R");
        jRadioButton1.setSelected(true);
        //jRadioButton1.setActionCommand("Red  ");
        jRadioButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jRadioButton1.setBorderPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jRadioButton1, gridBagConstraints);

        jRadioButton2.setBackground(new java.awt.Color(0, 255, 0));
        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setPreferredSize(buttonSize);
        jRadioButton2.setText("G");
        jRadioButton2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jRadioButton2.setBorderPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			    button2ActionPerformed(evt);
			}
	    });
        
        jPanel1.add(jRadioButton2, gridBagConstraints);

        jRadioButton3.setBackground(new java.awt.Color(0, 0, 255));
        buttonGroup1.add(jRadioButton3);
        //jRadioButton3.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButton3.setPreferredSize(buttonSize);
        jRadioButton3.setText("B");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			    button3ActionPerformed(evt);
			}
	    });
        jRadioButton3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jRadioButton3.setBorderPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jRadioButton3, gridBagConstraints);

        jLabel2.setText(label);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        jPanel1.add(jLabel2, gridBagConstraints);

        jRadioButton4.setBackground(new java.awt.Color(255, 0, 0));
        buttonGroup2.add(jRadioButton4);
        //jRadioButton4.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButton4.setPreferredSize(buttonSize);
        jRadioButton4.setText("R");
        //jRadioButton4.setActionCommand("Red  ");
        jRadioButton4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jRadioButton4.setBorderPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jRadioButton4, gridBagConstraints);

        jRadioButton5.setBackground(new java.awt.Color(0, 255, 0));
        buttonGroup2.add(jRadioButton5);
        jRadioButton5.setPreferredSize(buttonSize);
        jRadioButton5.setText("G");
        jRadioButton5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jRadioButton5.setBorderPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jRadioButton5, gridBagConstraints);

        jRadioButton6.setBackground(new java.awt.Color(0, 0, 255));
        buttonGroup2.add(jRadioButton6);
        //jRadioButton6.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButton6.setPreferredSize(buttonSize);
        jRadioButton6.setText("B");
        jRadioButton6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jRadioButton6.setBorderPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jRadioButton6, gridBagConstraints);

        jLabel3.setText("Channel 2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 10;
        jPanel1.add(jLabel3, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Select channels to switch");
        getContentPane().add(jLabel1, java.awt.BorderLayout.NORTH);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			    okActionPerformed(evt);
			}
	    });
        jPanel2.add(jButton1);

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		cancelActionPerformed(evt);
        	}
	    });
        jPanel2.add(jButton2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ColorJDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel color1Label;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;

    /**
     * Describe color1 here.
     */
    private int color1=0;

    /**
     * Describe color2 here.
     */
    private int color2=0;
    

    /**
     * Get the <code>Color2</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getColor2() {
    	return color2;
    }

    /**
     * Set the <code>Color2</code> value.
     *
     * @param newColor2 The new Color2 value.
     */
    public final void setColor2(final int newColor2) {
    	this.color2 = newColor2;
    }
    /**
     * Get the <code>Color1</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getColor1() {
    	return color1;
    }

    /**
     * Set the <code>Color1</code> value.
     *
     * @param newColor1 The new Color1 value.
     */
    public final void setColor1(final int newColor1) {
    	this.color1 = newColor1;
    }

    private void button1ActionPerformed(java.awt.event.ActionEvent evt){
    	setColor1(0);
    } 
    
    private void button2ActionPerformed(java.awt.event.ActionEvent evt){
    	setColor1(1);
    }
    
    private void button3ActionPerformed(java.awt.event.ActionEvent evt){
    	setColor1(2);
    }
    
    private void button4ActionPerformed(java.awt.event.ActionEvent evt){
    	setColor2(0);
    }
    
    private void button5ActionPerformed(java.awt.event.ActionEvent evt){
    	setColor2(1);
    }
    
    private void button6ActionPerformed(java.awt.event.ActionEvent evt){
    	setColor2(2);
    }

    private void okActionPerformed(java.awt.event.ActionEvent evt){
    	setVisible(false);
    }
    
    private void cancelActionPerformed(java.awt.event.ActionEvent evt){
    	setColor1(1);
    	setColor2(1);
    	setVisible(false);
    }
}
