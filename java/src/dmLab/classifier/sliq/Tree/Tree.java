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
package dmLab.classifier.sliq.Tree;
import java.util.HashSet;

import dmLab.array.FArray;
import dmLab.classifier.attributeIndicators.SliqNodeIndicators;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.ExperimentIndicators;


public abstract class Tree
{
	public Node root;
	public Node currentNode;

	protected boolean treeBuildInd;
	protected boolean treePruneInd;
	protected float treeBuildingTime;
	protected float treePruningTime;
	protected int attributesNumber;
	protected int maxTreeDepth;

	public void setAttributesNumber(int attributesNumber)
	{
		this.attributesNumber = attributesNumber;
	}
	// **************************************************
	public int getAttributesNumber()
	{
		return attributesNumber;
	}
	// **************************************************
	public Tree()
	{
		treeBuildInd = Const.TREE_NOT_BUILT;
		treePruneInd = Const.TREE_NOT_PRUNED;
		treeBuildingTime = 0;
		treePruningTime = 0;
		root = null;
		currentNode = null;
		attributesNumber = 0;
	}
	// **************************************************
	public void setTreeBuildInd()
	{
		treeBuildInd = Const.TREE_BUILT;
	}
	// **************************************************
	public void setTreePruneInd()
	{
		treePruneInd = Const.TREE_PRUNED;
	}
	// **************************************************
	public boolean getTreeBuildInd()
	{
		return treeBuildInd;
	}
	// **************************************************
	public boolean getTreePruneInd()
	{
		return treePruneInd;
	}
	// **************************************************
	public String getTreeBuildInfo()
	{
		if (treeBuildInd)
			return "Yes";
		else
			return "No";

	}
	// **************************************************
	public String getTreePruneInfo()
	{
		if (treePruneInd)
			return "Yes";
		else
			return "No";
	}

	// **************************************************
	public void setTreeBuildingTime(float treeBuildingTime)
	{
		this.treeBuildingTime = treeBuildingTime;
		setTreeBuildInd();
	}
	// **************************************************
	public void setTreePruningTime(float treePruningTime)
	{
		this.treePruningTime = treePruningTime;
		setTreePruneInd();
	}
	// **************************************************
	public Node getRoot() {
		return root;
	}

