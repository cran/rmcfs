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
package dmLab.classifier.sliq;




public class SliqInfo
{
	public SliqInfo()
	{
	}
//	*************************************************
	public static void print()
	{
		System.out.println(" -------------------------------------------------------- ");
		System.out.println("|           SLIQ Decision Tree implementation            |");
		System.out.println(" -------------------------------------------------------- ");
		System.out.println("| Author     : Mariusz Gromada                           |");
		System.out.println("| Supervisor : Prof. Dr Hab. Jacek Koronacki             |");
		System.out.println("| Implementation in cooperation with:                    |");
		System.out.println("|    Artificial Intelligence Dep.                        |");
		System.out.println("|    Institute of Computer Science                       |");
		System.out.println("|    Polish Academy of Sciences                          |");
		System.out.println("|    http://www.ipipan.waw.pl                            |");
		System.out.println(" -------------------------------------------------------- ");
		System.out.println("| dmLab by Michal Draminski                              |");
		System.out.println("|    e-mail: mdramins@ipipan.waw.pl                      |");
		System.out.println("|    www: http://www.ipipan.waw.pl/staff/m.draminski     |");
		System.out.println(" -------------------------------------------------------- ");
		System.out.println("| Questions / Problems / Issues - contact to:            |");
		System.out.println("|    Mariusz Gromada                                     |");
		System.out.println("|    e-mail: mariusz.gromada@wp.pl                       |");
		System.out.println("|    www: http://multifraktal.net                        |");
		System.out.println(" -------------------------------------------------------- ");
	}
	
}
