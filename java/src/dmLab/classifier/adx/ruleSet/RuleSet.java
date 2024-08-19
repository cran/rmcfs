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
package dmLab.classifier.adx.ruleSet;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import dmLab.array.FArray;
import dmLab.classifier.adx.ADXParams;
import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.complex.ComplexSet;
import dmLab.classifier.adx.complex.complexLinks.ComplexLink;
import dmLab.classifier.adx.complex.complexLinks.ComplexLinks;
import dmLab.classifier.adx.selector.SelectorList;

public class RuleSet
{
    public ComplexSet complexSetArray[];
    public ComplexLinks complexLinks;
    
    protected int decValIndex;
    protected ADXParams adxParams;

    public boolean verbose = false;
//  *****************************************
    public RuleSet(int maxComplexLength,ADXParams adxParams)
    {
        complexLinks=null;
        complexSetArray=new ComplexSet[maxComplexLength];
        this.adxParams=adxParams;
    }
//  *****************************************
    public RuleSet(ComplexSet complexList[],ADXParams adxParams)
    {
        complexSetArray=complexList;
        this.adxParams=adxParams;
    }
//  *****************************************
    public int addComplex(Complex complex)
    {
        int complexSize=complex.size();
        if(complexSetArray[complexSize-1]==null)
            complexSetArray[complexSize-1]=new ComplexSet(complexSize,adxParams);
        int complexIndex=complexSetArray[complexSize-1].addComplex(complex);
        return complexIndex;
    }
//  *****************************************
    public ComplexSet getComplexList(int index)
    {
        return complexSetArray[index];
    }
//  *****************************************
    public void calcGlobalParams()
    {
        complexLinks.calcGlobalParams(adxParams);
    }
//  *****************************************
    public boolean createRules(FArray array,SelectorList selectorList,int decisionValIndex)
    {
        decValIndex=decisionValIndex;
        
        if(adxParams.keepMinimalSet && adxParams.searchBeam<selectorList.size())
        {
            if(verbose) System.out.print("Deleting surplus selectors...");
            selectorList.deleteInsignificant(adxParams.searchBeam,adxParams.qMethod);
            if(verbose) System.out.println(" Done. Current selectors base size: "+selectorList.size());
        }		
        complexSetArray[0]=new ComplexSet(selectorList,adxParams,array.colsNumber());		
        // START DEBUG INFO
        //System.out.println("### DEBUG INFO\n"+complexSetArray[0].toString(selectorList,array));
        // END DEBUG INFO
        
        for(int currentComplexLength=1;currentComplexLength<complexSetArray.length;currentComplexLength++)
        {			            
            if(verbose) System.out.println("Iteration "+currentComplexLength+" of "+(complexSetArray.length-1));
                                
            double minQuality=complexSetArray[currentComplexLength-1].findSignificantQuality();

            int toReproduce=complexSetArray[currentComplexLength-1].selectComplexesToReproduce(minQuality);
            if(verbose) System.out.println("Complexes to reproducing: "+toReproduce);
            if(toReproduce==0)
                break;
            
            if(verbose) System.out.print("Generating complexes candidates (size="+currentComplexLength+")...");			
            
            complexSetArray[currentComplexLength]=new ComplexSet(selectorList,
                    complexSetArray[currentComplexLength-1],adxParams,array.colsNumber());
            complexSetArray[currentComplexLength].verbose = verbose;
            
            if(verbose) System.out.println(""+complexSetArray[currentComplexLength].size()+" Complexes generated.");
            
            if(testStopCriteria(currentComplexLength)==true)
                break;
            
            if(verbose) System.out.println("Evaluating complexes candidates...");
            complexSetArray[currentComplexLength].evaluate(selectorList,array,decisionValIndex);
            if(verbose) System.out.println(""+complexSetArray[currentComplexLength].size()+" Complexes evaluated.");
            
            //System.out.println("### DEBUG INFO - candidates\n"+complexSetArray[currentComplexLength].toString(selectorList,array));
            
            if(verbose) System.out.print("Clenaning candidates...");
            //if there are special criteria about complexes
            complexSetArray[currentComplexLength].cleanCandidatesSet(selectorList,adxParams);			
            if(verbose) System.out.println(" Done. Current complexes (size="+currentComplexLength+") number is "+complexSetArray[currentComplexLength].size());
            
            //System.out.println("### DEBUG INFO - after cleaning\n"+complexSetArray[currentComplexLength].toString(selectorList,array));			
            
            if(testStopCriteria(currentComplexLength))
                break;
            
            if(adxParams.keepMinimalSet && adxParams.searchBeam<complexSetArray[currentComplexLength].size())
            {
                minQuality=complexSetArray[currentComplexLength].findSignificantQuality();
                if(verbose) System.out.print("Deleting surplus complexes..."); 
                complexSetArray[currentComplexLength].deleteComplexesQLess(minQuality);
                if(verbose) System.out.println(" Done. Current complexes (size="+currentComplexLength+") number is "+complexSetArray[currentComplexLength].size());
            }
        }
        return true;
    }
//  *****************************************
    private boolean testStopCriteria(int currentComplexLength)
    {
        //if list is empty
        if(complexSetArray[currentComplexLength].size()==0)
        {
            if(verbose) System.out.println("Stop criterion has been met.");
            complexSetArray[currentComplexLength]=null;
            return true;
        }
        return false;
    }
//  *****************************************
    public void mergeRules(SelectorList selectorList,FArray array)
    {		
        for(int i=0;i<complexSetArray.length;i++){						
            if(complexSetArray[i]!=null){
                //System.out.println("### DEBUG INFO - before merging\n"+complexSetArray[i].toString(selectorList,array));                
                complexSetArray[i].mergeComplexes(selectorList,array);
                //System.out.println("### DEBUG INFO - after merging\n"+complexSetArray[i].toString(selectorList,array));                
            }
            else
                continue;
        }
    }
//  *****************************************
    public String toString(SelectorList selectorsList,FArray array)
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("#Rules for decision = "+array.getDecValuesStr()[decValIndex]).append('\n');
        for(int i=0;i<complexSetArray.length;i++)
        {
            if(complexSetArray[i]!=null)
                tmp.append(complexSetArray[i].toString(selectorsList,array));
        }		
        return tmp.toString();
    }
