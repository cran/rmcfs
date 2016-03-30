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
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.mcfs.cutoffMethods.Cutoff;
import dmLab.mcfs.mcfsEngine.arrays.MCFSArrays;
import dmLab.utils.FileUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.dataframe.DataFrame;

public abstract class MCFSFramework implements Runnable 
{
    public MCFSParams mcfsParams;
    public MCFSArrays mcfsArrays;
    public GlobalStats globalStats;
    
    public String experimentName;
    public boolean saveResutFiles = true;
    
    protected Random random;
//	*************************************
	public MCFSFramework(Random random)
	{
		this.random = random;
        experimentName="";
        mcfsArrays = new MCFSArrays();
	}
//	*************************************
    public void setParameters(MCFSParams mcfsParams)
    {
        this.mcfsParams=mcfsParams;  
    }
//  *************************************
    public boolean loadArrays()
    {    
        if(!mcfsArrays.loadArrays(mcfsParams))
            return false;
        
        return true;
    }
//  *************************************
	public boolean loadParameters(String runFileName)
	{
		mcfsParams=new MCFSParams();
        if(mcfsParams.load("",runFileName) == false)
        {
            System.err.println("Error loading configuration file. File: " + runFileName);
            return false;
        }
        return true;
	}
	//*************************************	
	//according to the bug in weka i need to delete one more time these files
	private void cleanTmpWekaFiles()
	{
        for(int i=0; i<mcfsParams.threadsNumber;i++)
        {
    	    String trainFilePath = mcfsParams.resFilesPATH + "C" + i + "_" + WekaClassifier.ARFF_TRAIN_FILE;
            String testFilePath = mcfsParams.resFilesPATH + "C" + i + "_" +WekaClassifier.ARFF_TEST_FILE;
            File trainFile=new File(trainFilePath);
            File testFile=new File(testFilePath);
            trainFile.delete();
            testFile.delete();
        }
	}
	//*************************************
	public abstract void run();
//  *************************************
    public boolean run(MCFSParams mcfsParams)
    {
    	this.mcfsParams=mcfsParams.clone(); 	
        if(!loadArrays())
        	return false;
        
        run();
        return true;
    }
	//***************************************
    public ConfusionMatrix runExperiment() 
    {       
        return runExperiment(mcfsArrays.sourceArray);
    }
    //*********************************	
	public ConfusionMatrix runExperiment(FArray inputArray)
	{		
        if(mcfsParams.verbose)
            System.out.println(mcfsParams.toString());
        
        if(!mcfsParams.check(inputArray))
            return null;       

        System.out.println("MCFS-ID: "+Params.booleanParamToString(mcfsParams.buildID==true, "ID-Graph"));
        System.out.println("MCFS-ID: "+Params.booleanParamToString(mcfsParams.finalCV==true, "finalCV"));
        System.out.println("MCFS-ID: "+Params.booleanParamToString(mcfsParams.finalRuleset==true, "finalRuleset"));
        System.out.println("MCFS-ID: "+Params.booleanParamToString(mcfsParams.splitSetSize>0, "Input data size limitation"));        
        System.out.println("MCFS-ID: "+Params.booleanParamToString(mcfsParams.balanceRatio>0, "Classes balancing"));        
        
        if(mcfsParams.balanceRatio>0)
        	SelectFunctions.getBalancedClassSizes(mcfsArrays.sourceArray,mcfsParams.balanceRatio,true);
        
		globalStats = new GlobalStats();
		globalStats.init(inputArray, mcfsParams, experimentName);

        Cutoff cutoff = new Cutoff(mcfsParams);
        globalStats.setCutoff(cutoff);
        if(mcfsParams.contrastAttr){ 
    		System.out.println("Adding Contrast Attributes...");
    		ExtFunctions.addContrastAttributes(inputArray);		
    		System.out.println("New input array size: attributes: "+inputArray.colsNumber()+ " events: "+inputArray.rowsNumber());	
        }
        		
		final int threadsNumber = mcfsParams.threadsNumber;
		
		System.out.println("Starting MCFS-ID Single Experiment. Projections(s) = " + mcfsParams.projections + " Splits(t) = " + mcfsParams.splits);
	    long start=System.currentTimeMillis();
	    System.out.println("Start: " +(new Date(start)).toString());	    

		MCFSJob mcfsJob[]=new MCFSJob[threadsNumber];
		Thread jobs[]=new Thread[threadsNumber];
		for(int i=0; i<threadsNumber ;i++){
		    mcfsJob[i] = new MCFSJob(i, mcfsParams, inputArray, globalStats);
		    //each job must to have its own Random object but related to the original seed
		    mcfsJob[i].init(new Random(random.nextLong()));
		    jobs[i]=new Thread(mcfsJob[i]);
	        jobs[i].setPriority(8);
		}
		System.out.println("Starting: " +threadsNumber + " threads.");
		for(int i=0;i<jobs.length;i++){
		    jobs[i].start();
	        System.out.println("Thread "+i+ " Started...");
	    }
	    try{
	        for(int i=0;i<jobs.length;i++)
	            jobs[i].join();
	    }
	    catch(InterruptedException e){
	         e.printStackTrace();
	    }
	    System.out.println("All " +threadsNumber + " threads are finished.");
	    for(int i=0; i<threadsNumber ;i++)
	        mcfsJob[i].finish();
	    
        long stop=System.currentTimeMillis();
        ConfusionMatrix confusionMatrix= globalStats.getConfusionMatrix();
        
        float experimentTime=(stop-start)/1000.0f;
        System.out.println("stop: " +(new Date(stop)).toString());
        System.out.println(confusionMatrix.toString());
        System.out.println(confusionMatrix.statsToString(4));

        //save ID
        if(globalStats.getAttrConnections()!=null){
        	globalStats.getAttrConnections().findMinMaxID();        
	        if(saveResutFiles)
	        	globalStats.getAttrConnections().save(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_CONNECTIONS);
        }
        
        //save distances
        DataFrame distances = globalStats.getDistances();
        if(saveResutFiles)
        	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_DISTANCE, distances.toString());
        
        //save confusion matrix
        String matrix = confusionMatrix.toString(false, true, false, ",");
        if(saveResutFiles)
        	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_MATRIX, matrix);        
                
        //save cutoff table
        double minRI = cutoff.calcCutoff(globalStats.getAttrImportances()[0]);
        System.out.println("Minimal important (based on all cutoff methods) RI = " + GeneralUtils.format(minRI,7));
        if(saveResutFiles)
        	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_CUTOFF, cutoff.toString());
        
        //save top ranking
        int mainMeasureIndex = globalStats.getAttrImportances()[0].mainMeasureIdx;
        Ranking topRanking = globalStats.getAttrImportances()[0].getTopRanking(mainMeasureIndex, (float)minRI);
        if(topRanking != null) {
        	System.out.println("Size of important (based on all cutoff methods) attributes set = "+topRanking.size());
            if(saveResutFiles)
            	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+"_"+MCFSParams.FILESUFIX_TOPRANKING, topRanking.toString());        		
        }
        
        System.out.println("*** MCFS-ID single experiment has been done! ***");
        System.out.println("*** MCFS-ID single experiment time (s.): "+experimentTime+" ***");        
        cleanTmpWekaFiles();
        
        //save parameters
        if(saveResutFiles)
        	FileUtils.saveString(mcfsParams.resFilesPATH+experimentName+".run", mcfsParams.toString());        		
        
        return confusionMatrix;
	}
// *************************************
	
}
