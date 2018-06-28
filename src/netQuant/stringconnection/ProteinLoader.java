package netQuant.stringconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

public class ProteinLoader implements Runnable {

	private Thread me;
    private boolean done;
    private int progress;
    private BufferedReader reader;
	private Vector<String> protnames = new Vector<String>();
	
	public ProteinLoader(BufferedReader dis) {
		
		reader = dis;
		
	}

	@Override
	public void run() {
		Thread myThread=Thread.currentThread();
		done=false;
		
		if (me==myThread){
		    
		    readFile();
			
		    done=true;
		}
		
	}

	public int getcurrentline() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getProgress() {
		return progress;
	}
    
    public boolean isDone() {
		return done;
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
	
	public void readFile() {
		
		String data;
		
		try {
			
			while ((data = reader.readLine())!=null) {
				protnames.add(data);
				progress++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		done = true;
	}

	public Vector<String> getList() {
		return protnames;
	}
}
