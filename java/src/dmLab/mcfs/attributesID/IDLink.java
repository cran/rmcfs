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
package dmLab.mcfs.attributesID;

import java.awt.Dimension;
import dmLab.utils.MyDict;

public class IDLink
{
	public int parentId;
	public int childId;
	protected int hash;

	//************************
	public IDLink(int parent, int child){		
		parentId = parent;
		childId = child;
		Dimension key = new Dimension(parent, child);
		hash = key.hashCode();
	}
	//************************
	public String getParent(MyDict myDict){
		return myDict.get(parentId);
	}
	//************************
	public String getChild(MyDict myDict){
		return myDict.get(childId);		
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
			IDLink c=(IDLink) obj;
			return parentId == c.parentId && childId == c.childId;
		}
	}
	//************************
}
