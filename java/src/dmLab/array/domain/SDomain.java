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
package dmLab.array.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import dmLab.array.loader.fileLoader.NullLabels;
import dmLab.array.meta.Attribute;

public class SDomain implements Cloneable
{      
    protected int initSize=100;
    protected int valuesFreq[];
    protected String domainValues[];    
    protected HashMap<String,Integer> map; 

    //***********************************
    public SDomain()
    {
        map=new HashMap<String,Integer>(initSize*2);
        domainValues=new String[initSize]; 
        valuesFreq=new int[initSize];
    }
//  ***********************************
    @SuppressWarnings("unchecked")
    @Override
    public SDomain clone()
    {
        SDomain domain = new SDomain();
        domain.initSize=initSize;
        domain.valuesFreq=valuesFreq.clone();
        domain.domainValues=domainValues.clone();
        domain.map=(HashMap<String,Integer>)map.clone();
        return domain;
    }
//  ***********************************
    public SDomain(String values[])
    {
        this();
        for(int i=0;i<values.length;i++)
            addItem(values[i]);
    }
    //***********************************
    public synchronized Integer addItem(String item)
    {
        int index;
        if(valuesFreq.length==map.size())
            extendArrays();

        Integer indexInt = map.get(item);

        if(indexInt==null)//have not been added yet
        {
            index=map.size();
            domainValues[index]=item;
            indexInt=new Integer(index);
            map.put(item,indexInt);
        }
        else
            index=indexInt.intValue();

        valuesFreq[index]++;
        return indexInt;
    }
    //**************************************
    public String getItem(int index)
    {
        return domainValues[index];
    }
    //**************************************
    public int getIndex(String item)
    {
        Integer indexInt=map.get(item);
        if(indexInt!=null)
            return indexInt.intValue();
        else
            return -1;
    }
//  *********************************
    public int getFreq(int idTerm)
    {
        return valuesFreq[idTerm];
    }
    //*********************************
    public int size()
    {
        return map.size();
    }
    //**************************************
    public Iterator<Integer> getTermIdIterator()
    {
        return map.values().iterator();
    }
    //***********************************
    private void extendArrays()
    {
        int intArray[]= new int[valuesFreq.length+initSize];
        System.arraycopy(valuesFreq,0,intArray,0,map.size());
        valuesFreq=intArray;
        intArray=null;

        String stringArray[]= new String[domainValues.length+initSize];
        System.arraycopy(domainValues,0,stringArray,0,map.size());
        domainValues=stringArray;        
    }
    //**************************************
    @Override
    public String toString()
    {
        StringBuffer tmp = new StringBuffer();
        int size=map.size();//for speed
        for(int i=0;i<size;i++)
            tmp.append(domainValues[i]+","+valuesFreq[i]+"\n");
        return tmp.toString();
    } 
    //*************************************
    public short fixAttrTypes()
    {
        if(map.size()==0)
            return -1;
        
        short type=Attribute.INTEGER;
        final int size=size();
        for(int i=0;i<size;i++)
        {
            if(!NullLabels.isNullLabel(domainValues[i]))
            {
                float floatVal=Float.NaN;
                try{
                    floatVal=Float.parseFloat(domainValues[i]);
                }
                catch(NumberFormatException e)
                {
                   return Attribute.NOMINAL; 
                }
                if(type==Attribute.INTEGER)
                {
                    int intVal=(int)floatVal;
                    if(intVal!=floatVal)
                        type=Attribute.NUMERIC;
                }
            }
        }
        return type;
    }
//  *************************************
    public boolean sort()
    {
        int size=size();
        if(size<=1)
            return true;
        int type=fixAttrTypes();        
        int freqTmp[]=new int[size];
        String itemsTmp[]=new String[size];
        for(int i=0; i<size; i++)
        {
            itemsTmp[i]=domainValues[i];
            freqTmp[i]=valuesFreq[i];
        }
        domainValues=itemsTmp.clone();
        valuesFreq=freqTmp.clone();
        
        if(type==Attribute.NUMERIC)
        {
            float numericValues[]=new float[size];
            map.clear();
            for(int i=0;i<size;i++)
            {
                if(NullLabels.isNullLabel(domainValues[i]))
                    numericValues[i]=Float.NaN;
                else
                    numericValues[i]=Float.parseFloat(domainValues[i]);
                
                map.put(Float.toString(numericValues[i]),i);
            }
            Arrays.sort(numericValues);
            for(int i=0;i<size;i++)
                freqTmp[i]=valuesFreq[map.get(Float.toString(numericValues[i]))];

            valuesFreq=freqTmp;
            map.clear();
            //tu moze dodawac zera
            for(int i=0;i<size;i++)
            {
                if(NullLabels.isNullValue(numericValues[i]))
                    domainValues[i]=NullLabels.defaultLabel;
                else
                {
                    if(((int)numericValues[i])==numericValues[i])
                        domainValues[i]=Integer.toString((int)numericValues[i]);
                    else
                        domainValues[i]=Float.toString(numericValues[i]);
                }
                
                map.put(domainValues[i],i);
            }
        }else if(type==Attribute.NOMINAL)
        {
            Arrays.sort(domainValues);
            //move freq
            for(int i=0;i<size;i++)
                freqTmp[i]=valuesFreq[map.get(domainValues[i])];
            valuesFreq=freqTmp;
            
            //redefine map
            map.clear();
            for(int i=0;i<size;i++)
                map.put(domainValues[i],i);            
        }
       
        return true;
    }
    //*************************************
    public String[][] getDomain()
    {
        final int size=size();
        String domain[][]=new String[2][size];
        for(int i=0;i<size;i++)
        {
            domain[0][i]=domainValues[i];
            domain[1][i]=Integer.toString(valuesFreq[i]);       
        }
        return domain;
    }
    //*************************************    
}
