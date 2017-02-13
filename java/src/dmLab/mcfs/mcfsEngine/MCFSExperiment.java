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
package dmLab.mcfs.mcfsEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.zip.ZipOutputStream;

import dmLab.array.FArray;
import dmLab.array.functions.SelectFunctions;
import dmLab.classifier.Classifier;
import dmLab.experiment.classification.ClassificationBody;
import dmLab.experiment.classification.ClassificationParams;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.mcfs.mcfsEngine.framework.MCFSClassic;
import dmLab.mcfs.mcfsEngine.framework.MCFSFramework;
import dmLab.mcfs.mcfsEngine.framework.MCFSPermutation;
import dmLab.utils.ArrayUtils;
import dmLab.utils.FileUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;
import dmLab.utils.dataframe.Column;
import dmLab.utils.dataframe.DataFrame;
import dmLab.utils.statFunctions.StatFunctions;

public class MCFSExperiment implements Runnable 
{        
	public MCFSFramework mcfs;
	public MCFSParams myParams;
	protected Random random;

	public final static String PERM_PREFIX = "perm_";  
	//************************************
	public MCFSExperiment(MCFSParams mcfsParams)
	{        
		random = new Random(mcfsParams.seed);
		this.myParams = mcfsParams;
	}
	//************************************    	
	@Override
	public void run() 
	{			
		if(myParams.mode == 2){
			MCFSParams tmpParams = myParams.clone();
			tmpParams.buildID = false;
			tmpParams.finalRuleset = false;
			tmpParams.finalCV = false;
			tmpParams.saveResutFiles = false;
			tmpParams.cutoffMethod = "contrast";
			System.out.println("****************************************************");
			System.out.println("*** Running stage I - initial MCFS-ID filtering  ***");
			FArray tmpArray = start(tmpParams);
			
			tmpParams = myParams.clone();
			String contrastFileName = FileUtils.dropFileExtension(new File(tmpParams.inputFileName));
			FileUtils.saveString(MCFSParams.TMP_PATH + contrastFileName + ".adx", tmpArray.toString());			
			tmpParams.inputFilesPATH = MCFSParams.TMP_PATH;
			tmpParams.inputFileName = contrastFileName + ".adx";
			System.out.println("***************************************************");
			System.out.println("*** Running stage II - final MCFS-ID filtering  ***");
			start(tmpParams);
			FileUtils.deleteFile(MCFSParams.TMP_PATH + contrastFileName + ".adx");
		}else
			start(myParams);
	}
	//************************************
	public FArray start(MCFSParams mcfsParams) 
	{	
        long start = System.currentTimeMillis();

		if(mcfsParams.check(null)==false)
			return null;

		DataFrame permutationRIValues = null;
		Ranking topRanking = null;
		ArrayList<Float> maxPermutationRI = new ArrayList<Float>();
		ArrayList<Float> maxPermutationID = new ArrayList<Float>();
		ArrayList<String> permPrefix = new ArrayList<String>(); 

		if(mcfsParams.cutoffMethod.equalsIgnoreCase("permutations")){
			for(int i=0;i<mcfsParams.cutoffPermutations;i++){
				System.out.println("***************************************************");
				System.out.println("*** MCFS-ID Cutoff Permutation Experiment #"+(i+1)+"/"+mcfsParams.cutoffPermutations+" ***");
				System.out.println("***************************************************");
				mcfs = new MCFSPermutation(random);
				mcfs.chartTitle = "MCFS-ID Progress " + "- Permutation Experiment #"+(i+1);
				((MCFSPermutation)mcfs).permPrefix = ((MCFSPermutation)mcfs).permPrefix+(i+1)+"_";
				permPrefix.add(((MCFSPermutation)mcfs).permPrefix);
				mcfsParams.saveResutFiles = false;
				if(!mcfs.run(mcfsParams))
					return null;
				AttributesRI imp = mcfs.globalStats.getAttrImportances()[0];
				if(permutationRIValues==null)
					permutationRIValues = createPermutationResult(mcfs.mcfsArrays.sourceArray, mcfsParams);
				permutationRIValues.setColumn(i+1, imp.getImportanceValues(imp.mainMeasureIdx));
				float[] minMax = imp.getMinMaxImportances(imp.mainMeasureIdx);
				maxPermutationRI.add(minMax[1]);
				if(mcfs.globalStats.getAttrConnections()!=null)
					maxPermutationID.add(mcfs.globalStats.getAttrConnections().getMaxID());
			}
		}

		String experimentName = mcfsParams.getExperimentName();

		//run classic MCFS procedure
		System.out.println("**************************");
		System.out.println("*** MCFS-ID Experiment ***");
		System.out.println("**************************");
		mcfs = new MCFSClassic(random);     
		mcfs.chartTitle = "MCFS-ID Progress - Raw Data";
		mcfsParams.saveResutFiles = true;
		if(!mcfs.run(mcfsParams))
			return null;
		AttributesRI importancesClassic = mcfs.globalStats.getAttrImportances()[0];

		//finish cutoff calculation
		if(permutationRIValues!=null){
			//calculate p values and add them to the result
			int mainMeasureIndex = importancesClassic.mainMeasureIdx;
			DataFrame p_values = calc_pValues(permutationRIValues, importancesClassic.getImportanceValues(mainMeasureIndex),0.05f);
			permutationRIValues.cbind(p_values);
			FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_PERMUTATIONS, permutationRIValues.toString());

			//get cutoff RI
			System.out.println("*** Calculation of cutoff RI ***");
			float[] minMaxRI = importancesClassic.getMinMaxImportances(mainMeasureIndex);
			System.out.println("Max RI (raw data) = "+minMaxRI[1]);
			//results of maxRI for all permutations
			System.out.println("Max RI (after permutations) = "+Arrays.toString(maxPermutationRI.toArray()));
			double cutoffRI = getCutoff(mcfsParams.cutoffAlpha, ArrayUtils.Float2double(maxPermutationRI.toArray()));
			topRanking = importancesClassic.getTopRanking(mainMeasureIndex, (float)cutoffRI);
			int topRankingSize = 0;
			if(topRanking!=null)
				topRankingSize = topRanking.size();
			System.out.println("Minimal important (based on permutations) RI = " + GeneralUtils.formatFloat(cutoffRI,7));
			System.out.println("Top important (based on permutations) attributes number = "+topRankingSize);

			double cutoffID = Double.NaN;            
			if(!maxPermutationID.isEmpty()){
				//get cutoff ID
				System.out.println("*** Calculation of cutoff ID ***");
				cutoffID = getCutoff(mcfsParams.cutoffAlpha, ArrayUtils.Float2double(maxPermutationID.toArray()));
				System.out.println("Minimal important (based on permutations) ID = " + GeneralUtils.formatFloat(cutoffID, 7));
			}

			//add new row to cutoffTable
			DataFrame cutoff = mcfs.globalStats.getCutoff().getCutoffTable();
			//remove mean
			cutoff = cutoff.excludeRows(new int[] {cutoff.rows()-1});
			DataFrame cutoffRow = new DataFrame(1, cutoff);    		
			int lastRowIdx = cutoffRow.rows()-1;
			cutoffRow.set(lastRowIdx, cutoffRow.getColIdx("method"), "permutations");						
			cutoffRow.set(lastRowIdx, cutoffRow.getColIdx("minRI"), cutoffRI);
			cutoffRow.set(lastRowIdx, cutoffRow.getColIdx("size"), (double)topRankingSize);
			cutoffRow.set(lastRowIdx, cutoffRow.getColIdx("minID"), cutoffID);
			cutoff.rbind(cutoffRow);    		
			mcfs.globalStats.getCutoff().setCutoffTable(cutoff);    		
			mcfs.globalStats.getCutoff().addMeanValue(importancesClassic);
			//save new extended cutoffTable to file
			FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_CUTOFF, mcfs.globalStats.getCutoff().toString());

