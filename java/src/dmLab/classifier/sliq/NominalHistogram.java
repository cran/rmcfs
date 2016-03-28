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
package dmLab.classifier.sliq;

import dmLab.classifier.sliq.Tree.Const;

//**********************************************************
class NominalHistogram {
	private int[][] left;
	private int[][] right;
	private int[] leftClassProxiesSummary;
	private int[] leftAttrProxiesSummary;
	private int[] rightClassProxiesSummary;
	private int[] rightAttrProxiesSummary;
	private int classProxiesNumber;
	private int attrProxiesNumber;

	public NominalHistogram(ClassList classList, AttributeList attributeList) {
		attrProxiesNumber = attributeList.getProxiesNumber();
		classProxiesNumber = classList.getProxiesNumber();
                //System.out.println("attrProxiesNumber: " + attrProxiesNumber + " classProxiesNumber: " + classProxiesNumber + " " + this);
		left = new int[attrProxiesNumber][classProxiesNumber];
		right = new int[attrProxiesNumber][classProxiesNumber];
		leftClassProxiesSummary = new int[classProxiesNumber];
		leftAttrProxiesSummary = new int[attrProxiesNumber];
		rightClassProxiesSummary = new int[classProxiesNumber];
		rightAttrProxiesSummary = new int[attrProxiesNumber];
		for (int i=0; i<attrProxiesNumber; i++) {
			leftAttrProxiesSummary[i] = 0;
			rightAttrProxiesSummary[i] = 0;
			for (int j=0; j<classProxiesNumber; j++) {
				left[i][j] = 0;
				right[i][j] = 0;
			}
		}

		for (int j=0; j<classProxiesNumber; j++) {
			leftClassProxiesSummary[j] = 0;
			rightClassProxiesSummary[j] = 0;
		}

	}

	public void updateToLeft(int attrProxyIndex) {
		for (int classProxyIndex = 0;  classProxyIndex < classProxiesNumber; classProxyIndex++) {
			left[attrProxyIndex][classProxyIndex] = right[attrProxyIndex][classProxyIndex];
			leftClassProxiesSummary[classProxyIndex] += right[attrProxyIndex][classProxyIndex];
			rightClassProxiesSummary[classProxyIndex] -= right[attrProxyIndex][classProxyIndex];
			right[attrProxyIndex][classProxyIndex] = 0;
		}
		leftAttrProxiesSummary[attrProxyIndex] = rightAttrProxiesSummary[attrProxyIndex];
		rightAttrProxiesSummary[attrProxyIndex] = 0;
	}

	public void allToRight() {
		for (int attrProxyIndex = 0; attrProxyIndex < attrProxiesNumber; attrProxyIndex++) {
			for (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
				right[attrProxyIndex][classProxyIndex] += left[attrProxyIndex][classProxyIndex];
				rightClassProxiesSummary[classProxyIndex] += left[attrProxyIndex][classProxyIndex];
				leftClassProxiesSummary[classProxyIndex] -= left[attrProxyIndex][classProxyIndex];
				left[attrProxyIndex][classProxyIndex] = 0;
			}
			rightAttrProxiesSummary[attrProxyIndex] += leftAttrProxiesSummary[attrProxyIndex];
			leftAttrProxiesSummary[attrProxyIndex] = 0;

		}
	}

	public void incRight(int attrProxyIndex, int classProxyIndex) {

		//show();
        //System.out.print(attrProxiesNumber + " " + classProxiesNumber + " " + attrProxyIndex+" "+classProxyIndex);
        //System.out.print(" "+right.length);
        //System.out.print(" "+right[attrProxyIndex].length);
        //System.out.print(" w1: " + right[attrProxyIndex][classProxyIndex]);
        rightAttrProxiesSummary[attrProxyIndex] += 1;
        rightClassProxiesSummary[classProxyIndex] += 1;

        right[attrProxyIndex][classProxyIndex]+=1;
        //System.out.println(" w2: " + right[attrProxyIndex][classProxyIndex]);
	}


	public int[] getHistogramArray(boolean child) {

		if (child == Const.LEFT_CHILD) {
			return leftClassProxiesSummary;
		} else {
			return rightClassProxiesSummary;
		}

	}

	public int[] getAttrProxiesSummaryArray(boolean child) {

		if (child == Const.LEFT_CHILD) {
			return leftAttrProxiesSummary;
		} else {
			return rightAttrProxiesSummary;
		}

	}

	public int getAttrProxiesNumber() {
		return attrProxiesNumber;
	}

	public void show() {
		System.out.println("");
		System.out.println(" --------- Nominal Histogram ---------- ");
		System.out.println(" LEFT: ");
		for (int attrProxyIndex = 0; attrProxyIndex < attrProxiesNumber; attrProxyIndex++) {
			System.out.print(leftAttrProxiesSummary[attrProxyIndex] + " | ");
			for  (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
				System.out.print(left[attrProxyIndex][classProxyIndex] + ", ");
			}
			System.out.println("");
		}
		System.out.println("------------");
		System.out.print("  | ");
		for  (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
			System.out.print(leftClassProxiesSummary[classProxyIndex] + ", ");;
		}

		System.out.println("");
		System.out.println(" RIGHT: ");
		for (int attrProxyIndex = 0; attrProxyIndex < attrProxiesNumber; attrProxyIndex++) {
			System.out.print(rightAttrProxiesSummary[attrProxyIndex] + " | ");
			for  (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
				System.out.print(right[attrProxyIndex][classProxyIndex] + ", ");;
			}
			System.out.println("");
		}
		System.out.println("------------");
		System.out.print("  | ");
		for  (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
			System.out.print(rightClassProxiesSummary[classProxyIndex] + ", ");;
		}
		System.out.println("");
		System.out.println(" --------- End  ---------- ");
	}
}
