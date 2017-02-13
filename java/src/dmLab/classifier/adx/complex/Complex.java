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
package dmLab.classifier.adx.complex;

import java.util.Arrays;

import dmLab.array.FArray;
import dmLab.classifier.adx.ADXParams;
import dmLab.classifier.adx.selector.Selector;
import dmLab.classifier.adx.selector.SelectorList;
import dmLab.utils.GeneralUtils;
public class Complex implements Cloneable
{
	protected int idSelectors[];
	protected float selectorsValues[];
	protected int selectorsAttributes[];
	protected float parentCovs[];
	protected int currentPosition;

	public float posCoverage;//public for speed
	public float negCoverage;
	public float coverage;
	public float posSupport;
	public float negSupport;

	public boolean reproduce=true; 

	public static final int MERGE_NO=0;
	public static final int MERGE_ALWAYS=1;    
	public static final int MERGE_IF_QUALITY_INCREASED=2;
	//	*******************************************************
	public Complex(int complexSize)
	{
		idSelectors= new int[complexSize];
		currentPosition=0;		
	}
	//	*******************************************************
	public Complex(int[] selectors)
	{
		idSelectors= new int[selectors.length];
		for(int i=0;i<selectors.length;i++)
			idSelectors[i]=selectors[i];
	}
	//	*******************************************************
	public Complex(Complex c)	
	{
		idSelectors = c.idSelectors.clone();
		selectorsValues = c.selectorsValues.clone();
		selectorsAttributes = c.selectorsAttributes.clone();
		parentCovs = c.parentCovs.clone();
		currentPosition = c.currentPosition;

		posCoverage = c.posCoverage;
		negCoverage = c.negCoverage;
		coverage = c.coverage;
		posSupport = c.posSupport;
		negSupport = c.negSupport;
		reproduce = c.reproduce;		
	}	
	//	*******************************************************
	@Override
	public Complex clone()
	{
		return new Complex(this);
	}
	//	*******************************************************
	public int size()
	{
		return idSelectors.length;
	}
	//	*******************************************************
	public void setSelectorId(int position,int selectorId)
	{
		idSelectors[position]=selectorId;
	}
	//	*******************************************************
	public boolean addSelectorId(int selectorId)
	{
		if(currentPosition<idSelectors.length)
		{
			idSelectors[currentPosition++]=selectorId;
			return true;
		}
		else
			return false;
	}
	//	*******************************************************
	public int getSelectorId(int position)
	{
		return idSelectors[position];
	}
	//	*******************************************************
	public double calcQuality(int qMethod)
	{
		return Quality.calc(posCoverage,negCoverage,qMethod);
	}
	//  *******************************************************
	public double calcPosProbability()
	{
		return (posSupport)/(negSupport+posSupport);
	}
	//	*******************************************************
	@Override
	public String toString()
	{
		StringBuffer tmp=new StringBuffer();		
		tmp.append(Arrays.toString(idSelectors));

		return tmp.toString();
	}
	//  *******************************************************
	public String toString(SelectorList selectorsList,FArray array)
	{    
		StringBuffer tmp=new StringBuffer();
		for(int j=0;j<idSelectors.length;j++)
		{
			if(j!=0)
				tmp.append(" and ");
			tmp.append(selectorsList.getSelector(idSelectors[j]).toString(array));
		}
		return tmp.toString();
	}
	//  *******************************************************
	public String toStringCov()
	{    
		StringBuffer tmp=new StringBuffer();
		tmp.append("p: "+ GeneralUtils.formatFloat(posCoverage,3));
		tmp.append(" n: "+ GeneralUtils.formatFloat(negCoverage,3));
		tmp.append(" c: "+ GeneralUtils.formatFloat(coverage,3));
		tmp.append(" ps: "+ GeneralUtils.formatFloat(posSupport,3));
		tmp.append(" ns: "+ GeneralUtils.formatFloat(negSupport,3));
		return tmp.toString();
	}
	//  *******************************************************
	public String toStringQ(ADXParams cfg)
	{    
		StringBuffer tmp=new StringBuffer();
		tmp.append("q: "  + GeneralUtils.formatFloat(calcQuality(cfg.qMethod),4));
		tmp.append(" qf: "  + GeneralUtils.formatFloat(calcQuality(cfg.qMethodFinal),4));
		return tmp.toString();
	}
	//  *******************************************************
	public String toStringPosProbability()
	{
		StringBuffer tmp=new StringBuffer();        
		tmp.append("pr: "+ GeneralUtils.formatFloat(calcPosProbability(),4));
		return tmp.toString();        
	}
	//	*******************************************************
	//more safe function just a little bit slower
	public boolean covers(SelectorList selectorsList,FArray array,int eventIndex)
	{
		final int size=idSelectors.length;//for speed
		for(int i=0;i<size;i++)
		{
			final Selector tmpSelector=selectorsList.getSelector(idSelectors[i]);			
			if(!tmpSelector.covers(array.readValue(tmpSelector.attrIndex,eventIndex)))
				return false;
		}
		return true;
	}
	//	*******************************************************
	//	method tests if complexes can be merged
	public boolean mergingPossible(SelectorList selectorsList,Complex complex,FArray array)
	{
		//testing of length
		if(idSelectors.length!=complex.size())
			return false;

		//testing of attributes
		for(int i=0;i<idSelectors.length;i++)
		{
			Selector s1=selectorsList.getSelector(idSelectors[i]);
			Selector s2=selectorsList.getSelector(complex.getSelectorId(i));
			if(!s1.mergingPossible(s2,array))
				return false;
		}
		return true;
	}
	//	*******************************************************	
	public boolean mergeCondidtion(Complex complex,ADXParams cfg)
	{
		if(cfg.mergeCondition==Complex.MERGE_NO)
			return false;
		else if(cfg.mergeCondition==Complex.MERGE_ALWAYS)
			return true;
		else if(cfg.mergeCondition==Complex.MERGE_IF_QUALITY_INCREASED)
		{	
			double c1_q=Quality.calc(posCoverage,negCoverage,cfg.qMethod);
			double c2_q=Quality.calc(complex.posCoverage,complex.negCoverage,cfg.qMethod);
			double sum_q=Quality.calc(posCoverage+complex.posCoverage,negCoverage+complex.negCoverage,cfg.qMethod);
			//System.out.println("### DEBUG INFO\t"+"c1_q: "+c1_q+" c2_q: "+c2_q+" sum_q: "+sum_q);
			if(sum_q>c1_q || sum_q>c2_q)			
				return true;
		}
		return false;
	}
	//	*******************************************************
	public boolean merge(SelectorList selectorsList,Complex complex,FArray array)
	{
		boolean merged=false;
		for(int i=0;i<idSelectors.length;i++)
		{
			Selector s1=selectorsList.getSelector(idSelectors[i]).clone();
			Selector s2=selectorsList.getSelector(complex.getSelectorId(i));
			if(s1.merge(s2,array))
			{
				//create and add new selector                
				int selectorId=selectorsList.addSelector(s1);
				idSelectors[i]=selectorId;
				merged=true;
			}
		}
		if(merged)
		{
			posCoverage+=complex.posCoverage;
			negCoverage+=complex.negCoverage;
			coverage+=complex.coverage;
			posSupport+=complex.posSupport; 
			negSupport+=complex.negSupport;
		}
		return merged;
	}
	//  *******************************************************
	public boolean contains(SelectorList selectorsList,Complex complex,FArray array)
	{
		//testing of length
		if(idSelectors.length!=complex.size())
			return false;

		//testing of attributes
		for(int i=0;i<idSelectors.length;i++)
		{
			Selector s1=selectorsList.getSelector(idSelectors[i]);
			Selector s2=selectorsList.getSelector(complex.getSelectorId(i));
			if(!s1.contains(s2,array))
				return false;
		}
		return true;
	}    
	//	*******************************************************
	//	method returns true if complex has more information then selectors contained into it
	public boolean betterThanSubSelectors(SelectorList selectorsList,int qMethod)
	{
		for(int i=0;i<idSelectors.length;i++)
		{
			final Selector selector=selectorsList.getSelector(idSelectors[i]);
			if(posCoverage < selector.posCoverage && negCoverage >= selector.negCoverage)				
				return false;
		}
		return true;
	}
	//	*******************************************************
	//	method returns true if complex has more information then subComplexes contained into it
	public boolean betterThanParents(int qMethod)
	{
		if(parentCovs==null)
		{
			System.err.println("Error! Table parentCovs is NULL!");
			return false;
		}
		for(int i=0;i<parentCovs.length;i+=2)
		{
			if( posCoverage < parentCovs[i] && negCoverage >= parentCovs[i+1])
				return false;
		}
		return true;
	}
	//  *******************************************************
	//  method returns true if complex has more information then selectors contained into it
	public boolean betterThanSubSelectors_Q(SelectorList selectorsList,int qMethod)
	{
		double currQuality=Quality.calc(posCoverage, negCoverage, qMethod);
		for(int i=0;i<idSelectors.length;i++)
		{
			final Selector selector=selectorsList.getSelector(idSelectors[i]);
			double selectorQuality=Quality.calc(selector.posCoverage, selector.negCoverage, qMethod);            
			if(currQuality>selectorQuality)               
				return true;
		}
		return false;
	}
	//  *******************************************************
	//  method returns true if complex has more information then subComplexes contained into it
	public boolean betterThanParents_Q(int qMethod)
	{
		if(parentCovs==null)
		{
			System.err.println("Error! Table parentCovs is NULL!");
			return false;
		}
		double currQuality=Quality.calc(posCoverage, negCoverage, qMethod);
		double parentQuality_1=Quality.calc(parentCovs[0], parentCovs[1], qMethod);            
		double parentQuality_2=Quality.calc(parentCovs[2], parentCovs[3], qMethod);

		if(currQuality>parentQuality_1 || currQuality>parentQuality_2)               
			return true;

		return false;
	}
	//	*******************************************************
	public void setParentsCovs(float parent1_PosCov,float parent1_NegCov,float parent2_PosCov,float parent2_NegCov)
	{
		parentCovs=new float[4];
		parentCovs[0]=parent1_PosCov;
		parentCovs[1]=parent1_NegCov;
		parentCovs[2]=parent2_PosCov;
		parentCovs[3]=parent2_NegCov;
	}
	//	*******************************************************
	//	method tests if complex contains subsequence of another complex
	public boolean containsSubsequence(Complex complex)
	{						
		for(int i=1;i<idSelectors.length;i++)
		{
			if(idSelectors[i]!=complex.idSelectors[i-1])
				return false;
		}
		return true;
	}
	//	*******************************************************
	public boolean equalAttributes(Complex complex,SelectorList selectorsList)
	{
		for(int i=0;i<idSelectors.length;i++)
			if(idSelectors.length==1)
				if(selectorsList.getSelector(idSelectors[i]).attrIndex
						!=selectorsList.getSelector(complex.idSelectors[i]).attrIndex)
					return false;
		return true;
	}
	//	*******************************************************
	public void prepareToCoversFast(SelectorList selectorsList,int attrNumber)
	{
		selectorsValues=new float[attrNumber];
		selectorsAttributes=new int[idSelectors.length];
		for(int i=0;i<idSelectors.length;i++)
		{
			final Selector selector=selectorsList.getSelector(idSelectors[i]);
			selectorsAttributes[i]=selector.attrIndex;
			//I assume there is only one value in each selector
			selectorsValues[selectorsAttributes[i]]=selector.getValue(0);
		}
	}
	//	*******************************************************
	//if position array is not empty I have precalculated indexes
	public boolean coversFast(FArray array,int eventIndex)
	{
		final int size=selectorsAttributes.length;//for speed
		for(int i=0;i<size;i++)
		{
			if(selectorsValues[selectorsAttributes[i]]!= array.readValue(selectorsAttributes[i],eventIndex) )
				return false;
		}
		return true;
	}
	//	*******************************************************
}
