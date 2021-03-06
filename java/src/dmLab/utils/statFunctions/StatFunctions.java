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
package dmLab.utils.statFunctions;

import dmLab.utils.MathUtils;
import jdistlib.T;
import jdistlib.disttest.DistributionTest;
import jdistlib.disttest.NormalityTest;
import jdistlib.disttest.TestKind;

public class StatFunctions {
	//***************************************************
	public static double[] getConfidenceInterval(double alpha, double x[])
	{
		int size = x.length;
		double stdev = MathUtils.stdev(x);
		double avg = MathUtils.mean(x);
		//System.out.println("size: "+size+" avg: "+avg+" stdev: "+stdev);
		double t=0;
		try{
			//colt version deprecated
			//t = Probability.studentTInverse(alpha, size-1);
			//jdistlib version of the same as above
			t = T.quantile(1-alpha/2, size-1, true, false);
		}
		catch(Exception e){
			System.out.println("T-Student Inverse Error:");
			System.out.println("alpha: "+alpha+" size: "+(size-1));
			return null;
		}
		//System.out.println("t-stat: "+t);			
		double conf1=(avg-(t*stdev/Math.sqrt(size)));
		double conf2=(avg+(t*stdev/Math.sqrt(size)));

		double[] ret={conf1,conf2};
		return ret;
	}
	//***************************************************
	//colt version deprecated
	/*
	public static double tTestOneSample(double x[], double mu){
		double t = (MathUtils.mean(x) - mu)/(MathUtils.stdev(x)/(Math.sqrt(x.length)));
		double p = 2 * Probability.studentT(x.length-1, -Math.abs(t));
		return p;
	}
	*/
	//***************************************************
	public static double tTestOneSample(double x[], double mu){

		double ret[] = DistributionTest.t_test(x, mu, TestKind.TWO_SIDED);    	
		return ret[1];
	}
	//***************************************************
	public static double shapiroWilkNormTest(double x[]){

		double w = NormalityTest.shapiro_wilk_statistic(x);    	
		double p = NormalityTest.shapiro_wilk_pvalue(w, x.length);
		//System.out.println("Shapiro-Wilk normality test: W = " + GeneralUtils.format(w,7) + ", p-value = " + GeneralUtils.format(p,7));
		return p;
	}
	//***************************************************
	public static double andersonDarlingNormTest(double x[]){

		double w = NormalityTest.anderson_darling_statistic(x);
		double p = NormalityTest.anderson_darling_pvalue(w, x.length);
		//System.out.println("Anderson-Darling normality test: W = " + GeneralUtils.format(w,7) + ", p-value = " + GeneralUtils.format(p,7));
		return p;
	}
	//***************************************************
	public static float pearson(double x[], double y[])
	{
		if(x.length!=y.length)
			return Float.NaN;
		double pearson=0;
		double avg_x=MathUtils.mean(x);
		double avg_y=MathUtils.mean(y);
		double numerator=0;//licznik
		double denominator_x=0;//mianownik
		double denominator_y=0;//mianownik
		for(int i=0;i<x.length;i++)
		{
			numerator+=(x[i]-avg_x)*(y[i]-avg_y);
			denominator_x+=Math.pow((x[i]-avg_x), 2);
			denominator_y+=Math.pow((y[i]-avg_y), 2);
		}
		pearson = numerator/( Math.sqrt(denominator_x) * Math.sqrt(denominator_y) );
		if(Double.isNaN(pearson))
			pearson = 0f;
		return (float)pearson;
	}
	//  *********************************************
	public static float mae(double x[], double y[]){
		if(x.length!=y.length)
			return Float.NaN;
		double mae=0;
		for(int i=0;i<x.length;i++){
			mae += Math.abs(x[i]-y[i]);
		}
		mae=mae/( x.length );
		return (float)mae;    	
	}
	//  *********************************************
	public static float rmse(double x[], double y[]){
		if(x.length!=y.length)
			return Float.NaN;
		double rmse=0;
		for(int i=0;i<x.length;i++){
			rmse += Math.pow(x[i]-y[i],2);
		}
		rmse = Math.sqrt(rmse/( x.length ));
		return (float)rmse;    	
	}
	//  *********************************************
	//see:
	//https://en.wikipedia.org/wiki/Symmetric_mean_absolute_percentage_error
	public static float smape(double x[], double y[]){
		if(x.length!=y.length)
			return Float.NaN;
		double smape=0;
		for(int i=0;i<x.length;i++){
			smape += Math.abs(x[i]-y[i])/(Math.abs(x[i])+Math.abs(y[i]));
		}
		smape = smape/x.length;
		return (float)smape;    	
	}
	//  *********************************************
}
