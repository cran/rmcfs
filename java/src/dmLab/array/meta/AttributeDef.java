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
package dmLab.array.meta;

import java.util.Arrays;

public class AttributeDef extends Attribute
{  
	public String[] decValues;
	public short role;

	public static short ROLE_IGNORE = 0;
	public static short ROLE_INPUT = 1;
	public static short ROLE_DECISION = 2;

	//****************************************
	public AttributeDef()
	{
		super();
		decValues = null;
		role = ROLE_INPUT;
	}
	//****************************************
	@Override
	public String toString()
	{
		String ret = name + " " + type2String(type) + " " + role;
		//if(decValues != null)
			ret += " " + Arrays.toString(decValues);
		
		return ret;
	}
	//****************************************
}
