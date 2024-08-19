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
package dmLab.array.saver;

import dmLab.array.Array;

public class Array2CSV extends Array2String
{	
	public Array2CSV()
	{
		separator = ',';
		nullLabel = "NA";
	}
	//************************************
	@Override
    public String toString(Array container)
	{
		if(isEmpty(container))
			return null;
		
		StringBuffer buffer=new StringBuffer();
		
		buffer.append(attributesToString(container));
		buffer.append('\n');
		buffer.append(eventsToString(container));
		
		return buffer.toString();
	}
	//************************************
	@Override
    protected String attributesToString(Array container)
	{
		StringBuffer buffer=new StringBuffer();
		final int attributesNumber=container.colsNumber();
		//final int decisionIndex=container.getDecAttrIndex();
		for(int i=0;i<attributesNumber;i++)
		{
			buffer.append(container.attributes[i].name);
			if(i!=attributesNumber-1)				
				buffer.append(',');
		}		
		return buffer.toString();
		
	}
	//************************************
}
