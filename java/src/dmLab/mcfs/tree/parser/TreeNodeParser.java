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
package dmLab.mcfs.tree.parser;

import dmLab.classifier.attributeIndicators.J48NodeIndicators;
import dmLab.mcfs.tree.TreeNode;
import dmLab.utils.StringUtils;
import dmLab.utils.condition.Condition;

public class TreeNodeParser
{   
    //****************************************
    public TreeNodeParser()
    {
    }
    //****************************************
    public void parse(TreeNode node, String line)
    {
        parseLevel(node, line);
        parseCondition(node, line);        
        parseDecision(node, line);
        parseNodeIndicators(node, line);
        parseClassProb(node, line);
        node.nodeIndicators.attributeName = node.condition.attributeName;
    }
    //****************************************
    public String parseDecision(TreeNode node, String line)
    {                
        //clear decision 
        node.setDecision(null);
        
        int startIndex,stopIndex;
        startIndex=line.lastIndexOf(":");
        
        if(startIndex==-1)
            return null;
        else
            startIndex++;

        stopIndex = Math.min(line.indexOf("("),line.indexOf("#"));
        if(stopIndex==-1)
            return null;
        
        if(stopIndex>startIndex){
            String decision=line.substring(startIndex,--stopIndex).trim();
            node.setDecision(decision);
            return decision;
        }
        else
            return null;
    }    
    //****************************************    
    public int parseLevel(TreeNode node, String line)
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
    public Condition parseCondition(TreeNode node, String line)
    {        
        int startIndex,stopIndex;
        startIndex=line.lastIndexOf("|");
        if(startIndex==-1)
            startIndex=0;
        else
            startIndex++;

        stopIndex=line.indexOf(":");
        if(stopIndex==-1)
            stopIndex=line.indexOf("#");
        
        if(stopIndex>startIndex){
            node.condition.parse(line.substring(startIndex,stopIndex).trim());
            return node.condition;
        }
        else
            return null;
    }        
    //****************************************
    public float parseClassProb(TreeNode node, String line)
    {
        float classProb = 0;
    	int startIndex, stopIndex;
        String tmpLine = line; 
        startIndex = tmpLine.indexOf("(");
        stopIndex  = tmpLine.lastIndexOf(")");
        if(startIndex==-1)
            return Float.NaN;
        tmpLine = line.substring(startIndex+1, stopIndex);
        String[] classSize = StringUtils.tokenizeString(tmpLine, new char[]{'/','%'}, true);
        float maxVal = 0;
        float sumVal = 0;
        for(int i=0; i<classSize.length; i++){
        	float currValue = Float.parseFloat(classSize[i]);
        	if(currValue > maxVal)
        		maxVal = currValue;
        	sumVal += currValue;
        }
        classProb = maxVal / sumVal; 
        node.nodeIndicators.classProb = classProb;
        node.nodeIndicators.cov = sumVal;
        
        return classProb;
    }
    //****************************************    
    public J48NodeIndicators parseNodeIndicators(TreeNode node, String line)
    {
        int startIndex, stopIndex;
        String tmpLine = line; 
        startIndex = tmpLine.indexOf("#");
        stopIndex  = tmpLine.lastIndexOf("#");
        if(startIndex==-1)
            return null;
        
        tmpLine = line.substring(startIndex,stopIndex);        
        String[] indicators = StringUtils.tokenizeString(tmpLine, new char[]{'#'}, true);
        
        for(int i=0;i<node.nodeIndicators.size;i++){   
            float tmpDbl = Float.parseFloat(indicators[i]);
            if(i==0){
                node.nodeIndicators.nodeIndex = (int)tmpDbl;
                node.setNodeID(node.nodeIndicators.nodeIndex);
            }
            if(i==1)
                node.nodeIndicators.attrInfoGain=tmpDbl;
            else if (i==2)
                node.nodeIndicators.attrGainRatio=tmpDbl;
            else if (i==3)
                node.nodeIndicators.attrEventsNumber=tmpDbl;
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
