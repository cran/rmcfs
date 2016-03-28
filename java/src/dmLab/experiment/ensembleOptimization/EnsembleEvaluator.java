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
/**
 * 
 */
package dmLab.experiment.ensembleOptimization;

import dmLab.geneticFramework.Evaluator;
import dmLab.geneticFramework.Instance;

/**
 * @author mdramins
 *
 */
public class EnsembleEvaluator extends Evaluator {

    EnsembleExperiment ensembleExperiment;
    String inputFleName="data1_train_mcfs_out.arff";
    int ensembleClassifierSize=0;
    //*********************************    
    public EnsembleEvaluator()
    {
        super();
    }
    //*********************************
    @Override
    public boolean init()
    {
        ensembleExperiment=new EnsembleExperiment();        
        return true;
    }
    //****************************
    public void setInputFileName(String inputFleName)
    {
        this.inputFleName=inputFleName;        
    }
    //****************************
    public void setEnsembleClassifierSize(int ensembleClassifierSize)
    {
        this.ensembleClassifierSize=ensembleClassifierSize;   
    }    
    //****************************
    @Override
    public float eval(Instance instance)
    {        
        float[] weights=new float [ensembleClassifierSize];
        for(int i=0; i<ensembleClassifierSize; i++)
            weights[i]= instance.getParamByIndex(i).getValue();
        return ensembleExperiment.run(inputFleName,weights); 
    }
    //*********************************
}
