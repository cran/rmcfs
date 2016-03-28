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
package dmLab.classifier.sliq;

import java.util.ArrayList;

import dmLab.array.FArray;
import dmLab.classifier.sliq.Tree.Const;




//**********************************************************
class ClassList {
	private ClassListEvent[] events;
	private int decAttrIndex;
	private int eventsNumber;
	private ArrayList <ClassProxyEvent>proxies;
	private int proxiesNumber;

	public ClassList(FArray trainArray) {
		decAttrIndex = trainArray.getDecAttrIdx();
		eventsNumber  = trainArray.rowsNumber();
		events = new ClassListEvent[eventsNumber];

		for (int i=0; i<eventsNumber; i++) {
			events[i] = new ClassListEvent();
			events[i].classIndex = i;
			events[i].classLabel = trainArray.readValue(decAttrIndex,i);
		}

		sort();
		proxiesNumber = setProxies();

	}

	public ClassListEvent getEvent(int classIndex) {
		return events[classIndex];
	}

	public int getClassIndex(int classIndex) {
		return events[classIndex].classIndex;
	}

	public float getClassLabel(int classIndex) {
		return events[classIndex].classLabel;
	}

	public SliqNode getClassLeafRef(int classIndex) {
		return events[classIndex].classNodeRef;
	}

	public boolean getClassLeafPurity(int classIndex) {
		return events[classIndex].classNodeRef.getLeafIndicator();
	}

	public void setClassLeafRef(int classIndex, SliqNode leaf) {
		events[classIndex].classNodeRef = leaf;
	}

	public int getClassProxyIndex(int classIndex) {
		return events[classIndex].proxyIndex;
	}

	public void initializeClassNodesRefs(SliqTree sliqTree) {
		for (int i = 0; i < eventsNumber; i++) {
			events[i].classNodeRef = (SliqNode)sliqTree.getRoot();
		}
	}


	// *****************************************
	// sort method proxies ...
	private void sort() {
		quickSort(0,eventsNumber-1);
	}

	// *****************************************
	// ... using Quic-Sort alg.
	private void quickSort(int l, int r) {
		int i = l;
		int j = r;
		ClassListEvent x;
		ClassListEvent w;

		x = events[((l+r)/2)];

		do {
			while (events[i].classLabel < x.classLabel) { i++; }
			while (events[j].classLabel > x.classLabel) { j--; }

			if (i<=j) {
				w = events[i];
				events[i] = events[j];
				events[j] = w;
				i++;
				j--;
			}

		} while (i <= j);

		if (l<j) quickSort(l,j);
		if (i<r) quickSort(i,r);

	}
	// *****************************************
	//
	private int setProxies() {
		proxies = new ArrayList<ClassProxyEvent>();
		int proxyIdx = 0;
		float tmpClassLabel = events[0].classLabel;
		proxies.add(new ClassProxyEvent(tmpClassLabel));
		events[0].proxyIndex = proxyIdx;

		for (int i=1; i<eventsNumber; i++) {
			if (events[i].classLabel != tmpClassLabel) {
				tmpClassLabel = events[i].classLabel;
				proxyIdx++;
				proxies.add(new ClassProxyEvent(tmpClassLabel));
			}
			events[i].proxyIndex = proxyIdx;
		}

		ClassListEvent[] tmpEvents = new ClassListEvent[eventsNumber];
		for (int i=0; i<eventsNumber; i++) {
			tmpEvents[events[i].classIndex] = events[i];
		}

		events = tmpEvents;

		return proxyIdx+1;
	}
	// *****************************************
	//
	public int getProxiesNumber()
	{
		return proxiesNumber;
	}
	// *****************************************
	//
	public float getClassProxy(int proxyIndex)
	{
		return (proxies.get(proxyIndex)).classLabel;
	}
	// *****************************************
	//
	public int getEventsNumber()
	{
		return eventsNumber;
	}
	// *****************************************
	//
	public SliqNode getAttrValueLeaf(AttributeList attributeList, int attrEventInedx)
	{
		return getClassLeafRef(attributeList.getAttrClassIndex(attrEventInedx));
	}
	// *****************************************
	public boolean getAttrValueLeafPurity(AttributeList attributeList, int attrEventInedx)
	{
		return events[attributeList.getAttrClassIndex(attrEventInedx)].classNodeRef.getLeafIndicator();
	}
	// *****************************************
	public int getAttrClassProxyIndex(AttributeList attributeList, int attrEventInedx)
	{
		return getClassProxyIndex(attributeList.getAttrClassIndex(attrEventInedx));
	}
//	 *****************************************
	public void calcClassFrequencies(SliqTree sliqTree)
	{

		for (int i = 0; i < eventsNumber; i++)
		{
			if (events[i].classNodeRef.getLeafIndicator() == Const.NODE)
				events[i].classNodeRef.classFrequencies[events[i].proxyIndex]++;
		}
	}
	//*****************************************
	public void initHistograms(SliqTree sliqTree, AttributeList attributeList)
	{
		SliqNode node;
		int leafsNumber = sliqTree.getLeafsSize();

		if (attributeList.getAttrType() == Const.NUMERIC_ATTR)
		{
			for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++)
			{
				node = sliqTree.getLeaf(leafIndex);
				if (node.getLeafIndicator() == Const.NODE)
					node.numericHistogram.init(node.classFrequencies);
			}
		}
		else {
			for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++)
			{
				node = sliqTree.getLeaf(leafIndex);
				if (node.getLeafIndicator() == Const.NODE)
                                node.nominalHistogram = new NominalHistogram(this, attributeList);
			}
		}
	}
	//***********************************************
	public void updateToLEftAttrValueLeafNumHist(AttributeList attributeList,int attrEventIndex) {
		int classIndex = attributeList.getAttrClassIndex(attrEventIndex);
		events[classIndex].classNodeRef.numericHistogram.updateToLEft(events[classIndex].proxyIndex);
	}
    //***********************************************
	public void incRightAttrValueLeafNomHist(AttributeList attributeList,int attrEventIndex)
    {
		int classIndex = attributeList.getAttrClassIndex(attrEventIndex);
		events[classIndex].classNodeRef.nominalHistogram.incRight(attributeList.getAttrProxyIndex(attrEventIndex), events[classIndex].proxyIndex);
	}

}
