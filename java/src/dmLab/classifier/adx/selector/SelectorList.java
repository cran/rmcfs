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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import dmLab.array.FArray;
import dmLab.classifier.adx.complex.Quality;
import dmLab.utils.condition.Operator;

public class SelectorList
{
	private Selector selectorsArray[];
	private HashMap <String,Integer>hashDictionary;
	private int size;
	private int initSize=1000;
	public static final int precision=3;
	public boolean verbose=true;
//	*******************************************************
//	default constructor
	public SelectorList()
	{
		hashDictionary = new HashMap<String,Integer>();
		selectorsArray = new Selector[initSize];
		size=0;
	}
//	*******************************************************
//	method gives size of selectors list (number of selectors)
	public int size()
	{
		return size;
	}
//	*******************************************************
//	method adds selector to selector List
	public int addSelector(Selector selector)
	{
		int newSelectorId=-1;
		String symbolicSelector=selector.toString();
		
		if(hashDictionary.containsKey(symbolicSelector)==true)
		{
			newSelectorId=(hashDictionary.get(symbolicSelector)).intValue();
		}
		else
		{
			if(selectorsArray.length==size)
				extend();
			selectorsArray[size++]=selector;
			newSelectorId=size-1;
			hashDictionary.put(symbolicSelector,new Integer(newSelectorId));
		}
		return newSelectorId;
	}
//	*******************************************************
//	method returns selector
	public Selector getSelector(int selectorIndex)
	{
        return selectorsArray[selectorIndex];
	}
//	*******************************************************
//	method finds simple selectors
	public int createSelectorBase(FArray array,int decisionValIndex)
	{
		final int attrNumber=array.colsNumber();
		final int decAttrIndex= array.getDecAttrIdx();
		
		for(int i=0;i<attrNumber;i++)//i == attribute index
		{
			if(i!=decAttrIndex)
			{
				//if(!array.attributes[i].discretizer.rangesCreated())
				//for nominal or no discretized attributes
				final int domainSize=array.getADXDomain(i).size();
				for(int j=0;j<domainSize;j++)//j == index of value
				{
				    if (domainSize>1 && !(domainSize==2 && array.getADXDomain(i).contains(Float.NaN)))
                        if(array.getADXDomain(i).posCoverage(j,decisionValIndex)>0)
    						if(!((Float)array.getADXDomain(i).getValue(j)).isNaN())
    							addSelector(new Selector(false,i,new Operator("="),
    								((Float)array.getADXDomain(i).getValue(j)).floatValue(),
    								array.getADXDomain(i).posCoverage(j,decisionValIndex),
    								array.getADXDomain(i).negCoverage(j,decisionValIndex),
    								array.getADXDomain(i).coverage(j,decisionValIndex),
                                    array.getADXDomain(i).posSupport(j,decisionValIndex),
                                    array.getADXDomain(i).negSupport(j,decisionValIndex)));                                       
				}				
			}
		}
		if(verbose) System.out.println("");
		return size;
	}
//	*******************************************************
//	method prints selector List
	public String toString(FArray array)
	{
		StringBuffer tmp=new StringBuffer();
		Selector selector;
		tmp.append("Selectors: #"+size).append('\n');
		for(int i=0;i<size;i++)
		{
			selector=getSelector(i);
			tmp.append("#"+i).append("\t");
            tmp.append(selector.toString(array)).append('\t');
            tmp.append(selector.toStringCov()).append('\n');                        
		}
		return tmp.toString();
	}
//  *******************************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        Selector selector;
        tmp.append("Selectors: #"+size).append('\n');
        for(int i=0;i<size;i++)
        {
            selector=getSelector(i);
            tmp.append(selector.toString()).append('\t');
            tmp.append(selector.toStringCov()).append('\n');
        }
        return tmp.toString();
    }   
//	*******************************************************
	public void save(String fileName)
	{
		FileWriter file;
		try{
			file= new FileWriter(fileName+".sel",false);
		}		
		catch(IOException ex){
			System.err.println("Error opening file. File: "+fileName+".sel");
			return;
		}				
		try {
			for(int i=0;i<size;i++)
				file.write(getSelector(i).toString()+'\n');
			file.close();
		} catch (IOException e) {
			System.err.println("Error writing file. File: "+fileName+".sel");
			e.printStackTrace();
		}
	}
//	*******************************************************
	public void save(String fileName,FArray array)
	{
		FileWriter file;
		try{
			file= new FileWriter(fileName+".txt",false);
		}		
		catch(IOException ex){
			System.err.println("Error opening file. File: "+fileName+".txt");
			return;
		}				
		try {
			for(int i=0;i<size;i++)
				file.write(getSelector(i).toString(array) + " "+getSelector(i).toStringCov()+'\n');
			file.close();
		} catch (IOException e) {
			System.err.println("Error writing file. File: "+fileName+".txt");
			e.printStackTrace();
		}
	}
//	*******************************************************
	private void extend()
	{
		Selector selectorsArrayTmp[]= new Selector[selectorsArray.length+initSize];
		System.arraycopy(selectorsArray,0,selectorsArrayTmp,0,size);
		selectorsArray=selectorsArrayTmp;
		selectorsArrayTmp=null;
	}
//	*******************************************************
	private double findSignificantQ(int searchBeam,int qMethod)
	{
		double significantQ[]=new double[searchBeam];
		int leaveCounter=searchBeam-1;
		double q;
		for(int i=0;i<size;i++)
		{
			q=Quality.calc(selectorsArray[i].posCoverage,selectorsArray[i].negCoverage,qMethod);
			if(leaveCounter>0 )
				significantQ[leaveCounter--]=q;
			else if( significantQ[0] < q)
				//leaveCounter ==0 anyway
			{
				significantQ[0]=q;
				Arrays.sort(significantQ);
			}
		}
		if(leaveCounter>0)
			Arrays.sort(significantQ);
		// START DEBUG INFO
		//for(int k=0;k<significantQ.length;k++)
		//System.out.println("SIGNIFICANT Table: "+significantQ[k]);
		// END DEBUG INFO
		return significantQ[leaveCounter]; //here is the minimal value to take
	}
//	**********************************************************
//	method leaves only specified set of selectors
	public void deleteInsignificant(int searchBeam,int qMethod)
	{
		if (size == 0 || searchBeam >= size)
			return;
		double significantQ = findSignificantQ(searchBeam, qMethod);
		// START DEBUG INFO
		//System.out.println("significantQ: "+significantQ);
		// END DEBUG INFO
		int notNull = 0;
		for (int i = 0; i < size; i++)
		{            
            if( Quality.calc(selectorsArray[i].posCoverage,selectorsArray[i].negCoverage, qMethod) < significantQ)
			{
				hashDictionary.remove(selectorsArray[i].toString());
				selectorsArray[i] = null;
			}
			else
				notNull++;
		}
		//Erase null selectors
		Selector selectorsArrayTmp[] = new Selector[(int)Math.ceil((double)notNull/(double)initSize)*initSize];
		// START DEBUG INFO
		//System.out.println("selectorsArrayTmp: "+selectorsArrayTmp.length);
		//System.out.println("notNull: "+notNull);
		// END DEBUG INFO
		int j=0;
		for (int i = 0; i < size; i++)
		{
			if (selectorsArray[i] != null)
				selectorsArrayTmp[j++]=selectorsArray[i];
		}
		selectorsArray=selectorsArrayTmp.clone();
		size=notNull;
		selectorsArrayTmp=null;
	}
//	**********************************************************
}
