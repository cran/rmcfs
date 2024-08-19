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
package dmLab.geneticFramework;


/**
 * @author mdramins
 *
 */
public abstract class Genetic {

    protected Instance instance;
    protected Instance bestInstancesSet[];
    protected float evals[];
    protected int mutationType=Instance.EXPLORATION;
        
    public Genetic() {
        instance=new Instance();
    }
    //**************************
    public Instance run(int generations,int generationSize,Evaluator evaluator)
    {
        defineInstance();
        if(instance.size()==0)
        {
            System.err.println("ERROR! instance.size==0");
            return null;
        }        
        Population populations[]=new Population[generations];
        bestInstancesSet=new Instance[generations];
        evals=new float[generations];
        for(int i=0;i<populations.length;i++)
        {
            System.out.println("### GENETIC ### generation...");
            if(i==0)
                populations[0]=new Population(generationSize,instance);
            else
                populations[i]=new Population(populations[i-1],mutationType);
            System.out.println("### GENETIC ### evaluation...");
            populations[i].evaluate(evaluator);
            System.out.println("");
            System.out.println("### GENETIC ### Generarion: "+(i+1)+" ####");
            int id=populations[i].getMaxEvalId();
            bestInstancesSet[i]= populations[i].getInstance(id);
            evals[i]=populations[i].getEval(id);
            System.out.println("### GENETIC ### max eval: "+evals[i]);
            System.out.println(bestInstancesSet[i].toString());
            System.out.println("### GENETIC ### ");            
        }
        for(int i=0;i<populations.length;i++)
        {
            System.out.println("### GENETIC ### eval: "+evals[i]);
            System.out.println(bestInstancesSet[i].toString());
        }
        return bestInstancesSet[generations-1];
    }
    //**************************
    public abstract void defineInstance();
}
