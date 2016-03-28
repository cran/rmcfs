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
package dmLab.classifier.sliq.Tree.treePruning.mdlTreePruning;
import dmLab.classifier.sliq.Tree.Const;
import dmLab.classifier.sliq.Tree.Node;
import dmLab.classifier.sliq.Tree.Tree;
import dmLab.classifier.sliq.Tree.treePruning.TreePruning;

public class MdlTreePruning extends TreePruning
{
	protected double[][] attrCountMatrix;
	protected int attributesNumber;
	protected long start;
	protected long stop;
	
	public MdlTreePruning(Tree tree)
	{
		super(tree);
		start = System.currentTimeMillis();
		stop = 0;
		attributesNumber = tree.getAttributesNumber();
		attrCountMatrix = new double[attributesNumber][2];
		int attrIndex = 0;
		for (attrIndex=0; attrIndex<attributesNumber; attrIndex++) {
			attrCountMatrix[attrIndex][0] = 0;
			attrCountMatrix[attrIndex][1] = 0;
		}
		attrCount(tree.root);
		for (attrIndex=0; attrIndex<attributesNumber; attrIndex++) {
			if (attrCountMatrix[attrIndex][0] > 0)
				attrCountMatrix[attrIndex][1] = Math.log(attrCountMatrix[attrIndex][0])/Math.log(2);
		}
	}
	
	// **************************************************
	@Override
    public void treePrune()
	{
		runMdl(tree.root);
		stop = System.currentTimeMillis();
		tree.setTreePruningTime(((float)(stop-start))/1000);
	}
	// **************************************************
	protected void runMdl(Node node)
	{
		if (costLeaf(node) < costBoth(node))
			pruneChildNodes(node);
		
		if (node.getLeafIndicator() != Const.LEAF)
		{
			if (node.left != null)
				runMdl(node.left);
			if (node.right != null)
				runMdl(node.right);
		}
	}
	// **************************************************
	protected void pruneChildNodes(Node node)
	{
		node.left = null;
		node.right = null;
		node.setLeafIndicator(Const.LEAF);
	}
	// **************************************************
	protected void attrCount(Node node)
	{
		if (node.getLeafIndicator() != Const.LEAF)
			attrCountMatrix[node.getSplittingAttrIndex()][0]+=1;
		
		if (node.left != null)
			attrCount(node.left);
		
		if (node.right != null)
			attrCount(node.right);
	}
	// **************************************************
	protected double costLeaf(Node node)
	{
		return lStruct(node) + err(node);
	}
	// **************************************************
	protected double costBoth(Node node)
	{
		if (node.getLeafIndicator() == Const.LEAF)
			return costLeaf(node);
		
		return lStruct(node) + lTest(node) + costBoth(node.left) + costBoth(node.right);
	}
	// **************************************************
	protected double err(Node node)
	{
		return node.getErrorsNumber();
	}
	// **************************************************
	protected double lTest(Node node)
	{
		if (node.getSplittingAttrType() == Const.NOMINAL_ATTR)
			return attrCountMatrix[node.getSplittingAttrIndex()][1];
		else
			return (Math.log(node.getSplittingValues()[0])/Math.log(2));
	}
	// **************************************************
	protected double lStruct(Node node)
	{
		return 1;
	}
	// **************************************************
	// **************************************************
}
