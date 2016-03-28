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
package dmLab.classifier.knn;

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

public class KNNParams extends Params {
    
    protected int knn;
    //*************************************
    public KNNParams()
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
        knn=1;
	    return true;
    }
    //*************************************    
    @Override
    public String toString() 
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("###  knn Classifier Parameters ###").append('\n').append('\n');
        tmp.append("knn.knn="+ knn).append('\n');     
		tmp.append(super.toString());
		
		return tmp.toString();
    }
    //*************************************
    @Override
    protected boolean update(Properties properties) 
    {
        knn=Integer.valueOf(properties.getProperty("knn.knn", "1")).intValue();
        return true;
    }
    //*************************************
}
