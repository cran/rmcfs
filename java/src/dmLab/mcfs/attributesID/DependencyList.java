/*******************************************************************************
 * #-------------------------------------------------------------------------------
 * # Copyright (c) 2003-2016 IPI PAN.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Public License v3.0
 * # which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/gpl.html
 * # 
 * #-------------------------------------------------------------------------------
 * # @description: data mining (dmLab) library that implements MCFS-ID algorithm
 * # @author: Michal Draminski [mdramins@ipipan.waw.pl]
 * # @company: Polish Academy of Sciences - Institute of Computer Science
 * # @homepage: http://www.ipipan.eu/
 * #-------------------------------------------------------------------------------
 * # Algorithm 'SLIQ' developed by Mariusz Gromada
 * # R Package developed by Michal Draminski & Julian Zubek
 * #-------------------------------------------------------------------------------
 * # If you want to use dmLab or MCFS/MCFS-ID, please cite the following paper:
 * # M.Draminski, A.Rada-Iglesias, S.Enroth, C.Wadelius, J. Koronacki, J.Komorowski 
 * # "Monte Carlo feature selection for supervised classification", 
 * # BIOINFORMATICS 24(1): 110-117 (2008)
 * #-------------------------------------------------------------------------------
 *******************************************************************************/
package dmLab.mcfs.attributesID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class DependencyList {
	
	private int[] keys;
	protected HashSet<Integer>[] values;
	private HashMap<Integer,Integer> keyToId;	
	
	//********************************
	@SuppressWarnings("unchecked")
	public DependencyList(int[] keys){
		this.keys = keys;
		values = new HashSet[keys.length];		
		
		keyToId = new HashMap<Integer,Integer>();
		for(int i=0;i<keys.length;i++){
			keyToId.put(keys[i], i);
		}		
	}
	//********************************
	public void put(int key, int value){
		int id = keyToId.get(key);
		
		if(values[id]==null)
			values[id] = new HashSet<Integer>();
		values[id].add(value);
	}
	//********************************
    public Integer[] getValues(int key){
    	int id = keyToId.get(key);
    	Integer[] vals = new Integer[1];
    	
    	if(values[id]!=null){
    		vals = values[id].toArray(vals);    				
    		return vals;
    	}
    	else
    		return null;
    }
	//********************************
    public String toString()
    {
    	StringBuffer tmp=new StringBuffer();    	
    	for(int i=0;i<keys.length;i++){    		
    		tmp.append(keys[i]).append(" -> ");    		
    		Integer[] vals = getValues(keys[i]);    		    		
    		if(vals!=null)
    			tmp.append(Arrays.toString(vals));
    		tmp.append("\n");   		
    	}
    	return tmp.toString();
    }
	//********************************
}
