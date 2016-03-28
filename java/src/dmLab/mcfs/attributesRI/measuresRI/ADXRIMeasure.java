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

import dmLab.classifier.adx.complex.Quality;
import dmLab.classifier.attributeIndicators.ADXSelectorIndicators;
import dmLab.classifier.attributeIndicators.AttributeIndicators;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.ExperimentIndicators;

public class ADXRIMeasure extends ImportanceMeasure
{
    //*********************************
    public ADXRIMeasure(MCFSParams cfg)
    {
        super(cfg);
        name = MEASURE_RI;
    }
    //*********************************
    @Override
    public double calcAttrImportance(ExperimentIndicators experimentIndicators,AttributeIndicators indicators)
    {
        ADXSelectorIndicators ind=(ADXSelectorIndicators) indicators;
        float selectorQuality=(float) (Quality.calc(ind.posCoverage,ind.negCoverage,cfg.qMethod)*ind.coverage);
        float complexQuality=(float) (Quality.calc(ind.complexPosCoverage,ind.complexNegCoverage,cfg.qMethod)*ind.complexCoverage);
        
        double part=(double)ind.complexCoverage/(double)experimentIndicators.eventsNumber;        
        
        if(cfg.useComplexQuality)
            return (float)Math.pow(experimentIndicators.predictionQuality,cfg.u) * selectorQuality * complexQuality * Math.pow(part,cfg.v);
        else
            return (float)Math.pow(experimentIndicators.predictionQuality,cfg.u) * selectorQuality * Math.pow(part,cfg.v); 
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
