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
package dmLab.classifier.attributeIndicators;



/**
 * @author Mike242
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class J48NodeIndicators extends AttributeIndicators
{
    public int nodeIndex;
	public float attrInfoGain;
	public float attrGainRatio;
	public float attrEventsNumber;
	public float classProb;
	public float cov;
	
    //*********************************************
    public J48NodeIndicators()
    {
        //number of indicators
        size=4;
        
        nodeIndex = 0;
        attrInfoGain = 0;
        attrGainRatio = 0;
        attrEventsNumber = 0;
        classProb = 0;
    }
	//*********************************************
	public J48NodeIndicators(int eventsNumber)
	{
		nodeIndex=0;
		attrInfoGain=0;
		attrGainRatio=0;
		attrEventsNumber=0;
	}
	//*********************************************
	public String toString2()
	{
		StringBuffer tmp=new StringBuffer(); 
		tmp.append("nodeIndex: "+nodeIndex);
		tmp.append(" attrInfoGain: "+attrInfoGain);
		tmp.append(" attrGainRatio: "+attrGainRatio);
		tmp.append(" attrEventsNumber: "+attrEventsNumber);
		return tmp.toString();
	}
	//*********************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer(); 
        tmp.append("#").append(nodeIndex);
        tmp.append("#").append(attrInfoGain);
        tmp.append("#").append(attrGainRatio);
        tmp.append("#").append(attrEventsNumber).append("#");
        return tmp.toString();
    }
    //*********************************************

}
