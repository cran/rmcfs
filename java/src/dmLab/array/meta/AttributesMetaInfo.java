/*******************************************************************************
 * #-------------------------------------------------------------------------------
 * # dmLab 2003-2019
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
 *******************************************************************************/
package dmLab.array.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import dmLab.array.Array;
import dmLab.utils.roulette.RouletteInput;
import dmLab.utils.roulette.RouletteItem;

public class AttributesMetaInfo {
	protected Attribute[] attributes;
	protected HashMap<String, Integer> dictIdMap;
		
	protected HashMap<Short, Integer> weightMap;
	protected ArrayList<HashSet<Integer>> weightGroups; 

	protected int decIndex = -1;	
	
	//********************************
	public AttributesMetaInfo(Array array){
		decIndex = array.getDecAttrIdx();
		init(array.attributes);
	}
	//********************************
	public AttributesMetaInfo(Attribute[] attributes){
		decIndex = -1;
		init(attributes);
	}
	//*************************************
	protected void init(Attribute[] attributes){
		this.attributes = attributes;
		dictIdMap = new HashMap<String, Integer>();				
		weightMap = new HashMap<Short, Integer>();
		weightGroups = new ArrayList<HashSet<Integer>>();
		
		for(int i=0; i<attributes.length; i++){
			//build dict
			dictIdMap.put(attributes[i].name, i);
			
			//build weights groups
			short currentAttrWeight = attributes[i].weight;			
			//for decision attribute assume its weight equals to zero 
			if(i == decIndex )
				currentAttrWeight = 0;
			
			Integer groupId = weightMap.get(currentAttrWeight);		
			//System.out.println(attributes[i].name+ " " + currentAttrWeight + " "+groupId);
			if(groupId == null){
				groupId = weightMap.size();
				weightMap.put(currentAttrWeight, groupId);
				weightGroups.add(new HashSet<Integer>());
			}
			weightGroups.get(groupId).add(i);					
		}		
	}
	//********************************
	public Attribute getAttribute(String name){
		Integer id = getIndex(name);
		if(id == null)
			return null;
		else
			return attributes[id];
	}
	//********************************
	public Attribute getAttribute(int index){
		return attributes[index];
	}
	//********************************
	public Integer getIndex(String name){
		Integer id = dictIdMap.get(name);
		if(id == null)
			return -1;
		else
			return id;
	}	
	//********************************
	public int getWeightsSize(){
		return weightMap.size();
	}
	//********************************
	public int getAttributesSize(){
		return attributes.length;
	}
	//********************************
	public Short[] getWeights(){
		Short[] weights = new Short[1]; 
		weights = weightMap.keySet().toArray(weights);
		return weights;
	}
	//********************************
	public Integer[] getIndexArray(short weight){		
		Integer[] ids = new Integer[1];
		ids = weightGroups.get(weightMap.get(weight)).toArray(ids);
		return ids;
	}
	//********************************
	public RouletteInput getRouletteInput(){
		Short[] weights = getWeights();
		RouletteInput rouletteInput = new RouletteInput();
		for(int i=0; i<weights.length; i++){
			rouletteInput.addItem(new RouletteItem(weights[i], getIndexArray(weights[i]).length));
		}
		rouletteInput.init();
		return rouletteInput;
	}
	//********************************
	public String toString(){
		StringBuffer tmp = new StringBuffer();
		Short[] weights = getWeights(); 
		for(int i=0; i<weights.length; i++){
			tmp.append("w = "+weights[i]).append("\n");
			Integer[] ids = getIndexArray(weights[i]);
			for(int j=0; j<ids.length; j++){				
				tmp.append("\tid: "+ids[j]+"\t"+getAttribute(ids[j]).name).append("\n");
			}			
		}
		return tmp.toString();
	}
	//********************************
}
