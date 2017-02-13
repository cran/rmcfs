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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dmLab.array.Array;
import dmLab.array.FArray;
import dmLab.classifier.attributeIndicators.AttributeIndicators;
import dmLab.mcfs.attributesRI.measuresRI.NodesMeasure;
import dmLab.mcfs.attributesRI.measuresRI.Importance;
import dmLab.mcfs.attributesRI.measuresRI.ImportanceMeasure;
import dmLab.utils.ArrayUtils;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;
import dmLab.utils.list.StringList;

public class AttributesRI 
{
    public String label;
    public int mainMeasureIdx;
    
    protected ArrayList<ImportanceMeasure>measures;
    protected HashMap<String, Integer> measureName; 
    protected float importances[][] = null;
    protected Dictionary attrMap;

//  ****************************************
    public AttributesRI()
    {
        label="";
        mainMeasureIdx=-1;
        importances=null;
        attrMap=new Dictionary();
        measures=new ArrayList<ImportanceMeasure>();
        measureName = new HashMap<String, Integer>(); 
    }
//  ****************************************
    public AttributesRI(FArray array)
    {
    	this();        
        final int attrNumber=array.colsNumber();
        final int decIndex=array.getDecAttrIdx();
        for(int i=0;i<attrNumber;i++)
            if(i!=decIndex)
                attrMap.addItem(array.attributes[i].name);
    }
  //************************************************
    public void initImportances()
    {
        importances=new float [attrMap.size()][measures.size()];
    }    
    //**************************************
    public void addMeasure(ImportanceMeasure measure)
    {
        measures.add(measure);
        measureName.put(measure.name, measureName.size());
    }
    //**************************************
    public ImportanceMeasure getMeasure(int measureIndex)
    {        
        return measures.get(measureIndex);
    }    
    //******************************************
    public boolean addImportances(String attributeName, ExperimentIndicators experimentIndicators, AttributeIndicators indicators)
    {        
        int attributeIndex=attrMap.getItem(attributeName);
        
        if(attributeIndex==-1){
        	System.err.println("Attribute does not exist in the data. *****"+"\n"+
        			" attributeName: "+attributeName+"\n"+
        			" experimentIndicators: "+experimentIndicators.toString()+"\n"+
        			" attributeIndicators: "+indicators.toString());
            return false;
        }
        
        addImportances(attributeIndex,experimentIndicators,indicators);        
        return true;
    }
//******************************************
    public void addImportances(int attributeIndex, ExperimentIndicators experimentIndicators, AttributeIndicators indicators)
    {        
        final int size = measures.size();
    	for(int i=0;i<size;i++)
            importances[attributeIndex][i] += measures.get(i).calcAttrImportance(experimentIndicators, indicators);
    }
//**********************************************
    public void addProjections(FArray array)
    {
        final int decIndex = array.getDecAttrIdx();
        int measureIdx = getMeasureIndex(ImportanceMeasure.MEASURE_PROJECTIONS);
        ImportanceMeasure measure = measures.get(measureIdx);
        
        final int size = array.colsNumber();
        for(int i=0; i<size; i++){
        	if(i!=decIndex){
	            int attributeIndex = attrMap.getItem(array.attributes[i].name);
	            importances[attributeIndex][measureIdx] += measure.calcAttrImportance();
        	}
        }        
    }
//**********************************************
    public boolean sumImportances(AttributesRI ri)
    {
        final int size_attr = attrMap.size();
        if(size_attr!=ri.attrMap.size()){
            System.err.println("Destination AttributesRI size does not equal to the source AttributesRI size.");
            return false;
        }
        
        final int size_m = measures.size();        
        if(size_m != ri.measures.size()){
            System.err.println("Destination AttributesRI measures number does not equal to the source AttributesRI measures number.");
            return false;
        }
        
        for(int i=0;i<size_attr;i++)
            for(int j=0;j<size_m;j++)
                importances[i][j] += ri.importances[i][j];
        
        return true;
    }
//  ********************************************
    public int getMeasureIndex(String measureName)
    {
    	Integer measureIndex = this.measureName.get(measureName);
    	if(measureIndex==null){
    		System.err.println("Measure "+measureName+" does not exist");
    		return -1;
    	}
        return measureIndex;  
    }
//**********************************************
    public void calcNormMeasure(int splits)
    {        
        int measureProjectionsIdx = getMeasureIndex(ImportanceMeasure.MEASURE_PROJECTIONS);
        int measureRIIdx = getMeasureIndex(ImportanceMeasure.MEASURE_RI);
        int measureRINormIdx = getMeasureIndex(ImportanceMeasure.MEASURE_RINORM);
        
        final int size=getAttributesNumber();
        for (int i=0;i<size;i++){                                    
            if(importances[i][measureProjectionsIdx]!=0)                    
                importances[i][measureRINormIdx]=importances[i][measureRIIdx]/(importances[i][measureProjectionsIdx] * (float)splits);
        }        
    }
//**********************************************    
    public void flushMeasures()
    {
        final int size = measures.size();
        for(int i=0;i<size;i++)
        	measures.get(i).flush();  
    }
//  **********************************************
    public int getAttributesNumber()
    {
        return attrMap.size();
    }
//  ********************************************
    public int getMeasuresNumber()
    {
    	return measures.size();
    }
//  ********************************************    
    public float getSum(int measureIndex)    
    {
        float sum=0;        
        for(int i=0;i<importances.length;i++)
            sum+=importances[i][measureIndex];
        return sum;
    }
//  ********************************************
    public String[] getMeasuresNames()
    {
        final int size = measures.size();
    	String array[]=new String[size];
        for (int i =0;i<array.length;i++)
            array[i]=measures.get(i).name;

        return array;
    }
//  ********************************************
    public float getImportance(String attributeName, int measureIndex)
    {
        int index = attrMap.getItem(attributeName);
        
        if(index!=-1)
            return importances[index][measureIndex];
        else
            return Float.NaN;            
    }
//  ********************************************
    public float[] getImportanceValues(int measureIndex)
    {
        float[] retVal = new float[importances.length];
        for(int i=0;i<importances.length;i++)
            	retVal[i]=importances[i][measureIndex];
        return retVal;
    }
//  ********************************************
    public Importance[] getImportances(int measureIndex){
    	final int size = attrMap.size();    	
    	Importance[] imp = new Importance[size];
    	for(int i=0;i<size;i++){
    		int index = attrMap.getItem(attrMap.getItem(i));        
			//System.out.println("Add "+importances[index][mainMeasureIndex]+" label "+attributes.getItem(i));
			imp[i] = new Importance(attrMap.getItem(i), importances[index][measureIndex]);
    	}
    	return imp;
    }
    //**************************************
    public float[] getMinMaxImportances(int measureIndex)
    {    	
    	float minImportanceValue=importances[0][measureIndex]; 
    	float maxImportanceValue=importances[0][measureIndex];

        for(int i=0;i<importances.length;i++){
        	float importanceValue = importances[i][measureIndex];            
        	if(importanceValue>maxImportanceValue)
        		maxImportanceValue=importanceValue;
        	if(importanceValue<minImportanceValue)
        		minImportanceValue=importanceValue;
        }
    	return new float[]{minImportanceValue, maxImportanceValue};
    }
//  ********************************************
    public Ranking getTopRanking(int measureIndex, float minRI)
    {           
        int size=0;
        for(int i=0;i<importances.length;i++)
            if(importances[i][measureIndex]>minRI)
                size++;
        
        if(size!=0)
            return getTopRankingSize(measureIndex,size);
        else
            return null;
    }
//  ********************************************
    public Ranking getTopRankingSize(int measureIndex, int size)
    {       
        float ranking[][] = new float[importances.length][2];
        if(size>importances.length)
        	size = importances.length;
        
        for(int i=0;i<importances.length;i++){
            ranking[i][0]=i;//attribute index
            ranking[i][1]=importances[i][measureIndex];//value of the measure
        }
        ArrayUtils.qSort(ranking, 1);
        
        Ranking rank = new Ranking(size);
        for(int i=importances.length-1,j=0;i>=importances.length-size;i--,j++){
            String attr=attrMap.getItem((int)ranking[i][0]);
            rank.put(attr,ranking[i][1],j);
        }                
        rank.setMeasureName(measures.get(measureIndex).name);
        
        return rank;       
    }
//  ********************************************
    public boolean[] getColMask(Array container, int measureIndex, int filterSize, boolean inverseFiltering)
    {
        Ranking ranking=getTopRankingSize(measureIndex, filterSize);
        final int columns = container.colsNumber();
        boolean[] colMask = new boolean[columns];
        //set true if inverseFiltering==true
        Arrays.fill(colMask, false || inverseFiltering);
        
        //translation to mask
        int decisionIndex=container.getDecAttrIdx();        
        for(int i=0;i<columns;i++){
        	if(i==decisionIndex)
            	//always select decision attribute
            	colMask[i] = true; 
            else if(ranking.contains(container.attributes[i].name))
                //set false if inverseFiltering==true
            	colMask[i] = true && !inverseFiltering;
        }
        System.out.println("Filtered Attributes: \n"+ranking.toString());
        return colMask;        
    }
//  ********************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();   
        StringBuffer line;
        final int size=attrMap.size();

