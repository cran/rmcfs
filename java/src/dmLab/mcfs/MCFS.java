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
package dmLab.mcfs;

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
		if(mcfsParams.load("", paramsFileName)){
			//MDR DEBUG
			//mcfsParams.verbose = false;
			//mcfsParams.tmpPATH = new File("/Users/mdraminski/TEMP1/").getAbsolutePath();
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
