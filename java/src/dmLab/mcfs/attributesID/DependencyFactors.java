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

public class DependencyFactors implements Comparable<DependencyFactors>
{
	public float weight;

	//************************
	public DependencyFactors(float weight){
		this.weight = weight;
	}
	//************************
	public float addWeight(float weight){
		this.weight += weight;
		return this.weight;
	}
	//************************
	public boolean add(DependencyFactors connFactors){
		addWeight(connFactors.weight);
		return true;
	}	
	//************************
	public String toString(){
		StringBuffer tmp=new StringBuffer();
		tmp.append("").append(weight).append("");
		return tmp.toString();
	}
	//************************
    public int compareTo(DependencyFactors conn) 
    {
        //TODO tu trzeba obsluge NA dodac
    	float result = conn.weight - weight;        
        if(result > 0.0)
            return 1;
        else if(result < 0.0)                    
            return -1;
        else
            return 0;
    }
    //**************************************

}
