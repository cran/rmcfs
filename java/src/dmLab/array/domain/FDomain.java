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

import java.util.Arrays;
import java.util.HashSet;

import dmLab.utils.ArrayUtils;



public class FDomain extends Domain implements Cloneable
{
	protected float domainValues[];

	//********************************	
	public FDomain()
	{		
	}
	//	********************************
	@Override
	public boolean isCreated()
	{
		if(domainValues==null)
			return false;
		else
			return true;
	}
	//	********************************
	public boolean createDomain(float[] column)
	{
		HashSet<Float> set = new HashSet<Float>();		
		for(int i=0;i<column.length;i++)
			set.add(column[i]);

		Float[] floatValues = new Float[1];
		floatValues = set.toArray(floatValues);
		domainValues = ArrayUtils.float2Float(floatValues); 
		return true;
	}
	//	*******************************************************
	@Override
	public void setDomainValues(float[] values)
	{
		domainValues = values.clone();
	}
	//	*******************************************************
	@Override
	public float[] getDomainValues()
	{
		return domainValues;
	}
	//  ********************************
	@Override
	public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append(Arrays.toString(domainValues));
		return tmp.toString();
	}
	//	***********************************
	@Override
	public int size()
	{
		return 	domainValues.length;
	}
	//	*******************************************************
	@Override
	public FDomain clone(){
		FDomain domain = new FDomain();
		domain.domainValues = domainValues.clone();        
		return domain;
	}
	//	*******************************************************	

}
