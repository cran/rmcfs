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

import dmLab.discretizer.Discretizer;
import dmLab.discretizer.DiscretizerParams;
import dmLab.discretizer.change.DiscChange;
import dmLab.discretizer.chiMerge.DiscChiMerge;
import dmLab.discretizer.equalFrequency.EqualFrequency;
import dmLab.discretizer.equalRanges.EqualRanges;
import dmLab.discretizer.fayyadIrani.FayyadIrani;
import dmLab.utils.ArrayUtils;

public class DiscRanges implements Cloneable
{
	protected float ranges[];

	//	*******************************************************
	public DiscRanges()
	{
		ranges=null;
	}
	//	*******************************************************
	public DiscRanges(float ranges[])
	{
		if(ranges!=null)
			this.ranges = ranges;
	}
	//	*******************************************************
	public float[] getRanges()
	{
		return ranges;
	}
	//	*******************************************************
	public int getSize()
	{
		if(ranges==null)
			return -1;
		else
			return ranges.length;
	}
	//	*******************************************************
	public boolean find(float values[], float[] decision, DiscretizerParams discParams)
	{
		if(values.length < discParams.discIntervals())
			return false;

		Discretizer discretizer=null;
		final int algorithm = discParams.discAlgorithm();
		if(algorithm==Discretizer.EQUAL_RANGES)
			discretizer=new EqualRanges();
		else if(algorithm==Discretizer.EQUAL_FREQUENCY)
			discretizer=new EqualFrequency();
		else if(algorithm==Discretizer.CHI2)
			discretizer=new DiscChiMerge();
		else if(algorithm==Discretizer.CHANGE)
			discretizer=new DiscChange();
		else if(algorithm==Discretizer.FAYYAD_IRANI)
			discretizer=new FayyadIrani();
		else
			return false;

		discretizer.findRanges(values, decision, discParams);
		ranges = discretizer.getRanges();
		return true;
	}
	//	*******************************************************
	private int getRangeId(float value)
	{
		if(ranges==null){
			return -1;
		}else if(Float.isNaN(value)){
			return -1;
		}else if(value >= ranges[ranges.length-1]){
			return ranges.length-1;
		}else if(ranges.length <= 100){
			//simple method based on searching one by one, for small number of ranges
			//(<100) it's faster than binary search		
			return ArrayUtils.indexOf(ranges, value, false);
		}else{
			return ArrayUtils.indexOf(ranges, value, true);
		}
	}
	//	*******************************************************
	public float[] getRange(float value)
	{		
		if(Float.isNaN(value) || ranges==null)
			return null;

		float range[]=new float[2];		
		int cutPointIndex=getRangeId(value);
		if(cutPointIndex==0)
			range[0]=Float.NEGATIVE_INFINITY;
		else
			range[0]=ranges[cutPointIndex-1];

		if(cutPointIndex==ranges.length-1)
			range[1]=Float.POSITIVE_INFINITY;
		else
			range[1]=ranges[cutPointIndex];

		return range;
	}
	//	*******************************************************
	public String getRangeStr(float value)
	{		
		if(Float.isNaN(value))
			return "NULL";
		if(ranges==null )
			return Float.toString(value);

		float range[]=getRange(value);	
		if(range==null)
			return "NULL";

		return "("+range[0]+";"+range[1]+"]";
	}
	//	*******************************************************
	public float getDiscreteValue(float value)
	{
		if(ranges==null || Float.isNaN(value))
			return value;
		else
			return ranges[getRangeId(value)];
	}
	//	*******************************************************
	public DiscRanges clone()
	{
		DiscRanges d = new DiscRanges();
		d.ranges = ranges.clone();
		return d; 
	}
	//	*******************************************************
	@Override
	public String toString()
	{
		if(ranges==null)
			return "";

		StringBuffer tmp=new StringBuffer();
		for(int i=0;i<ranges.length;i++)
		{
			tmp.append(ranges[i]);
			if(i!=ranges.length-1)
				tmp.append(", ");
		}
		return tmp.toString();
	}
	//  *******************************************************
}
