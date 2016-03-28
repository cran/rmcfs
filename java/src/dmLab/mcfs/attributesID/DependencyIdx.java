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
package dmLab.mcfs.attributesID;

import java.awt.Dimension;
import dmLab.utils.MyDict;

public class DependencyIdx
{
	public int parentId;
	public int childId;
	public int hash;

	//************************
	public DependencyIdx(int parent, int child){		
		parentId = parent;
		childId = child;
		Dimension key = new Dimension(parent, child);
		hash = key.hashCode();
	}
	//************************
	public String toString(MyDict myDict){
		StringBuffer tmp=new StringBuffer();
		if(myDict==null)
			tmp.append(parentId).append(",").append(childId);
		else
			tmp.append(myDict.get(parentId)).append(",").append(myDict.get(childId));
		return tmp.toString();
	}
	//************************
	public String toString(){		
		return toString(null);
	}
	//************************
	@Override
	public int hashCode() {
		return hash;
	}
	//************************
	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		else{
			DependencyIdx c=(DependencyIdx) obj;
			return parentId == c.parentId && childId == c.childId;
		}
	}
	//************************
}
