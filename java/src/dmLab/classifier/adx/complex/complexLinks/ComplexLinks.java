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

import java.util.Arrays;

import dmLab.array.FArray;
import dmLab.classifier.adx.ADXParams;
import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.selector.SelectorList;

public class ComplexLinks
{
    protected ComplexLink[] links;
    protected boolean mask[];
    
    protected int iterator;
    protected boolean iterateSelected;
    
    protected double sumPos;
    protected double sumNeg;
    protected int activeComplexes;
    
    public double alternativeScore;

    //********************************
    public ComplexLinks()
    {
        iterator=0;
        iterateSelected=true;
    }
    //********************************
    public ComplexLinks(ComplexLink[] links)
    {
        this();
        this.links=links;
        mask=new boolean [links.length];
        enableAll();        
    }
    //********************************
    public ComplexLink[] getLinks()
    {
        return links;
    }
//  ********************************
    public ComplexLink getLink(int index)
    {
        return links[index];
    }
//  ********************************
    public void setLinks(ComplexLink[] links)
    {
        this.links = links;
        mask=new boolean [links.length];
    }
//  ********************************
    public int size()
    {
        return links.length;
    }
//  ********************************
    public boolean enabled(int index)
    {
        return mask[index];
    }
//  ********************************
    public void sort()
    {
        Arrays.sort(links);    
    }
//  ********************************
    public void enableAll()
    {
        Arrays.fill(mask,true);
    }
//  ********************************
    public void disableAll()
    {
        Arrays.fill(mask,false);
    }
//  ********************************
    public void enable(int index)
    {
        mask[index]=true;
    }
//  ********************************
    public void disable(int index)
    {
        mask[index]=false;
    }
//  ********************************
    public void calcGlobalParams(ADXParams adxParams)
    {
        activeComplexes=0;
        sumPos=0;
        sumNeg=0;    

        for(int j=0;j<links.length;j++)
        {
            if(mask[j])
            {
                final Complex complex=links[j].complex;
                activeComplexes++;
                sumPos+=complex.posCoverage;
                sumNeg+=complex.negCoverage;
            }
        }
    }
//  *****************************************
    public double calcScore(SelectorList selectorsList,FArray array,int eventIndex,ADXParams adxParams)
    {
        double currPos=0;
        double currNeg=0;
        int covered=0;

        double probability=1;
        double avgProbability=0;

        for(int i=0;i<links.length;i++)
        {
            if(mask[i])
            {
                {
                    final Complex complex=links[i].complex;
                    if(complex.covers(selectorsList,array,eventIndex))
                    {
                        currPos+=complex.posCoverage;
                        currNeg+=complex.negCoverage;

                        probability*=1-complex.calcPosProbability();
                        avgProbability+=complex.calcPosProbability();
                        covered++;                            
                    }
                }
            }
        }
        double score=Double.NaN;

        if(covered==0)
            return 0;
        else
        {                    
            if(adxParams.scoreMethod==0)                //0:[(p-n)/c]/[(P-N)/C]
            {
                double sumAvg=(sumPos-sumNeg)/activeComplexes;
                score= ((currPos - currNeg)/covered)/sumAvg;
            }        
            else if(adxParams.scoreMethod==1)                //1:(p-n)/(P-N)
                score= (currPos - currNeg)/(sumPos-sumNeg);        
            else if(adxParams.scoreMethod==2)            //2:(p/P)*(N/n)
            {
                if(currNeg!=0)
                    score= (currPos/sumPos)*(sumNeg/currNeg) ;
                else
                    score= (currPos/sumPos);
            }
            else if(adxParams.scoreMethod==3)            //3:(p/P)*[1-(n/N)]
            {
                if(sumNeg!=0)
                    score= (currPos/sumPos)*(1-currNeg/sumNeg);
                else
                    score= (currPos/sumPos);
            }
            else if(adxParams.scoreMethod==4)            //4:Pr
                score= 1-probability;                
            else if(adxParams.scoreMethod==5)            //5:avgPr
                score= avgProbability/covered;
            else if(adxParams.scoreMethod==6)            //6:avgPr | (p/P)
            {
                score= avgProbability/covered;
                alternativeScore=(currPos/sumPos);
            }
            else if(adxParams.scoreMethod==7)            //6:Pr | (p/P)
            {
                score= 1-probability;
                alternativeScore=(currPos/sumPos);
            }
        }
        if(Double.isNaN(score)){
            System.err.println("Error! Score is NaN for eventIndex="+eventIndex);
            System.err.println("# sumPos="+sumPos+" sumNeg="+sumNeg);
            System.err.println("# currPos="+currPos+" currNeg="+currNeg);
            System.err.println("# activeComplexes="+activeComplexes+" covered="+covered);
            System.err.println("# probability="+probability+" avgProbability="+avgProbability);
        }
        else if(Double.isInfinite(score)){
            System.err.println("Error! Score is Infinite for eventIndex="+eventIndex);
            System.err.println("# sumPos="+sumPos+" sumNeg="+sumNeg);
            System.err.println("# currPos="+currPos+" currNeg="+currNeg);
            System.err.println("# activeComplexes="+activeComplexes+" covered="+covered);
            System.err.println("# probability="+probability+" avgProbability="+avgProbability);
        }
        return score;
    }
//  **********************************************************
    public String toString(SelectorList selectorsList,FArray array)
    {
        StringBuffer tmp=new StringBuffer();
        for(int i=0;i<links.length;i++)
        {
            if(mask[i])
            {
                tmp.append(links[i].complex.toString(selectorsList,array));
                tmp.append(' ').append(links[i].complex.toStringCov());
                tmp.append(' ').append(links[i].complex.toStringPosProbability());
                tmp.append('\n');
            }
        }       
        return tmp.toString();
    }
//  *****************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        for(int i=0;i<links.length;i++)
        {
            if(mask[i])
                tmp.append(links[i].toString()+"\n");
        }
        return tmp.toString();
    }
//*****************************************
    public boolean[] getMask()
    {
        return mask;
    }
//  *****************************************    
    public boolean initIterator(boolean iterateSelected)
    {
        iterator=0;
        this.iterateSelected=iterateSelected;
        return true;
    }
//  *****************************************
    public ComplexLink getNext()
    {        
        while(iterator<links.length)
        {
            if(mask[iterator]==iterateSelected)
                return links[iterator++];
            else
                iterator++;
        }
            
        return null;
    }
//  *****************************************
}
