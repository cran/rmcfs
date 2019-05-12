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
package dmLab.mcfs.mcfsEngine.arrays;

import java.io.File;

import dmLab.array.FArray;
import dmLab.array.loader.File2Array;
import dmLab.mcfs.MCFSParams;

public class MCFSArrays
{
    public FArray sourceArray;
  //************************************
    public MCFSArrays()
    {
        sourceArray = null;
    }    
//  ************************************
    public MCFSArrays(FArray array)
    {
        sourceArray = array;
        initArrays();
    }
//************************************
    public boolean loadArrays(MCFSParams mcfsParams)
    {
        sourceArray = new FArray();
        File2Array file2Container = new File2Array();

        File file = new File(mcfsParams.inputFilesPATH + "//" + mcfsParams.inputFileName);
        //System.out.println("MDR DEBUG: Loading file: " + file.getAbsolutePath());
        if (!file2Container.load(sourceArray, file.getAbsolutePath()))
            return false;
        
        if (!sourceArray.checkDecisionValues())
            return false;
        
        //System.out.println("### DEBUG ### InputArray\n" + sourceArray.toString());
        initArrays();
        
        return true;
    }
//  *************************************
    public void initArrays(){
    	if(sourceArray != null){
	        sourceArray.findDomains();
	        sourceArray.fixAttributesNames(true);
    	}    	
    }
//  *************************************    
}
