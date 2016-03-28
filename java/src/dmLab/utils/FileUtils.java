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
package dmLab.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
	//********************************
	public static String getFileExtension(String fileName)
	{
		String extension="";    
		int index=fileName.lastIndexOf('.');
		if (index > 0 &&  index < fileName.length() - 1)        
			extension=fileName.substring(index+1).toLowerCase();
		return extension;   
	}
	// ************************************************
	public static String getFilePath(File file)
	{
		String path="";
		String absPath=file.getAbsolutePath();
		int index=absPath.lastIndexOf(file.getName());
		path=absPath.substring(0,index);
		return path;
	}
	//********************************
	public static String getFileLabel(File file)
	{
		String label="";
		String name=file.getName();
		String ext=getFileExtension(name);              
		int index=name.lastIndexOf(ext);
		label=name.substring(0,index-1);
		return label;
	}   
	//********************************
	public static boolean saveString(String fileName, String data){
		FileWriter file;
        try{
            file= new FileWriter(fileName,false);
        }       
        catch(IOException ex){
            System.err.println("Error opening file: "+fileName);
            return false;
        }               
        try {
            file.write(data);
            file.close();
        } catch (IOException e) {
            System.err.println("Error writing or closing file: "+fileName);
            return false;
        }      
        return true;		
	}
	//********************************
	public static String loadString(String fileName) 
	{
		BufferedReader reader=null;
		try {
			reader = new BufferedReader( new FileReader (fileName));
		} catch (FileNotFoundException e1) {
			System.err.println("Error opening file: "+fileName);
			e1.printStackTrace();
		}
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while( ( line = reader.readLine() ) != null ) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
		} catch (IOException e) {
			System.err.println("Error reading file: "+fileName);
			e.printStackTrace();
		}	    

		try {
			if(reader!=null)
				reader.close();
		} catch (IOException e) {
			System.err.println("Error closing file: "+fileName);
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}
	//********************************
	public static String addExtension(String fileName,String extension)
	{	
		String retfileName;
	    String ext=FileUtils.getFileExtension(fileName);
	    if(ext.equalsIgnoreCase(extension))
	        retfileName=fileName;
	    else
	        retfileName=fileName+"."+extension;
	    
	    return retfileName;	    
	}
	//********************************

}
