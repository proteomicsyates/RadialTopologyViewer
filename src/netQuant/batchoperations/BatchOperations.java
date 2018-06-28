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


package netQuant.batchoperations;

import java.io.File;
import java.io.IOException;

import netQuant.DataFormatException;
import netQuant.dataio.DataLoader;
import netQuant.display.EditableGraphPanel;
import netQuant.display.FRspring;
import netQuant.graph.Graph;

/**
 * This is an example of how to batch mode. 
 */

public class BatchOperations {

	DataLoader dl;
    
	/** constructor */
    public BatchOperations() {
        dl=new DataLoader(1,1);
    }
    
    /**
     *  convert all files which follow the tabbed rule
     *  creates graph from them and then saves them
     */
    public void convertTabbedToNetQuant(String dir){
        dir=clean(dir);
        File f = new File(dir+File.separator);
        String[] children=f.list();
        Graph g;
        for (int i=0; i<children.length; i++){

            try {
                System.out.println(dir+File.separator+children[i]);
                g=dl.loadSimplest(children[i]);
                System.out.println("saving "+dir+File.separator+children[i]+".dat");
                dl.save(g,dir+File.separator+children[i]+".dat");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            } catch (DataFormatException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
        System.out.println("Converted as many files as possible in dir "+dir);
    }
    
    /**
     * modify layout to a batch of graphs
     * @param dir
     */
    public void layoutAll(String dir){
        dir=clean(dir);
        File f = new File(dir);
        String[] children=f.list();
        Graph g;
        int dim=600;
        FRspring frSpring;
        for (int i=0; i<children.length; i++){
            if(children[i].endsWith(".dat")){
                try {
                    System.out.println("loading: "+children[i]);
                    g=dl.load(dir+File.separator+children[i]);
                    frSpring =
                            new FRspring(g,dim, dim);
                    frSpring.iterateAll();
                    // reset positions to 0-1
                    g.divideNodePosition(dim);
                    System.out.println("saving "+dir+File.separator+children[i]);
                    dl.save(g,dir+File.separator+children[i]);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } catch (DataFormatException ex) {
                    System.out.println(ex.getMessage());
                }catch (java.lang.ArithmeticException ex){
                    System.out.println(ex.getMessage());
                }
            }
            
        }
        System.out.println("Converted as many files as possible in dir "+dir);
    }
    
    /**
     * exports graph as a PNG/JPG
     * @param dir
     */
    public void exportAllToPNG(String dir){
        dir=clean(dir);
        File f = new File(dir);
        String[] children=f.list();
        Graph g;
        EditableGraphPanel dp = new EditableGraphPanel();
        for (int i=0; i<children.length; i++){
            if(children[i].endsWith(".dat")){
                try {
                    System.out.println("loading: "+children[i]);
                    g=dl.load(dir+File.separator+children[i]);
                    dp.setGraph(g);
                    System.out.println("saving "+dir+File.separator+children[i]);
                    dp.saveImage(dir+File.separator+children[i]+".png",1); //the 1 at the end is for PNG, 0 is JPG
                    
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } catch (DataFormatException ex) {
                    System.out.println(ex.getMessage());
                }catch (java.lang.ArithmeticException ex){
                    System.out.println(ex.getMessage());
                }
            }
            
        }
        System.out.println("Converted as many files as possible in dir "+dir);
    }
    
    /**
     * removes, if present, the file separator
     * @param dir
     * @return
     */
    private String clean(String dir){
        
        if(dir.endsWith(File.separator)){
            dir=dir.substring(0,dir.length()-1);
        }
        return dir;
    }
    
    /**
     * open, change layout and save the graphs
     * @param args
     */
    public static void main(String[] args) {
        BatchOperations b = new BatchOperations();
        System.out.println("Converting");
        b.convertTabbedToNetQuant(args[0]);
        System.out.println("Laying out ");
        b.layoutAll(args[0]);
        b.exportAllToPNG(args[0]);
    }
}
