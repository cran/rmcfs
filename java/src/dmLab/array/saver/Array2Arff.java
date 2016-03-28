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
package dmLab.array.saver;

import dmLab.array.Array;
import dmLab.array.meta.Attribute;

public class Array2Arff extends Array2String
{	    
    public Array2Arff()
	{
		separator=',';
		nullLabel="?";       
	}
	//************************************
	@Override
    public String toString(Array container)
	{
		if(isEmpty(container))
			return null;
		
		StringBuffer buffer=new StringBuffer();
		
		buffer.append("@relation ");
        final int decisionIndex=container.getDecAttrIdx(); 
        if(decisionIndex>0 && decisionIndex<container.colsNumber())            
            buffer.append(container.attributes[container.getDecAttrIdx()].name);
        else
            buffer.append("dmLabRelation");
                    
		buffer.append('\n').append('\n');		
		buffer.append(attributesToString(container)).append('\n');

		buffer.append("@data").append('\n').append('\n');
		buffer.append(eventsToString(container));
		buffer.append('\n');		
		return buffer.toString();
	}
	//************************************
	@Override
    protected String attributesToString(Array array)
	{
		if(!array.domainsCreated())
			array.findDomains();

		StringBuffer buffer=new StringBuffer();
		final int attributesNumber=array.colsNumber();
		//final int decisionIndex=container.getDecAttrIndex();
		for(int i=0;i<attributesNumber;i++)
		{
            buffer.append(" @attribute ").append(getCleanAttrName(array.attributes[i].name));
			buffer.append('\t').append(type2String(array.attributes[i].type));
			if(array.attributes[i].type==Attribute.NOMINAL)
			{
				String values[] = array.getDomainStr(i);
				buffer.append(' ').append('{');
                boolean valueAdded=false;
				for(int j=0;j<values.length;j++)
				{
					//do not add nullLabel to domain in arff file
                    if(!values[j].equalsIgnoreCase(nullLabel))
                    {
                        if(valueAdded)
                            buffer.append(',');
                        buffer.append(values[j]);
                        valueAdded=true;
                    }
				}
				
				buffer.append('}');
			}
			buffer.append('\n');
		}		
		return buffer.toString();		
	}
	//************************************
	private String type2String(short attributeType)
	{
		if(attributeType==Attribute.NOMINAL)
			return "";
		else if(attributeType==Attribute.NUMERIC || attributeType==Attribute.INTEGER)
			return "real";
		else
			return "";
	}
	//************************************
	
}

