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
import java.util.ArrayList;

import dmLab.array.meta.AttributeDef;
import dmLab.utils.FileUtils;

public class FileLoaderADH extends FileLoaderCSV {
	//	************************************************
	public FileLoaderADH()
	{
		super();
	}
	//	************************************************
	@Override
	protected boolean myInit()
	{
		super.myInit();
		fileType = FileType.ADH;
		return true;
	}
	//	************************************************
	@Override
	protected boolean readHeaderFile(File inputFile) {		
		System.out.println("Loading header: '"+ inputFile.getName() +"'...");
		String line="";
		int lineCount=0;
		
		BufferedReader fileReader;
		if((fileReader = FileUtils.openFile(inputFile)) == null)
			return false;
		
		ArrayList<AttributeDef> attrDefList = new ArrayList<AttributeDef>();
		
		do{
			try{
				line = fileReader.readLine();
				lineCount++;
			}catch (Exception e) {
				System.err.println("Error reading input file. Line: "+lineCount);
				return false;
			}
			if(line != null){ 

				line = trimComments(line);
				if(line.length()==0) //if line is empty
					continue;
			
				AttributeDef attr = FileLoaderADX.parseAttribute(line);				
				//System.out.println("DEBUG: " + attr.toString());
				if(attr.role == AttributeDef.ROLE_IGNORE)
					ignoredAttributesNumber++;				
				attrDefList.add(attr);
			}		
		}while(line!=null); //end while
		
		attrDefArray = new AttributeDef[1]; 
		attrDefArray = attrDefList.toArray(attrDefArray);
		
		if(!FileUtils.closeFile(fileReader))
			return false;

		return true;
	}
	//	************************************************
	@Override
	protected boolean parseInputFile(File inputFile){
		return super.parseInputFile(getDataFile(inputFile));
	}
	//	************************************************
	@Override
	protected boolean readInputFile(File inputFile){
		return super.readInputFile(getDataFile(inputFile));
	}
	//	************************************************
	@Override	
	protected File getDataFile(File inputFile){
        String adhName = inputFile.toString();
        String csvName = adhName.substring(0, adhName.lastIndexOf('.'));		
		File inputFileCSV = new File(csvName+".csv");
		return inputFileCSV;
	}
	//	************************************************
}
