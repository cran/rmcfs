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
package dmLab.classifier.adx;

import java.util.Arrays;
import java.util.Properties;

import dmLab.array.FArray;
import dmLab.classifier.Params;
import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.complex.ComplexSet;
import dmLab.classifier.adx.ruleFamily.RuleFamily;
import dmLab.utils.ArrayUtils;

public class ADXParams extends Params
{
	public int searchBeam;//how wide is beam of searching
	protected int finalBeam[];// //how many rules to leave finally
	public int complexGenerality;// 0:longer complexes  1:shorter complexes
	public int scoreMethod;// 0:[(p-n)/c]/[(P-N)/C]  1:(p-n)/(P-N)  2:(p/P)*(N/n) 3:maxQ 4:maxQ/MAXQ
    public int scoreQ;
	public int qMethod;// 0:p-n	1:(p-n)*(1-n) 2:(p-n)*sqrt(1-n)
	public int selSignificantMethod;// 0:del n<>0  1:del p<n   2:leave finalBeam number of best rules by qMethod 3: with qMin
	public int qMethodFinal;
	public double qMin;
	public int useSensitivity;
    protected double sensitivityArray[];
	public boolean keepMinimalSet;
	public int mergeCondition;
	public int cleanCandidates;
    public int maxEventsForSelection;
	
//	*************************************
	public ADXParams()
	{
		super();
	}
//	*************************************
	@Override
    public boolean setDefault()
	{
		searchBeam=1000;//how wide is beam of searching
		finalBeam=new int[1];// //how many rules to leave finally
		finalBeam[0]=50;
		complexGenerality=0;// 0:longer complexes  1:shorter complexes
		scoreMethod=5;// 0:[(p-n)/c]/[(P-N)/C]  1:(p-n)/(P-N)  2:(p/P)*(N/n) 3:maxQ 4:maxQ/MAXQ 5:(p/P)*(1-n/N)
        scoreQ=1;
		selSignificantMethod=1;// 0:del n<>0  1:del p<n   2:leave finalBeam number of best rules by qMethod 3: with qMin
		qMethod=1;// 0:p-n  1:(p-n)*(1-n) 2:(p-n)*sqrt(1-n)
		qMethodFinal=2;
		qMin=0.2;
        useSensitivity=RuleFamily.SENSITIVITY_NO;
		sensitivityArray=new double[1];
		sensitivityArray[0]=1.0;
		keepMinimalSet=true;            
		mergeCondition=Complex.MERGE_IF_QUALITY_INCREASED;
		cleanCandidates=ComplexSet.REMOVE_IF_WORSE_THAN_PARENTS_Q;
        maxEventsForSelection=3000;
        
	    return true;
	}
//	*************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("### ADX Parameters ### ").append('\n');
		tmp.append("adx.complexGenerality="+ complexGenerality).append('\n');
		tmp.append("adx.searchBeam="+ searchBeam).append('\n');
		tmp.append("adx.finalBeam="+Arrays.toString(finalBeam)).append('\n');
		tmp.append("adx.qMethod="+qMethod).append('\n');
		tmp.append("adx.qMethodFinal="+qMethodFinal).append('\n');
		tmp.append("adx.qMin="+ qMin).append('\n');
		tmp.append("adx.selSignificantMethod="+ selSignificantMethod).append('\n');
		tmp.append("adx.scoreMethod="+ scoreMethod).append('\n');
        tmp.append("adx.scoreQ="+ scoreQ).append('\n');        
		tmp.append("adx.useSensitivity="+ useSensitivity).append('\n');
		tmp.append("adx.sensitivityArray="+Arrays.toString(sensitivityArray)).append('\n');
        tmp.append("adx.maxEventsForSelection="+ maxEventsForSelection).append('\n');                
		tmp.append("adx.keepMinimalSet="+ keepMinimalSet).append('\n');
		tmp.append("adx.cleanCandidates="+ cleanCandidates).append('\n');
        tmp.append("adx.mergeCondition="+ mergeCondition).append('\n');
		tmp.append(super.toString());
		
		return tmp.toString();
	}
