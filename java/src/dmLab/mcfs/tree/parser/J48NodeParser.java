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
package dmLab.mcfs.tree.parser;

import dmLab.classifier.attributeIndicators.J48NodeIndicators;
import dmLab.mcfs.tree.TreeNode;
import dmLab.utils.condition.Condition;

public class J48NodeParser
{   
    //****************************************
    public J48NodeParser()
    {
    }
    //****************************************
    public void parse(TreeNode node,String line)
    {
        parseLevel(node,line);
        parseCondition(node,line);        
        parseDecision(node,line);
        parseNodeIndicators(node,line);
        node.nodeIndicators.attributeName = node.condition.attributeName;
    }
    //****************************************
    public String parseDecision(TreeNode node,String line)
    {                
        //clear decision 
        node.setDecision(null);
        
        int startIndex,stopIndex;
        startIndex=line.lastIndexOf(":");
        
        if(startIndex==-1)
            return null;
        else
            startIndex++;

        stopIndex=line.indexOf("(");
        if(stopIndex==-1)
            return null;
        
        if(stopIndex>startIndex)
        {
            String decision=line.substring(startIndex,--stopIndex).trim();
            node.setDecision(decision);
            return decision;
        }
        else
            return null;
    }    
    //****************************************
    public int parseLevel(TreeNode node,String line)
    {
        char lineArray[]=line.toCharArray();
        int level=0;
        for(int i=0;i<lineArray.length;i++)
        {
            if(lineArray[i]=='|')
                level++;
            else if(lineArray[i]!=' ')
                break;                
        }
        node.setLevel(level);
        return level;
    }
    //****************************************
    public Condition parseCondition(TreeNode node,String line)
    {
        //MDR DEBUG
        //System.out.println("PARSE CONDITION: "+line);
        
        int startIndex,stopIndex;
        startIndex=line.lastIndexOf("|");
        if(startIndex==-1)
            startIndex=0;
        else
            startIndex++;

        stopIndex=line.indexOf(":");
        if(stopIndex==-1)
            stopIndex=line.indexOf("#");
        
        if(stopIndex>startIndex)
        {
            node.condition.parse(line.substring(startIndex,stopIndex).trim());
            return node.condition;
        }
        else
            return null;
    }        
    //****************************************
    public J48NodeIndicators parseNodeIndicators(TreeNode node,String line)
    {
        int startIndex,stopIndex;
        
        startIndex=line.indexOf("#");
        if(startIndex==-1)
            return null;      
        
        for(int i=0;i<node.nodeIndicators.size;i++)
        {   
            stopIndex=line.indexOf("#",startIndex+1);
            float tmpDbl=Float.parseFloat(line.substring(startIndex+1,stopIndex));
            if(i==0)
            {
                node.nodeIndicators.nodeIndex=(int)tmpDbl;
                node.setNodeID(node.nodeIndicators.nodeIndex);
            }
            if(i==1)
                node.nodeIndicators.attrInfoGain=tmpDbl;
            else if (i==2)
                node.nodeIndicators.attrGainRatio=tmpDbl;
            else if (i==3)
                node.nodeIndicators.attrEventsNumber=tmpDbl;
            startIndex=stopIndex;
        }
        return node.nodeIndicators;
    }
    //****************************************
    public String parseAttrName(String line)
    {
        int startIndex,stopIndex;
        startIndex=line.lastIndexOf("|");
        if(startIndex==-1)
            startIndex=0;
        else
            startIndex++;

        stopIndex=line.indexOf("=");
        if(stopIndex==-1)
            stopIndex=line.indexOf(">");
        if(stopIndex==-1)
            stopIndex=line.indexOf("<");

        if(stopIndex>startIndex)
            return line.substring(startIndex,--stopIndex).trim();
        else
            return null;
    }
    //****************************************
}
