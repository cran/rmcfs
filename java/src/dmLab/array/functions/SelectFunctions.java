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
package dmLab.array.functions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import dmLab.array.Array;
import dmLab.array.FArray;
import dmLab.array.meta.Attribute;
import dmLab.array.meta.AttributesMetaInfo;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.utils.ArrayUtils;
import dmLab.utils.StringUtils;
import dmLab.utils.condition.Condition;
import dmLab.utils.list.IntegerList;
import dmLab.utils.roulette.RouletteInput;
import dmLab.utils.roulette.RouletteSelection;

public class SelectFunctions
{	
	public ArrayUtils arrayUtils;
	
	//*************************************
	public SelectFunctions(Random random){
		arrayUtils = new ArrayUtils(random);
	}
	//*************************************
	public int[] balanceClassesIdx(FArray srcArray, int[] balancedSizes)
	{
		ArrayList<int[]> finalIdxList = new ArrayList<int[]>(); 
		float[] decValues = srcArray.getDecValues();
		HashMap<Float, int[]> decisionValuesTable = srcArray.getDecisionValuesTable();
		int finalIdxArraySize = 0;
		for(int i=0;i<decValues.length;i++) {
			int[] currIdx = decisionValuesTable.get(decValues[i]);
			int[] currIdxSelected = arrayUtils.randomSelectValues(currIdx, balancedSizes[i]);
			finalIdxArraySize += currIdxSelected.length; 
			finalIdxList.add(currIdxSelected);
		}
		int[] finalIdxArray = new int[finalIdxArraySize];
		int k = 0;
		for(int i=0;i<finalIdxList.size();i++) {
			final int[] currIdx = finalIdxList.get(i);
			for(int j=0;j<currIdx.length;j++)
				finalIdxArray[k++] = currIdx[j];
		}		
		Arrays.sort(finalIdxArray);
		
		return finalIdxArray;
	}
	//********************************************
	public FArray shuffleColumns(FArray srcArray){
		FArray dstArray = srcArray.clone();
		float[] colMask = new float[srcArray.colsNumber()];
		for(int i=0;i<colMask.length;i++)
			colMask[i] = i;
		
		colMask = arrayUtils.shuffle(colMask, 3);
		for(int i=0; i<colMask.length; i++){
			int srcIndex = (int)colMask[i];
			dstArray.attributes[i] = srcArray.attributes[srcIndex];						
			dstArray.setDomain(i, srcArray.getFDomain(srcIndex));			
			dstArray.setColumn(i, srcArray.getColumn(srcIndex));
			if(srcIndex == srcArray.getDecAttrIdx()){
				dstArray.setDecAttrIdx(i);
				dstArray.setDecValues(srcArray.getDecValues());
			}
		}		
		return dstArray;
	}	
	//********************************************
	// ROWS SELECTION
	//********************************************	
	public static Array selectRows(Array srcArray, String rowCondition)
	{
		Condition condition=new Condition();
		if(!condition.parse(rowCondition)){
			System.err.println("Error Parsing Condition: "+rowCondition);
			return null;
		}

		int conditionAttrIndex=srcArray.getColIndex(condition.attributeName);
		if(conditionAttrIndex==-1){
			System.err.println("Incorrect Attribute. Attribute: "+ condition.attributeName+" is not present.");
			return null;
		}
		if(srcArray.attributes[conditionAttrIndex].type==Attribute.NOMINAL
				&& condition.operator.isNumericalOnly()){
			System.err.println("Incorrect Operator. Attribute is NOMINAL ( use '=' or '!=' ).");
			return null;
		}

		boolean colMask[] = new boolean[srcArray.colsNumber()];
		Arrays.fill(colMask, true);
		boolean rowMask[] = getRowMask(srcArray, condition);
		if(rowMask==null)
			return null;

		return srcArray.clone(colMask, rowMask);
	}
	//********************************************
	public static boolean[] getRowMask(Array array, Condition condition)
	{
		final int conditionAttrIndex = array.getColIndex(condition.attributeName);

		if(conditionAttrIndex==-1){
			System.err.println("Incorrect Attribute. Attribute: "+ condition.attributeName+" does not exist in the array.");
			return null;
		}

		float conditionValue = Float.NaN;
		if(array.attributes[conditionAttrIndex].type == Attribute.NUMERIC){
			try{
				conditionValue = StringUtils.myParseFloat(condition.value);       
			}catch(NumberFormatException e){
				System.err.println("Incorrect Condition Value. Value: " + condition.value + " is not numeric.");
				return null;
			}
		}

		boolean[] rowMask = new boolean[array.rowsNumber()];
		Arrays.fill(rowMask, false);

		for(int i=0; i<rowMask.length; i++){
			String value = array.readValueStr(conditionAttrIndex, i);
			if(array.attributes[conditionAttrIndex].type == Attribute.NOMINAL){
				if(condition.operator.compare(value, condition.value))
					rowMask[i] = true;
			}else if(array.attributes[conditionAttrIndex].type == Attribute.NUMERIC){
				if(condition.operator.compare(StringUtils.myParseFloat(value), conditionValue))
					rowMask[i] = true;
			}
		}
		return rowMask;
	}
	// ********************************************
	public static Array removeNaNRows(Array srcArray, String column){	
		boolean[] rowMask = SelectFunctions.getRowMask(srcArray, new Condition(column + " != ?"));
		int nanCount = ArrayUtils.count(rowMask, false);
		if(nanCount > 0){
			System.out.print("Warning! Target column contains '?' values...");
			boolean colMask[] = new boolean[srcArray.colsNumber()];
			Arrays.fill(colMask, true);
			srcArray = srcArray.clone(colMask, rowMask);
			srcArray.findDomains();
			srcArray.setAllDecValues();
			System.out.println(" "+nanCount + " rows are removed.");
		}
		return srcArray;
	}	
	// ********************************************
	//this function reduces the size of the inputArray to size defined by size
	public Array selectRowsRandom(Array srcArray, float dstRows)
	{
		if(dstRows<=0 || srcArray.rowsNumber()<=dstRows)
			return srcArray;

		if(dstRows<1)
			dstRows = Math.round(dstRows * srcArray.rowsNumber());

		int splitMask[]=new int[srcArray.rowsNumber()];
		arrayUtils.randomFill(splitMask, (int)dstRows, 1);

		boolean[] colMask = new boolean[srcArray.colsNumber()];
		Arrays.fill(colMask, true);
		Array dstArray = srcArray.clone(colMask, ArrayUtils.int2boolean(splitMask, 1));

		return dstArray;
	}	
	//	********************************************
	//splitMask: value 1 - train set; value 0 - test set
	public int[] getSplitMaskRandom(Array srcArray, double splitRatio)
	{
		int splitMask[] = new int [srcArray.rowsNumber()];
		int dstRows =  (int)(srcArray.rowsNumber() * splitRatio);
		arrayUtils.randomSelect(splitMask, dstRows, 1, 0);
		return splitMask;
	}	
	//	********************************************
	//splitMask: value 1 - train set; value 0 - test set
	public int[] getSplitMaskUniform(FArray srcArray, double splitRatio)
	{
		if(srcArray.attributes[srcArray.getDecAttrIdx()].type != Attribute.NOMINAL)
			return getSplitMaskRandom(srcArray, splitRatio);
			
		final float decColumn[]=srcArray.getColumn(srcArray.getDecAttrIdx());		
		final int srcRows=srcArray.rowsNumber();
		final float decValues[]=srcArray.getDecValues();
		final int distribution[]=ArrayUtils.distribution(decColumn, decValues, splitRatio);

		int splitMask[]=new int[srcRows];

		IntegerList intList[]=new IntegerList[decValues.length];
		for(int i=0;i<intList.length;i++)
			intList[i]=new IntegerList();

		for(int i=0;i<decColumn.length;i++){
			for(int j=0;j<decValues.length;j++){
				if(decColumn[i]==decValues[j])
					intList[j].add(i);
			}
		}

		for(int j=0;j<decValues.length;j++){
			final int size=intList[j].size();
			int random[]=new int[size];
			arrayUtils.randomFill(random,distribution[j],1);
			for(int i=0;i<size;i++){
				if(random[i]==1)
					splitMask[intList[j].get(i)]=1;
			}
		}
		return splitMask;
	}	
	//	********************************************
	//splitMask: value 1 - train set; value 0 - test set
	public static Array[] split(Array srcArray, int splitMask[])
	{
		if(!srcArray.domainsCreated()){
			srcArray.findDomains();
		}
		boolean[] colMask = new boolean[srcArray.colsNumber()];
		Arrays.fill(colMask, true);
		Array[] trainTestArrays = new Array[2];
		trainTestArrays[0] = srcArray.clone(colMask, ArrayUtils.int2boolean(splitMask, 1));
		trainTestArrays[1] = srcArray.clone(colMask, ArrayUtils.int2boolean(splitMask, 0));

		return trainTestArrays;
	}
	//********************************************
	// COLUMNS SELECTION
	//********************************************
	//**** function randomly selects attributes
	public int[] getColumnsMask(Array srcArray, int dstColumns)
	{	
		int[] colMask = null;		
		AttributesMetaInfo attrMetaInfo = srcArray.buildAttributesMetaInfo(false);
		//System.out.println(attrMetaInfo.toString());
	
		if(dstColumns >= srcArray.colsNumber()){
			colMask = new int[srcArray.colsNumber()];
			Arrays.fill(colMask, 1);
		}else{
			RouletteInput rouletteInput = attrMetaInfo.getRouletteInput();
			RouletteSelection rouletteSelection = new RouletteSelection(arrayUtils.random);
			rouletteSelection.run(rouletteInput, dstColumns);
			//int[] selected = rouletteSelection.run(rouletteInput, dstColumns);
			//System.out.println(rouletteInput.toString());
			//System.out.println("selected: " + Arrays.toString(selected));
			colMask = rouletteSelection.select(attrMetaInfo, rouletteInput);
			//add decision attribute
			colMask[srcArray.getDecAttrIdx()] = 1;
		}		
		return colMask;
	}
	//********************************************
	public static int[] getColumnsIdx(Array srcArray, AttributesRI importances, int dstColumns)
	{		
		final int srcColumns = srcArray.colsNumber();
		int[] colIdx = null;

		if(dstColumns >= srcColumns){
			colIdx = ArrayUtils.seq(0, srcArray.colsNumber());			
		}else{
			//+1 for decision attribute
			colIdx = new int [Math.min(srcColumns,dstColumns) + 1];
			AttributesMetaInfo attrMetaInfo = srcArray.buildAttributesMetaInfo(false);
			Ranking topRanking = importances.getTopRankingSize(importances.mainMeasureIdx, importances.getAttributesNumber());
			String[] attributes = topRanking.getAttributesNames();			
			int i = 0;
			int currIdx = 0;
			while(i<attributes.length && currIdx < (colIdx.length - 1)){
				if(!ExtFunctions.isContrastAttribute(attributes[i])){
					//System.out.println("## DEBUG Attribute: "+ attributes[i]);
					colIdx[currIdx++] = attrMetaInfo.getIndex(attributes[i]);
				}
				i++;
			}
			colIdx[currIdx] = srcArray.getDecAttrIdx();
			Arrays.sort(colIdx);
		}
		
		return colIdx;
	}
	//********************************************
	//function selects attributes; colMask=1 - select; colMask=0 - ignore
	public static Array selectColumns(Array srcArray, int colMask[])
	{
		boolean[] rowMask = new boolean[srcArray.rowsNumber()];
		Arrays.fill(rowMask, true);
		return srcArray.clone(ArrayUtils.int2boolean(colMask, 1), rowMask);
	}
	//********************************************
	public static Array selectColumns(Array array, String colNames[])    
	{
		HashSet<String> colNamesSet = new HashSet<String>();
		Collections.addAll(colNamesSet, colNames);

		boolean colMask[] = new boolean [array.colsNumber()];
		boolean rowMask[] = new boolean [array.rowsNumber()];
		Arrays.fill(colMask, false);
		Arrays.fill(rowMask, true);

		for(int i=0; i<array.attributes.length ;i++){
			if(colNamesSet.contains(array.attributes[i].name))
				colMask[i] = true;
		}
		array = array.clone(colMask, rowMask);
		return array;
	}
	//********************************************
	public static Array removeColumn(Array array, int column)
	{
		if(column<0 || column>array.colsNumber())
			return null;

		boolean colMask[]=new boolean [array.colsNumber()];
		boolean rowMask[]=new boolean [array.rowsNumber()];
		Arrays.fill(colMask, true);
		Arrays.fill(rowMask, true);
		colMask[column] = false;
		array = array.clone(colMask, rowMask);
		return array;
	}
	//********************************************		
}
