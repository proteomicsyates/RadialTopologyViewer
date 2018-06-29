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

/* this panel holds the string image, and will rotate it
 * on command
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import netQuant.NetQuantFrame;

public class AnimPanel extends JPanel implements Runnable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6002015311547147779L;
	BufferedImage image = null;
    int anchorX;
    int anchorY;
    String text;
    Font nodeFont = new Font("TimesNewRoman", Font.ITALIC,28);

    public AnimPanel(){
	this("/netQuant/images/logo_still_p.gif");
	//java.net.URL url = ClassLoader.getSystemResource("images/logo_still_p.gif");
	//setImage(url) ;
	//init();
    }
    
    public AnimPanel(String imageName,String greeting){
	setImage(imageName);
	text=greeting;
	init();
    }
    public AnimPanel(String imageName){
        System.out.println("sending "+imageName);
	setImage(imageName);
	init();
    }
    
    private void init(){
	setBackground(NetQuantFrame.STRINGCOLOR);
	setOpaque(true);
	anchorX=(int) image.getWidth(this)/2;
	anchorY=(int) image.getHeight(this)/2;
	setPreferredSize(new Dimension(image.getWidth(this),image.getHeight(this)));
    }
    
    

//    public void setImage(String imageSource){
//	java.net.URL imageURL=this.getClass().getResource(imageSource);
//	MediaTracker mt = new MediaTracker(this);
//	image = Toolkit.getDefaultToolkit().getImage(imageURL);
//	mt.addImage(image, 0);
//	try {
//	    mt.waitForID(0);
//	    //System.out.println("image loaded?");
//	} catch (InterruptedException ex) {
//	    ex.printStackTrace();
//	}
//        catch (NullPointerException ne){
//            System.out.println("Null pointer in AnimPanel");
//        }
//	if ((mt.statusAll(false) & MediaTracker.ERRORED) != 0) {
//	    image=null;
//	}
//    }	
    
     private void setImage(final String imageURL) 
	{
        try {
            System.out.println(imageURL);
            
            image= javax.imageio.ImageIO.read(getClass().getResource(imageURL));
        } catch (Exception ex) {
            image=null;
            ex.printStackTrace();
        }
	
    }
    
    public void paintComponent(Graphics g){
	clear(g);
	Graphics2D g2d = (Graphics2D) g;
	
	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
			     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			     RenderingHints.VALUE_ANTIALIAS_ON);
	
	AffineTransform rotate45 = 
	    AffineTransform.getRotateInstance(Math.PI*Math.sin(rotation),anchorX,anchorY);
	//rotate45.scale(Math.abs(Math.sin(rotation/3)),Math.abs(Math.sin(rotation/3)) );
	//g2d.setTransform(rotate45);
	//g2d.drawImage(image,centerX,centerY,this);
        if (image!=null)
	g2d.drawImage(image,rotate45,this);
	g2d.setColor(Color.yellow);
	g2d.setFont(nodeFont);
	if (text!=null){
	    g2d.drawString(text,5,image.getHeight(this));
	}
    }

    double rotation=0;
    Thread spinThread;

 protected void clear (Graphics g) {
	super.paintComponent(g);
    }
    public void start(){
	if (spinThread==null)
	    spinThread=new Thread(this);
	spinThread.start();
    }

    public void run(){
	Thread myThread=Thread.currentThread();
	//System.out.println("started panel");
	while(spinThread==myThread){
	    rotation+=0.02;
	    repaint();
	    try{
		Thread.sleep(30);
	    }
	    catch (InterruptedException e){
		break; //back to work
	    }
	}
    }

    public void stop(){
	rotation=0;
	if (spinThread!=null)
	    spinThread=null;
    }

}
