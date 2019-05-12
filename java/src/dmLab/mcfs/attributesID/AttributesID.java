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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import dmLab.mcfs.attributesID.graph.IDGraph;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.utils.FileUtils;
import dmLab.utils.MyDict;

public class AttributesID implements Iterable<IDEdge>
{
	protected HashMap<IDLink, IDProps> myIDMap;
	protected MyDict myDict;

	protected float maxID;
	protected float minID;

	protected boolean directedGraph;
	protected boolean selfID;

	public static String ID_FILE_HEADER = "parent,child,weight";
	public static String ID_FILE_HEADER_OLD = "edge_a,edge_b,weight";

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
		myIDMap = new HashMap<IDLink, IDProps>();
		minID = Float.MAX_VALUE;
		maxID = Float.MIN_VALUE; 
	}
	//*************************************
	public int size(){
		return myIDMap.size();
	}
	//*************************************
	public boolean isDirected(){
		return directedGraph;
	}
	//*************************************
	public int getNodesNumber(){
		return myDict.size();  
	}
	//*************************************
	public int getEdgesNumber(){
		return myIDMap.size();
	}
	//*************************************
	public MyDict getDict(){
		return myDict;
	}
	//*************************************
	public int addAttributesID(AttributesID attrID)
	{
		IDLink[] myIDLinks = new IDLink[1];
		myIDLinks = attrID.myIDMap.keySet().toArray(myIDLinks);

		for(int i=0;i<myIDLinks.length;i++){
			IDLink link = myIDLinks[i];
			if(link!=null){    	
				IDProps props = attrID.myIDMap.get(link);
				putID(link, props);
			}        
		}

		return myIDMap.size();
	}
	//*************************************
	public int addID(String parent, String child, float weight)
	{
		if(selfID==false && parent.equalsIgnoreCase(child))
			return myIDMap.size();

		putID(parent, child, weight);    	
		if(!directedGraph){
			putID(child, parent, weight);
		}

		return myIDMap.size();
	}
	//  ********************************************
	private int putID(String parent, String child, float weight)
	{
		int parentId = myDict.put(parent);
		int childId = myDict.put(child);

		IDLink link = new IDLink(parentId, childId);    	
		return putID(link, new IDProps(weight));    	
	}
	//  ********************************************
	private int putID(IDLink link, IDProps props){
		IDProps myIDProps = myIDMap.get(link);

		if(myIDProps == null){
			myIDProps = props;
			myIDMap.put(link, props);
		}else{
			myIDProps.add(props);
		}

		//obtain minID and maxID
		if(myIDProps.weight > maxID)
			maxID = myIDProps.weight;
		if(myIDProps.weight < minID)
			minID = myIDProps.weight;

		return myIDMap.size();
	}
	//*************************************    
	@Override
	public String toString()
	{        
		StringBuffer tmp=new StringBuffer();
		tmp.append(ID_FILE_HEADER).append('\n');
		IDLink[] myIDLinksArray = new IDLink[1];
		myIDLinksArray = myIDMap.keySet().toArray(myIDLinksArray);
		for(int i=0;i<myIDLinksArray.length;i++){
			IDLink link = myIDLinksArray[i];
			if(link!=null){
				IDProps props = myIDMap.get(link);
				tmp.append(link.toString(myDict)).append(",").append(props.toString()).append("\n");
			}
		}
		return tmp.toString();
	}
	//*************************************    
	public String toConnString()
	{
		String[] attributes = myDict.getKeys();

		StringBuffer tmp=new StringBuffer();        
		IDList connLists = getIDList(); 

		for(int i=0;i<attributes.length;i++){
			int attrId = myDict.get(attributes[i]);
			Integer[] vals = connLists.getValues(attrId);
			if(vals!=null){
				tmp.append(attributes[i]);
				IDEntity[] myIDEntityArray = new IDEntity[vals.length]; 
				for(int j=0;j<vals.length;j++){
					IDLink link = new IDLink(attrId,vals[j]);
					IDProps props = myIDMap.get(link);
					myIDEntityArray[j] = new IDEntity(link, props);
				}    			
				Arrays.sort(myIDEntityArray);
				for(int j=0;j<myIDEntityArray.length;j++){
					tmp.append(',').append(myDict.get(myIDEntityArray[j].link.childId)).append("("+myIDEntityArray[j].props.toString()+")");
				}
				tmp.append('\n');
			}        	
		}

		return tmp.toString();
	}
	//*************************************    
	public IDList getIDList(){

		String[] attributes = myDict.getKeys();

		int[] attrIds = new int[attributes.length];        
		for(int i=0;i<attributes.length;i++){
			attrIds[i] = myDict.get(attributes[i]); 
		}

		IDList retIDList = new IDList(attrIds);
		IDLink[] linkArray = new IDLink[1];        
		linkArray = myIDMap.keySet().toArray(linkArray);

		for(int i=0;i<linkArray.length;i++){
			IDLink link = linkArray[i];
			if(link!=null){
				retIDList.put(link.parentId, link.childId);
			}
		}

		return retIDList;
	}
	//*************************************
	public boolean save(String fileName)
	{
		return FileUtils.saveString(fileName, toConnString());
	}
	//*************************************
	public boolean load(String fileName)
	{
		IDLoader loader = new IDLoader();
		return loader.load(fileName, this);               
	}
	//*************************************
	public AttributesID filter(float minWeight, AttributesRI importance, int attrNumber)
	{
		Ranking ranking = null;
		String[] attributes = null;

		if(importance!=null)
			ranking = importance.getTopRankingSize(importance.mainMeasureIdx, attrNumber);

		if(ranking!=null)
			attributes = ranking.getAttributesNames();        

		AttributesID retAttrID = this.filter(attributes, minWeight); 
		return retAttrID;
	}
	//*************************************    
	public AttributesID filter(String[] attributes, float minWeight)
	{
		AttributesID retAttrID = new AttributesID(myDict.clone(), directedGraph, selfID);        
		HashSet<Integer> attributesSet=null;
		if(attributes != null){
			attributesSet = new HashSet<Integer>();
			for(int i=0; i<attributes.length; i++){
				Integer currAttrId = myDict.get(attributes[i]);
				if(currAttrId!=null)
					attributesSet.add(currAttrId);
			}
		}

		IDLink[] linksArray = new IDLink[1];
		linksArray = myIDMap.keySet().toArray(linksArray);
		for(int i=0;i<linksArray.length;i++){
			IDLink link = linksArray[i];
			if(link!=null){
				IDProps props = myIDMap.get(link);
				boolean put;        		
				if(props.weight>=minWeight)
					put = true;
				else
					put = false;

				if(attributesSet==null){
					put &= true;
				}else{
					if(attributesSet.contains(link.parentId) && attributesSet.contains(link.childId))
						put &= true;
					else
						put &= false;
				}        		
				if(put)
					retAttrID.putID(link, props);
			}
		}

		return retAttrID;
	}
	//*************************************
	public IDGraph toGraph(float minWeight, AttributesRI importance, int attrNumber)
	{        
		AttributesID currAttrID = filter(minWeight, importance, attrNumber);        
		IDGraph graph = currAttrID.toGraph();
		graph.setNodesWeights(importance);

		return graph;
	}
	//*************************************    
	public IDGraph toGraph()
	{
		float minID = Float.MAX_VALUE;
		float maxID = Float.MIN_VALUE;    	

		IDGraph graph = new IDGraph();
		IDLink[] linksArray = new IDLink[1];
		linksArray = myIDMap.keySet().toArray(linksArray);

		for(int i=0;i<linksArray.length;i++){
			IDLink link = linksArray[i];
			if(link!=null){
				IDProps props = myIDMap.get(link);
				float weight = props.weight;
				if(weight > maxID)
					maxID = weight;
				if(weight < minID)
					minID = weight;

				graph.addEdge(myDict.get(link.parentId), myDict.get(link.childId), weight);
			}
		}

		graph.setMinEdgeWeight(minID);
		graph.setMaxEdgeWeight(maxID);

		return graph;
	}
	//*******************************************
	public float getMaxID()
	{
		return maxID;
	}
	//*******************************************    
	public float getMinID()
	{
		return minID;
	}
	//*******************************************
	public float getIDWeight(int topSize)
	{
		ArrayList<Float> w = new ArrayList<Float>();
		IDLink[] linksArray = new IDLink[1];
		linksArray = myIDMap.keySet().toArray(linksArray);
		for(int i=0;i<linksArray.length;i++){
			IDLink link = linksArray[i];
			if(link!=null){
				w.add(myIDMap.get(link).weight);
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
	@Override
	public Iterator<IDEdge> iterator() {
		IDEdgeIterator it = new IDEdgeIterator(myIDMap,myDict);								
		return it;
	}
	//*************************************
}
