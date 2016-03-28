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
package dmLab.gui.chartPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;

public class MyLineChart extends LineChart{
    
    protected float x[];
    protected float y[];
    protected float xValues[];
    protected float yValues[];    
    protected float maxX;
    protected float maxY;
    protected float minX;
    protected float minY;
    
    private final int defaultBorder=20;
    private final int maxNumberOfLabes=10;
    private final int defaultArrowSize=5;
    
    //**************************    
    public MyLineChart(String chartTitle,int imageWidth,int imageHeight)
    {                
        super(chartTitle,imageWidth,imageHeight);
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
        return draw(seriesTitle,tmpX,tmpY);  
    }
    //**************************
    @Override
    public BufferedImage draw(ArrayList<DataSeries> series) {
        DataSeries s = series.get(0);
        return draw(s.seriesTitle,s.x,s.y);
    }
    //**************************
    public BufferedImage draw(String seriesTitle, float x[], float y[])
    {       
        this.x=x.clone();
        this.y=y.clone();
        this.xValues=x.clone();
        this.yValues=y.clone();

        maxX=MathUtils.maxValue(x);
        maxY=MathUtils.maxValue(y);
        minX=MathUtils.minValue(x);
        minY=MathUtils.minValue(y);

        if(x.length==y.length)
        {
            for(int i=0;i<x.length;i++)
            {
                if(maxX-minX!=0)
                    this.x[i]=(x[i]-minX)/(maxX-minX);
                if(maxY-minY!=0)
                    this.y[i]=1-((y[i]-minY)/(maxY-minY));
            }
        }
        return redraw();     
    }
    //**************************
    public BufferedImage redraw()
    {               
        if(x==null || y == null)
            return null;
        
        BufferedImage img = new BufferedImage(imgWidth, imgHeight,BufferedImage.TYPE_INT_RGB);
        Graphics g=img.getGraphics();
        //setting background
        g.setColor(Color.white);        
        Polygon p=new Polygon();
        p.addPoint(0,0);
        p.addPoint(imgWidth,0);
        p.addPoint(imgWidth,imgHeight);
        p.addPoint(0,imgHeight);        
        g.fillPolygon(p);
        //drawing
        int border=defaultBorder;
        int arrowSize=defaultArrowSize;
        g.setColor(Color.black);
        //x axis
        g.drawLine(border,imgHeight-border,imgWidth-border,imgHeight-border);
        g.drawLine(imgWidth-border,imgHeight-border,imgWidth-border-arrowSize,imgHeight-border+arrowSize);
        g.drawLine(imgWidth-border,imgHeight-border,imgWidth-border-arrowSize,imgHeight-border-arrowSize);
        //y axis
        g.drawLine(border,border,border,imgHeight-border);                
        g.drawLine(border,border,border+arrowSize,border+arrowSize);
        g.drawLine(border,border,border-arrowSize,border+arrowSize);        

        float sizeX=imgWidth-2*border;        
        float sizeY=imgHeight-2*border;
        int coordinateX=0,lastCoordinateX=0;
        int coordinateY=0,lastCoordinateY=0;
                      
        if(x.length!=y.length)
            g.drawString("Error x.length!=y.length",imgWidth/4,imgHeight/2);
        else
        {
            int scale=1;
            if(x.length>maxNumberOfLabes)
                scale=(int)Math.ceil((double)x.length/(double)maxNumberOfLabes);
            if(scale<1) scale=1;

            for(int i=0;i<x.length;i++)
            {               
                coordinateX=(int) (border+sizeX*x[i]);
                coordinateY=(int) (border+sizeY*y[i]);
                //painting labels
                if(i%scale==0)
                {
                    g.drawString(GeneralUtils.format(xValues[i],1),coordinateX,imgHeight);
                    g.drawString(GeneralUtils.format(yValues[i],1),0,coordinateY);
                }
                //drawing lines
                if(i>=1)
                    g.drawLine(lastCoordinateX,lastCoordinateY,coordinateX,coordinateY);
                lastCoordinateX=coordinateX;
                lastCoordinateY=coordinateY;
            }
        }
        return img;
    }
    //**************************
}
