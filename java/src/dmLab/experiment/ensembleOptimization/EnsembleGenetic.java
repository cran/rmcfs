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
package dmLab.experiment.ensembleOptimization;
import dmLab.classifier.ensemble.EnsembleClassifier;
import dmLab.geneticFramework.Genetic;
import dmLab.geneticFramework.Instance;
import dmLab.geneticFramework.Parameter;

/**
 * @author mdramins
 *
 */
public class EnsembleGenetic extends Genetic {

    //*******************************    
    public EnsembleGenetic() 
    {
        super();
    }
    //*******************************
    @Override
    public void defineInstance() 
    {
        EnsembleClassifier c = new EnsembleClassifier();
        int ensembleClassifierSize=c.size();
        for(int i=0;i<ensembleClassifierSize;i++)
            instance.add(new Parameter(c.getSubClassifierName(i),0,1,true));
                    
        instance.init();
    }
    //**********************************************
    public static void main(String[] args)
    {
        EnsembleEvaluator evaluator=new EnsembleEvaluator();        
        evaluator.init();
        EnsembleClassifier c = new EnsembleClassifier();
        int ensembleClassifierSize=c.size();
        
        evaluator.setEnsembleClassifierSize(ensembleClassifierSize);
        evaluator.setInputFileName(args[0]);
        System.out.println("### Input File Name: "+args[0]+" ###");
        EnsembleGenetic ensembleGenetic=new EnsembleGenetic();        
        ensembleGenetic.mutationType=Instance.EXPLORATION;
        ensembleGenetic.run(6,100,evaluator);
    }
    //*********************************    
    

}
