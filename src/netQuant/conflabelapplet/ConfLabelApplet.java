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

package netQuant.conflabelapplet;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;

import netQuant.NetQuantSettings;
import netQuant.applet.NetQuantLite;

public final class ConfLabelApplet extends NetQuantLite {
    
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7150266846494000385L;


	public void initPanel(){
        System.out.println("Initializing panel");
        String settingsParam=getParameter("settings");
        
        NetQuantSettings stringSettings = new NetQuantSettings(settingsParam);
        
        String linkStart=getParameter("linkStart");
        String linkEnd=getParameter("linkEnd");
        System.out.println(linkStart);
        setPanel(new ConfLabelAppletPanel(stringSettings, this, linkStart, linkEnd));
        System.out.println(getPanel());
    }
    
    JCheckBox relax=new JCheckBox("Relax",true);
    JCheckBox names = new JCheckBox("Labels", true);
    JButton frButton = new JButton("Layout");
    JScrollPane jScrollPane;
    final Color christian=new Color(230,226,230);
    
    
    public void populateControlPanel(){
        
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(christian);
        relax.setBackground(christian);
        
        names.setBackground(christian);
        frButton.setBackground(christian);
        controlPanel.add(relax); relax.addItemListener(this);
        
        //controlPanel.add(names); names.addItemListener(this);
        controlPanel.add(frButton); frButton.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object es=e.getSource();
        if (es==frButton){
            //panel.startCool();
            relax.setSelected(false);
            panel.energy();
        }
        //if (es == imageButton){
        
    }
    
    public void itemStateChanged(ItemEvent e){
        Object src = e.getSource();
        if (src==relax){
            if (relax.isSelected()){
                panel.start();
                //panel.energy();
            } else
                panel.stop();
        }
        
    }
}