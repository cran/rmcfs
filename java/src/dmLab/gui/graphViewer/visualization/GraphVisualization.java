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
package dmLab.gui.graphViewer.visualization;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import dmLab.mcfs.attributesID.graph.GraphEdge;
import dmLab.mcfs.attributesID.graph.GraphNode;
import dmLab.mcfs.attributesID.graph.IDGraph;
import dmLab.utils.GeneralUtils;


public class GraphVisualization 
{
    public static int MAX_SATURATION=100;
    public static int MIN_SATURATION=0;
    
    public static Color EDGE_COLOR=Color.DARK_GRAY;
    public static Color FONT_COLOR=Color.WHITE;
    public static float IMPORTANT_CELL_HUE=0f;
    public static float OTHER_CELL_HUE=0f;
    
    public static int MAX_EDGE_WIDTH=5;
    public static int MIN_EDGE_WIDTH=1;
    
    public static float CENTER_X=400;
    public static float CENTER_Y=230;
    public static float R=200; 

    protected JGraph graph;    
    protected float minImportanceValue;

    private static int edgeWeightFormat=2; 
    //******************************************    
    public GraphVisualization()
    {
        clean();
    }
    //******************************************
    public void clean()
    {
        minImportanceValue=-1;
        graph=null;
    }
    //******************************************   
    public JGraph getGraph()
    {
        return graph;
    }
    //******************************************
    public BufferedImage getImage()
    {
        return graph.getImage(Color.WHITE,1);
    }
    //******************************************
    protected void initGraphModel()
    {
        GraphModel model = new DefaultGraphModel();
        GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
        graph = new JGraph(model, view);
    }
    //******************************************
    public void createGraph(IDGraph connectionsGraph)
    {
        int nodesNumber=connectionsGraph.getNodesNumber();
        int edgesNumber=connectionsGraph.getEdgesNumber();        
                
        //MDR DEBUG
        /*
        System.out.println("******* connectionsGraph **********");              
        System.out.println(connectionsGraph.toString());
        System.out.println("NODES: "+nodesNumber);
        System.out.println("EDGES: "+edgesNumber);
        System.out.println("**********************");        
         //*/

        initGraphModel();
        DefaultGraphCell[] cells = new DefaultGraphCell[nodesNumber+edgesNumber];
        double angleStep=360.0/nodesNumber;                
        GraphNode nodes[]=connectionsGraph.getNodes();

        float maxNodeWeight=connectionsGraph.getMaxNodeWeight();
        float minNodeWeight=connectionsGraph.getMinNodeWeight();        
        
        for(int i=0;i<nodes.length;i++)
        {                                                
            double angle=angleStep*i;
            double a = (Math.sin(angle*Math.PI/180)*R);
            double b = Math.sqrt(Math.pow(R,2.0)-Math.pow(a,2.0));

            double x = CENTER_X+a;
            double y ;

            if(90<angle && angle <=270)
                y = CENTER_Y+b;
            else
                y = CENTER_Y-b;

            //System.out.println(""+attrConnections.nodes.get(i)+ " angle: "+angle + " a: "+a+" b: "+b + " x: "+x+" y: "+y);
            DefaultGraphCell cell = new DefaultGraphCell(nodes[i].name);
            //set color and size of the graph's node/cell
            GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x,y,100,20));
                        
            float nodeSaturation=(nodes[i].weight-minNodeWeight)/(maxNodeWeight-minNodeWeight);            
            nodeSaturation=((MAX_SATURATION-MIN_SATURATION)*nodeSaturation)+MIN_SATURATION;
            nodeSaturation=nodeSaturation/MAX_SATURATION;
            
            Color nodeColor;
            if(minImportanceValue!=-1 && nodes[i].weight<minImportanceValue)
                //nodeColor=Color.getHSBColor(OTHER_CELL_HUE,nodeSaturation,0.7f);
            	nodeColor=Color.getHSBColor(OTHER_CELL_HUE, 0, 0.7f); //all grey
            else
                nodeColor=Color.getHSBColor(IMPORTANT_CELL_HUE,nodeSaturation,0.8f);//red with saturation
                
