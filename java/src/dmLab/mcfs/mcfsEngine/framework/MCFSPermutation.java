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
package dmLab.mcfs.mcfsEngine.framework;

import java.util.Random;

import dmLab.array.FArray;
import dmLab.utils.ArrayUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.statFunctions.StatFunctions;


public class MCFSPermutation extends MCFSFramework implements Runnable 
{
	public final static String PERM_PREFIX = "perm_";  
	
	public String experimentNamePrefix = PERM_PREFIX;
//	*************************************
    public MCFSPermutation(Random random) {
		super(random);
	}
//	*************************************
	@Override
    public void run()
	{
		mcfsParams.buildID = true;
		mcfsParams.finalCV = false;
		mcfsParams.finalRuleset = false;
		mcfsParams.cutoffMethod = "mean";
		
		if(experimentName.equalsIgnoreCase(mcfsParams.getExperimentName()))
			experimentName = experimentNamePrefix + mcfsParams.getExperimentName();

        FArray permArray = mcfsArrays.sourceArray; 
        float decColumn[] = permArray.getColumn(permArray.getDecAttrIdx());
        double[] x = ArrayUtils.float2double(decColumn);
        ArrayUtils arrayUtils = new ArrayUtils(random);
        arrayUtils.shuffle(decColumn, 3);
        double[] y = ArrayUtils.float2double(decColumn);
        System.out.println("Pearson's correlation of shuffled decision: "+ GeneralUtils.formatFloat(StatFunctions.pearson(x, y),4));
        
        runExperiment(permArray);
	}
//	*************************************
    
}
