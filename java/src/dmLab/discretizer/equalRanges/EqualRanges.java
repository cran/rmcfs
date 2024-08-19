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
package dmLab.discretizer.equalRanges;

import java.util.ArrayList;

import dmLab.discretizer.Discretizer;
import dmLab.discretizer.DiscretizerParams;
import dmLab.utils.ArrayUtils;
import dmLab.utils.MathUtils;
import dmLab.utils.helpers.MinMax;

public class EqualRanges extends Discretizer
{
	//discretization based on equal ranges

	//*******************************************************
	public EqualRanges()
	{
	}
	//*******************************************************
	@Override
	public float[] findRanges(float[] values, float[] decision, DiscretizerParams discParams)
	{
		int intervals = discParams.discIntervals();
		if(values.length < intervals){
			ranges = null;
			return getRanges();
		}
		
		MinMax min_max = ArrayUtils.getMinMax(values, true);
		
		if(Float.isNaN(min_max.minValue) || Float.isNaN(min_max.maxValue)){
			ranges = null;
		}else{
			float interval=(min_max.maxValue-min_max.minValue)/intervals;    
			ranges = new ArrayList<Float>();
			for(int i=0;i<intervals-1;i++)
				ranges.add(min_max.minValue+(i+1)*interval);
			ranges.add(MathUtils.maxValue(values));
		}

		return getRanges();
	}
	//*******************************************************
}
