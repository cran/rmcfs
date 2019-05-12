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
package dmLab.mcfs.mcfsEngine.framework;

import java.io.File;
import java.util.Date;
import java.util.Random;

import dmLab.array.FArray;
import dmLab.array.functions.ExtFunctions;
import dmLab.array.functions.SelectFunctions;
import dmLab.classifier.Params;
import dmLab.classifier.WekaClassifier;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.mcfs.cutoffMethods.Cutoff;
import dmLab.mcfs.mcfsEngine.MCFSAutoParams;
import dmLab.mcfs.mcfsEngine.arrays.MCFSArrays;
import dmLab.utils.FileUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.cmatrix.ConfusionMatrix;

public abstract class MCFSFramework implements Runnable 
{
	protected MCFSParams mcfsParams;
	public MCFSArrays mcfsArrays;
	public GlobalStats globalStats;

	public String chartTitle;
	public String experimentName;

	protected Random random;
	//*************************************
	public MCFSFramework(Random random)
	{
		this.random = random;
		experimentName = "";
		chartTitle = "MCFS-ID Progress";
	}
	//*************************************
	public boolean loadArrays()
	{    
		if(mcfsArrays == null){
			mcfsArrays = new MCFSArrays();
			if(!mcfsArrays.loadArrays(mcfsParams))
				return false;
		}
		return true;
	}
	//*************************************
	public boolean run(MCFSParams mcfsParams)
	{
		this.mcfsParams = mcfsParams.clone();
		if(!loadArrays())
			return false;
		
		if(experimentName.isEmpty())
			experimentName = mcfsParams.getExperimentName();
		
		//remove rows with empty class definition 
		FArray inputArray = mcfsArrays.sourceArray;
		mcfsArrays.sourceArray = (FArray)SelectFunctions.removeNaNRows(inputArray, inputArray.attributes[inputArray.getDecAttrIdx()].name);

		run();
		return true;
	}
	//*************************************
	public abstract void run();
	//*********************************	
	protected ConfusionMatrix runExperiment(FArray inputArray)
	{					
		if(mcfsParams.verbose)
			System.out.println("MCFSFramework.runExperiment() Running experiment: " + experimentName);
			
		if(!mcfsParams.check(inputArray))
			return null;
			
		if(mcfsParams.verbose)
			System.out.println("MCFSFramework Params: \n" + mcfsParams.toString());

		//for numeric target turn off balancing and final ruleset
		if(!inputArray.isTargetNominal()){
			mcfsParams.balance = 0;
			mcfsParams.finalRuleset = false;
		}

		if(mcfsParams.buildID)
			System.out.println("MCFS-ID param: "+Params.intParamToString((mcfsParams.buildID) ? 1 : 0, "ID-Graph"));
		if(mcfsParams.finalCV)
			System.out.println("MCFS-ID param: "+Params.intParamToString((mcfsParams.finalCV) ? 1 : 0, "finalCV"));
		if(mcfsParams.finalRuleset)
			System.out.println("MCFS-ID param: "+Params.intParamToString((mcfsParams.finalRuleset) ? 1 : 0, "finalRuleset"));
		//System.out.println("MCFS-ID parameter: "+Params.intParamToString((mcfsParams.splitSetSize > 0) ? 1 : 0, "Data size limitation"));
		if(mcfsParams.balance != 0)
			System.out.println("MCFS-ID param: "+Params.intParamToString((int)mcfsParams.balance, "balance classes"));        

		mcfsParams.tmpBalancedClassSizes = MCFSAutoParams.getBalancedClassSizes(mcfsParams.balance, inputArray);

		if(mcfsParams.cutoffMethod.equalsIgnoreCase("contrast")){
			System.out.println("Adding Contrast Attributes...");
			//ExtFunctions.addContrastAttributes(inputArray);
			int contrastColumns = Math.max(Math.round(((float)inputArray.colsNumber()-1f) * mcfsParams.contrastSize), MCFSParams.CONTRAST_ATTR_MIN);
			ExtFunctions.addColumnsUniform(inputArray, MCFSParams.CONTRAST_ATTR_NAME, contrastColumns);
			System.out.println("Data size: attributes: "+inputArray.colsNumber()+ " objects: "+inputArray.rowsNumber());
			//System.out.println(inputArray.toString());
		}

		globalStats = new GlobalStats();
		globalStats.init(inputArray, mcfsParams, experimentName, chartTitle);

		Cutoff cutoff = new Cutoff(mcfsParams);
		globalStats.setCutoff(cutoff);

		final int threadsNumber = mcfsParams.threadsNumber;
		System.out.println("Starting MCFS-ID Procedure: projectionSize(m) = " +mcfsParams.projectionSizeValue+ ", projections(s) = " + mcfsParams.projectionsValue + ", splits(t) = " + mcfsParams.splits);
		long start = System.currentTimeMillis();
		System.out.println("Start time: " +(new Date(start)).toString());	    

		MCFSJob mcfsJob[]=new MCFSJob[threadsNumber];
		Thread jobs[]=new Thread[threadsNumber];
		for(int i=0; i<threadsNumber ;i++){
			mcfsJob[i] = new MCFSJob(i, mcfsParams, inputArray, globalStats);
			//each job must to have its own Random object but related to the original seed
			mcfsJob[i].init(new Random(random.nextLong()));
			jobs[i] = new Thread(mcfsJob[i]);
			jobs[i].setPriority(8);
		}

		System.out.println("Running: " +threadsNumber + " threads...");
		globalStats.initTextProgressBar();
		for(int i=0;i<jobs.length;i++){
			jobs[i].start();
		}
		try{
			for(int i=0;i<jobs.length;i++){
				jobs[i].join();
				//System.out.println("Job "+i+ " executed...");
			}
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}		
		for(int i=0; i<threadsNumber ;i++)
			mcfsJob[i].finish();
		
		globalStats.finalizeImportances();
		ConfusionMatrix confusionMatrix = globalStats.getConfusionMatrix();
		globalStats.finalizeTextProgressBar();

		long stop = System.currentTimeMillis();
		float experimentTime = (stop-start)/1000.0f;               
		System.out.println(mcfsParams.projectionsValue * mcfsParams.splits + " trees built within " + GeneralUtils.timeIntervalFormat(experimentTime));
		System.out.println();
		if(confusionMatrix != null){
			System.out.println("Prediction Summary on a Random Subsample (st):");
			System.out.println(confusionMatrix.statsToString(2, false));
		}else{
			System.out.println("Prediction Summary on a Random Subsample (st):");
			System.out.println(globalStats.getSplitsStats().toStringSummary("pearson"));
			System.out.println(globalStats.getSplitsStats().toStringSummary("MAE"));
			System.out.println(globalStats.getSplitsStats().toStringSummary("RMSE"));
			System.out.println(globalStats.getSplitsStats().toStringSummary("SMAPE"));
			System.out.println();
		}
		
		double minRI = cutoff.calcCutoff(globalStats.getAttrImportances()[0]);
		System.out.println("Cutoff RI (based on mean cutoff value) = " + GeneralUtils.formatFloat(minRI,7));
		int mainMeasureIndex = globalStats.getAttrImportances()[0].mainMeasureIdx;
		Ranking topRanking = globalStats.getAttrImportances()[0].getTopRanking(mainMeasureIndex, (float)minRI);
		if(topRanking != null) {
			System.out.println("Important attributes (based on mean cutoff value) = "+topRanking.size());
		}
		
        if(mcfsParams.saveResultFiles){
    		if(mcfsParams.verbose)
    			System.out.println("Saving results files for experiment: " + experimentName);

    		//save RI
        	AttributesRI[] attrRI = globalStats.getAttrImportances();
            for(int j=0; j<attrRI.length; j++){
            	FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+attrRI[j].label+"_"+mcfsParams.filesufix_RI, attrRI[j].toString());
            	//attrRI[j].save(mcfsParams.resFilesPATH+experimentName+"_"+attrRI[j].label+"_"+MCFSParams.FILESUFIX_RI);
            }
            
    		//save ID
    		if(globalStats.getAttrConnections()!=null){			
    			globalStats.getAttrConnections().save(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_ID);
    		}
    					
			//save cutoff table
			FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_CUTOFF, cutoff.toString());
			