        //add header
        line=new StringBuffer();
        int iSize=measures.size();
        line.append("attribute,");
        for(int i=0;i<iSize;i++)
        {
            line.append(measures.get(i).name);
            if(i<iSize-1) line.append(',');
        }
        tmp.append(line).append('\n');
        //add results
        for(int i=0;i<size;i++)
        {
            line=new StringBuffer();
            line.append(attrMap.getItem(i)).append(',');
            for(int j=0;j<iSize;j++)
            {
                line.append(importances[i][j]);
                if(j<iSize-1) line.append(',');	
            }
            tmp.append(line).append('\n');
        }
        return tmp.toString();
    }
    //**************************************
    public void save(String outFileName)
    {
        String fileName;
        String extension="csv";

        String ext=FileUtils.getFileExtension(outFileName);
        if(ext.equalsIgnoreCase(extension))
            fileName=outFileName;
        else
            fileName=outFileName+"."+extension;

        FileWriter file;
        try{
            file= new FileWriter(fileName,false);
        }		
        catch(IOException ex){
            System.err.println("Error opening file. File: "+fileName);
            return;
        }				
        try {
            file.write(toString());
            file.close();
        } catch (IOException e) {
            System.err.println("Error writing file. File: "+fileName);
            e.printStackTrace();
        }      
    }
    //******************************************
	public boolean load(String inFileName)
    {
        String fileName;
        String extension="csv";
        
        File file =new File(inFileName);
        if(!file.exists())
            return false;
        
        String ext=FileUtils.getFileExtension(inFileName);
        if(ext.equalsIgnoreCase(extension))
            fileName=inFileName;
        else
            fileName=inFileName+"."+extension;

        BufferedReader fileReader;
        try{
            fileReader= new BufferedReader(new FileReader(fileName));
        }		
        catch(IOException ex){
            System.err.println("Error opening file. File: "+fileName);
            return false;
        }
        String line=null;
        int lineCount=-1;//the first line is a header
        StringList lines=new StringList();

        do{
            try{
                line=fileReader.readLine();
                if(line==null)
                    break;
                else if(line.trim().length()==0)
                    continue;
                lineCount++;
            }
            catch (Exception e) {
                System.out.println("Error reading input file.");
				e.printStackTrace();
            }
            if(lineCount==0)
            {
                String[] list=StringUtils.tokenizeString(line,new char[]{','}, false);
                for(int i=1;i<list.length;i++)//skip 'attribute' label
                {                    
                    NodesMeasure measure=new NodesMeasure(null);
                    measure.name=list[i];
                    measures.add(measure);
                }
            }
            else
                lines.add(line);

        }while(line!=null); //end while
        
        //set mainMeasure on the last one
        mainMeasureIdx=measures.size()-1;
        final int size = lines.size();
        
        importances=new float [size][measures.size()];
        
        for(int i=0;i<size;i++)
        {		
            String[] list=StringUtils.tokenizeString(lines.get(i),new char[]{','}, false);
            String attrName=list[0];
            int attributeIndex=attrMap.addItem(attrName);
            final int listSize=list.length;
            for(int j=1;j<listSize;j++)//skip 'attributeName' label
                importances[attributeIndex][j-1]=Float.parseFloat(list[j]);
        }
        
        getMinMaxImportances(mainMeasureIdx);
        
        try {
            fileReader.close();
        } catch (IOException e) {
            System.err.println("Error closing file. File: "+fileName);
            e.printStackTrace();
        }
        return true;
    }
    //**************************************
}
