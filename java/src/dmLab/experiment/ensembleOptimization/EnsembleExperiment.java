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
package dmLab.experiment.ensembleOptimization;

import java.util.Random;

import dmLab.classifier.Classifier;
import dmLab.classifier.ensemble.EnsembleClassifier;
import dmLab.experiment.classification.ClassificationBody;
import dmLab.experiment.classification.ClassificationParams;
import dmLab.utils.cmatrix.AccuracyMeasure;

public class EnsembleExperiment
{
    private ClassificationBody classification;
    public double wAcc;
    public double Acc;
    //**************************************
    //*** main method
    public static void main(String[] args)
    {
        EnsembleExperiment exp=new EnsembleExperiment();
        exp.run("data2_train_mcfs_out.arff",new float[] {1f,1f,1f,1f,1f,1f,1f,1f,1f});        
    }
    //****************************************  
    public EnsembleExperiment()
    {
        classification=new ClassificationBody(new Random(System.currentTimeMillis()));                
        classification.loadParameters("classification.run");
        classification.classParams.debug=false;
        classification.classParams.verbose=false;
        classification.classParams.saveClassifier=false;
        classification.classParams.savePredictionResult=false;
        classification.classParams.validationType=ClassificationParams.VALIDATION_CV;               
        //classification.classParams.folds=3;
        //classification.classParams.repetitions=5;
        classification.classParams.classifier=Classifier.ENSEMBLE;
    }
    //***************************************
    public float run(String inputFileName,float[] _weights)
    {
        classification.classParams.inputFileName=inputFileName;
        classification.createClassifier();
        if(!((EnsembleClassifier)classification.classifier).setWeights(_weights))
        {
            System.err.println("Error in weights setting!");
            return -1;
        }
        classification.classifier.params.debug=false;
        classification.classifier.params.verbose=false;
        classification.loadArrays();
        classification.classifier.params.check(classification.inArray);

        wAcc = classification.run();
        Acc = classification.resultConfMatrix.calcMeasure(AccuracyMeasure.ACC);
        
        if(classification.classParams.verbose)
        	System.out.println(classification.toStringResults());                 
        else
            System.out.println("wAcc: "+wAcc);
        return (float)wAcc;
    }
    //****************************************    
}
