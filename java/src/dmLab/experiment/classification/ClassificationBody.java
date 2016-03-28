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

import java.io.IOException;
import java.util.Random;

import dmLab.array.Array;
import dmLab.array.FArray;
import dmLab.array.functions.ExtFunctions;
import dmLab.array.functions.SelectFunctions;
import dmLab.array.loader.File2Array;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.meta.Attribute;
import dmLab.array.saver.Array2File;
import dmLab.classifier.Classifier;
import dmLab.classifier.Prediction;
import dmLab.classifier.adx.ADXClassifier;
import dmLab.classifier.bayesNet.BayesNetClassifier;
import dmLab.classifier.ensemble.EnsembleClassifier;
import dmLab.classifier.hyperPipes.HyperPipesClassifier;
import dmLab.classifier.j48.J48Classifier;
import dmLab.classifier.knn.KNNClassifier;
import dmLab.classifier.logistic.LogisticClassifier;
import dmLab.classifier.nb.NBClassifier;
import dmLab.classifier.randomForest.RandomForestClassifier;
import dmLab.classifier.ripper.RipperClassifier;
import dmLab.classifier.rnd.RNDClassifier;
import dmLab.classifier.sliq.SliqClassifier;
import dmLab.classifier.svm.SVMClassifier;
import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;
import dmLab.utils.cmatrix.AccuracyMeasure;
import dmLab.utils.cmatrix.ConfusionMatrix;



public class ClassificationBody
{
    public ClassificationParams classParams;
    public Classifier classifier;
    public ConfusionMatrix resultConfMatrix;
    
    public FArray inArray;
    protected FArray trainArray;
    protected FArray testArray;
    
    protected File2Array file2Container;
    protected Array2File array2file;
    
    public float learningTime;
    public float testingTime;
    public float experimentTime;
    public double accArray[];
    public double wAccArray[];    
    public Prediction[] predictions;
    
    protected SelectFunctions selectFunctions;
    
