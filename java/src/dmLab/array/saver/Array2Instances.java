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
/*
 * Created on 2005-02-15
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package dmLab.array.saver;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import dmLab.array.FArray;


public class Array2Instances
{
	public Array2Instances()
	{
		super();
	}
	//***************************************
	public static Instances convert(FArray array)
	{
		if(!array.domainsCreated()){
			System.err.println("Cannot convert to Instances. Domains of input FArray are not determined!");
			return null;
		}

		FastVector attInfo = new FastVector();
		final int rows = array.rowsNumber();
		for(int i=0; i<array.attributes.length; i++){
			if (array.attributes[i].type==dmLab.array.meta.Attribute.NUMERIC)
				attInfo.addElement(new Attribute(array.attributes[i].name));
			else if (array.attributes[i].type==dmLab.array.meta.Attribute.NOMINAL){            	
				String[] domainValues = array.getDomainStr(i);
				FastVector vect = new FastVector (domainValues.length);
				for(int v=0; v<domainValues.length; v++){
					vect.addElement(domainValues[v]);
				}
				attInfo.addElement(new Attribute(array.attributes[i].name, vect));
			}
		}
		Instances instances=null;
		instances=new Instances("converted",attInfo,rows);
		for(int j=0;j<rows;j++){
			Instance instance=new Instance(array.attributes.length);
			instance.setDataset(instances);

			for(int i=0;i<array.attributes.length;i++){
				if (array.attributes[i].type==dmLab.array.meta.Attribute.NUMERIC)
					instance.setValue(i, array.readValue(i,j));
				else if (array.attributes[i].type==dmLab.array.meta.Attribute.NOMINAL)
					instance.setValue(i, array.readValueStr(i,j));
			}
			instances.add(instance);
		}
		instances.setClassIndex(array.getDecAttrIdx());
		return instances;
	}
	//***************************************
}
