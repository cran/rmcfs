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
package dmLab.mcfs.attributesRI;

import java.util.*;

public class Dictionary
{
	protected int initSize=500;
	protected HashMap <String,Integer>itemsMap;
	protected String items[];
	
	//***********************************
	public Dictionary(int defaultSize)
	{
		initSize=defaultSize;
		itemsMap=new HashMap<String,Integer>(initSize*2);
		clear();
	}
	//***********************************
	public Dictionary()
	{
		itemsMap=new HashMap<String,Integer>(initSize*2);
		clear();
	}
	//***********************************
//	***** clears dictionary
	public void clear()
	{
		itemsMap.clear();
		items=new String[initSize];
	}
//	**************************************
	public int getItem(String item)
	{
		Integer index=itemsMap.get(item);
		if(index!=null)
			return index.intValue();
		else
			return -1;
	}
//	**************************************
//	adds new String value to HashMap
	public int addItem(String item)
	{
		int index;        
		if(items.length==itemsMap.size())
			extend();
		
		if(itemsMap.containsKey(item)!=true)
		{
			index=itemsMap.size();
			items[index]=item;
			itemsMap.put(items[index],new Integer(index));
		}
		else
			index=(itemsMap.get(item)).intValue();
		
		return index;
	}
//	**************************************
//	***** returns String value from HashMap
	public String getItem(int index)
	{
		return items[index];
	}
	//**************************************
//	***** returns size of HashMap
	public int size()
	{
		return itemsMap.size();
	}
	//**************************************
	//method recreates array
	private void extend()
	{	
		String tmp[]= new String[items.length+initSize];
		System.arraycopy(items,0,tmp,0,itemsMap.size());
		items=tmp;
        tmp=null;
	}
	//**************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		final int size=itemsMap.size();//for speed
		tmp.append("dictionary #"+size).append('\n');		
		for(int i=0;i<size;i++)
			tmp.append(""+i+", "+getItem(i)).append('\n');
		return tmp.toString();
	}
	//**************************************
}
