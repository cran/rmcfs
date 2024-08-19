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
package dmLab.classifier.svm;

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

public class SVMParams extends Params {

    
    public float c;
    //*************************************
    public SVMParams()
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
        c=1.0f;
	    return true;
    }
    //*************************************    
    @Override
    public String toString() 
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("###  svm Classifier Parameters ###").append('\n').append('\n');
        tmp.append("svm.c="+ c).append('\n');
		tmp.append(super.toString());
		
		return tmp.toString();
    }
    //*************************************
    @Override
    protected boolean update(Properties properties) 
    {
        c=Float.valueOf(properties.getProperty("svm.c", "1.0")).floatValue();
        return true;
    }
    //*************************************

}
