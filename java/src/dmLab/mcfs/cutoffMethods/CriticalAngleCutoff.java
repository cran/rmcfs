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
package dmLab.mcfs.cutoffMethods;

import java.util.Arrays;

import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.measuresRI.Importance;
import dmLab.utils.GeneralUtils;

public class CriticalAngleCutoff extends CutoffMethod {
	
	//*********************************
	public CriticalAngleCutoff(MCFSParams mcfsParams) {
		super(mcfsParams);
		name = "criticalAngle";
	}
	//*********************************
	public double getCutoff(Importance[] importance){
		
		return getCutoffMinRI(Importance.toValues(importance));
	}
	//****************************************
	public double getCutoffMinRI(double[] values) {
		double minRI = 0;		
		double threshold = mcfsParams.cutoffAngle;		
		Arrays.sort(values);
			
		double[] xvalues = new double[values.length];

		// Normalize values.
		double min_val = values[0];
		double max_val = values[values.length-1]-min_val;
		double xval_step = 1.0/values.length;
		for(int i=0; i<values.length; i++) {
			values[i] = (values[i]-min_val)/max_val;
			xvalues[i] = i*xval_step;
		}
		
		double a1 = 0.0;
		double a0 = linearRegressionAngle(xvalues, values, values.length-2);
		for(int i=values.length-3; i>0; i--) {
			a1 = linearRegressionAngle(xvalues, values, i);
			if(Math.abs(a1-a0) < threshold) {
				minRI = values[i]*max_val+min_val;
				break;
			}
			a0 = a1;
		}
				
		System.out.println("Minimal (based on linear regression angle) RI = " + GeneralUtils.format(minRI,7));
		
		return minRI;
	}
	//****************************************	
	private double linearRegressionAngle(double[] xvals, double[] yvals, int shift) {
		double sumy = 0.0, sumx = 0.0;
		for(int i=shift; i<yvals.length; i++) {
			sumy += yvals[i];
			sumx += xvals[i];
		}
		double ybar = sumy / yvals.length;
		double xbar = sumx / yvals.length;

		double xxbar = 0.0, xybar = 0.0;
		for (int i=shift; i < yvals.length; i++) {
			xxbar += (xvals[i] - xbar) * (xvals[i] - xbar);
			xybar += (xvals[i] - xbar) * (yvals[i] - ybar);
		}
		double beta1 = xybar / xxbar;

		return beta1;
	}
	//****************************************
}
