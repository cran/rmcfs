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
import dmLab.array.meta.Attribute;

import java.io.BufferedWriter;
import java.io.IOException;

public class Array2ADX extends Array2String
{	
	public Array2ADX()
	{
		separator = ',';
		nullLabel = "?";
	}
	//************************************
	@Override
    public String toString(Array array)
	{
		if(isEmpty(array))
			return null;
		
		StringBuffer buffer=new StringBuffer();
		buffer.append("attributes").append('\n');
		buffer.append("{").append('\n');
		buffer.append(attributesToString(array));
		buffer.append("}").append('\n');
		
		buffer.append("events").append('\n');
		buffer.append("{").append('\n');
		buffer.append(eventsToString(array));
		buffer.append("}").append('\n');

		return buffer.toString();
	}
	//************************************
	public boolean toWriter(Array array, BufferedWriter fileWriter){
		if(isEmpty(array))
			return false;
		try {
			fileWriter.write("attributes" + '\n');
			fileWriter.write("{" + '\n');
			fileWriter.write(attributesToString(array));
			fileWriter.write("}" + '\n');
			fileWriter.write("events"  + '\n');
			fileWriter.write("{" + '\n');
			eventsToWriter(array, fileWriter);
			fileWriter.write("}"  + '\n');
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}
	//************************************
	@Override
    protected String attributesToString(Array array)
	{
		StringBuffer buffer=new StringBuffer();
		final int attributesNumber=array.colsNumber();
		final int decisionIndex=array.getDecAttrIdx();
		for(int i=0;i<attributesNumber;i++){
			buffer.append(' ').append(getCleanAttrName(array.attributes[i].name));
			buffer.append('\t').append(type2String(array.attributes[i].type));
			buffer.append('\t').append("["+array.attributes[i].weight+"]");
			if(i==decisionIndex){
                buffer.append('\t').append("decision");                
                buffer.append(decValues2String(array.getDecValuesStr()));
			}
			buffer.append('\n');
		}		
		return buffer.toString();
		
	}
	//************************************
	private String decValues2String(String decValues[]){
		StringBuffer buffer=new StringBuffer();
		if(decValues!=null){
			buffer.append('(');
			for(int j=0;j<decValues.length;j++){
				buffer.append(decValues[j]);
				if(j!=decValues.length-1)
					buffer.append(',');
			}
			buffer.append(')');
		}
		return buffer.toString();
	}
	//************************************
    private String type2String(short attributeType)
    {
        if(attributeType==Attribute.NOMINAL)
            return "nominal";
        else if(attributeType==Attribute.NUMERIC || attributeType==Attribute.INTEGER)
            return "numeric";
        else
            return "";
    }
    //************************************
    
}
