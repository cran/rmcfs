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

import java.io.File;

import dmLab.array.Array;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;

public abstract class FileLoader 
{	
	protected int eventsNumber;
	protected int attributesNumber;
	protected int ignoredAttributesNumber;

	public boolean trimComments = true;
	public char separator = ',';
	protected NullLabels nullLabels;
	protected String defaultNullLabel = "?";

	protected boolean ignoredAttributeMask[];
	protected Array myArray;

	protected String commentChar = "#";
	protected int fileType = FileType.UNKNOWN;

	//****************************************
	public FileLoader()
	{		
		init();
	}
	//	************************************************
	public void init()
	{
		nullLabels = new NullLabels(5);
		ignoredAttributeMask = null;
		eventsNumber = 0;
		attributesNumber = 0;
		ignoredAttributesNumber = 0;
		myInit();
	}
	//	************************************************
	//	***  function loads the file
	public boolean loadFile(Array array, File file)
	{
		String fileExt = FileUtils.getFileExtension(file.getName());		
		if(FileType.toType(fileExt) != fileType){
			System.err.println("Input file is not " + FileType.toTypeStr(fileType) + " type. File: "+file.toString());
			return false;			
		}

		if(!readHeaderFile(file))
			return false;

		System.out.println("Loading data: '" + getDataFile(file).getName() + "'...");
		myArray = array;
		
		if(parseInputFile(file) == false){
			System.err.println("Error Parsing file. File: "+getDataFile(file).toString());
			return false;
		}

		if(!checkData())
			return false;		

		myArray.init(attributesNumber, eventsNumber);
		ignoredAttributeMask = new boolean [attributesNumber+ignoredAttributesNumber];


		if(readInputFile(file)==false){
			System.err.println("Error reading file. File: "+file.toString());
			return false;
		}else{
			System.out.println("Data loaded.");
		}

		//System.out.println("DEBUG: \n" + myArray.toString());
		return true;
	}
	//	************************************************
	//	*** method returns false if attributes number or events number is 0
	protected boolean checkData()
	{
		System.out.println("attributes: "+attributesNumber+" events: "+eventsNumber);
		if(attributesNumber==0){
			System.err.println("Input data does not contain attributes.");
			return false;
		}
		else if(eventsNumber==0){
			System.err.println("Input data does not contain events.");
			return false;
		}
		return true;
	}
	//	************************************************
	//	*** this method trims comments
	protected String trimComments(String inputLine)
	{        
		if(!trimComments)
			return inputLine;
		if(inputLine==null)
			return null;
		if(inputLine.indexOf(commentChar)==-1)
			return inputLine.trim();
		else        
			return inputLine.substring(0,inputLine.indexOf(commentChar)).trim();
	}
	//	************************************************
	public void addNullLabel(String nullLabel)
	{
		this.nullLabels.add(nullLabel);
	}
	//	************************************************
	public void setSeparator(char separator)
	{
		this.separator = separator;
	}
	//  ************************************************
	//*** this method reads one event
	protected boolean loadEvent(String inputLine, int row)
	{
		int column=0;
		String[] list = StringUtils.tokenizeString(inputLine,new char[]{separator}, false);

		if(list.length != (attributesNumber + ignoredAttributesNumber)){
			System.err.println("Number of values does not equal to defined attributes number. Event: " + row);
			return false;
		}     

		for(int i=0;i<ignoredAttributeMask.length;i++){
			if(!ignoredAttributeMask[i]){
				String value = list[i];
				if(nullLabels.containsIgnoreCase(value))
					value = defaultNullLabel;

				if(value.length()!=0){
					if(!myArray.writeValueStr(column, row, value)){
						System.err.println("Error loading value. Attribute: "+myArray.attributes[column].name+" (#"+i+") value: "+value);
						return false;
					}else
						column++;
				}else{
					System.err.println("Empty Value! Attribute: "+myArray.attributes[column].name+" (#"+i+")");
					return false;
				}
			}
		}
		return true;
	}
	//	************************************************
	protected abstract boolean readHeaderFile(File inputFile);
	//  ************************************************    
	protected abstract boolean parseInputFile(File inputFile);
	//	************************************************
	protected abstract boolean readInputFile(File inputFile);
	//	************************************************
	protected abstract boolean myInit();
	//  ************************************************
	protected abstract File getDataFile(File inputFile);
	//  ************************************************
	
}
