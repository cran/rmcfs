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
package dmLab.classifier.m5;

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

public class M5Params extends Params {

    public boolean rTree;
    public int minNumObj;
    public boolean unpruned;            
    public boolean unsmoothed;
    //*************************************
    public M5Params()
    {
        super();
    }
    //*************************************
    @Override
    public boolean check(FArray array) 
    {
        return true;
    }
    //*************************************    
    @Override
    public boolean setDefault() 
    {    	
        rTree = true;
        minNumObj = 2;
        unpruned = false;            
        unsmoothed = false;    	
	    return true;
    }
    //*************************************    
    @Override
    public String toString() 
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("###  m5 Classifier Parameters ###").append('\n').append('\n');
        tmp.append("m5.rTree="+ rTree).append('\n');
        tmp.append("m5.minNumObj="+ minNumObj).append('\n');
        tmp.append("m5.unpruned="+ unpruned).append('\n');
        tmp.append("m5.unsmoothed="+ unsmoothed).append('\n');
		tmp.append(super.toString());
		
		return tmp.toString();
    }
    //*************************************
    @Override
    protected boolean update(Properties properties) 
    {
    	rTree=Boolean.valueOf(properties.getProperty("m5.rTree", "true")).booleanValue();
	    minNumObj=Integer.valueOf(properties.getProperty("m5.minNumObj", "2")).intValue();
	    unpruned=Boolean.valueOf(properties.getProperty("m5.unpruned", "false")).booleanValue();
	    unsmoothed=Boolean.valueOf(properties.getProperty("m5.unsmoothed", "false")).booleanValue();
        return true;
    }
    //*************************************

}
