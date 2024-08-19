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
package dmLab.classifier.adx.ruleParser;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class CovParser
{
    //public final static 
    protected HashMap<String,Float> covs=new HashMap<String,Float>();
    //**************************************
    public CovParser()
    {        
        covs.put("p:", 1.0f);
        covs.put("n:", 0.0f);
        covs.put("c:", 0.0f);
        covs.put("ps:", 1.0f);
        covs.put("ns:", 0.0f);
        covs.put("q:", 1.0f);
        covs.put("qf:", 1.0f);
        covs.put("pr:", 1.0f);
    }
    //**************************************
    public float getValue(String label)
    {
        return covs.get(label);   
    }
    //**************************************
    public void parse(String strCoverages) throws IOException
    {
        StreamTokenizer tokenizer=new StreamTokenizer(new StringReader(strCoverages));        
        //int size=covs.size();
        //Set labelsSet=covs.keySet();
        //Object labels[]=labelsSet.toArray();
        
        while(tokenizer.nextToken()!=StreamTokenizer.TT_EOF)
        {            
            String label=tokenizer.sval;
            if(covs.containsKey(label+":"))
            {
                while(tokenizer.ttype!=StreamTokenizer.TT_NUMBER)
                    tokenizer.nextToken();
                
                float value=(float)tokenizer.nval;
                covs.put(label+":", value);    
            }                
        }
    }
    //**************************************
    public int indexOfCovs(String strCoverages)
    {
        int minIndex=strCoverages.length();
        int currIndex;
        int size=covs.size();
        Set<String> labelsSet=covs.keySet();
        Object labels[]=labelsSet.toArray();

        for(int i=0;i<size;i++)
        {
            currIndex=strCoverages.indexOf((String)labels[i]);
            if(currIndex<minIndex && currIndex>0)
                minIndex=currIndex;
        }
        return minIndex;
    }
    //**************************************
    @Override
    public String toString()
    {
        return Arrays.toString(covs.entrySet().toArray());
    }
    //**************************************
    public void test()
    {
        String rule=" l_stacji=(-Infinity;1.5] and entropiaStacji=(-Infinity;0.003877101] and BAD_TRANS_VALUE_PKT=(-4266.0;2999.5]  p:0.788 n:0.338 c:0.430 q: 0.2973 qf: 0.3656 pr: 0.3723";
        CovParser covParser=new CovParser();        
        int index=covParser.indexOfCovs(rule);
        System.out.println(rule.substring(0, index));
        try {
            covParser.parse(rule.substring( index,rule.length()));
        } catch (IOException e) {
            e.printStackTrace();
        }        
        System.out.println(covParser.toString());
    }
//  *******************************************
}
