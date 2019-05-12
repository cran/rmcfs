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
package dmLab.classifier.sliq.Tree;

import java.util.Arrays;

import dmLab.classifier.attributeIndicators.SliqNodeIndicators;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.ExperimentIndicators;

public abstract class Node
{	
	protected int splittingAttrIndex;
	protected boolean splittingAttrType;
	protected float[] splittingValues;
	
	protected float diversityMeasure;
	protected float goodnessOfSplit;
	
	protected boolean leafIndicator;
	
	protected int eventsNumber;
	protected int errorsNumber;
	protected float nodeClass;
	protected float nodeClassFrequency;
	
	public Node left;
	public Node right;
	//*************************************
	public Node()
	{
		left = null;
		right = null;
		splittingAttrIndex = Const.NO_SPLITTING_ATTR_INDEX;
		splittingAttrType = Const.NO_ATTR_TYPE;
		splittingValues = null;
		goodnessOfSplit = Const.NO_GOODNESS_OF_SPLIT;
		diversityMeasure = Const.NO_DIVERSITY_MEASURRE;
		nodeClass = Const.NO_NODE_CLASS;
		nodeClassFrequency = Const.NO_NODE_CLASS_FREQUENCY;
		leafIndicator = Const.NODE;
		eventsNumber = 0;
		errorsNumber = 0;
	}
	//*****************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();		
		tmp.append("attr: "+splittingAttrIndex+" ");
		tmp.append("type: "+splittingAttrType+" ");
		tmp.append("values: "+Arrays.toString(splittingValues)+" ");
		tmp.append("leaf: "+leafIndicator+" ");
		if(left==null)
			tmp.append("left: null ");
		else
			tmp.append("left: "+left.splittingAttrIndex+" ");
		if(right==null)
			tmp.append("right: null ");
		else
			tmp.append("right: "+right.splittingAttrIndex+" ");
		
		return tmp.toString();
	}
    //****************************
    public void addInfo(AttributesRI attributesImportance, ExperimentIndicators experimentIndicators)
    {
        if(!leafIndicator)
        {
            SliqNodeIndicators nodeIndicators=new SliqNodeIndicators();
            nodeIndicators.attrEventsNumber=eventsNumber;
            nodeIndicators.diversityMeasure=diversityMeasure;
            nodeIndicators.goodnessOfSplit=goodnessOfSplit;
                        
            //DEBUG
            //System.out.println("### attr index "+splittingAttrIndex+" "+nodeIndicators.toString()+'\n');
            //System.out.println(this.toString());

            attributesImportance.addImportances(splittingAttrIndex,experimentIndicators,nodeIndicators);
            if (left != null)
                left.addInfo(attributesImportance,experimentIndicators);
            if (right != null)
                right.addInfo(attributesImportance,experimentIndicators);
        }
    }
	//****************************		
	public int getSplittingAttrIndex()
	{
		return splittingAttrIndex;
	}
	//****************************		
	public boolean getSplittingAttrType() {
		return splittingAttrType;
	}
	//****************************	
	public float[] getSplittingValues() {
		return splittingValues;
	}
	//****************************	
	public float getGoodnessOfSplit() {
		return goodnessOfSplit;
	}
	//****************************	
	public float getDiversityMeasure() {
		return diversityMeasure;
	}
	//****************************	
	public boolean getLeafIndicator() {
		return leafIndicator;
	}
	//****************************	
	public int getEventsNumber() {
		return eventsNumber;
	}
	//****************************	
	public int getErrorsNumber() {
		return errorsNumber;
	}
	//****************************	
	public float getNodeClass() {
		return nodeClass;
	}
	//****************************	
	public float getNodeClassFrequency() {
		return nodeClassFrequency;
	}
	//****************************	
	public void setSplittingAttrIndex(int splittingAttrIndex) {
		this.splittingAttrIndex = splittingAttrIndex;
	}
	//****************************	
	public void setSplittingAttrType(boolean splittingAttrType) {
		this.splittingAttrType = splittingAttrType;
	}
	//****************************	
	public void setSplittingValues(float[] splittingValues) {
		this.splittingValues = splittingValues;
	}
	//****************************	
	public void setGoodnessOfSplit(float goodnessOfSplit) {
		this.goodnessOfSplit = goodnessOfSplit;
	}
	//****************************	
	public void setDiversityMeasure(float diversityMeasure) {
		this.diversityMeasure = diversityMeasure;
	}
	//****************************	
	public void setNodeClass(float nodeClass) {
		this.nodeClass = nodeClass;
	}
	//****************************	
	public void setNodeClassFrequency(float nodeClassFrequency) {
		this.nodeClassFrequency = nodeClassFrequency;
	}
	//****************************	
	public void setLeafIndicator(boolean leafIndicator) {
		this.leafIndicator = leafIndicator;
	}
	//****************************	
	public void setEventsNumber(int eventsNumber) {
		this.eventsNumber = eventsNumber;
	}
	//****************************	
	public void setErrorsNumber(int errorsNumber) {
		this.errorsNumber = errorsNumber;
	}
	//****************************	
	public void incEventsNumber(int eventsNumber) {
		this.eventsNumber += eventsNumber;
	}
	//****************************	
}
