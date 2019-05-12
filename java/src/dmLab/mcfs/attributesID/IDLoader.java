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
package dmLab.mcfs.attributesID;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import dmLab.utils.StringUtils;

public class IDLoader 
{
	//***************************
	public IDLoader()
	{
	}
	//***************************
	public boolean load(String fileName, AttributesID attrID)
	{
		attrID.init();
		BufferedReader file;
		try{
			file= new BufferedReader(new FileReader(fileName));
		}       
		catch(IOException ex){
			System.err.println("Error opening file. File: "+fileName);
			return false;
		}
		String line="";
		int lineCount=0;
		HashMap<String, Integer> headerMap = new HashMap<String, Integer>(); 
		boolean useReadIDEdge = false;
		do{
			try{
				line=file.readLine();
				if(line==null)
					break;
				else if(line.trim().length()==0)
					continue;
				lineCount++;
			}catch (Exception e) {
				System.err.println("Error reading input file. Line: " + lineCount);
				try{
					file.close();
				} catch (IOException e1){
					System.err.println("Error closing input file. File: "+fileName);
					e1.printStackTrace();
				}                
			}
			
			if(lineCount==1){
				//compare file header with reference one
				//file header must contain the following fields "parent, child, weight" OR "edge_a, edge_b, weight"
				String[] refHeader = AttributesID.ID_FILE_HEADER.toLowerCase().split(",");
				String[] refHeaderOld = AttributesID.ID_FILE_HEADER_OLD.toLowerCase().split(",");
				String[] headerArray = line.toLowerCase().split(",");				
				
				useReadIDEdge = StringUtils.allin(refHeaderOld, headerArray, true, true);
				useReadIDEdge = useReadIDEdge || StringUtils.allin(refHeader, headerArray, true, true);
				if(useReadIDEdge){
					for(int i=0; i<headerArray.length; i++)
						headerMap.put(StringUtils.trimChars(headerArray[i], new char[]{'"','\''}).toLowerCase(), i);
				}else{
					readIDList(line, attrID);
				}
			}else{			
				if(useReadIDEdge){
					readIDEdge(line, attrID, headerMap);
				}else{
					readIDList(line, attrID);
				}
			}
			
		}while(line!=null); //end while
		
		try {
			file.close();
		} catch (IOException e) {
			System.err.println("Error closing file. File: "+fileName);
		}
		return true;
	}
	//***************************
	private boolean readIDList(String line, AttributesID attrID){
		String[] list=StringUtils.tokenizeString(line,new char[]{','}, false);
		final int size=list.length;

		for(int i=1;i<size;i++)
		{
			String[] parsedConnection = StringUtils.tokenizeString(list[i],new char[]{'(',')',';'}, false);
			try{
				float weight=Float.parseFloat(parsedConnection[1]); 
				attrID.addID(list[0], parsedConnection[0], weight);
			}catch (NumberFormatException e) {
				System.err.println("Error parsing line: "+line);
			}
			//System.out.println(list[0]+"-"+parsedConnection[0]+" :: "+ parsedConnection[1]);            
		}
		return true;
	}
	//***************************
	private boolean readIDEdge(String line, AttributesID attrID, HashMap<String, Integer> headerMap){
		String[] header;
		String[] refHeader = AttributesID.ID_FILE_HEADER.toLowerCase().split(",");
		String[] refHeaderOld = AttributesID.ID_FILE_HEADER_OLD.toLowerCase().split(",");
		
		header = refHeader;		
		if(!headerMap.containsKey(header[0].toLowerCase()))
			header = refHeaderOld; 
		if(!headerMap.containsKey(header[0].toLowerCase())){
			System.err.println("ID file does not contain correct header: "+ Arrays.toString(refHeader) + " OR " + Arrays.toString(refHeaderOld));			
			return false;
		}
		int parentIdx = headerMap.get(header[0]);
		int childIdx = headerMap.get(header[1]);
		int weightIdx = headerMap.get(header[2]);

		String[] values = StringUtils.tokenizeString(line, new char[]{','}, false);
		try{
			float weight = Float.parseFloat(values[weightIdx]); 
			attrID.addID(StringUtils.trimChars(values[parentIdx], new char[]{'"','\''}),
					StringUtils.trimChars(values[childIdx], new char[]{'"','\''}), weight);
		}catch (NumberFormatException e) {
			System.err.println("Error parsing line: "+line);
		}

		return true;
	}
	//***************************
}