    //*************************************************
    public ClassificationBody(Random random)
    {
    	selectFunctions = new SelectFunctions(random);
        file2Container=new File2Array();
        array2file=new Array2File();
        cleanStats();
    }
    //*************************************************
    public void cleanStats()
    {
        learningTime=0;
        testingTime=0;
        experimentTime=0;
    }
    //*************************************************
    public float run()    
    {
    	return run(null);
    }
    //*************************************************
    public Float run(FArray array)    
    { 
    	if(classifier == null)
    		if(!createClassifier())
        		return null;    			

    	if(array != null)
    		inArray = array;
    	else if(!loadArrays())
    		return null;
    	    		
    	if(classParams != null  && classifier.params.check(inArray)){
    		if(classParams.validationType==ClassificationParams.VALIDATION_SPLIT
    				|| classParams.validationType==ClassificationParams.VALIDATION_TEST_SET)
    			multipleTrainTest(inArray);
    		else if(classParams.validationType==ClassificationParams.VALIDATION_CV)
    			multipleCV(inArray);
    	}
    	else{
    		return null;
    	}

    	return resultConfMatrix.calcMeasure(AccuracyMeasure.WACC);
    }
    //*************************************************
    public boolean setParameters(ClassificationParams classParams){
    	this.classParams = classParams;
        return classParams.check(null);
    }
    //*************************************************
    public boolean loadParameters(String paramsFileName)
    {
        classParams = new ClassificationParams();
        if(classParams.load("", paramsFileName) == false){
            System.err.println("Error loading configuration file. File: " + paramsFileName);
            return false;
        }        
        
        if(classParams.verbose){
            System.out.println(classParams.toString());
        }
        return classParams.check(null);
    }
    //*************************************************
    public boolean createClassifier()
    {
    	
        if(classParams.classifier==Classifier.ENSEMBLE){
            classifier=new EnsembleClassifier();
        }
        else if(classParams.classifier==Classifier.RND){
            classifier=new RNDClassifier();
        }
        else if(classParams.classifier==Classifier.J48){
            classifier=new J48Classifier();
        }
        else if(classParams.classifier==Classifier.ADX){
            classifier=new ADXClassifier();
        }
        else if(classParams.classifier==Classifier.SLIQ){
            classifier=new SliqClassifier();
        }
        else if(classParams.classifier==Classifier.RF){
                classifier=new RandomForestClassifier();
        }
        else if(classParams.classifier==Classifier.NB){
            classifier=new NBClassifier();
        }
        else if(classParams.classifier==Classifier.KNN){
            classifier=new KNNClassifier();
        }
        else if(classParams.classifier==Classifier.RIPPER){
            classifier=new RipperClassifier();
        }
        else if(classParams.classifier==Classifier.SVM){
            classifier=new SVMClassifier();
        }
        else if(classParams.classifier==Classifier.BNET){
            classifier=new BayesNetClassifier();
        }
        else if(classParams.classifier==Classifier.HP){
            classifier=new HyperPipesClassifier();
        }
        else if(classParams.classifier==Classifier.LOGISTIC){
            classifier=new LogisticClassifier();
        }
        else{
            System.err.println("Error creating the classifier.");
            return false;
        }        
        if(!classifier.params.load(classParams.classifierCfgPATH, classifier.label))
            return false;
        classifier.init();
        
        classifier.setTempPath(classParams.resFilesPATH);       
          
        if(classParams.verbose)
        	System.out.println(classifier.params.toString());
        
        cleanStats();    
        return true;
    }
    //*************************************************
    public boolean setInputArray(FArray inputArray){
    	inArray = inputArray;
        if(inArray.checkDecisionValues()==false)
            return false;

    	return true;
    }
    //*************************************************    
    public boolean loadArrays()
    {
    	if(inArray!=null)
    		return true;
    	
        inArray=new FArray();
        if(classParams.verbose) System.out.println("Loading Input Table...");
        if(!file2Container.load(inArray,classParams.inputFilesPATH+classParams.inputFileName))
            return false;
        if(inArray.checkDecisionValues()==false)
            return false;
        
        if(classParams.debug){
            System.out.println(" ### DEBUG ### ");
            System.out.println(inArray.toString());
        }
        
        if(classParams.verbose) 
        	System.out.println("Input table has been loaded.");         
                
        if(classParams.validationType==ClassificationParams.VALIDATION_TEST_SET){
            trainArray=inArray;
            if(classParams.verbose) 
            	System.out.println("Loading Testing Table...");
            
            testArray = new FArray();
            testArray.dictionary = trainArray.dictionary.clone();
            testArray.setDecValues(trainArray.getDecValues());
            testArray.setDecAttrIdx(trainArray.getDecAttrIdx());
            file2Container.load(testArray,classParams.inputFilesPATH+classParams.testFileName);
            
            if(classParams.verbose) 
            	System.out.println("Testing table has been loaded.");
        }        
        return true;
    }
    //*************************************************
    private boolean split(FArray inputArray, int[] splitMask)
    {        
        if(classParams.verbose) 
        	System.out.println("Splitting Input Table...");
                        
        if(splitMask==null){
	        if(classParams.splitType==ClassificationParams.SPLIT_RANDOM)
	            splitMask = selectFunctions.getSplitMaskRandom(inputArray, classParams.splitRatio);
	        else if(classParams.splitType==ClassificationParams.SPLIT_UNIFORM)
	            splitMask = selectFunctions.getSplitMaskUniform(inputArray, classParams.splitRatio);
	        else{
	        	System.err.println("classParams.splitType does not equal to SPLIT_RANDOM or SPLIT_UNIFORM.");
	        	return false;
	        }
        }
		Array[] trainTestArrays = SelectFunctions.split(inputArray, splitMask);		
		trainArray = (FArray)trainTestArrays[0];
		testArray = (FArray)trainTestArrays[1];
		
        if(classParams.verbose) 
        	System.out.println("Input table has been splitted.");
        
        return true;
    }
    //**************************************
    private boolean savePredictionArray(FArray array, String fileName)
    {
        FArray predictionArray = array.clone();
        String decValues[] = array.getDecValuesStr();
        int scoreIndex[]=new int[decValues.length];
        boolean saveScores = predictions[0].hasScores();

        //add scores
        if(saveScores){
            for(int i=0;i<decValues.length;i++)
            {
                String scoreAttrName="score_"+decValues[i];
                ExtFunctions.addAttribute(predictionArray, scoreAttrName);                
                scoreIndex[i]=predictionArray.getColIndex(scoreAttrName);
                predictionArray.attributes[scoreIndex[i]].type=Attribute.NUMERIC;
            }
        }
        //add prediction
        ExtFunctions.addAttribute(predictionArray,"prediction");
        int predictionIndex=predictionArray.getColIndex("prediction");
        final int rows = predictionArray.rowsNumber();
        
        for(int j=0;j<rows;j++)
        {
            if(saveScores){
                for(int i=0;i<decValues.length;i++)
                    predictionArray.writeValue(scoreIndex[i], j, (float)predictions[j].getScore(i));
            }
            predictionArray.writeValueStr(predictionIndex, j, predictions[j].getLabel());
        }
        //Save array with prediction column
        array2file.setFormat(FileType.ADX);
        array2file.saveFile(predictionArray, fileName+"_pred");
        
        array2file.setFormat(FileType.CSV);
        array2file.saveFile(predictionArray, fileName+"_pred");
        
        return true;
    }
    //*************************************************
    public ConfusionMatrix multipleCV(FArray inputArray)
    {
        if(classParams.verbose) 
        	System.out.println("Running multCV...");
        
        ConfusionMatrix cMatrix=new ConfusionMatrix(inputArray.getColNames(true)[inputArray.getDecAttrIdx()],
        		inputArray.getDecValuesStr());
        FArray inputArrayOriginal = inputArray.clone();
        
        int repetitions=classParams.repetitions;
        accArray=new double[repetitions];
        wAccArray=new double[repetitions];

        for(int i=0;i<classParams.repetitions;i++)
        {                       
            double start = System.currentTimeMillis();
            String label = "_rep"+ Integer.toString(i+1);
            ConfusionMatrix singleMatrix = singleCV(inputArray, label);
            experimentTime+=(System.currentTimeMillis()-start)/1000.0;
            cMatrix.add(singleMatrix);
            
            accArray[i]=singleMatrix.calcMeasure(AccuracyMeasure.ACC);
            wAccArray[i]=singleMatrix.calcMeasure(AccuracyMeasure.WACC);
            
            String experimentLabel=classParams.label+Integer.toString(i+1);    
            if(classParams.savePredictionResult)
                savePredictionArray(inputArrayOriginal,classParams.resFilesPATH+"//"+experimentLabel);
                        
            if(classParams.verbose){
                System.out.println();
                System.out.println("##### CV "+Integer.toString(i+1)+" RESULT #####");                
                System.out.println(singleMatrix.toString());
                System.out.println(singleMatrix.statsToString(4));
            }           
            System.gc();
        }
        
        resultConfMatrix = cMatrix;
        normalizeResults();
        return cMatrix;
    }
    //*************************************************    
    public ConfusionMatrix singleCV(FArray inputArray, String label)
    {
        if(classParams.verbose) 
        	System.out.println("Running single CV...");

        ConfusionMatrix cMatrix = new ConfusionMatrix(inputArray.getColNames(true)[inputArray.getDecAttrIdx()],
        		inputArray.getDecValuesStr());
        
        int cvFolds = classParams.folds;
        int rows = inputArray.rowsNumber();
        int cvTable[] = new int [rows];        
        int splitMask[] = new int[rows];//1-train 0-test        
        selectFunctions.arrayUtils.randomFill(cvTable,cvFolds);
        predictions=new Prediction[rows];
        
        for(int i=0;i<cvFolds;i++){
            for(int j=0;j<rows;j++){
                if(cvTable[j]==i)
                    splitMask[j]=0;//testing set
                else
                    splitMask[j]=1;
            }                       
            split(inputArray, splitMask);            
            singleTrainTest(trainArray, testArray);
            learningTime+=classifier.getLearningTime(); 
            testingTime+=classifier.getTestingTime();
            cMatrix.add(classifier.getConfusionMatrix());
            
            Prediction singlePrediction[]=classifier.getPredictions();
            int k=0;
            //setting global prediction from the single one
            for(int j=0;j<rows;j++)
                if(cvTable[j]==i)
                    predictions[j]=singlePrediction[k++];

            //saving classifier
            String experimentLabel=classParams.label+label+"_fold"+Integer.toString(i+1);            
            if(classParams.saveClassifier){
                try {
                    classifier.saveDefinition(classParams.resFilesPATH,experimentLabel);
                } catch (IOException e){
                    System.err.println("Error saving classifier.");
                    e.printStackTrace();
                }
            }            
        }

        resultConfMatrix = cMatrix;
        return cMatrix;
    }    
//  *************************************************
    public ConfusionMatrix multipleTrainTest(FArray inputArray)
    {
        int repetitions=0;
        if(classParams.validationType==ClassificationParams.VALIDATION_TEST_SET){
            if(classParams.verbose) 
            	System.out.println("MultTrainTest based on testing set...");
            repetitions=1;            
        }else if(classParams.validationType==ClassificationParams.VALIDATION_SPLIT){
            if(classParams.verbose) 
            	System.out.println("MultTrainTest based on splitting of input set...");
            repetitions=classParams.repetitions;
        }
        
        accArray=new double[repetitions];
        wAccArray=new double[repetitions];
        
        ConfusionMatrix cMatrix=new ConfusionMatrix(inputArray.getColNames(true)[inputArray.getDecAttrIdx()],
        		inputArray.getDecValuesStr());
        
        for(int i=0;i<repetitions;i++){
            if(classParams.validationType==ClassificationParams.VALIDATION_SPLIT)
                split(inputArray,null);
            
            if(classParams.debug){
                System.out.println(" ### DEBUG ### ");
                System.out.println(trainArray.toString());
                System.out.println(testArray.toString());
            }
            
            double start = System.currentTimeMillis(); 
            singleTrainTest(trainArray,testArray);
            experimentTime += (System.currentTimeMillis()-start)/1000.0;
            
            learningTime += classifier.getLearningTime(); 
            testingTime += classifier.getTestingTime();
            ConfusionMatrix singleMatrix = classifier.getConfusionMatrix();           
            cMatrix.add(singleMatrix);            
            accArray[i] = singleMatrix.calcMeasure(AccuracyMeasure.ACC);
            wAccArray[i] = singleMatrix.calcMeasure(AccuracyMeasure.WACC);

            //different name of experiment for each iteration
            String experimentLabel=classParams.label+Integer.toString(i+1);            
            if(classParams.saveClassifier){
                try {
                    classifier.saveDefinition(classParams.resFilesPATH,experimentLabel);
                }catch (IOException e){
                    System.err.println("Error saving classifier.");
                    e.printStackTrace();
                }
            }
            
            predictions=classifier.getPredictions();
            if(classParams.savePredictionResult)
                savePredictionArray(testArray, classParams.resFilesPATH+"//"+experimentLabel);
            
            if(classParams.verbose){
                System.out.println("\n##### SPLIT "+Integer.toString(i+1)+" RESULT #####");                
                System.out.println(singleMatrix.toString());
                System.out.println(singleMatrix.statsToString(4));
            } 
        }
        
        resultConfMatrix = cMatrix;
        normalizeResults();
        return cMatrix;
    }
    //*************************************************
    public ConfusionMatrix singleTrainTest(FArray trainArray, FArray testArray)
    {        
        classifier.train(trainArray);
        if(classParams.debug){
        	System.out.println("### DEBUG ### ");
        	System.out.println(classifier.toString());        	
        }        
        classifier.test(testArray);
        ConfusionMatrix cMatrix = classifier.getConfusionMatrix(); 
        resultConfMatrix = cMatrix;
        return cMatrix;
    }            
//***************************************************
    //normalize results obtained from multCV and multTrainTest by number of repetitions
    private void normalizeResults()
    {
    	float repetitions = classParams.repetitions; 
        if(classParams.validationType == ClassificationParams.VALIDATION_CV)
        	repetitions *= classParams.folds;
        
        experimentTime = experimentTime/repetitions;
        learningTime = learningTime/repetitions;
        testingTime = testingTime/repetitions;
    }    
    //*************************************************    
    public String toStringResults()
    {
        StringBuffer tmp = new StringBuffer();
        
        if(resultConfMatrix!=null)
        {
            tmp.append("\n").append("#######  FINAL RESULT  #######").append("\n");
            tmp.append(""+classParams.repetitions+" repetition");
            if(classParams.repetitions>1)
            	tmp.append("s has");
            else
            	tmp.append(" have");
            
            tmp.append(" been processed.").append("\n");

            tmp.append(toStringCMatrix()).append("\n");
            
            int folds=1;
            if(classParams.validationType==ClassificationParams.VALIDATION_CV)
                folds=classParams.folds;
            
            tmp.append("Average Repetition time: "+ GeneralUtils.format(experimentTime*folds,2) + " s.").append("\n");            
            tmp.append("Average Experiment time: "+ GeneralUtils.format(experimentTime,2) + " s.").append("\n");
            tmp.append("Average Learning time: "+ GeneralUtils.format(learningTime,2) + " s.").append("\n");
            tmp.append("Average Testing time: "+ GeneralUtils.format(testingTime,2) + " s.").append("\n");
            if(accArray!=null && wAccArray!=null)
            {
                tmp.append("\n");
                tmp.append("Accuracy variance: "+ GeneralUtils.format(MathUtils.variance(accArray),5) + " s.").append("\n");
                tmp.append("wAccuracy variance: "+ GeneralUtils.format(MathUtils.variance(wAccArray),5) + " s.").append("\n");
            }
        }
        return tmp.toString();
    }
    //*************************************************    
    public String toStringCMatrix()
    {
        if(resultConfMatrix==null){
        	return "";
        }else{
            StringBuffer tmp = new StringBuffer();
            tmp.append(resultConfMatrix.toString(true, true, false, "\t")).append("\n");
            tmp.append(resultConfMatrix.statsToString(4)).append("\n");
            return tmp.toString();
        }        
    }
    //*************************************************
}
