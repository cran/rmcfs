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

import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.measuresRI.Importance;
import dmLab.utils.GeneralUtils;

public class KMeansCutoff extends CutoffMethod {
	
	//****************************************
	public KMeansCutoff(MCFSParams mcfsParams) {
		super(mcfsParams);
		name = "kmeans";
	}
	//*********************************
	public double getCutoff(Importance[] importance){
		
		return getCutoffMinRI(Importance.toValues(importance));		
	}
	//****************************************
	public double getCutoffMinRI(double[] values) {
		double minRI = 0;
		
		minRI = kmeans(values);
		
		System.out.println("Minimal (based on k-means clustering) RI = " + GeneralUtils.format(minRI,7));
		
		return minRI;
	}
	//****************************************
	private double kmeans(double[] vals) {
		
		int max_iters = 100;
		
		// Initialize clusters to border values.
		double clsA=vals[0], clsB=vals[0];		
		for(int i=1; i<vals.length; i++) {
			if(vals[i] < clsA) clsA = vals[i];
			if(vals[i] > clsB) clsB = vals[i];
		}
		
		double clsA_, clsB_;
		
		int[] indices = new int[vals.length];

		for(int i=0; i<max_iters; i++) {
			
			// Assign values to clusters.
			for(int j=0; j<vals.length; j++) {
				indices[j] = Math.abs(clsA-vals[j]) < Math.abs(clsB-vals[j]) ? 0 : 1;
			}
			
			clsA_ = clsA;
			clsB_ = clsB;
			
			// Calculate new centroids.
			clsA = 0.0;
			clsB = 0.0;
			int ca = 0, cb = 0;
			for(int j=0; j<vals.length; j++) {
				if(indices[j] == 0) {
					clsA += vals[j];
					ca++;
				} else {
					clsB += vals[j];
					cb++;
				}
			}
			clsA /= ca;
			clsB /= cb;
			
			// Break if no difference.
			if(clsA == clsA_ && clsB == clsB_) 
				break;
		}
		
		// Find the smallest value in the larger cluster.
		double minRI = clsB;
		for(int i=0; i<vals.length; i++) {
			if(indices[i] == 1 && vals[i] < minRI) minRI = vals[i];	
		}
		
		return minRI;
	}
	//****************************************
}
