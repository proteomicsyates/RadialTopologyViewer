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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CallString implements Runnable {
    
    private Thread me;
    private boolean done;

    private String url;
    private int port;
    private String[] queries;
    private JPanel resultPanel;

    public CallString(String url, int port, String[] queries){
		this.url=url;
		this.port=port;
		this.queries=queries;
    }


    public JPanel getPanel(){
    	return resultPanel;
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

    public void run(){
	Thread myThread=Thread.currentThread();
	done=false;
	if (me==myThread){
	    //System.out.println("thread running");
	    try {
	    	resultPanel=callString();
	    }
	    catch (IOException ie){
	    	resultPanel=errorPanel("Database not responding");
	    }
	    
	}
	done=true;
    }
    
    int progress=0;
    public int getProgress(){
	return progress;
	
    }
    public boolean isDone(){
	return done;
    }

    public JPanel callString() throws IOException{
    	progress=0;
	//done =false;
	JPanel selectPanel =new JPanel();
	selectPanel.setBackground(new java.awt.Color(255, 255, 255));
	selectPanel.setLayout
	    (new javax.swing.BoxLayout
	     (selectPanel, javax.swing.BoxLayout.Y_AXIS));
	Socket stringSocket = new Socket(url,port);

	PrintWriter out = new PrintWriter
	    (stringSocket.getOutputStream(),true);
	BufferedReader in = new BufferedReader
	    (new InputStreamReader
	     (stringSocket.getInputStream()));
	String inLine;
	int count=0;
	// read each line of the text area and send to the socketListener
		
	for (int n=0; n<queries.length; n++){
	    progress++;
	    out.println("GN:"+queries[n]);
	    //System.out.println("GN:"+queries[n]);
	    while (!in.ready()) {}
	    	inLine = in.readLine();
	    if (inLine!= null) {
		
		String result[]=inLine.split("[\\n\\t]");
		
		for (int i=0; i<result.length; i++){
		    //result[i]=result[i].replaceAll(" ","");

		    // check if the response was an error
		    if (checkError(result[i])){
				out.close();
				in.close();
				stringSocket.close();
				return errorPanel("Database is not responding");
		    }
		
		    result[i]=result[i].replaceAll(" ","");
		    //System.out.println(result[i]);
		    if (result[i].length()>1){
			javax.swing.JRadioButton jr = 
			    new javax.swing.JRadioButton(result[i]);
			jr.setBackground(new java.awt.Color(255, 255, 255));
		
			selectPanel.add(jr);
			count++;
		    }
		}
	    }
	}
	
	out.close();
	
	in.close();
	stringSocket.close();
	if (count==0){
	    selectPanel.add(new JLabel("Sorry, no matches found"));
	
	}
	return selectPanel;
    }
        
 //   private JPanel errorPanel(){
 //   	return errorPanel("STRING is not responding: server may be down");
 //   }

    private JPanel errorPanel(final String text){
	JPanel errorPanel = new JPanel();
	errorPanel.setLayout(new javax.swing.BoxLayout
	     (errorPanel, javax.swing.BoxLayout.Y_AXIS));
	JLabel error = new JLabel(text);
	errorPanel.add(error);
	//errorPanel.add(new JLabel("STRING");
	errorPanel.add(new JLabel("If this problem persists, please contact"));
	errorPanel.add(new JLabel("hooper@embl.de"));
	errorPanel.add(error);
	
	return errorPanel;
    }
	
    private boolean checkError(String inLine){
	if (inLine.startsWith("Error"))
	    return true;
	return false;
    }
		     

}