	// **************************************************
	public float classifyEvent(FArray events, int eventIndex)
	{
		currentNode = root;

		float value;
		float[] splittingValues;
		int i;
		boolean test;
		boolean childNull = false;
		boolean child = Const.LEFT_CHILD;

		while ((currentNode.getLeafIndicator() != Const.LEAF ) && (childNull == false))
		{
			value = events.readValue(currentNode.getSplittingAttrIndex(), eventIndex);
			//if under testing is misssing value

			if(Float.isNaN(value))
			{
				if (currentNode.left.eventsNumber<currentNode.right.eventsNumber)
                                    child = Const.RIGHT_CHILD;
				else
                                    child = Const.LEFT_CHILD;
			}
			else
			{
				splittingValues = currentNode.getSplittingValues();

				if (currentNode.getSplittingAttrType() == Const.NUMERIC_ATTR)
				{
					if (value <= splittingValues[0])
						child = Const.LEFT_CHILD;
					else
						child = Const.RIGHT_CHILD;

				}else  //nominal attribute
				{
					i=0;
					test = false;
					do {
						if (value == splittingValues[i])
						{
							child = Const.LEFT_CHILD;
							test = true;
						}
						i++;
					} while ((i<splittingValues.length) && (test == false));

					if (test == false)
						child = Const.RIGHT_CHILD;
				}
			}

			if (child == Const.LEFT_CHILD)
			{
				if (currentNode.left != null)
					currentNode = currentNode.left;
				else
					childNull = true;
			}
			else {
				if (currentNode.right != null)
					currentNode = currentNode.right;
				else
					childNull = true;
			}
		}
		return currentNode.nodeClass;
	}
	//*******************************************
	public String toString(FArray trainArray,boolean struct)
	{
		StringBuffer tmp=new StringBuffer();
		if (struct)
		{
			tmp.append("##### Tree structure #####").append('\n').append('\n');
			printNodes(trainArray, root, 0, "", tmp);
			tmp.append('\n');
		}
		tmp.append("##### Tree info #####").append('\n');
		tmp.append("# Nodes number .............. : " + getNodesNumber()).append('\n');
		tmp.append("# Leafs number .............. : " + getLeafsNumber()).append('\n');
		tmp.append("# Tree height ............... : " + getTreeDepth()).append('\n');
		tmp.append("# Tree built ................ : " + getTreeBuildInd()).append('\n');
		tmp.append("# Tree pruned ............... : " + getTreePruneInd()).append('\n');
		tmp.append("# Tree building time [s] .... : " + treeBuildingTime).append('\n');
		tmp.append("# Tree pruning time [s] ..... : " + treePruningTime).append('\n');
		tmp.append("# Total training time [s] ... : " + (treeBuildingTime+treePruningTime)).append('\n');
		return tmp.toString();
	}
	//*******************************************
	public int getTreeDepth()
	{
		maxTreeDepth = 0;
		return evalTreeDepth(root,0);
	}
	//*******************************************
	protected int evalTreeDepth(Node node,int treeDepth)
	{
		if (treeDepth > maxTreeDepth)
			maxTreeDepth = treeDepth;
		if (node.left != null)
			evalTreeDepth(node.left,treeDepth+1);
		if (node.right != null)
			evalTreeDepth(node.right,treeDepth+1);
		return maxTreeDepth;

	}
	//*******************************************
	public int getLeafsNumber()
	{
		return countLeafs(root);
	}
	//*******************************************
	public int getNodesNumber()
	{
		return countNodes(root);
	}
	//*******************************************
	protected int countLeafs(Node node)
	{
		if (node.getLeafIndicator() == Const.LEAF)
			return 1;
		else
			return countLeafs(node.left) + countLeafs(node.right);
	}
	//*******************************************
	protected int countNodes(Node node)
	{
		if (node.getLeafIndicator() != Const.LEAF)
			return 1 + countNodes(node.left) + countNodes(node.right);
		return 0;
	}
	//*******************************************
	private void printNodes(FArray trainArray, Node node, int offset,String direction,StringBuffer tmp)
	{
		for (int i=0; i<offset; i++)
			tmp.append("|  ");

		tmp.append(direction);

		if (node.getLeafIndicator() != Const.LEAF)
		{
			int idx=node.getSplittingAttrIndex();
			String name=trainArray.attributes[idx].name;

			tmp.append(name);
			if (node.getSplittingAttrType() == Const.NUMERIC_ATTR) {
				tmp.append(" <= " + node.getSplittingValues()[0] /*+ "   /if leaf/ --> " + trainArray.attributes[trainArray.getDecAttrIndex()].name + " = " +  trainArray.dictionary .translate2String(node.getNodeClass())*/).append('\n');
			} else {
				tmp.append(" \\in {");
				for (int i=0; i<node.getSplittingValues().length; i++) {
					tmp.append(trainArray.dictionary.toString(node.getSplittingValues()[i]));
					if (i<node.getSplittingValues().length-1)
						tmp.append(",");
					else
						tmp.append("}" /*+ "   /if leaf/ --> " + trainArray.attributes[trainArray.getDecAttrIndex()].name + " = " +  trainArray.dictionary .translate2String(node.getNodeClass())*/).append('\n');
				}
			}
		} else {
			tmp.append("[leaf] -> " + trainArray.attributes[trainArray.getDecAttrIdx()].name + " = " +  trainArray.dictionary .toString(node.getNodeClass())).append('\n');
			/*System.out.println("    (corr#/err#) = (" + (node.getEventsNumber() - node.getErrorsNumber()) + "/" + node.getErrorsNumber() + ")");*/
		}

		if (node.left != null)
			printNodes(trainArray, node.left,offset+1,"L:   ",tmp);
		if (node.right != null)
			printNodes(trainArray, node.right,offset+1,"R:   ",tmp);
	}
	//***************************************
    public void addInfo(FArray trainArray, Node node, ExperimentIndicators experimentIndicators,AttributesRI attributesImportance, HashSet<String> attrSet)
    {
        if(!node.leafIndicator)
        {
            SliqNodeIndicators nodeIndicators=new SliqNodeIndicators();
            nodeIndicators.attrEventsNumber=node.eventsNumber;
            nodeIndicators.diversityMeasure=node.diversityMeasure;
            nodeIndicators.goodnessOfSplit=node.goodnessOfSplit;

            //DEBUG
            //System.out.println("### attr index "+node.getSplittingAttrIndex()+" "+nodeIndicators.toString());
            //System.out.println(node.toString()+'\n');

            int idx=node.getSplittingAttrIndex();
            String attrName=trainArray.attributes[idx].name;
            attributesImportance.addImportances(attrName,experimentIndicators,nodeIndicators);
            attrSet.add(attrName);
            if (node.left != null)
                addInfo(trainArray,node.left,experimentIndicators,attributesImportance,attrSet);
            if (node.right != null)
                addInfo(trainArray,node.right,experimentIndicators,attributesImportance,attrSet);
        }
    }
    //***************************************

}
