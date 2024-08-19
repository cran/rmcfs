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
package dmLab.classifier.sliq;

import dmLab.classifier.sliq.Tree.Const;
import dmLab.classifier.sliq.Tree.Node;




//**********************************************************
class SliqNode extends Node
{
	
	public NumericHistogram numericHistogram;
	public NominalHistogram nominalHistogram;
	//public int eventsNumber;
	public int tmpSplittingValueProxyIndex;
	public int tmpSplittingValueProxyIndexEval;
	public float tmpGoodnessOfSplit;
	public int attrListSplittingAttrIndex;
	//public boolean purity;
	
	public int[] classFrequencies;
	//****************************
	public SliqNode(int classProxiesNumber)
	{
		numericHistogram = new NumericHistogram(classProxiesNumber);
		eventsNumber = Const.NO_EVENTS;
		attrListSplittingAttrIndex = Const.NO_SPLITTING_ATTR_INDEX;
		
		classFrequencies = new int[classProxiesNumber];
		for (int i=0; i<classProxiesNumber; i++)		
			classFrequencies[i] = 0;
				
		tmpInit();
	}
	//****************************
	public void tmpInit()
	{
		tmpSplittingValueProxyIndex = Const.NO_SPLITTING_VALUE_PROXY_INDEX;
		tmpSplittingValueProxyIndexEval = Const.NO_SPLITTING_VALUE_PROXY_INDEX;
		tmpGoodnessOfSplit = Const.NO_GOODNESS_OF_SPLIT;
	}
	//****************************
	
}
