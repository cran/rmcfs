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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import dmLab.mcfs.attributesID.graph.GraphNode;
import dmLab.mcfs.attributesID.graph.IDGraph;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.utils.FileUtils;
import dmLab.utils.MyDict;

public class AttributesID
{
    protected HashMap<DependencyIdx, DependencyFactors> connections;
    protected MyDict myDict;
    
    protected float maxID;
    protected float minID;

    protected boolean directedGraph;
    protected boolean selfID;
    
    public static String CONN_FILE_HEADER = "parent,child,weight";
    
    //*************************************    		
    public AttributesID(MyDict myDict, boolean directed, boolean selfID){
    	this.myDict = myDict;
    	this.directedGraph = directed;
    	this.selfID = selfID;
        init();
    }
    //*************************************
    public AttributesID(String[] attributes, boolean directed, boolean selfID)
    {
    	myDict = new MyDict(attributes);
    	this.directedGraph = directed;
    	this.selfID = selfID;
        init();
    }
    //*************************************    
    public AttributesID(boolean directed, boolean selfID)
    {
    	myDict = new MyDict();
    	this.directedGraph = directed;
    	this.selfID = selfID;    	    	
        init();
    }
    //*************************************
    public void init()
    { 
    	connections = new HashMap<DependencyIdx, DependencyFactors>();
    	minID = maxID = Float.NaN;
    }
    //*************************************
    public int size(){
    	return connections.size();
    }
    //*************************************
    public void setDirected(boolean directed)
    {
    	directedGraph = directed;
    }
    //*************************************
    public boolean isDirected()
    {
    	return directedGraph;
    }
    //*************************************
    public DependencyFactors getDependencyFactors(String parent, String child)
    {
    	int parentId = myDict.put(parent);
    	int childId = myDict.put(child);    	
    	DependencyIdx connID = new DependencyIdx(parentId, childId);
    	return connections.get(connID);
    }
    //*************************************
    public int addDependency(String parent, String child, float weight)
    {
    	if(selfID==false && parent.equalsIgnoreCase(child))
    		return connections.size();
    		
    	put(parent,child,weight);    	
    	if(!directedGraph){
    		put(child,parent,weight);
        }
        return connections.size();
    }
    //  ********************************************
    private int put(String parent, String child, float weight)
    {
    	int parentId = myDict.put(parent);
    	int childId = myDict.put(child);
    	
    	DependencyIdx connID = new DependencyIdx(parentId, childId);    	
    	DependencyFactors connFactors = connections.get(connID);
    	if(connFactors==null){
    		connFactors = new DependencyFactors(weight);
    		connections.put(connID, connFactors);
    	}else{
    		connFactors.addWeight(weight);
    	}
    	return connections.size();
    }
    //  ********************************************
    private int put(DependencyIdx dependencyIdx, DependencyFactors dependencyFactors){
    	DependencyFactors myConnFactors = connections.get(dependencyIdx);    	
    	if(myConnFactors==null){
    		connections.put(dependencyIdx, dependencyFactors);
    	}else{
    		myConnFactors.add(dependencyFactors);
    	}
    	return connections.size();
    }
    //  ********************************************
    public int addDependencies(AttributesID attrID)
    {
    	DependencyIdx[] connectionIds = new DependencyIdx[1];
        connectionIds = attrID.connections.keySet().toArray(connectionIds);
                	
	    for(int i=0;i<connectionIds.length;i++){
	    	DependencyIdx connId = connectionIds[i];
	        if(connId!=null){    	
	        	DependencyFactors connFactors = attrID.connections.get(connId);
	        	put(connId,connFactors);
	        }        
	    }
        return connections.size();
    }
    //*************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(CONN_FILE_HEADER).append('\n');
        DependencyIdx[] connectionIds = new DependencyIdx[1];
        connectionIds = connections.keySet().toArray(connectionIds);
        for(int i=0;i<connectionIds.length;i++){
        	DependencyIdx connId = connectionIds[i];
        	if(connId!=null){
        		DependencyFactors connFactors = connections.get(connId);
        		tmp.append(connId.toString(myDict)).append(",").append(connFactors.toString()).append("\n");
        	}
        }
        return tmp.toString();
    }
    //*************************************    
    public String connString()
    {
    	String[] attributes = myDict.getKeys();
    	
        StringBuffer tmp=new StringBuffer();        
        DependencyList connLists = getDependencyLists(); 
        
        for(int i=0;i<attributes.length;i++){
        	int attrId = myDict.get(attributes[i]);
        	Integer[] vals = connLists.getValues(attrId);
        	if(vals!=null){
        		tmp.append(attributes[i]);
        		Dependency[] connArray = new Dependency[vals.length]; 
        		for(int j=0;j<vals.length;j++){
        			DependencyIdx connId = new DependencyIdx(attrId,vals[j]);
        			DependencyFactors connFactors = connections.get(connId);
        			connArray[j] = new Dependency(connId, connFactors);
        		}    			
        		Arrays.sort(connArray);
        		for(int j=0;j<connArray.length;j++){
    				tmp.append(',').append(myDict.get(connArray[j].connId.childId)).append("("+connArray[j].connFactors.toString()+")");
        		}
        		tmp.append('\n');
        	}        	
        }
        
        return tmp.toString();
    }
  //*************************************    
    public DependencyList getDependencyLists(){

    	String[] attributes = myDict.getKeys();

        DependencyIdx[] dependencyIndex = new DependencyIdx[1];        
        dependencyIndex = connections.keySet().toArray(dependencyIndex);
        int[] attrIds = new int[attributes.length];
        
        for(int i=0;i<attributes.length;i++){
        	attrIds[i] = myDict.get(attributes[i]); 
        }
        
    	DependencyList cll = new DependencyList(attrIds);
        
    	for(int i=0;i<dependencyIndex.length;i++){
    		DependencyIdx connId = dependencyIndex[i];
        	if(connId!=null){
        		cll.put(connId.parentId, connId.childId);
        	}
    	}

    	return cll;
    }
    //*************************************
    public boolean save(String fileName)
    {
        return FileUtils.saveString(fileName, connString());
    }
    //*************************************
    public boolean load(String fileName)
    {
    	DependencyLoader loader = new DependencyLoader();
    	return loader.load(fileName, this);               
    }
    //*************************************
    public AttributesID cut(float minWeight, AttributesRI importance, int attrNumber)
    {
	    Ranking ranking=null;
	    String[] selectedAttr=null;
	    
	    if(importance!=null)
	        ranking = importance.getTopRankingSize(importance.mainMeasureIdx, attrNumber);
	
	    if(ranking!=null)
	    	selectedAttr = ranking.getAttributesNames();        
	    
	    AttributesID retConnections = this.cut(selectedAttr, minWeight); 
	    return retConnections;
    }
    //*************************************    
    public AttributesID cut(String[] selectedAttr, float minWeight)
    {
        AttributesID retConnections = new AttributesID(myDict.clone(), directedGraph, selfID);        
        HashSet<Integer> attributesSet=null;
        if(selectedAttr != null){
        	attributesSet = new HashSet<Integer>();
        	for(int i=0; i<selectedAttr.length; i++){
        		Integer currAttrId = myDict.get(selectedAttr[i]);
        		if(currAttrId!=null)
        			attributesSet.add(currAttrId);
        	}
        }

        DependencyIdx[] connectionIds = new DependencyIdx[1];
        connectionIds = connections.keySet().toArray(connectionIds);
        for(int i=0;i<connectionIds.length;i++){
        	DependencyIdx connId = connectionIds[i];
        	if(connId!=null){
        		DependencyFactors connFactors = connections.get(connId);
        		boolean put;        		
        		if(connFactors.weight>=minWeight)
        			put = true;
        		else
        			put = false;
        		
        		if(attributesSet==null){
        			put &= true;
        		}else{
        			if(attributesSet.contains(connId.parentId) && attributesSet.contains(connId.childId))
        				put &= true;
        			else
        				put &= false;
        		}
        		
        		if(put)
        			retConnections.put(connId, connFactors);
        	}
        }
        retConnections.findMinMaxID();
        
        return retConnections;
    }
    //*************************************
    public IDGraph toGraph(float minWeight, AttributesRI importance, int attrNumber)
    {        
        AttributesID cut = this.cut(minWeight, importance, attrNumber);        
        IDGraph graph = cut.toGraph();        
        graph.setNodesWeights(importance);

        if(importance!=null){
        	float[] minMax = importance.getMinMaxImportances(importance.mainMeasureIdx);
            graph.setMinNodeWeight(minMax[0]);
            graph.setMaxNodeWeight(minMax[1]);
        }
        else{
            graph.setMinNodeWeight(GraphNode.DEFAULT_WEIGHT);
            graph.setMaxNodeWeight(GraphNode.DEFAULT_WEIGHT);
        }   

        return graph;
    }

    //*************************************    
    public IDGraph toGraph()
    {
    	IDGraph graph=new IDGraph();
        DependencyIdx[] connectionIds = new DependencyIdx[1];
        connectionIds = connections.keySet().toArray(connectionIds);
        for(int i=0;i<connectionIds.length;i++){
        	DependencyIdx connId = connectionIds[i];
        	if(connId!=null){
        		DependencyFactors connFactors = connections.get(connId);        		
        		graph.addEdge(myDict.get(connId.parentId), myDict.get(connId.childId), connFactors.weight);
        	}
        }
                        
        graph.setMinEdgeWeight(minID);
        graph.setMaxEdgeWeight(maxID);

        return graph;
    }
    //  *******************************************
    public float getMaxID()
    {
        return maxID;
    }
    //  *******************************************    
    public float getMinID()
    {
        return minID;
    }
    //  *******************************************
    public void findMinMaxID()
    {
    	minID = Float.MAX_VALUE;
    	maxID = Float.MIN_VALUE;    	
        DependencyIdx[] connectionIds = new DependencyIdx[1];
        connectionIds = connections.keySet().toArray(connectionIds);
        for(int i=0;i<connectionIds.length;i++){
        	DependencyIdx connId = connectionIds[i];
        	if(connId!=null){
        		DependencyFactors connFactors = connections.get(connId);
        		
        		float weight = connFactors.weight;
        		if(weight>maxID)
                    maxID=weight;
                if(weight<minID)
                    minID=weight;
        	}
        }        
    }
    //  *******************************************
    public float getIDValue(int topSize)
    {
        ArrayList<Float> w = new ArrayList<Float>();
        DependencyIdx[] connectionIds = new DependencyIdx[1];
        connectionIds = connections.keySet().toArray(connectionIds);
        for(int i=0;i<connectionIds.length;i++){
        	DependencyIdx connId = connectionIds[i];
        	if(connId!=null){
        		DependencyFactors connFactors = connections.get(connId);        		
        		 w.add(connFactors.weight);
        	}
        }
        Float[] f = new Float[1];
        f = w.toArray(f);
        Arrays.sort(f);
        if(f.length-topSize<0)
        	return f[0];
        
        return f[f.length-topSize];
    }
    //*************************************
    public int getNodesNumber()
    {
        return myDict.size();  
    }
    //*************************************
    public int getEdgesNumber()
    {
        return connections.size();  
    }
    //*******************************************
}
