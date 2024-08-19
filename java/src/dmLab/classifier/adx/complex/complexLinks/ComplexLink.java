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
package dmLab.classifier.adx.complex.complexLinks;

import dmLab.classifier.adx.complex.Complex;
import dmLab.utils.GeneralUtils;


public class ComplexLink implements Comparable<ComplexLink>
{
    public int setIndex;
    public int complexIndex;
    
    public Complex complex;
    public double qMeasure;
//  ************************************************
    public ComplexLink(Complex complex,int setIndex, int complexIndex,double quality)
    {
        this.complex=complex;
        this.setIndex=setIndex;
        this.complexIndex=complexIndex;
        this.qMeasure=quality;
    }
//  ************************************************
    public int compareTo(ComplexLink complexLink) throws ClassCastException
    {        
        if(qMeasure==complexLink.qMeasure)
            return 0;
        else if(qMeasure>complexLink.qMeasure)
            return 1;
        else
            return -1;
    }
//  ***********************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("setId: "+setIndex);               
        tmp.append(" complexId: "+ complexIndex);
        tmp.append(" qMeasure: "+GeneralUtils.formatFloat(qMeasure,4));
        return tmp.toString();
    }
//  ***********************************************
}
