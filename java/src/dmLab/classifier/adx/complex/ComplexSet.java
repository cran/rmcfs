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
import java.util.HashMap;

import dmLab.array.FArray;
import dmLab.classifier.adx.ADXParams;
import dmLab.classifier.adx.selector.Selector;
import dmLab.classifier.adx.selector.SelectorList;
import dmLab.utils.ArrayUtils;
import dmLab.utils.list.IntegerList;

public class ComplexSet
{
	private ComplexList complexesList;
	private int complexSize;
	private HashMap <Integer,Integer> positionBySelectorId;
	private ADXParams cfg;
	
	public static final int REMOVE_NO=0;  
    public static final int REMOVE_IF_WORSE_THAN_SELECTORS_Q=1;
    public static final int REMOVE_IF_WORSE_THAN_PARENTS_Q=2;
    	
	public boolean verbose = false;
//	**********************************************************
	public ComplexSet(int sizeOfComplex,ADXParams adxParams)
	{
		complexesList=new ComplexList();
		complexSize=sizeOfComplex;
        cfg=adxParams;
	}
//	**********************************************************
	//creation of complexes(szie=1) based on selector base 
	public ComplexSet(SelectorList selectorsList,ADXParams adxParams,int attrNumber)
	{
		complexSize=1;
		cfg=adxParams;
		complexesList=new ComplexList();
		int tempArray [] = new int [1];
		final int selectorsListSize=selectorsList.size();
		
		for(int i=0;i<selectorsListSize;i++)
		{
			Selector tmpSelector=(selectorsList.getSelector(i));
            tempArray[0]=i;
            Complex tmpComplex=new Complex(tempArray);				
            tmpComplex.coverage=tmpSelector.coverage;
            tmpComplex.posCoverage=tmpSelector.posCoverage;
            tmpComplex.negCoverage=tmpSelector.negCoverage;
            tmpComplex.posSupport=tmpSelector.posSupport;
            tmpComplex.negSupport=tmpSelector.negSupport;
            
            tmpComplex.prepareToCoversFast(selectorsList,attrNumber);
            complexesList.add(tmpComplex);			
		}
	}
//	**********************************************************
	public ComplexSet(SelectorList selectorsList,ComplexSet prevComplexList,ADXParams params,int attrNumber)
	{
		cfg=params;
		double minQuality=0.0;
		if(prevComplexList.size()>cfg.searchBeam)
			minQuality=prevComplexList.findSignificantQuality();
				
		CreateSet(selectorsList,prevComplexList,minQuality,attrNumber);
	}
//**********************************************************	
	private void CreateSet(SelectorList selectorsList,ComplexSet prevComplexList,double minQuality,int attrNumber)
	{
		complexSize=prevComplexList.complexSize()+1;
		complexesList=new ComplexList();
		int newSelectorsId []=new int [complexSize];
		final int prevListSize=prevComplexList.complexesList.size();
		final int prevComplexSize=prevComplexList.complexSize();		
		
		if(complexSize==2)//prevComplexSize==1
		{
			for(int i=0;i<prevListSize;i++)
			{
				final Complex complex_i=prevComplexList.getComplex(i);
				if(complex_i.reproduce)
				{
					for(int j=i+1;j<prevListSize;j++)
					{
						final Complex complex_j=prevComplexList.getComplex(j);						
						if(complex_j.reproduce && !complex_i.equalAttributes(complex_j,selectorsList))
						{							
							for(int selPosition=0;selPosition<prevComplexSize;selPosition++)
								newSelectorsId[selPosition]=complex_i.getSelectorId(selPosition);
							newSelectorsId[newSelectorsId.length-1]=complex_j.getSelectorId(prevComplexSize-1);
							
							Complex newComplex=new Complex(newSelectorsId);
							newComplex.setParentsCovs(complex_i.posCoverage,complex_i.negCoverage,complex_j.posCoverage,complex_j.negCoverage);
							newComplex.prepareToCoversFast(selectorsList,attrNumber);
							complexesList.add(newComplex);
						}
					}
				}//end if
			}
		}
		else // if (complexSize>2)
		{			
			prevComplexList.createPositionBySelectorMap();
			for(int i=0;i<prevListSize;i++)
			{
				final Complex complex_i=prevComplexList.getComplex(i);
				if(complex_i.reproduce)
				{
					Integer positionBySelector=prevComplexList.positionBySelectorId.get(complex_i.idSelectors[1]);
					if(positionBySelector!=null)
					{						
						int complex_j_id=positionBySelector.intValue();
						Complex complex_j=prevComplexList.getComplex(complex_j_id);
						
						while(complex_j!=null && complex_i.idSelectors[1]==complex_j.idSelectors[0])
						{	
							if(complex_j.reproduce && complex_i.containsSubsequence(complex_j))
							{
								for(int selPosition=0;selPosition<prevComplexSize;selPosition++)
									newSelectorsId[selPosition]=complex_i.getSelectorId(selPosition);
								newSelectorsId[newSelectorsId.length-1]=complex_j.getSelectorId(prevComplexSize-1);
								
								Complex newComplex=new Complex(newSelectorsId);
								newComplex.setParentsCovs(complex_i.posCoverage,complex_i.negCoverage,complex_j.posCoverage,complex_j.negCoverage);
								newComplex.prepareToCoversFast(selectorsList,attrNumber);
								complexesList.add(newComplex);
							}
							complex_j=prevComplexList.getComplex(++complex_j_id);							
						}
					}
				}//end if
			}			
		}
	}
//	**********************************************************
//	method returns complex from list
	public Complex getComplex(int index)
	{
		return (complexesList.get(index));
	}
//	**********************************************************
//	method deletes complex from list
	public void removeComplex(int index)
	{
		complexesList.remove(index);
	}
//	**********************************************************
	public int size()
	{
		if(complexesList!=null)
			return complexesList.size();
		else
			return 0;
	}
//	**********************************************************
	public boolean trimList()
	{
		return complexesList.trim();
	}
//	**********************************************************
//	method returns size of complex - it is number of selectors contained
	public int complexSize()
	{
		return complexSize;
	}
//	**********************************************************
//	method prints list of complexes in text mode
	public String toString(SelectorList selectorsList,FArray array)
	{
		StringBuffer tmp=new StringBuffer();	
		tmp.append("#Complex Size: "+complexSize).append('\n');
		tmp.append("#Complexes Number: "+complexesList.size()).append('\n');
		
		final int complexesListSize = complexesList.size();//for speed
		Complex tmpComplex;
		
		for(int i=0;i<complexesListSize;i++)
		{
            tmpComplex=complexesList.get(i);
			tmp.append(" "+tmpComplex.toString(selectorsList, array)+'\t'+tmpComplex.toStringCov());
            tmp.append(" "+tmpComplex.toStringQ(cfg));
            tmp.append(" "+tmpComplex.toStringPosProbability());            
            tmp.append('\n');
		}
		return tmp.toString();
	}
//	**********************************************************
//	method prints only indexes of selectors contained into complexes
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		final int complexListSize=complexesList.size();
		for(int i=0;i<complexListSize;i++)
		{
			for(int j=0;j<complexSize;j++)
				tmp.append(" "+ getSelectorIndex(i,j));
			tmp.append('\n');
		}
		return tmp.toString();
	}
