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
import dmLab.utils.dataframe.ColumnMetaInfo;
import dmLab.utils.dataframe.DataFrame;

public class MCFSFinalCV {

	protected int[] algorithms;	
	protected Random random;
	
	public ConfusionMatrix[] j48ConfMatrix;
	private ConfusionMatrix j48ConfMatrixTmp;
	
	//************************************
	public MCFSFinalCV(int[] algorithms, Random random){
		this.algorithms = algorithms;
		this.random = random;
	}
	//************************************
	public DataFrame run(FArray array, AttributesRI importances, int[] size, int cvFolds, int setSize, int repetitions)
	{
		this.j48ConfMatrix = new ConfusionMatrix[size.length];

		SelectFunctions selectFunctions = new SelectFunctions(random);
		if(array.rowsNumber() <= setSize)
			repetitions = 1;

		DataFrame result_df=null;		
		for(int i=0;i<size.length;i++){
			int currSize = size[i];
			if(currSize > 0 && currSize < array.colsNumber()){
				FArray topRankingArray = (FArray)SelectFunctions.selectColumns(array, SelectFunctions.getColumnsMask(array, importances, currSize));
		    	//System.out.println("***topRankingArray***\n"+topRankingArray.toString());		
				System.out.println("*** "+topRankingArray.rowsNumber()+" objects and "+(topRankingArray.colsNumber()-1)+" input attributes ***");				
				DataFrame step_df=null;
				for(int j=0; j<repetitions; j++){
					FArray rep_array = (FArray)selectFunctions.selectRowsRandom(topRankingArray, setSize);
					if(repetitions > 1)
						System.out.println("*** CV Repetition = " + (j+1) + " ["+rep_array.rowsNumber() +" x "+rep_array.colsNumber()+"]");
					
					DataFrame rep_df = singleCV(rep_array, Integer.toString(currSize), cvFolds);
					j48ConfMatrix[i] = j48ConfMatrixTmp;					
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
		j48ConfMatrixTmp = null;
		return result_df;
	}
	//************************************
	private DataFrame singleCV(FArray array, String label, int cvFolds)
	{
		j48ConfMatrixTmp = null;		
		DataFrame df;
		if(array.isTargetNominal()){
			df = new DataFrame(algorithms.length, new String[]{"label","algorithm","acc","wacc"});
			df.setColTypes(new short[]{ColumnMetaInfo.TYPE_NOMINAL,ColumnMetaInfo.TYPE_NOMINAL,ColumnMetaInfo.TYPE_NUMERIC,ColumnMetaInfo.TYPE_NUMERIC});
		}else{
			df = new DataFrame(algorithms.length, new String[]{"label","algorithm","pearson","MAE","RMSE","SMAPE"});
			df.setColTypes(new short[]{ColumnMetaInfo.TYPE_NOMINAL,ColumnMetaInfo.TYPE_NOMINAL,
					ColumnMetaInfo.TYPE_NUMERIC,ColumnMetaInfo.TYPE_NUMERIC,ColumnMetaInfo.TYPE_NUMERIC,ColumnMetaInfo.TYPE_NUMERIC});			
		}
		
		ClassificationBody classification = new ClassificationBody(random);
		classification.setParameters(new ClassificationParams());
		classification.classParams.verbose = false;
		classification.classParams.saveClassifier = false;
		classification.classParams.savePredictionResult = false;		
		classification.classParams.folds = cvFolds;
		classification.classParams.repetitions = 1;
				
		for(int i=0;i<algorithms.length;i++){
			classification.classParams.model=algorithms[i];
			classification.initClassifier();
			if(i == 0)
				System.out.print("Evaluating model performance using "+cvFolds+" fold CV. Model: "+Classifier.int2label(algorithms[i]));
			else
				System.out.print(", "+Classifier.int2label(algorithms[i]));
				
			PredictionResult predResult = classification.runCV(array);
			df.set(i, 0, label);
			df.set(i, 1, Classifier.int2label(algorithms[i]));
			if(array.isTargetNominal()){	
				ConfusionMatrix matrix = predResult.getConfusionMatrix();
				if(algorithms[i] == Classifier.J48)
					j48ConfMatrixTmp = matrix;
				df.set(i, 2, (float)matrix.calcMeasure(QualityMeasure.ACC));
				df.set(i, 3, (float)matrix.calcMeasure(QualityMeasure.WACC));
			}else{
				df.set(i, 2, (float)predResult.getPredQuality(QualityMeasure.PEARSON));
				df.set(i, 3, (float)predResult.getPredQuality(QualityMeasure.MAE));
				df.set(i, 4, (float)predResult.getPredQuality(QualityMeasure.RMSE));
				df.set(i, 5, (float)predResult.getPredQuality(QualityMeasure.SMAPE));
			}
		}
		System.out.println();
		return df;
	}
	//************************************
}
