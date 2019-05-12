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
package dmLab.array.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ADXDomainSet implements Cloneable
{
	protected ArrayList<ADXDomainValue> valuesList;
	protected HashMap<Float, ADXDomainValue> valuesMap;
	//	***********************************
	public ADXDomainSet()
	{
		valuesMap=new HashMap<Float,ADXDomainValue>();
		valuesList=new ArrayList<ADXDomainValue>();
	}
//	***********************************
	public int addValue(ADXDomainValue domainValue)
	{			
		valuesList.add(domainValue);
		valuesMap.put(domainValue.value,domainValue);
		return valuesList.size();
	}
//	***********************************
    public boolean contains(float value)
    {
        return valuesMap.containsKey(value);
    }
//  ***********************************    
	public ADXDomainValue getDomainValue(float value)
	{
		return valuesMap.get(value);
	}
//	***********************************
	public ADXDomainValue getDomainValue(int valueIndex)
	{
		return valuesList.get(valueIndex);
	}
//	***********************************
	public int size()
	{
		return valuesList.size();
	}
	//***********************************
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		final int size = size();
		if(size==0)
			return "";
		
		for(int j=0;j<size;j++)
			tmp.append(valuesList.get(j).toString()).append('\n');

		return tmp.toString();		
	}
	//**********************************
	public boolean sort()
	{
		Collections.sort(valuesList);
		return true;
	}
	//**********************************
	public float[] getValues()
	{
		final int size = size();
		float values[]=new float[size];
		for(int i=0;i<size;i++)
			values[i]=valuesList.get(i).value;
			
		return values;
	}
	//**********************************
	@SuppressWarnings("unchecked")
	public ADXDomainSet clone(){
		ADXDomainSet domainSet = new ADXDomainSet();		
		domainSet.valuesList = (ArrayList<ADXDomainValue>) valuesList.clone();        
		domainSet.valuesMap = (HashMap<Float, ADXDomainValue>) valuesMap.clone();                
		return domainSet;
	}
//	*******************************************************	

}
