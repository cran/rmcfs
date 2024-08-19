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
package dmLab.array.meta;

import dmLab.utils.MyDict;


public class Dictionary implements Cloneable
{
	protected MyDict dict;
	public boolean locked; 

	//***********************************
	public Dictionary()
	{
		dict = new MyDict();
		locked = false;
		initDict();
	}
	//********************************************
	protected void initDict()
	{
		dict.put("false");// default 0
		dict.put("true");// default 1
	}
	//********************************************
	public int size()
	{
		return dict.size();
	}
	//********************************************
	public void clear()
	{
		dict.init();
		initDict();
	}
	//***********************************
	public float toFloat(String value)
	{
		Float floatVal = toFloatSpecial(value);
		if(floatVal != null)
			return floatVal;
		
		Integer intVal = dict.get(value);
		if(intVal == null && locked){
			System.err.println("Dictionary does not contain: '"+value+"'");
			return(Float.NaN);
		} else if(intVal == null)
			intVal = dict.put(value);
					
		return intVal.floatValue();
	}
	//**************************************
	public float[] toFloat(String values[])
	{
		float[] retValues = new float[values.length];
		for(int i=0; i<values.length; i++)
			retValues[i] = toFloat(values[i]);
		return retValues;
	}
	//**************************************
	public String toString(float value)
	{
		String strValue = toStringSpecial(value);
		if(strValue != null)
			return strValue;		
		return dict.get(Math.round(value));		
	}
	//**************************************
	public String[] toString(float values[])
	{
		String[] retValues = new String[values.length];
		for(int i=0; i<values.length; i++)
			retValues[i] = toString(values[i]);
		return retValues;
	}
	//**************************************
	public Float toFloatSpecial(String value)
	{
		if (value==null)
			return Float.NaN;  
		else if (value.equalsIgnoreCase("NULL") || value.equalsIgnoreCase("NAN") 
				|| value.equalsIgnoreCase("NA") || value.equalsIgnoreCase("?"))
			return Float.NaN;
		else if (value.equalsIgnoreCase("+inf")||value.equalsIgnoreCase(""+Float.POSITIVE_INFINITY))
			return Float.POSITIVE_INFINITY;
		else if (value.equalsIgnoreCase("-inf")||value.equalsIgnoreCase(""+Float.NEGATIVE_INFINITY))
			return Float.NEGATIVE_INFINITY;
		else
			return null;
	}
	//**************************************
	public String toStringSpecial(float value)
	{
		if (Float.isNaN(value))
			return "?";
		else if (value == Float.POSITIVE_INFINITY)
			return ""+Float.POSITIVE_INFINITY;
		else if (value == Float.NEGATIVE_INFINITY)
			return ""+Float.NEGATIVE_INFINITY;
		else if (Float.isInfinite(value) && value > 0)
			return ""+Float.POSITIVE_INFINITY;
		else if (Float.isInfinite(value) && value < 0)
			return ""+Float.NEGATIVE_INFINITY;
		else
			return null;
	}
	//**************************************
	public Dictionary clone(){
		Dictionary retDict = new Dictionary();
		retDict.dict = dict.clone();
		return retDict;
	}
	//**************************************
	public String toString()
	{
		return dict.toString(); 
	}
	//********************************************
}
