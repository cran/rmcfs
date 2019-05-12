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
package dmLab.utils.statList;

import dmLab.utils.StringUtils;


public class StatsObject {
	private float[] values;
	
	//********************************
	public StatsObject(String s){
		init(s,',');
	}
	//********************************
	public StatsObject(float[] val){
		this.values=val.clone();
	}
	//********************************
	public String toString(){
		return toString(',');
	}
	//********************************
	public String toString(char sep){
		StringBuffer tmp=new StringBuffer();
		for(int i=0;i<values.length;i++){
			if(i>0)
				tmp.append(sep);
			tmp.append(values[i]);
		}
		return tmp.toString();
	}
	//********************************
	public int size()
	{
		return values.length;
	}
	//********************************
	public float get(int index)
	{
		return values[index];
	}
	//********************************
	public int init(String s,char sep)
	{
		String[] chain = StringUtils.tokenizeString(s, new char[]{','}, false);
		values = new float[chain.length];
		for(int i=0;i<values.length;i++){
			values[i]=Float.parseFloat(chain[i]);
		}
		return values.length;
	}
	//********************************
}
