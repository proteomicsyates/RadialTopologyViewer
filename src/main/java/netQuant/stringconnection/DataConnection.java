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


package netQuant.stringconnection;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import netQuant.graph.Graph;




public class DataConnection extends javax.swing.JDialog 
    implements ItemListener, ActionListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7722416466376381737L;

	/** Creates new DataConnection */
    public DataConnection(java.awt.Frame frame, 
			   java.awt.Component locationComp) {
    	super(frame, "String", true);
    	setDefaultLookAndFeelDecorated(true);
	//super("Data retrieval");
        initComponents();
        setLocationRelativeTo(locationComp);
    }
  
    String intialSelection="";

    private static DataConnection dataConnection;
   
    final int preferredHeight=20;
    final int preferredWidth=50;
    final java.awt.Color stringColor=new java.awt.Color(161, 173, 236);
    private ArrayList <String>queryList = new ArrayList<String>(); 
    private boolean cogMode = false;
    // holder for the data
    //TimeNet.StringSet hs = new TimeNet.StringSet();
    private static Graph graph = new Graph();

    AnimPanel ap;

    /**
     * initialize components
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
		
        buttonGroup1 = new javax.swing.ButtonGroup();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        dataConnectionTabbedPane = new javax.swing.JTabbedPane();
        queryPanel = new javax.swing.JPanel();
        geneLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        nodeTextArea = new javax.swing.JTextArea();
        cancelFirstButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        organismBox = new javax.swing.JComboBox();
        submitFirstButton = new javax.swing.JButton();
        currentNodesCheckBox = new javax.swing.JCheckBox();
        dataPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        selectPanel = new javax.swing.JPanel();
        exampleNodeRadioButton2 = new javax.swing.JRadioButton();
        exampleNodeRadioButton1 = new javax.swing.JRadioButton();
        subDataPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel("label 4");
        depthComboBox = new javax.swing.JComboBox();
        submitFinalButton = new javax.swing.JButton();
        cancelFinalButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        clearTextButton = new javax.swing.JButton("Clear");
        cogBox = new javax.swing.JCheckBox("Cog mode");

        jRadioButton3.setText("jRadioButton3");

        jLabel1.setBackground(new java.awt.Color(161, 173, 236));
        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("STRING Data Connection");
        jLabel1.setOpaque(true);
        getContentPane().add(jLabel1, java.awt.BorderLayout.NORTH);

        dataConnectionTabbedPane.setBackground(new java.awt.Color(161, 173, 236));
        dataConnectionTabbedPane.setOpaque(true);
        queryPanel.setLayout(new java.awt.GridBagLayout());

        queryPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        queryPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        geneLabel.setText("Gene name");
        geneLabel.setToolTipText("Enter gene names here");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0; // was 2
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        queryPanel.add(geneLabel, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 80));
        jScrollPane1.setViewportView(nodeTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 176;
        gridBagConstraints.ipady = 56;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        queryPanel.add(jScrollPane1, gridBagConstraints);

        cancelFirstButton.setText("Cancel");
        cancelFirstButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    cancelFirstButtonActionPerformed(evt);
		}
	    });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        queryPanel.add(cancelFirstButton, gridBagConstraints);

        jLabel3.setText("Minimum score cutoff");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(3, 29, 3, 29);
        queryPanel.add(jLabel3, gridBagConstraints);

        buildOrganismBox();

        organismBox.setPreferredSize(new java.awt.Dimension(40, 20));
        organismBox.addItemListener(new java.awt.event.ItemListener() {
		public void itemStateChanged(java.awt.event.ItemEvent evt) {
		    organismBoxItemStateChanged(evt);
		}
	    });
	
	
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 74;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        queryPanel.add(organismBox, gridBagConstraints);

        submitFirstButton.setText("Submit");
        submitFirstButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    submitFirstButtonActionPerformed(evt);
		}
	    });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        queryPanel.add(submitFirstButton, gridBagConstraints);


        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        queryPanel.add(cogBox);
        
        cogBox.addItemListener(new java.awt.event.ItemListener() {
        	public void itemStateChanged(java.awt.event.ItemEvent evt) {
		    cogBoxItemStateChanged(evt);
		}
	    });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        //queryPanel.add(currentNodesCheckBox, gridBagConstraints);

	// the clear button is added here
        queryPanel.add(clearTextButton,gridBagConstraints);
        clearTextButton.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		clearTextButtonActionPerformed(evt);
		}
	    });

        dataConnectionTabbedPane.addTab("Query", queryPanel);

        dataPanel.setLayout(new java.awt.BorderLayout());

        dataPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(300, 300));
        selectPanel.setLayout(new javax.swing.BoxLayout(selectPanel, javax.swing.BoxLayout.Y_AXIS));

        selectPanel.setBackground(new java.awt.Color(255, 255, 255));
       
        jScrollPane2.setViewportView(selectPanel);

        dataPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jLabel4.setText("Depth");
        //subDataPanel.add(jLabel4);
        progressBar2 = new JProgressBar(0,500);
        progressBar2.setStringPainted(true);
        progressBar2.setValue(0);
        progressBar2.setString("Getting data");
        progressBar2.setIndeterminate(false);
        subDataPanel.add(progressBar2);

        //depthComboBox.setPreferredSize(new java.awt.Dimension(50, preferredHeight));
        depthComboBox.addItemListener(new java.awt.event.ItemListener() {
		public void itemStateChanged(java.awt.event.ItemEvent evt) {
		    depthComboBoxItemStateChanged(evt);
		}
	    });

	// subDataPanel.add(depthComboBox);

        submitFinalButton.setText("Submit");
        submitFinalButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    submitDataButtonActionPerformed(evt);
		}
	    });

        subDataPanel.add(submitFinalButton);

        cancelFinalButton.setText("Cancel");
        cancelFinalButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    cancelFinalButtonActionPerformed(evt);
		}
	    });

        subDataPanel.add(cancelFinalButton);

        dataPanel.add(subDataPanel, java.awt.BorderLayout.SOUTH);

        jLabel5.setText("Select genes");
        dataPanel.add(jLabel5, java.awt.BorderLayout.NORTH);

        dataConnectionTabbedPane.addTab("Data", dataPanel);
        int dataIndex = dataConnectionTabbedPane.indexOfTab("Data");
        dataConnectionTabbedPane.setEnabledAt(dataIndex,false);
	
        getContentPane().add(dataConnectionTabbedPane, java.awt.BorderLayout.CENTER);

        //jLabel2.setBackground(stringColor);
        //jLabel2.setIcon(new javax.swing.ImageIcon("logo_still_p.gif"));
        //jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        //jLabel2.setOpaque(true);
        ap=new AnimPanel();
        getContentPane().add(ap,java.awt.BorderLayout.WEST);
	//java.awt.Dimension d=new java.awt.Dimension(600,400);
	//setPreferredSize(d);
	
        acceptPanel = new javax.swing.JPanel();
        subAcceptPanel = new javax.swing.JPanel();
        acceptLabel = new javax.swing.JLabel();
        acceptLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        colorButton = new javax.swing.JButton("Color");
        colorButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    colorButtonActionPerformed(evt);
		}
	    });
        shapeButton = new javax.swing.JButton("Shape");
        shapeButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    shapeButtonActionPerformed(evt);
		}
	    });
        acceptAndCloseButton = new javax.swing.JButton("Accept");
        acceptAndCloseButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    submitFinalButtonActionPerformed(evt);
		}
	    });
        cancelAndCloseButton = new javax.swing.JButton("Cancel");
        cancelAndCloseButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    cancelFinalButtonActionPerformed(evt);
		}
	    });

		acceptPanel.setLayout(new java.awt.BorderLayout());
		subAcceptPanel.setLayout(new java.awt.FlowLayout());
		acceptPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		subAcceptPanel.add(acceptAndCloseButton);
		subAcceptPanel.add(cancelAndCloseButton);
		subAcceptPanel.add(colorButton);
		subAcceptPanel.add(shapeButton);
		acceptPanel.add("Center",acceptLabel);
		acceptPanel.add("South",subAcceptPanel);
		dataConnectionTabbedPane.addTab("Accept", acceptPanel);
		int acceptIndex = dataConnectionTabbedPane.indexOfTab("Accept");
		dataConnectionTabbedPane.setEnabledAt(acceptIndex,false);
		
        pack();
    }
    
    /**
     *  provide a number of initial nodes as an example
     */
    public void exampleNodes(){
    	nodeTextArea.append("TRPB_SYNY3");

    }

    private void setInitialSelection(String inLine){
    	nodeTextArea.append(inLine);
    }

    /**
     * Ask for the confirmation on data import
     */
    private void updateAccept(){
	
    	String text="<html>Data to import:<p>"+
	    "<center>nodes: <b>"+graph.getNodeSize()+
	    "</b><p>edges: <b>"+graph.getEdgeSize()+
	    "</b><p></center><p>Accept this?";
    	acceptLabel.setText(text);
    }
    
    private void submitFinalButtonActionPerformed(java.awt.event.ActionEvent evt) {
	// TODO add your handling code here:
	setVisible(false);
	
    }        
    
    private void colorButtonActionPerformed (java.awt.event.ActionEvent evt) {
    	java.awt.Color newColor=javax.swing.JColorChooser.showDialog(getRootPane(),
								     "Choose node color",
								     java.awt.Color.red);
    	if (newColor!=null)
	    graph.setNodeColor(newColor);
    }


    private void shapeButtonActionPerformed (java.awt.event.ActionEvent evt) {
	Object[] shapes = {"circle","rectangle","triangle","diamond"};
	int s =javax.swing.JOptionPane.showOptionDialog
	    (this,
	     "Choose a node shape",
	     "Shape",
	     javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
	     javax.swing.JOptionPane.QUESTION_MESSAGE,
	     null,
	     shapes,
	     "circle");
	graph.setNodeShape(s);
    
    }

    private void submitDataButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
	getGraph();
	//annotateGraph();
    }

    private void depthComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_depthComboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_depthComboBoxItemStateChanged

    private String organism="All";
    private float cutoff=0.3f;
    private void organismBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_organismBoxItemStateChanged
        Float temp=(Float)organismBox.getSelectedItem();
	cutoff=temp.floatValue();
    }//GEN-LAST:event_organismBoxItemStateChanged


    private void cancelFirstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelFirstButtonActionPerformed
        // TODO add your handling code here:
	cancelAction();
    }//GEN-LAST:event_cancelFirstButtonActionPerformed

    private void clearTextButtonActionPerformed(java.awt.event.ActionEvent evt){
	nodeTextArea.setText("");
    }

    private void submitFirstButtonActionPerformed(java.awt.event.ActionEvent evt) {
	//try {
	    //ap.start();
	    if (!cogMode){
		connect();
		
	    }
	    if (cogMode){
	
		getCogGraph();
	
	    }
    }


    private void cogBoxItemStateChanged(java.awt.event.ItemEvent evt) {
	cogMode=cogBox.isSelected();
	//System.out.println("cog?: "+cogMode);
        // TODO add your handling code here:
    }                                                     


    private void currentNodesCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_currentNodesCheckBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_currentNodesCheckBoxItemStateChanged

    private void cancelFinalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelFinalButtonActionPerformed
        cancelAction();
    }//GEN-LAST:event_cancelFinalButtonActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    private void cancelAction(){
	ap.stop();
	graph=null;
	setVisible(false);
    }
    /**
     * @param args the command line arguments
     */
    /*
      public static void main(String args[]) {
      DataConnection dc = new DataConnection();
      dc.setSize(450,300);
      dc.exampleNodes();
      dc.setVisible(true);
      }
    */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelFinalButton;
    private javax.swing.JButton cancelFirstButton;
    private javax.swing.JCheckBox currentNodesCheckBox;
    private javax.swing.JTabbedPane dataConnectionTabbedPane;
    private javax.swing.JComboBox depthComboBox;
    private javax.swing.JLabel geneLabel;
    private javax.swing.JComboBox organismBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JPanel selectPanel;
    private javax.swing.JPanel subDataPanel;
    
    private javax.swing.JRadioButton exampleNodeRadioButton1;
    private javax.swing.JRadioButton exampleNodeRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea nodeTextArea;
    private javax.swing.JButton submitFinalButton;
    private javax.swing.JButton submitFirstButton;
    
    // accept panel components
    private javax.swing.JPanel acceptPanel;
    private javax.swing.JLabel acceptLabel;
    private javax.swing.JButton colorButton;
    private javax.swing.JButton shapeButton;
    private javax.swing.JButton acceptAndCloseButton;
    private javax.swing.JButton cancelAndCloseButton;
    private javax.swing.JPanel subAcceptPanel;
    private javax.swing.JButton clearTextButton;
    // End of variables declaration//GEN-END:variables
 
    // cog mode?
    private javax.swing.JCheckBox cogBox;


    /* Build the list of queries to send to STRING
     */
    private void buildQuery(){
	queryList.clear();
	String[] queries = nodeTextArea.getText().split("\\n");
	String suffix="";
	if (organism.compareTo("All")!=0)
	    suffix="_"+organism;
	for (int i=0; i<queries.length;i++){
	    queryList.add("GN:"+queries[i]+suffix);
	}
    }
    /* Send queries while queries remain
     */
    //private String sendQuery(){
	

    /**
     * connection to string
     */
    private final int port = 4444;
    private final String url = "string.embl.de";
    /* progress bars to show that we are doing something
     */
    private ProgressMonitor progressBar;
    private JProgressBar progressBar2;
    
    private ActionListener updateEnergyBar;
    private Timer timer;
    private Timer timer2;
    private CallString cs;
    private GetGraph gg;
    /* The call methods to the socketListener
     */
   
    public void testingThreads(){
		ArrayList<String> al = new ArrayList<String>();
		
		al.add("1076.CAE25513");
		al.add("1148.TRPB_SYNY3");
		al.add("117.TRPB_RHOBA");
		al.add("118099.TRPB_BUCAI");
			gg=new GetGraph(url,port,al,0.0f);
		
		timer2 = new Timer(200,getGraphActionListener);
		timer2.start();
		gg.start();
    }
    
    public void connect(){
        
	String[] queries = nodeTextArea.getText().split("\\n");
	progressBar = 
	    new ProgressMonitor(this,"Sending query","",0,queries.length);
	//progressBar.setProgress(0);
        //progressBar.setMillisToDecideToPopup(100);
	cs = new CallString(url,port,queries);
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	timer = new Timer(200,callStringActionListener);
	cs.start();
	timer.start();
    }

    public void getGraph(){
	//System.out.println("getting graph");
	ArrayList<String> selection = buildSelectionList();
	
	gg = new GetGraph(url,port,selection, cutoff);
	////progressBar2.setMaximum(500);
	//progressBar2.setValue(50);
	progressBar2.setIndeterminate(true);
	progressBar2.repaint();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	//System.out.println("starting threads");
	
	timer2 = new Timer(200,getGraphActionListener);
	
	timer2.start();
	gg.start();
		
    }
    
    private void annotateGraph(){
	
	ag = new Annotator(url,port,graph);
	progressBar2.setIndeterminate(false);
	progressBar2.setMaximum(ag.getMax());
	progressBar2.setString("Annotating");
	progressBar2.setValue(0);
	progressBar2.repaint();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	timer2 = new Timer(200,annotateGraphActionListener);
	
	timer2.start();
	ag.start();
	//System.out.println(graph.report());
	//	progressBar2.setString("start");

		
    }

    GetCogGraph gcg;
    private void getCogGraph(){
	//System.out.println("getting graph");
	String[] queries = nodeTextArea.getText().split("\\n");
	
	int dataIndex = dataConnectionTabbedPane.indexOfTab("Data");
	dataConnectionTabbedPane.setEnabledAt(dataIndex,true);
	dataConnectionTabbedPane.setSelectedIndex(dataIndex);

	gcg = new GetCogGraph(url,port,queries, cutoff);
	
	progressBar2.setIndeterminate(true);
	progressBar2.repaint();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
	timer2 = new Timer(200,getCogGraphActionListener);
	
	timer2.start();
	gcg.start();
	
	
    }
    /*public void getGraph(){
	progressBar = 
	    new ProgressMonitor(this,"Sending query","",0,queries.length);
    */
    /* the actionlisterner
     */
    private ActionListener callStringActionListener = new ActionListener(){
	    public void actionPerformed(ActionEvent evt){
		//System.out.println("prog asction fired");
		//progressBar.setProgress(cs.getProgress());
		if (progressBar.isCanceled() || cs.isDone()){
		    //System.out.println("done, stopping timer");
		    timer.stop();
		    //timer=null;
		    cs.stop();
		    //progressBar.close();
		    //progressBar.setProgress(0);
		    setCursor(null);
		    selectPanel=cs.getPanel();
		    jScrollPane2.setViewportView(selectPanel);
		    int dataIndex = dataConnectionTabbedPane.indexOfTab("Data");
		dataConnectionTabbedPane.setEnabledAt(dataIndex,true);
		dataConnectionTabbedPane.setSelectedIndex(dataIndex);
		}
	    }
	};

    private ActionListener getGraphActionListener = new ActionListener(){
	    public void actionPerformed(ActionEvent evt){
		//System.out.println("prog2 action fired");
		//System.out.println(gg.getProgress());
		progressBar2.setString(gg.getProgress()+" entries");
		if (gg.isDone()){
		    //get the graph
		    timer2.stop();
		    gg.stop();
		    progressBar2.setString("creating graph");
		    graph=gg.getGraph();
		    graph.autoFixOrientation();
		    
		    annotateGraph();
		    /*updateAccept();
		    progressBar2.setIndeterminate(false);
		    progressBar2.setString(gg.getProgress()+" entries");
		    setCursor(null);
		    int acceptIndex = 
			dataConnectionTabbedPane.indexOfTab("Accept");
		    dataConnectionTabbedPane.setEnabledAt(acceptIndex,true);
		    dataConnectionTabbedPane.setSelectedIndex(acceptIndex);*/
		}
		//	else
		    //System.out.println(gg.getProgress());

	    }
       };

        private ActionListener getCogGraphActionListener = new ActionListener(){
	    public void actionPerformed(ActionEvent evt){
		//System.out.println("prog2 action fired");
		//System.out.println(gg.getProgress());
		progressBar2.setString(gcg.getProgress()+" entries");
		if (gcg.isDone()){
		    //System.out.println("done");
		    timer2.stop();
		    gcg.stop();
		    graph=gcg.getGraph();
		    graph.autoFixOrientation();
		    updateAccept();
		    progressBar2.setIndeterminate(false);
		    progressBar2.setString(gcg.getProgress()+" entries");
		    setCursor(null);
		    if (gcg.getStatus().compareTo("ok")==0){
		    int acceptIndex = 
			dataConnectionTabbedPane.indexOfTab("Accept");
		    dataConnectionTabbedPane.setEnabledAt(acceptIndex,true);
		    dataConnectionTabbedPane.setSelectedIndex(acceptIndex);
		    }
		    else{
			selectPanel.add(new JLabel("Sorry, no matches could be found."));
			jScrollPane2.setViewportView(selectPanel);
			int dataIndex = dataConnectionTabbedPane.indexOfTab("Data");
			dataConnectionTabbedPane.setEnabledAt(dataIndex,true);
			dataConnectionTabbedPane.setSelectedIndex(dataIndex);
		    }
		}
		//	else
		    //System.out.println(gg.getProgress());

	    }
       };

    private Annotator ag;

    private ActionListener annotateGraphActionListener = new ActionListener(){
	    public void actionPerformed(ActionEvent evt){
		progressBar2.setValue(ag.getProgress());
		//System.out.println(ag.getProgress());
		if (ag.isDone()){
		    //System.out.println("done");
		    timer2.stop();
		    ag.stop();
		   // graph.setNodeAnnotation(ag.getAnnotation());
		    
		    updateAccept();
		    progressBar2.setIndeterminate(false);
		    progressBar2.setString(gg.getProgress()+" entries");
		    setCursor(null);
		    int acceptIndex = 
			dataConnectionTabbedPane.indexOfTab("Accept");
		    dataConnectionTabbedPane.setEnabledAt(acceptIndex,true);
		    dataConnectionTabbedPane.setSelectedIndex(acceptIndex);
		}
		//	else
	    //System.out.println(gg.getProgress());
	    }
	    
	};
    

