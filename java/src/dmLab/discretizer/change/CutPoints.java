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
package dmLab.discretizer.change;

import java.util.ArrayList;


public class CutPoints
{
    private float cutPoints[];
    private int cuts[];
    private int sourceIndexes[];
    private int classFreq[][];
    private int size;
    //private double array[];
    //*****************************************
    public CutPoints(int cutPointsNumber,int classesNumber)    
    {
        cutPoints=new float[cutPointsNumber];
        sourceIndexes=new int[cutPointsNumber];
        classFreq=new int [classesNumber][cutPointsNumber];
        cuts=new int[cutPointsNumber];
        size=0;
    }
    //*****************************************
    public float getValue(int index)
    {
        return cutPoints[index];
    }
    //*****************************************
    public ArrayList<Float> getValues()
    {    	
    	ArrayList<Float> retList = new ArrayList<Float>();
    	for(int i=0;i<cutPoints.length;i++)
    		retList.add(cutPoints[i]);
    	
        return retList;
    }
    //*****************************************
    public void add(float cutPoint,int sourceIndex,int freq[],int cuts) 
    {
        cutPoints[size]=cutPoint;
        sourceIndexes[size]=sourceIndex;
        for(int i=0;i<classFreq.length;i++)
            classFreq[i][size]=freq[i];
        
        this.cuts[size]=cuts;
        
        size++;
    }
    //*****************************************
    public double[] getClassesSum(int begin,int end)
    {
        if(end>size) 
            return null;
        
        double array[]=new double[classFreq.length];
        
        for(int i=begin;i<end;i++)//over cutPoints
            for(int j=0;j<classFreq.length;j++)//over classes
                array[j]+=classFreq[j][i];
        
        return array;
    }
    //*****************************************
    public double getCutsSum(int begin,int end)
    {
        if(end>size) 
            return Double.NaN;
        double cutsSum=0;
        for(int i=begin;i<end;i++)//over cutPoints
            cutsSum+=cuts[i];
        return cutsSum;
    }
    //*****************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("cutPoint").append('\t').append("sourceIndex").append('\t').append("cuts").append('\n');
        for(int i=0;i<size;i++)
        {
            tmp.append(cutPoints[i]).append('\t').append('\t');
            tmp.append(sourceIndexes[i]).append('\t');
            tmp.append(cuts[i]).append('\t');
            tmp.append("\t***\t");            
            for(int j=0;j<classFreq.length;j++)
                tmp.append(classFreq[j][i]).append('\t');
            tmp.append('\n');
        }
        return tmp.toString();
    }
    //*****************************************
    public int size()
    {
        return size;
    }
    //*****************************************
}
