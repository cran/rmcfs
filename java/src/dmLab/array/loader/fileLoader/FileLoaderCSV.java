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

import dmLab.array.Array;
import dmLab.array.meta.Attribute;
import dmLab.array.meta.AttributeRole;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;



public class FileLoaderCSV extends FileLoader 
{
	public boolean firstLineContainsAttributes;
	public boolean consequentSeparatorsTreatAsOne; 
	public String defaultAttributeName;
	
	protected AttributeRole[] attrDefArray;
	private boolean allDecision = false;

	//	************************************************
	//	*** load csv file into array class
	public FileLoaderCSV()
	{
		super();
	}
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
		firstLineContainsAttributes = true;
		defaultAttributeName = "attr";
		trimComments = false;
		consequentSeparatorsTreatAsOne = false;
		fileType = FileType.CSV;
		return true;
	}
	//	************************************************
	//	*** method parses file and finds attribute and event numbers
	@Override
	protected boolean parseInputFile(File inputFile)
	{
		int lineCount = 0;
		String line="";
		char separators[] = new char[]{separator};
		boolean firstLine=true;

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
				System.err.println("Error reading input file. Line: "+lineCount);
				return false;
			}
			if(line!=null){ 
				line = trimComments(line);
				if(line.length()==0) //if line is empty
					continue;
				if(firstLine){
					String[] tokens = StringUtils.tokenizeString(line, separators, consequentSeparatorsTreatAsOne);
					attributesNumber = tokens.length;
					if(firstLineContainsAttributes){
						if(!readHeaderLine(tokens))
							return false;
					}else{
						if(!readHeaderLine(null))
							return false;
						eventsNumber++;
					}
					firstLine = false;
				}else{
					eventsNumber++;
				}
			}
		}while(line!=null); //end while
		
		if(attrDefArray!=null && attrDefArray.length != attributesNumber){
			System.err.println("Number of attributes in data ("+attributesNumber+") does not equal to header("+attrDefArray.length+").");
			return false;			
		}
		
		attributesNumber = attributesNumber - ignoredAttributesNumber;
		
		if(!FileUtils.closeFile(fileReader))
			return false;

		return true;
	}
	//	************************************************
	//*** method reads attributes and events into memory
	@Override
	protected boolean readInputFile(File inputFile)
	{
		int lineCount=0;
		String line="";
		boolean firstLine = true;
		int eventIndex=0;
		
		BufferedReader fileReader;
		if((fileReader = FileUtils.openFile(inputFile)) == null)
			return false;
		
		//load attributes
		if(attrDefArray!=null){
			int attrIndex = 0;
			for(int i=0;i<attrDefArray.length;i++){
				AttributeRole attr = attrDefArray[i];
				if(attr.role == AttributeRole.ROLE_IGNORE){
					ignoredAttributeMask[i] = true;					
				}else{
					ignoredAttributeMask[i] = false;
					myArray.attributes[attrIndex].name = attr.name;
					myArray.attributes[attrIndex].type = attr.type;
					myArray.attributes[attrIndex].weight = attr.weight;					
					if(attr.role == AttributeRole.ROLE_DECISION){
						myArray.setDecAttrIdx(attrIndex);
						if(attr.decValues.length>0){
							myArray.setDecValues(attr.decValues);
						}else{
							allDecision = true;
						}
					}
					attrIndex ++;
				}
			}//end for
		}//end if
		
		do{
			try{
				line = fileReader.readLine();
				lineCount++;
			}catch (Exception e) {
				System.err.println("Error reading input file. Line: "+lineCount);
				return false;
			}
			if(line!=null){ 

				line = trimComments(line);
				if(line.length()==0) //if line is empty
					continue;

				if(firstLine && firstLineContainsAttributes){
					firstLine=false;    
				}else{
					if(!loadEvent(line, eventIndex)){
						System.err.println("Error reading event. Line: "+lineCount);
						return false;
					}
					eventIndex++;
				}   
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
	protected boolean readHeaderLine(String[] tokens) {

		if(attrDefArray == null)
			attrDefArray = new AttributeRole[attributesNumber];
		
		if(tokens == null){
				for(int i=0;i<attributesNumber;i++){
					if(attrDefArray[i] == null){
						attrDefArray[i] = new AttributeRole();
						attrDefArray[i].name = defaultAttributeName+i;
						attrDefArray[i].type = Attribute.NOMINAL;
					}
				}			
		}else{
			for(int i=0; i<tokens.length; i++){
				String name = tokens[i];
				if(name.equalsIgnoreCase(""))
					name = defaultAttributeName+i;
				if(name.startsWith("\"") || name.startsWith("\'")) {
					name = StringUtils.trimQuotation(name);
				}					
				if(attrDefArray[i] == null){
					attrDefArray[i] = new AttributeRole();
					attrDefArray[i].name = name.trim().replaceAll(" ", Array.SPACE_CHAR);
					attrDefArray[i].type = Attribute.NOMINAL;
				}else if(!attrDefArray[i].name.equalsIgnoreCase(name)){
					System.out.println(name);
					System.out.println(attrDefArray[i].name);
					System.err.println("Name of attribute in data ("+name+") does not correspond to header attribute name ("+attrDefArray[i].name+").");
					return false;
				}
			}
		}

		return true;
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
