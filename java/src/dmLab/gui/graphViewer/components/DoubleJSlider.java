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
package dmLab.gui.graphViewer.components;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;


public class DoubleJSlider extends JSlider {

	private static final long serialVersionUID = 2812627103300016657L;
	protected int scale;
	
    //************************************
    public DoubleJSlider() {
    	super();
    	init();
    }	
    //************************************    	
    public DoubleJSlider(float min, float max, float value, float step) {    	      	    	
    	init(min,max,value,step);
    }
    //************************************
    public void init()
    {
    	init(0,0,0,0.1f);
        this.setPaintTicks(false);
        this.setPaintLabels(false);
    }
    //************************************    
    public void init(float min, float max, float value, float step)
    {    	
        this.scale = Math.round(1f / step);
        super.setMaximum((int)(scale*max));
        super.setMinimum((int)(scale*min));
        super.setValue((int)(scale*value));
                
        Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
        int medValue = (int)((super.getMaximum()-super.getMinimum())/2.0f);
        float medLabelValue =  (this.getScaledMaximum()-this.getScaledMinimum())/2.0f;
        
        String minLabel = Float.toString(this.getScaledMinimum());
        String medLabel = Float.toString(medLabelValue);
        String maxLabel = Float.toString(this.getScaledMaximum());
        if(scale == 1)
        {
            minLabel = Integer.toString(this.getMinimum());
            medLabel = Integer.toString(medValue);
            maxLabel = Integer.toString(this.getMaximum());        	
        }
        
        labelTable.put(new Integer(super.getMinimum()),new JLabel(minLabel));
        labelTable.put(new Integer(medValue),new JLabel(medLabel));
        labelTable.put(new Integer(super.getMaximum()),new JLabel(maxLabel));
        super.setLabelTable(labelTable);
        
        this.setMajorTickSpacing((int)(0.5*(this.getMaximum()-this.getMinimum())));
        this.setMinorTickSpacing((int)(0.25*(this.getMaximum()-this.getMinimum())));

        this.setPaintTicks(true);
        this.setPaintLabels(true);
    }
    //************************************
    public void setScaledValue(float value){
    	super.setValue((int)(value * this.scale));
    }
    //************************************
    public float getScaledValue() {
         return ((float)super.getValue())/this.scale;
    }
    //************************************
    public float getScaledMinimum(){
    	return ((float)super.getMinimum())/this.scale;
    }
    //************************************    
    public float getScaledMaximum(){
    	return ((float)super.getMaximum())/this.scale;
    }        
    //************************************ 
}
