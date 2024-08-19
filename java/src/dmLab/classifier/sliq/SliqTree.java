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

import java.util.ArrayList;

import dmLab.array.FArray;
import dmLab.classifier.sliq.Tree.Const;
import dmLab.classifier.sliq.Tree.Node;
import dmLab.classifier.sliq.Tree.Tree;




//**********************************************************
class SliqTree extends Tree
{

	private ArrayList <Node>leafs;
	private int classProxiesNumber;

	public SliqTree(ClassList classList)
	{
		super.root = new SliqNode(classList.getProxiesNumber());
		leafs = new ArrayList<Node>();
		leafs.add(super.root);
		classProxiesNumber = classList.getProxiesNumber();
	}
//	************************************************
	public int getLeafsSize()
	{
		return leafs.size();
	}
//	************************************************
	public SliqNode getLeaf(int leafIndex)
	{
		return (SliqNode)leafs.get(leafIndex);
	}
	// ************************************************
	private int countNodeChildEvents(SliqNode node, boolean attrType, boolean child)
	{
		int[] histArray;
		int sum = 0;

		if (attrType == Const.NUMERIC_ATTR)
		{
			//numeric attributes

			histArray = node.numericHistogram.getHistogramArray(child);

		} else {
			//nominal attributes

			histArray = node.nominalHistogram.getHistogramArray(child);

		}

		for (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
			sum+=histArray[classProxyIndex];
		}

		return sum;
	}
	// ************************************************
	private float calcNodeChildGiniIndex(SliqNode node, boolean attrType, boolean child) {

		float eventsNumber = node.getEventsNumber();
		float childEventsNumber = countNodeChildEvents(node, attrType, child);
		int[] histArray;
		float sum  = 0;

		if (childEventsNumber > 0)
		{
			if (attrType == Const.NUMERIC_ATTR)
			{
				//numeric attributes
				histArray = node.numericHistogram.getHistogramArray(child);

			}
			else {

				//nominal attributes
				histArray = node.nominalHistogram.getHistogramArray(child);
			}

			for (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
				sum += Math.pow( ((histArray[classProxyIndex])/childEventsNumber),2 );
			}
			return (childEventsNumber/eventsNumber)*(1-sum);

		}
		else
			return 0;

	}
	// ************************************************
	public float getGoodnessOfSplit(SliqNode node, boolean attrType)
	{
		return node.getDiversityMeasure() - (calcNodeChildGiniIndex(node, attrType, Const.LEFT_CHILD) + calcNodeChildGiniIndex(node, attrType, Const.RIGHT_CHILD));
	}
//	************************************************
	private ArrayList<Integer> compactNominalHistogram(SliqNode node)
	{
		ArrayList <Integer> compactHist = new ArrayList<Integer>();
		int attrProxiesNumber = node.nominalHistogram.getAttrProxiesNumber();
		int[] attrHistArray = node.nominalHistogram.getAttrProxiesSummaryArray(Const.RIGHT_CHILD);

		for (int attrProxyIndex=0; attrProxyIndex < attrProxiesNumber; attrProxyIndex++)
			if (attrHistArray[attrProxyIndex] > 0)
				compactHist.add(attrProxyIndex);

		return compactHist;
	}
//	************************************************
	public void calcFreqClassGiniPurity(SliqNode node, ClassList classList)
	{

		int nonZeroClassProxyIndex = 0;
		int maxFreq = 0;
		int classMaxFreqIndex = 0;
		node.setEventsNumber(0);
		float sum = 0;


		for (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
			node.incEventsNumber(node.classFrequencies[classProxyIndex]);
			if (node.classFrequencies[classProxyIndex] > 0) nonZeroClassProxyIndex++;
			if (node.classFrequencies[classProxyIndex] > maxFreq) {
				maxFreq = node.classFrequencies[classProxyIndex];
				classMaxFreqIndex = classProxyIndex;
			}
		}

		if (nonZeroClassProxyIndex >= 2 )
			node.setLeafIndicator(Const.NODE);
		else
			node.setLeafIndicator(Const.LEAF);

		for (int classProxyIndex = 0; classProxyIndex < classProxiesNumber; classProxyIndex++) {
			sum += Math.pow( (((float)node.classFrequencies[classProxyIndex])/node.getEventsNumber()),2 );
		}

		node.setDiversityMeasure(1-sum);

		node.setNodeClass(classList.getClassProxy(classMaxFreqIndex));
		node.setNodeClassFrequency( ((float)maxFreq)/((float)node.getEventsNumber()) );
		node.setErrorsNumber(node.getEventsNumber() - maxFreq);

	}
//	************************************************

