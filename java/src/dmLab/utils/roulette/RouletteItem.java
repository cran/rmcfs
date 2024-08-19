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
package dmLab.utils.roulette;

public class RouletteItem {
	public short weight;
	public int size;
	public int cumSum;
	public int selSize;
	
	//********************************
	public RouletteItem(short weight, int size){
		this.weight = weight;
		this.size = size;
		this.cumSum = 0;
		this.selSize = 0;
	}
	//********************************
	public String toString(){
		StringBuffer tmp = new StringBuffer();		
		tmp.append(weight).append("\t").append(size).append("\t");
		tmp.append(selSize).append("\t").append(cumSum);		
		return tmp.toString();
	}	
	//********************************	
}
