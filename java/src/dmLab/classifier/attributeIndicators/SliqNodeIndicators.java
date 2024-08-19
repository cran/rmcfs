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


public class SliqNodeIndicators extends AttributeIndicators
{
	public float diversityMeasure;
	public float goodnessOfSplit;
	public float attrEventsNumber;
	//*********************************************
    public SliqNodeIndicators()
    {
        //number of indicators
        size=3;
        
        diversityMeasure=0;
        goodnessOfSplit=0;
        attrEventsNumber=0;
    }
    //*********************************************
    public SliqNodeIndicators(int eventsNumber)
	{
        //number of indicators
        size=3;
        
        diversityMeasure=0;
		goodnessOfSplit=0;
		attrEventsNumber=0;
	}
	//*********************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer(); 
		tmp.append("### sliq NodeIndicators ###").append('\n');
		tmp.append(" diversityMeasure: "+diversityMeasure);
		tmp.append(" goodnessOfSplit: "+goodnessOfSplit);
		tmp.append(" attrEventsNumber: "+attrEventsNumber);
		return tmp.toString();
	}
	//*********************************************
}
