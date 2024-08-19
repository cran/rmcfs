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
package dmLab.classifier.randomForest;

import java.io.IOException;

import weka.classifiers.trees.RandomForest;
import weka.core.converters.ArffLoader;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.saver.Array2File;
import dmLab.classifier.WekaClassifier;
import dmLab.mcfs.attributesRI.AttributesRI;

public class RandomForestClassifier extends WekaClassifier {

    private RandomForestParams cfg;
//  ****************************************************
    public RandomForestClassifier()
    {
        super();
        wekaClassifier = new RandomForest();               
        arffLoader = new ArffLoader();
        array2File = new Array2File();
        array2File.setFormat(FileType.ARFF);
        label=labels[RF];
        model=RF;
        params=new RandomForestParams();
        cfg=(RandomForestParams)params;
    }
//  ****************************************************
    @Override
    public boolean init() 
    {
        wekaClassifier = new RandomForest();
        setParams();
        return true;
    }
//  ***************************************************
    @Override
    protected void setParams()
    {
        if(wekaClassifier!=null)
        {
            ((RandomForest)wekaClassifier).setNumTrees(cfg.numTrees);
            ((RandomForest)wekaClassifier).setNumFeatures(cfg.numFeatures);
            ((RandomForest)wekaClassifier).setSeed(cfg.randomSeed);            
        }
    }
//  ****************************************************
    @Override
    public boolean add_RI(AttributesRI[] importances) 
    {
        return false;
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
}
