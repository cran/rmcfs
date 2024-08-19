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
package dmLab.array.meta;

import java.util.Arrays;

public class AttributeRole extends Attribute
{  
	public String[] decValues;
	public short role;

	public static short ROLE_IGNORE = 0;
	public static short ROLE_INPUT = 1;
	public static short ROLE_DECISION = 2;

	//****************************************
	public AttributeRole()
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
