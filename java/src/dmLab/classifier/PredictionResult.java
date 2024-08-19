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
package dmLab.classifier;

import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.cmatrix.QualityMeasure;
import dmLab.utils.statFunctions.StatFunctions;

public class PredictionResult{
	public ConfusionMatrix confusionMatrix;
	public Prediction predictions[];
	public final int type;

	//  *************************************
	public PredictionResult(int type){
		this.type = type;
	}
	//  *************************************
	public ConfusionMatrix getConfusionMatrix()
	{
		return confusionMatrix;
	}
	//*****************************************
	public Prediction[] getPredictions()
	{
		return predictions;  
	}
	//  *****************************************
	public String predictionsToString()
	{
		StringBuffer tmp=new StringBuffer();
		for(int i=0;i<predictions.length;i++)            
			tmp.append(predictions[i].toString());

		return tmp.toString(); 
	}
	//  *****************************************
	public double getPredQuality(){
		if(type == Classifier.MODEL_CLASSIFIER){
			return getPredQuality(QualityMeasure.WACC);
		}else if(type == Classifier.MODEL_PREDICTOR){
			return getPredQuality(QualityMeasure.PEARSON);
		}else
			return Double.NaN;			
	}
	//  *****************************************
	public double getPredQuality(int measure){
		
		if(type == Classifier.MODEL_CLASSIFIER){
			if(measure == QualityMeasure.WACC)
				return QualityMeasure.calcWAcc(confusionMatrix.getMatrix());
			else if(measure == QualityMeasure.ACC)
				return QualityMeasure.calcAcc(confusionMatrix.getMatrix());
			else
				return Double.NaN;
		}else{			
			double[] x = new double[predictions.length];
			double[] y = new double[predictions.length];
			for(int i = 0; i< x.length; i++){
				x[i] = (double)predictions[i].getRealValue();	
				y[i] = (double)predictions[i].getPredictedValue();				
			}
			
			if(measure == QualityMeasure.PEARSON)
				return StatFunctions.pearson(x, y);
			else if(measure == QualityMeasure.MAE)
				return StatFunctions.mae(x, y);
			else if(measure == QualityMeasure.RMSE)
				return StatFunctions.rmse(x, y);
			else if(measure == QualityMeasure.SMAPE)
				return StatFunctions.smape(x, y);
			return Double.NaN;
		}
	}
	//  *****************************************
	public String toString()
	{
		if(type == Classifier.MODEL_PREDICTOR && predictions != null){
			StringBuffer tmp = new StringBuffer();
			tmp.append("Pearson Correlation: " + getPredQuality(QualityMeasure.PEARSON)).append("\n");
			tmp.append("MAE: " + getPredQuality(QualityMeasure.MAE)).append("\n");
			tmp.append("RMSE: " + getPredQuality(QualityMeasure.RMSE)).append("\n");
			tmp.append("SMAPE: " + getPredQuality(QualityMeasure.SMAPE)).append("\n");
			return tmp.toString();
		}else if(type == Classifier.MODEL_CLASSIFIER && confusionMatrix != null){
			StringBuffer tmp = new StringBuffer();
			tmp.append(confusionMatrix.toString(true, true, false, "\t"));
			tmp.append(confusionMatrix.statsToString(4, true));
			return tmp.toString();
		}else {
			return "";
		}
	}
	//  *****************************************
}
