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
package dmLab.utils;

import java.util.Arrays;
import java.util.Random;

public class MathUtils
{
	private static Random rn = new Random();

	public MathUtils()
	{
	}
	//***************************************************
	public static double log(double x,double a)
	{
		return Math.log(x)/Math.log(a);
	}
	//***************************************************
	public static double log2(double x)
	{
		return Math.log(x)/Math.log(2);
	}
	//  *************************************
	public static double sum(double array[])
	{
		double sum=0;
		for(int i=0;i<array.length;i++)
			sum+=array[i];
		return sum;
	}
	//  *************************************
	public static float sum(float array[])
	{
		float sum=0;
		for(int i=0;i<array.length;i++)
			sum+=array[i];
		return sum;
	}
	//  *************************************
	public static int sum(int array[])
	{
		int sum=0;
		for(int i=0;i<array.length;i++)
			sum+=array[i];
		return sum;
	}
	//***************************************************
	public static double mean(double array[])
	{
		return MathUtils.mean(array,0,array.length);
	}
	//***************************************************
	public static double mean(double array[], int start, int stop)
	{
		if(stop> array.length || stop<=start)
			return Float.NaN;

		double sum=0;
		int size=0;
		for(int i=start;i<stop;i++){	        
			if(!Double.isNaN(array[i])){
				sum+=array[i];
				size++;
			}	        
		}
		return sum / size;
	}
	//***************************************************
	public static double median(double array[]){
		double[] arrayTmp = array.clone();
		Arrays.sort(arrayTmp);
		double median;
		if (arrayTmp.length % 2 == 0)
			median = (arrayTmp[arrayTmp.length/2] + arrayTmp[arrayTmp.length/2 - 1])/2;
		else
			median = arrayTmp[arrayTmp.length/2];
		
		return median;
	}
	//***************************************************
	public static double stdev(double array[])
	{
		return Math.sqrt(variance(array));
	}
	//***********************************
	public static double variance(double array[])
	{
		if(array.length<=1)
			return 0.0;

		double avg=mean(array);     
		double sum = 0;
		for (int i = 0; i < array.length; i++){
			sum += Math.pow(array[i]-avg,2.0);
		}

		sum = sum/(array.length-1.0);
		return sum;
	}
	//***********************************
	/**
	 * static method for entropy calculation
	 * @param array double[] input values
	 * @param normalize boolean if return value has to be normalized
	 * @return double entropy
	 */
	public static double entropy(double array[], boolean normalize)
	{
		if(array.length==1)
			return 0;
		double entrophy=0;
		double sum=0;

		for(int i=0;i<array.length;i++)
			sum+=array[i];
		for(int i=0;i<array.length;i++)
		{
			double temp=(array[i]/sum);
			if( temp >0)
				entrophy -= temp * MathUtils.log2(temp);
		}
		if(normalize)
			entrophy=entrophy/MathUtils.log2(array.length);
		return entrophy;
	}
	//***********************************
	public static double newton(double n, double k)
	{
		double newton=1;
		//double nominator=n-k;
		//double denominator=1;
		for(int i=0;i<k;i++)
		{
			newton*=n--/k--;
		}
		return newton;
	}
	//***************************************************
	public static int rand(int lo, int hi)
	{
		int n = hi - lo + 1;
		int i = rn.nextInt() % n;
		if (i < 0)
			i = -i;
		return lo + i;
	}
	//***************************************************
	public static float maxValue(float array[])
	{
		if(array.length==0)
			return -1;

		float maxVal=array[0];

		for(int i=1;i<array.length;i++)
			if(array[i]>maxVal)
				maxVal=array[i];

		return maxVal;
	}
	//***************************************************
	public static float minValue(float array[])
	{
		if(array.length==0)
			return -1;

		float minVal=array[0];

		for(int i=1;i<array.length;i++)
			if(array[i]<minVal)
				minVal=array[i];

		return minVal;
	}
	//***************************************************
	public static float mathOperation(float val1, float val2, String operator)
	{
		if(operator.equalsIgnoreCase("+"))
			return val1+val2;
		else if(operator.equalsIgnoreCase("-"))
			return val1-val2;
		else if(operator.equalsIgnoreCase("*"))
			return val1*val2;
		else if(operator.equalsIgnoreCase("/"))
			return val1/val2;		
		else if(operator.equalsIgnoreCase("^"))
			return (float)Math.pow((double)val1, (double)val2);		
		else return Float.NaN; 
	}
	//****************************************
	public static double truncate(double x, int digits)
	{
		double mult = Math.pow(10, digits);
		int intX = (int)(x * mult);	    
		return (double)intX/mult;
	}
	//****************************************

}
