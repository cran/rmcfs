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
package dmLab.discretizer;


import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

///************************************************************
//*** This class holds parameters from *.run file.

public class DiscretizerParams extends Params
{
    public int discAlgorithm;
    public int discIntervals;
    public float maxSimilarity;
    ///************************************************************
    public DiscretizerParams()
    {
        super();
        fileType="cfg";
    }    
    ///************************************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("### Discretizer Parameters ### ").append('\n');
        tmp.append("discAlgorithm="+ discAlgorithm).append('\n');
        tmp.append("discIntervals="+ discIntervals).append('\n');
        tmp.append("maxSimilarity="+ maxSimilarity).append('\n');
        tmp.append("verbose="+ verbose).append('\n');
        return tmp.toString();
    }
    //*****************************************
    @Override
    public boolean setDefault()
    {
    	discAlgorithm = Discretizer.FAYYAD_IRANI;
        discIntervals=4;
        maxSimilarity=0.5f;
        verbose = false;
        
	    return true;
    }
    ///************************************************************
    public int discAlgorithm()
    {
    	return discAlgorithm;
    }
    ///************************************************************
    public int discIntervals()
    {
    	return discIntervals;
    }
    ///************************************************************
    public float maxSimilarity()
    {
        return maxSimilarity;
    }
    ///************************************************************
    @Override
    protected boolean update(Properties properties)
    {
        discAlgorithm = Integer.valueOf(properties.getProperty("discAlgorithm", "5")).intValue();
        discIntervals = Integer.valueOf(properties.getProperty("discIntervals", "4")).intValue();
        maxSimilarity= Float.valueOf(properties.getProperty("maxSimilarity", "0.5")).floatValue();
        verbose = Boolean.valueOf(properties.getProperty("verbose", "false")).booleanValue();
        return check(null);
    }
    //*****************************************
    @Override
    public boolean check(FArray array)
    {
        if (discIntervals <= 0){
            System.err.println("Error! Incorrect discRanges.");
            return false;
        }
        
        if (discAlgorithm <= 0 || discAlgorithm > 10){
            System.err.println("Error! Incorrect discretizationType.");
            return false;
        }        
        return true;
    }
//  *****************************************
    
}//end class
