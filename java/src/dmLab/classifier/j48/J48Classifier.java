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
package dmLab.classifier.j48;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import weka.classifiers.trees.J48;
import weka.core.converters.ArffLoader;
import dmLab.array.functions.ExtFunctions;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.saver.Array2File;
import dmLab.classifier.WekaClassifier;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.ExperimentIndicators;
import dmLab.mcfs.tree.Tree;
import dmLab.mcfs.tree.TreeNode;
import dmLab.mcfs.tree.parser.J48Parser;
import dmLab.utils.cmatrix.AccuracyMeasure;
import dmLab.utils.list.IntegerList;


public class J48Classifier extends WekaClassifier
{
    private J48Params cfg; 	
//  ****************************************************
    public J48Classifier()
    {
        super();
        wekaClassifier = new J48();               
        arffLoader = new ArffLoader();
        array2File = new Array2File();
        array2File.setFormat(FileType.ARFF);
        label=labels[J48];
        type=J48;
        params=new J48Params();
        cfg=(J48Params)params;
    }    
//  ****************************************************
    @Override
    public boolean saveDefinition(String path,String name) throws IOException
    {
        if(params.verbose) System.out.print("Saving classifier definition...");
        params.save(path,name);
        //this method saves only visualization of the decision tree
        //there is no method to load such visualization from the file
        FileWriter writer=null;
        writer = new FileWriter(path+"//"+name+".tree");
        writer.write(toString());
        writer.close();
        if(params.verbose) System.out.println(" Done!");
        return true;
    }
//  *****************************************
    @Override
    public boolean loadDefinition(String path,String name)
    {
        if(params.verbose) System.out.print("Loading classifier definition...");
        //load classifier parameters
        params.load(path,name);
        //load classifier definition e.g. rules, tree structure etc.
        //this classifier has not such sophisticated definition
        System.out.println("Loading of definition is not implemented!");
        //j48Tree.load(path+"//"+label);      
        if(params.verbose) System.out.println("Done!");
        return true;
    }
    //************************************** 
    @Override
    public boolean init()
    {
        wekaClassifier = new J48();
        setParams();
        return true;
    }
//  ****************************************
    @Override
    public boolean addImportances(AttributesRI importances[])
    {                
        attrSet=new HashSet<String>();
        ((J48)wekaClassifier).setPrintNodeIndicators(true);
        IntegerList nodesId=new IntegerList();                 
        String j48String=wekaClassifier.toString();        
        J48Parser j48Parser=new J48Parser(j48String);
        TreeNode node;

        ExperimentIndicators experimentIndicators=new ExperimentIndicators();
        experimentIndicators.eventsNumber=trainSetSize;
        experimentIndicators.predictionQuality=AccuracyMeasure.calcWAcc(confusionMatrix.getMatrix());

        while((node=j48Parser.getNextNode())!=null)
        {
            //check if node have been already added
            if(!nodesId.contains(node.nodeIndicators.nodeIndex))
            {
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
            j48Parser.flush(); 
        }
        
        for(int i=0;i<importances.length;i++)
            importances[i].flushMeasures();
        
        return true;
    }
//  ****************************************************
    @Override
    public boolean addIDependencies(AttributesID connectionsLight, MCFSParams params)
    {
        int maxConnectionDepth=params.maxConnectionDepth;
    	
    	String j48String=wekaClassifier.toString();
        J48Parser j48Parser=new J48Parser(j48String);
        Tree tree=new Tree();
        tree.init();        
        tree.getRootNode().parseTree(j48Parser);
        tree.finalize(); 
        tree.initNodesIterating();
        HashSet<String> existingConnection=new HashSet<String>();
        /*
		//*************** DEBUG ********************
        System.out.println("addConnections2");
        System.out.println("******** TREE ***********");
        System.out.println(tree.toString());
        System.out.println("******** TREE ***********");
        */
        while(tree.hasNextNode())
        {
            TreeNode node = tree.getNextNode();            
            //*************** DEBUG ********************
            //System.out.println("******** NODE ***********");
            //System.out.println("Node: id "+ node.getNodeID()+" lvl "+node.getLevel()+" name "+node.condition.attributeName);
            //System.out.println("******** NODE ***********");
    		//System.out.println("node.isLeaf(): "+node.isLeaf());
    		//System.out.println("node.isRoot(): "+node.isRoot());

            if(!node.isRoot())
            {   
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
                    */            		
                    float connValue = sizeValue * node.nodeIndicators.attrGainRatio;
                    
                    String connStr = ""+parentTmp.nodeIndicators.nodeIndex+"-"+node.nodeIndicators.nodeIndex;                    
                    //System.out.println(Arrays.toString(addedConnections.toArray()));
                    boolean addConnection = !existingConnection.contains(connStr) &&
                    		!ExtFunctions.isContrastAttribute(parentTmp.condition.attributeName) &&
                    		!ExtFunctions.isContrastAttribute(node.condition.attributeName);                    
                    
                    //System.out.println("connStr: "+connStr);
                    //System.out.println("addConnection: "+addConnection);
                    if(addConnection)
                    {                    	
                    	/*System.out.println("**********************");
                    	System.out.println("connStr: "+connStr);
                    	System.out.println("existingConnection: "+existingConnection.contains(connStr));
                    	System.out.println(parentTmp.condition.attributeName+" -> " + node.condition.attributeName);       	
						*/
                    	
                    	//System.out.println("ADD: "+connStr);
	                    existingConnection.add(connStr);	                    
	                    connectionsLight.addDependency(parentTmp.condition.attributeName, node.condition.attributeName,connValue);
                    }
                   	parentTmp = parentTmp.getParent();                   	
            	}            	            
            }
        }
        return true;
    }    
//****************************************************
    @Override
    protected void setParams()
    {
        if(wekaClassifier!=null)
        {
            //J48 Parameters
            ((J48)wekaClassifier).setBinarySplits(cfg.binarySplits);
            ((J48)wekaClassifier).setMinNumObj(cfg.minNumObj);
            ((J48)wekaClassifier).setSaveInstanceData(cfg.saveInstanceData);
            //The confidence factor used for pruning (smaller values incur more pruning
            ((J48)wekaClassifier).setConfidenceFactor(cfg.confidenceFactor);
            //Whether reduced-error pruning is used instead of C.4.5 pruning
            ((J48)wekaClassifier).setReducedErrorPruning(cfg.reducedErrorPruning);
            //Whether to consider the subtree raising operation when pruning
            ((J48)wekaClassifier).setSubtreeRaising(cfg.subtreeRaising);
            /* Determines the amount of data used for reduced-error pruning
			 One fold is used for pruning, the rest for growing the tree */
            ((J48)wekaClassifier).setNumFolds(cfg.numFolds);
            ((J48)wekaClassifier).setUnpruned(cfg.unpruned);
            //Whether counts at leaves are smoothed based on Laplace
            ((J48)wekaClassifier).setUseLaplace(cfg.useLaplace);
        }
    }
//**************************************
}
