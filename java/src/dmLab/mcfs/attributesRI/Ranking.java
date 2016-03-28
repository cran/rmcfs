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
package dmLab.mcfs.attributesRI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import dmLab.utils.StringUtils;
import dmLab.utils.list.StringList;

public class Ranking {
    
    protected String names[];
    protected float weights[];
    protected String measureName;
    protected HashMap <String,Integer>map;
    public boolean normalize=false;
    //*********************************
    public Ranking()
    {
    }
    //*********************************
    public Ranking(int number)
    {
        init(number);
    }
    //*********************************
    public void init(int number)
    {
        names=new String [number];
        weights=new float[number];
        map=new HashMap<String,Integer>();
    }
    //*********************************
    public int size()
    {
        return weights.length;
    }
    //*********************************
    public boolean put(String name,float weight,int position)
    {
        if(position<0 || position >= names.length)            
            return false;        
        names[position]=name;
        weights[position]=weight;
        map.put(name,new Integer(position));            
        return true;
    }
    //*********************************
    public String[] getAttributesNames()
    {
        return names; 
    }
    //*********************************
    public float compare(Ranking ranking)
    {
        if(ranking.size()!=size())
            return -1;
        
        float distance=0f;
        float maxDistance=0f;
        int size=size();
        //float factor=size*size*2;//2 times size square //czynnik skalujacy by maks odleglosc wynosila 2
        //if(size%2!=0) factor--;//dla nieparzystych jest o jeden mniejsza maksymalna wartosc 
        float factor=size;//2 times size square //czynnik skalujacy by maks odleglosc wynosila 2    
        
        for(int i=0;i<size;i++)
        {
            int position=ranking.getPosition(names[i]);
            if(position==-1)
                position=size;
            distance+= Math.abs(i-position)/factor;
            maxDistance+=Math.abs(i-size)/factor;
        }
        if(normalize)
            return distance/maxDistance;
        else
            return distance;
    }
    //*********************************
    public float commonPart(Ranking ranking)
    {
    	int size=names.length;
    	int common=0;
    	for(int i=0;i<size;i++)
    		if(ranking.contains(names[i]))
    			common++;    		      
    	return (float)common/(float)size;
    }
    //*********************************
    public int commonNumber(Ranking ranking)
    {
        int size=names.length;
        int common=0;
        for(int i=0;i<size;i++)
            if(ranking.contains(names[i]))
                common++;                 
        return common;
    }
    //*********************************
    public boolean contains(String name)
    {
        return map.containsKey(name);
    }
    //*********************************
    public int getPosition(String name)
    {
        Integer val=(map.get(name));
        if(val!=null)
            return val.intValue();
        else
            return -1;
    }
    //*********************************
    public String getMeasureName()
    {
        return measureName;
    }
    //*********************************
    public void setMeasureName(String measureName)
    {
        this.measureName=measureName;        
    }
    //*********************************
    @Override
    public String toString()
    {
        StringBuffer buf=new StringBuffer();
        int size=size();
        buf.append("position,attribute,").append(measureName).append('\n');
        for(int i=0;i<size;i++)
        {
            buf.append(i+1).append(',');
            buf.append(names[i]).append(',');
            buf.append(weights[i]);
            buf.append('\n');
        }
        return buf.toString();
    }
    //*********************************
	public boolean load(String fileName)
    {
        File file = new File(fileName);
        if(!file.exists())
            return false;
        
        BufferedReader fileReader;
        try{
            fileReader= new BufferedReader(new FileReader(fileName));
        }       
        catch(IOException ex){
            System.err.println("Error opening file. File: "+fileName);
            return false;
        }
        
        StringList lines=new StringList();
        String line=null;
        do{
            try{
                line=fileReader.readLine();
            }
            catch (Exception e) {
                System.out.println("Error reading input file.");
                e.printStackTrace();                
            }
            if(line!=null && line.length()!=0)
                lines.add(line);
        }while(line!=null); //end while

        init(lines.size()-1);
        char[] separators={','};
        
        int size=lines.size();        
        for(int i=0;i<size;i++)
        {
            String[] lineList=StringUtils.tokenizeString(lines.get(i),separators,false);
            if(i==0)
                measureName=lineList[2];
            else
                put(lineList[1],Float.parseFloat(lineList[2]),i-1);
        }
        
        try{
			fileReader.close();
		} catch (IOException ex){
            System.out.println("Error closing input file.");
            return false;
		}
        
        return true;
    }
    //*********************************
    public HashMap<String,Integer> getAttributesMap()
    {
        return map;
    }
    //*********************************
    public float getWeight(int index)
    {
        return weights[index];
    }
    //*********************************   
}
