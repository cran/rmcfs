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
package dmLab.classifier.m5;

import java.io.IOException;

import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.saver.Array2File;
import dmLab.classifier.PredictionResult;
import dmLab.classifier.WekaTree;
import dmLab.mcfs.tree.Tree;
import dmLab.mcfs.tree.parser.M5Parser;
import weka.classifiers.trees.M5P;
import weka.core.converters.ArffLoader;

public class M5Classifier extends WekaTree 
{
	private M5Params cfg;  
	//  ****************************************************
	public M5Classifier()
	{
		super();
		modelType = MODEL_PREDICTOR;
		predResult = new PredictionResult(modelType);
		wekaClassifier = new M5P();               
		arffLoader = new ArffLoader();
		array2File = new Array2File();
		array2File.setFormat(FileType.ARFF);
		label=labels[M5];
		model=M5;
		params=new M5Params();
		cfg=(M5Params)params;

		beforeTrain();
	}
	//  ****************************************************
	@Override
	public boolean loadDefinition(String path, String name) throws IOException 
	{
		return false;
	}
	//  ****************************************************
	@Override
	public boolean saveDefinition(String path, String name) throws IOException 
	{
		return false;
	}
	//  ****************************************************
	@Override
	public boolean init() 
	{
		wekaClassifier = new M5P();
		setParams();
		return true;
	}       
	//  ****************************************************
	@Override
	public void setPrintNodeIndicators(boolean print){
		((M5P)wekaClassifier).setPrintNodeIndicators(print);
	}
	//  ****************************************
	@Override
	public Tree parseWekaTree(String treeString){		
		return new Tree(new M5Parser(wekaClassifier.toString()));
	}
	//  ****************************************************
	@Override
	protected void setParams() 
	{
		if(wekaClassifier!=null)
		{
			((M5P)wekaClassifier).setBuildRegressionTree(cfg.rTree);
			((M5P)wekaClassifier).setMinNumInstances(cfg.minNumObj);
			((M5P)wekaClassifier).setUnpruned(cfg.unpruned);            
			((M5P)wekaClassifier).setUseUnsmoothed(cfg.unsmoothed);
		}
	}
	//  ****************************************
}
