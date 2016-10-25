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

import java.io.BufferedWriter;
import java.util.Random;

import dmLab.array.FArray;
import dmLab.classifier.Classifier;
import dmLab.classifier.j48.J48Classifier;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.mcfsEngine.modules.Projection;
import dmLab.utils.ArrayUtils;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.statList.StatsList;

public class MCFSJob implements Runnable
{    
	private int jobId;

	protected Classifier classifier;
	protected Projection projection;
	protected ConfusionMatrix localMatrix;
	protected BufferedWriter accFile;

	protected MCFSParams mcfsParams;    
	protected FArray inputArray;
	protected AttributesRI localImportance[];
	protected AttributesID localAttrID;
	protected StatsList localSplitsStats;

	protected GlobalStats globalStats;
	//************************************    
	public MCFSJob(int id, MCFSParams mcfsParams, FArray inputArray, GlobalStats globalStats)
	{
		jobId = id;
		this.globalStats = globalStats;
		this.mcfsParams = mcfsParams;
		this.inputArray = inputArray;
		localSplitsStats = new StatsList();
		localImportance = globalStats.initImportances(mcfsParams, inputArray);
		if(mcfsParams.buildID)
			localAttrID = new AttributesID(globalStats.attrNames, true, false);
	}
	//************************************
	public boolean init(Random random)
	{ 
		createClassifier();
		projection = new Projection(mcfsParams, random);
		if(inputArray.isTargetNominal())
			localMatrix = new ConfusionMatrix(inputArray.getColNames(true)[inputArray.getDecAttrIdx()],inputArray.getDecValuesStr());
		else
			localMatrix = null;
		
		return true;
	}
	//************************************
	public boolean finish()
	{
		classifier.finish();
		return true;
	}
	//************************************
	public void setJobId(int id)
	{
		jobId=id;
	}
	//************************************
	private boolean createClassifier()
	{
		if(!ArrayUtils.valueIn(mcfsParams.model, new int[]{Classifier.AUTO, Classifier.J48,Classifier.M5,Classifier.ADX,Classifier.SLIQ})){							
			System.err.println("@@@ Thread: "+jobId+" Error. Unknown defined classifier.");
			return false;
		}
			
		classifier = Classifier.getClassifier(mcfsParams.model);
	
		if(!classifier.params.load(mcfsParams.classifierCfgPATH,classifier.label))
			return false;

		classifier.init();
		//classifier.params.verbose=mcfsParams.verbose;
		//classifier.params.debug=mcfsParams.debug;                
		classifier.setTempPath(mcfsParams.resFilesPATH);
		classifier.setId(jobId);

		if(classifier instanceof J48Classifier)
			((J48Classifier)classifier).mode = mcfsParams.wekaClassifierMode;

		if(!classifier.params.check(inputArray))
			return false;

		//System.out.println("@@@ Thread: "+jobId+" "+classifier.params.toString());
		return true;
	}
	//************************************    
	public void run() 
	{
		boolean keepLoop=true;
		int loopIndex=0;
		while(keepLoop){
			ConfusionMatrix matrix = projection.projectionLoop(classifier, inputArray, localImportance, localAttrID);
			if(localMatrix != null){
				localMatrix.add(matrix);
			}
			localSplitsStats.add(projection.getSplitsStats());
			//every saveResultInterval update global stats
			if(loopIndex%mcfsParams.progressInterval==0 && loopIndex!=0){
				//System.out.println("@@@ Thread: " + jobId + " updating global stats *** ");
				//save from local to global                
				keepLoop = globalStats.update(jobId, localMatrix, localImportance, localAttrID);

				for(int i=0;i<localImportance.length;i++)
					if(localImportance[i]!=null)
						localImportance[i].initImportances();

				if(localMatrix != null){
					localMatrix.cleanMatrix();
				}
				
				if(localAttrID!=null)
					localAttrID.init();
			}
			loopIndex++;
		}
		//update acc_wAcc stats after job is finished
		globalStats.updateSplitsStats(localSplitsStats);
	}
	//************************************ 
	public StatsList getJobSplitsStats(){
		return localSplitsStats;
	}
	//************************************ 
}
