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

import java.util.HashMap;
import java.util.Iterator;

import dmLab.utils.MyDict;

public class IDEdgeIterator implements Iterator<IDEdge>{
	
	protected HashMap<IDLink, IDProps> myIDMap;
	protected MyDict myDict;

	protected IDLink[] myIDLinksArray = new IDLink[1];	
	protected int currIdx;

	//*************************************
	public IDEdgeIterator(HashMap<IDLink, IDProps> myIDMap, MyDict myDict){
		this.myIDMap = myIDMap;
		this.myDict = myDict;
		
		myIDLinksArray = myIDMap.keySet().toArray(myIDLinksArray);
		currIdx = 0;
	}
	//*************************************    		
	@Override
	public boolean hasNext() {
		return currIdx < myIDLinksArray.length;
	}
	//*************************************
	@Override
	public IDEdge next() {
		if(!hasNext())
			return null;
		
		IDEdge edge = getEdge();
		while(hasNext() && edge == null){
			currIdx++;
			edge = getEdge();			
		}		
		currIdx++;
		return edge;	
	}
	//*************************************	
	private IDEdge getEdge(){
		if(currIdx >= myIDLinksArray.length)
			return null;
		
		IDLink link = myIDLinksArray[currIdx];
		if(link != null){
			IDEdge edge = new IDEdge(link.getParent(myDict), link.getChild(myDict)); 
			edge.weight = myIDMap.get(link).weight;
			return edge;
		} else {
			return null;
		}
	}	
	//*************************************
	@Override
	public void remove() {
	}
	//*************************************    		

}
