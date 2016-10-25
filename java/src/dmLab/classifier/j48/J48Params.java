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

import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;

public class J48Params extends Params {

	//J48 Parameters
	public boolean binarySplits;
	public int minNumObj;
	public boolean saveInstanceData;
    //The confidence factor used for pruning (smaller values incur more pruning
	public float confidenceFactor;
    //Whether reduced-error pruning is used instead of C.4.5 pruning
	public boolean reducedErrorPruning;
    //Whether to consider the subtree raising operation when pruning
	public boolean subtreeRaising;
    /* Determines the amount of data used for reduced-error pruning
      One fold is used for pruning, the rest for growing the tree */
	public int numFolds;
	public boolean unpruned;
    //Whether counts at leaves are smoothed based on Laplace
	public boolean useLaplace;
    //*************************************
	public J48Params()
	{
		super();
	}
    //*************************************
	@Override
    public boolean setDefault()
	{
	    binarySplits=false;
	    minNumObj=2;
	    saveInstanceData=false;
	    confidenceFactor=0.25f;
	    reducedErrorPruning=false;
	    subtreeRaising=true;
	    numFolds=3;
	    unpruned=false;
	    useLaplace=false;
	    return true;
	}
    //*************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("###  j48 Classifier Parameters ###").append('\n').append('\n');
        tmp.append("j48.binarySplits="+ binarySplits).append('\n');
		tmp.append("j48.minNumObj="+ minNumObj).append('\n');
		tmp.append("j48.saveInstanceData="+ saveInstanceData).append('\n');
		tmp.append("j48.confidenceFactor="+ confidenceFactor).append('\n');
		tmp.append("j48.reducedErrorPruning="+ reducedErrorPruning).append('\n');
		tmp.append("j48.subtreeRaising="+ subtreeRaising).append('\n');
		tmp.append("j48.numFolds="+ numFolds).append('\n');
		tmp.append("j48.unpruned="+ unpruned).append('\n');
		tmp.append("j48.useLaplace="+ useLaplace).append('\n');
		tmp.append(super.toString());
		
		return tmp.toString();
	}
    //*************************************
	@Override
    protected boolean update(Properties properties)
	{
	    binarySplits=Boolean.valueOf(properties.getProperty("j48.binarySplits", "false")).booleanValue();;
	    minNumObj=Integer.valueOf(properties.getProperty("j48.minNumObj", "1")).intValue();
	    saveInstanceData=Boolean.valueOf(properties.getProperty("j48.saveInstanceData", "false")).booleanValue();
	    confidenceFactor=Float.valueOf(properties.getProperty("j48.confidenceFactor", "0.25")).floatValue();
	    reducedErrorPruning=Boolean.valueOf(properties.getProperty("j48.reducedErrorPruning", "false")).booleanValue();
	    subtreeRaising=Boolean.valueOf(properties.getProperty("j48.subtreeRaising", "true")).booleanValue();;
	    numFolds=Integer.valueOf(properties.getProperty("j48.numFolds", "3")).intValue();
	    unpruned=Boolean.valueOf(properties.getProperty("j48.unpruned", "false")).booleanValue();
	    useLaplace=Boolean.valueOf(properties.getProperty("j48.useLaplace", "false")).booleanValue();
		return true;
	}
    //*************************************
	@Override
    public boolean check(FArray array)
	{
		return true;
	}
    //*************************************
}
