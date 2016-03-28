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
import dmLab.utils.StringUtils;

public class MCFSParams extends ExperimentParams
{
	// STATIC VARIABLES //
	public static String FILESUFIX_IMPORTANCES = "RI.csv";
	public static String FILESUFIX_CONNECTIONS = "ID.csv";
	public static String FILESUFIX_TOPRANKING = "topRanking.csv";
	public static String FILESUFIX_DISTANCE = "distances.csv";
	public static String FILESUFIX_MATRIX = "cmatrix.csv";
	public static String FILESUFIX_CUTOFF = "cutoff.csv";
	public static String FILESUFIX_CV_RESULT = "cv_accuracy.csv";
	public static String FILESUFIX_PERMUTATIONS = "permutations.csv";
	public static String FILESUFIX_RULESET = "jrip.txt";

	public static String DISCRETIZER_CFG_FILE = "discretizer.cfg";
	public static String CONTRAST_ATTR_NAME = "contrast_attr_";

	public static String[] CUTOFF_METHODS = new String[]{"mean", "criticalAngle", "kmeans", "permutations","contrastAttributes"};

	public long seed;
	public boolean progressShow;
	public int progressInterval;
	public int threadsNumber = 8;
	public int classifier;

	public int projections;
	public float projectionSize;
	public int projectionSizeAttr=-1;

	public int splits;
	public float splitRatio;

	public float balanceRatio;
	public int splitSetSize;

	public boolean buildID = true;
	public boolean finalRuleset = true;
	public boolean finalCV = true;    
	public int finalCVSetSize = 1000;
	public int finalCVRepetitions = 3;
	public int foldsCV = 10;

	public float u;
	public float v;

	public int cutoffPermutations = 20;
	public float cutoffAlpha;
	public float cutoffAngle;
	public String cutoffMethod;

	public boolean contrastAttr=false;    
	public int contrastAttrThreshold;

	//specific configuration of classifiers
	public int wekaClassifierMode;
	//j48 params
	public boolean useGainRatio;
	public int maxConnectionDepth;
	//ADX params
	public int qMethod;
	public boolean useComplexQuality;  
	//sliq params
	public boolean useDiversityMeasure;

	//*************************************
	public MCFSParams()
	{
		setDefault();
	}
	//*************************************	
	public boolean setDefault()
	{
		super.setDefault();
		label="MCFS";
		inputFilesPATH = DEFAULT_DATA_PATH;
		resFilesPATH = DEFAULT_RES_PATH;
		verbose=false;
		debug=false;
		inputFileName = "";
		testFileName = null;
		outputFileName = null;

		seed = System.currentTimeMillis();
		threadsNumber=8;
		progressInterval=10;
		progressShow=true;

		classifier=Classifier.J48;

		projections = 2000; 
		projectionSize = 0.05f;

		splits = 5;
		splitRatio = 0.66f;
		balanceRatio=1;
		splitSetSize=1000;
		u=1f;
		v=1f;

		buildID = true;
		finalRuleset = true;
		finalCV = true;
		finalCVSetSize = 1000;
		finalCVRepetitions = 3;

		cutoffPermutations=20;
		cutoffAlpha=0.05f;

		contrastAttr=false;    
		contrastAttrThreshold=5;            
		cutoffAngle=0.01f;

		cutoffMethod = "mean";

		//specific configuration of classifiers
		maxConnectionDepth=5;
		useGainRatio=true;
		qMethod=2;
		useComplexQuality=true;      
		useDiversityMeasure=true;

		//wekaClassifier mode MEMORY/FILE
		wekaClassifierMode = WekaClassifier.MEMORY;

		return true;
	}
	//*************************************************
	public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append(super.toString()).append('\n');
		tmp.append("### MCFS Parameters ### ").append('\n');        
		tmp.append("mcfs.projections = "+ projections).append('\n');
		tmp.append("mcfs.projectionSize = "+ projectionSize).append('\n');
		tmp.append("mcfs.splits = "+ splits).append('\n');
		tmp.append("mcfs.splitRatio = "+ splitRatio).append('\n');
		tmp.append("mcfs.balanceRatio = "+ balanceRatio).append('\n');
		tmp.append("mcfs.splitSetSize = "+ splitSetSize).append('\n');
		tmp.append("mcfs.contrastAttr = "+ contrastAttr).append('\n');
		tmp.append("mcfs.contrastAttrThreshold = "+ contrastAttrThreshold).append('\n');
		tmp.append("mcfs.cutoffPermutations = "+ cutoffPermutations).append('\n');
		tmp.append("mcfs.cutoffAlpha = "+ cutoffAlpha).append('\n');
		tmp.append("mcfs.cutoffAngle = "+ cutoffAngle).append('\n');
		tmp.append("mcfs.cutoffMethod = "+ cutoffMethod).append('\n');
		tmp.append("mcfs.buildID = "+ buildID).append('\n');        
		tmp.append("mcfs.finalRuleset = "+ finalRuleset).append('\n');        
		tmp.append("mcfs.finalCV = "+ finalCV).append('\n');        
		tmp.append("mcfs.finalCVSetSize = "+ finalCVSetSize).append('\n');
		tmp.append("mcfs.finalCVRepetitions = "+ finalCVRepetitions).append('\n');
		tmp.append("mcfs.u = "+ u).append('\n');
		tmp.append("mcfs.v = "+ v).append('\n');        
		tmp.append('\n');
		tmp.append("mcfs.threadsNumber = "+threadsNumber).append('\n');
		tmp.append("mcfs.showDistanceProgress = "+progressShow).append('\n');
		tmp.append("mcfs.progressInterval = "+progressInterval ).append('\n');
		tmp.append("mcfs.classifier = "+ Classifier.int2label(classifier)).append('\n');
		tmp.append("mcfs.seed = "+seed).append('\n');
		tmp.append("j48.useGainRatio = "+ useGainRatio).append('\n');
		tmp.append("j48.maxConnectionDepth = "+ maxConnectionDepth).append('\n');
		tmp.append("adx.useComplexQuality = "+ useComplexQuality).append('\n');
		tmp.append("adx.qMethod = "+ qMethod).append('\n');
		tmp.append("sliq.useDiversityMeasure = "+ useDiversityMeasure).append('\n');