			//save top ranking if not null
			if(topRanking != null) {
				FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_TOPRANKING, topRanking.toString());
			}

			//save distances
			FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_DISTANCE, globalStats.getDistances().toString());

			//save confusion matrix or error rates
			if(confusionMatrix != null){	        		
				String matrix = confusionMatrix.toString(false, true, false, ",");
				FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_MATRIX, matrix);
			}else{
				//save pearson, MAE, RMSE, SMAPE        		
				FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+"_"+MCFSParams.FILESUFIX_PREDICTION_STATS, globalStats.getSplitsStats().toString());
			}
			
			//save parameters
			FileUtils.saveString(mcfsParams.resFilesPATH+File.separator+experimentName+".run", mcfsParams.toString());
        }
        
		cleanTmpWekaFiles();
		globalStats.closeChartFrame();

		return confusionMatrix;
	}
	// *************************************
	//regarding to the bug in weka i need to delete one more time these files
	private void cleanTmpWekaFiles(){
		
		for(int i=0; i<mcfsParams.threadsNumber;i++){
			File trainFile = new File(mcfsParams.resFilesPATH + File.separator + "C" + i + "_" + WekaClassifier.ARFF_TRAIN_FILE);
			File testFile = new File(mcfsParams.resFilesPATH + File.separator + "C" + i + "_" +WekaClassifier.ARFF_TEST_FILE);
			FileUtils.deleteFile(trainFile.getAbsolutePath());
			FileUtils.deleteFile(testFile.getAbsolutePath());
		}
	}
	//*************************************
	//remove contrast attributes from input data
	public FArray removeContrastAttr(FArray inputArray){
		if(mcfsParams.cutoffMethod.equalsIgnoreCase("contrast")){
			System.out.println("Removing Contrast Attributes...");			
			//ExtFunctions.addContrastAttributes(inputArray);
			int[] colMask = new int[inputArray.colsNumber()];
			for(int i=0; i< inputArray.attributes.length; i++){
				if(!inputArray.attributes[i].name.startsWith(MCFSParams.CONTRAST_ATTR_NAME))
					colMask[i] = 1;
			}
			inputArray = (FArray) SelectFunctions.selectColumns(inputArray, colMask);
			//System.out.println(inputArray.toString());
		}
		return(inputArray);
	}
	//*************************************	
}