/*
    // this gets annotations for nodes 
    public void annotateGraph() throws IOException{
	//System.out.println("annotating ");
	StringBuffer sb = graph.getNodeList();
	Socket stringSocket = new Socket("string.embl.de",port);
	//System.out.println("socket opened ");
	PrintWriter out = new PrintWriter(stringSocket.getOutputStream(),true);
	BufferedReader in = new BufferedReader
	    (new InputStreamReader
	     (stringSocket.getInputStream() ));
	String inLine;
	//StringBuffer temp;
	//System.out.println("using "+sb);
	//sb.append("bye");
	
	
	// prepare the transfer
	StringBuffer transferSB = new StringBuffer();
	String[] queries = sb.toString().split("\n");
	for (int i = 0;  i<queries.length ; i++) {
	    transferSB.append("NODE:");
	    transferSB.append(queries[i]);
	    transferSB.append("\n");
	}
	transferSB.append("bye");

	out.println(transferSB);
	//System.out.println("sending to thread: "+queries[i]);
	while (!in.ready()) {}
	while( (inLine=in.readLine())!=null){
	    
	    String[] result=inLine.split("\t");
	    //System.out.println(result[0]+" mapped to "+result[1]);
	    graph.setNodeAnnotation(result[0],result[1]);
	    //System.out.println("set node annotation");
	    
	}
	
	out.close();
	in.close();
	stringSocket.close();
	
	}
*/

    // todo: go to socketthread and make it accept
    // lines with "NODE:bla bla"

    

 
    /* private void parseCogEdge(Graph g, String inLine){
	String[] result = inLine.split("\\t+");
	float combined=Float.parseFloat(result[result.length-1])/1000f;
	
	if (combined<cutoff)
	    return;
	if (inLine.compareTo("Not found")==0)
	    return;
	//System.out.println(inLine+"\n"+result.length);
	Pattern genePattern=Pattern.compile("^(COG\\d+)");
	Matcher matcher;
	// get nodes
	String n1=result[0];
	String n2=result[1];
	matcher=genePattern.matcher(result[0]);
	if (matcher.find())
	    n1=matcher.group(1);
	matcher=genePattern.matcher(result[1]);
	if (matcher.find())
	    n2=matcher.group(1);
	float confidence;
	for (int i=2; i<result.length-1; i++){
	    //System.out.println(result[i]);
	    confidence=Float.parseFloat(result[i])/1000;
	    // is there an interaction?
	    if (confidence>0.){
		Edge e = new Edge(n1,n2,confidence,i-1);
		//System.out.println(e);
		g.addEdge(e);
		//System.out.println(g.report());
	    }
	}
    }
    
    */
    
    public void itemStateChanged(ItemEvent e){
	//handleClassStateChange(e);
    }
    
    
    public void actionPerformed(ActionEvent ae) {
        
    }
    
    /*public void handleClassStateChange(java.awt.event.ItemEvent e){
      Object src = e.getSource();
      javax.swing.JRadioButton jr = (javax.swing.JRadioButton) src;
      System.out.println(jr.getText());
      }*/

    
    // returns the selections in a list
    public ArrayList<String> buildSelectionList(){
	int n=selectPanel.getComponentCount();
	ArrayList<String> selection = new ArrayList<String>();
	for (int i=0; i<n; i++){
	    javax.swing.JRadioButton jr 
		= (javax.swing.JRadioButton) selectPanel.getComponent(i);
	    if (jr.isSelected())
		selection.add(jr.getText());
	    //System.out.println(jr.getText()+" "+jr.isSelected());
	}
	return selection;
    }

    // build the organism box
    private void buildOrganismBox(){
	//String[] orgs = {"All","SYNY3", "BUCAI", "DEIRA", "ECOLI"};
	//double[] cu = {"0.1","SYNY3", "BUCAI", "DEIRA", "ECOLI"};
	for (int i=1; i<10; i++)
	    organismBox.addItem(new Float( (float)i/10f) );
	organismBox.setSelectedIndex(3);
	cutoff=0.3f;
    }

    public Graph returnGraph(){
	return graph;
    }

    // send the gene list to string and get the network
    //private DataConnection dataConnection = new DataConnection();
    public static Graph showDialog(java.awt.Component frameComp,
				   java.awt.Component locationComp
				   ){
	java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
	dataConnection=new DataConnection(frame, locationComp);
	dataConnection.setSize(450,300);
	dataConnection.setVisible(true);
	return graph;
    }

    public static Graph showDialog(String initial,
				   java.awt.Component frameComp,
				   java.awt.Component locationComp
				   ){
	java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
	dataConnection=new DataConnection(frame, locationComp);
	dataConnection.setSize(450,300);dataConnection.setInitialSelection(initial);
	dataConnection.setVisible(true);
	//System.out.println(dataConnection.getClass()+" "+initial);
	
	return graph;
    }
    

   
    /*public static Graph showDialog(java.awt.Component frameComp,
      java.awt.Component locationComp,
      String[] initValues
      ){
      java.awt.Frame frame = javax.swing.JOptionPane.getFrameForComponent(frameComp);
      dataConnection=new DataConnection(frame, locationComp);
      dataConnection.setSize(450,300);
      dataConnection.setInitial(initValues);
      dataConnection.setVisible(true);
      return graph;
      }
      private void setInitial(String[] values){
      for (int i=0; i<values.length; i++)
	   
      nodeTextArea.append(values[i]+"\n");
      }*/
    
    // test the connection before creating the dialog
    
				   
}
