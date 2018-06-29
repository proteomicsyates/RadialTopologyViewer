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
 * NodeNameEditor.java
 *
 * Created on November 15, 2006, 4:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netQuant.graphedit;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import netQuant.graph.Graph;

/**
 *
 * @author hooper
 */
public class NodeNameEditor extends AbstractCellEditor
        implements TableCellEditor{
    Graph g;
    String label;
    EditGraphDialog parent;
    JComponent component = new JTextField();
    
    /** Creates a new instance of NodeNameEditor */
    public NodeNameEditor(Graph g, EditGraphDialog parent) {
        this.g=g;
        this.parent=parent;
    }
    
    public Object getCellEditorValue() {
        String newValue=((JTextField)component).getText();
        parent.notifyNodeNameChange(label,newValue);
        //g.renameNodes(label,newValue);
        //System.out.println(newValue);
        return newValue;
        
    }
    

    
    /**
     * Calls <code>fireEditingStopped</code> and returns true.
     *
     * @return true
     */
    public boolean stopCellEditing() {
        boolean retValue;
        //System.out.println(label);
        retValue = super.stopCellEditing();
        return retValue;
    }
    
    /**
     *  Sets an initial <code>value</code> for the editor.  This will cause
     *  the editor to <code>stopEditing</code> and lose any partially
     *  edited value if the editor is editing when this method is called. <p>
     *
     *  Returns the component that should be added to the client's
     *  <code>Component</code> hierarchy.  Once installed in the client's
     *  hierarchy this component will then be able to draw and receive
     *  user input.
     *
     * @param table		the <code>JTable</code> that is asking the
     * 				editor to edit; can be <code>null</code>
     * @param value		the value of the cell to be edited; it is
     * 				up to the specific editor to interpret
     * 				and draw the value.  For example, if value is
     * 				the string "true", it could be rendered as a
     * 				string or it could be rendered as a check
     * 				box that is checked.  <code>null</code>
     * 				is a valid value
     * @param isSelected	true if the cell is to be rendered with
     * 				highlighting
     * @param row     	the row of the cell being edited
     * @param column  	the column of the cell being edited
     * @return the component for editing
     */
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
        label=(String) value;
        System.out.println(value);
        ((JTextField)component).setText((String)value);
        return component;
    }
    
    
}
