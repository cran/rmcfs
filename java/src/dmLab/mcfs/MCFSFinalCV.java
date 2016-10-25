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

import java.util.Random;

import dmLab.array.FArray;
import dmLab.array.functions.SelectFunctions;
import dmLab.classifier.Classifier;
import dmLab.classifier.PredictionResult;
import dmLab.experiment.classification.ClassificationBody;
import dmLab.experiment.classification.ClassificationParams;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.utils.cmatrix.QualityMeasure;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.dataframe.Column;
import dmLab.utils.dataframe.DataFrame;

public class MCFSFinalCV {

	protected int[] algorithms;
	protected Random random;
	
	//************************************
	public MCFSFinalCV(int[] algorithms, Random random){
		this.algorithms = algorithms;
		this.random = random;
	}
	//************************************
	public DataFrame run(FArray array, AttributesRI importances, int[] size, int cvFolds, int setSize, int repetitions)
	{
		SelectFunctions selectFunctions = new SelectFunctions(random);
		if(array.rowsNumber() <= setSize)
			repetitions = 1;

		DataFrame result_df=null;		
		for(int i=0;i<size.length;i++){
			int currSize = size[i];
			if(currSize>0 && currSize<array.colsNumber()-1){
				FArray topRankingArray = (FArray)SelectFunctions.selectColumns(array, importances, currSize);
		    	//System.out.println("***topRankingArray***\n"+topRankingArray.toString());		

				System.out.println("*** Running CV experiment on top "+topRankingArray.colsNumber()+" attributes and "+topRankingArray.rowsNumber()+" rows ***");				
				DataFrame step_df=null;
				for(int j=0;j<repetitions;j++){
					FArray rep_array = (FArray)selectFunctions.selectRowsRandom(topRankingArray, setSize);

					if(repetitions > 1)
						System.out.println("*** CV Repetition = " + (j+1) + ". Array size: rows="+rep_array.rowsNumber() +" cols="+rep_array.colsNumber());
					
					DataFrame rep_df = singleCV(rep_array, Integer.toString(currSize), cvFolds);
					if(step_df==null)
						step_df = rep_df;
					else
						step_df.mathOperation(rep_df, "+");
				}
				step_df.mathOperation(repetitions, "/");

				if(result_df==null)
					result_df = step_df;
				else
					result_df.rbind(step_df);

			}
		}
		return result_df;
	}
	//************************************
	private DataFrame singleCV(FArray array, String label, int cvFolds)
	{
		DataFrame df;
		if(array.isTargetNominal()){
			df = new DataFrame(algorithms.length, new String[]{"label","algorithm","acc","wacc"});
			df.setColTypes(new short[]{Column.TYPE_NOMINAL,Column.TYPE_NOMINAL,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC});
		}else{
			df = new DataFrame(algorithms.length, new String[]{"label","algorithm","pearson","MAE","RMSE","SMAPE"});
			df.setColTypes(new short[]{Column.TYPE_NOMINAL,Column.TYPE_NOMINAL,
					Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC});			
		}
		
		ClassificationBody classification = new ClassificationBody(random);
		classification.setParameters(new ClassificationParams());
		classification.classParams.debug = false;
		classification.classParams.verbose = false;
		classification.classParams.saveClassifier = false;
		classification.classParams.savePredictionResult = false;
		classification.classParams.classifierCfgPATH = "";
		classification.classParams.folds = cvFolds;
		classification.classParams.repetitions = 1;
		
		for(int i=0;i<algorithms.length;i++){
			classification.classParams.model=algorithms[i];
			classification.initClassifier();
			System.out.println("Running CV "+cvFolds+" fold. Algorithm: "+Classifier.int2label(algorithms[i]));
			
			PredictionResult predResult = classification.runCV(array);
			df.set(i, 0, label);
			df.set(i, 1, Classifier.int2label(algorithms[i]));
			if(array.isTargetNominal()){	
				ConfusionMatrix matrix = predResult.getConfusionMatrix();	        
				df.set(i, 2, (float)matrix.calcMeasure(QualityMeasure.ACC));
				df.set(i, 3, (float)matrix.calcMeasure(QualityMeasure.WACC));
			}else{
				df.set(i, 2, (float)predResult.getPredQuality(QualityMeasure.PEARSON));
				df.set(i, 3, (float)predResult.getPredQuality(QualityMeasure.MAE));
				df.set(i, 4, (float)predResult.getPredQuality(QualityMeasure.RMSE));
				df.set(i, 5, (float)predResult.getPredQuality(QualityMeasure.SMAPE));
			}
		}
		return df;
	}
	//************************************
}
