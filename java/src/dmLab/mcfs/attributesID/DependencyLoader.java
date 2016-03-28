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
package dmLab.mcfs.attributesID;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import dmLab.utils.StringUtils;

public class DependencyLoader 
{
	//***************************
	public DependencyLoader()
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
		boolean readNew=false;
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
				}                
			}            
			if(lineCount==1 && line.startsWith(AttributesID.CONN_FILE_HEADER))
				readNew=true;

			if(readNew){
				if(lineCount>1)
					readLineNew(line,attrID);
			}else{
				readLineOld(line,attrID);
			}

		}while(line!=null); //end while
		attrID.findMinMaxID();
		try {
			file.close();
		} catch (IOException e) {
			System.err.println("Error closing file. File: "+fileName);
		}
		return true;
	}
	//***************************
	private boolean readLineOld(String line, AttributesID attrID){
		String[] list=StringUtils.tokenizeString(line,new char[]{','}, false);
		final int size=list.length;

		for(int i=1;i<size;i++)
		{
			String[] parsedConnection = StringUtils.tokenizeString(list[i],new char[]{'(',')',';'}, false);
			try{
				float weight=Float.parseFloat(parsedConnection[1]); 
				attrID.addDependency(list[0], parsedConnection[0], weight);
			}catch (NumberFormatException e) {
				System.err.println("Error parsing line: "+line);
			}
			//System.out.println(list[0]+"-"+parsedConnection[0]+" :: "+ parsedConnection[1]);            
		}
		return true;
	}
	//***************************
	private boolean readLineNew(String line, AttributesID attrID){

		String[] list=StringUtils.tokenizeString(line,new char[]{','}, false);
		try{
			float weight=Float.parseFloat(list[2]); 
			attrID.addDependency(list[0], list[1], weight);
		}catch (NumberFormatException e) {
			System.err.println("Error parsing line: "+line);
		}

		return true;
	}
	//***************************
}
