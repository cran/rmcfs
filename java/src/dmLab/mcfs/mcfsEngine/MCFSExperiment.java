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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import dmLab.array.FArray;
import dmLab.array.functions.SelectFunctions;
import dmLab.classifier.Classifier;
import dmLab.experiment.classification.ClassificationBody;
import dmLab.experiment.classification.ClassificationParams;
import dmLab.mcfs.MCFSFinalCV;
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
    public MCFSParams mcfsParams;
    protected Random random;
    
    public final static String PERM_PREFIX = "perm_";  
    //************************************
    public MCFSExperiment(long seed)
    {        
    	random = new Random(seed);
    }
    //************************************
    public void init(MCFSParams mcfsParams){
    	this.mcfsParams=mcfsParams;    	
    }    
    //************************************    	
    @Override
    public void run() 
    {
        if(mcfsParams.check(null)==false)
        	return;
        
        DataFrame permutationRIValues = null;
        Ranking topRanking = null;
        ArrayList<Float> maxPermutationRI = new ArrayList<Float>();
        ArrayList<Float> maxPermutationID = new ArrayList<Float>();
        ArrayList<String> permPrefix = new ArrayList<String>(); 
        
        for(int i=0;i<mcfsParams.cutoffPermutations;i++){
            System.out.println("*** MCFS-ID Cutoff Permutation Experiment #"+(i+1)+" ***");        	
            mcfs = new MCFSPermutation(random);
            ((MCFSPermutation)mcfs).permPrefix = ((MCFSPermutation)mcfs).permPrefix+(i+1)+"_";
            permPrefix.add(((MCFSPermutation)mcfs).permPrefix);
            if(!mcfs.run(mcfsParams))
            	return;
            AttributesRI imp = mcfs.globalStats.getAttrImportances()[0];
            if(permutationRIValues==null)
            	permutationRIValues = createPermutationResult(mcfs.mcfsArrays.sourceArray, mcfsParams);
            permutationRIValues.setColumn(i+1, imp.getImportanceValues(imp.mainMeasureIdx));
            float[] minMax = imp.getMinMaxImportances(imp.mainMeasureIdx);
            maxPermutationRI.add(minMax[1]);
            if(mcfs.globalStats.getAttrConnections()!=null)
            	maxPermutationID.add(mcfs.globalStats.getAttrConnections().getMaxID());
        }
        
        String experimentName = mcfsParams.getExperimentName();
        
        //run classic MCFS procedure
        mcfs = new MCFSClassic(random);        
        if(!mcfs.run(mcfsParams))
        	return;
        AttributesRI importancesClassic = mcfs.globalStats.getAttrImportances()[0];
        
        //finish cutoff calculation
        if(permutationRIValues!=null){
        	//calculate p values and add them to the result
            int mainMeasureIndex = importancesClassic.mainMeasureIdx;
            DataFrame p_values = calc_pValues(permutationRIValues, importancesClassic.getImportanceValues(mainMeasureIndex),0.05f);
            permutationRIValues.cbind(p_values);
        	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_PERMUTATIONS, permutationRIValues.toString());
        	        	
            //get cutoff RI
            System.out.println("");
            System.out.println("*** Calculation of Minimal Important RI ***");
            float[] minMaxRI = importancesClassic.getMinMaxImportances(mainMeasureIndex);
            System.out.println("Max obtained RI = "+minMaxRI[1]);
            //results of maxRI for all permutations
            System.out.println("Max Permutation RI = "+Arrays.toString(maxPermutationRI.toArray()));
            double cutoffRI = getCutoff(mcfs.mcfsParams.cutoffAlpha, ArrayUtils.Float2double(maxPermutationRI.toArray()));
            topRanking = importancesClassic.getTopRanking(mainMeasureIndex, (float)cutoffRI);
            int topRankingSize = 0;
            if(topRanking!=null)
            	topRankingSize = topRanking.size();
            System.out.println("Minimal important (based on permutation) RI = " + GeneralUtils.format(cutoffRI,7));
            System.out.println("Top important (based on permutation) attributes number = "+topRankingSize);

            double cutoffID = Double.NaN;
            if(mcfsParams.buildID){
	            //get cutoff ID
	            System.out.println("");
	            System.out.println("*** Calculation of Minimal Important ID ***");
	            cutoffID = getCutoff(mcfs.mcfsParams.cutoffAlpha,ArrayUtils.Float2double(maxPermutationID.toArray()));
	            System.out.println("Minimal important (based on permutation) ID = " + GeneralUtils.format(cutoffID, 7));
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
            removePermResultTempFiles(permPrefix);
            
            //overwrite top ranking file based on specified topRankingMethod
            String topRankingMethod = mcfs.globalStats.getCutoff().getMethod(mcfs.mcfsParams.cutoffMethod);
            topRankingSize = (int)mcfs.globalStats.getCutoff().getCutoffValue(topRankingMethod);            
            topRanking = importancesClassic.getTopRankingSize(mainMeasureIndex, topRankingSize);
            System.out.println("");
            System.out.println("*** Final top important (based on "+topRankingMethod+") attributes = "+topRanking.size());            
            if(topRanking!=null){
            	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_TOPRANKING, topRanking.toString());
            }
        }
        //RUN FinalCV
        if(mcfsParams.finalCV){
        	int topRankingSize = (int)mcfs.globalStats.getCutoff().getCutoffValue(mcfs.mcfsParams.cutoffMethod);
        	if(topRankingSize == 0)
        		topRankingSize = 8;
        	MCFSFinalCV simpleCV = new MCFSFinalCV(new int[]{Classifier.J48,Classifier.NB,Classifier.SVM,Classifier.KNN,Classifier.LOGISTIC,Classifier.RIPPER},random);
        	//int[] cutoffValues = getCutoffValues(ArrayUtils.double2int(mcfs.globalStats.getCutoff().getCutoffValues()));
        	int[] cutoffValues = getCutoffValues(new int[]{topRankingSize});
            System.out.println("");
        	System.out.println("*** Running Cross Validation experiment on top " + Arrays.toString(cutoffValues) + " attributes ***");        	
            DataFrame res = simpleCV.run(mcfs.mcfsArrays.sourceArray,mcfs.globalStats.getAttrImportances()[0], cutoffValues, 
            		mcfsParams.foldsCV, mcfsParams.finalCVSetSize, mcfsParams.finalCVRepetitions);
            if(mcfs.saveResutFiles)
            	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_CV_RESULT, res.toString());
        }
        
        //RUN Final Rules
        if(mcfsParams.finalRuleset){
        	int topRankingSize = (int)mcfs.globalStats.getCutoff().getCutoffValue(mcfs.mcfsParams.cutoffMethod);
        	if(topRankingSize == 0)
        		topRankingSize = 2;

            System.out.println("");
        	System.out.println("*** Building RIPPER ruleset on top "+ topRankingSize +" attributes ***");
			FArray topRankingArray = (FArray)SelectFunctions.selectColumns(mcfs.mcfsArrays.sourceArray, mcfs.globalStats.getAttrImportances()[0], topRankingSize);
						
			ClassificationBody classification = new ClassificationBody(random);
			classification.setParameters(new ClassificationParams());
			classification.classParams.debug = false;
			classification.classParams.verbose = false;
			classification.classParams.saveClassifier = false;
			classification.classParams.savePredictionResult = false;
			classification.classParams.classifierCfgPATH = "";
			classification.classParams.classifier = Classifier.RIPPER;

			classification.createClassifier();
			classification.singleTrainTest(topRankingArray,topRankingArray);			
			String ripperResult = classification.classifier.toString(false) +"\n\n";
			
			classification.createClassifier();
			classification.cleanStats();

			classification.classParams.folds = 	mcfsParams.foldsCV;
			classification.classParams.repetitions = mcfsParams.finalCVRepetitions;
			classification.multipleCV(topRankingArray);
			ripperResult += "##### Cross Validation Result (10 folds repeated "+ mcfsParams.finalCVRepetitions +" times) #####" + "\n" + classification.toStringCMatrix();			
			System.out.println(ripperResult);
			
            if(mcfs.saveResutFiles)
            	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_RULESET, ripperResult);			
        }
        
        return;
    }
    //*********************************
    protected int removePermResultTempFiles(ArrayList<String> permPrefix)
    {
    	String experimentName = mcfsParams.getExperimentName();
    	for(int i=0;i<permPrefix.size();i++){
    		String fileName = mcfsParams.resFilesPATH+permPrefix.get(i)+experimentName+"__"+MCFSParams.FILESUFIX_IMPORTANCES;
    		//System.out.println("removing:"+fileName);
	    	File f = new File(fileName);
	    	if(f.exists())
	    		f.delete();
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
		System.out.println("Anderson-Darling normality test p-value = " + GeneralUtils.format(pValue,7));		
    	double[] confidence = StatFunctions.getConfidenceInterval(alpha, values);
    	System.out.println("Confidence Interval: "+ GeneralUtils.format(confidence[0],7)+" ; "+GeneralUtils.format(confidence[1],7));                
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
    		mean[i] = (float)MathUtils.avg(permRI);
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
