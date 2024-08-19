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
package dmLab.mcfs.attributesRI;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import dmLab.array.Array;
import dmLab.array.FArray;
import dmLab.classifier.attributeIndicators.AttributeIndicators;
import dmLab.mcfs.attributesRI.measuresRI.Importance;
import dmLab.mcfs.attributesRI.measuresRI.ImportanceMeasure;
import dmLab.mcfs.attributesRI.measuresRI.NodesMeasure;
import dmLab.utils.ArrayUtils;
import dmLab.utils.FileUtils;
import dmLab.utils.StringUtils;
import dmLab.utils.dataframe.ColumnMetaInfo;
import dmLab.utils.dataframe.DataFrame;
import dmLab.utils.list.StringList;

public class AttributesRI 
{
    public String label;
    public int mainMeasureIdx;
        
    protected ArrayList<ImportanceMeasure>measures;
    protected HashMap<String, Integer> measureName; 
    protected float importances[][] = null;
    protected Dictionary attrMap;
    
    public static String ATTRIBUTE_LABEL = "attribute";

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
    public void addProjections(String[] colNames)
    {        
    	int measureIdx = getMeasureIndex(ImportanceMeasure.MEASURE_PROJECTIONS);
    	ImportanceMeasure measure = measures.get(measureIdx);

    	for(int i=0; i<colNames.length; i++){
    		int attributeIndex = attrMap.getItem(colNames[i]);
    		importances[attributeIndex][measureIdx] += measure.calcAttrImportance();
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
    public void calcNormalizedMeasures(int splits, boolean all)
    {        
        int measureProjectionsIdx = getMeasureIndex(ImportanceMeasure.MEASURE_PROJECTIONS);
        int measureRIIdx = getMeasureIndex(ImportanceMeasure.MEASURE_RI_ROUGH);
        int measureRINormIdx = getMeasureIndex(ImportanceMeasure.MEASURE_RI);

        int measureClassifiersIdx = getMeasureIndex(ImportanceMeasure.MEASURE_CLASSIFIERS);
        int measureNodesIdx = getMeasureIndex(ImportanceMeasure.MEASURE_NODES);
        
        final int size = getAttributesNumber();
        for (int i=0;i<size;i++){                                    
            if(importances[i][measureProjectionsIdx]!=0)                    
                importances[i][measureRINormIdx] = importances[i][measureRIIdx]/(importances[i][measureProjectionsIdx] * (float)splits);
            if(all) {
	            if(importances[i][measureClassifiersIdx]!=0)
	                importances[i][measureClassifiersIdx] = importances[i][measureClassifiersIdx]/(importances[i][measureProjectionsIdx] * (float)splits);
	            if(importances[i][measureNodesIdx]!=0)
	                importances[i][measureNodesIdx] = importances[i][measureNodesIdx]/(importances[i][measureProjectionsIdx] * (float)splits);
            }
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
    public String[] getMeasuresNamesBasic()
    {
    	String[]  names = new String[]{ImportanceMeasure.MEASURE_PROJECTIONS,
            	ImportanceMeasure.MEASURE_CLASSIFIERS,
            	ImportanceMeasure.MEASURE_NODES,
            	ImportanceMeasure.MEASURE_RI};
    	return(names);
    }
//  ********************************************
    public String[] getMeasuresNames()
    {
        final int size = measures.size();
    	String array[]=new String[size];
        for (int i=0;i<array.length;i++)
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
    public Ranking getTopRanking(float minRI)
    {           
    	return getTopRanking(mainMeasureIdx, minRI);
    }
//  ********************************************
    public Ranking getTopRanking(int measureIndex, float minRI)
    {           
        int size=0;
        for(int i=0;i<importances.length;i++)
            if(importances[i][measureIndex] > minRI)
                size++;
        
        if(size!=0)
            return getTopRankingSize(measureIndex,size);
        else
            return null;
    }
//  ********************************************
    public Ranking getTopRankingSize(int size)
    {
    	return getTopRankingSize(mainMeasureIdx, size);
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
        Ranking ranking = getTopRankingSize(measureIndex, filterSize);
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
    	return toString(getMeasuresNamesBasic());
    }
//  ********************************************
    public DataFrame toDataFrame(String[] measuresNames) {
    	final int size = getAttributesNumber();
    	//create container
		DataFrame df = new DataFrame(size, measuresNames.length + 1);
		df.separator=",";
		ArrayList<String> names = new ArrayList<String>(measuresNames.length + 1);		
		names.add(AttributesRI.ATTRIBUTE_LABEL);
		names.addAll(Arrays.asList(measuresNames));
		String[] t = new String[1];		
		df.setColNames(names.toArray(t));			
		for(int i=0; i<df.cols(); i++ ){
			if(i==0)
				df.setColType(i, ColumnMetaInfo.TYPE_NOMINAL);
			else
				df.setColType(i, ColumnMetaInfo.TYPE_NUMERIC);
		}

		//create the measure mask
        boolean[] measuresMask = new boolean[measures.size()];
    	Arrays.fill(measuresMask, false);
        for(int i=0;i<measuresMask.length;i++){
        	if(names.contains(measures.get(i).name)){
	            measuresMask[i] = true;
        	}
        }
        
		//fill the container
        for(int i=0;i<size;i++){
        	int k=0;
    		df.set(i, k++, getAttrName(i));        	
            for(int j=0;j<measuresMask.length;j++){
            	if(measuresMask[j]){
            		df.set(i, k++, importances[i][j]);
            	}
            }
        }
        return(df);
    }
//  ********************************************
    public String toString(String[] measuresNames)
    {
        StringBuffer tmp = new StringBuffer();   
        StringBuffer line;
        
        final int size = attrMap.size();
        boolean[] measuresMask = new boolean[measures.size()];
    	Arrays.fill(measuresMask, false);
        HashSet<String> measuresNamesSet = null;
        
        if(measuresNames != null){
        	measuresNamesSet = new HashSet<String>(Arrays.asList(measuresNames));
        }else{
        	measuresNamesSet = new HashSet<String>(Arrays.asList(getMeasuresNames()));        	
        }

        //add header
        line = new StringBuffer();
        line.append(ATTRIBUTE_LABEL);
        for(int i=0;i<measuresMask.length;i++){
        	if(measuresNamesSet.contains(measures.get(i).name)){
	            line.append(',').append(measures.get(i).name);
	            measuresMask[i] = true;
        	}
        }
        tmp.append(line).append('\n');
        
        //add results
        for(int i=0;i<size;i++){
            line = new StringBuffer();
            line.append(attrMap.getItem(i));
            for(int j=0;j<measuresMask.length;j++){
            	if(measuresMask[j]){
            		line.append(',').append(importances[i][j]);
            	}
            }
            tmp.append(line).append('\n');
        }
        return tmp.toString();
    }
    //******************************************
    public int getAttrIndex(String attrName) {    	
    	return attrMap.getItem(attrName);
    }
    //******************************************

    public String getAttrName(int attrIndex) {
    	return attrMap.getItem(attrIndex);
    }
    //******************************************
    public float[] getAttrImportances(int attrIndex) {
    	return importances[attrIndex];
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
        int attributeIdx = -1;

        do{
            try{
                line=fileReader.readLine();
                if(line==null)
                    break;
                else if(line.trim().length()==0)
                    continue;
                lineCount++;
            }catch (Exception e) {
                System.out.println("Error reading input file.");
				e.printStackTrace();
            }
            if(lineCount==0){
                String[] headerArray = StringUtils.tokenizeString(line,new char[]{','}, false);
				for(int i=0; i<headerArray.length; i++){
					headerArray[i] = StringUtils.trimChars(headerArray[i], new char[]{'"','\''});
					if(headerArray[i].equalsIgnoreCase(ATTRIBUTE_LABEL))
						attributeIdx = i;
				}
                
                for(int i=attributeIdx+1; i<headerArray.length; i++){//skip 'attribute' label and jump to RI values
                    NodesMeasure measure=new NodesMeasure(null);
                    measure.name=headerArray[i];
                    measures.add(measure);
                }
            }else{
                lines.add(line);
            }
            
        }while(line!=null); //end while
        
        //set mainMeasure on the last one
        mainMeasureIdx=measures.size()-1;
        final int size = lines.size();        
        importances = new float [size][measures.size()];
        for(int i=0;i<size; i++){
            String[] attrRIvalues=StringUtils.tokenizeString(lines.get(i),new char[]{','}, false);
            String attrName = StringUtils.trimChars(attrRIvalues[attributeIdx], new char[]{'"','\''});            
            int attributeId = attrMap.addItem(attrName);
            for(int j=attributeIdx+1;j<attrRIvalues.length;j++){//skip 'attribute' label and jump to RI values
            	importances[attributeId][j-(attributeIdx+1)] = Float.parseFloat(attrRIvalues[j]);                
            }
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
