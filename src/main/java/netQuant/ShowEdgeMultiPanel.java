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


public class ShowEdgeMultiPanel extends javax.swing.JPanel 
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5938523200085247937L;
	private NetQuantSettings ss;
    //private ShowEdgePanel[] seps;
    private netQuant.NetQuantFrame parent; //ref to parent
    
    private final int baseSize=20;

    /**
     * Constructor
     */
    public ShowEdgeMultiPanel() {
        this.ss=new NetQuantSettings();
        initComponents();
        initEdgePanels();
    }
    
    /**
     * Constructor
     * @param parent
     * @param ss
     */
    public ShowEdgeMultiPanel(netQuant.NetQuantFrame parent,NetQuantSettings ss) {
        this.ss=ss;
        this.parent=parent;
        initComponents();
        initEdgePanels();
    }

    
 //   private ShowEdgePanel sp = new ShowEdgePanel(
 //           new Color(244,20,120),"My interaction",baseSize,baseSize,1, this);
    
    /**
     * update NetQuantSettings
     * @param ss
     */
    public void updateSettings(NetQuantSettings ss){
    	removeAll();
        this.ss=ss;
        initEdgePanels();
    }
    
    /**
     * initialize edge panels
     */
    private void initEdgePanels(){
        Integer temp;
	//seps=new ShowEdgePanel[ss.getSize()];
        ShowEdgePanel sp;
        for (int i=1; i<=ss.getSize();i++){
	    temp=new Integer(i);
            sp=new ShowEdgePanel(ss.getColor(temp),
                    ss.getName(temp,0), baseSize, baseSize,i, this);
	    //seps[i-1].addItemListener(this);
            add(sp);
            
        }
	//pack();
    }

    /**
     * initialize components
     */
    private void initComponents() {

        setLayout(new java.awt.GridLayout(0, 1));

        setBackground(new java.awt.Color(238, 238, 238));
	
    }
    
    /**
     * pass to the parent to handle the edge event
     * @param number
     * @param selected
     */
    protected void handleEdgeEvent(int number, boolean selected){
	
    	parent.handleEdgeEvent(number, selected);
    }
        
 
}
