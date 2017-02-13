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
package dmLab.array.loader;

import java.io.File;
import java.util.Arrays;

import dmLab.array.Array;
import dmLab.array.loader.fileLoader.FileLoader;
import dmLab.array.loader.fileLoader.FileLoaderADH;
import dmLab.array.loader.fileLoader.FileLoaderADX;
import dmLab.array.loader.fileLoader.FileLoaderArff;
import dmLab.array.loader.fileLoader.FileLoaderCSV;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.mcfs.MCFSParams;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;

public class File2Array extends Data2Array
{    
	FileLoader fileLoader;
	public char separatorCSV = ',';
	public boolean firstLineContainsAttributesCSV = true;
	public String defaultAttributeNameCSV = "attr";
	public boolean trimCommentsCSV = false;
	public boolean consequentSeparatorsTreatAsOneCSV = false;
	//*******************************    
	public File2Array()
	{
		fileLoader=null;
	}
	//*******************************
	public boolean load(Array array, String inputFileName)
	{
		boolean retVal;
		File[] extracted = null;

		File file = new File(inputFileName);
		if(FileUtils.getFileExtension(file.getName()).equalsIgnoreCase("zip")){
			String fileNamePrefix = FileUtils.dropFileExtension(file);        	
			extracted = FileUtils.extract(file, new File(MCFSParams.TMP_PATH), fileNamePrefix);
			Arrays.sort(extracted);
			//System.out.println(Arrays.toString(extracted));
			if(extracted == null || extracted.length == 0){
				System.err.println("Input Zip file: '"+file.toString()+"' does not contain any '"+fileNamePrefix+".*' file. '");
				return false;
			}
			File tmpFile = null;
			for(int i=0; i<extracted.length; i++){
				if(StringUtils.equalsToAny(FileUtils.getFileExtension(extracted[i].getName()), new String[] {"adx","adh","arff","csv"})){
					tmpFile	= extracted[i];
					break;
				}
			}
			if(tmpFile == null){
				System.err.println("Input Zip file: '"+file.toString()+"' does not contain any data file: '" + fileNamePrefix +"[.adx, .adh, .arff, .csv]'.");
				return false;
			}
			file = new File(MCFSParams.TMP_PATH + tmpFile.toString());
			
		}

		fileLoader = initFileLoader(file);

		if(fileLoader == null){
			return false;
		}else{
			fileLoader.init();
			retVal = fileLoader.loadFile(array, file);
		}

		//clean extracted files from zip
		if(extracted !=null){
			for(int i=0; i<extracted.length; i++){
				File tmpFile = new File(MCFSParams.TMP_PATH + extracted[i].toString());
				if(tmpFile.exists()){        	
					tmpFile.delete();
				}
			}
		}

		return retVal;
	}
	//*******************************
	private FileLoader initFileLoader(File file)
	{                       
		String fileExt = FileUtils.getFileExtension(file.getName());        		
		int type = FileType.toType(fileExt);
		if(type == FileType.ADX){
			return new FileLoaderADX();
		}else if(type == FileType.ADH){
			return new FileLoaderADH();			
		}else if(type == FileType.ARFF){
			return new FileLoaderArff();
		}else if(type == FileType.CSV){
			FileLoaderCSV fl = new FileLoaderCSV();        	
			fl.firstLineContainsAttributes = firstLineContainsAttributesCSV;
			fl.separator = separatorCSV;
			fl.defaultAttributeName = defaultAttributeNameCSV;
			fl.trimComments = trimCommentsCSV;
			fl.consequentSeparatorsTreatAsOne = consequentSeparatorsTreatAsOneCSV;            
			return fl;
		}else{
			System.err.println("Format of input file: "+ file.getName() +" is not recognized.");        	
			return null;
		}        
	}
	//*******************************    
}
