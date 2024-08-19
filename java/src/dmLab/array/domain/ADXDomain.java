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
package dmLab.array.domain;

public class ADXDomain extends Domain implements Cloneable
{
	protected ADXDomainSet domainSet;
	protected float decValues[];
	protected int totalPosNumber[];
	protected int rows;
	
	public final static int random=0;
	public final static int infoGain=4;
	public final static int gainRatio=5;
	public final static int giniIndex=6;
	//********************************
	public ADXDomain()
	{		
	}
//	********************************	
	@Override
	public boolean isCreated()
	{
		if(domainSet==null)
			return false;
		else
			return true;
	}
//	********************************
	public boolean createDomain(float[] discRanges, float[] column, float[] decColumn, float decValues[])
	{
		domainSet=new ADXDomainSet();
		this.decValues = decValues;
		rows = column.length;
		totalPosNumber = new int[decValues.length];
		
		if(column.length!=decColumn.length){
			System.err.print("Size of column is diffrent than size of decColumn!");
			return false;
		}
		
		if(discRanges!=null){
			//for nominal attributes and numeric that are not discretized
			for(int i=0;i<discRanges.length;i++)
				domainSet.addValue(new ADXDomainValue(discRanges[i], decValues.length));				
		}
		
		for(int i=0;i<rows;i++)
		{			
			ADXDomainValue domainValue = domainSet.getDomainValue(column[i]);
			if(domainValue==null){
				domainValue=new ADXDomainValue(column[i], decValues.length);
				domainSet.addValue(domainValue);				
			}				
			final int decisionIndex=getDecisionIndex(decColumn[i]);
			if(decisionIndex!=-1)//if decision[i] is not in decisionValues[]
			{
				domainValue.incrementPosNumber(decisionIndex);
				totalPosNumber[decisionIndex]++;
			}
			else
				domainValue.incrementCoveredNumber();
		}						
		return true;
	}
//	*******************************************************
	public void setDomainValues(float[] values)
	{
		domainSet=new ADXDomainSet();
		for(int i=0;i<values.length;i++){
			domainSet.addValue(new ADXDomainValue(values[i], decValues.length));
		}
	}
//	*******************************************************
	public float[] getDomainValues()
	{
		return domainSet.getValues();
	}
//  ********************************    
    public boolean contains(float value)
    {
        return domainSet.contains(value);
    }
//	********************************
	public int getTotalPositives(int decisionIndex)
	{
		return totalPosNumber[decisionIndex];
	}
//	*******************************************************
	public int getTotalNegatives(int decisionIndex)
	{
		return rows-totalPosNumber[decisionIndex];
	}
//  ********************************
    public float posSupport(int valueIndex,int decisionIndex)
    {
        return domainSet.getDomainValue(valueIndex).getPosNumber(decisionIndex);        
    }
//  *******************************************************
    public float negSupport(int valueIndex,int decisionIndex)
    {
        return domainSet.getDomainValue(valueIndex).getNegNumber(decisionIndex);
    }
//	********************************
//	***** function calculates p/P for multi decision - coverage of positive events
	public float posCoverage(int valueIndex,int decisionIndex)
	{
		return (float)domainSet.getDomainValue(valueIndex).getPosNumber(decisionIndex)/(float)totalPosNumber[decisionIndex];		
	}
//	*******************************************************
//	function calculates n/N for multi decision - coverage of negative events
	public float negCoverage(int valueIndex,int decisionIndex)
	{
		return domainSet.getDomainValue(valueIndex).getNegNumber(decisionIndex)/((float)rows-(float)totalPosNumber[decisionIndex]);
	}
//	*******************************************************
//	function calculates coverage for multi decision
	public float coverage(int valueIndex,int decisionIndex)
	{
		float sum=(float)domainSet.getDomainValue(valueIndex).getPosNumber(decisionIndex)+
			(float)domainSet.getDomainValue(valueIndex).getNegNumber(decisionIndex);
		
		return sum/rows;		
	}
//	********************************
	public float getValue(int valueIndex)
	{
		return domainSet.getDomainValue(valueIndex).value;
	}
//	********************************
	private int getDecisionIndex(float decisionValue)
	{
		for(int i=0;i<decValues.length;i++)
			if(decValues[i]==decisionValue)
				return i;
		return -1;
	}
//  ********************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append(domainSet.toString());
		for(int i=0;i<totalPosNumber.length;i++)
		{
			tmp.append("totalPos: "+totalPosNumber[i]).append('\t');
			tmp.append("totalNeg: "+(rows-totalPosNumber[i])).append('\n');
		}
		tmp.append("#decision: "+decValues.length);
		return tmp.toString();
	}
//	***********************************
	public boolean sort()
	{
		return domainSet.sort();
	}
//	***********************************
	public int size()
	{
		return 	domainSet.size();
	}
	//************************************
	public int decisionsNumber()
	{
		return decValues.length;
	}
