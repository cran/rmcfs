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
import dmLab.array.meta.AttributeRole;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;



public class FileLoaderADX extends FileLoader 
{
	private boolean allDecision = false;
	//	************************************************
	//	*** load adx file into array class
	//	************************************************	
	@Override
	protected boolean myInit()
	{
		separator=',';
		nullLabels.clear();
		nullLabels.add("");
		nullLabels.add("?");
		nullLabels.add("NaN");
		nullLabels.add("NA");
		nullLabels.add("Null");
		commentChar="#";
		fileType = FileType.ADX;
		return true;
	}
	//	************************************************
	//	*** method parses file and finds attribute and event numbers
	@Override
	protected boolean parseInputFile(File inputFile)
	{
		boolean attributeSection=false;
		boolean eventSection=false;
		boolean bracketOpen=false;
		int lineCount=0;
		int startLine,stopLine;
		String line="";

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
		
		if(!FileUtils.closeFile(fileReader))
			return false;
		
		return true;
	}
	//	************************************************
	//*** method reads attributes and events into memory
	@Override
	protected boolean readInputFile(File inputFile)
	{
		int attrIndex=0;
		int eventIndex=0;
		boolean attributeSection=false;
		boolean eventSection=false;
		boolean bracketOpen=false;
		int lineCount=0;
		int attributeCount=0;
		int startLine,stopLine;
		String line="";

		BufferedReader fileReader;
		if((fileReader = FileUtils.openFile(inputFile)) == null)
			return false;
		
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
							if(!loadAttribute(line.substring(startLine,stopLine),attrIndex)){
								System.err.println("Error reading attribute! Line: "+lineCount);
								return false;
							}
							ignoredAttributeMask[attributeCount++]=false;
							attrIndex++;						
						}

						if(line.endsWith("}")){
							bracketOpen=false;
							attributeSection=false;
						}
					}//end if
					// Reading events section
					if(eventSection==true){
						if(!loadEvent(line.substring(startLine,stopLine), eventIndex)){
							System.err.println("Error reading event. Line: "+lineCount);
							return false;
						}
						if(line.endsWith("}")){
							bracketOpen=false;
							eventSection=false;
						}
						eventIndex++;
					}//end if
				}//end if(bracketOpen==true)
			}
		}while(line!=null); //end while

		if(allDecision==true){
			myArray.setAllDecValues();
		}
		
		if(!FileUtils.closeFile(fileReader))
			return false;
		
		return true;
	}

	//	************************************************
	//	*** this method reads info about one attribute
	private boolean loadAttribute(String inputLine, int attrPointer)
	{
		AttributeRole attr = parseAttribute(inputLine);
		if(attr != null){
			myArray.attributes[attrPointer] = (Attribute)attr;
			if(attr.role == AttributeRole.ROLE_DECISION){
				myArray.setDecAttrIdx(attrPointer);
				if(attr.decValues != null && attr.decValues.length>0){
					myArray.setDecValues(attr.decValues);
				}else{
					allDecision = true;
				}
			}
		}else
			return false;
			
		return true;
	}
	//	************************************************
	public static AttributeRole parseAttribute(String inputString)
	{
		AttributeRole attr = new AttributeRole(); 
		String decisionValues[] = null;
		inputString = inputString.trim();
		
		int decisionIndex = inputString.toLowerCase().lastIndexOf("decision");
		String decision = "";
		
		//if decision attribute
		if(decisionIndex > 0){
			decision = inputString.substring(decisionIndex);			
			attr.role = AttributeRole.ROLE_DECISION;						
			decisionValues = parseDecValues(decision, ',');
			if(decisionValues == null){
				System.err.println("Incorrect definition of decision attribute (expected: 'decision(all)'): " + decision);
				return null;				
			}else{
				attr.decValues = decisionValues;
			}
		}else{
			decisionIndex = inputString.length();
		}
		
		//tokenize attribute definition
		String[] list = StringUtils.tokenize(inputString.substring(0, decisionIndex), new char[]{' ','\t'}, new char[] {'\"'});        
		if(list.length < 2){
			System.err.println("Incorrect definition of the attribute (expected: 'name type [weight] role').");
			return null;			
		}
		
		for(int i=0; i<list.length; i++){
			String token = list[i];
			if(i==0){
				if(token.startsWith("\"") || token.startsWith("\'")) {
					token = StringUtils.trimQuotation(token);
				}
				attr.name = token;
			}else if(i==1){
				//attribute type
				if(Attribute.type2Int(token) != -1){
					attr.type = Attribute.type2Int(token);
				}else{
					System.err.println("Incorrect type of attribute: "+list[0]+" type: "+token);
					return null;
				}
			}else if(i>=2){
				//attribute weight or role
				if(token.equalsIgnoreCase("ignore")){
					if(attr.role != AttributeRole.ROLE_INPUT){
						System.err.println("Role of attribute is already defined: "+list[0]+" role: "+token);
						return null;						
					}						
					attr.role = AttributeRole.ROLE_IGNORE;
				}else if(token.startsWith("[") && token.endsWith("]")){
					int weight = 1;
					try{
						Float f = Float.parseFloat(token.substring(1, token.length()-1));
						weight = f.intValue();
					}catch(Exception e){
						System.err.println("Incorrect weight of attribute: "+list[0]+" weight: "+token);
						return null;						
					}
					if(weight<=0 || weight > Short.MAX_VALUE){
						System.err.println("Incorrect weight of attribute: "+list[0]+" weight: "+token);
						return null;						
					}						
					attr.weight = (short)weight;					
				}else{
					System.err.println("Incorrect role of attribute: "+list[0]+" role: "+token);
					return null;
				}					
			}
		}
				
		if(attr.type==Attribute.UNKNOWN){
			System.err.println("Type is not defined for attribute: "+list[0]);
			return null;
		}
		return attr;
	}	
	//	************************************************
	//	**** decodes decision values where input string contains commas
	private static String[] parseDecValues(String inputString, char separator)
	{
		//numeric target and all
		if(inputString.trim().equalsIgnoreCase("decision"))
			return new String[0];
		
		if(inputString.indexOf("(")==-1 || inputString.indexOf(")")==-1){
			System.err.println("Missing bracket!");
			return null;
		}else if(inputString.indexOf(")") < inputString.indexOf("(")){
			System.err.println("Unexpected bracket closing.");
			return null;
		}

		String valuesString = inputString.substring(inputString.indexOf("(")+1, inputString.indexOf(")")).trim();
		
		if(valuesString.length()==0){
			System.err.println("Decision values are not defined.");
			return null;
		}
		String[] decValues = StringUtils.tokenizeString(valuesString, new char[]{separator},false);
		if(decValues.length == 1 && decValues[0].equalsIgnoreCase("all")){
			return new String[0];
		}
		
		for (int i = 0; i < decValues.length; i++){
			if (decValues[i].length() == 0){
				System.err.println("Missing decision value!");
				return null;
			}
		}
		return decValues;
	}
	//	************************************************
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
