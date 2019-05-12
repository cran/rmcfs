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

import java.util.Arrays;

import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.measuresRI.Importance;
import dmLab.utils.GeneralUtils;

public class ContrastCutoff extends CutoffMethod {
	
	//*********************************
	public ContrastCutoff(MCFSParams mcfsParams) {
		super(mcfsParams);
		name = "contrast";
	}
	//*********************************
	public double getCutoff(Importance[] importance)
	{
		int size=0;
		for(int i=0;i<importance.length;i++){
			if(importance[i].name.startsWith(MCFSParams.CONTRAST_ATTR_NAME))
				size++;			
		}
		if(size==0){
			return Double.NaN;
		}else{
			double[] values = new double[size];
			for(int i=0,j=0;i<importance.length;i++){
				if(importance[i].name.startsWith(MCFSParams.CONTRAST_ATTR_NAME))
					values[j++]=importance[i].importance;
			}		
			return getCutoffMinRI(values);
		}
	}
	//*********************************	
	public double getCutoffMinRI(double[] values) 
	{		
		double minRI = Double.NaN;		
    	Arrays.sort(values);
    	
    	int cf = Math.max(Math.round(values.length * mcfsParams.contrastCutoff), 1);
    	if(cf <= 0 || cf > values.length) {
    		System.out.println("Invalid mcfsParams.contrastAttrThreshold value, ignoring cutoff.");
    	} else {
    		minRI = values[values.length - cf];
    	   	System.out.println("Cutoff RI (based on contrast attributes) = " + GeneralUtils.formatFloat(minRI,7));
    	}
    	   	
		return minRI;
	}
	//*********************************
}
