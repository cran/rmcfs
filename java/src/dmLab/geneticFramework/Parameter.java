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

import java.util.Random;

/**
 * @author mdramins
 *
 */

public class Parameter implements Cloneable{
    protected float min;
    protected float max;
    protected String name;
    protected float value;
    protected boolean binary=false;
    private static float exploitation_power=0.25f;
    //*****************************    
    public Parameter(String name,float min, float max,boolean binary) 
    {
        this.binary=binary;
        this.name=name;
        if(min>=max)
            System.err.println("ERROR! min>=max name:"+name+" min: "+min +" max: "+max);
        this.max=max;
        this.min=min;
    }
    //*****************************
    public void mutate(Random rand,int type)
    {
        if(type==Instance.EXPLORATION)
            value=(max-min)*rand.nextFloat()+min;
        else if(type==Instance.EXPLOITATION)
            value+=rand.nextGaussian()*(max-min)*exploitation_power;
        
        if(value>max) value=max;
        if(value<min) value=min;
        
        if(binary)
            binaryValue();
    }
    //*****************************
    public void init(Random rand)
    {
        value=(max-min)*rand.nextFloat()+min;
        if(binary)
            binaryValue();
    }
    //*****************************
    private void binaryValue()
    {
        if(value-min<max-value)
            value=min;
        else
            value=max;
    }
    //*****************************
    @Override
    public String toString()
    {
        return name+"="+value;
    }
    //*****************************
    public static void main(String[] args) {
        test();
    }
    //*****************************
    public static void test()
    {
        Random rand=new Random();
        Parameter p=new Parameter("aa",6f,48f,false);
        p.init(rand);
        System.out.println(p.toString());
        for(int i=0;i<100;i++)
        {
            p.init(rand);
            //p.mutate(rand,Instance.EXPLORATION);
            //p.mutate(rand,Instance.EXPLOITATION);
            System.out.println(p.toString());
        }        
    }
    //*****************************
    @Override
    public Parameter clone()
    {
        Parameter p=new Parameter(name,min,max,binary);
        p.value=value;
        return p;        
    }
    //*****************************
    public float getValue()
    {
        return value;
    }
    //*****************************
}
