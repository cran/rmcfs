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
package dmLab.geneticFramework;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author mdramins
 *
 */
public class Instance implements Cloneable{
    
    public static int EXPLORATION=1;
    public static int EXPLOITATION=2;  
    
    protected Parameter params[];
    protected ArrayList<Parameter> paramsList;    
    protected Random rand;
    
    public Instance() 
    {
        paramsList=new ArrayList<Parameter>();
        rand=new Random(System.currentTimeMillis());
    }
    //**************************
    public void mutate(int type)
    {
      for(int i=0;i<params.length;i++)
          if(rand.nextInt(params.length)==0) // mutation with prob 1/n
              params[i].mutate(rand,type);                
    }
    //**************************
    public int size()
    {
        if(params!=null)
            return params.length;
        else
            return 0;
    }
    //**********************    
    public void init()
    {
        if(paramsList!=null)
            convert();
        for(int i=0;i<params.length;i++)
            params[i].init(rand);
    }
    //**********************
    private void convert()
    {
        Object o[]=paramsList.toArray();
        params= new Parameter[o.length];
        for(int i=0;i<o.length;i++)
            params[i]=(Parameter)o[i];   
        paramsList=null;
    }
    //**********************
    public Parameter[] getParams()
    {
      return params;
    }
    //**********************
    public Instance(Instance mother, Instance father)
    {
        rand=new Random(mother.rand.nextLong()+father.rand.nextLong());
        if(mother.size()!=father.size())
          System.out.println("ERROR! Instances have different length of Params!");
        params=new Parameter[mother.size()];
        
        double rnd=(rand.nextGaussian()*0.15)+0.5;
        int cut = (int)(rnd*mother.size());
        for(int i=0;i<params.length;i++)
        {
          if(i<=cut)
              params[i]=mother.params[i].clone();
          else
              params[i]=father.params[i].clone();
        }
    }
    //*****************************
    public boolean add(Parameter p)
    {
        if(paramsList==null)
            return false;

        paramsList.add(p);
        return true;
    }
    //********************************
    @Override
    public Instance clone()
    {
        Instance instance=new Instance();
        instance.params=new Parameter[params.length];
        for(int j=0;j<params.length;j++)
            instance.params[j]=params[j].clone();
        instance.rand.setSeed(rand.nextLong());
        instance.paramsList=null;
        return instance;
    }    
    //********************************
    @Override
    public String toString()
    {
        StringBuffer buf=new StringBuffer();
        for(int i=0;i<params.length;i++)
            buf.append(params[i].toString()+"\n");
        return buf.toString();
    }
    //********************************
    public Parameter getParamByName(String name)
    {
        for(int i=0;i<params.length;i++)
            if(params[i].name.equalsIgnoreCase(name))
                return params[i];
        return null;
    }
    //********************************
    public Parameter getParamByIndex(int index)
    {
        if(params!=null)
            return params[index];
        else
            return paramsList.get(index);               
    }
    //********************************
    public void setSeed(long seed)
    {
        rand.setSeed(seed);
    }
    //******************************** 
    public static void main(String[] args) {
        Instance instance=new Instance();
        instance.add(new Parameter("lag",6,48,false));
        instance.add(new Parameter("stopLossPower",0,3,false));
        instance.add(new Parameter("s1",0,1,false));
        instance.add(new Parameter("s2",0,1,false));
        instance.init();        
        for(int i=0;i<10;i++)
        {
            Instance i2=instance.clone();
            i2.init();
            System.out.println(i2.toString());
            i2.mutate(EXPLORATION);
            System.out.println(i2.toString());
            System.out.println("");
        }        
    }
    //******************************** 
}
