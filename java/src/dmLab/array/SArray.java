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
package dmLab.array;

import java.util.Arrays;

import dmLab.array.domain.SDomain;
import dmLab.array.meta.Attribute;
import dmLab.utils.ArrayUtils;

public class SArray extends Array
{
	private String valuesArray[][];
	private String decisionValues[];
	
	public SDomain domains[];
	//********************************	
	public SArray()
	{
		super();
		domains = null;
	}
	//********************************
	public SArray(int columns,int rows)
	{
		this();
		init(columns,rows);
	}
	//********************************
	@Override
	public SArray clone()
	{
		return clone(null, null);
	}
	//********************************
	@Override
	public SArray clone(boolean colMask[], boolean rowMask[])
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
		SArray dstArray = null;

		if(srcColumns!=colMask.length)
			System.err.println("Incorrect length of colMask: "+colMask.length);
		else if(srcRows!=rowMask.length)
			System.err.println("Incorrect length of eventIgnoreMask: "+rowMask.length);
		else
		{
			int currentRow = 0;
			int currentColumn = 0;

			dstArray = new SArray(destColumns, destRows);
			if(domains!=null)
				dstArray.domains = new SDomain[destColumns];

			//copy attributes
			for(int i=0; i<srcColumns; i++){
				if(colMask[i]){	
					dstArray.attributes[currentColumn]=attributes[i].clone();					
					if(i==decAttrIdx){
						dstArray.decAttrIdx = currentColumn;
						dstArray.decisionValues = decisionValues.clone();
					}
					if(domains!=null && domains[i]!=null)
						dstArray.domains[currentColumn] = domains[i].clone();

					currentColumn++;
				}				
			}
			
			//copy rows
			currentRow=0;
			for(int j=0;j<srcRows;j++)
			{
				currentColumn=0;
				if(rowMask[j]){
					//copy attributes
					for(int i=0;i<srcColumns;i++){
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
	//********************************	
	@Override
	public void cbind(Array array)
	{
		if(rowsNumber() != array.rowsNumber()){
			System.err.println("Function cbind cannot be used. Arrays have different numbers of rows.");
			return;
		}
			
		//add new columns to attributes
		bindAttributes(array.attributes);

		SArray srcArray = (SArray)array;

		//extend domains if they are created
		if(domainsCreated())
			domains = new SDomain[attributes.length];
			
		//add new values - simple copy of String values
		final int rows = rowsNumber();
		String[][] valuesArrayTMP = valuesArray;
		valuesArray = new String[attributes.length][rows];
		for(int j=0, i=0; i<valuesArray.length; i++){
			if(i<valuesArrayTMP.length)
				valuesArray[i] = valuesArrayTMP[i];
			else
					valuesArray[i] = srcArray.valuesArray[j++].clone();
		}
		
		//find domains for this array if it already had domains obtained 
		if(domains!=null)
			findDomains();
	}
	//********************************
	@Override
	public int colsNumber()
	{
		return attributes.length;
	}	
	//********************************
	@Override
	public int rowsNumber()
	{
		return valuesArray[0].length;
	}
	//********************************
	@Override
	public String[] getColumnStr(int attributeIndex)
	{
		return valuesArray[attributeIndex];
	}
	//********************************
	@Override
	public void init(int columns, int rows)
	{
		initAttributes(columns, rows);
		valuesArray = new String[columns][rows];
		domains=new SDomain[columns];           
	}
	//********************************
	@Override
	public String readValueStr(int column, int row)
	{
		return valuesArray[column][row];
	}
	//********************************
	@Override
	public boolean writeValueStr(int column, int row, String value)
	{
		valuesArray[column][row]=value;
		return true;
	}
	//********************************
	@Override
	public boolean setDecValues(String[] decisionValues)
	{
		this.decisionValues = decisionValues.clone();
		return true;
	}
	//********************************
	@Override
	public String[] getDecValuesStr()
	{
		return decisionValues;
	}
	//********************************
	@Override
	public boolean setAllDecValues()
	{		
		SDomain domain=new SDomain(valuesArray[decAttrIdx]);
		setDecValues(domain.getDomain()[0]);
		return true;
	}
	//********************************
	@Override
	public boolean findDomains()
	{
		domains = new SDomain[colsNumber()];
		for(int i=0;i<domains.length;i++){
			domains[i]=new SDomain(valuesArray[i]);
			domains[i].sort();
		}
		return true;
	}
	//********************************
	@Override
	public boolean domainsCreated(){
		if(domains==null)
			return false;
		else
			return true;
	}
	//********************************
	@Override
	public String[] getDomainStr(int column)
	{
		if(domains[column]==null)
			domains[column]=new SDomain(valuesArray[column]);

		return domains[column].getDomain()[0];
	}
	//********************************
	@Override
	public void swapColumns(int srcColumn, int dstColumn)
	{
		String eventsArrayTmp[]=valuesArray[srcColumn];
		SDomain domainTmp=domains[srcColumn].clone();
		Attribute attributeTmp=attributes[srcColumn].clone();

		valuesArray[srcColumn]=valuesArray[dstColumn];
		domains[srcColumn]=domains[dstColumn];
		attributes[srcColumn]=attributes[dstColumn];

		valuesArray[dstColumn]=eventsArrayTmp;
		domains[dstColumn]=domainTmp;
		attributes[dstColumn]=attributeTmp;        
	}
	//********************************
}
