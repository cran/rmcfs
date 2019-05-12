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
package dmLab.utils.roulette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RouletteInput{
	protected ArrayList<RouletteItem> rouletteItemsList;
	protected HashMap<Short, Integer> weightMap;
	protected int sizeSum;
	protected int selected;
	protected int[] cumSums;
		
	//********************************
	public RouletteInput(){
		rouletteItemsList = new ArrayList<RouletteItem>();		
	}
	//********************************
	public RouletteInput(short[] weights, int[] sizes){
		this();
		if(weights.length != sizes.length)			
			System.err.println("RouletteInput: weights.length != sizes.length");
		
		for(int i=0;i<weights.length;i++){
			rouletteItemsList.add(new RouletteItem(weights[i], sizes[i]));
		}		
		init();
	}
	//********************************
	public int init(){
		selected = 0;
		sizeSum = 0;
		final int rouletteSize = rouletteItemsList.size();		
		weightMap = new HashMap<Short, Integer>();
		
		//calc cumsums
		cumSums = new int[rouletteSize];
		for(int i=0; i<rouletteSize; i++){
			RouletteItem item = rouletteItemsList.get(i);
			weightMap.put(item.weight, i);
			cumSums[i] = item.weight * item.size;
			if(i>0)
				cumSums[i] = cumSums[i-1] + cumSums[i];
			item.cumSum = cumSums[i];
			sizeSum += item.size;
		}		
		return cumSums[cumSums.length-1];
	}
	//********************************
	public int size(){
		return rouletteItemsList.size();
	}	
	//********************************
	public void addItem(RouletteItem item){
		rouletteItemsList.add(item);
	}	
	//********************************
	public RouletteInput removeItem(int itemId){
		
		RouletteInput rouletteInputClone = new RouletteInput();
		final int rouletteSize = size();				
		for(int i=0; i<rouletteSize; i++){
			if(i != itemId)
				rouletteInputClone.addItem(rouletteItemsList.get(i));
		}
		rouletteInputClone.init();
		return rouletteInputClone;
	}	
	//********************************
	public RouletteItem getItem(int itemId){
		return rouletteItemsList.get(itemId);
	}	
	//********************************
	public int getItemId(short weight){
		return weightMap.get(weight);
	}
	//********************************
	public int findItemId(int shoot){			
		int itemId = 0;		
		while(cumSums[itemId] < shoot){
			itemId ++;
		}
		return itemId;
	}
	//********************************
	public boolean incSelSize(int itemId){
		selected++;
		RouletteItem item = rouletteItemsList.get(itemId);
		item.selSize ++;
		if(item.selSize < item.size)
			return true;
		else{
			return false;		
		}
	}
	//********************************
	public int getMaxCumSum(){
		if(cumSums == null || cumSums.length != size())
			return init();
		else
			return cumSums[cumSums.length-1]; 
	}
	//********************************
	public int[] getSelectedSize(){
		final int rouletteSize = size();
		int[] selectedSize = new int[rouletteSize]; 		
		for(int i=0; i<rouletteSize; i++){
			selectedSize[i] = getItem(i).selSize;
		}
		return selectedSize;		
	}
	//********************************	
	public String toString(){
		StringBuffer tmp = new StringBuffer();
		final int rouletteSize = rouletteItemsList.size();		
		for(int i=0; i<rouletteSize; i++){
			RouletteItem item = rouletteItemsList.get(i);
			tmp.append(item.toString()).append("\n");
		}
		tmp.append("cumSums: "+Arrays.toString(cumSums));
		return tmp.toString();
	}
	//********************************	
}
