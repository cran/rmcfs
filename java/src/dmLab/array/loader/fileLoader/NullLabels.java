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
package dmLab.array.loader.fileLoader;

import dmLab.utils.list.StringList;

public class NullLabels extends StringList
{
    public static final String defaultLabel="?";
    private static final String[] labels=new String[]{"","null","?","NaN"};
    //*************************************	
    public NullLabels(int i)
    {
        super(i);
    }	
    //*************************************
    public static boolean isNullLabel(String label)
    {
        for(int i=0;i<labels.length;i++)            
            if(label.equalsIgnoreCase(labels[i]))
                return true;
        return false;
    }
    //*************************************
    public static boolean isNullValue(float value)
    {
        return Float.isNaN(value);
    }
    //*************************************
}

