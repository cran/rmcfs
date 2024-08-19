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
package dmLab.mcfs.attributesID.graph;

import java.util.ArrayList;
import java.util.HashMap;

import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.Dictionary;

public class IDGraph
{
	protected Dictionary nodesNames = new Dictionary();  
    
    protected HashMap<Integer,GraphNode> nodes;
    protected ArrayList<GraphEdge>edges;
    
    protected GraphEdge edgesArray[];
    
    protected float maxNodeWeight; 
    protected float minNodeWeight;
    
    protected float maxEdgeWeight; 
    protected float minEdgeWeight;
    
    //******************************
    public IDGraph()
    {
        nodesNames = new Dictionary();
        nodes = new HashMap<Integer,GraphNode>();
        edges = new ArrayList<GraphEdge>(50000);
        
        maxNodeWeight = GraphNode.DEFAULT_WEIGHT; 
        minNodeWeight = GraphNode.DEFAULT_WEIGHT;
        
        maxEdgeWeight = 0; 
        minEdgeWeight = 0;
    }
    //******************************
    public GraphNode[] getNodes()
    {        
        Object nodesArray[] = nodes.values().toArray();
        GraphNode graphNodes[]=new GraphNode[nodesArray.length];
        
        for(int i=0;i<nodesArray.length;i++)
        {
            //I have to put the nodes in natural order
            //nodes.values().toArray(); do not keep natural order
            int index = nodesNames.getItem(((GraphNode)nodesArray[i]).name);            
            graphNodes[index]=(GraphNode)nodesArray[i];        
        }
        return graphNodes;        
    }
    //******************************
    public GraphEdge[] getEdges()
    {        
    	edgesArray = new GraphEdge[edges.size()];    	
    	edgesArray = edges.toArray(edgesArray);    	            
        return edgesArray;         
    }
    //******************************
    public void addEdge(String node1, String node2, float edgeWeight)    
    {
        int nodeIndex1 = nodesNames.addItem(node1);
        int nodeIndex2 = nodesNames.addItem(node2);
        if(nodes.get(nodeIndex1)==null)
            nodes.put(nodeIndex1, new GraphNode(node1,-1f));
        if(nodes.get(nodeIndex2)==null)
            nodes.put(nodeIndex2, new GraphNode(node2,-1f));
        
        GraphEdge edge=new GraphEdge(nodeIndex1, nodeIndex2, edgeWeight);
        if(!edgeExists(edge))
            edges.add(edge);        
    }
    //******************************
    private boolean edgeExists(GraphEdge edge)
    {
        final int size = edges.size();
        for(int i=0;i<size;i++)
        {
            if(((GraphEdge)edges.get(i)).compareTo(edge)==0)
            return true;
        }
        return false;
    }    
    //******************************
    public void setNodesWeights(AttributesRI importance)
    {
        if(importance==null){
            setMinNodeWeight(GraphNode.DEFAULT_WEIGHT);
            setMaxNodeWeight(GraphNode.DEFAULT_WEIGHT);
            return;
        }
        
    	float minRI = Float.MAX_VALUE;
    	float maxRI = Float.MIN_VALUE;    	
    	
    	GraphNode[] nodesArray = new GraphNode[1]; 
    	nodesArray = nodes.values().toArray(nodesArray);
        for(int i=0;i<nodesArray.length;i++)
        {            
            GraphNode node = nodesArray[i];
            //if node does not exist in importance the result of getImportance is NaN
            float weight = importance.getImportance(node.name, importance.mainMeasureIdx);
            if(Float.isNaN(weight))
                System.err.println("Attribute "+node.name+" does not exists in AttributesImportance");
            else
                node.weight = weight;
            
    		if(weight > maxRI)
    			maxRI = weight;
            if(weight < minRI)
                minRI = weight; 
        }
        setMinNodeWeight(minRI);
        setMaxNodeWeight(maxRI);
    }
    //******************************
    public int getNodesNumber()
    {
        return nodes.size();
    }
    //******************************
    public int getEdgesNumber()
    {
        return edges.size();
    }
    //******************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(" minNodeWeight: ").append(minNodeWeight);
        tmp.append(" maxNodeWeight: ").append(maxNodeWeight).append('\n');
        tmp.append(" minEdgeWeight: ").append(minEdgeWeight);
        tmp.append(" maxEdgeWeight: ").append(maxEdgeWeight).append('\n');
        
        tmp.append("#####################").append('\n');
        GraphNode graphNodes[] = getNodes();

        for(int i=0;i<graphNodes.length;i++)
            tmp.append(nodesNames.getItem(graphNodes[i].name)+","+graphNodes[i].toString()).append('\n');
        
        tmp.append("#####################").append('\n');
        final int size = edges.size();
        for(int i=0;i<size;i++)
            tmp.append(((GraphEdge)edges.get(i)).toString()).append('\n');        
                         
        return tmp.toString();
    }
    //******************************    
    public GraphEdge[] getEdgesArray() {
        return edgesArray;
    }
    //****************************** 
    public void setEdgesArray(GraphEdge[] edgesArray) {
        this.edgesArray = edgesArray;
    }
    //****************************** 
    public float getMaxEdgeWeight() {
        return maxEdgeWeight;
    }
    //****************************** 
    public void setMaxEdgeWeight(float maxEdgeWeight) {
        this.maxEdgeWeight = maxEdgeWeight;
    }
    //****************************** 
    public float getMaxNodeWeight() {
        return maxNodeWeight;
    }
    //****************************** 
    public void setMaxNodeWeight(float maxNodeWeight) {
        this.maxNodeWeight = maxNodeWeight;
    }
    //****************************** 
    public float getMinEdgeWeight() {
        return minEdgeWeight;
    }
    //****************************** 
    public void setMinEdgeWeight(float minEdgeWeight) {
        this.minEdgeWeight = minEdgeWeight;
    }
    //****************************** 
    public float getMinNodeWeight() {
        return minNodeWeight;
    }
    //****************************** 
    public void setMinNodeWeight(float minNodeWeight) {
        this.minNodeWeight = minNodeWeight;
    }
    //****************************** 
}
