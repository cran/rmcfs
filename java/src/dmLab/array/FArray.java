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
package dmLab.array;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import dmLab.array.domain.ADXDomain;
import dmLab.array.domain.Domain;
import dmLab.array.domain.FDomain;
import dmLab.array.meta.Attribute;
import dmLab.array.meta.Dictionary;
import dmLab.array.meta.DiscRanges;
import dmLab.array.saver.Array2ADX;
import dmLab.utils.ArrayUtils;



public class FArray extends Array
{	
	protected float[][] valuesArray;
	protected float[] decisionValues;
	protected Domain[] domains;

	public Dictionary dictionary;
	public DiscRanges[] discRanges;

	//	********************************************
	public FArray()
	{
		super();
		dictionary = new Dictionary();
		domains = null;
		discRanges = null;
	}
	//	********************************************
	public FArray(int columns, int rows)
	{
		init(columns,rows);
	}
	//	********************************************
	@Override
	public void init(int columns, int rows)
	{
		initAttributes(columns, rows);
		dictionary = new Dictionary();
		valuesArray = new float[columns][rows];
		domains = null;
		discRanges = null;
	}
	//	********************************************
	@Override
	public FArray clone()
	{
		return clone(null, null);
	}
	//	********************************************
	@Override
	public FArray clone(boolean colMask[], boolean[] rowMask)
	{		
		final int srcColumns = colsNumber();
		final int srcRows = rowsNumber();		
		
		if(colMask==null){
			colMask = new boolean[srcColumns];
			Arrays.fill(colMask, true);
		}
		if(rowMask==null){
			rowMask = new boolean[srcRows];
			Arrays.fill(rowMask, true);
		}
		
		final int destColumns = ArrayUtils.count(colMask, true);
		final int destRows = ArrayUtils.count(rowMask, true);
		FArray dstArray = null;
		
		if(srcColumns!=colMask.length)
			System.err.println("Incorrect length of colMask: "+colMask.length);
		else if(srcRows!=rowMask.length)
			System.err.println("Incorrect length of rowMask: "+rowMask.length);
		else{
			int currentRow = 0;
			int currentColumn = 0;

			dstArray = new FArray(destColumns, destRows);		
			if(discRanges!=null)
				dstArray.discRanges = new DiscRanges[destColumns];
			if(domains!=null)
				dstArray.domains = new Domain[destColumns];
			
			//copy attributes discRanges and domains
			for(int i=0; i<srcColumns; i++){
				if(colMask[i]){	
					dstArray.attributes[currentColumn] = attributes[i].clone();					
					if(i == decAttrIdx){
						dstArray.decAttrIdx=currentColumn;
						if(decisionValues!=null)
							dstArray.decisionValues = decisionValues.clone();
					}
					if(discRanges!=null && discRanges[i]!=null)
						dstArray.discRanges[currentColumn] = discRanges[i].clone();
					
					if(domains!=null && domains[i]!=null)
						dstArray.domains[currentColumn] = domains[i].clone();
					
					currentColumn++;
				}				
			}
			
			//copy dictionary
			dstArray.dictionary = dictionary.clone();

			//copy rows
			currentRow=0;
			for(int j=0;j<srcRows;j++){
				currentColumn=0;
				if(rowMask[j]){
					//copy attributes
					for(int i=0; i<srcColumns; i++){
						if(colMask[i]){
							dstArray.valuesArray[currentColumn][currentRow] = valuesArray[i][j]; 
							currentColumn++;
						}
					}
					currentRow++;
				}
			}
			currentColumn=0;
			currentRow=0;
			
			if(dstArray.decAttrIdx == -1){
				//if decision attribute is ignored set on the last one
				dstArray.decAttrIdx = dstArray.colsNumber()-1; 
				dstArray.setAllDecValues();
			}
		}
		return dstArray;
	}
	//	********************************************
	@Override
	//this functions copy values from array! Its not clone()
	public void cbind(Array array)
	{
		if(rowsNumber() != array.rowsNumber()){
			System.err.println("Function cbind cannot be used. Arrays have different numbers of rows.");
			return;
		}
			
		//add new columns to attributes
		bindAttributes(array.attributes);

		FArray srcArray = (FArray)array;

		//extend domains if they are created
		if(domainsCreated())
			domains = new FDomain[attributes.length];
			
		//add new columns to discRanges
		if(discRanges!=null){
			DiscRanges[] discRangesTMP = discRanges;
			discRanges = new DiscRanges[attributes.length];
			for(int j=0, i=0; i<discRanges.length; i++)
				if(i<discRangesTMP.length)
					discRanges[i] = discRangesTMP[i];
				else
					discRanges[i] = srcArray.discRanges[j].clone();
		}

		//add new values - simple copy of float values for numeric and writeStr for nominal
		//I need to keep dictionary consistent with new data valuesArray
		final int rows = rowsNumber();
		float[][] valuesArrayTMP = valuesArray;
		valuesArray = new float[attributes.length][rows];
		for(int j=0, i=0; i<valuesArray.length; i++){
			if(i<valuesArrayTMP.length)
				valuesArray[i] = valuesArrayTMP[i];
			else{
				if(srcArray.attributes[j].type == Attribute.NUMERIC){
					valuesArray[i] = srcArray.valuesArray[j++].clone();
				}else if(srcArray.attributes[j].type == Attribute.NOMINAL){
					for(int k=0;k<rows ;k++)
						writeValueStr(i, k, srcArray.readValueStr(j, k));
					j++;
				}else{
					System.err.println("Function cbind cannot copy the attribute. Type of attribute: "+srcArray.attributes[j].name +" is not NUMERICAL or NOMINAL.");
				}
			}
		}
				
		//find domains for this array if it already had domains obtained 
		if(domains!=null)
			findDomains();
	}
	//	********************************************
	@Override
	public int colsNumber()
	{
		return attributes.length;
	}
	//	********************************************
	@Override
	public int rowsNumber()
	{
		return valuesArray[0].length;
	}		
	//  ********************************************
	public boolean isDiscretized()
	{
		if(discRanges==null)
			return false;
		else
			return true;
	}
	//  ********************************************
	public boolean isDiscretized(int column)
	{
		if(discRanges==null || discRanges[column]==null || discRanges[column].getSize()<=0)
			return false;
		else
			return true;
	}
	//	********************************************
	public float[] getColumn(int column)
	{
		return valuesArray[column];
	}
	
