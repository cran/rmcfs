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
package dmLab.classifier.adx;

import java.util.HashSet;

import dmLab.array.FArray;
import dmLab.array.functions.DiscFunctions;
import dmLab.array.loader.File2Array;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.meta.Attribute;
import dmLab.array.meta.DiscRanges;
import dmLab.array.saver.Array2File;
import dmLab.classifier.Classifier;
import dmLab.classifier.Prediction;
import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.complex.ComplexSet;
import dmLab.classifier.adx.ruleFamily.RuleFamily;
import dmLab.classifier.adx.ruleParser.RuleFamilyParser;
import dmLab.classifier.adx.ruleSet.RuleSet;
import dmLab.classifier.adx.selector.Selector;
import dmLab.classifier.attributeIndicators.ADXSelectorIndicators;
import dmLab.discretizer.DiscretizerParams;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.ExperimentIndicators;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.cmatrix.QualityMeasure;

public class ADXClassifier extends Classifier
{   
    protected ADXParams cfg;
    protected FArray trainArray;
	protected RuleFamily ruleFamily;
	protected DiscretizerParams discParams;
	protected DiscRanges[] discRangesTrain;

	//*****************************************
	public ADXClassifier()
	{
		super();
		label=labels[ADX];
		model=ADX;
		params=new ADXParams();
		cfg = (ADXParams)params;
		
		discParams = new DiscretizerParams();
	}
	//*****************************************
	@Override
    public boolean train(FArray trainArray)
	{
    	if(!checkTargetAttr(trainArray))
    		return false;

		long start,stop;
		this.trainArray=trainArray;
		ruleFamily=new RuleFamily(trainArray.getDecValues().length,trainArray.colsNumber(),cfg);
		ruleFamily.verbose = cfg.verbose;
		start=System.currentTimeMillis();		
		if(!discTrainArray(trainArray))
			return false;
		else{
			//save ranges for later use
			storeDiscranges(trainArray.discRanges);
		}		
		
		trainArray.findADXDomains();		
		ruleFamily.createRules(trainArray);
		stop=System.currentTimeMillis();
		learningTime=(stop-start)/1000.0f;
        trainSetSize=trainArray.rowsNumber();
		return true;
	}
	//*****************************************
	private boolean discTrainArray(FArray inputArray){
		if(!inputArray.isDiscretized()){
			System.out.println("Warning! Input table contains numeric values. Discretization is processed...");
			discParams.verbose = cfg.verbose;
			DiscFunctions.findRanges(trainArray, discParams);
            DiscFunctions.applyRanges(trainArray);
            return true;
		}else{
			for(int i=0; i<inputArray.colsNumber(); i++){
				if(inputArray.attributes[i].type == Attribute.NUMERIC && inputArray.isDiscretized(i)==false){
					System.err.println("Error! Input table contains numeric values that are not discretized.");
					return false;
				}				
			}
            return true;
		}
	}
	//*****************************************
	protected boolean storeDiscranges(DiscRanges[] discRanges){
		//save ranges for later use
		discRangesTrain = discRanges.clone();
		for(int i=0; i<discRangesTrain.length; i++)
			if(trainArray.discRanges[i] != null)
				discRangesTrain = discRanges.clone();
		
		return true;
	}
	//*****************************************    
	@Override
    public boolean init()
	{        
		return true;
	}
	//*****************************************	
	@Override
    public boolean test(FArray testArray)
	{
    	if(!checkTargetAttr(testArray))
    		return false;

		if(!discTestArray(testArray))
			return false;
		
		long start,stop;
		start=System.currentTimeMillis();   		
		predResult.confusionMatrix = new ConfusionMatrix(testArray.getColNames(true)[testArray.getDecAttrIdx()],
				testArray.getDecValues(),testArray.getDecValuesStr());
		
		float predictedDecision;
		float realDecision;
		final int testEventsNumber=testArray.rowsNumber();
		final int interval=(int)Math.ceil(0.1*testEventsNumber);
		int threshold=interval;
		predResult.predictions=new Prediction[testEventsNumber];        
        
		final int decAttrIndex=testArray.getDecAttrIdx();
        
        ruleFamily.prepareClassification();
		for(int i=0;i<testEventsNumber;i++)
		{
            predictedDecision = ruleFamily.classifyEvent2(testArray,i);
			realDecision = testArray.readValue(decAttrIndex,i);
			predResult.confusionMatrix.add(realDecision,predictedDecision);
			
            String realClassName = testArray.dictionary.toString(realDecision);
            String predictedClassName = testArray.dictionary.toString(predictedDecision);            
            predResult.predictions[i]=new Prediction(realClassName,predictedClassName,ruleFamily.getLastScores());
            
			if(i>threshold && threshold!=0){
				threshold+=interval;
			}
		}
		stop=System.currentTimeMillis();
		testingTime=(stop-start)/1000.0f;
		return true;
	}
	//*****************************************    
	private boolean discTestArray(FArray inputArray){
		if(!inputArray.isDiscretized()){
			System.out.println("Warning! Input table contains numeric values. Discretization is applied...");		
			inputArray.discRanges = discRangesTrain;
           	DiscFunctions.applyRanges(inputArray);
            return true;
		}else{
			for(int i=0; i<inputArray.colsNumber(); i++){
				if(inputArray.attributes[i].type == Attribute.NUMERIC && inputArray.isDiscretized(i)==false){
					System.err.println("Error! Input table contains numeric values that are not discretized.");
					return false;
				}				
			}
            return true;
		}
	}
	//*****************************************
	public void setDiscParams(DiscretizerParams discretizerParams)
	{
		this.discParams = discretizerParams;
	}
	//*****************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();          
		tmp.append(" ### ADX Classifier ### ").append('\n');
		tmp.append("label="+ label).append('\n');
		tmp.append(ruleFamily.toString()).append('\n');
		return tmp.toString();
	}
	//*****************************************
	@Override
    public boolean saveDefinition(String path,String name)
	{
		//System.out.println("Saving classifier definition...");
		Array2File array2File = new Array2File();
        array2File.setFormat(FileType.ADX);
		array2File.saveFile(trainArray, path+"//"+name);
		DiscFunctions.saveRanges(trainArray,path+"//"+name);				
		params.save(path,name);		
		ruleFamily.saveSymbolicSelectors(path+"//"+name);
		ruleFamily.saveSelectors(path+"//"+name);		
		ruleFamily.saveRuleFamily(path+"//"+name);
		return false;
	}
	//*****************************************
	@Override
    public boolean loadDefinition(String path,String name)
	{
		//System.out.println("Loading classifier definition...");		
		//System.out.println("Loading training data...");
        File2Array file2Container=new File2Array();
		trainArray=new FArray();
		if(!file2Container.load(trainArray, path+"//"+name+".adx"))
			return false;
		
		try {
			discRangesTrain = DiscFunctions.loadRanges(trainArray, path+"//"+name+".dsc");			
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
		}
		
		//System.out.println("Loading classifier parameters...");
		if(!params.load(path,name))
			return false;
		
		//System.out.println("Loading rules...");
		ruleFamily = new RuleFamily(trainArray.getDecValues().length,trainArray.colsNumber(),cfg);
		ruleFamily.setTrainArray(trainArray);
		ruleFamily.verbose = cfg.verbose;        
		RuleFamilyParser ruleParser=new RuleFamilyParser(ruleFamily,trainArray);
		
		String filePrefix=path+"//"+name;
		int decisions=trainArray.getDecValues().length;
		for(int d=0;d<decisions;d++)
		{	        
			try{
				if(!ruleParser.loadRuleSet(filePrefix+"_"+trainArray.getDecValuesStr()[d]+".rls",d))
					return false;
			}
			catch(Exception e){
				System.err.print(e.getMessage());
				return false;
			}
		}
        ruleFamily.prepareClassification();
		return true;
	}
	//*****************************************
	@Override
    public float classifyEvent(FArray array, int eventIndex)
	{
		return ruleFamily.classifyEvent2(array,eventIndex);
	}
	//*****************************************       
    @Override
    public boolean add_RI(AttributesRI importances[])
    {
        attrSet=new HashSet<String>();
        
        ADXSelectorIndicators selectorIndicators=new ADXSelectorIndicators();
        ExperimentIndicators experimentIndicators=new ExperimentIndicators();
        experimentIndicators.eventsNumber=trainSetSize;
        experimentIndicators.predictionQuality=QualityMeasure.calcWAcc(predResult.confusionMatrix.getMatrix());
        
        final int rulesets=ruleFamily.ruleSets();
        for(int f=0; f<rulesets; f++) //over all rulesets           
        {   
            RuleSet ruleset=ruleFamily.getRuleSet(f);
            //each ruleset is for different class           
            for(int r=0;r<ruleset.complexSetArray.length;r++) //over all complexList
            {
                ComplexSet complexList = ruleset.complexSetArray[r];
                if(complexList!=null)
                {           
                    int complexesListSize=complexList.size();
                    
                    for(int c=0;c<complexesListSize;c++)//over all complexes
                    {                   
                        Complex complex=complexList.getComplex(c);
                        selectorIndicators.setIndicators(complex);
                        int complexSize=complex.size();
                        for(int s=0;s<complexSize;s++)//over all selectors
                        {
                            Selector selector=ruleFamily.selectorListArray[f].getSelector(complex.getSelectorId(s));
                            selectorIndicators.setIndicators(selector);
                            //System.out.println("Adding selector: "+selector.toString(trainArray));
                            //System.out.println(selectorIndicators.toString());
                            String attrName=trainArray.attributes[selector.attrIndex].name;
                            importances[0].addImportances(attrName,experimentIndicators,selectorIndicators);
                            importances[f+1].addImportances(attrName,experimentIndicators,selectorIndicators);
                            attrSet.add(attrName);
                        }//end for over selectors
                    }//end for over complexes
                }
            }//end for over all complexLists
        }//end for over all complexList rulesets
        
        for(int i=0;i<importances.length;i++)
            importances[i].flushMeasures();
        
        return true;
    }
	//*****************************************
    public float getFinalSelectionTime()
    {
        return ruleFamily.finalSelectionTime;
    }
    //*****************************************
    @Override
    public boolean finish() {
        return true;
    }
    //*****************************************
}
