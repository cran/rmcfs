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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class JLineChart extends LineChart{

    private XYSeriesCollection myDataset;
    private ArrayList<Boolean> seriesMainAxisIndicator;
    private Color[] colors={Color.red,Color.blue,Color.black,Color.green,Color.magenta,Color.yellow,Color.orange,Color.gray};
    public boolean axis2ON=true;
    // ***********************************************    
    public JLineChart(String chartTitle, int imageWidth, int imageHeight) {
        super(chartTitle, imageWidth, imageHeight);        
    }
    // ***********************************************
    @Override
    public BufferedImage draw(String seriesTitle, float[] x, float[] y) 
    {
        addSeries(seriesTitle,x,y,true,true);
        return redraw();
    }
    // ***********************************************
    @Override
    public BufferedImage draw(ArrayList<DataSeries> series) {
        for(int i=0;i<series.size();i++)
        {
            DataSeries s = series.get(i);
            if(i==0)
                addSeries(s.seriesTitle,s.x,s.y,s.mainAxis,true);
            else
                addSeries(s.seriesTitle,s.x,s.y,s.mainAxis,false);
        }
        return redraw();
    }
    // ***********************************************
    @Override
    public BufferedImage redraw() 
    {
        if(myDataset == null)
            return null;
        
        JFreeChart chart = createChart(getDataset());
        return chart.createBufferedImage(imgWidth, imgHeight, null);
    }
    // ***********************************************
    private Dataset getDataset(){
        return myDataset;
    }
    // ***********************************************
    private void addSeries(String seriesTitle, float[] x, float[] y, boolean mainAxis, boolean flushDataset)
    {
        if(flushDataset || myDataset == null)
        {
        	myDataset = new XYSeriesCollection();
        	seriesMainAxisIndicator = new ArrayList<Boolean>();
        }
        XYSeries series = new XYSeries(seriesTitle);
        series.setDescription(seriesTitle);
        
        for (int i = 0; i < x.length; i++)
            series.add(x[i], y[i]);

        myDataset.addSeries(series);
        seriesMainAxisIndicator.add(mainAxis);
    }
    // ***********************************************
    private JFreeChart createChart(Dataset dataset) {
        
    	int colorIndex=0;
    	boolean legendOn = false;
        if(((XYSeriesCollection)dataset).getSeriesCount() > 1)
            legendOn = true;

        XYSeriesCollection datasetMainAxis;
        XYSeriesCollection datasetSecondAxis=null;
        //if two axis split the data into two datasets
        if(axis2ON){
        	datasetMainAxis = new XYSeriesCollection();
        	datasetSecondAxis = new XYSeriesCollection();
        	int size = ((XYSeriesCollection)dataset).getSeriesCount();        	
        	for(int i=0;i<size;i++){
                XYSeries series = ((XYSeriesCollection)dataset).getSeries(i);
        		if(seriesMainAxisIndicator.get(i))
        			datasetMainAxis.addSeries(series);
        		else
        			datasetSecondAxis.addSeries(series);	
        	}
        }
        else
        	datasetMainAxis=(XYSeriesCollection)dataset;
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
            chartTitle,      // chart title
            xLabel,                      // x axis label
            yLabel,                      // y axis label
            (XYDataset)datasetMainAxis,                  // data
            PlotOrientation.VERTICAL,
            legendOn,                     // include legend
            true,                     // tooltips
            false                     // urls
        );
        
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
        //final StandardLegend legend = (StandardLegend) chart.getLegend();
        //legend.setDisplaySeriesShapes(true);
        
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        final XYLineAndShapeRenderer rendererMain = new XYLineAndShapeRenderer();
        int seriesNumber = ((XYSeriesCollection)datasetMainAxis).getSeriesCount();
        for(int i=0;i<seriesNumber;i++){
        	rendererMain.setSeriesPaint(i, colors[colorIndex++]);
            rendererMain.setSeriesLinesVisible(i, true);
            rendererMain.setSeriesShapesVisible(i, false);            
        }                
        plot.setRenderer(rendererMain);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.
              
        if(axis2ON){
        	seriesNumber = ((XYSeriesCollection)datasetSecondAxis).getSeriesCount();
        	String secondAxisLabel="";
        	//create second axis label
        	for(int i=0;i<seriesNumber;i++){
        		XYSeries series = ((XYSeriesCollection)datasetSecondAxis).getSeries(i);
        		if(i!=0)
        			secondAxisLabel += "/";
        		secondAxisLabel += series.getDescription();        		
        	}
        	final NumberAxis axis2 = new NumberAxis(secondAxisLabel+" values");
	        axis2.setAutoRangeIncludesZero(false);	        
	        plot.setRangeAxis(1, axis2);                
	        plot.setDataset(1,datasetSecondAxis);
	        plot.mapDatasetToRangeAxis(1, 1);
	
	        final XYLineAndShapeRenderer rendererSecondAxis = new XYLineAndShapeRenderer();
	        for(int i=0;i<seriesNumber;i++){
	        	rendererSecondAxis.setSeriesPaint(i, colors[colorIndex++]);
	        	rendererSecondAxis.setSeriesLinesVisible(i, true);
	        	rendererSecondAxis.setSeriesShapesVisible(i, false);
	        }
	        plot.setRenderer(1, rendererSecondAxis);        
        }        
        return chart;
    }
}
