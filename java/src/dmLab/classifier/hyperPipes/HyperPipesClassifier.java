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
package dmLab.classifier.hyperPipes;

import java.io.IOException;

import weka.classifiers.misc.HyperPipes;
import weka.core.converters.ArffLoader;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.saver.Array2File;
import dmLab.classifier.WekaClassifier;
import dmLab.mcfs.attributesRI.AttributesRI;

public class HyperPipesClassifier extends WekaClassifier {
    
	@SuppressWarnings("unused")
	private HyperPipesParams cfg;
//  ****************************************************
    public HyperPipesClassifier()
    {
        super();
        wekaClassifier = new HyperPipes();               
        arffLoader = new ArffLoader();
        array2File = new Array2File();
        array2File.setFormat(FileType.ARFF);
        label=labels[HP];
        type=HP;
        params=new HyperPipesParams();
        cfg=(HyperPipesParams)params;
    }
//  ****************************************************
    @Override
    protected void setParams() 
    {
        if(wekaClassifier!=null)
        {
            //((HyperPipes)wekaClassifier).set.setKNN(cfg.knn);
        }
    }
//  ****************************************************
    @Override
    public boolean init() 
    {
        wekaClassifier = new HyperPipes();
        setParams();
        return true;
    }    
//  ****************************************************
    @Override
    public boolean addImportances(AttributesRI[] importances) {
        return false;
    }
//  ****************************************************
    @Override
    public boolean loadDefinition(String path, String name) throws IOException {
        return false;
    }
//  ****************************************************
    @Override
    public boolean saveDefinition(String path, String name) throws IOException {
        return false;
    }
//  ****************************************************
}
