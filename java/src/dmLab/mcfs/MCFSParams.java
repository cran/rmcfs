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
package dmLab.mcfs;

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Classifier;
import dmLab.classifier.WekaClassifier;
import dmLab.experiment.ExperimentParams;
import dmLab.mcfs.mcfsEngine.MCFSAutoParams;
import dmLab.utils.StringUtils;

public class MCFSParams extends ExperimentParams
{
	// STATIC VARIABLES //
	public static String FILESUFIX_RI = "RI.csv";
	public static String FILESUFIX_ID = "ID.csv";
	public static String FILESUFIX_TOPRANKING = "topRanking.csv";
	public static String FILESUFIX_DISTANCE = "distances.csv";
	public static String FILESUFIX_MATRIX = "cmatrix.csv";
	public static String FILESUFIX_CUTOFF = "cutoff.csv";
	public static String FILESUFIX_CV_RESULT = "cv_accuracy.csv";
	public static String FILESUFIX_PERMUTATIONS = "permutations.csv";
	public static String FILESUFIX_RULESET = "jrip.txt";
	public static String FILESUFIX_PREDICTION_STATS = "predictionStats.csv";	
	public static String FILESUFIX_DATA = "data";

	public static String DISCRETIZER_CFG_FILE = "discretizer.cfg";
	public static String CONTRAST_ATTR_NAME = "contrast_attr_";	

	public static String[] CUTOFF_METHODS = new String[]{"mean", "criticalAngle", "kmeans", "contrast", "permutations"};

	public static int PROJECTION_SIZE_MIN = 1;
	public static int CONTRAST_ATTR_MIN = 1;
	
	public static int MODE_AUTO = 0; 
	
	public static String TMP_PATH = "./tmp/";
	
	public long seed;
	public int progressTopMinSize;
	public boolean progressShow;
	public int progressInterval;
	public int threadsNumber;
	public int model;
	public String target;
	public int mode;
	
	public boolean buildID;
	public boolean finalRuleset;
	public boolean finalCV;
	public int finalCVSetSize;
	public int finalCVRepetitions;
	public int foldsCV;
	
	public String cutoffMethod;
	public int cutoffPermutations;
	public int featureFreq;
	public float balance;

	public int projections;
	public float projectionSize;
	public int projectionSizeMax;
	public int splits;
	public float splitRatio;
	public int splitSetSize;

	public float u;
	public float v;
	public float cutoffAlpha;
	public float cutoffAngle;
	public float contrastSize;    
	public float contrastCutoff;
	public boolean zipResult;
    public boolean saveResutFiles;

	//specific configuration of classifiers
	//mode memory or file
	public int wekaClassifierMode;
	//j48 params
	public boolean useGainRatio;
	public int maxConnectionDepth;
	//ADX params
	public int qMethod;
	public boolean useComplexQuality;  
	//sliq params
	public boolean useDiversityMeasure;
	
	//temporary params
	public int[] tmpBalancedClassSizes;
	public int projectionsValue;
	public int projectionSizeValue;
	
