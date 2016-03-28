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
package dmLab.array.domain;

public class ADXDomainValue implements Comparable<ADXDomainValue>
{
	protected float value;
	private int posNumber[];
	private int coveredNumber;
	
	//*************************
	public ADXDomainValue(float value, int decisionValues)
	{
		this.value=value;
		posNumber=new int [decisionValues];
		coveredNumber=0;
	}
	//*************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();		
		tmp.append("val: " + value);
		for(int i=0 ;i<posNumber.length;i++)			
			tmp.append("\tp:" + posNumber[i] + " n:" + (coveredNumber-posNumber[i]));
		return tmp.toString();
	}
	//*************************
	public int getPosNumber(int decisionIndex)
	{
		return posNumber[decisionIndex];
	}
	//*************************
	public int getNegNumber(int decisionIndex)
	{
		return coveredNumber-posNumber[decisionIndex];
	}
	//*************************
	public boolean incrementPosNumber(int decisionIndex)
	{
		posNumber[decisionIndex]++;
		coveredNumber++;
		return true;
	}
	//*************************
	public boolean incrementCoveredNumber()
	{
		coveredNumber++;
		return true;
	}
	//*************************
	public int coveredEvents()
	{
		return coveredNumber;
	}
	//*************************
	public int compareTo(ADXDomainValue domainValue)
	{		
		if(value > domainValue.value)
			return 1;
		else if(value < domainValue.value)			
			return -1;
		else
			return 0;
	}
	//*************************
}
