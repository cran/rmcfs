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

import dmLab.classifier.attributeIndicators.AttributeIndicators;
import dmLab.classifier.attributeIndicators.J48NodeIndicators;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.ExperimentIndicators;

public class J48RIMeasure extends ImportanceMeasure
{           
    //*********************************
    public J48RIMeasure(MCFSParams cfg)
    {        
        super(cfg);
        name=MEASURE_RI;
    }
    //*********************************
    @Override
    public double calcAttrImportance(ExperimentIndicators experimentIndicators, AttributeIndicators indicators)
    {                
    	J48NodeIndicators ind = (J48NodeIndicators)indicators;
        double part=(double)ind.attrEventsNumber/(double)experimentIndicators.eventsNumber;
        
        if(cfg.useGainRatio)
        	return Math.pow(experimentIndicators.predictionQuality,cfg.u) * ind.attrGainRatio * Math.pow(part,cfg.v);            
        else
        	return Math.pow(experimentIndicators.predictionQuality,cfg.u) * ind.attrInfoGain * Math.pow(part,cfg.v);
    }
    //***********************************
    @Override
    public double calcAttrImportance()
    {
        return 0;
    }
    //********************************* 
    @Override
    public void flush()
    {
    }
    //*********************************
}
