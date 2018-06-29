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


package netQuant.example;

import netQuant.NetQuantSettings;
import netQuant.applet.NetQuantLite;

/**
 * example of an applet
 * @author diego
 *
 */
public class ExampleApplet extends NetQuantLite {
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1708605290262600847L;

	public void initPanel(){
        System.out.println("Initializing ExampleAppletPanel");
        String settingsParam=getParameter("settings");
        
        NetQuantSettings stringSettings = new NetQuantSettings(settingsParam);
        
        String linkStart=getParameter("linkStart");
        String linkEnd=getParameter("linkEnd");
        System.out.println(linkStart);
        setPanel(new ExamplePanel(stringSettings));
        System.out.println(panel);
    }
    
    
    
}
