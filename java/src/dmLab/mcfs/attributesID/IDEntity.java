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

import dmLab.utils.MyDict;

public class IDEntity implements Comparable<IDEntity> 
{
	public IDLink link;
	public IDProps props;
	//******************************
	public IDEntity(IDLink link, IDProps props){
		this.link = link;
		this.props = props;
	}
	//******************************
	@Override
	public int compareTo(IDEntity interDep) {
		return props.compareTo(interDep.props);
	}
	//******************************	
	public String toString(){
		return toString(null);
	}
	//******************************
	public String toString(MyDict myDict){
		StringBuffer tmp = new StringBuffer();
		tmp.append(link.toString(myDict)).append(" ").append(props);
		return tmp.toString();
	}
	//******************************	

}
