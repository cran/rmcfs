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
package dmLab.array.loader.fileLoader;
import java.io.BufferedReader;
import java.io.File;

import dmLab.array.meta.Attribute;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;



public class FileLoaderArff extends FileLoader 
{
	//	************************************************
	//	*** load arff file into array class
	//	************************************************
	@Override
	protected boolean myInit()
	{
		separator=',';
		nullLabels.clear();
		nullLabels.add("?");
		commentChar="%";
		fileType = FileType.ARFF;
		return true;
	}
	//	************************************************
	//	*** method parses file and finds attribute and event numbers
	@Override
	protected boolean parseInputFile(File inputFile)
	{
		int lineCount=0;
		String line="";
		boolean dataBlock=false;

		BufferedReader fileReader;
		if((fileReader = FileUtils.openFile(inputFile)) == null){
			FileUtils.closeFile(fileReader);
			return false;
		}

		do{
			try{
				line = fileReader.readLine();
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
		
		if(!FileUtils.closeFile(fileReader))
			return false;
		
		return true;
	}
	//	************************************************
	//*** method reads attributes and events into memory
	@Override
	protected boolean readInputFile(File inputFile)
	{
		int attrPointer=0;
		int eventPointer=0;
		int lineCount=0;
		String line="";
		//int eventIndex=0;        
		//char eventSeparators[]=new char[]{separator};
		boolean dataBlock=false;

		BufferedReader fileReader;
		if((fileReader = FileUtils.openFile(inputFile)) == null){
			FileUtils.closeFile(fileReader);
			return false;
		}
		
		do{
			try{
				line = fileReader.readLine();
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
		
		if(!FileUtils.closeFile(fileReader))
			return false;
		
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
	@Override
	protected boolean readHeaderFile(File inputFile) {
		return true;
	}
	//	************************************************
	@Override
	protected File getDataFile(File inputFile) {
		return inputFile;
	}
	//	************************************************
}
