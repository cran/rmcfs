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

import java.util.Arrays;
import java.util.HashMap;

import dmLab.discretizer.Discretizer;
import dmLab.discretizer.DiscretizerParams;
import dmLab.utils.MathUtils;

public class DiscChange extends Discretizer
{
    //discretization based on change of of decision attribute value
    public DiscChange()
    {
    }
    //**************************************** 
    @Override
	public float[] findRanges(float[] values, float[] decision, DiscretizerParams discParams)
    {
		int intervals = discParams.discIntervals();
		if(values.length < intervals){
			ranges = null;
			return getRanges();
		}    

        Event events[]=getEvents(values, decision);              
        CutPoints cPoints = calcCutPoints(events);        
        //System.out.println("MDR DEBUG: "+cutPoints.toString()+'\n');
        
        ranges = cPoints.getValues();        
		ranges.add(MathUtils.maxValue(values));
		
		return getRanges();
    }
    //**********************************
    protected CutPoints calcCutPoints(Event events[])
    {
//      count classes number create mapping and detects NaN values
        int first=0;
        int classes=0;
        int cutPointsNumber=0;
        HashMap<Float,Integer> map=new HashMap<Float,Integer>();        
        boolean ambiguity=false;
        
        for(int i=0;i<events.length;i++)
        {
            if(Float.isNaN(events[i].value))
                first++;
            else if(map.get(events[i].classIndex) == null)
                map.put(events[i].classIndex, classes++);
            
            if( i > first )
            {                                
                    if(events[i].classIndex!=events[i-1].classIndex 
                            && events[i].value == events[i-1].value)
                        ambiguity=true;

                    if((ambiguity==true && events[i].value > events[i-1].value) ||
                            ambiguity==false && events[i].classIndex!=events[i-1].classIndex)
                    {//adding cutPoint
                        cutPointsNumber++;
                        ambiguity=false;  
                    }
            }
        }
        /*
        System.out.println("cutPointsNumber="+cutPointsNumber);        
        System.out.println(map.values().toString());
        //*/
        
        CutPoints cPoints=new CutPoints(cutPointsNumber+1,classes); 
        int classEvents[]=new int[classes];
        Arrays.fill(classEvents, 0);
        int cuts=0;
        ambiguity=false;
        //find the cut points        
        for(int i=first+1;i<events.length;i++)
        {                        
            if(events[i].value != events[i-1].value)
                cuts++;
            
            if(events[i].classIndex!=events[i-1].classIndex 
                    && events[i].value == events[i-1].value)
                ambiguity=true;

            if((ambiguity==true && events[i].value > events[i-1].value) ||
                    ambiguity==false && events[i].classIndex!=events[i-1].classIndex)
            {//adding cutPoint
                float cutPoint=events[i-1].value+((events[i].value-events[i-1].value)/2);
                classEvents[map.get(events[i-1].classIndex)]++;
                cPoints.add(cutPoint, i-1, classEvents,cuts);
                Arrays.fill(classEvents, 0);
                ambiguity=false;
                cuts=0;
            }
            else                
                classEvents[map.get(events[i-1].classIndex)]++;

            //the last one
            if(i==events.length-1)
            {
                classEvents[map.get(events[i-1].classIndex)]++;
                cPoints.add(events[i].value, i, classEvents,cuts);
            }
        }
        return cPoints;
    }
    //*******************************************************
    protected Event[] getEvents(float[] values,float[] decision)
    {
        Event events[]=new Event[values.length];
        
        for(int i=0;i<values.length;i++)
            events[i]=new Event(values[i],decision[i]);
        //sort the values and classes labels
        Arrays.sort(events);
        
        return events;
    }
    //*******************************************************    
}