//	**********************************************************
//	method returns index of selector contained into complex
	public int getSelectorIndex(int complexIndex,int complexPosition)
	{
		return (complexesList.get(complexIndex)).getSelectorId(complexPosition);
	}
//	**********************************************************
//	method finds pos and neg coverage
	public boolean evaluate(SelectorList selectorsList,FArray array,int decisionValIndex)
	{
		if( complexesList.size()==0) 
			return false;
		
		int complexPosSupport,complexNegSupport;
		final int eventsNumber=array.rowsNumber();//for speed
		final int complexesListSize=complexesList.size();//for speed
		final float complexesListSizeDbl=complexesList.size();//for speed
		final int decAttrIndex=array.getDecAttrIdx();//for speed
		final float decisionValue=array.getDecValues()[decisionValIndex];//for speed
		
		final int interval=(int)Math.ceil(0.1*complexesListSize);
		int threshold=interval;
		
		final int totalPos=array.getADXDomain(decAttrIndex).getTotalPositives(decisionValIndex);
		final int totalNeg=array.getADXDomain(decAttrIndex).getTotalNegatives(decisionValIndex);
		
		if(verbose) System.out.print("Progress: ");		
		for(int i=0;i<complexesListSize;i++)//for all complexes
		{
			complexPosSupport=0;
			complexNegSupport=0;
            final Complex complex=complexesList.get(i);
			for(int j=0;j<eventsNumber;j++)//for all events
			{
				if( complex.coversFast(array,j) )
				{
					if(array.readValue(decAttrIndex,j)==decisionValue)
						complexPosSupport++;
					else
						complexNegSupport++;
				}
			}
			complex.posCoverage=(float)complexPosSupport/(float)totalPos;
            complex.negCoverage=(float)complexNegSupport/(float)totalNeg;
            complex.coverage=(float)(complexPosSupport+complexNegSupport)/(float)eventsNumber;
            complex.posSupport=complexPosSupport;
            complex.negSupport=complexNegSupport;
			//this part is only for printing of progress
			if(i>threshold && threshold!=0)
			{
				if(verbose) System.out.print(""+ (int)(100.0*(i)/complexesListSizeDbl) + "% ");
				threshold+=interval;
			}
		}
		if(verbose)
			System.out.println("100% ");
		return true;
	}
