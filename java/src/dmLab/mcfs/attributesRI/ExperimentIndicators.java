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
package dmLab.mcfs.attributesRI;

public class ExperimentIndicators
{
    public double predictionQuality;
    public int eventsNumber;
    //****************************
    public ExperimentIndicators()
    {
        eventsNumber=-1;
        predictionQuality=-1;
    }
    //****************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(eventsNumber).append(", ").append(predictionQuality);
        return tmp.toString();
    }
    //****************************

}
