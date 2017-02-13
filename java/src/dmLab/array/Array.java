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

import dmLab.array.meta.Attribute;


public abstract class Array implements Cloneable
{
	public static String SPACE_CHAR="_";

	public Attribute[] attributes;
	protected int decAttrIdx;

	public boolean debug=false;
	//********************************
	public Array()
	{
		attributes = null;
		decAttrIdx = -1;
	}
	//********************************
	public abstract void init(int cols, int rows);
	//********************************
	protected void initAttributes(int cols, int rows)
	{
		decAttrIdx = -1;
		attributes = new Attribute[cols];
		for (int i = 0; i < attributes.length; i++){
			attributes[i] = new Attribute();
		}
	}
	//	********************************************
	public abstract Array clone();
	//	********************************************
	public abstract Array clone(boolean colMask[], boolean rowMask[]);
	//	********************************************
	public int getDecAttrIdx()
	{
		return decAttrIdx;
	}
	//	********************************************
	public boolean setDecAttrIdx(int col)
	{
		if (col < 0 || col >= colsNumber())
			return false;
		else{
			decAttrIdx = col;
			setAllDecValues();
		}
		return true;
	}
	//  ********************************************
	public String[] getColNames(boolean includeDecision)
	{		
		final int colNumber=colsNumber();		
		String[] colNames = new String[colNumber];
		int decIndex=getDecAttrIdx();
		if(includeDecision){
			decIndex = -1;
		}else{
			colNames = new String[colNumber-1];
		}

		for(int i=0,j=0;i<colNumber;i++){
			if(i!=decIndex){
				colNames[j++] = attributes[i].name;
			}
		}
		return colNames;
	}
	//	********************************************
	//	**** return index of column by given name, returns -1 if column is not present
	//TODO add HashMap here?
	public int getColIndex(String colName)
	{
		int index = -1;
		for (int i = 0; i < attributes.length; i++){
			if (attributes[i].name.equalsIgnoreCase(colName)){
				index = i;
				break;
			}
		}
		return index;
	}
	//********************************
	protected void bindAttributes(Attribute[] srcAttributes){
		Attribute[] attributesTMP = attributes;
		attributes = new Attribute[attributes.length + srcAttributes.length];
		for(int j=0, i=0; i<attributes.length; i++)
			if(i<attributesTMP.length)
				attributes[i] = attributesTMP[i];
			else
				attributes[i] = srcAttributes[j++];
	}
	//********************************
	public abstract void cbind(Array array);
	//********************************
	public abstract int rowsNumber();
	//********************************
	public abstract int	colsNumber();
	//********************************
	public abstract boolean setAllDecValues();
	//********************************	
	public abstract boolean setDecValues(String[] decisionValues);
	//********************************
	public abstract String[] getDecValuesStr();
	//********************************
	public abstract String readValueStr(int col, int row);
	//********************************
	public abstract boolean writeValueStr(int col, int row, String value);
	//********************************
	public abstract boolean findDomains();
	//********************************
	public abstract boolean domainsCreated();
	//********************************
	public abstract String[] getDomainStr(int col);
	//********************************
	public abstract String[] getColumnStr(int col);
	//********************************
	public abstract void swapColumns(int source, int destination);
	//********************************
	public boolean isTargetNominal(){
		if(attributes[decAttrIdx].type == Attribute.NOMINAL)
			return true;
		else
			return false;
	}
	//********************************
	

}
