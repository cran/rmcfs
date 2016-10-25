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
import dmLab.utils.MyString;
import dmLab.utils.StringUtils;



public class FileLoaderADX extends FileLoader 
{
	private boolean allDecision=false;
	//	************************************************
	//	*** class loads adx file into array class
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
		commentChar="#";
		return true;
	}
	//	************************************************
	//	*** method parses file and finds attribute and event numbers
	@Override
	protected boolean parseInputFile(BufferedReader inputFile)
	{
		boolean attributeSection=false;
		boolean eventSection=false;
		boolean bracketOpen=false;
		int lineCount=0;
		int startLine,stopLine;
		String line="";

		do{
			try{
				line=inputFile.readLine();
				lineCount++;
			}catch (Exception e) {
				System.err.println("Error reading input file.");
				return false;
			}
			if(line!=null){

				line = trimComments(line);
				if(line.equalsIgnoreCase("")) //if line is empty
					continue;

				// finding keywords Section of interpreting problems/key words/brackets
				if( (line.toLowerCase().indexOf("attributes")!=-1 || line.toLowerCase().indexOf("events")!=-1) && line.toLowerCase().indexOf("{")!=-1)
				{
					if(line.toLowerCase().indexOf("attributes") > line.toLowerCase().indexOf("{")){
						System.err.println("Bracket Before Declaration of 'attributes' section! Line: "+String.valueOf(lineCount));
						return false;
					}
					else if(line.toLowerCase().indexOf("attributes")!=-1){
						attributeSection=true;
						bracketOpen=true;
					}

					if(line.toLowerCase().indexOf("events") > line.toLowerCase().indexOf("{")){
						System.err.println("Bracket Before Declaration of 'events' section! Line: "+String.valueOf(lineCount));
						return false;
					}else if(line.toLowerCase().indexOf("events")!=-1){
						eventSection=true;
						bracketOpen=true;
					}
				}else{
					if(line.toLowerCase().indexOf("attributes")!=-1) //attribute section
						attributeSection=true;
					else if(line.toLowerCase().indexOf("events")!=-1) //event section
						eventSection=true;

					if(line.toLowerCase().indexOf("{")!=-1 && (attributeSection==false && eventSection==false)){
						System.err.println("Unexpected Bracket Opening! Line: "+String.valueOf(lineCount));
						return false;
					}else if(line.toLowerCase().indexOf("}")!=-1 && (attributeSection==false && eventSection==false)){
						System.err.println("Unexpected Bracket Closing! Line "+String.valueOf(lineCount));
						return false;
					}

					if(line.toLowerCase().indexOf("{")!=-1 && (attributeSection==true || eventSection==true)){
						bracketOpen=true;
					}else if(line.toLowerCase().equalsIgnoreCase("}")==true && (attributeSection==true || eventSection==true)){
						bracketOpen=false;
						attributeSection=false;
						eventSection=false;
					}
				}
				if(bracketOpen==true){
					// Finding start and stop section
					startLine=line.indexOf("{")+1;
					stopLine=line.indexOf("}");
					if(startLine==-1)
						startLine=0;
					if(stopLine==-1)
						stopLine=line.length();
					if(startLine==stopLine)
						continue;
					//  parsing attributes section
					if(attributeSection==true){
						if(line.substring(startLine,stopLine).equalsIgnoreCase("")==false)
							if(line.indexOf("ignore")==-1){
								attributesNumber++;
							}else{
								ignoredAttributesNumber++;
							}
						if(line.endsWith("}")){
							bracketOpen=false;
							attributeSection=false;
						}
					}
					// parsing events section
					if(eventSection==true){
						if(line.substring(startLine,stopLine).equalsIgnoreCase("")==false)
							eventsNumber++;
						if(line.endsWith("}")){
							bracketOpen=false;
							eventSection=false;
						}
					}
				}//end if(bracketOpen==true)
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
		int attrPointer=0;
		int eventPointer=0;
		boolean attributeSection=false;
		boolean eventSection=false;
		boolean bracketOpen=false;
		int lineCount=0;
		int attributeCount=0;
		int startLine,stopLine;
		String line="";
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
				// Section of line finding comments
				line = trimComments(line);
				if(line.equalsIgnoreCase("")) //if line is empty
					continue;

				// finding keywords
				if(line.toLowerCase().indexOf("attributes")!=-1) //attribute section
					attributeSection=true;
				else if(line.toLowerCase().indexOf("events")!=-1) //event section
					eventSection=true;

				if(line.toLowerCase().indexOf("{")!=-1 && (attributeSection==true || eventSection==true)){
					bracketOpen=true;
				}else if(line.toLowerCase().equalsIgnoreCase("}")==true && (attributeSection==true || eventSection==true)){
					bracketOpen=false;
					attributeSection=false;
					eventSection=false;
				}

				if(bracketOpen==true){
					// Finding start and stop section
					startLine=line.indexOf("{")+1;
					stopLine=line.indexOf("}");
					if(startLine==-1)
						startLine=0;
					if(stopLine==-1)
						stopLine=line.length();
					if(startLine==stopLine)
						continue;
					//  Reading attributes section
					if(attributeSection==true){
						if(line.substring(startLine,stopLine).endsWith("ignore")){
							ignoredAttributeMask[attributeCount++]=true;
						}else{
							if(!loadAttribute(line.substring(startLine,stopLine),attrPointer)){
								System.err.println("Error reading attribute! Line: "+lineCount);
								return false;
							}
							ignoredAttributeMask[attributeCount++]=false;
							attrPointer++;						
						}

						if(line.endsWith("}")){
							bracketOpen=false;
							attributeSection=false;
						}
					}//end if
					// Reading events section
					if(eventSection==true){
						if(!loadEvent(line.substring(startLine,stopLine), eventPointer)){
							System.err.println("Error reading event. Line: "+lineCount);
							return false;
						}
						if(line.endsWith("}")){
							bracketOpen=false;
							eventSection=false;
						}
						eventPointer++;
					}//end if
				}//end if(bracketOpen==true)
			}
		}while(line!=null); //end while

		if(allDecision==true)
			myArray.setAllDecValues();
		System.out.println("Reading file has been done.");

		return true;
	}

	//	************************************************
	//	*** this method reads info about one attribute
	private boolean loadAttribute(String inputLine, int attrPointer)
	{
		String decisionValues[]=null;
		char separators[]=new char[]{' ','\t'};        
		String[] list=StringUtils.tokenize(inputLine,separators,new char[] {'\"','\''});        

		if(list[list.length-1].equalsIgnoreCase("ignore")==true)
			return true;

		for(int i=0;i<list.length;i++)
		{
			String label=list[i];
			if(i==0){
				MyString s = new MyString(label);
				s.remove('\'');
				s.remove('\"');
				myArray.attributes[attrPointer].name=s.toString();
			}
			else if(i==1) //attribute type
			{
				if(Attribute.convert(label)==-1){
					System.err.println("Incorrect type of attribute: "+list[0]+" type: "+label);
					return false;
				}
				else
					myArray.attributes[attrPointer].type = Attribute.convert(label);
			}else if(i==2){
				//attribute role
				if(label.equalsIgnoreCase("ignore")==false && label.toLowerCase().startsWith("decision")==false){
					System.err.println("Incorrect role of attribute: "+list[0]+" role: "+label);
					return false;
				}else{					
					if(label.toLowerCase().startsWith("decision")==true){
						if(label.toLowerCase().equalsIgnoreCase("decision")){
							myArray.setDecAttrIdx(attrPointer);
							allDecision = true;
						}else if((decisionValues = decodeDecValues(label))!=null){
							myArray.setDecAttrIdx(attrPointer);
							if(decisionValues.length==1 && decisionValues[0].equalsIgnoreCase("all"))
								allDecision = true;
							else
								myArray.setDecValues(decisionValues);
						}else{
							System.err.println("Error decoding. Word: "+label);
							return false;
						}
					}                    
				}
			}
		} 
		if(myArray.attributes[attrPointer].type==Attribute.UNKNOWN){
			System.err.println("Type is not dedined for attribute: "+list[0]);
			return false;
		}
		return true;
	}
	//	************************************************
	//	**** decodes decision values where input string contains commas
	private String[] decodeDecValues(String inputString)
	{
		String valuesChain;

		//numeric target
		if(inputString.trim().equalsIgnoreCase("decision"))
			return null;

		if(inputString.indexOf("(")==-1 || inputString.indexOf(")")==-1){
			System.err.println("Missing bracket!");
			return null;
		}
		else if(inputString.indexOf(")") < inputString.indexOf("(")){
			System.err.println("Unexpected bracket closing.");
			return null;
		}

		valuesChain=inputString.substring( inputString.indexOf("(")+1,inputString.indexOf(")")).trim();
		if(valuesChain.length()==0){
			System.err.println("Decision values are not defined.");
			return null;
		}
		String[] list=StringUtils.tokenizeString(valuesChain,new char[]{separator},false);
		final int listSize=list.length;
		String[] decisionValues=new String[listSize];

		for (int i = 0; i < listSize; i++){
			if (list[i].length() != 0){
				decisionValues[i] = list[i];
			}else{
				System.err.println("Missing decision value!");
				return null;
			}
		}
		return decisionValues;
	}
	//	************************************************
}

