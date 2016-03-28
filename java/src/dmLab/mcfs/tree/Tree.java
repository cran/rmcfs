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
package dmLab.mcfs.tree;

import java.util.HashMap;

public class Tree 
{
    protected TreeNode rootNode;
    protected HashMap<Integer,TreeNode> nodes;

    protected Object[] nodesArray;
    protected int nodeIterator;

    //****************************************
    public Tree()
    {
        rootNode=null;
        nodes=new HashMap<Integer,TreeNode>();
        nodesArray=null;
    }
    //****************************************
    public void init()
    {
        final Integer rootInt=new Integer(-1);
        rootNode=new TreeNode(null,rootInt);
        rootNode.level=-1;
        rootNode.nodeIndicators.nodeIndex=-1;
        nodes.put(rootInt,rootNode);  
    }  
    //****************************************
    public void addNode(TreeNode parent,TreeNode node)
    {
        parent.addKid(node);
        nodes.put(node.getNodeID(),node);
    }
    //****************************************  
    public TreeNode getRootNode()
    {
        return rootNode;
    }
    //****************************************
    @Override
    public String toString()
    {       
        if(rootNode!=null)
        {
            return rootNode.toString();
        }
        else{
            StringBuffer tmp=new StringBuffer();
            final Object[] values=nodes.values().toArray();
            for(int i=0;i<values.length;i++)
                tmp.append( ((TreeNode)values[i]).toString() );
            return tmp.toString(); 
        }
    }
    //****************************************
    public TreeNode getNode(int nodeID)
    {    
        return nodes.get(new Integer(nodeID));
    }
    //****************************************
    /*
     nodes are empty
     to fill it there is need to add method finalize
     that fills nodes by recursion (node by node) 
    */   
    @Override
    public void finalize()
    {
        nodeIterator=-1;
        rootNode.finalize(this);
    }
    //****************************************
    public void initNodesIterating()
    {                
        nodeIterator=0;              
        if(nodesArray==null)
            nodesArray=nodes.values().toArray();

        //DEBUG MDR
        /*
        for(int i=0;i<nodesArray.length;i++)
            System.out.println(" "+((TreeNode)nodesArray[i]).level+
                    " "+((TreeNode)nodesArray[i]).getNodeID()+
                    " "+((TreeNode)nodesArray[i]).nodeIndicators.nodeIndex+
                    " "+((TreeNode)nodesArray[i]).condition.toString());
        //*/
    }
    //****************************************  
    public boolean hasNextNode()
    {
        if(nodesArray==null)
            initNodesIterating();
        if(nodeIterator<nodesArray.length)
            return true;
        else
            return false;
    }
    //****************************************
    public TreeNode getNextNode()
    {
        if(nodesArray==null)
            initNodesIterating();       
        return (TreeNode)nodesArray[nodeIterator++];
    }
    //****************************************
    public int size()
    {
        return nodes.size();
    }
    //****************************************
    
}
