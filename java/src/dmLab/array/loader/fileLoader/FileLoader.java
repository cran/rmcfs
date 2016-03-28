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
import java.io.File;
import java.io.FileReader;

import dmLab.array.Array;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;

public abstract class FileLoader 
{
	public FileType fileType;
	protected  BufferedReader fileReader;
	protected String fileName;
	
	protected int eventsNumber;
	protected int attributesNumber;
	protected int ignoredAttributesNumber;
	
	public char separator=',';
	protected NullLabels nullLabels;
	protected String defaultNullLabel="?";
	public boolean trimComments=true;
    
	protected boolean ignoredAttributeMask[];
	protected Array myArray;
	
	protected String commentChar="#";
//****************************************
	public FileLoader()
	{		
		init();
	}
//	************************************************
	public void init()
	{
		fileReader=null;
		fileType = new FileType();
		nullLabels=new NullLabels(5);
		fileName="";
		ignoredAttributeMask=null;
        eventsNumber=0;
        attributesNumber=0;
        ignoredAttributesNumber=0;
        privateInitializator();
	}
//	************************************************
//	***  function loads the file
	public boolean loadFile(Array array, String inputFileName)
	{
		myArray=array;
		
		openFile(inputFileName);
		if(parseInputFile(fileReader)==false)
		{
			System.err.println("Error Parsing file. File: "+fileName);
			closeFile();
			return false;
		}
		
		if(!checkIntegration())
			return false;
		
		closeFile();
				
		myArray.init(attributesNumber,eventsNumber);
		ignoredAttributeMask=new boolean [attributesNumber+ignoredAttributesNumber];
		
		openFile(inputFileName);
		if(readInputFile(fileReader)==false)
		{
			System.err.println("Error reading file. File: "+fileName);
			closeFile();
			return false;
		}
		closeFile();
		return true;
	}
//	************************************************
	public boolean openFile(String inputFileName)
	{      	
		String fileExtension=FileUtils.getFileExtension(inputFileName);
		if(FileType.toType(fileExtension)!=-1)
			fileName=inputFileName;
		else
			fileName=inputFileName+"."+fileType.getTypeStr();		

		File file=new File(fileName);
		if(!file.exists())
		{
			System.err.println("File does not exist. File: " + inputFileName);
			return false;
		}	
		
		try{
			fileReader = new BufferedReader(new FileReader(fileName));
		}
		catch (Exception e) {
			System.err.println("Error opening file. File: "+fileName);
			return false;
		}
		return true;
	}
//	************************************************
	public boolean closeFile()
	{
		try{
			fileReader.close();
		}
		catch (Exception e) {
			System.err.println("Error closing file. File: "+fileName);
			return false;
		}
		return true;
	}
//	************************************************
//	*** method returns false if attributes number or events number is 0
	protected boolean checkIntegration()
	{
		System.out.println("attributes: "+attributesNumber+" events: "+eventsNumber);
		if(attributesNumber==0)
		{
			System.err.println("No attributes defined in input file. File: "+fileName);
			return false;
		}
		else if(eventsNumber==0)
		{
			System.err.println("No events defined in input file. File: "+fileName);
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
        String[] list=StringUtils.tokenizeString(inputLine,new char[]{separator},false);
        
        if(list.length!=(attributesNumber+ignoredAttributesNumber))
        {
            System.err.println("Number of values does not equal to attributes number.");
            return false;
        }            
            
        for(int i=0;i<ignoredAttributeMask.length;i++)
        {
            if(!ignoredAttributeMask[i])
            {
                String value=list[i];
                if(nullLabels.containsIgnoreCase(value))
                    value=defaultNullLabel;
                
                if(value.length()!=0)    
                {
                    if(!myArray.writeValueStr(column,row,value))
                    {
                        System.err.println("Error loading value. Attribute: "+myArray.attributes[column].name+" (#"+i+") value: "+value);
                        return false;
                    }
                    else
                    	column++;
                }
                else
                {
                    System.err.println("Empty Value! Attribute: "+myArray.attributes[column].name+" (#"+i+")");
                    return false;
                }
            }
        }
        return true;
    }
//  ************************************************    
	protected abstract boolean parseInputFile(BufferedReader inputFile);
//	************************************************
	protected abstract boolean readInputFile(BufferedReader inputFile);
//	************************************************
	protected abstract boolean privateInitializator();
//  ************************************************
}
