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
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.ExperimentIndicators;

public class RINormMeasure extends ImportanceMeasure 
{
  //***********************************
    public RINormMeasure(MCFSParams cfg)
    {
        super(cfg);
        name = MEASURE_RI;
    }
  //***********************************
    @Override
    public double calcAttrImportance(ExperimentIndicators experimentIndicators,AttributeIndicators indicators) {
        return 0;
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
    }
  //***********************************
}
