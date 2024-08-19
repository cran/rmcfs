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
package dmLab.experiment.discretization;



import java.util.Properties;

import dmLab.array.FArray;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.experiment.ExperimentParams;

public class DiscretizationParams extends ExperimentParams
{   
    public String discretizerConfigFile;
    public int outputFormat;
    public boolean saveRanges;
    
//  *************************************
//  **** sets all default values
    public DiscretizationParams()
    {
        super();
        fileType="run";
        outputFormat=FileType.ADX;
    }
//  *****************************************
    @Override
    public boolean setDefault()
    {
        verbose = false;

    	label="experiment";
        inputFilesPATH = ExperimentParams.DEFAULT_DATA_PATH;
        resFilesPATH = ExperimentParams.DEFAULT_RES_PATH;        
        inputFileName="";
        testFileName="";        
        outputFileName="";
        
        discretizerConfigFile="discretizer";
        saveRanges=true;
        
	    return true;
    }
//  *************************************
//  *** function prints parameters
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(super.toString()).append('\n');
        
        tmp.append("### Discretization Parameters ### ").append('\n');                 
        tmp.append("discretizerConfigFile="+ discretizerConfigFile).append('\n');
        tmp.append("outputFormat="+FileType.toTypeStr(outputFormat)).append('\n');
        tmp.append("saveRanges="+saveRanges).append('\n');
        return tmp.toString();
    }
//  *************************************
    @Override
    protected boolean update(Properties properties)
    {
        if(!super.update(properties))
        	return false; 
        	                
        discretizerConfigFile=properties.getProperty("discretizerConfigFile", "discretizer");
        outputFormat=FileType.toType(properties.getProperty("outputFormat", "ADX"));
        saveRanges=Boolean.valueOf(properties.getProperty("saveRanges", "true")).booleanValue();
    	if(outputFileName.length()==0)
        {
    		int extensionIndex=inputFileName.lastIndexOf(".");
    		if(extensionIndex==-1)
    			outputFileName=	inputFileName+"_discretized";
    		else
    			outputFileName=inputFileName.substring(0,extensionIndex)+"_discretized";
        }
        return true;
    }
//  *****************************************
    @Override
    public boolean check(FArray array)
    {
        if(!super.check(array))
        	return false;
        	
        if(discretizerConfigFile.length()==0)
        {
            System.err.println("Parameter 'discretizerConfigFile' is not defined");
            return false;
        }
        return true;
    }
//  *****************************************
    
}
