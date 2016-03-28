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
package dmLab;


public class DMLabInfo
{
	public static String VERSION = "2.1.0";
	public static String DATE = "2016.03.28";

	public DMLabInfo()
	{
	}
//	*************************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("*****************************************").append('\n');
		tmp.append("********          dmLab           *******").append('\n');
		tmp.append("***            version "+VERSION+"          ***").append('\n');
		tmp.append("*****           "+DATE+"          *****").append('\n');
		tmp.append("*****************************************").append('\n');
		tmp.append("*****************************************").append('\n');
		tmp.append("Created by Michal Draminski [mdramins@ipipan.waw.pl]").append('\n');
		tmp.append("http://www.ipipan.eu/staff/m.draminski/").append('\n');
		tmp.append("Polish Academy of Sciences - Institute of Computer Science").append('\n');
		tmp.append("Department of Artificial Intelligence").append('\n');
		tmp.append("**************************************************************************").append('\n');
        tmp.append("'MCFS-ID' and 'ADX' are developed by Michal Draminski").append('\n');
        tmp.append("'rmcfs' developed by Michal Draminski & Julian Zubek").append('\n');
        tmp.append("'SLIQ' developed by Mariusz Gromada").append('\n');
		tmp.append("**************************************************************************").append('\n');
        tmp.append("If you want to use dmLab or 'MCFS-ID' in your work, please cite the paper:").append('\n');
        tmp.append("M.Draminski, A.Rada-Iglesias, S.Enroth, C.Wadelius, J. Koronacki, J.Komorowski").append('\n');
        tmp.append("'Monte Carlo feature selection for supervised classification', BIOINFORMATICS 24(1): 110-117 (2008)").append('\n');
		tmp.append("**************************************************************************").append('\n');
		tmp.append("**************************************************************************").append('\n');
		return tmp.toString();
	}
//************************************************
}
