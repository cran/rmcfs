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
package dmLab.classifier.adx.ruleParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dmLab.array.FArray;
import dmLab.array.meta.Attribute;
import dmLab.classifier.adx.complex.Complex;
import dmLab.classifier.adx.ruleFamily.RuleFamily;
import dmLab.classifier.adx.selector.Selector;
import dmLab.classifier.adx.selector.SelectorList;
import dmLab.utils.condition.Operator;
import dmLab.utils.helpers.ParsingException;

public class RuleFamilyParser
{
	private RuleFamily ruleFamily;
	private FArray array;
	
	private Selector selector;
	private Complex complex;
	private Operator operator;
	float covs[];
	String[] valuesArray=null;
//************************************************
	public RuleFamilyParser(RuleFamily ruleFamily,FArray eventsArray)
	{
		this.ruleFamily=ruleFamily;
		array=eventsArray;
	}
//	************************************************
	public Complex parseComplex(String rule,int decisionIndex) throws ParsingException
	{
		int endIndex=0;
		int beginIndex=0;
		int covIndex=-1;
		int selectorsNumber=countSelectors(rule);
        
        CovParser covParser=new CovParser();        
		covIndex=covParser.indexOfCovs(rule);		
		complex=new Complex(selectorsNumber);
		
		while(beginIndex<rule.length() && beginIndex<covIndex)
		{
			endIndex=rule.indexOf(" and ",beginIndex);			
			if(endIndex==-1 && covIndex!=-1)
				endIndex=covIndex;
			if(endIndex==-1 && covIndex==-1)
				endIndex=rule.length();
			
			String strSelector=rule.substring(beginIndex,endIndex).trim();
			beginIndex=endIndex+4;			
			if(!loadSelector(strSelector,complex,ruleFamily.getSelectorList(decisionIndex)))
				throw new ParsingException("Error in '"+strSelector+"'");			
		}
		
		if(covIndex!=-1)//load coverages
		{
            String strCoverages=rule.substring(covIndex,rule.length()).trim();            
            System.out.println(strCoverages);
            try {
                covParser.parse(strCoverages);
            } catch (IOException e) {
                throw new  ParsingException("Error in '"+strCoverages+"'");
            }
            System.out.println(covParser.toString());
            complex.posCoverage=covParser.getValue("p:");
            complex.negCoverage=covParser.getValue("n:");
            complex.posSupport=covParser.getValue("ps:");
            complex.negSupport=covParser.getValue("ns:");
            complex.coverage=covParser.getValue("c:");
		}
		
		ruleFamily.getRuleSet(decisionIndex).addComplex(complex);
		return complex;
	}
//************************************************
	private int countSelectors(String rule)
	{
		int count=0;
		int lastIndex=0;		

		while(lastIndex!=-1)
		{
			lastIndex=rule.indexOf(" and ",lastIndex+1);
			if(lastIndex!=-1)
			{
				count++;
				lastIndex++;
			}
		}
		return count+1;//one more
	}
//************************************************
	private Operator parseOperator(String strSelector)
	{
		operator=new Operator();
		int operatorIndex;
		for(int i=Operator.operators.length-1;i>=0;i--)
		{
			//search over all operators
			operatorIndex=strSelector.indexOf( Operator.labels[i] );
			if(operatorIndex!=-1)
			{
				operator.op=Operator.operators[i];
				return operator;
			}
		}
		return null;
	}
	//************************************************
	private String[] parseValues(String strValues)
	{
		String value;
		int stop=0,start=0,begin=0;
		
		ArrayList <String> valuesList=new ArrayList<String>();
		
		start=strValues.indexOf('[');
		if(start==-1)
			start=strValues.indexOf('(');
		stop=strValues.indexOf(']');
		if(stop==-1)
			stop=strValues.indexOf(')');
		
		if( (start!=-1 && stop==-1) || (start==-1 && stop!=-1 ))
		{
			System.err.println("Missing bracket. values: " +strValues);
			return null;
		}
		
		if(start==-1)
			start=0;
		else
			start++;
		
		if(stop==-1)
			stop=strValues.length();
		
		String strList=strValues.substring(start,stop).trim();
		
		int commaIndex=strList.indexOf(';');
		if(commaIndex==-1)
			commaIndex=strList.indexOf(',');
				
		if(commaIndex==-1) //if there is only one value
		{			
			valuesArray=new String[1];
			valuesArray[0]=strList;
		}
		else //if there is list of values or if its a range
		{			
			while(begin<strList.length())
			{
				value=strList.substring(begin,commaIndex).trim();
				if(value.length()>0)
				{
					valuesList.add(value);
					begin=commaIndex+1;
				}
				else
				{
					System.err.println("Incorrect values definition: " +strValues);
					return null;
				}				
				commaIndex=strList.indexOf(",",begin);								
				if(commaIndex==-1)
					commaIndex=strList.length();
			} 			
			valuesArray=new String[valuesList.size()];
			for(int i=0;i<valuesArray.length;i++)
				valuesArray[i]=(valuesList.get(i)).trim();
		}					
		return valuesArray;
	}
//	************************************************
	private boolean loadSelector(String strSelector,Complex complex,SelectorList selectorList) throws ParsingException
	{
		parseSelector(strSelector);
		if(selector==null)
			return false;
		
		//System.out.println(selector.toString(array));
		
		int selectorIndex=selectorList.addSelector(selector);
		complex.addSelectorId(selectorIndex);		
		return true;
	}
//	************************************************	
	public Selector parseSelector(String strSelector) throws ParsingException
	{
		float value;
		String strValues;
		String values[];
		boolean range=false;
		
		operator=parseOperator(strSelector);
		if(operator==null)//ERROR incorrect operator or missing
			throw new  ParsingException("Incorrect operator or missing. Selector: " +strSelector);
		
		int operatorIndex=strSelector.indexOf(operator.toString());
		String attributeName=strSelector.substring(0,operatorIndex).trim();		
		int attributeIndex=array.getColIndex(attributeName);
		if(attributeIndex==-1)
			throw new  ParsingException("Incorrect attribute name. Selector: " +strSelector);		
		
		strValues=strSelector.substring(operatorIndex+operator.toString().length(),strSelector.length()).trim();
		
		if(strValues.indexOf(";")!=-1)
			range=true;		
		
		values=parseValues(strValues);
		if(values==null)
			throw new ParsingException("Error parsing values. Selector: " +strSelector);

		if(range && values.length!=2)
			throw new ParsingException("Incorrect range definition. Two values in range expected. Selector: " +strSelector);

		if(operator.op!=Operator.EQUAL && values.length>1)
				throw new ParsingException("Incorrect range or list definition. Operator '=' expected. Selector: " +strSelector);
		
		selector=new Selector();
		selector.set(range,attributeIndex,operator);
		for(int i=0;i<values.length;i++)
		{
			//check if it is a number
			if(array.attributes[attributeIndex].type==Attribute.NUMERIC)
				value = Float.parseFloat(values[i]);
			else
				value = array.dictionary.toFloat(values[i]);
			selector.addValue(value);
		}
		return selector;
	}
//	*************************************************
	public boolean loadRuleSet(String fileName,int decisionIndex) throws ParsingException
	{
		BufferedReader inputFile=null;
		try{
			inputFile = new BufferedReader(new FileReader(fileName));
		}
		catch (Exception e) {
			System.err.println("Error opening file. File: "+fileName);
			return false;
		}
		String rule;
		try {
			while (null!=(rule=inputFile.readLine()))
				if(!rule.startsWith("#"))
					parseComplex(rule,decisionIndex);
		} catch (IOException e) {
			System.err.println("Error reading file. File: "+fileName);
			e.printStackTrace();
		}
		
		try {
			inputFile.close();
		} catch (IOException e) {
			System.err.println("Error closing file. File: "+fileName);
			e.printStackTrace();
		}		
		return true;	
	}
//	*************************************************
}
