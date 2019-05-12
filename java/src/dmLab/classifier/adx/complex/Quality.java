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
package dmLab.classifier.adx.complex;


public class Quality
{
  public Quality()
  {
  }
//*********************************
  public static double calc(float posCoverage,float negCoverage,int qMethod)
  {
    if (qMethod == 0)
      return (posCoverage - negCoverage);
    else if (qMethod == 1)
      return (posCoverage - negCoverage) * (1 - negCoverage);
    else if (qMethod == 2)
      return (posCoverage - negCoverage) * Math.pow(1 - negCoverage,2.0);
    else if (qMethod == 3)
      return posCoverage;
    else if (qMethod == 4)
      return 1.0 / negCoverage;
    else if (qMethod == 5)
    	return posCoverage * (1 - negCoverage);
    else
      return -1.0;
  }
//*********************************

}
