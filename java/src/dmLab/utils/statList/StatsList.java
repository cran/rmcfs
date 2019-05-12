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
package dmLab.utils.statList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dmLab.utils.ArrayUtils;
import dmLab.utils.FileUtils;
import dmLab.utils.MathUtils;
import dmLab.utils.StringUtils;

public class StatsList {
	private String[] header;
	private HashMap<String,Integer> colIndex;
	private ArrayList<StatsObject> list;
	
	//********************************
	public	StatsList(){
		list = new ArrayList<StatsObject>();
	}
	//********************************
	public boolean add(StatsObject stats){
		return list.add(stats);
	}
	//********************************
	public boolean add(StatsList statList){
		if(header == null){
			header = statList.header.clone();
			headerMapInit();
		}
		return list.addAll(statList.list);
	}
	//********************************
	public String toString(){
		StringBuffer tmp=new StringBuffer();
		tmp.append(headerToString(',')).append('\n');
		final int size = list.size();
		for(int i=0; i<size; i++){
			tmp.append(list.get(i).toString()).append('\n');			
		}
		return tmp.toString();
	}
	//********************************
	public String toStringSummary(String colName){
		if(getColIndex(colName)==null)
			return null;
		
		double[] col = ArrayUtils.float2double(getCol(colName));		
		StringBuffer tmp=new StringBuffer();
		tmp.append(colName).append(": ");
		tmp.append("mean = ").append(MathUtils.mean(col)).append(" ");
		tmp.append("median = ").append(MathUtils.median(col)).append(" ");
		tmp.append("stdev = ").append(MathUtils.stdev(col)).append(" ");
		
		return tmp.toString();		
	}	
	//********************************
	public int size(){
		return list.size();
	}	
    //*************************************
    public boolean save(String fileName)
    {
    	return FileUtils.saveString(fileName, toString());
    }
	//********************************
    public boolean load(String fileName)
    {    	
    	BufferedReader file;
        try{
            file= new BufferedReader(new FileReader(fileName));
        }       
        catch(IOException ex){
            System.err.println("Error opening file. File: "+fileName);
            return false;
        }
        
        String line=null;
        int lineCount=0;
        do{
            try{
                line=file.readLine();
                if(line==null)
                    break;
                else if(line.trim().length()==0)
                    continue;
                lineCount++;
            }
            catch (Exception e) {
                System.err.println("Error reading input file. Line: " + lineCount);                
                try{
                    file.close();
                } catch (IOException e1){
                    System.err.println("Error closing input file. File: "+fileName);
                    e1.printStackTrace();
                    return false;
                }
            }
            if(lineCount==1)
            	addHeader(line,',');
            else
            	list.add(new StatsObject(line));
            
        }while(line!=null); //end while    	
    	    	
    	return true;
    }
	//********************************
    public StatsObject getStatsObject(int index){
    	return list.get(index);
    }
	//********************************
	public String headerToString(char sep){
		if(header == null)
			return "";
		
		StringBuffer tmp=new StringBuffer();
		for(int i=0;i<header.length;i++){
			if(i>0)
				tmp.append(sep);
			tmp.append(header[i]);
		}
		return tmp.toString();
	}
	//********************************
	public int addHeader(String s, char sep)
	{
		header = StringUtils.tokenizeString(s, new char[]{','}, false);
		headerMapInit();
		return header.length;
	}
	//********************************
	private void headerMapInit(){
		colIndex = new HashMap<String,Integer>();
		for(int i=0;i<header.length;i++){
			colIndex.put(header[i], i);	
		}
	}
	//********************************
	public Integer getColIndex(String colName){
		return colIndex.get(colName);
	}
	//********************************
	public float[] getCol(String colName){
		int size = list.size();
		Integer colIndex = getColIndex(colName);
		if(colIndex == null)
			return null;
					
		float[] column = new float[size];
		for(int i=0;i<size;i++){
    		  column[i] = getStatsObject(i).get(colIndex);
		}
		return column;
	}
	//********************************
}
