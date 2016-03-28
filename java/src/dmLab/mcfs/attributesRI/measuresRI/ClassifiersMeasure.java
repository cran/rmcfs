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
package dmLab.mcfs.attributesRI.measuresRI;

import java.util.HashSet;

import dmLab.classifier.attributeIndicators.AttributeIndicators;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.ExperimentIndicators;

public class ClassifiersMeasure extends ImportanceMeasure
{
    private HashSet<String> attrUsed;  

	//this measure adds 1 for each classifier that was based on the attribute
    //***********************************    
    public ClassifiersMeasure(MCFSParams cfg)
    {
        super(cfg);
        name=MEASURE_CLASSIFIERS;
        attrUsed=new HashSet<String>();
    }
    //***********************************
    @Override
    public double calcAttrImportance(ExperimentIndicators experimentIndicators, AttributeIndicators indicators)
    {
        int importance=1;
        if(attrUsed.contains(indicators.attributeName))
            importance=0;
        else
           attrUsed.add(indicators.attributeName);
        
        return importance;
    }
    //***********************************
    @Override
    public double calcAttrImportance()
    {
        return 0;
    }
    //***********************************
    @Override
    public void flush()
    {
        attrUsed.clear();
    }
    //*********************************
}
