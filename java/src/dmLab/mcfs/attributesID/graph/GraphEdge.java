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

public class GraphEdge implements Comparable<GraphEdge>
{
    public int nodeIndex1;
    public int nodeIndex2;
    public float weight;
    //************************
    public GraphEdge()
    {
        nodeIndex1 = -1;
        nodeIndex2 = -1;
        weight=-1;        
    }
    //************************
    public GraphEdge(int nodeIndex1, int nodeIndex2, float weight)
    {
        this.nodeIndex1 = nodeIndex1;
        this.nodeIndex2 = nodeIndex2;
        this.weight = weight;
    }
    //************************    
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(nodeIndex1).append(',').append(nodeIndex2).append(',').append(weight);        
        return tmp.toString();
    }
    //************************
    //return 0 is equal if edge is based on the same pair of indexes
    public int compareTo(GraphEdge edge) 
    {
        if( (edge).nodeIndex1==nodeIndex1 && (edge).nodeIndex2==nodeIndex2 )           
            return 0;
        else
            return -1;
    }
    //************************
}