//	**********************************************************
//	delete complexes which have p=0 and n=0   AND
//	delete complexes witch has less p and higher n then selectors contained
	public int cleanCandidatesSet(SelectorList selectorsList,ADXParams adxParams)
	{
		final int listSize = complexesList.size();
		if(listSize==0)
			return -1;
		
		int removed=0;
		for(int i=0;i<listSize;i++)//for all complexes
		{
			final Complex complex= complexesList.get(i);
			//delete complexes that do not cover positive events at all
			if(complex.posCoverage==0.0)
			{
				complexesList.remove(i);
				removed++;
			}			
            else if(adxParams.cleanCandidates==REMOVE_IF_WORSE_THAN_SELECTORS_Q)
            {
                if(!complex.betterThanSubSelectors_Q(selectorsList,cfg.qMethod))
                {
                    complexesList.remove(i);
                    removed++;
                }
            }
            else if(adxParams.cleanCandidates==REMOVE_IF_WORSE_THAN_PARENTS_Q)
            {
                if(!complex.betterThanParents_Q(cfg.qMethod))
                {
                    complexesList.remove(i);
                    removed++;
                }
            }           
		}
		complexesList.trim();
		return removed;
	}
//	**********************************************************
//	method deletes complexes where pos coverage is less than neg
	public int deleteComplexesPosLessNeg()
	{
		final int  listSize=complexesList.size();//for speed
		if(listSize==0) 
			return -1;
		
		int removed=0;				
		for(int i=0;i<listSize;i++)//for all complexes
		{
			final Complex complex=complexesList.get(i);
			if( complex.posCoverage <= complex.negCoverage )
			{
				complexesList.remove(i);
				removed++;
			}
		}
		complexesList.trim();
		return removed;
	}
//	**********************************************************
//	method deletes complexes which covers negative events
	public int deleteComplexesCoversNeg()
	{
		final int listSize=complexesList.size();//for speed
		if(listSize==0) 
			return -1;
		int removed=0;
		for(int i=0;i<listSize;i++)//for all complexes
		{
			if( complexesList.get(i).negCoverage > 0.0 )
			{
				complexesList.remove(i);
				removed++;
			}
		}
		complexesList.trim();
		return removed;
	}
//	**********************************************************
//	method leaves only specified set of complexes
	public int deleteComplexesQLess(double minQuality)
	{
		final int listSize=complexesList.size();//for speed
		if(listSize==0)
			return -1;
		int removed=0;
		for(int i=0;i<listSize;i++)//for all complexes
		{
			if( getComplex(i).calcQuality(cfg.qMethod) < minQuality )
			{
				complexesList.remove(i);
				removed++;
			}
		}
		complexesList.trim();
		return removed;
	}
//  **********************************************************
    public void disableComplexesToReproduce()
    {
        final int listSize=complexesList.size();    
        for(int i=0;i<listSize;i++)
        {
            final Complex complex=complexesList.get(i);          
            if(cfg.complexGenerality==1 && complex.negCoverage==0.0)
                complex.reproduce=false;
        }
    }
