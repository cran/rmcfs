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
package dmLab.classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import dmLab.array.FArray;
import dmLab.mcfs.mcfsEngine.MCFSAutoParams;
import dmLab.utils.FileUtils;

public abstract class Params
{	
	protected String fileType;
	
	public boolean verbose;
	//*****************************************
	public Params()
	{
		fileType = "cfg";
		verbose = false;
		setDefault();
	}
	//*****************************************
	public abstract boolean setDefault();
	//*****************************************
	public abstract boolean check(FArray array);
	//*************************************
	protected abstract boolean update(Properties properties);
	//*************************************
	protected void set(Params p){		
		//properties = new Properties();
		fileType = p.fileType;
		verbose = p.verbose;
	}
	//*************************************
	public boolean load(String path, String inputFileName)
	{
		Properties properties = new Properties();

		if(path==null || inputFileName==null)
			return setDefault();

		String filePath;
		String myPath="";
		String ext=FileUtils.getFileExtension(inputFileName);
		
		if(path.length()!=0)
			myPath=path+"//";
		
		if(ext.equalsIgnoreCase(fileType))
			filePath=myPath+inputFileName;
		else
			filePath=myPath+inputFileName+"."+fileType;		
		
		try{
			FileInputStream cfgFile = new FileInputStream(filePath);
			properties.load(cfgFile);
			cfgFile.close();
		}
		catch (FileNotFoundException e){
			System.out.println("Config file: '"+filePath+"' not found. Default params will be used.");
			return setDefault();
		}
		catch (IOException e){
			System.err.println("Error: Loading config file. File" + filePath);
			e.printStackTrace();
			return false;
		}
		verbose = Boolean.valueOf(properties.getProperty("verbose", "false")).booleanValue();
		return update(properties);
	}
//	*****************************************
	public boolean save(String path, String filename)
	{
		String cfgFilePath=path+"//"+filename+"."+fileType;
		if(path.length()==0)
			cfgFilePath=filename+fileType;
		
		File cfgFile = new File(cfgFilePath);
		FileWriter writer=null;
		try {
			writer = new FileWriter(cfgFile);
		} catch (IOException e) {
			System.err.println("Error: Cannot create the file. File: "+cfgFilePath);
			e.printStackTrace();
			return false;
		}	  
		try {
			writer.write(toString());
			writer.close();
		} catch (IOException e) {
			System.err.println("Error: Saving and Closing config file. File: "+cfgFilePath);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//*****************************************
	public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("verbose="+ verbose).append('\n');
		return tmp.toString(); 
	}
	//*****************************************
	public static String intParamToString(int paramValue, String paramName) 
	{   
		StringBuffer tmp=new StringBuffer();
		if(paramValue >= 1){
			tmp.append(paramName).append(" is ON");
    	}else if(paramValue == 0){
    		tmp.append(paramName).append(" is OFF");
    	}else if(paramValue == MCFSAutoParams.AUTO){
    		tmp.append(paramName).append(" is AUTO");
    	}else{
    		tmp.append(paramName).append(" is Unknown");
    	}
		return tmp.toString();
	}
//	*****************************************
}
