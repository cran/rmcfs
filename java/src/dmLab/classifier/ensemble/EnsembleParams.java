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
package dmLab.classifier.ensemble;

import java.util.Arrays;
import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;
import dmLab.utils.ArrayUtils;

public class EnsembleParams extends Params {
    
    protected float weights[]={1f,1f,1f,1f,1f,1f,1f,1f,1f};;
    //*************************************
    public EnsembleParams()
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
	    return true;
    }
    //*************************************
    @Override
    public String toString() {

        StringBuffer tmp=new StringBuffer();
        tmp.append("### ensemble Classifier Parameters ###").append('\n').append('\n');
        tmp.append("ensemble.weights="+Arrays.toString(weights)).append('\n');
		tmp.append(super.toString());
		
		return tmp.toString();
    }
    //*************************************
    @Override
    protected boolean update(Properties properties) 
    {
        String arrayStr=properties.getProperty("ensemble.weights", "[1,1,1,1,1,1,1,1,1]");
        arrayStr=arrayStr.substring(arrayStr.indexOf('[')+1,arrayStr.indexOf(']'));        
        double tmpArray[]=ArrayUtils.toDoubleArray(arrayStr);
        weights=new float[tmpArray.length];
        for(int i=0;i<tmpArray.length;i++)
            weights[i]=(float)tmpArray[i];  
        
        return true;
    }
    //*************************************

}
