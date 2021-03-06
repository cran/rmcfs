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
package dmLab.classifier.sliq;

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;




public class SliqParams extends Params
{
	boolean mdlPruning;
	int maxLevel;
	//*************************************
	public SliqParams()
	{
		super();
	}
	//*************************************
	@Override
    public boolean setDefault()
	{
		mdlPruning=true;
		maxLevel=125;
	    return true;
	}
	//*************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("### SLIQ Parameters ### ").append('\n');
		tmp.append("sliq.mdlPruning="+mdlPruning).append('\n');
		return tmp.toString();
	}
	//*************************************
	@Override
    protected boolean update(Properties properties)
	{
		mdlPruning=Boolean.valueOf(properties.getProperty("sliq.mdlPruning", "true")).booleanValue();;
		return true;
	}
	//*************************************
	@Override
    public boolean check(FArray array)
	{
		return true;
	}
	//*************************************
}
