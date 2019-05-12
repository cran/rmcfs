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
package dmLab.mcfs.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import dmLab.classifier.attributeIndicators.J48NodeIndicators;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.tree.parser.TreeParser;
import dmLab.utils.StringUtils;
import dmLab.utils.condition.Condition;
import dmLab.utils.condition.Rule;
import dmLab.utils.condition.Ruleset;

public class TreeNode
{
    private Integer nodeID;

    protected int level; 
    protected boolean leaf;
    protected String decision;

    protected TreeNode parent = null;
    protected ArrayList<TreeNode> kids;

    public J48NodeIndicators nodeIndicators;    
    public Condition condition;    
    //****************************************
    public TreeNode(TreeNode parent, Integer nodeID)
    {
        leaf = false;
        decision = null;
        this.parent = parent;
        this.nodeID = nodeID;

        kids = new ArrayList<TreeNode>();
        condition = new Condition();
        nodeIndicators = new J48NodeIndicators();
    }
    //****************************************
    public void setParent(TreeNode parent)
    {
        if(this.parent==null)
            this.parent = parent;
        else if(this.parent!=parent)
            System.err.println("Error! Parent already exists! ");
    }
    //****************************************   
    public TreeNode getParent()
    {
        return parent;
    }
    //****************************************
    public boolean hasParent()
    {
    	if(parent==null)
    		return true;
    	else
    		return false;
    }
    //****************************************
    public String printNode()
    {
        StringBuffer tmp=new StringBuffer();
        
        tmp.append(condition.toString()).append("\t");
        if(leaf)
            tmp.append(":").append(decision).append("\t");
        tmp.append(nodeIndicators.toString());

        return tmp.toString();
    }
    //****************************************    
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();

        //do not print root node 
        if(level>=0)
        {
            for(int i=0;i<level;i++)
                tmp.append("|   ");
    
            tmp.append(condition.toString()).append("\t");
            if(leaf)
                tmp.append(":").append(decision).append("\t");
            tmp.append(nodeIndicators.toString());
        }
        
        for(Iterator<TreeNode> i=this.kids.iterator();i.hasNext();)
            tmp.append("\n").append( (i.next()).toString() );
        return tmp.toString();
    }
    //****************************************
    public String toStringRule(String rule, int objectsNumber, float weight)
    {
        StringBuffer tmp = new StringBuffer();
        StringBuffer current = new StringBuffer(rule);
        
        //do not print root node 
        if(level>=0){
        	if(level>=1)
        		current.append(" AND ");    
        	current.append(condition.toString());
            if(leaf){
            	tmp.append(current).append(" THEN class is ").append(decision).append(",");
            	tmp.append(" ").append(decision).append(",");
            	tmp.append(" ").append(String.format("%.3f", nodeIndicators.classProb)).append(",");
            	tmp.append(" ").append(String.format("%.3f", nodeIndicators.cov/(float)objectsNumber)).append(",");
            	tmp.append(" ").append(String.format("%.3f", nodeIndicators.classProb * nodeIndicators.cov/(float)objectsNumber)).append(",");
            	tmp.append(" ").append(String.format("%.3f", weight)).append("\n");
            }
        }
        
        for(Iterator<TreeNode> i=this.kids.iterator(); i.hasNext();)
            tmp.append( (i.next()).toStringRule(current.toString(), objectsNumber, weight) );
        return tmp.toString();
    }
    //****************************************
    public Ruleset toRuleset(Ruleset ruleset, Rule currentRule, int objectsNumber, float weight)
    {        
        if(currentRule == null)
        	currentRule = new Rule(ruleset);
        	
        //do not print root node 
        if(level>=0){
        	currentRule.addCondition(condition);
            if(leaf){
            	currentRule.decisionClass = decision;
            	currentRule.classProb = nodeIndicators.classProb;
            	currentRule.cov = nodeIndicators.cov/(float)objectsNumber;
            	currentRule.quality = nodeIndicators.classProb * nodeIndicators.cov/(float)objectsNumber;
            	currentRule.weight = weight;
            	ruleset.add(currentRule);
            	currentRule = null;
            }
        }
        
        for(Iterator<TreeNode> i=this.kids.iterator(); i.hasNext();)
            (i.next()).toRuleset(ruleset, currentRule.clone(), objectsNumber, weight);
        
        return ruleset;
    }
    //****************************************
    public ArrayList<TreeNode> getKids()
    {
        return kids;
    }
    //****************************************
    public void addKid(TreeNode kid)
    {
        kids.add(kid);
    }
    //****************************************
    public int getLevel()
    {
        return level;
    }
    //****************************************
    public void setLevel(int level)
    {
        this.level = level;
    }
    //****************************************
    public void setNodeID(int nodeID)
    {
        this.nodeID=new Integer(nodeID);
    }
    //****************************************
    public Integer getNodeID()
    {
        return nodeID;
    }
    //****************************************
    public boolean isRoot()
    {
        if(level==-1)
            return true;
        else
            return false;
    }
    //****************************************
    public boolean isLeaf()
    {
        return leaf;
    }
    //****************************************
    public String getDecision()
    {
        return decision;
    }
    //****************************************
    public void setDecision(String decision)
    {
        if(decision==null || decision.equalsIgnoreCase(""))
            leaf=false;
        else
        {
            leaf=true;
            this.decision = decision;
        }
    }
    //****************************************
    public void parseTree(TreeParser treeReader)
    {
        TreeNode node;
        do{
            node=treeReader.getNextNode();
            /*
            if(node!=null)
                System.out.println("czytam: "+node.toString());
            */
            if(node!=null && node.level>level)
            {
                //System.out.println("poziom nizej - dodaje do "+this.toString());
                node.setParent(this);
                addKid(node);
                treeReader.flush();
                node.parseTree(treeReader);                
            }
        }
        while(node!=null && node.level>level);
    }
    //****************************************
    public void finalize(Tree tree)
    {
        nodeID = tree.nodeIterator;
        tree.nodes.put(tree.nodeIterator++, this);
        final int size = kids.size();
        for(int i=0;i<size;i++)
            kids.get(i).finalize(tree);
    }
    //****************************************
    public void addConnections(AttributesID connections,HashSet<String> addedConnections,TreeNode startNode,int maxLevel,int currentLevel)
    {        
        //if its a leaf or currentLevel is too high then return        
        if(isLeaf() || currentLevel>maxLevel)
            return;
        
        final int size = kids.size();
        for(int i=0;i<size;i++)
        {
            TreeNode kid=kids.get(i);
            String connectionLabel=startNode.nodeIndicators.nodeIndex+"_"+kid.nodeIndicators.nodeIndex;  
            
            if(!addedConnections.contains(connectionLabel) && !StringUtils.equalsTo(startNode.condition.attributeName,kid.condition.attributeName))
            {
                connections.addID(startNode.condition.attributeName, kid.condition.attributeName,1.0f/currentLevel);                                
                addedConnections.add(connectionLabel);
                //****************  DEBUG
                //System.out.println("Added connection : "+startNode.condition.attributeName+" "+kid.condition.attributeName+" waga "+Float.toString(1.0f/(float)currentLevel));
                //System.out.println("Added label: "+connectionLabel);
            }
            
            //addConnections over all kids
            if(!kid.isLeaf() && currentLevel<maxLevel)
                kid.addConnections(connections,addedConnections, startNode, maxLevel, currentLevel+1);
        }
    }
    //****************************************
}
