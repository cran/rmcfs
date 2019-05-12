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
package dmLab.classifier.j48;

import java.io.FileWriter;
import java.io.IOException;

import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.saver.Array2File;
import dmLab.classifier.WekaTree;
import dmLab.mcfs.tree.Tree;
import dmLab.mcfs.tree.parser.J48Parser;
import weka.classifiers.trees.J48;
import weka.core.converters.ArffLoader;


public class J48Classifier extends WekaTree
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
		model=J48;
		params=new J48Params();
		cfg=(J48Params)params;

		beforeTrain();
	}    
	//  ****************************************************
	@Override
	public boolean saveDefinition(String path,String name) throws IOException
	{
		//System.out.print("Saving classifier definition...");
		params.save(path,name);
		//this method saves only visualization of the decision tree
		//there is no method to load such visualization from the file
		FileWriter writer=null;
		writer = new FileWriter(path+"//"+name+".tree");
		writer.write(toString());
		writer.close();
		return true;
	}
	//  *****************************************
	@Override
	public boolean loadDefinition(String path,String name)
	{
		//System.out.print("Loading classifier definition...");
		//load classifier parameters
		params.load(path,name);
		//load classifier definition e.g. rules, tree structure etc.
		//this classifier has not such sophisticated definition
		System.out.println("Loading of definition is not implemented!");
		//j48Tree.load(path+"//"+label);      
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
	public void setPrintNodeIndicators(boolean print){
		((J48)wekaClassifier).setPrintNodeIndicators(print);
	}
	//  ****************************************
	@Override
	public Tree parseWekaTree(String treeString){		
		return new Tree(new J48Parser(wekaClassifier.toString()));
	}
	//  ****************************************
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