            setCellDefaultSettings(cell,nodeColor);
            cells[i]=cell;
        }   
        float maxEdgeWeight=connectionsGraph.getMaxEdgeWeight();
        float minEdgeWeight=connectionsGraph.getMinEdgeWeight();
        
        GraphEdge edges[]=connectionsGraph.getEdges();
        //cellIndex points on current position in cells[] 
        int cellIndex=nodesNumber;
        for(int i=0;i<edgesNumber;i++)
        {                                                
            DefaultEdge edge = new DefaultEdge(GeneralUtils.formatFloat(edges[i].weight,edgeWeightFormat));            
            edge.setSource(cells[edges[i].nodeIndex1].getChildAt(0));
            edge.setTarget(cells[edges[i].nodeIndex2].getChildAt(0));

            float edgeWidth=(edges[i].weight-minEdgeWeight)/(maxEdgeWeight-minEdgeWeight);
            edgeWidth=((MAX_EDGE_WIDTH-MIN_EDGE_WIDTH)*edgeWidth)+MIN_EDGE_WIDTH;    
            setEdgeDefaultSettings(edge,edgeWidth);            
            cells[cellIndex++] = edge;
        }

        graph.getGraphLayoutCache().insert(cells);
    }
    //******************************************
    protected void setCellDefaultSettings(DefaultGraphCell cell,Color color)
    {
        //GraphConstants.setGradientColor(cell.getAttributes(), color);        
        GraphConstants.setBackground(cell.getAttributes(), color); 
        GraphConstants.setForeground(cell.getAttributes(), FONT_COLOR);        
        GraphConstants.setOpaque(cell.getAttributes(), true);
        GraphConstants.setAutoSize(cell.getAttributes(), true);
        DefaultPort port = new DefaultPort();
        cell.add(port);        
    }
    //******************************************
    protected void setEdgeDefaultSettings(DefaultEdge edge,float width)
    {
    	GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
        GraphConstants.setLabelAlongEdge(edge.getAttributes(), true);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        GraphConstants.setLineColor(edge.getAttributes(),EDGE_COLOR);
        GraphConstants.setLineWidth(edge.getAttributes(), width);
    }
    //******************************************
    public boolean saveGraph(String fileName)
    {
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File(fileName));
        } catch (FileNotFoundException e1) {
            System.err.println("File not found. File: "+fileName);
            return false;
        }
        XMLEncoder enc = new XMLEncoder(out);
        enc.setExceptionListener(new ExceptionListener() {
            public void exceptionThrown(Exception e) {
            }
        });

        configureEncoder(enc);
        enc.writeObject(graph.getGraphLayoutCache());
        enc.close();
        
        return true;
    }
    //******************************************    
    protected void configureEncoder(XMLEncoder encoder)
    {    

        try {    
            encoder.setPersistenceDelegate(DefaultGraphModel.class,
                    new DefaultPersistenceDelegate(new String[] { "roots","attributes" }));
            encoder.setPersistenceDelegate(GraphLayoutCache.class,
                    new DefaultPersistenceDelegate(new String[] { "model", "factory", "cellViews", "hiddenCellViews", "partial" }));
            encoder.setPersistenceDelegate(DefaultGraphCell.class,
                    new DefaultPersistenceDelegate( new String[] { "userObject" }));
            encoder.setPersistenceDelegate(DefaultEdge.class,
                    new DefaultPersistenceDelegate( new String[] { "userObject" }));
            encoder.setPersistenceDelegate(DefaultPort.class,
                    new DefaultPersistenceDelegate( new String[] { "userObject" }));
            encoder.setPersistenceDelegate(AbstractCellView.class, new DefaultPersistenceDelegate(new String[] {"cell", "attributes"}));

            encoder.setPersistenceDelegate( DefaultEdge.DefaultRouting.class,
                    new PersistenceDelegate() 
                    {
                        @Override
                        protected Expression instantiate(Object oldInstance, Encoder out) 
                        {
                            return new Expression(oldInstance,GraphConstants.class,"getROUTING_SIMPLE", null);
                        }
                    });

            encoder.setPersistenceDelegate(DefaultEdge.LoopRouting.class,new PersistenceDelegate() 
            {
                @Override
                protected Expression instantiate(Object oldInstance, Encoder out) 
                {
                    return new Expression(oldInstance,GraphConstants.class,"getROUTING_DEFAULT", null);
                }
            });

            encoder.setPersistenceDelegate(ArrayList.class, encoder.getPersistenceDelegate(List.class));
            //encoder.writeObject(graph.getGraphLayoutCache());
            //encoder.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(graph, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
//  ******************************************
    public boolean loadGraph(String fileName)
    {
        FileInputStream in;
        try {
            in = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e1) {
            System.err.println("File not found. File: "+fileName);
            return false;
        }

        XMLDecoder dec = new XMLDecoder(in);
        if (dec != null)
        {
            initGraphModel();            
            graph.setGraphLayoutCache((GraphLayoutCache)dec.readObject());
            dec.close();            
        }
        return true;
    }
//  ******************************************    
    public JGraph test()
    {
        GraphModel model = new DefaultGraphModel();
        GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
        graph = new JGraph(model, view);
        
        DefaultGraphCell[] cells = new DefaultGraphCell[3];

        cells[0] = new DefaultGraphCell(new String("Hello"));
        GraphConstants.setBounds(cells[0].getAttributes(), new Rectangle2D.Double(20,20,40,20));
        GraphConstants.setGradientColor(cells[0].getAttributes(), Color.green);
        GraphConstants.setOpaque(cells[0].getAttributes(), true);
        DefaultPort port0 = new DefaultPort();
        cells[0].add(port0);

        cells[1] = new DefaultGraphCell(new String("World"));
        GraphConstants.setBounds(cells[1].getAttributes(), new Rectangle2D.Double(140,140,40,20));
        GraphConstants.setGradientColor(cells[1].getAttributes(), Color.red);
        GraphConstants.setOpaque(cells[1].getAttributes(), true);
        DefaultPort port1 = new DefaultPort();
        cells[1].add(port1);

        DefaultEdge edge = new DefaultEdge();
        edge.setSource(cells[0].getChildAt(0));
        edge.setTarget(cells[1].getChildAt(0));
        cells[2] = edge;

        int arrow = GraphConstants.ARROW_CLASSIC;
        GraphConstants.setLineEnd(edge.getAttributes(), arrow);
        GraphConstants.setEndFill(edge.getAttributes(), true);

        graph.getGraphLayoutCache().insert(cells);

        return graph;
    }
//  ******************************************    
    public void setMinImportanceValue(float minImportanceValue)
    {
        this.minImportanceValue = minImportanceValue;
    }
//  ******************************************
}
