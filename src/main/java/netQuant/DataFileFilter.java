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
import java.io.FilenameFilter;
import java.io.File;

/**
 * DataFileFilter filters the input file to check if is correctly formatted.
 * If new file types will be used here is where you need to add the allowed extensions
 * @author Diego
 *
 */
class DataFileFilter implements FilenameFilter{
    
    String[] acceptedNames;

    /**
     * define accepted extensions
     */
    public DataFileFilter(){
    	acceptedNames=new String[2];
    	acceptedNames[0]=".dat";
    	acceptedNames[1]=".txt";
    }

    /**
     * define accepted extensions
     * @param acceptedNames
     */
    public DataFileFilter(String[] acceptedNames){
    	this.acceptedNames=acceptedNames;
    }
    
    /**
     * check that the extension of the input file is accepted
     */
    public boolean accept(File dir, String name){
	for (int i=0; i<acceptedNames.length;i++)
	    if (name.endsWith(acceptedNames[i]))
		return true;
	
	return false;
    }
}
