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
package dmLab.classifier.adx.ruleSet;

import dmLab.array.FArray;
import dmLab.classifier.adx.ADXParams;
import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.complex.Quality;
import dmLab.classifier.adx.complex.complexLinks.ComplexLink;
import dmLab.classifier.adx.complex.complexLinks.ComplexLinks;
import dmLab.classifier.adx.selector.SelectorList;
import dmLab.utils.ArrayUtils;
import dmLab.utils.list.ObjectList;

public class SelectRules {
    
    public SelectRules()
    {        
    }
//  **********************************************************
    public void selectMinFinalRuleSet_fast(RuleSet ruleSet,FArray array,SelectorList selectorsList,ADXParams adxParams,int finalBeam)
    {
        //final int complexesNumber=ruleSet.getComplexesNumber();
        final int eventsNumber=array.rowsNumber();

        ruleSet.createLinks(adxParams);
        ComplexLinks links=ruleSet.getLinks();
        
        int posEvents[]=new int [eventsNumber];
        final int decisionId=array.getDecAttrIdx();//for speed
        final float decValue=array.getDecValues()[ruleSet.decValIndex];//for speed        
        int posNumber=0;
        int negNumber=0;
        //set 1 for positive events and -1 for negative events                        
        for(int i=0;i<eventsNumber;i++)
        {
            if(array.readValue(decisionId,i)==decValue)
            {
                posEvents[i]=1;
                posNumber++;
            }
            else
            {
                posEvents[i]=-1;
                negNumber++;
            }
        }
        int maxEventsForSelection=adxParams.maxEventsForSelection;
        //System.out.println("\n1. pos:"+UtilsArray.count(posEvents, 1)+" neg: "+UtilsArray.count(posEvents, -1)+" ignored: "+UtilsArray.count(posEvents, 0));
        //System.out.println("2. pos:"+posNumber+" neg: "+negNumber);
        //this part reduces number of events for selection of rules 
        int size=posEvents.length;
        if(posEvents.length>maxEventsForSelection)
        {
            double minProb=(double)(size-maxEventsForSelection)/(double)size;
            for(int i=0;i<size;i++)
            {
                if(Math.random()<minProb)
                {
                    if(posEvents[i]==1)
                        posNumber--;
                    else if(posEvents[i]==-1)
                        negNumber--;
                    posEvents[i]=0;
                }
            }
        }
        //System.out.println("3. pos:"+UtilsArray.count(posEvents, 1)+" neg: "+UtilsArray.count(posEvents, -1)+" ignored: "+UtilsArray.count(posEvents, 0));
        //System.out.println("4. pos:"+posNumber+" neg: "+negNumber);
        
        int rulesNumber=links.size();           
        int addedRules=0;
        links.sort();
        //System.out.println("\n"+links.toString(selectorsList, array));
        
        links.disableAll();
        links.calcGlobalParams(adxParams);            
        double globalScoreIndex=scoreIndex(ruleSet,array,selectorsList,links,adxParams,posEvents,posNumber,negNumber);
        
        for(int i=rulesNumber-1;i>=0;i--)
        {
            //System.out.println(" "+ i+" from "+rulesNumber);            
            //System.out.println("************  enabled: "+ i+" q: "+links.getLink(i).qMeasure);
            
            links.enable(i);
            links.calcGlobalParams(adxParams);
            double scoreIndex=scoreIndex( ruleSet, array, selectorsList,links,adxParams, posEvents,posNumber,negNumber);            
            //System.out.println("**** globalScoreIndex: "+globalScoreIndex+" scoreIndex: "+scoreIndex);
            
            if(scoreIndex>=globalScoreIndex)//stay with what u have
            {
                globalScoreIndex=scoreIndex;
                addedRules++;

                if(addedRules==finalBeam)
                {
                    //System.out.println(" --- added: "+ addedRules + " finalBeam="+finalBeam+". Stop adding complexes!");
                    break;
                }
                //System.out.println("**** "+ i + " to add");                
            }
            else //rollback changes
            {
                links.disable(i);
                //System.out.println("************ "+ i + " dont add");
            }
        }
        //System.out.println("************ FINAL RULES ************");
        //System.out.println(links.toString(selectorsList, array));
        ruleSet.commitLinks();
        
        //System.out.println("************ FINAL RULES2 ************");
        //System.out.println(ruleSet.toString(selectorsList, array));
    }
//  **********************************************************
    public void selectMinFinalRuleSet_normal(RuleSet ruleSet,FArray array,SelectorList selectorsList,ADXParams adxParams)
    {
        //final int complexesNumber=ruleSet.getComplexesNumber();
        final int eventsNumber=array.rowsNumber();

        ruleSet.createLinks(adxParams);
        ComplexLinks links=ruleSet.getLinks();
        
        int posEvents[]=new int [eventsNumber];
        final int decisionId=array.getDecAttrIdx();//for speed
        final float decValue=array.getDecValues()[ruleSet.decValIndex];//for speed        
        int posNumber=0;
        int negNumber=0;
        //set true for positive events                        
        for(int i=0;i<eventsNumber;i++)
        {
            if(array.readValue(decisionId,i)==decValue)
            {
                posEvents[i]=1;
                posNumber++;
            }
            else
            {
                posEvents[i]=-1;
                negNumber++;
            }
        }
                
        int rulesNumber=links.size();         
        links.sort();
        //System.out.println("\n"+links.toString(selectorsList, array));
        
        links.disableAll();
        //System.out.println("posScore,negScore");
        links.calcGlobalParams(adxParams);            
        double globalScoreIndex=scoreIndex(ruleSet,array,selectorsList,links,adxParams,posEvents,posNumber,negNumber);

        for(int i=rulesNumber-1;i>=0;i--)
        {
            //System.out.println(" "+ i+" z "+rulesNumber);            
            //System.out.println("************  enabled: "+ i+" q: "+links.getLink(i).qMeasure);
            
            links.enable(i);
            links.calcGlobalParams(adxParams);
            double scoreIndex=scoreIndex( ruleSet, array, selectorsList,links,adxParams, posEvents,posNumber,negNumber);            
            //System.out.println("**** globalScoreIndex: "+globalScoreIndex+" scoreIndex: "+scoreIndex);
            
            if(scoreIndex>=globalScoreIndex)//stay with what u have
            {
                globalScoreIndex=scoreIndex;
                //System.out.println("**** "+ i + " do dodania");                
            }
            else //rollback changes
            {
                links.disable(i);
                //System.out.println("************ "+ i + " nie dodaje");
            }
        }
        //System.out.println("************ FINAL RULES ************");
        //System.out.println(links.toString(selectorsList, array));
        ruleSet.commitLinks();
        
        //System.out.println("************ FINAL RULES2 ************");
        //System.out.println(ruleSet.toString(selectorsList, array));
    }
//  **********************************************************
    private double scoreIndex(RuleSet ruleSet,FArray array,SelectorList selectorsList,ComplexLinks links,
                    ADXParams adxParams,int posEvents[],int posNumber,int negNumber)
    {        
        double posScore=0;
        double negScore=0;
        double score=0;
        
        for(int i=0;i<posEvents.length;i++)
        {            
            if(posEvents[i]!=0)
            {
                score=links.calcScore(selectorsList, array, i, adxParams);                        
                if(posEvents[i]==1)
                    posScore+=score;            
                else if(posEvents[i]==-1)
                    negScore+=score;
            }
        }                
        //System.out.println("rules: \n"+ruleSet.toString(selectorsList, array));
        //System.out.println("sum posScore: "+posScore);
        //System.out.println("sum negScore: "+negScore);

        //System.err.println(posScore+","+negScore);
        
        double scoreResult=Quality.calc((float)(posScore/posNumber),(float)(negScore/negNumber),adxParams.scoreQ);       
        //System.out.println("scoreResult: "+scoreResult);        
        return scoreResult;
    }
//  *****************************************
//  method deletes all complexes over finalBeam number
    // method randomly selects complexes with smallest q to have finally 'finalBeam' number of complexes
    public int selectFinalRuleSet(RuleSet ruleSet,ADXParams adxParams,int finalBeam)
    {               
        int rules=0;
        int removed=0;
        
        for(int i=0;i<ruleSet.complexSetArray.length;i++)
            if(ruleSet.complexSetArray[i]!=null)
                rules+=ruleSet.complexSetArray[i].size();
        
        if(rules<=finalBeam)
            removed+=ruleSet.deleteComplexesPosLessNeg();
        else
        {
            double significantQ=ruleSet.getSignificantQuality(finalBeam,adxParams.qMethodFinal);
            ObjectList minQualityLinks=new ObjectList();
            for(int i=0;i<ruleSet.complexSetArray.length;i++)
            {               
                if(ruleSet.complexSetArray[i]!=null)
                {
                    int setSize=ruleSet.complexSetArray[i].size();
                    for(int j=0;j<setSize;j++)
                    {
                        Complex complex=ruleSet.complexSetArray[i].getComplex(j);
                        final double complexQuality=complex.calcQuality(adxParams.qMethodFinal);
                        if(complexQuality<significantQ)
                        {
                            ruleSet.complexSetArray[i].removeComplex(j);
                            removed++;
                        }                        
                        else if(complexQuality==significantQ)
                            minQualityLinks.add(new ComplexLink(complex,i,j,complexQuality));                        
                    }                    
                }
            } 
            
            if(finalBeam<rules-removed)
            {
                int toThrow=rules-removed-finalBeam;
                int throwArray[]=new int[minQualityLinks.size()];
        		ArrayUtils arrayUtils = new ArrayUtils();
        		arrayUtils.randomFill(throwArray,toThrow,1,0);

                for(int i=0;i<throwArray.length;i++)
                {
                    if(throwArray[i]==1)
                    {
                        final ComplexLink link=(ComplexLink)minQualityLinks.get(i);
                        ruleSet.complexSetArray[link.setIndex].removeComplex(link.complexIndex);
                        removed++;
                    }
                }
            }            
        }   
        for(int i=0;i<ruleSet.complexSetArray.length;i++)
            if(ruleSet.complexSetArray[i]!=null)
            {
                ruleSet.complexSetArray[i].trimList();                
                if(ruleSet.complexSetArray[i].size()==0)
                    ruleSet.complexSetArray[i]=null;
            }
        
        return removed;
    }
    //************************************************************
    public void selectMinFinalRuleSet_slow(RuleSet ruleSet,FArray array,SelectorList selectorsList,ADXParams adxParams)
    {
        //final int complexesNumber=ruleSet.getComplexesNumber();
        final int eventsNumber=array.rowsNumber();

        ruleSet.createLinks(adxParams);
        ComplexLinks links=ruleSet.getLinks();
        
        int posEvents[]=new int [eventsNumber];
        final int decisionId=array.getDecAttrIdx();//for speed
        final float decValue=array.getDecValues()[ruleSet.decValIndex];//for speed        
        int posNumber=0;
        int negNumber=0;
        //set true for positive events                        
        for(int i=0;i<eventsNumber;i++)
        {
            if(array.readValue(decisionId,i)==decValue)
            {
                posEvents[i]=1;
                posNumber++;
            }
            else
            {
                posEvents[i]=-1;
                negNumber++;
            }
        }
        
        links.enableAll();        
        int rulesNumber=links.size();
        int maxIndex=-1;
        //System.err.println("posScore,negScore");
        while(maxIndex>=-1)
        {
            links.calcGlobalParams(adxParams);
            
            double maxScoreIndex=scoreIndex(ruleSet,array,selectorsList,links,adxParams,posEvents,posNumber,negNumber);
            maxIndex=-1;
            
            for(int i=0;i<rulesNumber;i++)
            {            
                if(links.enabled(i))
                {
                    links.disable(i);
                    links.calcGlobalParams(adxParams);            
                    //System.out.println("************  disabled: "+ i);
                    double scoreIndex=scoreIndex( ruleSet, array, selectorsList,links,adxParams, posEvents,posNumber,negNumber);
                    links.enable(i);
                    if(scoreIndex>maxScoreIndex)
                    {
                        maxScoreIndex=scoreIndex;
                        maxIndex=i;
                        //System.out.println("************ "+ i + " do wywalenia");
                    }
                    else
                    {
                        //System.out.println("************ "+ i + " zostaje");
                    }
                }
            }            
            if(maxIndex>-1)
            {
                links.disable(maxIndex);
                //System.out.println("************ "+ maxIndex + " wywalam");
            }
            else
                maxIndex=-2;
        }
        //System.out.println("************ FINAL RULES ************");
        //System.out.println(links.toString(selectorsList, array));
        ruleSet.commitLinks();
        
        //System.out.println("************ FINAL RULES2 ************");
        //System.out.println(ruleSet.toString(selectorsList, array));
    }
    //********************************************************
}
