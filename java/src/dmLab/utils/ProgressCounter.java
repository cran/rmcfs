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
package dmLab.utils;

public class ProgressCounter {
	
	private float min;
	private float max;
	private int prevId;
	private float[] progressArray;
	
	public ProgressCounter(float min, float max, float[] progressArray){
		this.min = min;
		this.max = max;
		this.progressArray = progressArray;		
		prevId = -1;
	}
	//*************************************************
	public String getPercentValue(float x){
		if(x >= max)
			return Integer.toString((int)progressArray[progressArray.length-1]);
		
		float currPercent = (100 * (x-min)/(max-min));
		int currentId = 0;
						
		while(currentId < progressArray.length && currPercent >= progressArray[currentId]){
			currentId++;
		}
		currentId--;
		String retVal = null;
		if(currentId > prevId){
			retVal = Integer.toString((int)progressArray[currentId]); 
			prevId = currentId;
		}						
		return retVal;
	}
	//*************************************************
}