//  **********************************************************
//  method saves ruleset to file 
    public void save(SelectorList selectorsList,FArray array,String fileName)
    {
        FileWriter file;
        try{
            file= new FileWriter(fileName+".rls");
        }
        catch(IOException ex){
            System.err.println("Error opening file. File: "+fileName+".rls");
            return;
        }
        try {
            file.write(toString(selectorsList,array));
            file.close();
        } catch (IOException e) {
            System.err.println("Error writing file. File: "+fileName+".rls");
            e.printStackTrace();
        }		
    }	
//  *****************************************
//  method tests an event
    public double calcScore(SelectorList selectorsList,FArray array,int eventIndex)
    {
        return complexLinks.calcScore(selectorsList, array, eventIndex,adxParams);
    }
//  **********************************************************
    public boolean covers(SelectorList selectorsList,FArray array,int eventIndex)
    {
        for(int i=0;i<complexSetArray.length;i++)
        {
            if(complexSetArray[i]!=null)
            {
                final int size=complexSetArray[i].size();//for speed
                for(int j=0;j<size;j++)
                    if(complexSetArray[i].getComplex(j).covers(selectorsList,array,eventIndex))
                        return true;
            }
        }
        return false;
    }
//  **********************************************************
    protected int getComplexesNumber()
    {
        int globalComplexNumber=0;
        for(int i=0;i<complexSetArray.length;i++)
            if(complexSetArray[i]!=null)
                globalComplexNumber+=complexSetArray[i].size();
        return globalComplexNumber;
    }
