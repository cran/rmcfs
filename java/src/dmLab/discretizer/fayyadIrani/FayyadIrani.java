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
package dmLab.discretizer.fayyadIrani;

import java.util.ArrayList;

import weka.core.Utils;
import dmLab.discretizer.DiscretizerParams;
import dmLab.discretizer.change.CutPoints;
import dmLab.discretizer.change.DiscChange;
import dmLab.discretizer.change.Event;
import dmLab.utils.ArrayUtils;
import dmLab.utils.MathUtils;

public class FayyadIrani extends DiscChange
{
	//**********************************
	public FayyadIrani()
	{
	}
	//**************************************** 
	//discretization based on Fayyad and Irani algorithm
	@Override
	public float[] findRanges(float[] values, float[] decision, DiscretizerParams discParams)
	{
		Event events[]=getEvents(values, decision);              
		CutPoints cPoints = calcCutPoints(events);
		//System.out.println("MDR DEBUG cPoints: \n"+cPoints.toString()+'\n');

		ranges = selectBestCutPoint(cPoints, 0, cPoints.size());                
		if(ranges!=null)
			ranges.add(MathUtils.maxValue(values));

		return getRanges();
	}
	//  ***********************************
	protected ArrayList<Float> selectBestCutPoint(CutPoints cPoints, int begin, int end)
	{        
		double priorEntropy;
		double priorEventsNumber;
		double priorClassesNumber;

		double bestEntropy,currentEntropy;
		double bestLeftSet[]=null;
		double bestRightSet[]=null;

		int bestCutPointIndex=-1;

		if (end-begin<2)
			return null;
		
		ArrayList<Float> cutPointsList = new ArrayList<Float>();
		double priorSet[]=cPoints.getClassesSum(begin,end);    
		//System.out.println("PriorSet: \n" + Arrays.toString(priorSet));
		priorEntropy=MathUtils.entropy(priorSet,false);
		priorEventsNumber=MathUtils.sum(priorSet);
		priorClassesNumber=priorSet.length-ArrayUtils.count(priorSet,0);

		bestEntropy=priorEntropy;
		final int end_1=end-1;//only for speed up

		for(int i=begin;i<end_1;i++)
		{
			double leftSet[]=cPoints.getClassesSum(begin,i+1);
			double rightSet[]=cPoints.getClassesSum(i+1,end);

			//System.out.println("MDR leftSet" + Arrays.toString(leftSet));
			//System.out.println("MDR rightSet" + Arrays.toString(rightSet));

			double leftSize=MathUtils.sum(leftSet);
			double rightSize=MathUtils.sum(rightSet);    
			//System.out.println(cutPoints.getValue(i));

			currentEntropy=(leftSize/priorEventsNumber)*MathUtils.entropy(leftSet,false) + (rightSize/priorEventsNumber)*MathUtils.entropy(rightSet,false);

			if(currentEntropy<bestEntropy)
			{
				bestEntropy=currentEntropy;
				bestCutPointIndex=i;
				bestLeftSet=leftSet;
				bestRightSet=rightSet;
			}
		}
		//System.out.println("selected Cut Point: "+cPoints.getValue(bestCutPointIndex));
		double priorCutPointsNumber=cPoints.getCutsSum(begin, end) ;
		//if it is a good split        
		if(testCutPoint(priorEntropy, priorClassesNumber,priorCutPointsNumber,priorEventsNumber, bestEntropy, bestLeftSet, bestRightSet))
		{    
			//System.out.println("GOOD!");
			cutPointsList.add(cPoints.getValue(bestCutPointIndex));
			selectBestCutPoint(cPoints, begin, bestCutPointIndex+1);
			selectBestCutPoint(cPoints, bestCutPointIndex+1, end);
		}else{
			//System.out.println("BAD!");
		}		
		return cutPointsList;
	}
	//****************************************
	protected boolean testCutPoint(double priorEntropy, double priorClassesNumber, double priorCutPointsNumber,
			double priorEventsNumber, double bestEntropy,double leftSet[],double rightSet[])
	{               
		double gain = priorEntropy - bestEntropy;
		if (gain <= 0)
			return false;

		double entropyLeft=MathUtils.entropy(leftSet,true);
		double entropyRight=MathUtils.entropy(rightSet,false);
		double numClassesLeft=leftSet.length-ArrayUtils.count(leftSet,0);
		double numClassesRight=rightSet.length-ArrayUtils.count(rightSet,0);

		//Compute delta
		double delta = (MathUtils.log2(Math.pow(3, priorClassesNumber) - 2) 
				- (( priorClassesNumber*priorEntropy) -  (numClassesRight*entropyRight) - (numClassesLeft*entropyLeft)));

		//Check if split can be accepted

		double weight=((Utils.log2(priorCutPointsNumber) + delta) / priorEventsNumber);
		/*
        System.out.println("priorEntropy: "+priorEntropy);
        System.out.println("bestEntropy: "+bestEntropy);
        System.out.println("priorClassesNumber: "+priorClassesNumber);
        System.out.println("priorEventsNumber: "+priorEventsNumber);
        System.out.println("priorCutPointsNumber: "+priorCutPointsNumber);
        System.out.println(Arrays.toString(leftSet));
        System.out.println(Arrays.toString(rightSet));
        System.out.println("delta: "+delta);
        System.out.println("entropyLeft: "+entropyLeft);
        System.out.println("entropyRight: "+entropyRight);
        System.out.println("gain: "+gain);
        System.out.println("weight: "+weight);
        //*/
		return gain > weight;
	}
	//*******************************************************   
}
