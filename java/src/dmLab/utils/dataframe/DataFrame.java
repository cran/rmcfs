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
package dmLab.utils.dataframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import dmLab.utils.ArrayUtils;
import dmLab.utils.MathUtils;


public class DataFrame implements Cloneable
{
	protected ColumnMetaInfo[] columnsMetaInfo;
	protected Object[][] data;
	protected HashMap<String,Integer> colNamesMap;
	protected HashMap<Object,Integer> keyColMap;
	public String separator = ", ";
	
	//******************************
	protected DataFrame(){
	}
	//******************************
	public DataFrame(int rows, int cols){
		init(rows,cols);
	}
	//******************************
	public DataFrame(int rows, String[] colNames){
		init(rows, colNames.length);
		setColNames(colNames);
	}
	//******************************
	public DataFrame(int rows, DataFrame df){
		init(rows,df.cols());
		setColumns(df.getColumnsMetaInfo());
	}
	//******************************
	protected void init(int rows, int cols){
		keyColMap = null;
		columnsMetaInfo = new ColumnMetaInfo[cols];
		data = new Object[rows][cols];
		colNamesMap = new HashMap<String,Integer>();
        for(int i=0;i<columnsMetaInfo.length;i++){
        	String colName = "x"+i;
        	columnsMetaInfo[i] = new ColumnMetaInfo(colName, ColumnMetaInfo.TYPE_NOMINAL);
        	colNamesMap.put(colName, i);
        }		
	}
	//******************************
	protected void initColNamesMap(){
		colNamesMap = new HashMap<String,Integer>();	
	    for(int i=0;i<columnsMetaInfo.length;i++){
	    	colNamesMap.put(columnsMetaInfo[i].name, i);        	
	    }
	}
	//******************************
	public void setKeyColumn(int col) {
		keyColMap = new HashMap<Object,Integer>();
		final int rows = rows();
		for(int i=0; i<rows; i++) {
			keyColMap.put(data[i][col], i);
		}
	}
	//******************************
	public int getRowIdx(Object key) {
		if(keyColMap == null) {
			System.err.println("KeyColumn is not set for the DataFrame!");
			return -2;
		}else {
			Integer idx = keyColMap.get(key);
			if(idx == null)
				return -1;
			else
				return idx;
		}
	}
	//******************************
	public boolean setColumns(ColumnMetaInfo[] cols){
		if(cols.length != columnsMetaInfo.length)
			return false;
		
		colNamesMap = new HashMap<String,Integer>();		
        for(int i=0;i<columnsMetaInfo.length;i++){
        	columnsMetaInfo[i].name = cols[i].name;
        	columnsMetaInfo[i].type = cols[i].type;
        	colNamesMap.put(cols[i].name, i);        	
        }		
		return true;
	}
	//******************************	
	public boolean setColNames(String[] colNames){
		if(colNames.length != columnsMetaInfo.length)
			return false;
		
		colNamesMap = new HashMap<String,Integer>();		
        for(int i=0;i<columnsMetaInfo.length;i++){
        	columnsMetaInfo[i].name = colNames[i];
        	colNamesMap.put(colNames[i], i);
        }
        return true;		
	}
	//******************************
	public boolean setColTypes(short[] colTypes){
		if(colTypes.length != columnsMetaInfo.length)
			return false;
		
        for(int i=0;i<columnsMetaInfo.length;i++){
        	columnsMetaInfo[i].type = colTypes[i];
        }
        return true;		
	}
	//******************************
	public boolean setColType(int col, short colType){
		if(col >= columnsMetaInfo.length)
			return false;
		
        columnsMetaInfo[col].type = colType;
        return true;		
	}	
	//******************************
	public ColumnMetaInfo[] getColumnsMetaInfo(){
		return columnsMetaInfo;
	}
	//******************************
	public float[] getColumnNumeric(int col) {
		if(columnsMetaInfo[col].type != ColumnMetaInfo.TYPE_NUMERIC)
			return null;
		
		final int rows = rows();
		float[] retCol = new float[rows];
		for(int i=0;i<rows;i++) {			
			retCol[i] = (float)data[i][col];
		}
		return retCol;
	}
	//******************************
	public String[] getColNames(){
		String[] colNames = new String[columnsMetaInfo.length];
        for(int i=0;i<columnsMetaInfo.length;i++){
        	colNames[i]=columnsMetaInfo[i].name;
        }
        return colNames;
	}
	//******************************
	public short[] getColTypes(){
		short[] colTypes = new short[columnsMetaInfo.length];
        for(int i=0;i<columnsMetaInfo.length;i++){
        	colTypes[i]=columnsMetaInfo[i].type;
        }
        return colTypes;		
	}
	//******************************
	public int getColIdx(String colName){
		Integer col_idx = colNamesMap.get(colName);
		if(col_idx==null)
			return -1;
		else
			return colNamesMap.get(colName);
	}
	//******************************
	public int getFirstRowIdx(int col, Object cell){
		for(int i=0;i<data.length;i++){
			if(data[i][col].equals(cell))
				return i;
		}		
		return -1;
	}
	//******************************
	public int[] getRowIdx(int col, Object cell){
		ArrayList<Integer> idx = new ArrayList<Integer>();
		for(int i=0;i<data.length;i++){
			if(data[i][col].equals(cell))
				idx.add(i);
		}
		
		int[] retVal = new int[idx.size()];
		for(int i=0; i<retVal.length; i++)
			retVal[i] = idx.get(i);
		return retVal;
	}	
	//******************************
	public boolean setColumn(int col, float[] cells)
	{
		if(rows() != cells.length)
			return false;
		
		final int size = rows();
		
		for(int i=0;i<size;i++)
			data[i][col]=cells[i];
				
		return true;
	}
	//******************************
	public boolean setColumn(int col, String[] cells)
	{
		if(rows() != cells.length)
			return false;
		
		final int size = rows();
		
		for(int i=0;i<size;i++)
			data[i][col]=cells[i];
				
		return true;
	}
	//******************************
	public boolean set(int row, int col, Object cellValue)
	{
		data[row][col] = cellValue;
		return true;
	}
	//******************************
	public boolean set(int row, int col, Object[] cellValue)
	{
		for(int i=0; i<cellValue.length; i++) {
			data[row][col+i] = cellValue[i];
		}
		return true;
	}
	//******************************
	public Object get(int row, int col)
	{
		return data[row][col];
	}
	//******************************
	public Object[] getColumn(int col)
	{
		Object[] column = new Object[rows()];
		for(int i=0;i<column.length;i++){
			column[i]=data[i][col];
		}
		return column;
	}
	//******************************
	public Object[] getRow(int row)
	{
		return data[row];
	}
	//******************************
	public DataFrame excludeRows(int rows[]){
		if(rows.length > rows())
			return null;
						
		HashSet<Integer> rowset = new HashSet<Integer>(Arrays.asList(ArrayUtils.int2Integer(rows)));		
		boolean[] filter = new boolean[rows()];
		
		for(int i=0;i<filter.length;i++){
			if(rowset.contains(i))
				filter[i] = false;
			else
				filter[i] = true;
		}
		
		return filterRows(filter);		
	}
	//******************************
	public DataFrame includeRows(int[] rows){
		if(rows.length > rows())
			return null;
						
		HashSet<Integer> rowset = new HashSet<Integer>(Arrays.asList(ArrayUtils.int2Integer(rows)));		
		boolean[] filter = new boolean[rows()];
		
		for(int i=0;i<filter.length;i++){
			if(rowset.contains(i))
				filter[i] = true;
			else
				filter[i] = false;
		}
		
		return filterRows(filter);
	}
	//******************************
	public DataFrame filterRows(boolean[] filter){
		if(filter.length > rows())
			return null;
		
		int size = ArrayUtils.count(filter, true);		
		DataFrame df = new DataFrame(size, this);
		int dfRowIdx = 0;
		for(int i=0;i<filter.length;i++){
			if(filter[i]){
				for(int j=0;j<columnsMetaInfo.length;j++){
					df.data[dfRowIdx][j]=data[i][j];
				}
				dfRowIdx++;
			}
		}		
		return df;		
	}
	//******************************
	public String toString(){
        StringBuffer tmp=new StringBuffer();
        for(int i=0;i<columnsMetaInfo.length;i++){
        	if(i>0)
        		tmp.append(separator);
            tmp.append(columnsMetaInfo[i].name);
        }
        tmp.append('\n');
        
        final int rows = rows();
        for(int i=0;i<rows;i++){
            for(int j=0;j<columnsMetaInfo.length;j++){
            	if(data[i][j]!=null){
            		tmp.append(data[i][j]);
            	}            	
            	if(j<columnsMetaInfo.length-1)
            		tmp.append(separator);
            	else
            		tmp.append("\n");
            }
        }        
        return tmp.toString();
	}
	//******************************
	public boolean cbind(DataFrame df){
		if(rows() != df.rows())
			return false;
        
		final int rows = rows();
		final int cols_old = cols();			
		ColumnMetaInfo[] columns_old = columnsMetaInfo.clone();
		Object[][] data_old = data.clone();
		
		init(rows(),cols()+df.cols());
		final int cols_new = cols();
		
        for(int j=0;j<cols_new;j++){
        	if(j<cols_old)
        		columnsMetaInfo[j] = columns_old[j];
        	else
        		columnsMetaInfo[j] = df.columnsMetaInfo[j-cols_old];
        }		
        initColNamesMap();
        
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols_new;j++){
            	if(j<cols_old)
            		data[i][j] = data_old[i][j];
            	else
            		data[i][j] = df.data[i][j-cols_old];
            }
        }
		
		return true;
	}
	//******************************
	public boolean rbind(DataFrame df){
		if(cols() != df.cols())
			return false;
        
		final int rows_old = rows();
		final int cols = cols();
		ColumnMetaInfo[] columns_old = columnsMetaInfo.clone();
		Object[][] data_old = data.clone();
		
		init(rows()+df.rows(),cols());
		setColumns(columns_old);
		
		final int rows_new = rows();
				
        for(int i=0;i<rows_new;i++){
            for(int j=0;j<cols;j++){
            	if(i<rows_old)
            		data[i][j] = data_old[i][j];
            	else
            		data[i][j] = df.data[i-rows_old][j];
            }
        }
		
		return true;	
	}	
	//******************************
	public boolean mathOperation(float value, String operator){
		final int rows = rows();
		final int cols = cols();
						
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
            	if(columnsMetaInfo[j].type == ColumnMetaInfo.TYPE_NUMERIC){
            		data[i][j] = MathUtils.mathOperation((Float)data[i][j], value, operator);
            	}
            }
        }
		
		return true;		
	}
	//******************************
	public boolean mathOperation(DataFrame df, String operator){
		if(cols() != df.cols())
			return false;

		if(rows() != df.rows())
			return false;

		final int rows = rows();
		final int cols = cols();
						
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
            	if(columnsMetaInfo[j].type == ColumnMetaInfo.TYPE_NUMERIC){
            		data[i][j] = MathUtils.mathOperation((Float)data[i][j], (Float)df.data[i][j], operator);
            	}
            }
        }
		
		return true;		
	}
	//******************************
	public DataFrame clone()
	{
		DataFrame df = new DataFrame();		
		df.columnsMetaInfo=columnsMetaInfo.clone();
		for(int i=0;i<columnsMetaInfo.length;i++){
			df.columnsMetaInfo[i] = columnsMetaInfo[i].clone(); 
		}
		
		df.initColNamesMap();
		df.data=data.clone();
		return df;
	}
	//******************************
	public int cols()
	{
		return data[0].length;
	}
	//******************************
	public int rows()
	{
		return data.length;
	}
	//******************************
}
