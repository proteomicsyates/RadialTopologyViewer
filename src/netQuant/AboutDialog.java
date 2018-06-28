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


import javax.swing.JDialog;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Container;

/**
 * Show credits and address to documentation
 */
public class AboutDialog extends JDialog implements ActionListener{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -920978918283603037L;
	private static AboutDialog aboutFrame;
    
    private AboutDialog(java.awt.Frame frame, java.awt.Component locationComp, String v){
        super(frame, "About NetQuant",true);
        setDefaultLookAndFeelDecorated(true);
        version = v;
        //ss.report();
        init();
        setLocationRelativeTo(locationComp);
    }
    JPanel aboutPanel;
    JLabel aboutLabel;
    JLabel titleLabel;
    JPanel southPanel;
    private String version=null;
    JLabel westLabel;
    JLabel versionLabel;
    
    /**
     * initialization
     */
    private void init(){
        aboutPanel=new JPanel();
        southPanel=new JPanel();
        aboutLabel=new JLabel(aboutText);
        versionLabel=new JLabel("Version: "+version);
        aboutLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
       
        titleLabel=new JLabel(titleText);
        
        titleLabel.setBackground(NetQuantFrame.STRINGCOLOR);
        java.net.URL imageURL =
                this.getClass().getResource("/netQuant/images/medusa_logo_small.png");
        titleLabel.setIcon(new javax.swing.ImageIcon(imageURL));
        
        titleLabel.setOpaque(true);
        Container content = getContentPane();
        aboutPanel.setLayout(new BorderLayout());
        if (version!=null){
            southPanel.add(versionLabel);
        }
        southPanel.add(submitButton);
        content.add(aboutPanel);
        aboutPanel.add("North",titleLabel);
        
        aboutPanel.add("Center",aboutLabel);
        submitButton.addActionListener(this);
        aboutPanel.add("South",southPanel);
        //aboutPanel.add("West",westLabel);
    }
    
    /**
     * Actions when pressing buttons in dialog
     * @param ae
     */
    public void actionPerformed(ActionEvent ae) {
        Object obj = ae.getSource();
        
        //submit/done button action
        if(obj.equals(submitButton)) {
            submitButtonActionPerformed();
        }
        
    }
    
    /**
     * put visible the window
     */
    private void submitButtonActionPerformed() {
      
        setVisible(false);
    }
    
    String titleText="<html><center><h2>NetQuant</h2></center></html>";
    String aboutText="<html><center>Author: Diego Calzolari"+
            "<p>Yates Group<p>TSRI La Jolla, CA 2011</center>"+
            "<p><center><b>calzolar@scripps.edu</b>"
            +"<br>Full documentation at<p>fields.scripps.edu/NetQuant</center></html>";
                
    
    
    
    private JButton submitButton = new JButton("OK");
    
    /**
     * Show the dialog
     * @param frameComp Parent frame
     * @param locationComp Location
     */
  //  public static void showDialog(java.awt.Component frameComp,
  //          java.awt.Component locationComp){
  //      java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
        
   //     aboutFrame=new AboutDialog(frame, locationComp);
    //    aboutFrame.pack();
   //     aboutFrame.setVisible(true);
  //  }
    
    
    public static void showDialog(java.awt.Component frameComp,
            java.awt.Component locationComp,
            String version){
        java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
        aboutFrame=new AboutDialog(frame, locationComp, version);
        aboutFrame.setVersion(version);
        aboutFrame.setSize(200, 300);
      //  aboutFrame.pack();
        aboutFrame.setVisible(true);
    }
    public void setVersion(String version){
        this.version=version;
    }
}
