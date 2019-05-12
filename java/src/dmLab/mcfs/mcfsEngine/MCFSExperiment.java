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
import dmLab.mcfs.mcfsEngine.arrays.MCFSArrays;
import dmLab.mcfs.mcfsEngine.framework.GlobalStats;
import dmLab.mcfs.mcfsEngine.framework.MCFSClassic;
import dmLab.mcfs.mcfsEngine.framework.MCFSFramework;
import dmLab.mcfs.mcfsEngine.framework.MCFSPermutation;
import dmLab.utils.ArrayUtils;
import dmLab.utils.FileUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;
import dmLab.utils.dataframe.ColumnMetaInfo;
import dmLab.utils.dataframe.DataFrame;
import dmLab.utils.helpers.MinMax;
import dmLab.utils.statFunctions.StatFunctions;

public class MCFSExperiment implements Runnable 
{        
	protected MCFSFramework mcfs;
	protected MCFSParams myParams;
	protected Random random;
	public int topRankingSize = 0;	
	//************************************
	public MCFSExperiment(MCFSParams mcfsParams)
	{        
		if(mcfsParams.verbose)
			System.out.println("MCFSExperiment Params: \n" + mcfsParams.toString());
		
		random = new Random(mcfsParams.seed);
		this.myParams = mcfsParams;
	}
	//************************************
	@Override
	public void run()
	{
		if(myParams.mode == 2){
			MCFSParams mcfsParamsP1 = myParams.clone();
			mcfsParamsP1.buildID = false;
			mcfsParamsP1.finalRuleset = false;
			mcfsParamsP1.finalCV = false;
			mcfsParamsP1.saveResultFiles = false;
			mcfsParamsP1.zipResult = false;
			mcfsParamsP1.minTopRankingSize = 0.05f;
			mcfsParamsP1.cutoffMethod = "contrast";
			mcfsParamsP1.filesufix_RI = MCFSParams.FILESUFIX_RI_PHASE_1;
			System.out.println("****************************************************");
			System.out.println("*** Running Phase I - Initial MCFS-ID filtering  ***");
			//save initial ranking
			FArray tmpArray = start(mcfsParamsP1);
			if(tmpArray == null)
				return;
			
			AttributesRI ri_phase1 = mcfs.globalStats.getAttrImportances()[0];
			// save ranking from phase 1
			File ri_phase1_file = new File(mcfsParamsP1.resFilesPATH + File.separator + mcfsParamsP1.getExperimentName() + "_" + ri_phase1.label + "_" + mcfsParamsP1.filesufix_RI);
			//System.out.println("MDR DEBUG: ri_phase1_file: " + ri_phase1_file);
			FileUtils.saveString(ri_phase1_file.getAbsolutePath(), ri_phase1.toString());

			// save data for phase 2
			File input_phase2_file = new File(mcfsParamsP1.tmpPATH + File.separator + FileUtils.dropFileExtension(new File(mcfsParamsP1.inputFileName)) + ".adx");
			//System.out.println("MDR DEBUG: input_phase2_file: " + input_phase2_file);
			FileUtils.saveString(input_phase2_file.getAbsolutePath(), tmpArray.toString());

			MCFSParams mcfsParamsP2 = myParams.clone();
			mcfsParamsP2.inputFilesPATH = input_phase2_file.getAbsoluteFile().getParent();
			mcfsParamsP2.inputFileName = input_phase2_file.getName();
			mcfsParamsP2.minTopRankingSize = 0f;
			mcfsParamsP2.filesufix_RI = MCFSParams.FILESUFIX_RI_PHASE_2;
			mcfsParamsP2.zipResult = false;
			System.out.println("***************************************************");
			System.out.println("*** Running Phase II - Final MCFS-ID filtering  ***");
			start(mcfsParamsP2);

			AttributesRI ri_phase2 = getGlobalStats().getAttrImportances()[0];			

			//combine two rankings into one
			DataFrame df_ri_phase1 = ri_phase1.toDataFrame(ri_phase1.getMeasuresNamesBasic());
			DataFrame df_ri_phase2 = ri_phase2.toDataFrame(ri_phase2.getMeasuresNamesBasic());
			DataFrame ri_final = combineRankings(df_ri_phase1, df_ri_phase2);
			
			//save ranking from phase 2 and the final ranking
			//System.out.println(ri_final.toString());			
			File ri_final_file = new File(mcfsParamsP2.resFilesPATH + File.separator + mcfsParamsP2.getExperimentName()+"_"+ri_phase2.label+"_"+MCFSParams.FILESUFIX_RI);
			//System.out.println("MDR DEBUG: ri_final_file: " + ri_final_file);			
			FileUtils.saveString(ri_final_file.getAbsolutePath(), ri_final.toString());
			if(myParams.zipResult)
				zipResult(mcfsParamsP2);
		}else {
			start(myParams);
		}
		
		//remove tmp dir at the end
		File tmpdir = new File(myParams.tmpPATH);
		if(tmpdir.exists()) 
			FileUtils.deleteDir(tmpdir);
	}
	//************************************
	public FArray start(MCFSParams mcfsParams) 
	{			
		//System.out.println("MDR DEBUG: MCFSExperiment.start() Params: \n" + mcfsParams.toString());
		if(mcfsParams.verbose)
			System.out.println("MCFSExperiment.start() Params: \n" + mcfsParams.toString());

		long start = System.currentTimeMillis();

		if(mcfsParams.check(null) == false)
			return null;

		DataFrame permutationRIValues = null;
		Ranking topRanking = null;
		ArrayList<Float> maxPermutationRI = new ArrayList<Float>();
		ArrayList<Float> maxPermutationID = new ArrayList<Float>();
		ArrayList<String> permFiles = new ArrayList<String>();

		if(mcfsParams.cutoffMethod.equalsIgnoreCase("permutations")){
			MCFSParams cutoffParams = mcfsParams.clone();
			for(int i=0;i<cutoffParams.cutoffPermutations;i++){
				System.out.println("***************************************************");
				System.out.println("*** MCFS-ID Cutoff Permutation Experiment #"+(i+1)+"/"+cutoffParams.cutoffPermutations+" ***");
				System.out.println("***************************************************");
				
				mcfs = new MCFSPermutation(random);
				mcfs.chartTitle = "MCFS-ID Progress " + "- Permutation Experiment #"+(i+1);
				mcfs.experimentName = "perm_" + (i+1) + "_" + cutoffParams.getExperimentName();				
				permFiles.add(new File(mcfsParams.resFilesPATH + File.separator + mcfs.experimentName + "__" + mcfsParams.filesufix_RI).getAbsolutePath());				
				cutoffParams.saveResultFiles = false;
				cutoffParams.zipResult = false;
				if(!mcfs.run(cutoffParams))
					return null;
				AttributesRI cutoff_RI = mcfs.globalStats.getAttrImportances()[0];
				if(permutationRIValues == null)
					permutationRIValues = createPermutationResult(mcfs.mcfsArrays.sourceArray, cutoffParams);
				permutationRIValues.setColumn(i+1, cutoff_RI.getImportanceValues(cutoff_RI.mainMeasureIdx));
				float[] minMax = cutoff_RI.getMinMaxImportances(cutoff_RI.mainMeasureIdx);
				maxPermutationRI.add(minMax[1]);
				if(mcfs.globalStats.getAttrConnections()!=null)
					maxPermutationID.add(mcfs.globalStats.getAttrConnections().getMaxID());
				System.out.println("");
			}
		}

		//run classic MCFS procedure
		System.out.println("**************************");
		System.out.println("*** MCFS-ID Experiment ***");
		System.out.println("**************************");
		
		mcfs = new MCFSClassic(random);     
		mcfs.chartTitle = "MCFS-ID Progress - Real Data";
		if(!mcfs.run(mcfsParams))
			return null;
		
		String experimentName = mcfs.experimentName;
		AttributesRI importancesClassic = mcfs.globalStats.getAttrImportances()[0];

		//finish cutoff calculation
		if(permutationRIValues != null){
			//calculate p values and add them to the result
			int mainMeasureIndex = importancesClassic.mainMeasureIdx;
			DataFrame p_values = calc_pValues(permutationRIValues, importancesClassic.getImportanceValues(mainMeasureIndex),0.05f);
			permutationRIValues.cbind(p_values);
			FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_PERMUTATIONS, permutationRIValues.toString());

			//get cutoff RI
			System.out.println("*** Calculation of cutoff RI (based on permutations) ***");
			float[] minMaxRI = importancesClassic.getMinMaxImportances(mainMeasureIndex);
			System.out.println("Max RI (raw data) = "+minMaxRI[1]);
			//results of maxRI for all permutations
			System.out.println("Max RI (after permutations) = "+Arrays.toString(maxPermutationRI.toArray()));
			double cutoffRI = getCutoff(mcfsParams.cutoffAlpha, ArrayUtils.Float2double(maxPermutationRI.toArray()));
			topRanking = importancesClassic.getTopRanking(mainMeasureIndex, (float)cutoffRI);
			topRankingSize = 0;
			if(topRanking!=null)
				topRankingSize = topRanking.size();
			System.out.println("Cutoff RI (based on permutations) = " + GeneralUtils.formatFloat(cutoffRI,7));
			System.out.println("Important attributes (based on permutations) = "+topRankingSize);

			double cutoffID = Double.NaN;            
			if(!maxPermutationID.isEmpty()){
				//get cutoff ID
				System.out.println("*** Calculation of cutoff ID ***");
				cutoffID = getCutoff(mcfsParams.cutoffAlpha, ArrayUtils.Float2double(maxPermutationID.toArray()));
				System.out.println("Cutoff ID (based on permutations)  = " + GeneralUtils.formatFloat(cutoffID, 7));
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
			FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_CUTOFF, mcfs.globalStats.getCutoff().toString());

			//remove temporary perm1, perm2 ... importance files
			if(mcfsParams.verbose) {
				String[] s = new String[1];
				s = permFiles.toArray(s);
				System.out.println("deleting files: "+ Arrays.toString(s));
			}
			FileUtils.deleteFiles(permFiles);

			//overwrite top ranking file based on specified topRankingMethod
			String topRankingMethod = mcfs.globalStats.getCutoff().getMethod(mcfsParams.cutoffMethod);
			topRankingSize = (int)mcfs.globalStats.getCutoff().getCutoffValue(topRankingMethod);            
			topRanking = importancesClassic.getTopRankingSize(mainMeasureIndex, topRankingSize);
			System.out.println("*** Final Important attributes (based on "+topRankingMethod+") = "+topRanking.size());            
			if(topRanking!=null){
				FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_TOPRANKING, topRanking.toString());
			}
		}
		//RUN FinalCV
		if(mcfsParams.finalCV){
			int topRankingSize = Math.max((int)mcfs.globalStats.getCutoff().getCutoffValue(mcfsParams.cutoffMethod),4);
			MCFSFinalCV simpleCV;
			if(mcfs.mcfsArrays.sourceArray.isTargetNominal())
				simpleCV = new MCFSFinalCV(new int[]{Classifier.J48, Classifier.RF, Classifier.NB,Classifier.SVM,Classifier.KNN,Classifier.LOGISTIC,Classifier.RIPPER},random);
			else
				simpleCV = new MCFSFinalCV(new int[]{Classifier.M5},random);

			int[] cutoffValues = getCutoffValues(new int[]{topRankingSize});
			System.out.println("");
			System.out.println("*** Running CV experiment on input data limited to the top " + Arrays.toString(cutoffValues) + " attributes ***");        	
			DataFrame res = simpleCV.run(mcfs.mcfsArrays.sourceArray, mcfs.globalStats.getAttrImportances()[0], cutoffValues, 
					mcfsParams.finalCVfolds, mcfsParams.finalCVSetSize, mcfsParams.finalCVRepetitions);
			if(mcfsParams.saveResultFiles) {
				FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_CV_RESULT, res.toString());				
				int cmatrixIdx = ArrayUtils.indexOf(cutoffValues, topRankingSize);
				if(cmatrixIdx != -1 && simpleCV.j48ConfMatrix[cmatrixIdx] != null) {
					FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_MATRIX_TOP, 
							simpleCV.j48ConfMatrix[cmatrixIdx].toString(false, true, false, ","));
				}				
			}
		}

		// minimum 2 attributes
		int topRankingSize = Math.max((int)mcfs.globalStats.getCutoff().getCutoffValue(mcfsParams.cutoffMethod), 2) ;
		int sourceArrayColNumber = mcfs.mcfsArrays.sourceArray.colsNumber() - 1;
		int minTopRankingValue =  (int)(sourceArrayColNumber * mcfsParams.minTopRankingSize);				

		//if minTopRankingSize == 0 (in phase I) then always take topRankingSize
		if(topRankingSize < minTopRankingValue){
			String s = "Warning! Number of top attributes from Phase I equals to " + topRankingSize+" (" + 
					GeneralUtils.formatFloat(100.0*topRankingSize/sourceArrayColNumber,1) + "%). " + 
					"Phase II will use " + minTopRankingValue + " ("+GeneralUtils.formatFloat(100.0*mcfsParams.minTopRankingSize,1) + "%) of top attributes.";
			System.err.println(s);
			topRankingSize = minTopRankingValue;
		}

		int[] topRankingColMask = SelectFunctions.getColumnsMask(mcfs.mcfsArrays.sourceArray, mcfs.globalStats.getAttrImportances()[0], topRankingSize);
		FArray topRankingArray = (FArray)SelectFunctions.selectColumns(mcfs.mcfsArrays.sourceArray, topRankingColMask);

		//RUN Final Rules
		if(mcfsParams.finalRuleset && mcfs.mcfsArrays.sourceArray.isTargetNominal()){
			System.out.println("");
			System.out.println("*** Building final RIPPER ruleset on top "+ topRankingSize +" attributes ***");
			ClassificationBody classification = new ClassificationBody(random);
			classification.setParameters(new ClassificationParams());
			classification.classParams.verbose = false;
			classification.classParams.saveClassifier = false;
			classification.classParams.savePredictionResult = false;
			classification.classParams.repetitions = 1;
			classification.classParams.model = Classifier.RIPPER;			
			classification.initClassifier();
			classification.runTrainTest(topRankingArray, topRankingArray);
			String ripperResult = classification.classifier.toString(false) +"\n";
			classification.initClassifier();
			classification.classParams.folds = 	mcfsParams.finalCVfolds;
			classification.classParams.repetitions = mcfsParams.finalCVRepetitions;
			classification.runCV(topRankingArray);
			ripperResult += "RIPPER CV Result (10 folds repeated "+ mcfsParams.finalCVRepetitions +" times)\n" + classification.predResult.toString();
			System.out.println(classification.classifier.getPredResult().confusionMatrix.statsToString(2, false));
			if(mcfsParams.saveResultFiles)
				FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_RULESET, ripperResult);			
		}

		if(mcfsParams.saveResultFiles){
			System.out.println("*** Saving pruned data ***");
			//System.out.println("*** MDR DEBUG: " + mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_DATA + ".adh");
			//System.out.println("*** MDR DEBUG: " + mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_DATA + ".csv");			
			FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_DATA + ".adh", topRankingArray.toADH());
			FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_DATA + ".csv", topRankingArray.toCSV());
		}

		if(mcfsParams.saveResultFiles && mcfsParams.zipResult){
			zipResult(mcfsParams);
		}		

		long stop = System.currentTimeMillis();
		float experimentTime = (stop-start)/1000.0f;
		System.out.println("*** MCFS-ID Processing is done. Time: " + GeneralUtils.timeIntervalFormat(experimentTime) + " ***");
		System.out.println();
		
		return topRankingArray;
	}
	//************************************
	public GlobalStats getGlobalStats() {
		return mcfs.globalStats;		
	}
	//************************************
	public MCFSArrays getArrays() {
		return mcfs.mcfsArrays;		
	}
	//*********************************
	protected DataFrame createPermutationResult(FArray inputArrray, MCFSParams mcfsParams)
	{
		//create data frame for rankings
		String[] colNames=new String[mcfsParams.cutoffPermutations+1];
		short[] colTypes=new short[colNames.length];	

		colNames[0] = "attribute";
		colTypes[0] = ColumnMetaInfo.TYPE_NOMINAL;
		for(int i=1;i<colNames.length;i++){
			colNames[i] = MCFSPermutation.PERM_PREFIX + i;
			colTypes[i] = ColumnMetaInfo.TYPE_NUMERIC;
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
		System.out.println("Anderson-Darling normality test p-value = " + GeneralUtils.formatFloat(pValue, 7));		
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
			if(colNames[i].startsWith(MCFSPermutation.PERM_PREFIX)){
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
		p_values.setColTypes(new short[]{ColumnMetaInfo.TYPE_NUMERIC,ColumnMetaInfo.TYPE_NUMERIC,ColumnMetaInfo.TYPE_NUMERIC,ColumnMetaInfo.TYPE_NUMERIC});
		p_values.setColumn(0, mean);
		p_values.setColumn(1, valuesRI);
		p_values.setColumn(2, normality_p);
		p_values.setColumn(3, ttest_p);    	
		return p_values;
	}
	//*********************************
	private DataFrame combineRankings(DataFrame df_ri_phase1, DataFrame df_ri_phase2) {

		int rows = df_ri_phase1.rows();
		boolean[] attrMask = new boolean[rows]; 
		int attrColIndex = df_ri_phase2.getColIdx(AttributesRI.ATTRIBUTE_LABEL);
		int attrRIIndx = df_ri_phase1.getColIdx("RI");
		df_ri_phase2.setKeyColumn(attrColIndex);	

		//remove contrast attributes and set RI on -1 for top attributes (these from phase 2) 
		for(int i=0; i<rows; i++ ){
			String currAttr = df_ri_phase1.get(i, attrColIndex).toString();
			if(currAttr.startsWith(MCFSParams.CONTRAST_ATTR_NAME)) {
				attrMask[i] = false;
			}else{
				attrMask[i] = true;
				if(df_ri_phase2.getRowIdx(currAttr) >= 0) {
					df_ri_phase1.set(i, attrRIIndx, -1.0f);
				}										
			}
		}	

		DataFrame df_ri_combined = df_ri_phase1.filterRows(attrMask);
		df_ri_combined.separator = ",";
		rows = df_ri_combined.rows();
		
		//final rescaling
		float[] ri_col_phase1 = df_ri_combined.getColumnNumeric(attrRIIndx);
		float[] ri_col_phase2 = df_ri_phase2.getColumnNumeric(attrRIIndx);
		//System.out.println(Arrays.toString(ri_col_phase1));
		//System.out.println(Arrays.toString(ri_col_phase2));

		MinMax min_max_1 = ArrayUtils.getMinMax(ri_col_phase1,false); 
		MinMax min_max_2 = ArrayUtils.getMinMax(ri_col_phase2,false); 
		//System.out.println(Arrays.toString(min_max_1));
		//System.out.println(Arrays.toString(min_max_2));

		float[] ri_col_phase1_scaled = ArrayUtils.scaleArray(ri_col_phase1, min_max_1.minValue, min_max_2.minValue * 0.999f, false);
		//System.out.println(Arrays.toString(ri_col_phase1_scaled));
		//System.out.println(Arrays.toString(MathUtils.min_max(ri_col_phase1_scaled,false)));

		for(int i=0; i<rows; i++ ){
			String currAttr = df_ri_combined.get(i, attrColIndex).toString();
			int currIdx_2 = df_ri_phase2.getRowIdx(currAttr);
			if(currIdx_2 < 0) {					
				df_ri_combined.set(i, attrRIIndx, ri_col_phase1_scaled[i]);					
			}else{					
				df_ri_combined.set(i, 0, df_ri_phase2.getRow(currIdx_2));					
			}				
		}
		return(df_ri_combined);
	}
	//*********************************
	private void zipResult(MCFSParams mcfsParams) {
		String experimentName = mcfsParams.getExperimentName();
		String[] files = MCFSParams.getAllResultFileName(experimentName);
		try {
			FileOutputStream fos = new FileOutputStream(mcfsParams.resFilesPATH + File.separator + experimentName + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			for(int i = 0; i < files.length; i++){
				FileUtils.addFileToZip(mcfsParams.resFilesPATH + File.separator + files[i], zos);
			}
			FileUtils.addFileToZip(mcfsParams.resFilesPATH + File.separator + experimentName + ".run", zos);

			zos.close();
			fos.close();
			for(int i = 0; i < files.length; i++){
				FileUtils.deleteFile(mcfsParams.resFilesPATH + File.separator + files[i]);
			}
			FileUtils.deleteFile(mcfsParams.resFilesPATH + File.separator + experimentName + ".run");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
//*********************************
