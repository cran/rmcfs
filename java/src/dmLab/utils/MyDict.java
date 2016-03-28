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
package dmLab.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class MyDict implements Cloneable{
	//***************************	
	  protected HashMap <String,Integer> dictMap;	  
	  protected ArrayList<String> keyArray;
	//***************************
	  public MyDict(){
		  init();
	  }
	//***************************
	  public MyDict(String[] keys){
		  init();
		  put(keys);
	  }
	//***************************
	  public void init()
	  {
		  dictMap=new HashMap<String,Integer>();
		  keyArray=new ArrayList<String>();	    
	  }
	//***************************
	  public int put(String key){		  
		  Integer k = get(key);
		  if(k==null){			  
			  dictMap.put(key, keyArray.size());
			  keyArray.add(key);
			  k = keyArray.size()-1;
		  }
		  return k;
	  }
	  //***************************
	  public int put(String[] keys){
		  for(int i=0;i<keys.length;i++){
			  put(keys[i]);
		  }
		  return keyArray.size();
	  }
	  //***************************
	  public String get(int value){
		  if(keyArray.size()<=value)
			  return null;
		  else
			  return keyArray.get(value);
	  }	  
	  //***************************
	  public Integer get(String key){
		  
		  return dictMap.get(key);
	  }
	  //***************************	  
	  public String[] getKeys(){
		  String[] attributes = new String[1];
		  attributes = keyArray.toArray(attributes);
		  return attributes;
	  }
	  //***************************
	  public int size(){
		  return keyArray.size();
	  }
	  //***************************
	  public String toString(){
		  StringBuffer buff = new StringBuffer();		  
		  Object[] o = dictMap.keySet().toArray();
		  for(int i=0;i<o.length;i++){
			  buff.append((String)o[i]).append(" ").append(get((String)o[i])).append("\n");
		  }		  
		  return buff.toString();
	  }
	  //***************************
	  @SuppressWarnings("unchecked")
	  public MyDict clone(){
		  MyDict retDict = new MyDict();
		  retDict.dictMap = (HashMap<String, Integer>) this.dictMap.clone();
		  retDict.keyArray = (ArrayList<String>) this.keyArray.clone();
		  return retDict;
	  }
	  //***************************
}