//	*************************************
	@Override
    protected boolean update(Properties properties)
	{
		searchBeam=Integer.valueOf(properties.getProperty("adx.searchBeam", "50")).intValue();
		String arrayStr=properties.getProperty("adx.finalBeam", "[50]");
		arrayStr=arrayStr.substring(arrayStr.indexOf('[')+1,arrayStr.indexOf(']'));
		finalBeam=ArrayUtils.toIntArray(arrayStr);
		complexGenerality=Integer.valueOf(properties.getProperty("adx.complexGenerality", "0")).intValue();
		scoreMethod=Integer.valueOf(properties.getProperty("adx.scoreMethod", "6")).intValue();
        scoreQ=Integer.valueOf(properties.getProperty("adx.scoreQ", "1")).intValue();        
		qMethod=Integer.valueOf(properties.getProperty("adx.qMethod", "1")).intValue();
		qMethodFinal=Integer.valueOf(properties.getProperty("adx.qMethodFinal", "2")).intValue();
		selSignificantMethod=Integer.valueOf(properties.getProperty("adx.selSignificantMethod", "4")).intValue();
		qMin=Double.valueOf(properties.getProperty("adx.qMin", "0.2")).doubleValue();
        useSensitivity=Integer.valueOf(properties.getProperty("adx.useSensitivity","0")).intValue();
		arrayStr=properties.getProperty("adx.sensitivityArray", "[1]");
		arrayStr=arrayStr.substring(arrayStr.indexOf('[')+1,arrayStr.indexOf(']'));
		sensitivityArray=ArrayUtils.toDoubleArray(arrayStr);
        maxEventsForSelection=Integer.valueOf(properties.getProperty("adx.maxPosForSelection","3000")).intValue();
		keepMinimalSet=Boolean.valueOf(properties.getProperty("adx.keepMinimalSet", "true")).booleanValue();        
		cleanCandidates=Integer.valueOf(properties.getProperty("adx.cleanCandidates", "2")).intValue();
        mergeCondition=Integer.valueOf(properties.getProperty("adx.mergeCondition", "0")).intValue();
		return true;
	}
//  *****************************************
    public double getSensitivity(int ruleSetIndex)
    {
        if(finalBeam.length>1)
            return sensitivityArray[ruleSetIndex];
        else                             
            return sensitivityArray[0];
    }
//  *****************************************
    public int getFinalBeam(int ruleSetIndex)
    {
        if(finalBeam.length>1)
            return finalBeam[ruleSetIndex];
        else                             
            return finalBeam[0];
    }
//  *****************************************
    public int[] getFinalBeam()
    {
        return finalBeam;
    }
//  *****************************************
    public void setFinalBeam(int beam)
    {
        for(int i=0;i<finalBeam.length;i++)
            finalBeam[i]=beam;
    }    
//	*****************************************
	@Override
    public boolean check(FArray array)
	{
        if(finalBeam.length==1)
		{
			int val=finalBeam[0];
			finalBeam=new int[array.getDecValues().length];
			for(int i=0;i<finalBeam.length;i++)
				finalBeam[i]=val;
		}
        if(sensitivityArray.length==1)
        {
            double val=sensitivityArray[0];
            sensitivityArray=new double[array.getDecValues().length];
            for(int i=0;i<sensitivityArray.length;i++)
                sensitivityArray[i]=val;
        }

		if(array.getDecValues().length!=finalBeam.length)
		{
			System.err.println("Size of finalBeam array is different than decision values number!");
			return false;
		}
		
		for(int i=0;i<finalBeam.length;i++)
			if(finalBeam[i]<1)
			{
				System.err.println("Size of finalBeam for value "+i+" is < 1!");
				return false;
			}
		
		if(useSensitivity==RuleFamily.SENSITIVITY_ARRAY && sensitivityArray.length!=1 
                && array.getDecValues().length!=sensitivityArray.length)
		{
			System.err.println("Longest of sensitivityArray array is different than decision values number!");
			return false;
		}
		if(scoreMethod<0 || scoreMethod>7)
		{
			System.err.println("Incorrect scoreMethod.");
			return false;
		}
		if(qMethod<0 || qMethod>5)
		{
			System.err.println("Incorrect qMethod.");
			return false;
		}
		if(qMethodFinal<0 || qMethodFinal>5)
		{
			System.err.println("Incorrect qMethodFinal.");
			return false;
		}
		if(selSignificantMethod<0 || selSignificantMethod>4)
		{
			System.err.println("Incorrect selSignificantMethod.");
			return false;
		}
		if(cleanCandidates!=ComplexSet.REMOVE_NO 
                && cleanCandidates!=ComplexSet.REMOVE_IF_WORSE_THAN_SELECTORS_Q 
                && cleanCandidates!=ComplexSet.REMOVE_IF_WORSE_THAN_PARENTS_Q )
		{
			System.err.println("Incorrect cleanCandidates.");
			return false;
		}
        
        if(mergeCondition!=Complex.MERGE_NO
                && mergeCondition!=Complex.MERGE_ALWAYS 
                && mergeCondition!=Complex.MERGE_IF_QUALITY_INCREASED)
        {
            System.err.println("Incorrect mergeCondition.");
            return false;
        }
        if(maxEventsForSelection<1)
        {
            System.err.println("Incorrect maxPosForSelection. (maxPosForSelection<1)");
            return false;
        }
		for(int i=0;i<sensitivityArray.length;i++)
		{
			if(sensitivityArray[i]>1 ||sensitivityArray[i]<0)
			{
				System.err.println("Sensitivity must be between 0 and 1");
				return false;
			}
		}
		return true;
	}
//	*****************************************
}
