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
package dmLab.mcfs.attributesRI.measuresRI;

import dmLab.classifier.attributeIndicators.AttributeIndicators;
import dmLab.classifier.attributeIndicators.SliqNodeIndicators;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.ExperimentIndicators;

public class SliqRIMeasure extends ImportanceMeasure
{           
    //*********************************
    public SliqRIMeasure(MCFSParams cfg)
    {        
        super(cfg);
        name=MEASURE_RI_ROUGH;
    }
    //*********************************
    @Override
    public double calcAttrImportance(ExperimentIndicators experimentIndicators, AttributeIndicators indicators)
    {        
        SliqNodeIndicators ind = (SliqNodeIndicators)indicators;
        
        double part=(double)ind.attrEventsNumber/(double)experimentIndicators.eventsNumber;
        if(cfg.useDiversityMeasure)
            return Math.pow(experimentIndicators.predictionQuality,cfg.u) * ind.diversityMeasure * Math.pow(part,cfg.v);
        else
            return Math.pow(experimentIndicators.predictionQuality,cfg.u) * ind.goodnessOfSplit * Math.pow(part,cfg.v);        
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
