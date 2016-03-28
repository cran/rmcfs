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

import dmLab.array.Array;
import dmLab.array.meta.Attribute;
import dmLab.utils.StringUtils;



public class FileLoaderCSV extends FileLoader 
{
	public boolean firstLineContainsAttributes;
	public String defaultAttributeName;

	public boolean consequentSeparatorsTreatAsOne; 
	//	************************************************
	//	*** class loads adx file into array class
	public FileLoaderCSV()
	{
		super();
	}
	//	************************************************
	@Override
	protected boolean privateInitializator()
	{
		separator=',';
		nullLabels.clear();
		nullLabels.add("");
		nullLabels.add("?");
		nullLabels.add("NaN");
		nullLabels.add("NA");
		nullLabels.add("Null");        
		firstLineContainsAttributes=true;
		defaultAttributeName="attr";
		trimComments=false;
		consequentSeparatorsTreatAsOne=false;
		return true;
	}
	//	************************************************
	//	*** method parses file and finds attribute and event numbers
	@Override
	protected boolean parseInputFile(BufferedReader inputFile)
	{
		int lineCount=0;
		String line="";
		char separators[]=new char[]{separator};
		boolean firstLine=true;

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
				if(firstLine){
					String[] list=StringUtils.tokenizeString(line,separators,consequentSeparatorsTreatAsOne);
					attributesNumber=list.length;
					firstLine=false;    
				}else{
					String[] list=StringUtils.tokenizeString(line,separators,consequentSeparatorsTreatAsOne);
					if(attributesNumber<list.length)
						attributesNumber=list.length;
					eventsNumber++;
				}
			}
		}while(line!=null); //end while
		System.out.println("Simple Parsing Done!");

		return true;
	}
	//	************************************************
	//*** method reads attributes and events into memory
	@Override
	protected boolean readInputFile(BufferedReader inputFile)
	{
		int lineCount=0;
		String line="";
		char separators[]=new char[]{separator};
		boolean firstLine=true;
		int eventIndex=0;

		if(!firstLineContainsAttributes){
			for(int i=0;i<attributesNumber;i++){
				myArray.attributes[i].name=defaultAttributeName+i;
				myArray.attributes[i].type=Attribute.NOMINAL;
			}   
		}

		do{
			try{
				line=inputFile.readLine();
				lineCount++;
			}catch (Exception e) {
				System.err.println("Error reading input file. Line: "+lineCount);
				return false;
			}
			if(line!=null){ 

				line = trimComments(line);
				if(line.length()==0) //if line is empty
					continue;

				String[] list=StringUtils.tokenizeString(line,separators,consequentSeparatorsTreatAsOne);

				if(firstLine && firstLineContainsAttributes){
					for(int i=0;i<attributesNumber;i++){
						String name;
						if(i>=list.length){
							name=defaultAttributeName+i;
						}else{
							name=list[i];
						}
						if(name.equalsIgnoreCase(""))
							name=defaultAttributeName+i;
						myArray.attributes[i].name=name.trim().replaceAll(" ", Array.SPACEVALUE);
						myArray.attributes[i].type=Attribute.NOMINAL;
					}
					firstLine=false;    
				}else{
					//loading events
					final int listSize=list.length;
					for(int i=0;i<attributesNumber;i++){                    
						String value;
						if(i>=listSize){
							value=defaultNullLabel;
						}else{	
							value=list[i];                	
							if(nullLabels.containsIgnoreCase(value))
								value=defaultNullLabel;
						}
						myArray.writeValueStr(i, eventIndex, value);
					}
					eventIndex++;
				}   
			} 
		}while(line!=null); //end while

		System.out.println("Reading file is done!");        
		return true;   
	}
	//	************************************************
}

