//
//NetQuant is a graph viewer which allows interactive editing of networks
//(edges and nodes) and connects to databases and provides quantitative analysis.
//NetQuant is based on Medusa developed by Sean Hopper <http://www.bork.embl.de/medusa>
//
//Copyright (C) 2011 Diego Calzolari
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but 
//WITHOUT ANY WARRANTY; without even the implied warranty of 
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
//Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.


package netQuant.display;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * class to store and manage the quant values
 * @author diego
 *
 */
public class QuantDataHashTable {
	
	
	private Hashtable <String,Double> quantData;

	/**
	 * constructor
	 * it initialize the hash table
	 */
	public QuantDataHashTable() {
		
		quantData = new Hashtable<String, Double>();
		
	}
	
	public void clear() {
		quantData.clear();
	}
	
	/**
	 * add an entry inside the hash table
	 * @param k key for the hash table
	 * @param value double array with the quant values
	 */
	public void addQuantData(String k, double value) {
				
		if (!quantData.containsKey(k)) {
			quantData.put(k, value);
		} else {
			System.out.println("value " + k + " already in memory");
		}
		
	}
	
	/**
	 * return the input quant value for the specified key
	 * @param k key to find
	 * @return
	 */
	public double getInput(String k) {
		
		double value=0;
		
		if (quantData.containsKey(k)) {
			value = quantData.get(k);
		} else {
			System.out.println("Key not found");
			value = 0.0;
		}
		
		return value;
			
	}
	
	/**
	 * return the control quant value for the specified key
	 * @param k key to find
	 * @return
	 */
	public double getControl(String k) {
		
		double value=0;
		
		if (quantData.containsKey(k)) {
			value = quantData.get(k);
		} else {
			System.out.println("Key not found");
			value = 0.0;
		}
		
		return value;
			
	}

	/**
	 * return the exp quant value for the specified key
	 * @param k key to find
	 * @return
	 */
	public double getExp(String k) {
		
		double value=0;
		
		if (quantData.containsKey(k)) {
			value = quantData.get(k);
		} else {
			System.out.println("Key not found");
			value = 0.0;
		}
		
		return value;
			
	}
	
	public boolean cointains(String k) {
		
		if (quantData.containsKey(k)) {
			return true;
		} else
			return false;
		
		
	}

	public Enumeration<String> getKeys() {
		
		Enumeration<String> e = quantData.keys();
		
		return e;
	}
	
}
