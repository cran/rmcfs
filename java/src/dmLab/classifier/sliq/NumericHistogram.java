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
package dmLab.classifier.sliq;

import dmLab.classifier.sliq.Tree.Const;



//**********************************************************
class NumericHistogram {
	private int[] left;
	private int[] right;
	private int classProxiesNumber;
	
	public NumericHistogram(int classProxiesNumber) {
		left = new int[classProxiesNumber];
		right = new int[classProxiesNumber];
		this.classProxiesNumber = classProxiesNumber;
	}
	
	public void init(int[] classFrequencies) {
		for (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
			left[classProxyIndex] = 0;
			right[classProxyIndex] = classFrequencies[classProxyIndex];
		}
	}
	
	public void updateToLEft(int classProxyIndex) {
		left[classProxyIndex]+=1;
		right[classProxyIndex]-=1;
	}
	
	public void incRight(int classProxyIndex) {
		right[classProxyIndex]+=1;
	}
	
	public int[] getHistogramArray(boolean child) {
		
		if (child == Const.LEFT_CHILD) {
			return left;
		} else {
			return right;
		}
		
	}
	
	public void show() {
		System.out.println(" --------- Numeric Histogram ---------- ");
		//System.out.println(" Lefa: " + this);
		
		for (int i=0; i<classProxiesNumber; i++) {
			System.out.println(i + ", L: " + left[i] + ", R: " + right[i]);
		}
		System.out.println(" --------- End ----------------------- ");
	}
	
}
