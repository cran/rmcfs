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
package dmLab.mcfs.mcfsEngine.framework;

import java.util.Random;

import dmLab.array.FArray;
import dmLab.utils.ArrayUtils;
import dmLab.utils.statFunctions.StatFunctions;


public class MCFSPermutation extends MCFSFramework implements Runnable 
{
	public String permPrefix="_perm";

//	*************************************
    public MCFSPermutation(Random random) {
		super(random);
	}
//	*************************************
	@Override
    public void run()
	{
		saveResutFiles = false;
		mcfsParams.contrastAttr = false;
		mcfsParams.finalCV = false;
		mcfsParams.finalRuleset = false;
		
		experimentName = permPrefix+mcfsParams.getExperimentName();               
        FArray permArray=mcfsArrays.sourceArray;
 
        System.out.println("***************************************************");
        System.out.println("*** MCFS-ID & Permutation of Decision Attribute ***");
        System.out.println("***************************************************");        

        System.out.println("Processing permutation of decision attribute...");
        float decColumn[] = permArray.getColumn(permArray.getDecAttrIdx());
        double[] x = ArrayUtils.float2double(decColumn);
        ArrayUtils arrayUtils = new ArrayUtils(random);
        arrayUtils.shuffle(decColumn, 3);
        double[] y = ArrayUtils.float2double(decColumn);
        System.out.println("Pearson's correlation after permutation: "+ StatFunctions.pearson(x, y));
        
        if(runExperiment(permArray)==null)
            return;
	}
//	*************************************
    
}
