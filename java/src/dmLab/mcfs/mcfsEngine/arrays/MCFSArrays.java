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
package dmLab.mcfs.mcfsEngine.arrays;

import dmLab.array.FArray;
import dmLab.array.loader.File2Array;
import dmLab.mcfs.MCFSParams;

public class MCFSArrays
{
    public FArray sourceArray;
    
//  ************************************    
    public MCFSArrays()
    {
        sourceArray=null;
    }
//************************************
    public boolean loadArrays(MCFSParams mcfsParams)
    {
        sourceArray = new FArray();
        System.out.print("Loading Input Table...");
        File2Array file2Container=new File2Array();
        
        if (!file2Container.load(sourceArray, mcfsParams.inputFilesPATH + mcfsParams.inputFileName))
            return false;
        
        if (!sourceArray.checkDecisionValues())
            return false;
        
        if (mcfsParams.debug) 
            System.out.println("### DEBUG ### InputArray\n" + sourceArray.toString());
        
        sourceArray.findDomains();
        
        return true;
    }
//  *************************************   
}
