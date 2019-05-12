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

import java.util.Random;

import dmLab.array.meta.AttributesMetaInfo;
import dmLab.utils.ArrayUtils;

public class RouletteSelection {
	protected Random random;
	protected ArrayUtils arrayUtils;
	
	//********************************
	public RouletteSelection(){
		random = new Random();
		arrayUtils = new ArrayUtils(random);
	}
	//********************************
	public RouletteSelection(Random random){
		this.random = random;
		arrayUtils = new ArrayUtils(random);
	}
	//********************************
	public int[] run(RouletteInput rouletteInput, int size){
		if(size > rouletteInput.sizeSum)
			return null;
		
		int maxCumSumVal = rouletteInput.getMaxCumSum();
		//System.out.println("MaxCumSum "+maxCumSumVal);
		int remain = size;
		while(remain > 0){
			remain--;
			//nextInt gives [0,n) and I need (0,n]
			int shoot = random.nextInt(maxCumSumVal) + 1;
			//System.out.println("shoot:"+shoot);
			int itemId = rouletteInput.findItemId(shoot);
			//System.out.println("itemId:"+itemId);			
			if(!rouletteInput.incSelSize(itemId)){
				//System.out.println(rouletteInput.toString());				
				RouletteInput inputshaved = rouletteInput.removeItem(itemId);
				//System.out.println("REMOVE! itemId: "+itemId+"\n"+inputshaved);
				run(inputshaved, remain);
				//System.out.println("EXIT! remain before: " + remain);
				rouletteInput.selected += inputshaved.selected;
				remain -= inputshaved.selected;				
				//System.out.println("remain after: " + remain);
			}			
			//System.out.println(rouletteInput.toString());
			//System.out.println("END! remain: " + remain);
		}
		return rouletteInput.getSelectedSize();
	}
	//********************************
	public int[] select(AttributesMetaInfo attributesMetaInfo, RouletteInput rouletteInput){
		
		int[] mask = new int[attributesMetaInfo.getAttributesSize()];
		final int rouletteSize = rouletteInput.size();
		for(int i=0; i<rouletteSize; i++){
			//System.out.println();
			RouletteItem item = rouletteInput.getItem(i);
			Integer[] groupAttrId = attributesMetaInfo.getIndexArray(item.weight);
			//System.out.println("getSelectedMask: "+Arrays.toString(groupAttrId));
			
			int[] tmpMask = new int[groupAttrId.length];
			arrayUtils.randomSelect(tmpMask, item.selSize);
			//System.out.println("tmpMask: "+Arrays.toString(tmpMask));
			for(int j=0;j<tmpMask.length;j++){
				if(tmpMask[j] == 1)
					mask[groupAttrId[j]] = 1;
			}
			//System.out.println("mask: "+Arrays.toString(mask));			
		}		
		return mask;
	}
	//********************************
}
