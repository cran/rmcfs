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
package dmLab.mcfs.mcfsEngine.modules;

import java.util.Random;

import dmLab.array.FArray;
import dmLab.array.functions.SelectFunctions;
import dmLab.classifier.Classifier;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.utils.cmatrix.AccuracyMeasure;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.statList.StatsList;
import dmLab.utils.statList.StatsObject;

public class Projection
{    
    private MCFSParams mcfsParams;
    private Split split;
    private ConfusionMatrix confusionMatrix;
    private StatsList splitsStats;
    private SelectFunctions selectFunctions;
    
    //*************************************
    public Projection(MCFSParams mcfsParams, Random random)
    {
        this.mcfsParams=mcfsParams;
        split=new Split(mcfsParams, random);
        confusionMatrix=null;
        selectFunctions = new SelectFunctions(random);
    }
    //*************************************
    public ConfusionMatrix projectionLoop(Classifier classifier, FArray inputArray,
            AttributesRI attrRI[], AttributesID attrIDependencies)
    {
        splitsStats = new StatsList();
        splitsStats.addHeader("acc,wAcc,errors",',');
        
        confusionMatrix=new ConfusionMatrix(inputArray.getColNames(true)[inputArray.getDecAttrIdx()],
        		inputArray.getDecValuesStr());

        FArray projectionArray=null;
        int projectionSize=mcfsParams.projectionSizeAttr;
        
        if(projectionSize>=inputArray.colsNumber())
            projectionSize=inputArray.colsNumber()-1;
            
        int [] colMask = selectFunctions.getColumnsMask(inputArray, projectionSize);
        projectionArray = (FArray)SelectFunctions.selectColumns(inputArray, colMask);

        //add projection list of all selected attributes
        for(int i=0; i<attrRI.length; i++)
        	attrRI[i].addProjections(projectionArray);

        if (mcfsParams.verbose) 
        	System.out.println("projectionArray# "+projectionArray.info());
        
        //SPLIT LOOP
        for (int j = 0; j < mcfsParams.splits; j++){
            if (mcfsParams.verbose) 
            	System.out.println("*** SPLIT: " + j + " *** ");            
            FArray balancedArray=projectionArray;
            if(mcfsParams.balanceRatio>0){           
                if (mcfsParams.verbose)	
                	System.out.println("Balancing table...");          
                balancedArray = selectFunctions.balanceClasses(projectionArray, mcfsParams.balanceRatio);
                if (mcfsParams.verbose) 
                	System.out.println("balancedArray# "+balancedArray.info());
            }
            
            FArray limitedSizeArray=balancedArray;
            if(mcfsParams.splitSetSize>0){
                if (mcfsParams.verbose) 
                	System.out.println("Decreasing number of objects...");
            	limitedSizeArray = (FArray)selectFunctions.selectRowsRandom(balancedArray, (float)mcfsParams.splitSetSize);
            	if (mcfsParams.verbose) 
            		System.out.println("limitedSizeArray# "+limitedSizeArray.info());
            }

            //single confusion matrix
            ConfusionMatrix matrix=split.splitLoop(classifier, limitedSizeArray, attrRI, attrIDependencies);            
            confusionMatrix.add(matrix);
            
            float[] stats = {(float)AccuracyMeasure.calcAcc(matrix.getMatrix()),
            		(float)AccuracyMeasure.calcWAcc(matrix.getMatrix()),
            		AccuracyMeasure.errors(matrix.getMatrix())};            
            splitsStats.add(new StatsObject(stats));           
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
