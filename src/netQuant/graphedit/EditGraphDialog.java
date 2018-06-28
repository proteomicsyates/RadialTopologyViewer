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
 * EditGraphDialog.java
 *
 * Created on August 7, 2006, 7:07 PM
 */

package netQuant.graphedit;

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;

import netQuant.graph.Edge;
import netQuant.graph.Graph;
import netQuant.graph.Node;

/**
 *
 * @author  hooper
 */
public class EditGraphDialog extends javax.swing.JDialog {
    
    private EdgeTableModel edgeTableModel;
    private NodeTableModel nodeTableModel;
    private NewEdgeDialog newEdgeDialog;
    
    /** Creates new form EditGraphDialog */
    public EditGraphDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
             }
    
    private void setModels(){
        edgeTableModel=new EdgeTableModel(g);
        
        nodeTableModel=new NodeTableModel(g);
        newEdgeDialog = new NewEdgeDialog(null,true);
        initComponents();
        TableColumn col = nodeTable.getColumnModel().getColumn(0);
        col.setCellEditor(new NodeNameEditor(g,this));
    }
    
    private Graph g;
    
    private void updateTables(){
        edgeTableModel.init();
        edgeTable.updateUI();
        nodeTableModel.init();
        nodeTable.updateUI();
    }
    
    protected void notifyNodeNameChange(String oldLabel, String newLabel){
        //g.renameNodes(oldLabel,newLabel);
        edgeTableModel.init();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableSorter edgeSorter = new TableSorter(edgeTableModel);

        edgeTable = new javax.swing.JTable();
        edgeTable.setModel(edgeSorter);
        edgeSorter.setTableHeader(edgeTable.getTableHeader());
        jPanel2 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableSorter nodeSorter = new TableSorter(nodeTableModel);
        nodeTable = new javax.swing.JTable();
        nodeTable.setModel(nodeSorter);
        //jTable1.setModel(new NodeTableModel(g));
        nodeSorter.setTableHeader(nodeTable.getTableHeader());
        nodeTable.setDefaultRenderer(Color.class, new NodeColorRenderer(true));
        nodeTable.setDefaultEditor(Color.class, new ColorEditor());
        controlPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Graph editor");
        getContentPane().add(titleLabel, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(edgeTable);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jPanel2.add(addButton);

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jPanel2.add(deleteButton);

        jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("Edges", jPanel1);

        jScrollPane2.setViewportView(nodeTable);

        jTabbedPane1.addTab("Nodes", jScrollPane2);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        controlPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        controlPanel.add(cancelButton);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
// TODO add your handling code here:
        //System.out.println("Remove edge");
        edgeTableModel.removeEdges();
        edgeTable.updateUI();
        nodeTableModel.init();
        //removeEdge();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
// TODO add your handling code here:
        //System.out.println("Add an edge. Make a dialog!");
        newEdgeDialog.setVisible(true);
        Edge e =newEdgeDialog.getEdge();
        g.addEdge(e);
        
        //edgeTableModel.setG(g);
        edgeTableModel.init();
       TableSorter te= new TableSorter(edgeTableModel);      
        edgeTable.setModel(te);
        te.setTableHeader(edgeTable.getTableHeader());
        edgeTable.updateUI();
        
        //nodeTableModel.setG(g);
        nodeTableModel.init();
        TableSorter tn = new TableSorter(nodeTableModel);
        nodeTable.setModel(tn);
        tn.setTableHeader(nodeTable.getTableHeader());
        nodeTable.updateUI();
        JOptionPane.showMessageDialog(this,"Edge added. Remember to set X and Y position of\n"+
                "any new nodes that have resulted from this addition.");
    }//GEN-LAST:event_addButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
// TODO add your handling code here:
        g=getGraph();
        //System.out.println(g.report());
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditGraphDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

 

    public void setGraph(Graph g) {
        this.g = g;
        setModels();
       
    }
    
    private Graph newGraph(){
        //Graph g = getGraph();
        return new Graph();
    }
    
    public Graph getGraph(){
       Graph graph= edgeTableModel.getGraph();
        Node[] n=nodeTableModel.getNodes();
        for (int i = 0; i<n.length; i++){
            graph.setNode(n[i]);
        }
        
        return graph;
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTable edgeTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable nodeTable;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
    
}
