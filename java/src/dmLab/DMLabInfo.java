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
package dmLab;

import dmLab.utils.StringUtils;

public class DMLabInfo
{
	public static String VERSION = "2.3.2";
	public static String DATE = "2021.05.28";

	public DMLabInfo()
	{
	}
//	*************************************************
	@Override
    public String toString()
	{
		StringBuffer tmp = new StringBuffer();
		String dmlabVerLable = "#####        dmLab " + VERSION + " [" + DATE + "]        #####";
		String separatorLine = StringUtils.charRepeat('#', dmlabVerLable.length());
		
		tmp.append(separatorLine).append('\n');
		tmp.append(dmlabVerLable).append('\n');
		tmp.append(separatorLine).append('\n');
		tmp.append("Created by Michal Draminski [michal.draminski@ipipan.waw.pl]").append('\n');
		tmp.append("http://www.ipipan.eu/staff/m.draminski/").append('\n');
		tmp.append("Polish Academy of Sciences - Institute of Computer Science").append('\n');
		tmp.append(separatorLine).append('\n');		
		return tmp.toString();
	}
//************************************************
}
