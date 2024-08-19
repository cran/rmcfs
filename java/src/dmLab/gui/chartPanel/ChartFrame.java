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

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * @author mdramins
 */
public class ChartFrame extends JFrame  implements ComponentListener 
{
    private static final long serialVersionUID = 1L;
    
    protected ImagePanel panel;
    protected int defaultWidth=640;
    protected int defaultHeight=480;
    protected LineChart lineChart;
    
    private boolean jChartON = true;
    //**********************
    public ChartFrame(String windowTitle, String chartTitle)
    {    	
        super(windowTitle);        
        panel = new ImagePanel(new BorderLayout());        
        this.getContentPane().add(panel,BorderLayout.CENTER);
        panel.setLayout(new BorderLayout());
        this.setSize(defaultWidth, defaultHeight);
        if(jChartON)
            lineChart = new JLineChart(chartTitle,this.getContentPane().getWidth(), this.getContentPane().getHeight());
        else
            lineChart = new MyLineChart(chartTitle,this.getContentPane().getWidth(), this.getContentPane().getHeight());
        
        lineChart.setAxisLabels("x", "y");
        panel.addComponentListener(this);
        this.setVisible(true);
    }
    //**************************
    public void draw(ArrayList<DataSeries> series)
    {
        lineChart.setImageSize(this.getContentPane().getWidth(), this.getContentPane().getHeight());
        panel.setImage(lineChart.draw(series));
        panel.repaint();        
    }
    //**************************
    public void redraw()
    {       
        lineChart.setImageSize(this.getContentPane().getWidth(), this.getContentPane().getHeight());
        panel.setImage(lineChart.redraw());
        panel.repaint();        
    }
    //***********************************
    public void setAxisLabels(String xLabel, String yLabel)
    {
    	lineChart.setAxisLabels(xLabel, yLabel);
    }
    //***********************************
    public void componentResized(ComponentEvent e) 
    {
        redraw();
    }
    //***********************************
    public void componentMoved(ComponentEvent arg0)
    {
        //System.out.println("*** componentMoved");
        redraw();
    }
    //***********************************
    public void componentShown(ComponentEvent arg0)
    {     
        //System.out.println("*** componentShown");
        redraw();
    }
    //***********************************
    public void componentHidden(ComponentEvent arg0)
    {
        //System.out.println("*** componentHidden");
        redraw();
    }
    //***********************************
}
