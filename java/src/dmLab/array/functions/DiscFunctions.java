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
package dmLab.array.functions;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import dmLab.array.FArray;
import dmLab.array.meta.Attribute;
import dmLab.array.meta.DiscRanges;
import dmLab.discretizer.DiscretizerParams;
import dmLab.utils.ArrayUtils;
import dmLab.utils.FileUtils;
import dmLab.utils.helpers.ParsingException;

public class DiscFunctions
{
	private static final String discsRangesExtension="dsc";		
	//	*********************************
	public DiscFunctions()
	{
	}
	//************************************************************
	public static boolean findRanges(FArray array, DiscretizerParams discParams)
	{
		if(discParams.discIntervals() <= 0 || discParams.discAlgorithm() < 1)
			return false;

		final int columns=array.colsNumber();
		final int decAttrIndex=array.getDecAttrIdx();
		array.discRanges = new DiscRanges[array.attributes.length];
		
		for(int i=0; i<columns; i++){
			if(array.attributes[i].type==Attribute.NUMERIC){
				float values[] = array.getColumn(i);
				float decision[] = array.getColumn(decAttrIndex);
				array.discRanges[i] = new DiscRanges(); 
				if(array.discRanges[i].find(values, decision, discParams)){
					if(discParams.verbose)
						System.out.println("Attribute Discretized: "+array.attributes[i].name + " ranges: "+ Arrays.toString(array.discRanges[i].getRanges()));
				}				
			}			
		}
		return true;
	}
	//************************************************************
	public static boolean applyRanges(FArray array)
	{
		if(array.isDiscretized()){
			for(int i=0;i<array.attributes.length;i++){
				applyRanges(array,i);
			}
		}
		return true;
	}
	//************************************************************
	private static boolean applyRanges(FArray array, int column)
	{		
		if(array.isDiscretized()){
			if(array.isDiscretized(column)){
				final int rows = array.rowsNumber();
				for(int i=0; i<rows; i++){
					float valueNew = array.discRanges[column].getDiscreteValue(array.readValue(column,i));
					array.writeValue(column, i, valueNew);
				}
			}else
				return false;
		}else
			return false;
		
		return true;
	}
	//************************************************************
	public static boolean copyRanges(FArray sourceArray, FArray destinationArray)
	{		
		if(sourceArray.isDiscretized() && sourceArray.colsNumber()==destinationArray.colsNumber()){			
			final int cols = destinationArray.colsNumber();
			destinationArray.discRanges = new DiscRanges[cols];			
			for(int i=0; i<cols; i++){
				if(sourceArray.discRanges[i].getSize() > 0)
					destinationArray.discRanges[i] = sourceArray.discRanges[i].clone();
			}
		} else
			return false;

		return true;
	}
	//************************************************************
	public static String toStringRanges(FArray array)
	{
		StringBuffer tmp=new StringBuffer();
		final int cols = array.colsNumber();
		for(int i=0; i<cols; i++){
			tmp.append("Attribute: "+i).append(" # ").append(array.attributes[i].name).append('\n');
			boolean discretized = false;
			if(array.isDiscretized(i)){
				discretized = true;
			}
			tmp.append("Discretized: "+discretized).append('\n');
			if(discretized){
				tmp.append("Ranges: "+array.discRanges[i].getSize()).append('\n');				
				tmp.append(array.discRanges[i].toString());
				tmp.append('\n');
			}
		}
		return tmp.toString();		
	}
	//	************************************************************
	public static void saveRanges(FArray array, String inputFileName)
	{
		String fileName;    
		String ext=FileUtils.getFileExtension(inputFileName);
		if(ext.equalsIgnoreCase(discsRangesExtension))
			fileName=inputFileName;
		else
			fileName=inputFileName+"."+discsRangesExtension;

		FileWriter file;
		try{
			file= new FileWriter(fileName,false);
		}
		catch(IOException ex){
			System.err.println("Error opening file. File: "+fileName);
			return;
		}
		try {
			file.write(toStringRanges(array));
			file.close();
		} catch (IOException e) {
			System.err.println("Error writing file. File: "+fileName);
			e.printStackTrace();
		}	      		
	}
	//	************************************************************
	public static DiscRanges[] loadRanges(FArray array, String inputFileName) throws ParsingException
	{
		String fileName;    
		String ext=FileUtils.getFileExtension(inputFileName);
		if(ext.equalsIgnoreCase(discsRangesExtension))
			fileName=inputFileName;
		else
			fileName=inputFileName+"."+discsRangesExtension;				

		BufferedReader inputFile=null;
		try{
			inputFile = new BufferedReader(new FileReader(fileName));
		}
		catch (Exception e) {
			System.err.println("Error opening file. File: "+fileName);
			return null;
		}
		
		String line="";
		int column=0;
		int rangesNumber=0;
		float ranges[]=null;

		DiscRanges discRanges[] = new DiscRanges [array.colsNumber()];
		try
		{
			while (null!=(line=inputFile.readLine()))
			{
				line=line.trim();
				int hashIndex=line.indexOf('#');
				if(hashIndex!=-1)
					line=line.substring(0,hashIndex);					
				if(line.length()>0)
				{
					if(line.indexOf(':')==-1 && ranges!=null)
					{
						double tmpArray[]=ArrayUtils.toDoubleArray(line.trim());
						if(tmpArray==null || tmpArray.length<ranges.length)
						{
							inputFile.close();
							throw new ParsingException("Error parsing file. File: "+fileName+"! Incorrect ranges definition line:" + line);
						}
						for(int i=0;i<ranges.length;i++)
							ranges[i]=(float)tmpArray[i];
						discRanges[column]=new DiscRanges(ranges);						
						ranges=null;
					}
					else if( line.substring( 0,line.indexOf(':') ).trim().equalsIgnoreCase("Attribute") )
					{
						column=Integer.parseInt(line.substring(line.indexOf(':')+1,line.length()).trim());
						ranges=null;
						rangesNumber=0;
					}
					else if( line.substring( 0,line.indexOf(':') ).trim().equalsIgnoreCase("Discretized") )
					{
						boolean discretized=Boolean.getBoolean(line.substring(line.indexOf(':')+1,line.length()).trim());
						if(discretized==false)
							discRanges[column]=new DiscRanges();
					}
					else if( line.substring( 0,line.indexOf(':') ).trim().equalsIgnoreCase("Intervals") ||
							line.substring( 0,line.indexOf(':') ).trim().equalsIgnoreCase("Ranges"))
					{
						rangesNumber=Integer.parseInt(line.substring(line.indexOf(':')+1,line.length()).trim());
						if(rangesNumber<=0)
						{
							inputFile.close();
							throw new ParsingException("Error parsing file. File: "+fileName+". Attribute: "+ array.attributes[column].name +".  Ranges Number is <= 0.");
						}
						ranges=new float [rangesNumber];
					}
				}
			}//end while
			inputFile.close();
		}		
		catch (Exception e) {
			System.err.println("Error reading file. File: "+fileName);
			return null;
		}

		array.discRanges = discRanges;
		return discRanges;
	}
	//	************************************************************
}
