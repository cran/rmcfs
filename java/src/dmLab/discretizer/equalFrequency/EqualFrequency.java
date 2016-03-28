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
package dmLab.discretizer.equalFrequency;

import java.util.ArrayList;
import java.util.Arrays;

import dmLab.discretizer.Discretizer;
import dmLab.discretizer.DiscretizerParams;
import dmLab.utils.MathUtils;

//**** Equal Frequency discretization
public class EqualFrequency extends Discretizer
{
	public EqualFrequency() 
	{
	}
	//  *******************************************************
	@Override
	public float[] findRanges(float[] values, float[] decision, DiscretizerParams discParams)
	{
		int intervals = discParams.discIntervals();
		if(values.length < intervals){
			ranges = null;
			return getRanges();
		}

		float array[]= values.clone();
		Arrays.sort(array);        
		//determine the real size without NaN
		int size=values.length;
		for(int i=values.length-1; i>=0; i--)
			if(Float.isNaN(array[i]))
				size--;
			else
				i=0;//break loop

		if(size < intervals){
			ranges = null;
			return getRanges();
		}

		ranges = new ArrayList<Float>();
		float intervalSize = ((float)size/(float)intervals);
		int currIndex = Math.round(intervalSize); 
		while(currIndex < size){
			ranges.add((array[currIndex-1]+array[currIndex])/2);
			currIndex = Math.round(intervalSize + currIndex);
		}               
		ranges.add(MathUtils.maxValue(values));
            
		return getRanges();
	}
	//*******************************************************
}

