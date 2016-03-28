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
package dmLab.classifier.attributeIndicators;

import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.selector.Selector;

public class ADXSelectorIndicators extends AttributeIndicators
{
	public float posCoverage;
	public float negCoverage;
	public float coverage;

	public float complexPosCoverage;
	public float complexNegCoverage;
	public float complexCoverage;
	//*********************************************	
	public ADXSelectorIndicators()
	{
        //number of indicators
        size=6;
        
        posCoverage=0;
		negCoverage=0;
		coverage=0;
	}
	//*********************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer(); 
		tmp.append("### SelectorIndicators ###").append('\n');
		tmp.append(" posCoverage: "+posCoverage);
		tmp.append(" negCoverage: "+negCoverage);
		tmp.append(" coverage: "+coverage).append('\n');
		tmp.append(" complexPosCoverage: "+complexPosCoverage);
		tmp.append(" complexNegCoverage: "+complexNegCoverage);
		tmp.append(" complexCoverage: "+complexCoverage);	
		return tmp.toString();
	}
	//*********************************************
	public boolean setIndicators(Complex complex)
	{
		complexPosCoverage=complex.posCoverage;
		complexNegCoverage=complex.negCoverage;
		complexCoverage=complex.coverage;		
		return true;
	}
	//*********************************************
	public boolean setIndicators(Selector selector)
	{
		posCoverage=selector.posCoverage;
		negCoverage=selector.negCoverage;
		coverage=selector.coverage;
		
		return true;
	}
	//**********************************************
}
