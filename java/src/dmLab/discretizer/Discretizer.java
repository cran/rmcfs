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
package dmLab.discretizer;

import java.util.ArrayList;

public abstract class Discretizer
{
	protected ArrayList<Float> ranges;

	public static final int EQUAL_RANGES=1;
	public static final int EQUAL_FREQUENCY=2;
	public static final int CHI2=3;
	public static final int CHANGE=4;
	public static final int FAYYAD_IRANI=5;
	//************************************************
	public Discretizer()
	{      
		ranges = null;
	}    
	//************************************************
	public float[] getRanges()
	{
		if(ranges == null)
			return null;

		float[] floatRanges = new float[ranges.size()];
		for(int i=0; i<floatRanges.length; i++)
			floatRanges[i] = ranges.get(i).floatValue();
		return floatRanges;
	}
	//************************************************
	public abstract float[] findRanges(float[] values, float[] decision, DiscretizerParams discParams);
	//************************************************        
}
