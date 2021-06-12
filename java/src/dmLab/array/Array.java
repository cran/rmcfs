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

import java.util.HashSet;
import java.util.ArrayList;
import dmLab.array.functions.ExtFunctions;
import dmLab.array.meta.Attribute;
import dmLab.array.meta.AttributesMetaInfo;
import dmLab.mcfs.MCFSParams;

public abstract class Array implements Cloneable
{
	public static String SPACE_CHAR = "_";

	public Attribute[] attributes;
	
	protected int decAttrIdx;
	protected AttributesMetaInfo attributesDict;
	
	public boolean debug = false;
	//********************************
	public Array()
	{
		attributes = null;
		attributesDict = null;
		decAttrIdx = -1;
	}
	//********************************
	public static int[] colMask2colIdx(int[] colMask) {
		ArrayList<Integer> colIdx = new ArrayList <Integer>(); 
		for(int i=0;i<colMask.length;i++) {
			if(colMask[i] == 1)
				colIdx.add(i);
		}
		
		int[] colIdxInt = new int[colIdx.size()];
		for(int i=0;i<colIdx.size();i++) {
			colIdxInt[i] = colIdx.get(i); 
		}
		return colIdxInt;
	}
	//********************************
	public abstract void init(int cols, int rows);
	//********************************
	protected void initAttributes(int cols, int rows)
	{
		attributesDict = null;
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
	public abstract Array cloneByIdx(int colIdx[], int rowIdx[]);
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
	public String[] getColNames(int colIdx[], boolean includeDecision)
    {				
		ArrayList<String> colNames = new ArrayList<String>();		
		int decIndex=getDecAttrIdx();
		if(includeDecision)
			decIndex = -1;
		
		for(int i=0;i<colIdx.length;i++){			
			if(colIdx[i]!=decIndex) {
				colNames.add(attributes[colIdx[i]].name);
			}
		}
		
		String[] colNamesArray = new String[1]; 
		return colNames.toArray(colNamesArray);				
    }

	//	********************************************
	//	**** return index of column by given name, returns -1 if column is not present
	public int getColIndex(String colName)
	{
		int index = -1;
		//TODO add HashMap here?
		/*
		if(attributesDict != null)
			index = attributesDict.getIndex(colName);
		else{
			for (int i = 0; i < attributes.length; i++){
				if (attributes[i].name.equalsIgnoreCase(colName)){
					index = i;
					break;
				}
			}
		}*/
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
	public AttributesMetaInfo buildAttributesMetaInfo(boolean refresh){
		if(refresh || attributesDict == null){
			attributesDict = new AttributesMetaInfo(this);
		}
		return attributesDict;		
	}
	//********************************
	public void fixAttributesNames(boolean fixContrastNames){
		HashSet<String> attrSet = new HashSet<String>();				
		for(int i=0; i<attributes.length; i++){
			//replace existing contrast names
			if(ExtFunctions.isContrastAttribute(attributes[i].name)){
				attributes[i].name = MCFSParams.FIX_ATTR_PREFIX + attributes[i].name;
			}
			//check if attr is already there
			int idx = 1;
			String currAttrName = attributes[i].name;
			while(attrSet.contains(currAttrName)){
				currAttrName = attributes[i].name + "." + idx;
				idx++;
			}
			attributes[i].name = currAttrName;
			attrSet.add(attributes[i].name);
		}
	}
	//********************************
}
