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
package dmLab.discretizer.change;

public class Event implements Comparable<Event>
{
    public float value;
    public float classIndex;

    //********************************
    public Event()
    {        
    }
    //********************************
    public Event(float value,float classIndex)
    {
        this.value=value;
        this.classIndex=classIndex;
    }
    //********************************
    public int compareTo(Event event)
    {
        float objValue=event.value;
        float dist=this.value-objValue;
        
        //if they are the same
        if(dist==0 && !Float.isNaN(dist))
            dist=this.classIndex-event.classIndex;
        
        if(!Float.isNaN(dist))
        {  
            if(dist>0)            
                return 1;
            else if(dist==0)
                return 0;
            else
                return -1;
        }
        else
        {
            if(Float.isNaN(objValue) && Float.isNaN(value))
                return 0;
            else if(Float.isNaN(objValue))
                return 1;
            else
                return -1;
        }               
    }
    //********************************
    @Override
    public String toString()
    {
        return ""+ value+","+classIndex;
    }
    //********************************
}
