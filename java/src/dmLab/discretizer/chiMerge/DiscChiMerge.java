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
package dmLab.discretizer.chiMerge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import dmLab.array.domain.ADXDomain;
import dmLab.discretizer.Discretizer;
import dmLab.discretizer.DiscretizerParams;
import dmLab.utils.ArrayUtils;
import dmLab.utils.MathUtils;

public class DiscChiMerge extends Discretizer
{	
	//	discretization based on CHI2 statistic for one decision
	public DiscChiMerge()
	{	
	}
	//	*******************************************************
	@Override
	public float[] findRanges(float[] values, float[] decision, DiscretizerParams discParams)
	{
		int intervals = discParams.discIntervals();
		if(values.length < intervals){
			ranges = null;
			return getRanges();
		}
		float[] decValues = ArrayUtils.unique(decision);

		float[] cutPointsArray = new float[intervals];
		HashSet<Float> cutPointsSet = new HashSet<Float>(); 
		intervals=(int)Math.ceil((float)intervals/(float)decValues.length);

		for(int i=0;i<decValues.length;i++)
		{
			Arrays.fill(cutPointsArray,0.0f);
			cutPointsArray = discChi(values, decision, decValues[i], discParams.discIntervals(), discParams.maxSimilarity());
			if(cutPointsArray==null)
				return null; //if values is less number than discRanges
			else
				cutPointsSet.addAll(Arrays.asList(ArrayUtils.float2Float(cutPointsArray)));			
		}
		Float[] tmpcutPointsArray = new Float[1];
		tmpcutPointsArray = cutPointsSet.toArray(tmpcutPointsArray);
		Arrays.sort(tmpcutPointsArray);
		ranges = new ArrayList<Float>();
		ranges.addAll(Arrays.asList(tmpcutPointsArray));
		ranges.add(MathUtils.maxValue(values));
		
		return getRanges();
	}
	//	*******************************************************
	public float[] discChi(float[] values, float[] decision, float decValue, int intervals, float maxSimilarity)
	{        
		if(values.length < intervals)
			return null;

		if(values.length != decision.length)
		{
			System.err.print("size of attributeArray is different than size of decision Array!");
			return null;
		}

		ADXDomain domain=new ADXDomain();
		float decisions[]= {decValue};
		domain.createDomain(null,values,decision,decisions);

		if(domain.decisionsNumber()>intervals)
			return null;
		final int domainSize=domain.size();
		if(domainSize<intervals)
			return null;

		domain.sort();		
		float sortAttributeArray[]= new float [domainSize];
		int chi2GroupsNumber=domainSize;
		int positives[]=new int[domainSize];
		int negatives[]=new int[domainSize];
		domain.getData(sortAttributeArray,positives,negatives,0);

		ChiMerge chiMerge=new ChiMerge(sortAttributeArray);
		for (long i=sortAttributeArray.length;i>intervals;i--)
		{
			chiMerge.createCouples();
			chiMerge.calculateChi2(positives,negatives);
			if(chiMerge.getMinChi() > maxSimilarity)
			{
				//System.out.println("Similarity has been reached!");
				break;
			}
			chi2GroupsNumber=chiMerge.defineNewGroup();
		}
		float myCutPoints[]=new float[chi2GroupsNumber];		
		chiMerge.setRanges(myCutPoints, sortAttributeArray);

		return myCutPoints;
	}
	//	*******************************************************
}
