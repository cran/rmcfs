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

import java.util.StringTokenizer;

import dmLab.mcfs.tree.TreeNode;

public abstract class TreeParser
{
    protected StringTokenizer tokenizer;
    protected TreeNodeParser j48NodeParser;
    protected TreeNode node;
    protected boolean readingNodes;

    protected String beginLine;
    protected String endLine;    
        
    //****************************************    
    public TreeParser(String treeString)
    {
        readingNodes=false;
        j48NodeParser=new TreeNodeParser();

    	node = null;        
        tokenizer = new StringTokenizer(treeString,"\n");                
    }
    //****************************************
    public abstract String lineModifier(String line);
    //****************************************    
    //method clears node that was read previously
    public void flush()
    {
        node=null;  
    }
    //****************************************
    public TreeNode getNextNode()
    {       
        //flush method have not been used 
        //return previously read node
        if(node!=null)
            return node;

        while(tokenizer.hasMoreTokens()){
            String line=tokenizer.nextToken().trim();                                 
            line = lineModifier(line);
            
            //ends the parsing process
            if(line.startsWith(endLine)){
                readingNodes=false;
                node=null;
                return node;
            }

            if(readingNodes && !line.equalsIgnoreCase("")){
                node=new TreeNode(null,-1);
                j48NodeParser.parse(node,line);
                
                //if parsed line did not contain the real node return null value
                if(node.condition.attributeName==null)
                    node=null;
                
                return node;
            }
            //starts the parsing process            
            if(line.startsWith(beginLine))
                readingNodes=true;
        }   
        return node;
    }
    //****************************************
}
