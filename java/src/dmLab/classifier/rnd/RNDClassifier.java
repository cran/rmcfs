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
package dmLab.classifier.rnd;

import java.io.IOException;
import java.util.Random;

import dmLab.array.FArray;
import dmLab.classifier.Classifier;
import dmLab.classifier.Prediction;
import dmLab.classifier.attributeIndicators.J48NodeIndicators;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.ExperimentIndicators;
import dmLab.utils.GeneralUtils;
import dmLab.utils.cmatrix.QualityMeasure;
import dmLab.utils.cmatrix.ConfusionMatrix;

public class RNDClassifier extends Classifier
{
	private RNDParams cfg;
    private Random rand;
    private FArray trainArray;
    //*******************************************************	
	public RNDClassifier()
	{
		super();
		label=labels[RND];
		model=RND;
        params=new RNDParams();
        cfg=(RNDParams)params;
	}
	//*******************************************************	
	@Override
    public boolean train(FArray trainArray)
    {	    
        this.trainArray=trainArray;
        float start,stop;
		start=System.currentTimeMillis();
		if(params.verbose) System.out.println("Training...");
		rand=new Random((long)cfg.seed);
		if(params.verbose) System.out.println("Training is done!");
		stop=System.currentTimeMillis();
		learningTime=(float)((stop-start)/1000.0);
		if(params.verbose) System.out.println("Training time: "+ GeneralUtils.format(learningTime,2) + " s.");
		return true;
	}
	//*******************************************************
	@Override
    public boolean test(FArray testArray)
	{
		float start,stop;		
		if(params.verbose) System.out.print("Testing...");
		start=System.currentTimeMillis();
//		results need to be inserted into specified class
//		ConfusionMatrix it is unified class to store results
		predResult.confusionMatrix=new ConfusionMatrix(testArray.getColNames(true)[testArray.getDecAttrIdx()],
				testArray.getDecValues(),testArray.getDecValuesStr());
		float predictedDecision;
		float realDecision;
		final int testEventsNumber=testArray.rowsNumber();
		final int interval=(int)Math.ceil(0.1*testEventsNumber);
		int threshold=interval;
		predResult.predictions=new Prediction[testEventsNumber];
        
		final int decAttrIndex=testArray.getDecAttrIdx();
		
		for(int i=0;i<testEventsNumber;i++)
		{
			predictedDecision=classifyEvent(testArray,i);
			realDecision=testArray.readValue(decAttrIndex,i);
			predResult.confusionMatrix.add(realDecision,predictedDecision);
            
            String realClassName = testArray.dictionary.toString(realDecision);
			String predictedClassName=testArray.dictionary.toString(predictedDecision);
			predResult.predictions[i]=new Prediction(realClassName, predictedClassName,null);
            
			if(i>threshold && threshold!=0)
			{
				if(params.verbose) System.out.print(""+ (int)(100.0*i/testEventsNumber) + "% ");
				threshold+=interval;
			}
		}
		System.out.print("100% ");
		stop=System.currentTimeMillis();
		testingTime=(float)((stop-start)/1000.0);
		if(params.verbose) System.out.println(" Done!");
		if(params.verbose) System.out.println("Testing time: "+ GeneralUtils.format(testingTime,2) + " s.");		
		return true;
	}
//	*******************************************
//	returns double value of predicted class
	@Override
    public float classifyEvent(FArray array,int eventIndex)
	{
		double currentScore;
		int highestScoreIndex=-1;
		double highestScore=0;
		final float decisionValues[]=array.getDecValues(); //for speed
		
		for(int decisionValIndex=0;decisionValIndex<decisionValues.length;decisionValIndex++)
		{
			currentScore=testEvent(array,eventIndex,decisionValIndex); //finding score for each decision
			if(params.verbose) System.out.print("\t"+decisionValues[decisionValIndex]+" "+ GeneralUtils.format(currentScore,6));
			if(currentScore>highestScore) //the highest score the highest
			{
				highestScoreIndex=decisionValIndex;
				highestScore=currentScore;
			}
		}
		if(highestScore>0) //if highest score is zero classifier did not make decision
			return decisionValues[highestScoreIndex];
		else
			return -1.0f; //if classifier did not make decision return -1 class confusion Matrix will proceed this
	}
//	*******************************************
	private double testEvent(FArray array,int eventIndex,int decisionValIndex)
	{
		return rand.nextDouble();
	}
	//*******************************************************	  
    @Override
    public boolean add_RI(AttributesRI importances[])
	{	
        int idx=-1;
        int decisionAttrIndex=trainArray.getDecAttrIdx();
        while(idx==decisionAttrIndex)
            idx=rand.nextInt(trainArray.colsNumber());
        String name=trainArray.attributes[idx].name;

        ExperimentIndicators experimentIndicators=new ExperimentIndicators();
        experimentIndicators.eventsNumber=trainSetSize;
        experimentIndicators.predictionQuality=QualityMeasure.calcWAcc(predResult.confusionMatrix.getMatrix());
            
        importances[0].addImportances(name,experimentIndicators,new J48NodeIndicators(1));
        return true;
	}
	//*******************************************************
	@Override
    public boolean saveDefinition(String path,String name)  throws IOException
    {
		if(params.verbose) System.out.print("Saving classifier definition...");
		params.save(path,name);
		if(params.verbose) System.out.println(" Done!");
		return true;
	}
	//*******************************************************
	@Override
    public boolean loadDefinition(String path,String name) throws IOException
    {
		if(params.verbose) System.out.print("Loading classifier definition...");
		//load classifier parameters
		params.load(path,name);
		//load classifier definition e.g. rules, tree structure etc.
		//this classifier has not such sophisticated definition
		if(params.verbose) System.out.println("Done!");
		return true;
	}
    //*******************************************************   
    @Override
    public boolean init()
    {
        return true;
    }
    //*****************************************
    @Override
    public boolean finish() {
        return true;
    }
	//*******************************************************
	@Override
    public String toString()
	{	
	    StringBuffer tmp=new StringBuffer();
	    tmp.append(" ### RND Classifier ### ").append('\n');
	    tmp.append("label="+ label).append('\n');
	    return tmp.toString();		
	}	
	//*******************************************************
}
