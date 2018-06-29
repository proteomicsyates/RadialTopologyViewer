package netQuant.stringconnection;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import netQuant.graph.Graph;



public class ConnectorManager extends JPanel implements ActionListener,Runnable {

	private static final long serialVersionUID = -2556729252460577125L;
	
	private Thread me;
	JFileChooser fc;
	JProgressBar progress;
	JButton load, string, biogrid;
	Timer timer1,timer2,timer3;
	Graph webg;
	
	public ConnectorManager () {
		
		super(new BorderLayout());
		
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		progress = new JProgressBar();
		
		load = new JButton("Proteins");
		load.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			    loadProteins(evt);
			}
		});
		
		string = new JButton("String DB");
		string.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			    connecttoString(evt);
			}
		});
		
		biogrid = new JButton("BioGrid DB");
		biogrid.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			    connecttoBiogrid(evt);
			}
		});
		
		JPanel buttons = new JPanel();
		JPanel progressbar = new JPanel();
		
		buttons.add(load);
		buttons.add(string);
		buttons.add(biogrid);
		
		progressbar.add(progress);
		
		add(buttons,BorderLayout.PAGE_START);
		add(progressbar, BorderLayout.CENTER);
		
	}
	
	private static void createAndShowGUI() {
		
		JFrame frame = new JFrame("Connect to web database");
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(new ConnectorManager());
		
		frame.pack();
		frame.setVisible(true);
	}
	
	
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					UIManager.put("swing.boldMetal", Boolean.FALSE);
					createAndShowGUI();
				}
			}
		);
	}

	@Override
	public void run() {
		
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
		
		
	}
	
	int currentline=0;
	boolean done = false;
	
	Vector<String> proteins = new Vector<String>();
	
	ProteinLoader loadprot;
	
	public void loadProteins(java.awt.event.ActionEvent evt) {
		
		int returnval = fc.showOpenDialog(ConnectorManager.this);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			FileInputStream fis;
			BufferedInputStream bis;
			BufferedReader dis;
			
			try {
				fis =new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				dis = new BufferedReader(new InputStreamReader(bis));
		
		
				loadprot = new ProteinLoader(dis);
				
				progress.setIndeterminate(true);
				progress.setString("contacting String...");
				progress.repaint();
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				timer1 = new Timer(200,getLoadActionListener);
				
				timer1.start();
				loadprot.start();
				
			}  catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
		
	private ActionListener getLoadActionListener = new ActionListener() {
		 public void actionPerformed(ActionEvent evt){
			progress.setString(loadprot.getcurrentline() + " proteins");
			if (loadprot.isDone()) {
			 	timer1.stop();
			 	loadprot.stop();
			 	progress.setString("proteins loaded");
			 	progress.setIndeterminate(false);
			 	setCursor(null);
			 }
		 }
	};
			
	
	StringConnector stcon;
	
	public void connecttoString(java.awt.event.ActionEvent evt) {
		
		proteins=loadprot.getList();
		
		stcon = new StringConnector(proteins);
		stcon.setGraph(webg);
		progress.setIndeterminate(false);
		progress.setMaximum(stcon.getSize());
		progress.setString("contacting String...");
		progress.setValue(0);
		progress.repaint();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		timer2 = new Timer(200,stringActionListener);
		
		timer2.start();
		stcon.start();
		
		
	}
	
	private ActionListener stringActionListener = new ActionListener() {
		 public void actionPerformed(ActionEvent evt){
			 progress.setValue(stcon.getProgress());
			if (stcon.isDone()){
			   timer2.stop();
			   stcon.stop();
			   progress.setIndeterminate(false);
			   progress.setString("String data downloaded");
			   setCursor(null);
			}
		 }
	};
	
	
	BioGridConnector bgcon = new BioGridConnector();
	
	
	public void connecttoBiogrid(java.awt.event.ActionEvent evt) {
		
		webg = stcon.getGraph();
		
		bgcon.setGraph(webg);
		progress.setIndeterminate(false);
		progress.setMaximum(stcon.getGraph().getEdgeSize());
		progress.setString("contacting BioGrid...");
		progress.setValue(0);
		progress.repaint();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		timer3 = new Timer(200,biogridActionListener);
		
		timer3.start();
		bgcon.start();
		
		
	}

	private ActionListener biogridActionListener = new ActionListener() {
		 public void actionPerformed(ActionEvent evt){
			progress.setValue(bgcon.getProgress());
			if (bgcon.isDone()){
			   timer3.stop();
			   bgcon.stop();
			   progress.setIndeterminate(false);
			   progress.setString("Biogrid data downloaded");
			   setCursor(null);
			   done=true;
			   webg = bgcon.getGraph();
			}
		 }
	};
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isDone() {
		return done;
	}
	
	
	public Graph getGraph() {
		return bgcon.getGraph();
	}
	
	public void start(){
		
		if (me==null) {
		    me = new Thread(this);
		}
		me.start();
	    }
	    
	public void stop(){
	
		if (me!=null) {
		    me=null;
	    }
	}
	
	public void join() {
		
		Thread myThread=Thread.currentThread();
			
		if (me==myThread){
		    
			try {
				me.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
		
	}

	public void setGraph(Graph webgraph) {
		webg = webgraph;
		
	}
	
}
