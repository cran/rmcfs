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
package dmLab.experiment.classification;



import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Classifier;
import dmLab.experiment.ExperimentParams;

public class ClassificationParams extends ExperimentParams
{   
    public int model;
    public int repetitions;
    public double splitRatio;
    public int splitType;
    public int validationType;
    public int folds;
    
    public boolean saveClassifier;
    public boolean savePredictionResult; 
    
    public static final int SPLIT_RANDOM=1;
    public static final int SPLIT_UNIFORM=2;
    
    public static final int VALIDATION_SPLIT=1;
    public static final int VALIDATION_CV=2;
    public static final int VALIDATION_TEST_SET=3;
    
//  *************************************
//  **** sets all default values
    public ClassificationParams()
    {
        super();
        super.setDefault();
        setDefault();
    }
//  *****************************************
    @Override
    public boolean setDefault()
    {    	
        savePredictionResult = false;
        model = Classifier.label2int("j48");
        saveClassifier = true;
        validationType = VALIDATION_CV;
        folds = 3;
        repetitions = 1;
        splitRatio = 0.66;
        splitType = SPLIT_UNIFORM;
        
	    return true;
    }
//  *************************************
    public void set(ClassificationParams p)
    {    	
    	super.set(p);
    	
    	label = p.label;
        inputFilesPATH = p.inputFilesPATH;
        resFilesPATH = p.resFilesPATH;
        verbose = p.verbose;
        inputFileName = p.inputFileName;
        testFileName = p.testFileName;
        outputFileName = p.outputFileName;
        
        savePredictionResult = p.savePredictionResult;
        model = p.model;
        saveClassifier = p.saveClassifier;
        validationType = p.validationType;
        folds = p.folds;
        repetitions = p.repetitions;
        splitRatio = p.splitRatio;
        splitType = p.splitType;
    }    
//  *************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(super.toString()).append('\n');
        
        tmp.append("### Classification Parameters ### ").append('\n');                 
        tmp.append("model="+ Classifier.int2label(model)).append('\n');
        tmp.append("saveClassifier="+ saveClassifier).append('\n');
        tmp.append("savePredictionResult="+ savePredictionResult).append('\n');
        
        if(validationType==VALIDATION_SPLIT)
            tmp.append("validationType=split").append('\n');  
        else if(validationType==VALIDATION_CV)
            tmp.append("validationType=cv").append('\n');
        else if(validationType==VALIDATION_TEST_SET)
            tmp.append("validationType=testSet").append('\n');
        
        tmp.append("repetitions="+ repetitions).append('\n');  
        tmp.append("splitRatio="+ splitRatio).append('\n');  
        if(splitType==SPLIT_RANDOM)
            tmp.append("splitType=random").append('\n');  
        if(splitType==SPLIT_UNIFORM)
            tmp.append("splitType=uniform").append('\n');
        tmp.append("folds="+ folds).append('\n');        
        return tmp.toString();
    }
//  *************************************
    @Override
    protected boolean update(Properties properties)
    {
        if(!super.update(properties))
        	return false; 
        	
        model=Classifier.label2int(properties.getProperty("model", "j48"));
        repetitions=Integer.valueOf(properties.getProperty("repetitions", "1")).intValue();
        splitRatio=Double.valueOf(properties.getProperty("splitRatio", "0.66")).doubleValue();
        
        if(properties.getProperty("splitType", "uniform").equalsIgnoreCase("random"))
            splitType=SPLIT_RANDOM;
        else if(properties.getProperty("splitType", "uniform").equalsIgnoreCase("uniform"))
            splitType=SPLIT_UNIFORM;
        
        if(properties.getProperty("validationType", "cv").equalsIgnoreCase("split"))
            validationType=VALIDATION_SPLIT;
        else if(properties.getProperty("validationType", "cv").equalsIgnoreCase("cv"))
            validationType=VALIDATION_CV;
        else if(properties.getProperty("validationType", "cv").equalsIgnoreCase("testSet"))
            validationType=VALIDATION_TEST_SET;
            
        folds=Integer.valueOf(properties.getProperty("folds", "1")).intValue();
        saveClassifier=Boolean.valueOf(properties.getProperty("saveClassifier", "true")).booleanValue();
        savePredictionResult=Boolean.valueOf(properties.getProperty("savePredictionResult", "false")).booleanValue();
        
        return true;
    }
//  *****************************************
    @Override
    public boolean check(FArray array)
    {
        if(!super.check(array))
        	return false;
        	    	
        if(splitRatio<=0 || splitRatio>1){
            System.err.println("Incorrect 'splitRatio' parameter");
            return false;
        }
        if(repetitions<=0 ){
            System.err.println("Incorrect 'repetitions' parameter");
            return false;
        }
        if(folds<=0 ){
            System.err.println("Incorrect 'folds' parameter");
            return false;
        }
        if(model<0){
            System.err.println("Incorrect 'classifier' parameter.");
            return false;
        }
        if(testFileName.length()==0 && validationType==VALIDATION_TEST_SET){
            System.err.println("Parameter 'testFileName' is not defined.");
            return false;            
        }
        Classifier c = Classifier.getClassifier(model);
        if(array != null){
	        if(!array.isTargetNominal() && c.isClassifier()){
	            System.err.println("Target in 'NUMERIC' and model is 'Classifier'.");
	            return false;            
	        }
	        if(array.isTargetNominal() && !c.isClassifier()){
	            System.err.println("Target in 'NOMINAL' and model is 'Predictor'.");
	            return false;            
	        }
        }	
        return true;
    }
//  *****************************************
    
}
