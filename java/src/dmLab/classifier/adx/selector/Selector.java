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
package dmLab.classifier.adx.selector;

import java.util.Arrays;

import dmLab.array.FArray;
import dmLab.array.meta.Attribute;
import dmLab.utils.GeneralUtils;
import dmLab.utils.condition.Operator;

public class Selector implements Cloneable
{
	public int attrIndex;
	protected boolean range;	
	protected Operator operator;
	
	protected int size;
	protected float values[];
	
    public float posCoverage;
	public float negCoverage;
    public float posSupport;
    public float negSupport;
	public float coverage;
	
	private static int initSize=2;
//	*******************************************************
	public Selector()
	{
		range=false;
		attrIndex=-1;
		size=0;
		values=new float[initSize];
		operator=new Operator();
	}
//	*******************************************************
	public Selector(boolean range,int size,int attrIndex,Operator operator,float[] values,
			float posCov,float negCov,float cov,float posSupport,float negSupport)
	{
		this.values=values;
		this.size=size;
		this.attrIndex=attrIndex;
		this.operator=operator;
		this.posCoverage=posCov;
		this.negCoverage=negCov;
        this.posSupport=posSupport;
        this.negSupport=negSupport;        
		this.coverage=cov;
		this.range=range;
	}	
//*******************************************************
	public Selector(boolean range,int attributeIndex,Operator operator,float value,
			float posCov,float negCov,float cov,float posSupport,float negSupport)
	{
		this(range,1,attributeIndex,operator,new float[]{value},posCov,negCov,cov,posSupport,negSupport);
	}
//	*******************************************************
	@Override
    public Selector clone()
	{
		return new Selector(range,size,attrIndex,operator.clone(),values, posCoverage, negCoverage, coverage,posSupport,negSupport);
	}
//	*******************************************************
	public int contains(float value)
	{
		for(int i=0;i<size;i++)
			if(values[i]==value)
				return i;
		return -1;
	}
//	*******************************************************
	public boolean merge(Selector selector,FArray array)
	{
		if(attrIndex!=selector.attrIndex)
			return false;
		//DEBUG INFO
		//System.out.println(" ### merging "+toString(array)+" with "+selector.toString(array));
		//DEBUG INFO
		if(operator.op!=Operator.EQUAL)
			return false;		

		//if pure nominal selector
		if(!range && !array.isDiscretized(attrIndex))
		{
            boolean merged=false;
            for(int i=0;i<selector.size;i++)//add all values if it is nominal selector
			{
                if(contains(selector.values[i])==-1)
				{					
                    addValue(selector.values[i]);
                    merged=true;
				}
			}
            if(merged)
            {
                posCoverage+=selector.posCoverage;
                negCoverage+=selector.negCoverage;
                posSupport+=selector.posSupport;
                negSupport+=selector.negSupport;
                coverage+=selector.coverage;
            }
			return merged;
		}
		else
		{
			float min_max1[]=null;
			float min_max2[]=null;           
			
			if(range)			
				min_max1=values;			
			else
				min_max1=array.discRanges[attrIndex].getRange(values[0]);
			if(selector.range)
				min_max2=selector.values;
			else

				min_max2=array.discRanges[attrIndex].getRange(selector.values[0]);
					
			if((min_max1[1]>=min_max2[0] && min_max1[0]<=min_max2[1]) ||  
					(min_max2[1]>=min_max1[0] && min_max2[0]<=min_max1[1]))
			{
				values=new float[2];
				values[0]=Math.min(min_max1[0],min_max2[0]);
				values[1]=Math.max(min_max1[1],min_max2[1]);
				size=values.length;
				range=true;
				posCoverage+=selector.posCoverage;
				negCoverage+=selector.negCoverage;
                posSupport+=selector.posSupport;
                negSupport+=selector.negSupport;
				coverage+=selector.coverage;
				return true;
			}
		}
		return false;
	}
//*******************************************************
	public boolean mergingPossible(Selector selector,FArray array)
	{
		if(attrIndex!=selector.attrIndex)
			return false;
		//for now I dont need to merge selectors with  different operators
		if(operator.op!=Operator.EQUAL || selector.operator.op!=Operator.EQUAL)
			return false;

		if(!range && array.isDiscretized(attrIndex))
		{
			float min_max1[]=array.discRanges[attrIndex].getRange(values[0]);
			float min_max2[]=array.discRanges[attrIndex].getRange(selector.values[0]);
			if(min_max1[0]==min_max2[1] || min_max1[1]==min_max2[0]
                  || ( min_max1[1]>=min_max2[0] && min_max1[1]<=min_max2[1])
                  || ( min_max2[1]>=min_max1[0] && min_max2[1]<=min_max1[1]) )
				return true;
			else
				return false;
		}

		if(range &&	(values[1]<selector.values[0] || values[0]>selector.values[1]))
			return false;
		
		return true;
	}
//  *******************************************************
    public boolean contains(Selector selector,FArray array)
    {
        if(attrIndex!=selector.attrIndex)
            return false;
        //for now I dont need to merge selectors with  different operators
        if(operator.op!=Operator.EQUAL || selector.operator.op!=Operator.EQUAL)
            return false;

        if(!range && array.isDiscretized(attrIndex))
        {
            float min_max1[]=array.discRanges[attrIndex].getRange(values[0]);
            float min_max2[]=array.discRanges[attrIndex].getRange(selector.values[0]);
            if(min_max2[0]>=min_max1[0] && min_max2[1]<=min_max1[1])
                return true;
            else
                return false;
        }

        if(range && (selector.values[0]>=values[0] && selector.values[1]<=values[1]))
            return true;
        else if(!range)
        {
            for(int i=0;i<selector.values.length;i++)
            {
                if(contains(selector.values[i])==-1)
                    return false;
            }
            return true;
        }            
        return true;
    }
//*******************************************************
	public void set(boolean range,int attrIndex,Operator operator)
	{
		this.attrIndex=attrIndex;
		this.operator.op=operator.op;
		this.range=range;
	}
//	*******************************************************
	public boolean isRange()
	{
		return range;
	}
//  *******************************************************
    public boolean isGeneral()
    {      
        if(range && operator.op == Operator.EQUAL &&
                values[0]==Float.NEGATIVE_INFINITY && values[1]==Float.POSITIVE_INFINITY)
            return true;
        else if(!range && operator.op == Operator.EQUAL && Float.isNaN(values[0]))
            return true;
        else 
            return false;                        
    }
//	*******************************************************
	public boolean addValue(float value)
	{
		if(size==values.length)
			extend();
		
		values[size++]=value;
		return true;
	}
//	*******************************************************
	public float getValue(int index)
	{
		if(index<size)
			return values[index];
		else
			return Float.NaN;
	}
//	*******************************************************
	public boolean covers(float value)
	{
		if(Float.isNaN(value)) //if value is unknown
			return false;
		
		if(!range && operator.op == Operator.EQUAL)
		{			
			for(int i=0;i<size;i++){
				if(values[i]==value)
					return true;
			}
			return false;
		}
		else if(!range && operator.op == Operator.GREATER){
			for(int i=0;i<size;i++){
				if(values[i]>value)
					return true;
			}
			return false;
		}
		else if(!range && operator.op == Operator.LESS){
			for(int i=0;i<size;i++){
				if(values[i]>value)
					return true;
			}
			return false;
		}
		else if(!range && operator.op == Operator.GEQ){
			for(int i=0;i<size;i++){
				if(values[i]>=value)
					return true;
			}
			return false;

		}
		else if(!range && operator.op == Operator.LEQ){
			for(int i=0;i<size;i++){
				if(values[i]<=value)
					return true;
			}
			return false;
		}
		else if(range && operator.op == Operator.EQUAL)
		{
			if(values[0]==Float.NEGATIVE_INFINITY && 
                    values[1]==Float.POSITIVE_INFINITY)
                return true;
            if(value==Float.NEGATIVE_INFINITY 
					&& values[0]==Float.NEGATIVE_INFINITY)
				return true;					
			else if(value>values[0] && value<=values[1])//if positive infinity 
				return true;									//value<=valuesList[1] 
			else
				return false;
		}
		else
			System.err.println("Incorrect selector!");

		return false;
	}
//	*******************************************************
	public String toString(FArray array)
	{
		StringBuffer tmp=new StringBuffer();		
		String valuesStr="";
		boolean rangesCreated=array.isDiscretized(attrIndex);
		int type=array.attributes[attrIndex].type;
		
		if(!range)
		{
			for(int i=0;i<size;i++)
			{
				if(type==Attribute.NOMINAL)
					tmp.append(array.dictionary.toString(values[i]));
				else if(type==Attribute.NUMERIC && rangesCreated)		
					tmp.append(array.discRanges[attrIndex].getRangeStr(values[i]));
				else if(type==Attribute.NUMERIC && !rangesCreated)
					tmp.append(values[i]);
							
				if(i!=size-1)
					tmp.append(',');
			}			
			if( size>1)
				valuesStr="["+tmp.toString()+"]";
			else
				valuesStr=tmp.toString();
		}
		else if(range)
		{
			if(size==2)
			{
				if(operator.op == Operator.EQUAL)
					valuesStr="("+values[0]+";"+values[1]+"]";
				else
				{
					System.err.println("Incorrect selector! Selector contains '"+operator.toString()+"' and two values. Attribute: "+array.attributes[attrIndex].name);
					valuesStr=Arrays.toString(values);
				}					
			}
			else
			{
				System.err.println("Incorrect selector! Selector contains more than two values. Attribute: "+array.attributes[attrIndex].name);
				valuesStr=Arrays.toString(values);
			}
		}
		return array.attributes[attrIndex].name+operator.toString()+valuesStr;
	}
	//****************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();		
		String valuesStr="";
		
		for(int i=0;i<size;i++)
		{
				tmp.append(values[i]);
				if(i!=size-1)
					tmp.append(',');
		}			
		if(size>1)
				valuesStr="["+tmp.toString()+"]";
		else
			valuesStr=tmp.toString();
	
		return ""+attrIndex+operator.toString()+valuesStr;
	}
//	*******************************************************
	public String toStringCov()
	{
        StringBuffer tmp=new StringBuffer();
        tmp.append("p: "+ GeneralUtils.format(posCoverage,3));
        tmp.append(" n: "  + GeneralUtils.format(negCoverage,3));
        tmp.append(" c: "  + GeneralUtils.format(coverage,3));
        return tmp.toString();
	}
//	*******************************************************
	private void extend()
	{
		float valuesTmp[];
		valuesTmp= new float[values.length+initSize];
		System.arraycopy(values,0,valuesTmp,0,size);
		values=valuesTmp;
		valuesTmp=null;
	}
//*******************************************************

}
