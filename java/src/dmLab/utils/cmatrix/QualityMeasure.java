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
package dmLab.utils.cmatrix;


public class QualityMeasure
{
	public static int ACC = 1;
	public static int WACC = 2;
	public static int PEARSON = 3;
	public static int MAE = 4;
	public static int RMSE = 5;
	public static int SMAPE = 6;	
	
	/*
//*** calculates true based accuracy
  public double calcTrueAcc()
  {
    return (TP/f)*(c/(f+c)) + (TN/c)*(f/(f+c));
  }
//***********************************************
//*** calculates false based accuracy
  public double calcFalseAcc()
  {
    return (f*FP)/(c*(f+c))+(c*FN)/(f*(f+c));
  }
	 */
	//***********************************************
	//*** calculates weighted accuracy from input confusion matrix
	public static double calcWAcc(double confusionMatrix[][])
	{
		int rowSum,rows=0;
		double wAcc=0;
		for (int i=0;i<confusionMatrix[0].length;i++){
			rowSum=0;
			for (int j=0;j<confusionMatrix[0].length;j++)
				rowSum+=confusionMatrix[i][j];
			if(rowSum!=0){
				wAcc+=((double)confusionMatrix[i][i]/(double)rowSum );
				rows++;
			}
		}
		wAcc=wAcc/(rows);
		return wAcc;
	}
	//***********************************************
	//*** calculates regular accuracy from input confusion matrix
	public static double calcAcc(double confusionMatrix[][])
	{
		int sum=0;
		int trueDecision=0;

		for (int i=0;i<confusionMatrix[0].length;i++){
			for (int j=0;j<confusionMatrix.length;j++){
				sum+=confusionMatrix[i][j];
				if(i==j)
					trueDecision+=confusionMatrix[i][j];
			}
		}
		return (double)trueDecision/(double)sum;
	}
	//***********************************************
	//*** calculates true positive rate from input confusion matrix
	public static double calcTPRate(double confusionMatrix[][], int valueIndex)
	{
		int rowSum=0;
		for (int j=0;j<confusionMatrix[0].length;j++)
			rowSum+=confusionMatrix[valueIndex][j];

		return (double)confusionMatrix[valueIndex][valueIndex]/(double)rowSum;
	}
	//***********************************************
	//**** calculates false positive rate from input confusion matrix
	public static double calcFPRate(double confusionMatrix[][], int valueIndex)
	{
		int rowSum=0;
		int FP=0;
		for (int i=0;i<confusionMatrix[0].length;i++)
		{
			if(i!=valueIndex){
				FP+=confusionMatrix[i][valueIndex];
				for (int j=0;j<confusionMatrix.length;j++)
					rowSum+=confusionMatrix[i][j];
			}
		}
		return (double)FP/(double)rowSum;
	}
	//***********************************************
	//*** calculates number of events that were misclassified
	public static int errors(double confusionMatrix[][])
	{
		int errors=0;

		for (int i=0;i<confusionMatrix[0].length;i++){
			for (int j=0;j<confusionMatrix.length;j++)
				if(i!=j)
					errors+=confusionMatrix[i][j];
		}
		return errors;
	}
	//***********************************************  
}
