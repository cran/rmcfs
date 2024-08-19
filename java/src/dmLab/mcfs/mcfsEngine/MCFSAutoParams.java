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
package dmLab.mcfs.mcfsEngine;

import java.util.Arrays;
import dmLab.array.FArray;
import dmLab.mcfs.MCFSParams;
import dmLab.utils.ArrayUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;

public class MCFSAutoParams {
	
	public static int AUTO = -1;
	public static String AUTO_LABEL = "auto";	
	
	//********************************	
	public static String valueToString(float value){
		if(value == AUTO)
			return AUTO_LABEL;
		else
			return Float.toString(value);
	}
	//********************************	
	public static float valueToFloat(String paramName, String value){
		if(value.trim().equalsIgnoreCase(AUTO_LABEL))
			return AUTO;
		else{
			float retVal = AUTO;
			try{
				retVal = Float.valueOf(value).floatValue();
			}catch(NumberFormatException e){
				System.err.println("Warning! Incorrect value of " + paramName +": " + value + ". Using default value = 'auto'.");
				return AUTO;
			}
			return retVal;			
		}
	}
	//********************************	
	public static int setProjectionSize(float projectionSize, int projectionSizeMin, int projectionSizeMax, FArray array)
	{		
		// minus decision attribute
		int attributes = array.colsNumber() - 1;	
		int projectionSizeValue;
		
		if(projectionSize == AUTO){
			projectionSizeValue = (int)Math.round(Math.sqrt(attributes));
		}else if (projectionSize > 0 && projectionSize < 1){
			//if projectionSize < 1 it means fraction of attributes
			projectionSizeValue = (int)Math.round(attributes * projectionSize);
		}else if(projectionSize >= 1){ 
			projectionSizeValue = (int)projectionSize; 
		}else{			
			System.err.println("Warning! Incorrect value of projectionSize:" + projectionSize + ". Using default value = 'auto'.");
			projectionSizeValue = (int)Math.round(Math.sqrt(attributes));
		}
		
		if(projectionSizeValue >= array.colsNumber()){
			System.err.println("Warning! projectionSize(m) is larger than number of attributes. Using max value = " + attributes);
			projectionSizeValue = attributes;
		}		
		if(projectionSizeValue < projectionSizeMin){
			System.err.println("Warning! ProjectionSize(m) < 1. Using min value = " + projectionSizeMin);
			projectionSizeValue = projectionSizeMin;
		}
		if(projectionSizeValue > projectionSizeMax){
			System.err.println("Warning! ProjectionSize(m) = " + projectionSizeValue + ". Using max value = " + projectionSizeMax);
			projectionSizeValue = projectionSizeMax;
		}
		
		return projectionSizeValue;
	}
	//  ****************************************************
	public static int setProjections(int projections, float projectionSize, int featureFreq, FArray array)
	{		
		int attributes = array.colsNumber() - 1;
		int projectionsValue;
		
		if(projections == AUTO){			
			projectionsValue = Math.round(attributes/projectionSize * featureFreq);
		}else if(projections >= 1){
			projectionsValue = projections;
		}else{
			System.err.println("Warning! Incorrect value of projections: "+projections+" Using default value = 'auto'.");
			projectionsValue = Math.round(attributes/projectionSize * featureFreq);
		}		
		return projectionsValue;
	}
	//  ****************************************************
	public static float balanceModel(double ratio)
	{
		double[] ratioModel = new double[] {0.25,	0.2,	0.15,	0.1,	0.075,	0.05,	0.025,	0.01,	0.0075,	0.005,	0.0025,	0.001};				
		double[] paramModel = new double[] {1,		1,		1.20,	1.5,	1.75,	2,		2.5,	3,		3.25,	3.5,	3.75,	4};
		
		int id;
		for(id=0; id <ratioModel.length; id++){
			if(ratio > ratioModel[id]){
				break;
			}				
		}
		if(id == 0)
			return (float)paramModel[0];				
		else if(id >= ratioModel.length)
			return (float)paramModel[paramModel.length-1];
				
		double a = (ratio - ratioModel[id-1])/(ratioModel[id] - ratioModel[id-1]);
		float param = (float)(paramModel[id-1] + (a * (paramModel[id] - paramModel[id-1])));
		return param;
	}
	//  ****************************************************
	public static int[] getBalancedClassSizes(MCFSParams mcfsParams, FArray array)
	{
		float balance = mcfsParams.balance;
		if(!array.isTargetNominal()){
			return null;
		}
		
		int decIndex = array.getDecAttrIdx();		
		int[] classSizes = ArrayUtils.distribution(array.getColumn(decIndex), array.getDecValues());
		float minSize = MathUtils.minValue(ArrayUtils.int2float(classSizes));
		float maxSize = MathUtils.maxValue(ArrayUtils.int2float(classSizes));
		float classSizeRatio = minSize/maxSize;
	
		float balanceValue;
		int balancedClassSizes[] = classSizes.clone();

		if(balance == 0){
			balanceValue = 0;
			balancedClassSizes = null;
		}else if(balance >= 1){
			balanceValue = balance;
		}else if(balance == AUTO){
			balanceValue = balanceModel(classSizeRatio);
		}else{
			System.err.println("Warning! Incorrect value of balance: "+balance+" Using default value = 'auto'.");			
			balanceValue = balanceModel(classSizeRatio);
		}
		
		if(balanceValue > 1){
			double b = (double)minSize / Math.pow((double)minSize, 1.0/balanceValue);		
			for(int i=0; i<balancedClassSizes.length; i++){
				int newSize = (int)Math.ceil(b * Math.pow((double)balancedClassSizes[i], 1.0/balanceValue));
				if(balancedClassSizes[i] > newSize)
					balancedClassSizes[i] = newSize;
			}	
		}
		
		if((balanceValue == 0 || balanceValue == 1) && classSizeRatio < 0.1)
			System.err.println("Warning! Classes are imbalanced (classSizeRatio = "+GeneralUtils.formatFloat(classSizeRatio, 5) + "). It is recomended to set mcfs.balance = auto");			
		
		System.out.print("Classes = " + Arrays.toString(array.dictionary.toString(array.getDecValues())) + ", Sizes = " + Arrays.toString(classSizes));		
		if(balanceValue > 1)
			System.out.print(", balanced = " + Arrays.toString(balancedClassSizes));
		System.out.println(", classSizeRatio = " + classSizeRatio + ", balanceValue = " + balanceValue);
		
		return balancedClassSizes;
	}
	//  ****************************************************
	public static int[] getSplitSetClassSizes(MCFSParams mcfsParams, FArray array) {		
		if(!array.isTargetNominal()){
			return null;
		}		
		int[] splitSetClassSizes = null;
		int splitSetSize = mcfsParams.splitSetSize;
		if(mcfsParams.balance <= 1 & (splitSetSize==0 | splitSetSize >= array.rowsNumber())) {
			splitSetClassSizes = null;
		}else {
			int sum = MathUtils.sum(mcfsParams.balancedClassSizes);
			if(sum > splitSetSize) {
				splitSetClassSizes = mcfsParams.balancedClassSizes;
				for(int i=0;i<splitSetClassSizes.length;i++) {
					splitSetClassSizes[i] = (int)Math.ceil(splitSetSize * (float)splitSetClassSizes[i]/(float)sum);  
				}				
			}
		}
				
		return(splitSetClassSizes);
	}
	//  ****************************************************
	
}
