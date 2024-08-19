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
import dmLab.utils.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;

public abstract class Array2String
{
	protected char separator=',';
	protected String nullLabel="?";
    protected String forbiddenChars[]=new String[]{"%","\\","#"};
    
	public Array2String()
	{		
	}
	//************************************
	public abstract String toString(Array container);
	{
	}
	//************************************
	protected abstract String attributesToString(Array container);
	//************************************
	protected String eventsToString(Array container)
	{
		StringBuffer buffer=new StringBuffer();
		final int eventsNumber=container.rowsNumber();
		final int attributesNumber=container.colsNumber();
		for(int i=0;i<eventsNumber;i++)
		{            
            for(int j=0;j<attributesNumber;j++)
			{
				String value=container.readValueStr(j, i);
				if(value==null || value.length()==0 || value.equalsIgnoreCase("null"))
					value=nullLabel;
				buffer.append(value);
				
				if(j!=attributesNumber-1)
					buffer.append(separator);
				else
					buffer.append('\n');
			}
		}
		return buffer.toString();		
	}
//************************************
	public boolean eventsToWriter(Array container, BufferedWriter fileWriter) throws IOException {
		final int eventsNumber=container.rowsNumber();
		final int attributesNumber=container.colsNumber();
		for(int i=0;i<eventsNumber;i++) {
			for(int j=0;j<attributesNumber;j++){
				String value=container.readValueStr(j, i);
				if(value==null || value.length()==0 || value.equalsIgnoreCase("null"))
					value=nullLabel;
				fileWriter.write(value);
				if(j!=attributesNumber-1)
					fileWriter.write(separator);
				else
					fileWriter.write('\n');
			}
		}
		return true;
	}
//************************************
	protected boolean isEmpty(Array container)
	{
		if(container.rowsNumber()==0 || container.colsNumber()==0)
		{
			System.err.println("Array is Empty!");
			return true;
		}
		return false;
	}	
	//************************************
	public void setNullLabel(String nullLabel)
	{
		this.nullLabel = nullLabel;
	}
	//************************************
	public void setSeparator(char separator)
	{
		this.separator = separator;
	}
	//************************************
    protected String getCleanAttrName(String attributeName)
    {
        String name=attributeName;
        for(int j=0;j<forbiddenChars.length;j++)
            name=StringUtils.replaceAll(name, forbiddenChars[j], Array.SPACE_CHAR);
        return name;
    }
    //************************************
}
