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
package dmLab.utils.dataframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import dmLab.utils.ArrayUtils;
import dmLab.utils.MathUtils;


public class DataFrame implements Cloneable
{
	protected Column[] columns;
	protected Object[][] data;
	protected HashMap<String,Integer> colNamesMap;

	//******************************
	protected DataFrame(){
	}
	//******************************
	public DataFrame(int rows, int cols){
		init(rows,cols);
	}
	//******************************
	public DataFrame(int rows, DataFrame df){
		init(rows,df.cols());
		setColumns(df.getColumns());
	}
	//******************************
	protected void init(int rows, int cols){
		columns = new Column[cols];
		data = new Object[rows][cols];
		colNamesMap = new HashMap<String,Integer>();
        for(int i=0;i<columns.length;i++){
        	String colName = "x"+i;
        	columns[i] = new Column(colName, Column.TYPE_NOMINAL);
        	colNamesMap.put(colName, i);
        }		
	}
	//******************************
	protected void initColNamesMap(){
		colNamesMap = new HashMap<String,Integer>();	
	    for(int i=0;i<columns.length;i++){
	    	colNamesMap.put(columns[i].name, i);        	
	    }
	}
	//******************************
	public boolean setColumns(Column[] cols){
		if(cols.length != columns.length)
			return false;
		
		colNamesMap = new HashMap<String,Integer>();		
        for(int i=0;i<columns.length;i++){
        	columns[i].name = cols[i].name;
        	columns[i].type = cols[i].type;
        	colNamesMap.put(cols[i].name, i);        	
        }		
		return true;
	}
	//******************************	
	public boolean setColNames(String[] colNames){
		if(colNames.length != columns.length)
			return false;
		
		colNamesMap = new HashMap<String,Integer>();		
        for(int i=0;i<columns.length;i++){
        	columns[i].name = colNames[i];
        	colNamesMap.put(colNames[i], i);
        }
        return true;		
	}
	//******************************
	public boolean setColTypes(short[] colTypes){
		if(colTypes.length != columns.length)
			return false;
		
        for(int i=0;i<columns.length;i++){
        	columns[i].type = colTypes[i];
        }
        return true;		
	}
	//******************************
	public Column[] getColumns(){
		return columns;
	}
	//******************************
	public String[] getColNames(){
		String[] colNames = new String[columns.length];
        for(int i=0;i<columns.length;i++){
        	colNames[i]=columns[i].name;
        }
        return colNames;
	}
	//******************************
	public short[] getColTypes(){
		short[] colTypes = new short[columns.length];
        for(int i=0;i<columns.length;i++){
        	colTypes[i]=columns[i].type;
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
		data[row][col]=cellValue;
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
				for(int j=0;j<columns.length;j++){
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
        for(int i=0;i<columns.length;i++){
        	if(i>0)
        		tmp.append(", ");
            tmp.append(columns[i].name);
        }
        tmp.append('\n');
        
        final int rows = rows();
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns.length;j++){
            	if(data[i][j]!=null){
            		tmp.append(data[i][j]);
            	}            	
            	if(j<columns.length-1)
            		tmp.append(", ");
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
		Column[] columns_old = columns.clone();
		Object[][] data_old = data.clone();
		
		init(rows(),cols()+df.cols());
		final int cols_new = cols();
		
        for(int j=0;j<cols_new;j++){
        	if(j<cols_old)
        		columns[j] = columns_old[j];
        	else
        		columns[j] = df.columns[j-cols_old];
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
		Column[] columns_old = columns.clone();
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
            	if(columns[j].type == Column.TYPE_NUMERIC){
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
            	if(columns[j].type == Column.TYPE_NUMERIC){
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
		df.columns=columns.clone();
		for(int i=0;i<columns.length;i++){
			df.columns[i] = columns[i].clone(); 
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