	//	********************************************
	@Override
	public String[] getColumnStr(int column)
	{
		return dictionary.toString(getColumn(column));
	}
	//  ********************************************
	public boolean setColumn(int column, float[] values){
		if(values.length != rowsNumber())
			return false;
		if(column<0 || column>colsNumber())
			return false;

		for(int i=0;i<values.length;i++)
			valuesArray[column][i] = values[i];

		return true;		
	}
	//	********************************************
	public float[] getDecValues()
	{
		return decisionValues;
	}
	//	********************************************
	public void setDecValues(float[] decValues)
	{
		if(decValues != null)
			decisionValues = decValues.clone();
	}	
	//	********************************************
	@Override
	public String[] getDecValuesStr()
	{
		String decValuesStr[] = null;		
		if (attributes[decAttrIdx].type == Attribute.NOMINAL)
			decValuesStr = dictionary.toString(decisionValues);
		else if (attributes[decAttrIdx].type == Attribute.NUMERIC &&
				decisionValues != null){
			decValuesStr = ArrayUtils.float2String(decisionValues);				
		}

		return decValuesStr;
	}	
	//	********************************************
	@Override
	public boolean setDecValues(String[] decValues)
	{
		if(decValues != null){		
			decisionValues = new float[decValues.length];
			if (attributes[decAttrIdx].type == Attribute.NOMINAL)
				decisionValues = dictionary.toFloat(decValues);
			else if (attributes[decAttrIdx].type == Attribute.NUMERIC)
				decisionValues = ArrayUtils.string2float(decValues);
		}
		return true;
	}
	//********************************************
	public float[] getUniqueValues(int column){
		HashSet<Float> set = new HashSet<Float>();		
		final int rows=rowsNumber();
		for(int i=0;i<rows;i++)
			set.add(readValue(decAttrIdx,i));
		
		Float[] floatValues = new Float[1];
		floatValues = set.toArray(floatValues);
		return ArrayUtils.float2Float(floatValues);
	}
	//********************************************
	@Override
	public boolean setAllDecValues()
	{
		if(attributes[decAttrIdx].type == Attribute.NOMINAL)
			decisionValues = getUniqueValues(decAttrIdx);
		else
			decisionValues = null;
		return true;	
	}
	//	********************************************
	public float readValue(int column, int row)
	{
		return valuesArray[column][row];
	}
	// ********************************************
	public void writeValue(int column, int row, float value)
	{
		valuesArray[column][row] = value;
	}
	//	********************************************
	@Override
	public String readValueStr(int column, int row)
	{
		if (attributes[column].type == Attribute.NOMINAL)
			return dictionary.toString(valuesArray[column][row]);
		else if (attributes[column].type == Attribute.NUMERIC)
		{
			String strValue = dictionary.toStringSpecial(valuesArray[column][row]);
			if (strValue != null)
				return strValue;
			else
				return Float.toString(valuesArray[column][row]);
		}
		else
			return null;
	}
	//	********************************************
	@Override
	public boolean writeValueStr(int column, int row, String value)
	{
		if (attributes[column].type == Attribute.NOMINAL)
			valuesArray[column][row] = dictionary.toFloat(value);
		else if (attributes[column].type == Attribute.NUMERIC)
		{
			Float floatValue = dictionary.toFloatSpecial(value);
			if (floatValue == null){
				try{
					floatValue = Float.parseFloat(value);
				}catch (NumberFormatException e){
					System.err.println("Error parsing float value: " + value);
					return false;
				}
			}
			valuesArray[column][row] = floatValue;
		}
		return true;
	}
	//	********************************************
	public boolean checkDecisionValues()
	{
		if (decAttrIdx == -1){
			System.err.println("Decision Attribute is not Defined!");
			return false;
		}
		
		if(attributes[decAttrIdx].type != Attribute.NOMINAL){
			decisionValues = null;
			return true;
		}
		
		Float[] uniqueValues = ArrayUtils.float2Float(getUniqueValues(decAttrIdx));
		HashSet<Float> set = new HashSet<Float>();
		Collections.addAll(set, uniqueValues);
		
		for (int i = 0; i < decisionValues.length; i++){
			if(!set.contains(decisionValues[i])){
				System.err.println("Decision Value is not Present in the Dataset! Value: " + dictionary.toString(decisionValues[i]));
				return false;				
			}
		}		
		return true;
	}
	//********************************************	
	@Override
	public boolean findDomains()
	{
		domains = new FDomain[attributes.length];
		for(int i=0;i<attributes.length;i++){
			if(attributes[i].type == Attribute.NOMINAL){
				domains[i] = new FDomain();				
				((FDomain)domains[i]).createDomain(getColumn(i));
				if(debug) 
					System.out.println("### DEBUG INFO\n"+domains[i].toString());
			}
		}
		return true;
	}
	//********************************************	
	public boolean findADXDomains()
	{
		domains = new ADXDomain[attributes.length];
		for(int i=0;i<attributes.length;i++){
				domains[i] = new ADXDomain();				
				if(isDiscretized(i))
					((ADXDomain)domains[i]).createDomain(discRanges[i].getRanges(), getColumn(i), getColumn(decAttrIdx), decisionValues);
				else
					((ADXDomain)domains[i]).createDomain(null, getColumn(i), getColumn(decAttrIdx), decisionValues);
	
				if(debug)
					System.out.println("### DEBUG INFO\n"+domains[i].toString());
		}
		return true;
	}
	//********************************************
	public void setDomain(int column, Domain domain){
		if(domains!=null)
			domains[column] = domain;
	}
	//********************************************
	public FDomain getFDomain(int column)
	{
		if(domains==null || domains[column]==null)
			return null;		
		else if(domains[column] instanceof FDomain)
			return (FDomain)domains[column];
		else
			return null;
	}
	//********************************************
	public ADXDomain getADXDomain(int column)
	{
		if(domains==null || domains[column]==null)
			return null;		
		else if(domains[column] instanceof ADXDomain)
			return (ADXDomain)domains[column];
		else
			return null;
	}
	//********************************************
	@Override
	public String[] getDomainStr(int column)
	{				
		if(domains==null || domains[column]==null)
			return null;
		
		String[] domainStrValues = dictionary.toString(domains[column].getDomainValues());

		return domainStrValues;
	}
	//********************************************
	@Override
	public boolean domainsCreated(){
		if(domains==null)
			return false;
		else
			return true;
	}	
	//********************************
	@Override
	public void swapColumns(int source, int destination)
	{		
		Attribute attributeTmp = attributes[source];
		attributes[source]=attributes[destination];
		attributes[destination] = attributeTmp;
		
		if(isDiscretized()){
			DiscRanges discRangesTmp = discRanges[source];			
			discRanges[source]=discRanges[destination];
			discRanges[destination] = discRangesTmp;										
		}

		if(domainsCreated()){
			Domain domainTmp = domains[source];			
			domains[source] = domains[destination];
			domains[destination] = domainTmp;										
		}
	
		float eventsArrayTmp[] = valuesArray[source];
		valuesArray[source]=valuesArray[destination];
		valuesArray[destination] = eventsArrayTmp;

		if(decAttrIdx == source) 
			decAttrIdx = destination;
		else if(decAttrIdx == destination) 
			decAttrIdx = source;
	}
	//  ********************************************
	public String toString()
	{
		Array2ADX container2ADX = new Array2ADX();
		return container2ADX.toString(this);
	}
	//  ********************************************
	public String info(){
		return "attr: "+colsNumber()+" events: "+rowsNumber();
	}
	//  ********************************************
}
