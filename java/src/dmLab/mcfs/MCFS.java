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
package dmLab.mcfs;

import java.io.File;

import dmLab.DMLabInfo;
import dmLab.mcfs.mcfsEngine.MCFSExperiment;

public class MCFS
{
	//	*************************************
	public static void main(String[] args)
	{		
		DMLabInfo dmLabInfo = new DMLabInfo();      
		System.out.println(dmLabInfo.toString());

		String paramsFileName;      
		if(args.length>=1){
			paramsFileName=args[0];
		}else{
			System.err.println("Missing parameters file!");
			return;
		}

		MCFSParams mcfsParams = new MCFSParams();

		File tmpDir = new File(MCFSParams.TMP_PATH);
		if(!tmpDir.exists()) {
			tmpDir.mkdir();
		}

		if(mcfsParams.load("", paramsFileName)){
			for(int i=0; i<mcfsParams.inputFiles.length; i++){
				mcfsParams.inputFileName = mcfsParams.inputFiles[i];
				//System.out.println("Input File: " + mcfsParams.inputFileName);
				MCFSExperiment mcfs = new MCFSExperiment(mcfsParams);
				mcfs.run();
			}
		}
	}
	//	*************************************
}