	//*************************************
	public MCFSParams()
	{
		setDefault();
	}
	//*************************************	
	public boolean setDefault()
	{
		super.setDefault();
		
		label = "MCFS";
		inputFilesPATH = DEFAULT_DATA_PATH;
		resFilesPATH = DEFAULT_RES_PATH;
		verbose = false;
		inputFileName = "";
		testFileName = null;
		outputFileName = null;
		
		seed = System.currentTimeMillis();
		progressTopMinSize = 30;
		progressInterval = 20;
		progressShow = true;
		threadsNumber = 4;
		model = Classifier.AUTO;
		target = "";
		mode = 1;
		
		buildID = true;
		finalRuleset = true;
		finalCV = true;
		finalCVSetSize = 1000;
		finalCVRepetitions = 3;		
		foldsCV = 10;
		
		cutoffMethod = "mean";
		cutoffPermutations = 20;
		featureFreq = 100;
		balance = MCFSAutoParams.AUTO;

		projections = MCFSAutoParams.AUTO;
		projectionsValue = -1;
		projectionSize = MCFSAutoParams.AUTO;
		projectionSizeMax = 500;
		projectionSizeValue = -1;
		splits = 5;
		splitRatio = 0.66f;
		splitSetSize = 1000;

		u=1f;
		v=1f;
		cutoffAlpha = 0.05f;
		cutoffAngle = 0.01f;
		contrastSize = 0.1f;    
		contrastCutoff = 0.05f;
		
		zipResult = true;
		saveResutFiles = true;
		
		//specific configuration of classifiers
		maxConnectionDepth = 5;
		useGainRatio = true;
		qMethod = 2;
		useComplexQuality = true;      
		useDiversityMeasure = true;
		//wekaClassifier mode MEMORY/FILE
		wekaClassifierMode = WekaClassifier.MEMORY;

		//temporary params initiation
		tmpBalancedClassSizes = null;
		return true;
	}
	//*************************************************
	public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append(super.toString()).append('\n');
		tmp.append("### MCFS Parameters ### ").append('\n');
		tmp.append("mcfs.seed = "+seed).append('\n');
		tmp.append("mcfs.showDistanceProgress = "+progressShow).append('\n');
		tmp.append("mcfs.progressInterval = "+progressInterval ).append('\n');
		tmp.append("mcfs.threadsNumber = "+threadsNumber).append('\n');
		tmp.append("mcfs.model = "+ Classifier.int2label(model)).append('\n');
		tmp.append("mcfs.mode = "+ mode).append('\n');
		tmp.append("target = "+ target).append('\n');		
		tmp.append('\n');
		tmp.append("mcfs.buildID = "+ buildID).append('\n');        
		tmp.append("mcfs.finalRuleset = "+ finalRuleset).append('\n');        
		tmp.append("mcfs.finalCV = "+ finalCV).append('\n');        
		tmp.append("mcfs.finalCVSetSize = "+ finalCVSetSize).append('\n');
		tmp.append("mcfs.finalCVRepetitions = "+ finalCVRepetitions).append('\n');
		tmp.append('\n');
		tmp.append("mcfs.cutoffMethod = "+ cutoffMethod).append('\n');
		tmp.append("mcfs.cutoffPermutations = "+ cutoffPermutations).append('\n');		
		tmp.append("mcfs.featureFreq = "+ featureFreq).append('\n');
		tmp.append("mcfs.balance = "+ MCFSAutoParams.valueToString(balance)).append('\n');
		tmp.append('\n');		
		tmp.append("mcfs.projections = "+ MCFSAutoParams.valueToString(projections)).append('\n');
		tmp.append("mcfs.projectionSize = "+ MCFSAutoParams.valueToString(projectionSize)).append('\n');
		tmp.append("mcfs.projectionSizeMax = "+ projectionSizeMax).append('\n');
		tmp.append("mcfs.splits = "+ splits).append('\n');
		tmp.append("mcfs.splitRatio = "+ splitRatio).append('\n');
		tmp.append("mcfs.splitSetSize = "+ splitSetSize).append('\n');
		tmp.append('\n');				
		tmp.append("mcfs.u = "+ u).append('\n');
		tmp.append("mcfs.v = "+ v).append('\n');
		tmp.append("mcfs.cutoffAlpha = "+ cutoffAlpha).append('\n');
		tmp.append("mcfs.cutoffAngle = "+ cutoffAngle).append('\n');
		tmp.append("mcfs.contrastSize = "+ contrastSize).append('\n');
		tmp.append("mcfs.contrastCutoff = "+ contrastCutoff).append('\n');
		tmp.append("mcfs.zipResult = "+ zipResult).append('\n');		
		tmp.append('\n');		
		tmp.append("j48.useGainRatio = "+ useGainRatio).append('\n');
		tmp.append("j48.maxConnectionDepth = "+ maxConnectionDepth).append('\n');
		tmp.append("adx.useComplexQuality = "+ useComplexQuality).append('\n');
		tmp.append("adx.qMethod = "+ qMethod).append('\n');
		tmp.append("sliq.useDiversityMeasure = "+ useDiversityMeasure).append('\n');
		tmp.append('\n');
		return tmp.toString();
	}
	//  ****************************************************
	protected boolean update(Properties properties)
	{
		if(!super.update(properties))
			return false; 

		testFileName = null;
		outputFileName = null;

		String seedProp = properties.getProperty("mcfs.seed", "");
		if(seedProp.trim().length()==0)
			seed = System.currentTimeMillis();
		else
			seed = Long.valueOf(seedProp).intValue();
				
		progressShow = Boolean.valueOf(properties.getProperty("mcfs.progressShow", "true")).booleanValue();
		progressInterval = Integer.valueOf(properties.getProperty("mcfs.progressInterval", "10")).intValue();
		threadsNumber = Integer.valueOf(properties.getProperty("mcfs.threadsNumber", "8")).intValue();        
		model = Classifier.label2int(properties.getProperty("mcfs.model", "auto"));
		mode = Integer.valueOf(properties.getProperty("mcfs.mode", "1")).intValue();        
		target = properties.getProperty("target", "");

		buildID = Boolean.valueOf(properties.getProperty("mcfs.buildID", "true")).booleanValue();
		finalRuleset = Boolean.valueOf(properties.getProperty("mcfs.finalRuleset", "true")).booleanValue();
		finalCV = Boolean.valueOf(properties.getProperty("mcfs.finalCV", "true")).booleanValue();
		finalCVSetSize = Integer.valueOf(properties.getProperty("mcfs.finalCVSetSize", "1000")).intValue();
		finalCVRepetitions = Integer.valueOf(properties.getProperty("mcfs.finalCVRepetitions", "3")).intValue();

		cutoffMethod = properties.getProperty("mcfs.cutoffMethod", "mean");
		cutoffPermutations = Integer.valueOf(properties.getProperty("mcfs.cutoffPermutations", "20")).intValue();
		featureFreq = Integer.valueOf(properties.getProperty("mcfs.featureFreq", "100")).intValue();		
		balance = MCFSAutoParams.valueToFloat("mcfs.balance", properties.getProperty("mcfs.balance", "auto")); 

		projections = (int)MCFSAutoParams.valueToFloat("mcfs.projections", properties.getProperty("mcfs.projections", "auto"));        		
		projectionSize = MCFSAutoParams.valueToFloat("mcfs.projectionSize", properties.getProperty("mcfs.projectionSize", "auto"));
		projectionSizeMax = Integer.valueOf(properties.getProperty("mcfs.projectionSizeMax", "500")).intValue();
		splits = Integer.valueOf(properties.getProperty("mcfs.splits", "5")).intValue();
		splitRatio = Float.valueOf(properties.getProperty("mcfs.splitRatio","0.66")).floatValue();
		splitSetSize = Float.valueOf(properties.getProperty("mcfs.splitSetSize", "1000")).intValue();
		
		u = Float.valueOf(properties.getProperty("mcfs.u","1")).floatValue();
		v = Float.valueOf(properties.getProperty("mcfs.v","1")).floatValue();
		cutoffAngle = Float.valueOf(properties.getProperty("mcfs.cutoffAngle", "0.01")).floatValue();
		cutoffAlpha = Float.valueOf(properties.getProperty("mcfs.cutoffAlpha", "0.05")).floatValue();
		contrastSize = Float.valueOf(properties.getProperty("mcfs.contrastSize", "0.1")).floatValue();
		contrastCutoff = Float.valueOf(properties.getProperty("mcfs.contrastCutoff", "0.05")).floatValue();
		zipResult = Boolean.valueOf(properties.getProperty("mcfs.zipResult", "true")).booleanValue();
		
		useGainRatio = Boolean.valueOf(properties.getProperty("j48.useGainRatio", "true")).booleanValue();        
		maxConnectionDepth = Integer.valueOf(properties.getProperty("j48.maxConnectionDepth", "3")).intValue();
		qMethod = Integer.valueOf(properties.getProperty("adx.qMethod", "2")).intValue();;
		useComplexQuality = Boolean.valueOf(properties.getProperty("adx.useComplexQuality", "true")).booleanValue();
		useDiversityMeasure = Boolean.valueOf(properties.getProperty("sliq.useDiversityMeasure", "true")).booleanValue();

		return true;
	}
	//  ****************************************************
	public boolean check(FArray array)
	{
		if(!super.check(array))
			return false;

		if(splits<=0){
			System.err.println("Warning! Incorrect value of splits: "+splits+" Using default value = 5.");
			splits = 5;
		}
		if(splitRatio <= 0 || splitRatio >= 1){
			System.err.println("Warning! Incorrect value of splitRatio: "+splitRatio+" Using default value = 0.66.");
			splitRatio = 0.66f;
		}
		if(cutoffPermutations!=0 && cutoffPermutations < 3){
			System.err.println("Warning! Incorrect value of cutoffPermutations: "+cutoffPermutations+" Using minimum value = 3.\n");
			cutoffPermutations = 3;
		}
		if(cutoffPermutations==0 && cutoffMethod.equalsIgnoreCase("permutations")){
			System.err.println("Warning! Value of cutoffPermutations = "+cutoffPermutations+" and cutoffMethod = '"+cutoffMethod+"'. Using cutoffMethod = 'mean'.\n");
			cutoffMethod = "mean";
		}
		if(cutoffAlpha <= 0){
			System.err.println("Warning! Incorrect value of cutPointAlpha: "+cutoffAlpha+" Using default value = 0.05.\n");
			cutoffAlpha = 0.05f;
		}
		if(model!=Classifier.M5 && model!=Classifier.J48 && model!=Classifier.ADX && model!=Classifier.SLIQ && model!=Classifier.AUTO){
			System.err.println("Error! Incorrect classifier: "+model);
			return false;
		}
		if(!StringUtils.equalsToAny(cutoffMethod, CUTOFF_METHODS)){
			System.err.println("Error! Incorrect cutoffMethod: '"+cutoffMethod +"'");
			return false;
		}
		if(mode != 1 && mode != 2){
			System.err.println("Warning! Incorrect value of mode: "+mode+" Using default value = 1.\n");
			mode = 1;			
		}			

		// if array is not null
		if(array!=null){
			target = array.attributes[array.getDecAttrIdx()].name;
			projectionSizeValue = MCFSAutoParams.setProjectionSize(projectionSize, PROJECTION_SIZE_MIN, projectionSizeMax, array);
			projectionsValue = MCFSAutoParams.setProjections(projections, projectionSizeValue, featureFreq, array);
			if(model == Classifier.AUTO){
				if(array.isTargetNominal()){
					model = Classifier.J48;
					System.out.println("Nominal target detected - using J48 model.");
				}else{
					model = Classifier.M5;
					System.out.println("Numeric target detected - using M5 model.");
				}
			}
		}
		return true;    	    	
	}
	//  ****************************************************
	public MCFSParams clone()
	{    
		MCFSParams p = new MCFSParams();
		p.set(this);    	
		return p;
	}
	//  ****************************************************    
	public void set(MCFSParams p)
	{
		super.set(p);
		
		seed = p.seed;
		progressShow = p.progressShow;
		progressInterval = p.progressInterval;
		progressTopMinSize = p.progressTopMinSize;
		threadsNumber = p.threadsNumber;
		model = p.model;
		target = p.target;
		
		buildID = p.buildID;
		finalRuleset = p.finalRuleset;
		finalCV = p.finalCV;
		finalCVSetSize = p.finalCVSetSize;
		finalCVRepetitions = p.finalCVRepetitions;
		foldsCV = p.foldsCV;
		
		cutoffMethod = p.cutoffMethod;
		cutoffPermutations=p.cutoffPermutations;
		featureFreq = p.featureFreq;
		balance = p.balance;

		projections = p.projections;
		projectionsValue = p.projectionsValue;
		projectionSize = p.projectionSize;
		projectionSizeMax = p.projectionSizeMax;
		projectionSizeValue = p.projectionSizeValue;

		splits = p.splits;
		splitRatio = p.splitRatio;
		splitSetSize = p.splitSetSize;
		
		u = p.u;
		v = p.v;
		cutoffAlpha = p.cutoffAlpha;
		cutoffAngle = p.cutoffAngle;
		contrastSize = p.contrastSize;    
		contrastCutoff = p.contrastCutoff;
		zipResult = p.zipResult;
	    saveResutFiles = p.saveResutFiles;

		wekaClassifierMode = p.wekaClassifierMode;
		useGainRatio = p.useGainRatio;
		maxConnectionDepth = p.maxConnectionDepth;
		qMethod = p.qMethod;
		useComplexQuality = p.useComplexQuality;  
		useDiversityMeasure = p.useDiversityMeasure;

		return;
	}    
	//  ****************************************************
	public String getExperimentName()
	{
		//return "_s"+projections+"_t"+splits;    	
		return inputFileName.substring(0, inputFileName.lastIndexOf('.'));
	}
	//  ****************************************************
	public static String getExperimentName(String fileName)
	{
		int experimentPrefixIndex = fileName.lastIndexOf("__");
		if(experimentPrefixIndex==-1)
			experimentPrefixIndex = fileName.lastIndexOf("_");
		String experimentPrefix = "";
		if(experimentPrefixIndex!=-1){
			experimentPrefix = fileName.substring(0, experimentPrefixIndex+1);         	
		}
		return experimentPrefix;
	}
	//  ****************************************************
	public static String[] getAllResultFileName(String experimentName){
		
		String[] retStringArray = new String[] {
				experimentName + "__" + FILESUFIX_RI,
				experimentName + "_" + FILESUFIX_ID,
				experimentName + "_" + FILESUFIX_TOPRANKING,
				experimentName + "_" + FILESUFIX_DISTANCE,
				experimentName + "_" + FILESUFIX_MATRIX,
				experimentName + "_" + FILESUFIX_CUTOFF,
				experimentName + "_" + FILESUFIX_CV_RESULT,
				experimentName + "_" + FILESUFIX_PERMUTATIONS,
				experimentName + "_" + FILESUFIX_RULESET,
				experimentName + "_" + FILESUFIX_PREDICTION_STATS,
				experimentName + "_" + FILESUFIX_DATA + ".csv",
				experimentName + "_" + FILESUFIX_DATA + ".adh"};
		
		return retStringArray;
	}
	//  ****************************************************

}
