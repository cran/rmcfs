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
package dmLab.classifier.rnd;

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

public class RNDParams extends Params
{
	protected double seed;

//	*************************************
//	*** constructor sets default values
	public RNDParams()
	{    
		super();
	}
//	*************************************
	@Override
    public boolean setDefault()
	{
		seed=12345;
	    return true;
	}
//	*************************************
//	***  parameters to String
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("### RND Classifier Parameters ###").append('\n');
		tmp.append("rnd.seed="+ seed).append('\n');
		tmp.append(super.toString());
		
		return tmp.toString();
	}
	//*****************************************
	@Override
    protected boolean update(Properties properties)
	{
		seed=Double.valueOf(properties.getProperty("rnd.seed", "12345")).intValue();
		return true;
	}
	//*****************************************
	@Override
    public boolean check(FArray array)
	{
		//more advanced checking if parameters are proper for input data
		//for such simple classifier there is no need to do checking of parameters using Array
		if (seed < 0)
		{
			System.err.println("Error: Seed is incorrect!");
			return false;
		}
		return true;    
	}
	//*****************************************
}
