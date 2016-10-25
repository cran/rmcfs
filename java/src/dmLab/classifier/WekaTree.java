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
package dmLab.classifier;

import java.util.HashSet;

import dmLab.array.functions.ExtFunctions;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.ExperimentIndicators;
import dmLab.mcfs.tree.Tree;
import dmLab.mcfs.tree.TreeNode;
import dmLab.utils.list.IntegerList;


public abstract class WekaTree extends WekaClassifier
{
	protected Tree tree;
	//  ****************************************
	@Override
	public void beforeTrain(){	
		tree = null;
	}
	//  ****************************************
	public abstract void setPrintNodeIndicators(boolean print);
	//  ****************************************
	public abstract Tree parseWekaTree(String treeString);	
	//  ****************************************
	@Override
	public boolean add_RI(AttributesRI importances[])
	{                
		attrSet = new HashSet<String>();
		setPrintNodeIndicators(true);
		IntegerList nodesId = new IntegerList();

		if(tree == null)
			tree = parseWekaTree(wekaClassifier.toString());
		else
			tree.initNodesIterating();
				
		TreeNode node;
		ExperimentIndicators experimentIndicators = new ExperimentIndicators();
		experimentIndicators.eventsNumber = trainSetSize;
		experimentIndicators.predictionQuality = Math.abs(predResult.getPredQuality());

		while((node=tree.getNextNode())!=null){
			/*
			if(Float.isNaN(node.nodeIndicators.attrEventsNumber) || node.nodeIndicators.attrEventsNumber==0){
				System.out.println("@@@@@ attrEventsNumber is NAN or zero!!!!");
				System.out.println(wekaClassifier.toString());
			}
			//*/

			//check if node have been already added
			if(!nodesId.contains(node.nodeIndicators.nodeIndex)){
				/*
                // *************** DEBUG ********************
                System.err.println("##########   TREE ####### ");
                System.err.println(j48String);
                System.err.println("##########   TREE NODE ####### ");
                System.err.println(node.toString());
                System.err.println(node.condition.attributeName);
                System.err.println(node.nodeIndicators);                                        
                //*/
				importances[0].addImportances(node.condition.attributeName, experimentIndicators, node.nodeIndicators);
				nodesId.add(node.nodeIndicators.nodeIndex);
				attrSet.add(node.condition.attributeName);
			}
		}

		for(int i=0;i<importances.length;i++)
			importances[i].flushMeasures();

		return true;
	}
	//  ****************************************************
	@Override
	public boolean add_ID(AttributesID connectionsLight, MCFSParams params)
	{
		int maxConnectionDepth = params.maxConnectionDepth;
		setPrintNodeIndicators(true);
		if(tree == null)
			tree = parseWekaTree(wekaClassifier.toString());
		else
			tree.initNodesIterating();

		HashSet<String> existingConnection=new HashSet<String>();
		TreeNode node;

		/*
		//*************** DEBUG ********************
        System.out.println("addConnections2");
        System.out.println("******** TREE WEKA ***********");
        System.out.println(wekaClassifier.toString());
        System.out.println("******** TREE dmLab ***********");
        System.out.println(tree.toString());
        System.out.println("******** TREE END ***********");
		//*/
		while((node=tree.getNextNode())!=null){
			//*************** DEBUG ********************
			//System.out.println("******** NODE ***********");
			//System.out.println("Node: id "+ node.getNodeID()+" lvl "+node.getLevel()+" name "+node.condition.attributeName);
			//System.out.println("******** NODE ***********");
			//System.out.println("node.isLeaf(): "+node.isLeaf());
			//System.out.println("node.isRoot(): "+node.isRoot());

			TreeNode parentTmp = node.getParent();
			for(int i=0; i<maxConnectionDepth; i++)
			{            		
				if(parentTmp.isRoot())
					break;

				float sizeValue = (float)node.nodeIndicators.attrEventsNumber/(float)parentTmp.nodeIndicators.attrEventsNumber;
				/*
				System.out.println("******** CURR NODE ***********");
				System.out.println(node.printNode());
				System.out.println("******** PARENT ***********");
				System.out.println(parentTmp.printNode());
				System.out.println("******** VAL ***********");
				System.out.println(sizeValue);
				System.out.println(node.nodeIndicators.attrGainRatio);
				System.out.println("******** VAL ***********");
				//*/            		
				float connValue = sizeValue * node.nodeIndicators.attrGainRatio;
				//*************** DEBUG ********************
				/*
				if(Float.isNaN(connValue) || connValue==0){
					System.out.println("@@@@@ connValue is NAN!!!! sizeValue:"+sizeValue+" attrGainRatio: "+node.nodeIndicators.attrGainRatio);
					System.out.println(wekaClassifier.toString());
				}
				//*/

				String connStr = ""+parentTmp.nodeIndicators.nodeIndex+"-"+node.nodeIndicators.nodeIndex;                    
				//System.out.println(Arrays.toString(addedConnections.toArray()));
				boolean addConnection = !existingConnection.contains(connStr) &&
						!ExtFunctions.isContrastAttribute(parentTmp.condition.attributeName) &&
						!ExtFunctions.isContrastAttribute(node.condition.attributeName);                    

				//System.out.println("connStr: "+connStr);
				//System.out.println("addConnection: "+addConnection);
				if(addConnection)
				{                    	
					/*
					System.out.println("**********************");
					System.out.println("connStr: "+connStr);
					System.out.println("existingConnection: "+existingConnection.contains(connStr));
					System.out.println(parentTmp.condition.attributeName+" -> " + node.condition.attributeName);       	
					//*/

					//System.out.println("ADD: "+connStr);
					existingConnection.add(connStr);	                    
					connectionsLight.addDependency(parentTmp.condition.attributeName, node.condition.attributeName, connValue);
				}
				parentTmp = parentTmp.getParent();                   	
			}            	            
		}
		return true;
	}
	//  ****************************************************

}
