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

public class ComplexList implements Cloneable
{
	/** init size for array*/
	protected int initSize=500;
	/** array with Complexes */
	protected Complex list[]; //for speed
	/** size of the list */
	protected int size;
	//***********************************
	/**
	 * standard constructor initializes array
	 */
	public ComplexList()
	{
		clear();
	}
	//***********************************
	/**
	 * constructor that uses initSize as default value of list's length
	 * @param initSize int
	 */
	public ComplexList(int initSize)
	{
		this.initSize=initSize;
		clear();
	}
	//***********************************
	/**
	 * method clears an array
	 */
	public void clear()
	{
		size=0;
		list=new Complex[initSize];
	}
	//***********************************
	/**
	 * method adds new integer value into the list
	 * @param integer int new element
	 * @return int current size of the list
	 */
	public int add(Complex complex)
	{
		if(size==list.length)
			extend();
		
		list[size++]=complex;
		return size;
	}
	//***********************************
	/**
	 * returns value from the list
	 * @param index int index of the value
	 * @return complex from the list
	 */
	public Complex get(int index)
	{
		if(index>=0 && index<size)
			return list[index];
		else
			return null;
	}
	//***********************************
	/**
	 * method reallocates the array
	 */
	private void extend()
	{
		Complex listTmp[]= new Complex[list.length+initSize];
		System.arraycopy(list,0,listTmp,0,size);
		list=listTmp;
		listTmp=null;
	}
	//***********************************
	/**
	 * returns size of the list
	 * @return int list size
	 */
	public int size()
	{
		return size;
	}
	//***********************************
	@Override
    public ComplexList clone()
	{
		ComplexList cloneList = new ComplexList(size);
		for(int i=0;i<size;i++)
			cloneList.list[i]=list[i].clone();
		return cloneList;
	}
	//***********************************
	public boolean trim()
	{
		int nulls=0;
		for(int i=0;i<size;i++)
			if(list[i]==null)
				nulls++;
		
		Complex listTmp[]= new Complex[size-nulls];
		for (int i = 0, j = 0; i < size; i++)
			if(list[i]!=null)
				listTmp[j++]=list[i];
		
		list=listTmp;
		listTmp=null;
		size=list.length;
		return true;
	}
	//***********************************
	public boolean remove(int index)
	{
		list[index]=null;
		return true;
	}
//	***********************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();		
		for(int i=0;i<size;i++)
			if(list[i]!=null)
				tmp.append(list[i].toString()).append('\n');
			else
				tmp.append("null").append('\n');
		return tmp.toString();
	}
//	***********************************
	public int contains(Complex complex)
	{
		for(int i=0;i<size;i++)
			if(list[i]==complex)
				return i;
		return -1;	
	}
	//***********************************
}
