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
package dmLab.classifier.ripper;

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

public class RipperParams extends Params {

    boolean usePruning;
    //*****************************
    @Override
    public boolean check(FArray array) 
    {
        return true;
    }
    //*****************************
    @Override
    public boolean setDefault() 
    {
        usePruning=true;
	    return true;
    }
    //*****************************
    @Override
    public String toString() 
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("###  ripper Classifier Parameters ###").append('\n').append('\n');
        tmp.append("ripper.usePruning="+ usePruning).append('\n');     
		tmp.append(super.toString());
		
		return tmp.toString();
    }
    //*****************************
    @Override
    protected boolean update(Properties properties) 
    {    
        usePruning=Boolean.valueOf(properties.getProperty("ripper.usePruning", "true")).booleanValue();
        return true;
    }
    //*****************************
}
