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
package dmLab.mcfs.attributesRI.measuresRI;



public class Importance implements Comparable<Importance>{
	
	public String name;
	public double importance;
	//***************************
	public Importance(String name, double importnance){
		this.name = name;
		this.importance = importnance;
	}
	//***************************
    public int compareTo(Importance attributeRI) 
    {
        double result=attributeRI.importance-importance;
        if(result>0.0)                    
            return 1;
        else if(result<0.0)                    
            return -1;
        else
            return 0;
    }
    
    //**************************************
	public static double[] toValues(Importance[] importance){
		double[] result = new double[importance.length];
		for(int i=0;i<importance.length;i++)
		{
			result[i]=importance[i].importance;
		}
		return result;
	}
	//****************************************
	public String toString(){
		StringBuffer tmp=new StringBuffer();
		tmp.append(name).append(", ").append(importance);
		return tmp.toString();
	}
	//****************************************
}
