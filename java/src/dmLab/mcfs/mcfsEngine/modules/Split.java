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

import dmLab.array.Array;
import dmLab.array.FArray;
import dmLab.array.functions.SelectFunctions;
import dmLab.classifier.Classifier;
import dmLab.classifier.PredictionResult;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;

public class Split
{
    private MCFSParams mcfsParams;    
    private SelectFunctions selectFunctions;
    
    //*************************************    
    public Split(MCFSParams mcfsParams, Random random)
    {
        this.mcfsParams = mcfsParams;
        selectFunctions = new SelectFunctions(random);
    }
    //*************************************    
    public PredictionResult splitLoop(Classifier classifier, FArray inputArray,
            AttributesRI attrRI[], AttributesID attrIDependencies)
    {
        //shuffle input columns since WEKA always select first attribute if two are identical 
    	inputArray = selectFunctions.shuffleColumns(inputArray);                  
        
        if (mcfsParams.verbose) 
        	System.out.println("Splitting input table (ratio: "+mcfsParams.splitRatio+")...");

        int[] splitMask = selectFunctions.getSplitMaskUniform(inputArray, mcfsParams.splitRatio);
        Array trainTestArrays[] = SelectFunctions.split(inputArray, splitMask);
        FArray trainArray = (FArray)trainTestArrays[0];
        FArray testArray = (FArray)trainTestArrays[1];

        if (mcfsParams.debug){            	
        	System.out.println("trainArray# "+trainArray.info());
        	System.out.println("testArray# "+testArray.info());
        }

        if (mcfsParams.verbose) 
        	System.out.println("Training phase...");
        classifier.train(trainArray);
        
        //if (mcfsParams.debug) 
        //	System.out.println("### DEBUG ### \n"+classifier.toString()+"\n");
 
        if (mcfsParams.verbose) 
        	System.out.println("Testing phase...");
        classifier.test(testArray);
        
        PredictionResult predResult = classifier.getPredResult();
        
        //add Importances
        classifier.add_RI(attrRI);
        
        //add ID edges 
        if(attrIDependencies!=null)
            classifier.add_ID(attrIDependencies, mcfsParams);               
        //System.out.println("### DEBUG ### \n"+attrRI[0].toString());
            
        return predResult;
    }
    //*************************************************
}
