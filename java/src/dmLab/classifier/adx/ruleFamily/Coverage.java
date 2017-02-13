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
package dmLab.classifier.adx.ruleFamily;

import dmLab.utils.GeneralUtils;

public class Coverage
{	
	public float coverage;
	public float posCoverage;
	public float negCoverage;
	public int precision=3; 
    //******************************	
	public Coverage()
	{
	    clean();
	}
    //******************************
    public void clean()
    {
        coverage=0;
        posCoverage=0;
        negCoverage=0;  
    }
	//******************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append(" Cov: ").append(GeneralUtils.formatFloat(coverage,precision));
		tmp.append(" pCov: ").append(GeneralUtils.formatFloat(posCoverage,precision));
		tmp.append(" nCov: ").append(GeneralUtils.formatFloat(negCoverage,precision));		
		return tmp.toString();
	}
	//******************************

}