	public float[] findBestSubset(SliqNode node, AttributeList attributeList) {

		ArrayList <Integer> compactHist = compactNominalHistogram(node);
		//int attrProxiesNumber = node.nominalHistogram.getAttrProxiesNumber();
		int compactAttrProxiesNumber = compactHist.size();
		int NumberOfSubsets = (int)Math.pow(2,(double)compactAttrProxiesNumber - 1);
		float[] subset;
		float tmpGoodness = 0;
		int bestSubsetIndex = 0;
		int maxLength = 0;
		int tmpLenght = 0;
		int length = 0;

		String binarySubset;
		for (int subsetIndex=1; subsetIndex<NumberOfSubsets; subsetIndex++) {
			binarySubset = Integer.toBinaryString(subsetIndex);
			length = binarySubset.length();
			tmpLenght = 0;
			for (int i=0; i < length; i++) {
				if (binarySubset.charAt(i) == '1') {
					node.nominalHistogram.updateToLeft(compactHist.get(compactAttrProxiesNumber-(length-1-i)-1));
					tmpLenght++;
				}
			}
			tmpGoodness = getGoodnessOfSplit(node, Const.NOMINAL_ATTR);

			if (tmpGoodness > node.tmpGoodnessOfSplit) {
				node.tmpGoodnessOfSplit = tmpGoodness;
				bestSubsetIndex = subsetIndex;
				maxLength = tmpLenght;
			}

			node.nominalHistogram.allToRight();
		}

		subset = new float[maxLength];
		binarySubset = Integer.toBinaryString(bestSubsetIndex);
		length = binarySubset.length();
		tmpLenght = 0;
		for (int i=0; i < length; i++) {
			if (binarySubset.charAt(i) == '1') {
				subset[tmpLenght] = attributeList.getAttrProxyValue(compactHist.get(compactAttrProxiesNumber-(length-1-i)-1));
				tmpLenght++;
			}
		}


		return subset;
	}



	public void createLeafs(ClassList classList) {
		int leafsNumber = getLeafsSize();
		SliqNode node;
		ArrayList <Node>tmpLeafs = new ArrayList<Node>();

		for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++) {
			node = getLeaf(leafIndex);
			if (node.getSplittingAttrIndex() == Const.NO_SPLITTING_ATTR_INDEX){
				node.setLeafIndicator(Const.LEAF);
				//System.out.println("Nieczysty ale czysty : " + leafIndex + ", node: " + node);
			}
			if (node.getLeafIndicator() == Const.NODE) {
				node.left = new SliqNode(classList.getProxiesNumber());
				node.right = new SliqNode(classList.getProxiesNumber());
				tmpLeafs.add(node.left);
				tmpLeafs.add(node.right);
			} else {
				tmpLeafs.add(node);
			}
		}

		leafs = tmpLeafs;

	}
	//*********************************
	public boolean leafsPurityCheck()
	{
		int leafsNumber = getLeafsSize();

		for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++)
		{
			if (getLeaf(leafIndex).getLeafIndicator() == Const.NODE)
				return Const.NODE;
		}

		return Const.LEAF;
	}
	//*********************************
	public boolean applyNodeTest(SliqNode node, FArray events, int eventIndex)
	{
		float value = events.readValue(node.getSplittingAttrIndex(), eventIndex);
		float[] splittingValues = node.getSplittingValues();
		if (node.getSplittingAttrType() == Const.NUMERIC_ATTR) {
			if (value <= splittingValues[0])
				return Const.LEFT_CHILD;
			else
				return Const.RIGHT_CHILD;

		} else {

			int i=0;

			do {
				if (value == splittingValues[i]) return Const.LEFT_CHILD;
				i++;
			} while (i<splittingValues.length);

			return Const.RIGHT_CHILD;

		}

	}


	public void showLeafs() 
	{
		int leafsNumber = getLeafsSize();
		SliqNode node;
		//float[] splittingValues;
		System.out.println("");
		System.out.println("");
		System.out.println("======================= LEAFS ================");

		//int pp = 0;
		for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++) {
			node = getLeaf(leafIndex);
			//  if (node.purity == Const.NOT_PURE_LEAF) {
			//  pp++;
			//splittingValues = node.getSplittingValues();
			System.out.println("");
			System.out.println("");
			System.out.println("--- LEAF ---");
			System.out.println("Leaf index           : " + leafIndex + ", Leaf ref.: " + node);
			System.out.println("Purity               : " + node.getLeafIndicator());
			System.out.println("Events number        : " + node.getEventsNumber());
			System.out.println("Class                : " + node.getNodeClass());
			System.out.println("Freq                 : " + node.getNodeClassFrequency());
		}
		//System.out.println("Gini index           : " + node.getGiniIndex());
		//System.out.println("Goodness of split    : " + node.getGoodnessOfSplit());
		//System.out.println("Splitting attr index : " + node.getSplittingAttrIndex());
		//System.out.println("Splitting attr type  : " + node.getSplittingAttrType());
		//System.out.print("Splitting values     : ");
		//if (splittingValues != null) {
		//for (int i = 0; i < splittingValues.length; i++) {
		//System.out.print(splittingValues[i] + ", ");
		// }
		//}
		//  }

		//System.out.println("========================== " + pp);
	}


}

//**********************************************************
