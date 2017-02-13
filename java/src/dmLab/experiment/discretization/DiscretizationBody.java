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
package dmLab.experiment.discretization;

import dmLab.array.FArray;
import dmLab.array.functions.DiscFunctions;
import dmLab.array.loader.File2Array;
import dmLab.array.saver.Array2File;
import dmLab.discretizer.DiscretizerParams;

/** author: Michal Draminski & Pawel Wierzbicki  **/

///************************************************************
//*** This class starts discretization
class DiscretizationBody
{
	protected File2Array file2Container;
	protected Array2File array2File;
	protected FArray inputArray;
	protected float discTime;
	
	//*************************************************
	//**** constructor
	public DiscretizationBody()
	{
        file2Container=new File2Array();
		array2File=new Array2File();
	}
//	***********************************************
	public void run(String[] args)
	{
		if(args.length==0)
		{
			System.err.println("Missing argument - name of discretization parameters file! (*.run)");
			return;
		}
		DiscretizationBody disc=new DiscretizationBody();
		disc.run(args[0]);		
	}	
//	***********************************************
	public void run(String runFileName)
	{
		DiscretizationParams discParams = new DiscretizationParams();
		DiscretizerParams discretizerParams = new DiscretizerParams();
		
		if (discParams.load("",runFileName) == false ){
			System.err.println("Error loading configuration file. File: " + runFileName);
			return;
		}
		
		if (discretizerParams.load("",discParams.discretizerConfigFile)==false){
			System.err.println("Error loading parameters file. File: " + runFileName);
			return;
		}
		discretizerParams.verbose = discParams.verbose;
		
		//System.out.println(discParams.toString());
		//System.out.println(discretizerParams.toString());
		
		if(discParams.verbose) 
			System.out.println("Loading input table...");
		
		inputArray=new FArray();
		if (file2Container.load(inputArray,discParams.inputFilesPATH + discParams.inputFileName) == false)
			return;
		
		//System.out.println(" ### input array ### ");
		//System.out.println(inputArray.toString());            
		
		if(discParams.verbose) 
			System.out.println("Input table loaded.");   
		
		array2File.setFormat(discParams.outputFormat);			
		DiscFunctions.findRanges(inputArray,discretizerParams);
		DiscFunctions.applyRanges(inputArray);

		//System.out.println(" ### discsRanges ### ");
		//System.out.println(DiscFunctions.toStringRanges(inputArray));		
		//System.out.println(" ### dicretized array ### ");
		//System.out.println(inputArray.toString());
			
		array2File.saveFile(inputArray,discParams.resFilesPATH+discParams.outputFileName);
		
		if(discParams.verbose) 
			System.out.println("Discretizing have been done!");
	}
//	***********************************************
}
