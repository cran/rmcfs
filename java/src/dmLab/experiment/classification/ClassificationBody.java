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
package dmLab.experiment.classification;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import dmLab.classifier.PredictionResult;
import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;
import dmLab.utils.cmatrix.ConfusionMatrix;



public class ClassificationBody
{
	public ClassificationParams classParams;
	public Classifier classifier;

	protected File2Array file2Container;
	protected Array2File array2file;
	
	public float learningTime;
	public float testingTime;
	public float experimentTime;

	public PredictionResult predResult;
	private double predQualityVector[];

	protected SelectFunctions selectFunctions;

	//*************************************************
	public ClassificationBody(Random random)
	{
		selectFunctions = new SelectFunctions(random);
		file2Container = new File2Array();
		array2file = new Array2File();
		cleanTimeStats();
	}
	//*************************************************
	private void cleanTimeStats()
	{
		learningTime = 0;
		testingTime = 0;
		experimentTime = 0;
	}
	//*************************************************
	public float run()
	{
		return run(null,null);
	}
	//*************************************************
	public Float run(FArray trainArray, FArray testArray)
	{ 
		if(classifier == null)
			if(!initClassifier())
				return null;    			

		//[train, test] arrays
		FArray[] arrays = new FArray[2];

		if(trainArray != null){
			arrays[0] = trainArray;
			arrays[1] = testArray;
		}else{ 
			arrays = loadArrays();
		}
		
		if(arrays == null)
			return null;

		if(classParams != null  && classifier.params.check(arrays[0])){
			if(classParams.validationType==ClassificationParams.VALIDATION_CV)
				runCV(arrays[0]);
			else
				runTrainTest(arrays[0], arrays[1]);
		}
		else{
			return null;
		}

		return (float)predResult.getPredQuality();
	}
	//*************************************************
	public FArray[] loadArrays()
	{
		FArray trainArray = new FArray();
		FArray testArray = null;
		if(classParams.verbose) 
			System.out.println("Loading input data...");
		if(!file2Container.load(trainArray,classParams.inputFilesPATH+classParams.inputFileName))
			return null;
		if(trainArray.checkDecisionValues()==false)
			return null;

		//System.out.println(trainArray.toString());

		if(classParams.verbose) 
			System.out.println("Input data loaded.");         

		if(classParams.validationType==ClassificationParams.VALIDATION_TEST_SET){
			if(classParams.verbose) 
				System.out.println("Loading testing data...");

			testArray = new FArray();
			testArray.dictionary = trainArray.dictionary.clone();
			testArray.setDecValues(trainArray.getDecValues());
			testArray.setDecAttrIdx(trainArray.getDecAttrIdx());
			file2Container.load(testArray,classParams.inputFilesPATH+classParams.testFileName);

			if(classParams.verbose) 
				System.out.println("Testing data loaded.");
		}
		
		FArray[] arrays = new FArray[2];
		arrays[0] = trainArray;
		arrays[1] = testArray;

		return arrays;
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
	public boolean initClassifier()
	{
		classifier = Classifier.getClassifier(classParams.model);
		if(!classifier.params.load(classParams.classifierCfgPATH, classifier.label))
			return false;
		classifier.init();
		classifier.setTempPath(classParams.resFilesPATH);       

		predResult = new PredictionResult(classifier.modelType); 

		if(classParams.verbose)
			System.out.println(classifier.params.toString());

		cleanTimeStats();
		return true;
	}
	//*************************************************
	protected Array[] split(FArray array, int[] splitMask)
	{
		if(splitMask==null){
			if(classParams.splitType==ClassificationParams.SPLIT_RANDOM)
				splitMask = selectFunctions.getSplitMaskRandom(array, classParams.splitRatio);
			else if(classParams.splitType==ClassificationParams.SPLIT_UNIFORM)
				splitMask = selectFunctions.getSplitMaskUniform(array, classParams.splitRatio);
			else{
				System.err.println("classParams.splitType does not equal to SPLIT_RANDOM or SPLIT_UNIFORM.");
				return null;
			}
		}
		Array[] trainTestArrays = SelectFunctions.split(array, splitMask);
		return trainTestArrays;
	}
	//**************************************
	protected boolean savePredictionArray(FArray array, String fileName)
	{
		FArray predictionArray = array.clone();
		String decValues[] = array.getDecValuesStr();
		int scoreIndex[] = new int[decValues.length];
		boolean saveScores = true;
		if(predResult.predictions[0].getScores() == null)
			saveScores = false;

		//add scores
		if(saveScores){
			for(int i=0;i<decValues.length;i++){
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

		for(int j=0;j<rows;j++){
			if(saveScores){
				for(int i=0;i<decValues.length;i++)
					predictionArray.writeValue(scoreIndex[i], j, (float)predResult.predictions[j].getScores()[i]);
			}
			predictionArray.writeValueStr(predictionIndex, j, predResult.predictions[j].getPredicted());
		}
		//Save array with prediction column
		array2file.setFormat(FileType.ADX);
		array2file.saveFile(predictionArray, fileName+"_pred");

		array2file.setFormat(FileType.CSV);
		array2file.saveFile(predictionArray, fileName+"_pred");

		return true;
	}
	//*************************************************
	public PredictionResult runCV(FArray trainArray)
	{
		if(classParams.verbose) 
			System.out.println("Running mult CV...");
		
		ConfusionMatrix cMatrix = null;
		if(trainArray.isTargetNominal()){
			cMatrix=new ConfusionMatrix(trainArray.getColNames(true)[trainArray.getDecAttrIdx()], trainArray.getDecValuesStr());
		}
		
		predQualityVector = new double[classParams.repetitions];

		for(int i=0; i<classParams.repetitions; i++){
			
			double start = System.currentTimeMillis();
			String label = "_rep"+ Integer.toString(i+1);
			PredictionResult singlePredResult = singleCV(trainArray, label);
			experimentTime+=(System.currentTimeMillis()-start)/1000.0;
			predQualityVector[i] = singlePredResult.getPredQuality();
			predResult.predictions = singlePredResult.predictions;
			
			if(cMatrix != null){
				ConfusionMatrix singleMatrix = singlePredResult.confusionMatrix; 
				cMatrix.add(singleMatrix);
			}
			
			String experimentLabel=classParams.label+Integer.toString(i+1);    
			if(classParams.savePredictionResult)
				savePredictionArray(trainArray,classParams.resFilesPATH+File.separator+experimentLabel);

			if(classParams.verbose){
				System.out.println("\n##### CV "+Integer.toString(i+1)+" RESULT #####");                
				System.out.println(singlePredResult.toString());				
			}           
			System.gc();
		}

		predResult.confusionMatrix = cMatrix;
		finalizeTimeStats();
		return predResult;
	}
	//*************************************************    
	protected PredictionResult singleCV(FArray array, String label)
	{
		ConfusionMatrix cMatrix = null;
		if(array.isTargetNominal()){
			cMatrix = new ConfusionMatrix(array.getColNames(true)[array.getDecAttrIdx()],array.getDecValuesStr());
		}

		int cvFolds = classParams.folds;
		int rows = array.rowsNumber();
		int cvTable[] = new int [rows];        
		int splitMask[] = new int[rows];//1-train 0-test        
		selectFunctions.arrayUtils.randomFill(cvTable,cvFolds);
		Prediction[] predictions = new Prediction[rows];

		for(int i=0;i<cvFolds;i++){
			for(int j=0;j<rows;j++){
				if(cvTable[j]==i)
					splitMask[j]=0;//testing set
				else
					splitMask[j]=1;
			}                       
			Array[] cvArrays = split(array, splitMask);			
			PredictionResult singlePredResult = singleTrainTest((FArray)cvArrays[0], (FArray)cvArrays[1]);
			learningTime += classifier.getLearningTime(); 
			testingTime += classifier.getTestingTime();
			if(cMatrix!=null)
				cMatrix.add(singlePredResult.getConfusionMatrix());

			Prediction singlePrediction[] = singlePredResult.getPredictions();
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
		
		PredictionResult myPredResult = new PredictionResult(classifier.modelType); 
		myPredResult.predictions = predictions;
		myPredResult.confusionMatrix = cMatrix;
		return myPredResult;
	}    
	//  *************************************************
	public PredictionResult runTrainTest(FArray trainArray, FArray testArray)
	{
		if(testArray == null){
			if(classParams.verbose) 
				System.out.println("MultTrainTest - split training set...");
		}else{
			if(classParams.verbose) 
				System.out.println("MultTrainTest - training set & testing set...");
		}
		
		ConfusionMatrix cMatrix = null;
		if(trainArray.isTargetNominal()){
			cMatrix=new ConfusionMatrix(trainArray.getColNames(true)[trainArray.getDecAttrIdx()],trainArray.getDecValuesStr());
		}
		predQualityVector=new double[classParams.repetitions];
				
		for(int i=0;i<classParams.repetitions;i++){
			Array[] ttArrays = null;
			if(testArray == null){
				ttArrays = split(trainArray, null);
			}else{
				ttArrays = new Array[2];
				ttArrays[0] = trainArray;
				ttArrays[1] = testArray;
			}
						
			double start = System.currentTimeMillis(); 
			PredictionResult singlePredResult = singleTrainTest((FArray)ttArrays[0], (FArray)ttArrays[1]);			
			experimentTime += (System.currentTimeMillis()-start)/1000.0;
			learningTime += classifier.getLearningTime(); 
			testingTime += classifier.getTestingTime();
			predQualityVector[i] = singlePredResult.getPredQuality();
			predResult.predictions = singlePredResult.predictions;

			if(cMatrix != null){
				ConfusionMatrix singleMatrix = singlePredResult.getConfusionMatrix();           
				cMatrix.add(singleMatrix);            
			}
			
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

			if(classParams.savePredictionResult)
				savePredictionArray(testArray, classParams.resFilesPATH+File.separator+experimentLabel);

			if(classParams.verbose){
				System.out.println("\n##### SPLIT "+Integer.toString(i+1)+" RESULT #####");                
				System.out.println(singlePredResult.toString());
			} 
		}

		predResult.confusionMatrix = cMatrix;
		finalizeTimeStats();
		return predResult;
	}
	//*************************************************
	protected PredictionResult singleTrainTest(FArray trainArray, FArray testArray)
	{        
		classifier.train(trainArray);
		//System.out.println(classifier.toString());        	
		classifier.test(testArray);
		PredictionResult myPredResult = classifier.getPredResult();		
		return myPredResult;
	}            
	//***************************************************
	//normalize results by number of repetitions
	protected void finalizeTimeStats()
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

		tmp.append("\n").append("#######  FINAL RESULT  #######").append("\n");
		tmp.append("repetitions: " + classParams.repetitions).append("\n");
		tmp.append(predResult.toString()).append("\n");
		
		int folds=1;
		if(classParams.validationType==ClassificationParams.VALIDATION_CV)
			folds=classParams.folds;

		tmp.append("Average Repetition time: "+ GeneralUtils.formatFloat(experimentTime*folds,2) + " s.").append("\n");            
		tmp.append("Average Experiment time: "+ GeneralUtils.formatFloat(experimentTime,2) + " s.").append("\n");
		tmp.append("Average Learning time: "+ GeneralUtils.formatFloat(learningTime,2) + " s.").append("\n");
		tmp.append("Average Testing time: "+ GeneralUtils.formatFloat(testingTime,2) + " s.").append("\n");

		if(predQualityVector!=null){
			tmp.append("\nwPredQuality: "+ Arrays.toString(predQualityVector)).append("\n");
			tmp.append("\nwPredQuality variance: "+ GeneralUtils.formatFloat(MathUtils.variance(predQualityVector),5)).append("\n");
		}

		return tmp.toString();
	}
	//*************************************************
}