//	**********************************************************
	public int selectComplexesToReproduce(double minQuality)
	{
		final int listSize=complexesList.size();
		int toReproduce=0;
		IntegerList minQualityIDs=new IntegerList();
		for(int i=0;i<listSize;i++)
		{
			final Complex complex=complexesList.get(i);
			final double complexQuality=complex.calcQuality(cfg.qMethod);			
            
            if(cfg.complexGenerality==1 && complex.negCoverage==0.0)
                complex.reproduce=false;
            else if(complexQuality>=minQuality)
			{
				complex.reproduce=true;
				toReproduce++;
				if(complexQuality==minQuality)
					minQualityIDs.add(i);
			}
			else
				complex.reproduce=false;
		}
        //System.out.println(" DEBUG ####### toReproduce:"+toReproduce);
        //System.out.println(" DEBUG ####### minQualityIDs.size():"+minQualityIDs.size());
        
//		select randomly complexes for the next phase if there is still too many of them			
        if(toReproduce>cfg.searchBeam)
		{
			int toThrow=toReproduce-cfg.searchBeam;
            //System.out.println(" DEBUG ####### toThrow:"+toThrow);
			int throwArray[]=new int[minQualityIDs.size()];
    		ArrayUtils arrayUtils = new ArrayUtils();
    		arrayUtils.randomFill(throwArray,toThrow,1,0);

			for(int i=0;i<throwArray.length;i++)
				if(throwArray[i]==1)
				{
					complexesList.get(minQualityIDs.get(i)).reproduce=false;
					toReproduce--;
				}
		}
		//if(toReproduce>cfg.searchBeam)
        //    System.out.println(" DEBUG ####### toReproduce>cfg.searchBeam!!!");
        
		return toReproduce;
	}	 
//**********************************************************
	public double findSignificantQuality()
	{
		double significantQ[]=new double[cfg.searchBeam];
		Arrays.fill(significantQ,Double.NEGATIVE_INFINITY);
		
		int minValuePosition=significantQ.length;//the last one
		final int listSize=complexesList.size();
		
		for(int i=0;i<listSize;i++)
		{
			final Complex complex=(complexesList.get(i));
			if(complex.reproduce)
			{
				final double complexQuality=complex.calcQuality(cfg.qMethod);
				
				if(minValuePosition>0)
					significantQ[--minValuePosition]=complexQuality;
				else if(complexQuality>=significantQ[0])
				{
					significantQ[0]=complexQuality;
					Arrays.sort(significantQ);
				}
			}
		}
		Arrays.sort(significantQ);
        
        if(minValuePosition>=significantQ.length)
        {
            System.out.println("significantQ: "+Arrays.toString(significantQ));
            return Double.NEGATIVE_INFINITY;
        }
        else
            return significantQ[minValuePosition]; //here is the minimal value to take
	}
//	**********************************************************
//	method creates hashMap of positions based on first selectorId in complex
	public boolean createPositionBySelectorMap()
	{				
		if(complexSize<2)
			return false;
		
		final int listSize=complexesList.size();//for speed		
		positionBySelectorId=new HashMap<Integer,Integer>();
		int prevSelectorId=-1;
		
		for(int i=0;i<listSize;i++)
		{
			int selectorId=complexesList.get(i).getSelectorId(0);//only for the first one
			if(selectorId!=prevSelectorId)
			{
				positionBySelectorId.put(selectorId,i);
				prevSelectorId=selectorId;
			}
		}
		return true;
	}
//	*******************************************
	public boolean mergeComplexes(SelectorList selectorsList,FArray array)
	{
        final int listSize = complexesList.size();//for speed
		int currentIndex=0, stopIndex=0;
		
		if(listSize==0) 
			return false;
		
		for(int i=0;i<listSize-1;)
		{
			Complex complex=complexesList.get(i);
			if(complex==null)
			{
				i++;
				continue;				
			}		
			
			stopIndex=i+1;
			while(stopIndex<listSize 
                    && complex.mergingPossible(selectorsList,complexesList.get(stopIndex),array)
                    && complex.mergeCondidtion(complexesList.get(stopIndex),cfg))
				stopIndex++;
			
			if(currentIndex<stopIndex)
				currentIndex++;
				
			while(currentIndex<stopIndex)
			{				
                //System.out.println("#"+i+" "+complex.toString()+" merging with #"+currentIndex+" "+ getComplex(currentIndex));
                if(complex.merge(selectorsList,getComplex(currentIndex),array))
                {
					//Tutaj jest usuwanie kompleksow po laczeniu
                    removeComplex(currentIndex++);                    
                }
				else
                {   //remove complex if its already used
                    if(complex.contains(selectorsList, getComplex(currentIndex), array))
                        removeComplex(currentIndex);
                    else
                        System.err.println("Error. Complexes have not been merged.\n\t"
							+complex.toString(selectorsList,array)
                            +"\n\t"+getComplex(currentIndex).toString(selectorsList,array));
                    currentIndex++;
                }
			}
			i=stopIndex;			
		}
		complexesList.trim();
		return true;
	}
//	***********************************************
	public int addComplex(Complex complex)
	{
		return complexesList.add(complex)-1;
	}
//	**********************************************************
	
}
