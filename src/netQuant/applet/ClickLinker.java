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

package netQuant.applet ;

import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JApplet;


public class ClickLinker{
    JApplet applet;
   
    String linkStart;
    String linkEnd;
    
    /** Creates a new instance of ClickLinker */
    public ClickLinker(String linkStart, String linkEnd,
            JApplet applet) {
        this.applet=applet;
        System.out.println(linkStart);
        this.linkStart=linkStart;
        this.linkEnd=linkEnd;
    }
    
    public boolean isActive(){
        return (linkStart!=null);
    }
    
    public void linkOut(String node){
        if (linkStart==null){
            return;
        }
        try{
    
         URL url = new URL(linkStart+node+linkEnd);
         System.out.println("Linking to "+linkStart+node+linkEnd);
         applet.getAppletContext().showDocument(url,"_blank");
      }
      catch(MalformedURLException er){ }
    }
}
