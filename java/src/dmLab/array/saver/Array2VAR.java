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

import java.util.Arrays;

import dmLab.array.Array;
import dmLab.array.meta.Attribute;
import dmLab.utils.StringUtils;

public class Array2VAR extends Array2String
{	    
    public int numericRanges=3;
    
    public Array2VAR()
	{
		separator=',';
		nullLabel="";
	}
	//************************************
	@Override
    public String toString(Array container)
	{
		if(isEmpty(container))
			return null;
		
		StringBuffer buffer=new StringBuffer();
		buffer.append(attributesToString(container)).append('\n');
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
            String name=getCleanAttrName(container.attributes[i].name);
            buffer.append(StringUtils.toCharArray(name,12));
            short attrType=container.attributes[i].type;
			String type;
            float[] floatColumn=null;
            
            if(attrType==Attribute.NUMERIC)
            {
                //find and define data range
                String[] strColumn=container.getColumnStr(i);
                floatColumn=new float[strColumn.length];
                for(int j=0;j<strColumn.length;j++)
                    floatColumn[j]=Float.parseFloat(strColumn[j]);                
                
                Arrays.sort(floatColumn);
                //min-max
                type=""+floatColumn[0]+"-"+floatColumn[floatColumn.length-1];
            }   
            else
                type=type2String(attrType);
            
            buffer.append(StringUtils.toCharArray(type,12));
            buffer.append('\n');
            //opis zajmuje odpowiednia dlugosc
            String description=name+" attribute";
            buffer.append(StringUtils.toCharArray(description,55)).append('\n');
            
			if(attrType==Attribute.NOMINAL)
			{
				String values[] = container.getDomainStr(i);
				for(int j=0;j<values.length;j++)
				{                    
                   buffer.append(' ').append(StringUtils.toCharArray(values[j],12));
                   buffer.append(StringUtils.toCharArray("1",17));
                   buffer.append('\n');
				}				
			}
            else if(attrType==Attribute.INTEGER)
            { //nothing to do if integer               
            }
            else if(attrType==Attribute.NUMERIC)
            {   //define ranges               
               
                int start=0,stop=0;
                int step=floatColumn.length/numericRanges;
                
                while(stop<floatColumn.length-1)
                {    
                    stop=start+step;
                    if(stop>=floatColumn.length)
                        stop=floatColumn.length-1;
                    
                    if(floatColumn[start]!=floatColumn[stop])
                    {
                        String rangeLabel="["+floatColumn[start]+"-"+floatColumn[stop]+"]";
                        buffer.append(' ').append(StringUtils.toCharArray(rangeLabel,12));
                        buffer.append(StringUtils.toCharArray("1",17));
                        buffer.append('\n');
                    }
                    start=stop;
                                        
                }
                
            }                       
            buffer.append("�����").append('\n');
		}
        
        buffer.append("�����").append('\n').append('\n');        
		return buffer.toString();		
	}
	//************************************
	private String type2String(short attributeType)
	{
		if(attributeType==Attribute.NOMINAL)
			return "nominal";
		else if(attributeType==Attribute.INTEGER)
			return "integer";
        else if(attributeType==Attribute.NUMERIC)
            return "";
		else
			return "";
	}
	//************************************
	
}