//	***************************************
	public int[] getTotalPosNumber()
	{
		return totalPosNumber;
	}
//	***************************************
//	****** method calculates one of the quality of attribute
//	***** information gain, gain ratio, gini index
	public float calculateQuality(int method)
	{
		if(domainSet.size()==0)
			return -1;
		else if(method==random)
			return calculate_Random();
		else if(method==infoGain)
			return calculate_InfoGain();
		else if(method==gainRatio)
			return calculate_GainRatio();
		else if(method==giniIndex)
			return calculate_Gini();
		else
			return -1;
	}
//	************************************
//	***** calculation of weight #4
	public float calculate_InfoGain()
	{
		float I,infoGain=0;
		float entropyResult=0;
		final int size=size();
		for(int valueIndex=0;valueIndex<size;valueIndex++)
			entropyResult+= entropyForValue(valueIndex)*(domainSet.getDomainValue(valueIndex).coveredEvents()/(float)rows);
		I=calcI();
		infoGain=(I-entropyResult);
		return infoGain;
	}
//	*******************************************************
//	***** calculation of weight #5
	public float calculate_GainRatio()
	{
		float IV=0,gainRatio=0;
		float entropyResult=calculate_InfoGain();
		final int size=size();
		
		for(int valueIndex=0;valueIndex<size;valueIndex++)
		{
			float total=domainSet.getDomainValue(valueIndex).coveredEvents();
			if(total!=0)
				IV-=(total/rows )*Math.log(total/rows);
		}
		if(IV==0)
			gainRatio=0;
		else
			gainRatio+=(entropyResult/IV);
		
		return gainRatio;
	}
//	*******************************************************
//	***** calculation of giniIndex
	public float calculate_Gini()
	{    //(p*n)
		float gini=0;
		final int size=size();
		
		for(int valueIndex=0; valueIndex<size; valueIndex++)
			gini+=(domainSet.getDomainValue(valueIndex).coveredEvents()/(float)rows)*giniForValue(valueIndex);
		return gini;
	}
//	*******************************************************
//	***** calculation of weight #7
	public float calculate_Random()
	{ //this is stupid selection based on randomization
		//it is implemented only for comparison to other more sofisticated methods
		//if they are better than random selection
		return (float)Math.random();
	}
//	*******************************************************
//	**** function calculates enthrophy for one value
	private float entropyForValue(int valueIndex)
	{
		float entropy=0;
		
		float total=domainSet.getDomainValue(valueIndex).coveredEvents();
		for(int decIndex=0;decIndex<decValues.length;decIndex++)
		{
			float classPos=domainSet.getDomainValue(valueIndex).getPosNumber(decIndex);
			if(classPos!=0)
				entropy-=classPos/total*Math.log(classPos/total);
		}
		return entropy;
	}
//	*******************************************************
//	**** function calculates enthropy for decision attribute
	private float calcI()
	{
		float I=0;
		for(int decIndex=0;decIndex<decValues.length;decIndex++)
		{
			float pos=totalPosNumber[decIndex];
			if(pos!=0.0)
				I-=pos/rows* Math.log(pos/rows);
		}
		return I;
	}
//	*******************************************************
//	***** function calculates p*n for multi decision
	private float giniForValue(int valueIndex)
	{		
		 float gini=1;
		 final float decisions=decValues.length;
		 float total=domainSet.getDomainValue(valueIndex).coveredEvents();
		 
		 for(int decIndex=0;decIndex<decisions;decIndex++)
		 {
			 total=(float) Math.pow(total/decisions,decisions);
			 float classPos=domainSet.getDomainValue(valueIndex).getPosNumber(decIndex);
			 gini*=classPos;
		 }
		 return 1.0f-(gini/total);		 
	}
//	*******************************************************
	public boolean getData(float valuesArray[],int positivesArray[],int negativesArray[],int decisionIndex)
	{
		final int size=domainSet.size();
		if(valuesArray.length!=size && positivesArray.length!=size && negativesArray.length!=size)
		{
			System.err.println("Size of array is different than domain size.");
			return false;
		}
		
		for(int i=0;i<size;i++)
		{
			ADXDomainValue domainValue=domainSet.getDomainValue(i);
			valuesArray[i]=domainValue.value;
			positivesArray[i] = domainValue.getPosNumber(decisionIndex);
			negativesArray[i] = domainValue.getNegNumber(decisionIndex);;
		}
		return true;
	}
//	*******************************************************
	@Override
	public ADXDomain clone(){
        ADXDomain domain = new ADXDomain();
        domain.domainSet = domainSet.clone();        
        domain.decValues = decValues.clone();
        domain.totalPosNumber = totalPosNumber.clone();
        domain.rows = rows;                		
		return domain;
	}
//	*******************************************************
}