			//remove temporary perm1, perm2 ... importance files
			removePermResultTempFiles(permPrefix, mcfsParams);

			//overwrite top ranking file based on specified topRankingMethod
			String topRankingMethod = mcfs.globalStats.getCutoff().getMethod(mcfsParams.cutoffMethod);
			topRankingSize = (int)mcfs.globalStats.getCutoff().getCutoffValue(topRankingMethod);            
			topRanking = importancesClassic.getTopRankingSize(mainMeasureIndex, topRankingSize);
			System.out.println("*** Final top important (based on "+topRankingMethod+") attributes = "+topRanking.size());            
			if(topRanking!=null){
				FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_TOPRANKING, topRanking.toString());
			}
		}
		//RUN FinalCV
		if(mcfsParams.finalCV){
			int topRankingSize = Math.max((int)mcfs.globalStats.getCutoff().getCutoffValue(mcfsParams.cutoffMethod),4);
			MCFSFinalCV simpleCV;
			if(mcfs.mcfsArrays.sourceArray.isTargetNominal())
				simpleCV = new MCFSFinalCV(new int[]{Classifier.J48,Classifier.NB,Classifier.SVM,Classifier.KNN,Classifier.LOGISTIC,Classifier.RIPPER},random);
			else
				simpleCV = new MCFSFinalCV(new int[]{Classifier.M5},random);

			//int[] cutoffValues = getCutoffValues(ArrayUtils.double2int(mcfs.globalStats.getCutoff().getCutoffValues()));
			int[] cutoffValues = getCutoffValues(new int[]{topRankingSize});
			System.out.println("");
			System.out.println("*** Running CV experiment on top " + Arrays.toString(cutoffValues) + " attributes ***");        	
			DataFrame res = simpleCV.run(mcfs.mcfsArrays.sourceArray, mcfs.globalStats.getAttrImportances()[0], cutoffValues, 
					mcfsParams.foldsCV, mcfsParams.finalCVSetSize, mcfsParams.finalCVRepetitions);
			if(mcfsParams.saveResutFiles)
				FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_CV_RESULT, res.toString());
		}
		// minimum 2 attributes
		int topRankingSize = Math.max((int)mcfs.globalStats.getCutoff().getCutoffValue(mcfsParams.cutoffMethod), 2) ;
		FArray topRankingArray = (FArray)SelectFunctions.selectColumns(mcfs.mcfsArrays.sourceArray, mcfs.globalStats.getAttrImportances()[0], topRankingSize);

		//RUN Final Rules
		if(mcfsParams.finalRuleset && mcfs.mcfsArrays.sourceArray.isTargetNominal()){
			System.out.println("");
			System.out.println("*** Building RIPPER ruleset on top "+ topRankingSize +" attributes ***");
			ClassificationBody classification = new ClassificationBody(random);
			classification.setParameters(new ClassificationParams());
			classification.classParams.verbose = false;
			classification.classParams.saveClassifier = false;
			classification.classParams.savePredictionResult = false;
			classification.classParams.repetitions = 1;
			classification.classParams.model = Classifier.RIPPER;			
			classification.initClassifier();
			classification.runTrainTest(topRankingArray,topRankingArray);
			String ripperResult = classification.classifier.toString(false) +"\n";			
			classification.initClassifier();
			classification.classParams.folds = 	mcfsParams.foldsCV;
			classification.classParams.repetitions = mcfsParams.finalCVRepetitions;
			classification.runCV(topRankingArray);
			ripperResult += "RIPPER CV Result (10 folds repeated "+ mcfsParams.finalCVRepetitions +" times)\n" + classification.predResult.toString();
			System.out.println(ripperResult);

			if(mcfsParams.saveResutFiles)
				FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_RULESET, ripperResult);			
		}

		if(mcfsParams.saveResutFiles){
			System.out.println("*** Saving filtered data ***");
			FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_DATA + ".adh", topRankingArray.toADH());
			FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_DATA + ".csv", topRankingArray.toCSV());
		}

		if(mcfsParams.saveResutFiles && mcfsParams.zipResult){
			String[] files = MCFSParams.getAllResultFileName(experimentName);
			try {
				FileOutputStream fos = new FileOutputStream(mcfsParams.resFilesPATH + experimentName + ".zip");
				ZipOutputStream zos = new ZipOutputStream(fos);

				for(int i = 0; i < files.length; i++){
					FileUtils.addFileToZip(mcfsParams.resFilesPATH + files[i], zos);
				}
				FileUtils.addFileToZip(mcfsParams.resFilesPATH + experimentName + ".run", zos);

				zos.close();
				fos.close();
				for(int i = 0; i < files.length; i++){
					FileUtils.deleteFile(mcfsParams.resFilesPATH + files[i]);
				}
				FileUtils.deleteFile(mcfsParams.resFilesPATH + experimentName + ".run");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
        long stop = System.currentTimeMillis();
        float experimentTime = (stop-start)/1000.0f;               
        System.out.println("*** Calculations for input data: '" + FileUtils.dropFileExtension(new File(mcfsParams.inputFileName)) + "' are finished! Processing time: " + GeneralUtils.timeIntervalFormat(experimentTime) + " ***");
        
		return topRankingArray;
	}
	//*********************************
	protected int removePermResultTempFiles(ArrayList<String> permPrefix, MCFSParams mcfsParams)
	{
		String experimentName = mcfsParams.getExperimentName();
		for(int i=0;i<permPrefix.size();i++){
			String fileName = mcfsParams.resFilesPATH + permPrefix.get(i)+experimentName+"__"+MCFSParams.FILESUFIX_RI;
			FileUtils.deleteFile(fileName);
		}
		return 0;
	}
	//*********************************
	protected DataFrame createPermutationResult(FArray inputArrray, MCFSParams mcfsParams)
	{
		//create data frame for rankings
		String[] colNames=new String[mcfsParams.cutoffPermutations+1];
		short[] colTypes=new short[colNames.length];	

		colNames[0] = "attribute";
		colTypes[0] = Column.TYPE_NOMINAL;
		for(int i=1;i<colNames.length;i++){
			colNames[i] = PERM_PREFIX+i;
			colTypes[i] = Column.TYPE_NUMERIC;
		}
		DataFrame permResult = new DataFrame(inputArrray.colsNumber()-1, colNames.length);
		permResult.setColNames(colNames);
		permResult.setColTypes(colTypes);        
		permResult.setColumn(0, inputArrray.getColNames(false));

		return permResult;
	}
	//*********************************
	private double getCutoff(double alpha, double values[])
	{
		double pValue = StatFunctions.andersonDarlingNormTest(values);
		System.out.println("Anderson-Darling normality test p-value = " + GeneralUtils.formatFloat(pValue,7));		
		double[] confidence = StatFunctions.getConfidenceInterval(alpha, values);
		System.out.println("Confidence Interval: "+ GeneralUtils.formatFloat(confidence[0],7)+" ; "+GeneralUtils.formatFloat(confidence[1],7));                
		return confidence[1];
	}
	//*********************************
	private int[] getCutoffValues(int[] cutoffValues){
		Arrays.sort(cutoffValues);

		HashSet<Integer> vset = new HashSet<Integer>();
		for(int i=0; i<cutoffValues.length;i++){
			vset.add(cutoffValues[i]);
		}
		//remove zeros from vset and copy result to cutoffValues 
		vset.removeAll(Arrays.asList(Integer.valueOf(0)));
		Integer[] tmpArray = new Integer[1]; 
		tmpArray = vset.toArray(tmpArray);
		cutoffValues = ArrayUtils.Integer2int(tmpArray);
		Arrays.sort(cutoffValues);

		vset.add(Math.round(cutoffValues[0] * 0.75f));
		vset.add(Math.round(cutoffValues[0] * 0.5f));
		vset.add(Math.round(cutoffValues[0] * 0.25f));
		//vset.add(Math.round(cutoffValues[0] * 0.1f));    	
		vset.add(Math.round(cutoffValues[cutoffValues.length-1] * 1.25f));
		vset.add(Math.round(cutoffValues[cutoffValues.length-1] * 1.5f));
		vset.add(Math.round(cutoffValues[cutoffValues.length-1] * 2f));
		//vset.add(Math.round(cutoffValues[cutoffValues.length-1] * 2.5f));

		tmpArray = vset.toArray(tmpArray);
		cutoffValues = ArrayUtils.Integer2int(tmpArray);
		Arrays.sort(cutoffValues);

		return cutoffValues;
	}
	//*********************************
	private DataFrame calc_pValues(DataFrame permutationRIValues, float[] valuesRI, float threshold)
	{
		String[] colNames = permutationRIValues.getColNames();
		boolean[] colNamesMask = new boolean[colNames.length];
		int permNumber = 0;
		for(int i=0;i<colNames.length;i++){
			if(colNames[i].startsWith(PERM_PREFIX)){
				colNamesMask[i]=true;
				permNumber++;
			}
			else
				colNamesMask[i]=false;
		}

		float[] mean = new float[permutationRIValues.rows()];
		float[] normality_p = new float[permutationRIValues.rows()];
		float[] ttest_p = new float[permutationRIValues.rows()];		

		for(int i=0;i<permutationRIValues.rows();i++){
			Object[] row = permutationRIValues.getRow(i);

			double[] permRI = new double[permNumber];
			int currPermIdx = 0;
			for(int j=0;j<row.length;j++){
				if(colNamesMask[j]){
					permRI[currPermIdx++]=(Float)row[j];
				}
			}
			mean[i] = (float)MathUtils.mean(permRI);
			normality_p[i] = (float)StatFunctions.andersonDarlingNormTest(permRI);
			ttest_p[i] = (float)StatFunctions.tTestOneSample(permRI, (double)valuesRI[i]);
		}

		DataFrame p_values = new DataFrame(permutationRIValues.rows(), 4);
		p_values.setColNames(new String[]{"mean","RI_norm", "normality_test_p", "t_test_p"});
		p_values.setColTypes(new short[]{Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC});
		p_values.setColumn(0, mean);
		p_values.setColumn(1, valuesRI);
		p_values.setColumn(2, normality_p);
		p_values.setColumn(3, ttest_p);    	
		return p_values;
	}
	//*********************************
}
