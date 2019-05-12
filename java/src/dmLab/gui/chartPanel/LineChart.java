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
package dmLab.gui.chartPanel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class LineChart {
    protected String chartTitle;
    protected String xLabel="x";
    protected String yLabel="y";
    
    protected int imgWidth;
    protected int imgHeight;
    
    //**************************    
    public LineChart(String chartTitle, int imageWidth,int imageHeight)
    {                
        this.chartTitle=chartTitle;
        setImageSize(imageWidth,imageHeight);
    }
    //**************************
    public void setImageSize(int imageWidth,int imageHeight)
    {
        imgWidth=imageWidth;
        imgHeight=imageHeight;
    }
    //**************************
    public void setAxisLabels(String xLabel, String yLabel)
    {
        this.xLabel=xLabel;
        this.yLabel=yLabel;
    }
    //**************************
    public BufferedImage draw(String seriesTitle, ArrayList<Float> x, ArrayList<Float> y)
    {  
        float tmpX[]=new float[x.size()];
        float tmpY[]=new float[y.size()];
        for(int i=0;i<x.size();i++)
        {
            tmpX[i]=((Float)x.get(i)).floatValue();
            tmpY[i]=((Float)y.get(i)).floatValue();
        }
        return draw(seriesTitle, tmpX,tmpY);  
    }
    //**************************
    public abstract BufferedImage draw(String seriesTitle, float x[], float y[]);
    public abstract BufferedImage draw(ArrayList<DataSeries> series); 
    public abstract BufferedImage redraw();
    //**************************
}
