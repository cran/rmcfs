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
import dmLab.array.meta.Attribute;
import dmLab.classifier.sliq.Tree.Const;


//**********************************************************
class AttributeList {

	private boolean attrType;
	private int attrIndex;
	private AttributeListEvent[] events;
        private AttributeListEvent[] allEvents;
	private ArrayList <AttributeProxyEvent>proxies;
	private int proxiesNumber;
	private boolean splitStatus;

	//constructor
//	*****************************************
	public AttributeList(FArray trainArray, int attrIndex)
	{
		if ( (trainArray.attributes[attrIndex].type == Attribute.NUMERIC) ||
                     (trainArray.attributes[attrIndex].type == Attribute.UNKNOWN ) )
			attrType = Const.NUMERIC_ATTR;
		else
			attrType = Const.NOMINAL_ATTR;

		int eventsNumber = trainArray.rowsNumber();
		//count number of nulls
		int nulls=0;
		for (int i=0;i<eventsNumber;i++)
			if(Float.isNaN(trainArray.readValue(attrIndex, i)))
				nulls++;

		this.attrIndex = attrIndex;
		events = new AttributeListEvent[eventsNumber-nulls];
                allEvents = new AttributeListEvent[eventsNumber];

		int j = 0;
                float tmp=0;
		for(int i=0; i<eventsNumber; i++)
		{
                    tmp=trainArray.readValue(attrIndex, i);
                    allEvents[i]=new AttributeListEvent();
                    allEvents[i].attrValue = tmp;
                    allEvents[i].classListIndex = i;

			if(!Float.isNaN(tmp))
			{
				events[j]=new AttributeListEvent();
				events[j].attrValue = tmp;
				events[j].classListIndex = i;
				j++;
			}
		}

		initSplitStatus();
		sort();
		proxiesNumber = setProxies();
	}
//	*****************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("### attributeList ###\n");
		tmp.append("attrIndex: "+attrIndex+" events:").append('\n');
		for(int i=0;i<events.length;i++)
			if(events[i]==null)
				tmp.append("null").append('\n');
			else
				tmp.append(events[i].toString()).append('\n');
		return tmp.toString();
	}
//*****************************************
	public void initSplitStatus()
	{
		splitStatus = Const.ATTR_NOT_USED_IN_SPLIT;
	}
//	 *****************************************
	public int getEventsNumber()
	{
		return events.length;
	}
        public int getAllEventsNumber()
        {
                return allEvents.length;
        }
	// *****************************************
	public boolean getAttrType()
	{
		return attrType;
	}
	// *****************************************
	public int getAttrIndex()
	{
		return attrIndex;
	}
	// *****************************************
	public AttributeListEvent getEvent(int row)
	{
		return events[row];
	}
        public AttributeListEvent getAllEvent(int row)
        {
                return allEvents[row];
        }
	// *****************************************
	public float getEventValue(int eventIndex)
	{
		return events[eventIndex].attrValue;
	}
        public float getAllEventValue(int eventIndex)
        {
                return allEvents[eventIndex].attrValue;
        }
	// *****************************************
	public int getAttrClassIndex(int eventIndex)
	{
		return events[eventIndex].classListIndex;
	}
        public int getAllAttrClassIndex(int eventIndex)
        {
                return allEvents[eventIndex].classListIndex;
        }
	// *****************************************
	public int getAttrProxyIndex(int eventIndex)
	{
		return events[eventIndex].proxyIndex;
	}
//	 *****************************************
	public boolean getSplitStatus()
	{
		return splitStatus;
	}
//	 *****************************************
	public void setSplitStatus(boolean splitStatus)
	{
		this.splitStatus = splitStatus;
	}
	// *****************************************
	// sort method for Pre-Sorting phase ...
	private void sort()
	{
		quickSort(0,events.length-1);
	}
	// *****************************************
	// ... using Quic-Sort alg.
	private void quickSort(int l, int r)
	{
		int i = l;
		int j = r;
		AttributeListEvent x;
		AttributeListEvent w;

		x = events[((l+r)/2)];

		do {
			while (events[i].attrValue < x.attrValue )
				i++;
			while (events[j].attrValue > x.attrValue )
				j--;

			if (i<=j)
			{
				w = events[i];
				events[i] = events[j];
				events[j] = w;
				i++;
				j--;
			}
		} while (i <= j);

		if (l<j)
			quickSort(l,j);
		if (i<r)
			quickSort(i,r);
	}
	// *****************************************
	private int setProxies()
	{
		proxies = new ArrayList<AttributeProxyEvent>();
		int proxyIdx = 0;
		float tmpAttrValue = events[0].attrValue;
		proxies.add(new AttributeProxyEvent(tmpAttrValue));
		events[0].proxyIndex = proxyIdx;

		for (int i=1; i<events.length; i++)
		{
			if (events[i].attrValue != tmpAttrValue)
			{
				tmpAttrValue = events[i].attrValue;
				proxyIdx++;
				proxies.add(new AttributeProxyEvent(tmpAttrValue));
			}
			events[i].proxyIndex = proxyIdx;
		}
		return proxyIdx+1;
	}
	// *****************************************
	//
	public int getProxiesNumber() {
		return proxiesNumber;
	}
	// *****************************************
	//
	public AttributeProxyEvent getAttrProxy(int proxyIndex) {
		return proxies.get(proxyIndex);
	}
	// *****************************************
	//
	public float getAttrProxyValue(int proxyIndex) {
		return getAttrProxy(proxyIndex).attrValue;
	}
	// *****************************************
	//
	public float getAttrProxyGiniIndex(int proxyIndex) {
		return getAttrProxy(proxyIndex).giniIndex;
	}
}
