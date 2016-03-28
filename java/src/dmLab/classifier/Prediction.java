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
package dmLab.classifier;

public class Prediction {
    
    protected double scores[];
    protected String label;
    
    public Prediction(String label,double scores[])
    {
        this.label=label;
        this.scores=scores;
    }
    //****************************************
    public String getLabel()
    {
        return label;
    }
//  ****************************************
    public double getScore(int index)
    {
        if(scores!=null)
            if(index>=0 && index<scores.length)
                return scores[index];
        return Double.NaN;
    }
//  ****************************************    
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append(label);
        
        if(scores!=null)            
            for(int i=0;i<scores.length;i++)
                tmp.append(',').append(scores[i]);
        
        return tmp.toString();
    }
//  ****************************************    
    public boolean hasScores()
    {
        if(scores==null)
            return false;
        else
            return true;
    }
//  ****************************************
}
