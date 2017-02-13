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
package dmLab.classifier.adx.ruleFamily;

import java.util.Arrays;

import dmLab.array.FArray;
import dmLab.classifier.adx.ADXParams;
import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.ruleSet.RuleSet;
import dmLab.classifier.adx.ruleSet.SelectRules;
import dmLab.classifier.adx.selector.SelectorList;
import dmLab.utils.ArrayUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;

public class RuleFamily
{
    protected RuleSet ruleSetArray[];
    public SelectorList selectorListArray[];
	protected FArray trainArray;
	protected double classSensitivity[];
    protected ADXParams adxParams;
    protected double scores[];
    
    public static final int precision=5;
    public float finalSelectionTime=0;
    
    public static final int SENSITIVITY_NO=0;
    public static final int SENSITIVITY_ARRAY=1;
    public static final int SENSITIVITY_APRIORI=2;
    
	public boolean verbose = false;
	//*************************************
	public RuleFamily(int decisionValuesNumber,int maxComplexLength,ADXParams adxParams)
	{
        this.adxParams=adxParams;
        ruleSetArray=new RuleSet[decisionValuesNumber];
		selectorListArray=new SelectorList[decisionValuesNumber];
		classSensitivity=new double[decisionValuesNumber];
		Arrays.fill(classSensitivity,1.0);
		for(int i=0;i<ruleSetArray.length;i++)
		{
			ruleSetArray[i]=new RuleSet(maxComplexLength,adxParams);			
			selectorListArray[i]=new SelectorList();
		}
		init();
	}
//	*************************************
	public boolean createRules(FArray trainArray)
	{		
        long start,stop;
               
        final float decisionValues[]=trainArray.getDecValues();
		final int decisionValuesNumber=decisionValues.length;
		Coverage coverage;		
		final int eventNumber=trainArray.rowsNumber();

        this.trainArray=trainArray;
		init();
		
		for(int decValIndex=0;decValIndex<decisionValuesNumber;decValIndex++)
		{
			if(verbose) System.out.println("-----> Building Rules for Decision: "
					+ trainArray.attributes[trainArray.getDecAttrIdx()].name
					+"="+trainArray.getDecValuesStr()[decValIndex] + " <-----");
			
			selectorListArray[decValIndex]=new SelectorList();
			selectorListArray[decValIndex].verbose=verbose;
			
			if(verbose) System.out.println("Creating selectors base...");
			selectorListArray[decValIndex].createSelectorBase(trainArray,decValIndex);
			if(verbose) System.out.println(" Selectors base created. Selectors base size = "+selectorListArray[decValIndex].size());
			
			//System.out.println("DEBUG\n" + selectorListArray[decValIndex].toString(trainArray));
			
			if(verbose) System.out.println("Creating rules...");
			ruleSetArray[decValIndex].createRules(trainArray,selectorListArray[decValIndex],decValIndex);
			//before merging i delete complexes that have p<n           
            ruleSetArray[decValIndex].deleteComplexesPosLessNeg();
            
            if(adxParams.mergeCondition!=Complex.MERGE_NO){
				if(verbose) System.out.print("Merging rules...");
				ruleSetArray[decValIndex].mergeRules(selectorListArray[decValIndex],trainArray);
				if(verbose) System.out.println(" Rules merged.");
			}
			
			if (adxParams.useSensitivity==SENSITIVITY_ARRAY)
				classSensitivity[decValIndex] = adxParams.getSensitivity(decValIndex);
            else{ // SENSITIVITY_NO || SENSITIVITY_APRIORI
                double posEvents=trainArray.getADXDomain(trainArray.getDecAttrIdx()).getTotalPositives(decValIndex);                
                classSensitivity[decValIndex] = posEvents / eventNumber;
                if(verbose) System.out.println(" Probability of the class occurence = "+ GeneralUtils.formatFloat(classSensitivity[decValIndex],precision));
            }
						
			if(verbose){
				System.out.print("Calculating Coverages...");
				coverage=calcCoverage(trainArray,ruleSetArray[decValIndex],selectorListArray[decValIndex],decisionValues[decValIndex]);
				coverage.precision=precision;
				System.out.println(coverage.toString());
			}
            
            start=System.currentTimeMillis();
            SelectRules selectRules=new SelectRules(); 
			if(verbose)
				System.out.print("Selecting significant rules...");
			
			if(adxParams.selSignificantMethod==0)
				ruleSetArray[decValIndex].deleteComplexesCoversNeg();
			else if(adxParams.selSignificantMethod==1)
				ruleSetArray[decValIndex].deleteComplexesPosLessNeg();
			else if(adxParams.selSignificantMethod==2)
                ruleSetArray[decValIndex].deleteComplexesQLess(adxParams.qMin);							
			else if(adxParams.selSignificantMethod==3)
                selectRules.selectFinalRuleSet(ruleSetArray[decValIndex],adxParams,adxParams.getFinalBeam(decValIndex));				
			else if(adxParams.selSignificantMethod==4)
                selectRules.selectMinFinalRuleSet_fast(ruleSetArray[decValIndex],trainArray,selectorListArray[decValIndex],adxParams,adxParams.getFinalBeam(decValIndex));
			else
				System.err.println("Incorrect adxParams.selSignificantMethod");
            
            stop=System.currentTimeMillis();
            finalSelectionTime+=(stop-start)/1000.0f; 
			            
			if(verbose){
            	System.out.println(" Significant rules have been selected.");
				System.out.print("Calculating Coverages...");
				coverage=calcCoverage(trainArray,ruleSetArray[decValIndex],selectorListArray[decValIndex],decisionValues[decValIndex]);				
				coverage.precision=precision;
				System.out.println(coverage.toString());
			}
		}
		return true;
	}
//	*************************************
	public void saveRuleFamily(String inputFileName)
	{
		for(int decisionValIndex=0;decisionValIndex<trainArray.getDecValues().length;decisionValIndex++)
			ruleSetArray[decisionValIndex].save(selectorListArray[decisionValIndex],trainArray,inputFileName+"_"+trainArray.getDecValuesStr()[decisionValIndex]);
	}
//	*************************************
    public void prepareClassification()
    {
        for(int i=0;i<ruleSetArray.length;i++)
        {
            ruleSetArray[i].createLinks(adxParams);
            ruleSetArray[i].calcGlobalParams();
        }
    }
//  *************************************
	public float classifyEvent(FArray array,int eventIndex)
	{
		double currentScore;
        double prevScore=-1;
		int highestScoreIndex=-1;
		double highestScore=-1;
		final float decisionValues[]=array.getDecValues();
		boolean scoresEqual=true; 
        
		for(int decisionValIndex=0;decisionValIndex<ruleSetArray.length;decisionValIndex++)
		{
			currentScore=ruleSetArray[decisionValIndex].calcScore(selectorListArray[decisionValIndex],array,eventIndex);
			if(classSensitivity.length!=1)
				currentScore=currentScore*classSensitivity[decisionValIndex];
			//****** DEBUG INFO
			System.out.print("\t"+decisionValues[decisionValIndex]+" sc: "+ currentScore);
			//****** DEBUG INFO
			if(currentScore>highestScore)
			{
				highestScoreIndex=decisionValIndex;
				highestScore=currentScore;
			}
            if(scoresEqual)
            {
                if(currentScore!=prevScore && decisionValIndex>0)
                    scoresEqual=false;            
                prevScore=currentScore;
            }
		}
        System.out.println();
        //if score method 6 and if all the values have been equal
        //use alternative method
        if(highestScoreIndex==-1)
            System.out.println("scoresEqual \t sc: "+ highestScore);
        else if(scoresEqual)
            System.out.println("scoresEqual \t"+decisionValues[highestScoreIndex]+" sc: "+ highestScore);
            
        if(adxParams.scoreMethod==6 && scoresEqual && highestScore==1)
        {            
            highestScoreIndex=-1;
            highestScore=-1;
            for(int decisionValIndex=0;decisionValIndex<ruleSetArray.length;decisionValIndex++)
            {
                currentScore=ruleSetArray[decisionValIndex].complexLinks.alternativeScore;
                if(classSensitivity.length!=1)
                    currentScore=currentScore*classSensitivity[decisionValIndex];
                //****** DEBUG INFO
                System.out.print("scoresEqual =1 \t"+decisionValues[decisionValIndex]+" sc: "+ currentScore);
                //****** DEBUG INFO
                if(currentScore>highestScore)
                {
                    highestScoreIndex=decisionValIndex;
                    highestScore=currentScore;
                }
            }            
        }
        //****** DEBUG INFO
        //if(highestScoreIndex!=-1) System.out.println("ok \t result: "+ decisionValues[highestScoreIndex]);
        //****** DEBUG INFO
        
        if(highestScore>0.0f)
			return decisionValues[highestScoreIndex];
		else
			return -1.0f;
	}
//  *************************************
    public float classifyEvent2(FArray array,int eventIndex)
    {
        final float decisionValues[]=array.getDecValues();
        scores=calcScores(array, eventIndex, false);
        //****** DEBUG INFO
        //System.out.println("scores: "+Arrays.toString(scores));
        if(adxParams.useSensitivity==SENSITIVITY_ARRAY 
                || adxParams.useSensitivity==SENSITIVITY_APRIORI)
            for(int i=0;i<scores.length;i++)
                scores[i]*=classSensitivity[i];
            
        int maxIndex=ArrayUtils.maxIndex(scores);
        if(adxParams.scoreMethod>=6 && maxIndex==-1)
        {
            //all are equal and greater than zero
            scores=calcScores(array, eventIndex, true);
            //****** DEBUG INFO
            //System.out.println("scoresAlt: "+Arrays.toString(scores));
            maxIndex=ArrayUtils.maxIndex(scores);            
        }
        //System.out.println("maxIndex: "+maxIndex);
        
        if(maxIndex!=-1.0f)
            return decisionValues[maxIndex];
        else//if class is not decided
        {
            //System.out.println(" --- Class is not decided. scores: "+Arrays.toString(scores));
            if(MathUtils.sum(scores)!=0)                
                for(int i=0;i<scores.length;i++)
                    scores[i]*=classSensitivity[i];
            else
                for(int i=0;i<scores.length;i++)
                    scores[i]=classSensitivity[i];
                        
            //System.out.println(" classSensitivity:"+Arrays.toString(classSensitivity));
            //System.out.println(" scores2:"+Arrays.toString(scores));
            maxIndex=ArrayUtils.getRouletteIndex(classSensitivity);
            //System.out.println(" chosen class: "+maxIndex);
            return maxIndex;
        }
    }
//  *************************************
    public double[] getLastScores()
    {
        return scores;
    }
//  *************************************
    public double[] calcScores(FArray array,int eventIndex,boolean useAlternativeScore)
    {
        double scores[]=new double[array.getDecValues().length];
        
        for(int i=0;i<ruleSetArray.length;i++)
        {
            if(!useAlternativeScore)            
                scores[i]=ruleSetArray[i].calcScore(selectorListArray[i],array,eventIndex);
            else
                scores[i]=ruleSetArray[i].complexLinks.alternativeScore;
        }    
        return scores;        
    }    
//	*************************************
	public void saveSymbolicSelectors(String fileName)
	{
		for(int decisionValIndex=0;decisionValIndex<selectorListArray.length;decisionValIndex++)
			selectorListArray[decisionValIndex].save(fileName+"_"+trainArray.getDecValuesStr()[decisionValIndex]);
	}
//	*************************************
	public void saveSelectors(String fileName)
	{
		for(int decisionValIndex=0;decisionValIndex<selectorListArray.length;decisionValIndex++)
			selectorListArray[decisionValIndex].save(fileName+"_"+trainArray.getDecValuesStr()[decisionValIndex],trainArray);
	}
//	*************************************
	public Coverage calcCoverage(FArray array,RuleSet ruleSet,SelectorList selectorList,float decVal)
	{
		int covered=0;
		int posCovered=0;
		int pos=0;
		int negCovered=0;
		int neg=0;
		final int eventsNumber=array.rowsNumber();
		final int decisionId=array.getDecAttrIdx();
		for(int i=0;i<eventsNumber;i++)
		{
			final float dec=array.readValue(decisionId,i);
			if(dec==decVal)
				pos++;
			else
				neg++;
			if(ruleSet.covers(selectorList,array,i))
			{
				covered++;
				if(dec==decVal)
					posCovered++;
				else
					negCovered++;				
			}
		}
		Coverage coverage=new Coverage();
		coverage.coverage=(float)covered/(float)eventsNumber;
		coverage.posCoverage=(float)posCovered/(float)pos;
		coverage.negCoverage=(float)negCovered/(float)neg;
		return coverage;
	}
//	*************************************
	public int ruleSets()
	{
		return ruleSetArray.length;
	}
//	*************************************
	public RuleSet getRuleSet(int index)
	{
		if(index>=0 && index<ruleSetArray.length)
			return ruleSetArray[index];
		else
			return null;
	}
//	*************************************
	public SelectorList getSelectorList(int index)
	{
		if(index>=0 && index<selectorListArray.length)
			return selectorListArray[index];
		else
			return null;
	}
	//*************************************
	private void init()
	{
		for(int i=0;i<ruleSetArray.length;i++)
		{
			ruleSetArray[i].verbose = verbose;
			selectorListArray[i].verbose = verbose;			
		}
	}
	//*************************************
	public boolean setTrainArray(FArray array)
	{
		trainArray=array;
		return true;
	}
	//*************************************    
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		for(int decisionValIndex=0;decisionValIndex<trainArray.getDecValues().length;decisionValIndex++)
			tmp.append(ruleSetArray[decisionValIndex].toString(selectorListArray[decisionValIndex],trainArray));
		return tmp.toString();
	}
	//*************************************
}
