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
package dmLab.mcfs.mcfsEngine.modules;

import java.util.Random;
import dmLab.array.Array;
import dmLab.array.FArray;
import dmLab.array.functions.SelectFunctions;
import dmLab.classifier.Classifier;
import dmLab.classifier.PredictionResult;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.utils.ArrayUtils;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.cmatrix.QualityMeasure;
import dmLab.utils.statList.StatsList;
import dmLab.utils.statList.StatsObject;

public class Projection
{    
    private MCFSParams mcfsParams;
    private Split split;
    private StatsList splitsStats;
    private SelectFunctions selectFunctions;    
    //*************************************
    public Projection(MCFSParams mcfsParams, Random random)
    {
        this.mcfsParams = mcfsParams;
        split = new Split(mcfsParams, random);
        selectFunctions = new SelectFunctions(random);
    }
    //*************************************
    public ConfusionMatrix projectionLoop(Classifier classifier, FArray inputArray,
            AttributesRI attrRI[], AttributesID attrIDependencies)
    {    	
        splitsStats = new StatsList();
     
        ConfusionMatrix confusionMatrix;
        if(inputArray.isTargetNominal()){
        	confusionMatrix = new ConfusionMatrix(inputArray.getColNames(true)[inputArray.getDecAttrIdx()],inputArray.getDecValuesStr());
            splitsStats.addHeader("acc,wAcc,errors",',');
        }
        else{
            confusionMatrix = null;
            splitsStats.addHeader("pearson,MAE,RMSE,SMAPE",',');
        }
                
        int projectionSize = mcfsParams.projectionSizeValue;
        
        if(projectionSize>=inputArray.colsNumber())
            projectionSize=inputArray.colsNumber()-1;
            
        int[] colMask = selectFunctions.getColumnsMask(inputArray, projectionSize);
        int[] colIdx = Array.colMask2colIdx(colMask);
        
        //add projection list of all selected attributes
        for(int i=0; i<attrRI.length; i++)
        	attrRI[i].addProjections(inputArray.getColNames(colIdx, false));

        //SPLIT LOOP        
        for (int j = 0; j < mcfsParams.splits; j++){
            int rowIdx[];
            if(mcfsParams.splitSetClassSizes != null){
                //System.out.println("*** MDR DEBUG *** mcfsParams.splitSetClassSizes# "+Arrays.toString(mcfsParams.splitSetClassSizes));
                rowIdx = selectFunctions.balanceClassesIdx(inputArray, mcfsParams.splitSetClassSizes);
                //System.out.println("*** MDR DEBUG *** selected rows# "+rowIdx.length+"\n");                                                
            }else {
            	rowIdx = ArrayUtils.seq(0, inputArray.rowsNumber());
            }
            
        	//System.out.println("*** MDR DEBUG *** selected rows# "+rowIdx.length+"\n");            	            
            FArray splitArray = inputArray.cloneByIdx(colIdx, rowIdx);
        	//System.out.println("*** MDR DEBUG *** limitedSizeArray# "+splitArray.info());
        	//System.out.println("*** MDR DEBUG *** limitedSizeArray# "+splitArray.toString());        	
        	//System.out.println("*** MDR DEBUG *** limitedSizeArray# "+Arrays.toString(splitArray.getDecValuesStr()));        	
        	//System.out.println("*** MDR DEBUG *** selected rows# "+Arrays.toString(splitArray.getDecisionValuesTableSize()) +"\n");
        	
            //single prediction and confusion matrix
            PredictionResult predRes = split.splitLoop(classifier, splitArray, attrRI, attrIDependencies);
            if(inputArray.isTargetNominal()){            
	            ConfusionMatrix matrix = predRes.getConfusionMatrix();            
	            confusionMatrix.add(matrix);	            
	            float[] stats = {(float)QualityMeasure.calcAcc(matrix.getMatrix()),
	            		(float)QualityMeasure.calcWAcc(matrix.getMatrix()),
	            		QualityMeasure.errors(matrix.getMatrix())};
	            splitsStats.add(new StatsObject(stats));
            }else{
	            float[] stats = {(float)predRes.getPredQuality(QualityMeasure.PEARSON),
	            		(float)predRes.getPredQuality(QualityMeasure.MAE),
	            		(float)predRes.getPredQuality(QualityMeasure.RMSE),
	            		(float)predRes.getPredQuality(QualityMeasure.SMAPE)};
	            splitsStats.add(new StatsObject(stats));            	
            }
            
        }//end for

        return confusionMatrix;
    }
    //*************************************
    public StatsList getSplitsStats()
    {
    	return splitsStats;
    }    
    //*************************************
}