		return tmp.toString();
	}
	//  ****************************************************
	protected boolean update(Properties properties)
	{
		if(!super.update(properties))
			return false; 

		testFileName = null;
		outputFileName = null;

		//names of output files        
		progressShow = Boolean.valueOf(properties.getProperty("mcfs.progressShow", "true")).booleanValue();
		threadsNumber = Integer.valueOf(properties.getProperty("mcfs.threadsNumber", "8")).intValue();        
		classifier = Classifier.label2int(properties.getProperty("mcfs.classifier", "j48"));        
		progressInterval = Integer.valueOf(properties.getProperty("mcfs.progressInterval", "10")).intValue();
		String seedProp = properties.getProperty("mcfs.seed", "");
		if(seedProp.trim().length()==0)
			seed = System.currentTimeMillis();
		else
			seed = Long.valueOf(seedProp).intValue();

		projections = Integer.valueOf(properties.getProperty("mcfs.projections", "1000")).intValue();
		projectionSize = Float.valueOf(properties.getProperty("mcfs.projectionSize", "0.1")).floatValue();        

		splits = Integer.valueOf(properties.getProperty("mcfs.splits", "10")).intValue();
		splitRatio = Float.valueOf(properties.getProperty("mcfs.splitRatio","0.66")).floatValue();
		balanceRatio=Float.valueOf(properties.getProperty("mcfs.balanceRatio", "1")).floatValue();      

		splitSetSize=Float.valueOf(properties.getProperty("mcfs.splitSetSize", "1000")).intValue();

		cutoffMethod = properties.getProperty("mcfs.cutoffMethod", "mean");

		buildID = Boolean.valueOf(properties.getProperty("mcfs.buildID", "true")).booleanValue();
		finalRuleset = Boolean.valueOf(properties.getProperty("mcfs.finalRuleset", "true")).booleanValue();
		finalCV = Boolean.valueOf(properties.getProperty("mcfs.finalCV", "true")).booleanValue();
		finalCVSetSize = Integer.valueOf(properties.getProperty("mcfs.finalCVSetSize", "1000")).intValue();
		finalCVRepetitions = Integer.valueOf(properties.getProperty("mcfs.finalCVRepetitions", "3")).intValue();

		u=Float.valueOf(properties.getProperty("mcfs.u","1")).floatValue();
		v=Float.valueOf(properties.getProperty("mcfs.v","1")).floatValue();

		contrastAttr=Boolean.valueOf(properties.getProperty("mcfs.contrastAttr", "false")).booleanValue();
		contrastAttrThreshold=Integer.valueOf(properties.getProperty("mcfs.contrastAttrThreshold", "5")).intValue();            
		cutoffAngle=Float.valueOf(properties.getProperty("mcfs.cutoffAngle", "0.01")).floatValue();

		cutoffPermutations=Integer.valueOf(properties.getProperty("mcfs.cutoffPermutations", "25")).intValue();
		cutoffAlpha=Float.valueOf(properties.getProperty("mcfs.cutoffAlpha", "0.05")).floatValue();

		useGainRatio=Boolean.valueOf(properties.getProperty("j48.useGainRatio", "true")).booleanValue();        
		maxConnectionDepth=Integer.valueOf(properties.getProperty("j48.maxConnectionDepth", "3")).intValue();
		qMethod=Integer.valueOf(properties.getProperty("adx.qMethod", "2")).intValue();;
		useComplexQuality=Boolean.valueOf(properties.getProperty("adx.useComplexQuality", "true")).booleanValue();
		useDiversityMeasure=Boolean.valueOf(properties.getProperty("sliq.useDiversityMeasure", "true")).booleanValue();                
		return true;
	}
	//  ****************************************************
	public boolean check(FArray array)
	{
		if(!super.check(array))
			return false;

		if(splits<=0){
			System.err.println("Error! Incorrect value of splits: "+splits);
			return false;
		}
		if(projections<=0){
			System.err.println("Error! Incorrect value of projections: "+projections);
			return false;
		}        
		if(projectionSize<=0){
			System.err.println("Error! Incorrect value of projectionSize: "+projectionSize);
			return false;
		}
		if(splitRatio<=0 || splitRatio>=1){
			System.err.println("Error! Incorrect value of splitRatio: "+splitRatio);
			return false;
		}
		if(balanceRatio<0){
			System.err.println("Error! Incorrect value of balanceRatio: "+balanceRatio);
			return false;
		}
		if(cutoffPermutations<0){
			System.err.println("Error! Incorrect value of cutoffPermutations: "+cutoffPermutations);
			return false;
		}
		if(cutoffAlpha<=0){
			System.err.println("Error! Incorrect value of cutPointAlpha: "+cutoffAlpha);
			return false;
		}        
		if(classifier!=Classifier.RND && classifier!=Classifier.J48 &&
				classifier!=Classifier.ADX && classifier!=Classifier.SLIQ){
			System.err.println("Error! Incorrect value of classifier:"+classifier);
			return false;
		}
		if(array!=null){
			if(!setAttrSize(array.colsNumber()))
				return false;
		}
		if(!StringUtils.stringsEquals(cutoffMethod,CUTOFF_METHODS)){
			System.out.println("Warrning! Incorrect value of cutoffMethod:'"+cutoffMethod +"'. Replaced by 'mean'");
			cutoffMethod = "mean";        	
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

		progressShow=p.progressShow;
		progressInterval=p.progressInterval;
		threadsNumber =p. threadsNumber;
		classifier=p.classifier;
		projections=p.projections;
		projectionSize=p.projectionSize;
		projectionSizeAttr=p.projectionSizeAttr;
		splits=p.splits;
		splitRatio=p.splitRatio;
		balanceRatio=p.balanceRatio;
		splitSetSize=p.splitSetSize;
		cutoffMethod=p.cutoffMethod;        

		buildID = p.buildID;
		finalRuleset = p.finalRuleset;
		finalCV = p.finalCV;
		finalCVSetSize = p.finalCVSetSize;
		finalCVRepetitions = p.finalCVRepetitions;

		u=p.u;
		v=p.v;        
		cutoffPermutations=p.cutoffPermutations;
		cutoffAlpha=p.cutoffAlpha;
		contrastAttr=p.contrastAttr;    
		contrastAttrThreshold=p.contrastAttrThreshold;            
		cutoffAngle=p.cutoffAngle;
		wekaClassifierMode=p.wekaClassifierMode;
		useGainRatio=p.useGainRatio;
		maxConnectionDepth=p.maxConnectionDepth;
		qMethod=p.qMethod;
		useComplexQuality=p.useComplexQuality;  
		useDiversityMeasure=p.useDiversityMeasure;

		return;
	}    
	//  ****************************************************
	private boolean setAttrSize(int attributes)
	{
		//exact number of attributes
		projectionSizeAttr=(int)projectionSize;

		//if projectionSize<=1 it means fraction of attributes
		if(projectionSize<=1)    
			projectionSizeAttr=(int)(attributes*projectionSize);

		if(projectionSizeAttr>=attributes){
			System.err.println("Error parameters! projectionSizeAttr >= number of attributes");
			return false;
		}

		return true;
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

}
