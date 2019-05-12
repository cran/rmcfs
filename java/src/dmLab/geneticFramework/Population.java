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

import java.util.Random;

public class Population
{
  private float evals[];
  private float roulette[];
  private Instance instances[];
  private Random rand;
//********************************
  public Population(int instancesNumber, Instance instance)
  {
      rand=new Random(System.currentTimeMillis());// it has to be in this way to have pseudo random numbers
      init(instancesNumber,instance);
  }  
//********************************
  public void init(int instancesNumber, Instance instance)
  {
      instances=new Instance[instancesNumber];
      evals=new float [instancesNumber];
      roulette=new float [instancesNumber];
      for(int i=0;i<instances.length;i++)
      {
          instances[i]=instance.clone();
          instances[i].init();
          //System.out.println(""+instances[i]);
      }
  }  
//********************************  
  public Population(Population oldPopulation,int mutationType)
  {
    rand=new Random(System.currentTimeMillis());// it has to be in this way to have pseudo random numbers
    instances=new Instance[oldPopulation.size()];
    evals=new float [oldPopulation.size()];
    roulette=new float [oldPopulation.size()];
    oldPopulation.prepareRoulette();
    int mother,father;
    for(int i=0;i<instances.length;i++)
    {
      mother=oldPopulation.getParent();
      father=mother;
      int shots=0;
      while(father==mother && shots<instances.length)
      {
          father=oldPopulation.getParent();
          shots++;          
      }
      instances[i]=new Instance(oldPopulation.getInstance(mother),oldPopulation.getInstance(father));
      instances[i].mutate(mutationType);
    }
  }
//********************************
  public int size()
  {
    return instances.length;
  }
//********************************
  public Instance getInstance(int index)
  {
    if(index>=instances.length)
      return null;
    else
      return instances[index];
  }
//********************************
  public float getEval(int index)
  {
      return evals[index];
  }
//********************************
  private boolean prepareRoulette()
  {
    double min=evals[getMinEvalId()];
    if(min<0) 
        min=Math.sqrt(-1.0*min);
    else 
        min=0;
    
    float minPositiveEval=getMinPositiveEval();    
    for(int i=0;i<evals.length;i++)
    {
        if(evals[i]<0)
            roulette[i]=(float)Math.sqrt(-1.0*evals[i])+(float)min;
        else 
            roulette[i]=evals[i]+(float)min;
        
        if(roulette[i]==0)
            roulette[i]+=minPositiveEval*0.1f;
        if(i!=0)
            roulette[i]+=roulette[i-1];
    }        
    return true;
  }
//********************************
  private int getParent()
  {
      double value=rand.nextDouble()*roulette[roulette.length-1];
      for(int i=0;i<roulette.length;i++)
          if(value<roulette[i])
              return i;
      return -1;
  }
//********************************
  public void evaluate(Evaluator evaluator)
  {
    for(int i=0;i<instances.length;i++)
        evals[i]=evaluator.eval(instances[i]);    
  }
//********************************
  public int getMaxEvalId()
  {
      float maxEval=evals[0];
      int maxId=0;
      for(int i=0;i<evals.length;i++)
      {
          if(evals[i]>maxEval)
          {
              maxEval=evals[i];
              maxId=i;
          }
      }
      return maxId;
  }
//********************************
  public int getMinEvalId()
  {
      float minEval=Float.MAX_VALUE;
      int minId=0;
      for(int i=0;i<evals.length;i++)
      {
          if(minEval>evals[i])
          {
              minEval=evals[i];
              minId=i;
          }
      }
      return minId;
  }
//********************************
  private float getMinPositiveEval()
  {
      float minPositiveEval=Float.MAX_VALUE;
      for(int i=0;i<evals.length;i++)
      {
          if(minPositiveEval>evals[i] && evals[i]>0)
              minPositiveEval=evals[i];
      }
      return minPositiveEval;
  }
//********************************  
  
}
