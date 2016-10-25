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

import dmLab.utils.GeneralUtils;



public class ConfusionMatrix
{
	private String decision;
	private double confusionMatrix[][];
	private String decValuesStr[];
	private String decValuesStrDict[];
	private float decValuesDbl[];
	private boolean printAttrDictList;
	private int maxSize = 10;
	//**************************************
	//class stores confusion matrix
	public double[][] getMatrix()
	{
		return confusionMatrix;
	}
	//**************************************
	//initiation of matrix
	public ConfusionMatrix(String decision, float decisionValuesDbl[], String decisionValuesStr[])
	{
		this.decision = decision;
		decValuesDbl=decisionValuesDbl;
		decValuesStr=decisionValuesStr;
		decValuesStrDict= new String [decisionValuesStr.length];
		printAttrDictList=false;
		createDecValuesStrDict();
		confusionMatrix=new double [decValuesDbl.length+1][decValuesDbl.length+1];
	}
	//**************************************
	//initiation of matrix
	public ConfusionMatrix(String decision, String decisionValuesStr[])
	{
		this.decision = decision;
		decValuesDbl=null;
		decValuesStr=decisionValuesStr;
		decValuesStrDict= new String [decisionValuesStr.length];
		printAttrDictList=false;
		createDecValuesStrDict();
		confusionMatrix=new double [decisionValuesStr.length+1][decisionValuesStr.length+1];
	}
	//**************************************
	//*** creates dictionary based on decision values
	// if names are too long to print there is conversion of names
	private void createDecValuesStrDict()
	{
		for(int i=0;i<decValuesStrDict.length;i++){
			if(decValuesStr[i].length()<maxSize){
				decValuesStrDict[i]=decValuesStr[i];
			}else{
				decValuesStrDict[i]="val_"+i;
				printAttrDictList=true;
			}
		}
	}
	//**************************************
	//prints attributes names with possible names conversion
	private String labelsToString()
	{
		StringBuffer tmp = new StringBuffer();   
		tmp.append("Captions:").append("\n");
		for(int i=0;i<decValuesStrDict.length;i++)
			if(!decValuesStrDict[i].equalsIgnoreCase(decValuesStr[i]))
				tmp.append(decValuesStrDict[i]+"="+decValuesStr[i]).append("\n");
		tmp.append("\n");
		return tmp.toString();
	}
	//**************************************
	//adds one decision to matrix
	public void add(double realDecision,double predictedDecision)
	{
		confusionMatrix[findIndex(realDecision)][findIndex(predictedDecision)]++;
	}
	//**************************************
	//adds one decision  to matrix
	public void add(String realDecision,String predictedDecision)
	{
		confusionMatrix[findIndex(realDecision)][findIndex(predictedDecision)]++;
	}
	//**************************************
	//adds one decision to matrix
	public void add(ConfusionMatrix SourceConfusionMatrix)
	{
		for(int i=0;i<confusionMatrix.length;i++)
			for(int j=0;j<confusionMatrix.length;j++)
				confusionMatrix[i][j]+=SourceConfusionMatrix.confusionMatrix[i][j];
	}
	//**************************************
	//finds column index when decisionValue is specified
	private int findIndex(String decisionValue)
	{
		for(int i=0;i<decValuesStr.length;i++)
		{
			if(decValuesStr[i].equalsIgnoreCase(decisionValue))
				return i;
		}
		return decValuesStr.length;
	}
	//**************************************
	//finds column index when decisionValue is specified
	private int findIndex(double decisionValue)
	{
		if(decisionValue==-1)
			return decValuesDbl.length;
		for(int index=0;index<decValuesDbl.length;index++)
			if(decValuesDbl[index]==decisionValue)
				return index;
		return decValuesDbl.length;
	}
	//**************************************
	//*** method prints matrix and basic info
	@Override
	public String toString()
	{
		return toString(true, false, true, "\t");
	}
	//**************************************
	//*** method prints matrix
	public String toString(boolean header, boolean fullClassLabels, boolean showOther, String sep)
	{
		StringBuffer tmp=new StringBuffer();
		if(printAttrDictList==true && !fullClassLabels)
			tmp.append(labelsToString());
		if(header){
			tmp.append("Confusion Matrix").append("\n");
			tmp.append("\t\t\tpredicted").append("\n");
		}
		String[] decValuesTmp = decValuesStrDict; 
		if(fullClassLabels)
			decValuesTmp = decValuesStr;

		tmp.append(decision);
		for (int i=0; i<decValuesTmp.length; i++){
			tmp.append(sep).append(decValuesTmp[i]);
		}
		if(showOther)
			tmp.append(sep).append("other");
		tmp.append("\n");
		
		int size = confusionMatrix.length;
		if(!showOther)
			size = confusionMatrix.length - 1;
				
		for (int i=0; i<size; i++)
		{
			if(i<decValuesTmp.length)
				tmp.append(""+decValuesTmp[i]);
			else
				tmp.append("other");

			for (int j=0; j<size; j++)
				tmp.append(sep).append(""+confusionMatrix[i][j]);
			
			tmp.append("\n");
		}
		tmp.append("\n");

		return tmp.toString();
	}
	//**************************************
	//*** method prints additional statistics
	public String statsToString(int precision)
	{
		StringBuffer tmp=new StringBuffer();   
		tmp.append("Accuracy = "+ GeneralUtils.format(QualityMeasure.calcAcc(confusionMatrix),precision)).append("\n");
		tmp.append("WeightedAccuracy = "+ GeneralUtils.format(QualityMeasure.calcWAcc(confusionMatrix),precision)).append("\n");
		tmp.append("True Positive Rate").append("\n");
		for(int i=0;i<decValuesStr.length;i++)
			tmp.append("\t"+decValuesStr[i]+": "+ GeneralUtils.format(QualityMeasure.calcTPRate(confusionMatrix,i),precision)).append("\n");
		tmp.append("False Positive Rate").append("\n");
		for(int i=0;i<decValuesStr.length;i++)
			tmp.append("\t"+decValuesStr[i]+": "+ GeneralUtils.format(QualityMeasure.calcFPRate(confusionMatrix,i),precision)).append("\n");
		return tmp.toString();
	}
	//**************************************
	public float calcMeasure(int measure)
	{
		if(measure == QualityMeasure.ACC)
			return (float) QualityMeasure.calcAcc(confusionMatrix);
		else if(measure == QualityMeasure.WACC)
			return (float) QualityMeasure.calcWAcc(confusionMatrix);
		else
			return Float.NaN;

	}
	//**************************************
	public void cleanMatrix()
	{
		confusionMatrix=new double [decValuesStr.length+1][decValuesStr.length+1]; 
	}
	//**************************************
}
