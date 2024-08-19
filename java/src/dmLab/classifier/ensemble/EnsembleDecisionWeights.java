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
package dmLab.classifier.ensemble;

import java.util.Arrays;
import java.util.HashMap;

public class EnsembleDecisionWeights 
{    
    private float decisions[];
    private float weights[];
    private HashMap<Float,Integer> map;
    //put(Object key, Object value)
    //get(Object key)
    //**********************************************    
    public EnsembleDecisionWeights(float decisionValues[])
    {
        map=new HashMap<Float,Integer>();
        decisions=decisionValues.clone();        
        weights=new float[decisions.length];
        Arrays.fill(weights, 0);
        for(int i=0;i<decisions.length;i++)
            map.put(decisionValues[i], i);
    }
    //**********************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(Arrays.toString(decisions)).append('\n');
        tmp.append(Arrays.toString(weights)).append('\n');
        return tmp.toString();
    }
    //**********************************************
    public void cleanWeights()
    {
        weights=new float[decisions.length];        
    }
    //**********************************************    
    public boolean add(float decisionValue,float weight)
    {
        int index=getDecisionIndex2(decisionValue);
        if(index==-1)
        {
            System.err.println("Error! There is no decisionValue="+decisionValue+" in EnsembleDecisionWeihts.");
            return false;
        }
        weights[index]+=weight;
        return true;
    }
    //**********************************************
    public float getMaxDecision()
    {
        final int size=weights.length;
        int maxIndex=0;
        float maxWeight=weights[maxIndex];
        for(int i=0;i<size;i++)
        {
            if(maxWeight<weights[i])
            {
                maxWeight=weights[i];
                maxIndex=i;
            }
        }
        return decisions[maxIndex]; 
    }    
    //**********************************************
    public int getDecisionIndex2(float decisionValue)
    {
        return map.get(decisionValue);   
    }
    //**********************************************
    public int getDecisionIndex(float decisionValue)
    {
        final int size=decisions.length;
        for(int i=0;i<size;i++)
            if(decisions[i]==decisionValue)
                return i;
        return -1;
    }
    //**********************************************
}