//  **********************************************************
    public boolean createLinks(ADXParams adxParams)
    {
        ComplexLink links[]=new ComplexLink[getComplexesNumber()];
        int link=0;
        for(int i=0;i<complexSetArray.length;i++)
        {
            if(complexSetArray[i]!=null)
            {
                final int size=complexSetArray[i].size();
                for(int j=0;j<size;j++)
                {
                    Complex complex=complexSetArray[i].getComplex(j);
                    links[link++]=new ComplexLink(complex,i,j,complexSetArray[i].getComplex(j).calcQuality(adxParams.qMethodFinal));
                }
            }
        }
        complexLinks = new ComplexLinks(links);
        return true;
    }
//  **********************************************************
    public ComplexLinks getLinks()
    {
        return complexLinks;
    }
    //********************************************************
    public boolean commitLinks()
    {
        boolean iterateSelected=false;
        complexLinks.initIterator(iterateSelected);
        ComplexLink complexLink;
        while((complexLink=complexLinks.getNext())!=null)
        {          
            //delete complexes that have not been selected
            //System.out.println("delete: "+complexLink.toString());
            complexSetArray[complexLink.setIndex].removeComplex(complexLink.complexIndex);                
        }            
        trimLists();
        return true;
    }
//************************************************************    
    public boolean trimLists()
    {
        for(int i=0;i<complexSetArray.length;i++)
            if(complexSetArray[i]!=null)
                complexSetArray[i].trimList();
        return true;
    }
//  **********************************************************
//  method deletes complexes that have quality less than minQuality 
    public int deleteComplexesQLess(double minQuality)
    {
        int removed=0;
        for(int i=0;i<complexSetArray.length;i++)
        {
            if(complexSetArray[i]!=null)
                removed+=complexSetArray[i].deleteComplexesQLess(minQuality);
            if(complexSetArray[i]!=null && complexSetArray[i].size()==0)
                complexSetArray[i]=null;
        }
        return removed;
    }
//  **********************************************************
    public int deleteComplexesPosLessNeg()
    {
        int removed=0;
        for(int i=0;i<complexSetArray.length;i++)
        {
            if(complexSetArray[i]!=null)
            {
                removed+=complexSetArray[i].deleteComplexesPosLessNeg();
            }
            if(complexSetArray[i]!=null && complexSetArray[i].size()==0)
                complexSetArray[i]=null;
        }
        return removed;
    }
    //	*****************************************
    public int deleteComplexesCoversNeg()
    {
        int removed=0;
        for(int i=0;i<complexSetArray.length;i++)
        {
            if(complexSetArray[i]!=null)
            {
                complexSetArray[i].deleteComplexesCoversNeg();
                removed++;
            }
            if(complexSetArray[i]!=null && complexSetArray[i].size()==0)
                complexSetArray[i]=null;
        }
        return removed;
    }
//  *****************************************
    public double getSignificantQuality(int searchBeam,int qMethod)
    {		
        double significantQ[]=new double[searchBeam];
        Arrays.fill(significantQ,Double.NEGATIVE_INFINITY);
        
        int minValuePosition=significantQ.length;//the last one
        
        for(int j=0;j<complexSetArray.length;j++)
        {
            if(complexSetArray[j]==null)
                continue;
            final int listSize=complexSetArray[j].size();
            for(int i=0;i<listSize;i++)
            {
                final Complex complex=complexSetArray[j].getComplex(i);
                if(complex.reproduce)
                {
                    final double complexQuality=complex.calcQuality(qMethod);					
                    if(minValuePosition>0)
                        significantQ[--minValuePosition]=complexQuality;
                    else if(significantQ[0] < complexQuality)
                    {
                        significantQ[0]=complexQuality;
                        Arrays.sort(significantQ);
                    }
                }
            }
        }
        
        if(minValuePosition>0)
            Arrays.sort(significantQ);
        
        return significantQ[minValuePosition]; //here is the minimal value to take
    }
//  *****************************************
}
