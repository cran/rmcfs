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
package dmLab.array.loader.fileLoader;
import java.io.BufferedReader;

import dmLab.array.meta.Attribute;
import dmLab.utils.StringUtils;



public class FileLoaderArff extends FileLoader 
{
	//	************************************************
	//	*** class loads adx file into array class
	//	************************************************
	@Override
	protected boolean privateInitializator()
	{
		separator=',';
		nullLabels.clear();
		nullLabels.add("?");
		commentChar="%";
		return true;
	}
	//	************************************************
	//	*** method parses file and finds attribute and event numbers
	@Override
	protected boolean parseInputFile(BufferedReader inputFile)
	{
		int lineCount=0;
		String line="";
		boolean dataBlock=false;

		do{
			try{
				line=inputFile.readLine();
				lineCount++;
			}catch (Exception e) {
				System.out.println("Error reading input file. Line: "+lineCount);
				return false;
			}
			if(line!=null){ 
				line = trimComments(line);
				if(line.length()==0) //if line is empty
					continue;
				if(line.toLowerCase().startsWith("@attribute "))            
					attributesNumber++;
				else if(line.toLowerCase().startsWith("@data"))
					dataBlock=true;
				else if(dataBlock)
					eventsNumber++;
			}
		}while(line!=null); //end while

		System.out.println("Simple parsing has been done.");            
		return true;
	}
	//	************************************************
	//*** method reads attributes and events into memory
	@Override
	protected boolean readInputFile(BufferedReader inputFile)
	{
		int attrPointer=0;
		int eventPointer=0;
		int lineCount=0;
		String line="";
		//int eventIndex=0;        
		//char eventSeparators[]=new char[]{separator};
		boolean dataBlock=false;

		do{
			try{
				line=inputFile.readLine();
				lineCount++;
			}
			catch (Exception e) {
				System.err.println("Error reading input file.");
				return false;
			}
			if(line!=null){
				line = trimComments(line);
				if(line.length()==0) //if line is empty
					continue;

				if(line.toLowerCase().startsWith("@attribute ")){
					if(!loadAttribute(line,attrPointer)){
						System.err.println("Error reading attribute! Line: "+lineCount);
						return false;
					}
					attrPointer++;
				}
				else if(line.toLowerCase().startsWith("@data")){
					dataBlock=true;
				}else if(dataBlock){
					loadEvent(line,eventPointer);
					eventPointer++;
				}
			}
		}while(line!=null); //end while

		System.out.println("Reading file has been done.");        
		return true;   
	}
	//	************************************************
	private boolean loadAttribute(String inputLine, int attrPointer)
	{
		char attributeSeparators[]=new char[]{' ','\t',',','{','}'};
		String[] list=StringUtils.tokenizeString(inputLine,attributeSeparators,true);

		if(list.length<3 || !list[0].equalsIgnoreCase("@attribute"))
			System.err.println("Incorrect definition of attribute. Line: "+inputLine);

		String attributeName=list[1];
		String attributeType=list[2];

		int openBracketIndex=inputLine.indexOf('{');
		int closeBracketIndex=inputLine.indexOf('}');

		if(openBracketIndex<closeBracketIndex)
			attributeType="nominal";

		myArray.attributes[attrPointer].name=attributeName;//attribute name                
		if(attributeType.equalsIgnoreCase("real") || attributeType.equalsIgnoreCase("numeric"))        
			myArray.attributes[attrPointer].type = Attribute.NUMERIC;               
		else if(attributeType.equalsIgnoreCase("nominal") || attributeType.equalsIgnoreCase("string"))
			myArray.attributes[attrPointer].type = Attribute.NOMINAL;
		else
			System.err.println("Incorrect type of attribute. Line: "+inputLine);

		return true;
	}
	//  ************************************************
}

