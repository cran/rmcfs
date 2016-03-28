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

package dmLab.experiment;

import java.util.Arrays;
import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;
import dmLab.utils.StringUtils;

public class ExperimentParams extends Params implements Cloneable
{	
	public static String DEFAULT_RES_PATH = ".//results//";
	public static String DEFAULT_DATA_PATH = ".//data//";

	public String label;
	public String inputFilesPATH;
	public String resFilesPATH;
	public String classifierCfgPATH;

	public String inputFileName;
	public String[] inputFiles;

	public String testFileName;
	public String outputFileName;

	//  *************************************
	//  **** sets all default values
	public ExperimentParams()
	{
		super();
		fileType="run";
		setDefault();
	}
	//  *****************************************
	@Override
	public boolean setDefault()
	{
		verbose=true;
		debug=false;

		label="experiment";        
		inputFilesPATH = ExperimentParams.DEFAULT_DATA_PATH;
		resFilesPATH = ExperimentParams.DEFAULT_RES_PATH;
		classifierCfgPATH=".//cfg//";

		inputFileName="";
		testFileName="";
		outputFileName="";

		return true;
	}
	//***************************************** 
	public void set(ExperimentParams p){
		
		super.set(p);
		
		label = p.label;
		inputFilesPATH = p.inputFilesPATH;
		resFilesPATH = p.resFilesPATH;
		classifierCfgPATH=p.classifierCfgPATH;        
		inputFileName=p.inputFileName;
		inputFiles = p.inputFiles;
		testFileName=p.testFileName;
		outputFileName=p.outputFileName;
	}
	//  *************************************
	@Override
	public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("### Experiment Parameters ### ").append('\n');
		tmp.append(super.toString());
		tmp.append("label="+ label).append('\n');
		tmp.append("inputFilesPATH="+ inputFilesPATH).append('\n');  
		tmp.append("resFilesPATH="+ resFilesPATH).append('\n');
		tmp.append("classifierCfgPATH="+ classifierCfgPATH).append('\n');
		tmp.append("inputFileName="+ inputFileName).append('\n');
		if(inputFiles != null)
			tmp.append("inputFiles="+ Arrays.toString(inputFiles)).append('\n');
		else
			tmp.append("inputFiles="+ inputFileName).append('\n');

		if(testFileName!=null)
			tmp.append("testFileName="+ testFileName).append('\n');
		
		if(outputFileName!=null)
			tmp.append("outputFileName="+ outputFileName).append('\n');
		
		return tmp.toString();
	}
	//  *************************************
	@Override
	protected boolean update(Properties properties)
	{
		verbose=Boolean.valueOf(properties.getProperty("verbose", "true")).booleanValue();
		debug=Boolean.valueOf(properties.getProperty("debug", "false")).booleanValue();

		label=properties.getProperty("label", "experiment");
		inputFilesPATH=properties.getProperty("inputFilesPATH", ExperimentParams.DEFAULT_DATA_PATH);
		resFilesPATH=properties.getProperty("resFilesPATH", ExperimentParams.DEFAULT_RES_PATH);
		classifierCfgPATH=properties.getProperty("classifierCfgPATH", ".//cfg//");        
		inputFileName=properties.getProperty("inputFileName", "inputFile.adx");        
		inputFiles = StringUtils.tokenizeArray(inputFileName);        
		if(inputFiles == null){
			System.err.println("Parameter 'inputFileName' is not defined");
			return false;
		}
		if(inputFiles.length>1){
			inputFileName = inputFiles[0];
		}
		
		testFileName=properties.getProperty("testFileName", null);
		outputFileName=properties.getProperty("outputFileName", null);

		return true;
	}
	//  *****************************************
	@Override
	public boolean check(FArray array)
	{
		return true;
	}
	//*****************************************
}
