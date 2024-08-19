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
package dmLab.mcfs.cutoffMethods;

import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.measuresRI.Importance;

public abstract class CutoffMethod {

	protected MCFSParams mcfsParams;
	protected String name;
	
	//****************************************
	public CutoffMethod(MCFSParams mcfsParams) {
		this.mcfsParams = mcfsParams;
	}
	//****************************************
	public abstract double getCutoff(Importance[] importance);	
	//****************************************
}
