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

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

public class RandomForestParams extends Params {

    protected int numTrees = 10;
    protected int numFeatures = 0;
    protected int randomSeed = 1;  

//  ****************************************************
    @Override
    public boolean check(FArray array) 
    {    
        return true;
    }
//  ****************************************************
    @Override
    public boolean setDefault() 
    {
        numTrees = 10;
        numFeatures = 0;
        randomSeed = 1;
	    return true;
    }
//  ****************************************************
    @Override
    public String toString() 
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("###  Random Forest Classifier Parameters ###").append('\n').append('\n');
        tmp.append("rf.binarySplits="+ numTrees).append('\n');
        tmp.append("rf.minNumObj="+ numFeatures).append('\n');
        tmp.append("rf.saveInstanceData="+ randomSeed).append('\n');
		tmp.append(super.toString());
		
		return tmp.toString();
    }
//  ****************************************************
    @Override
    protected boolean update(Properties properties) 
    {
        numTrees=Integer.valueOf(properties.getProperty("rf.numTrees", "10")).intValue();;
        numFeatures=Integer.valueOf(properties.getProperty("rf.numFeatures", "0")).intValue();
        randomSeed=Integer.valueOf(properties.getProperty("rf.randomSeed", "1")).intValue();
        return true;
    }
//  ****************************************************
}
