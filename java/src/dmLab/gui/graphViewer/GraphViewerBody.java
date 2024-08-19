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
package dmLab.gui.graphViewer;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import dmLab.experiment.ExperimentParams;
import dmLab.gui.dataEditor.windows.AboutWindow;
import dmLab.gui.graphViewer.components.GraphViewerMenu;
import dmLab.gui.graphViewer.components.GraphViewerToolBar;
import dmLab.gui.graphViewer.visualization.GraphVisualization;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesID.graph.IDGraph;
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.utils.FileUtils;
import dmLab.utils.fileFilters.ComplexFileFilter;

public class GraphViewerBody extends javax.swing.JFrame implements ActionListener
{
    private GraphViewerMenu menuBar;
    private JFileChooser fileDialog;
    private GraphViewerToolBar toolBar;
    private JScrollPane graphPane;
    private JSplitPane horizontalSplitPanel;
    
    public AttributesID connections;
    public IDGraph graph;    
    public GraphVisualization graphVisualization;    
    
    private AboutWindow aboutWindow;    
    private static final String GUITitle="dmLab - ID Graphs Viewer";
    
    public static final int DEFAULT_NODES_NUMBER = 20;
    public static final int DEFAULT_EDGES_NUMBER = 20;
    public static int TOP_SET_MAX_SLIDER_VALUE = 500;
    
    private static final long serialVersionUID = -5269030527817628635L;
//  ************************************************    
    public GraphViewerBody()
    {        
        super();
        graphVisualization=new GraphVisualization();
        connections= new AttributesID(true, false);        
        initGUI();
    }
//  ************************************************    
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        

        if (eventObject == menuBar.openFileMenuItem) 
            openFile();
        else if(eventObject == menuBar.saveFileMenuItem)
            saveFile();
        else if(eventObject == menuBar.closeFileMenuItem)
            closeFile();
        else if(eventObject == menuBar.exitMenuItem)
            exitProgram();
        else if(eventObject == menuBar.aboutMenuItem)
        {
            if(aboutWindow==null)
                aboutWindow=new AboutWindow((new GraphViewerInfo()).toString());
            aboutWindow.setVisible(true);
        }
    }
//  ************************************************
    private void initGUI()
    {        
        this.setTitle(GraphViewerBody.GUITitle);     
        initFileDialog(); 
        createMenu();

        this.setSize(800, 700); 
        this.setLocation(200, 100);

        toolBar=new GraphViewerToolBar(this);
        toolBar.addActionListener(this);        
        
        horizontalSplitPanel = new JSplitPane();
        getContentPane().add(horizontalSplitPanel, BorderLayout.CENTER);        
        horizontalSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        
        horizontalSplitPanel.add(toolBar, JSplitPane.TOP);
        toolBar.setPreferredSize(new java.awt.Dimension(790, 100));
        drawGraph();
    }
    //*****************************************
    public void drawGraph()
    {        
        if(connections.getNodesNumber()==0)
        {
            if(graphVisualization.getGraph()!=null)
                graphPane=new JScrollPane(graphVisualization.getGraph());
            else
                graphPane=new JScrollPane();            
        }
        else
        {   
			//System.out.println("MDR: GENE2426X - GENE1613X "+connections.getConnection("GENE2426X","GENE1613X").toString());
			//System.out.println("MDR: GENE1613X - GENE2426X "+connections.getConnection("GENE1613X","GENE2426X").toString());
            graph = connections.toGraph(toolBar.getConnectionWeight(),toolBar.getAttributesImportance(),toolBar.getTopSetSize());
            graphVisualization.createGraph(graph);            
            graphPane=new JScrollPane(graphVisualization.getGraph());
        }       
        
        horizontalSplitPanel.add(graphPane, JSplitPane.BOTTOM);                  
        graphPane.setPreferredSize(new java.awt.Dimension(790, 500));
    } 
    //*****************************************    
    private void createMenu()
    {
        menuBar = new GraphViewerMenu();
        menuBar.addActionListener(this);
        this.setJMenuBar(menuBar);
        menuBar.setPreferredSize(new java.awt.Dimension(392, 18));        
    } 
    //************************************************
    private void initFileDialog()
    {        
        fileDialog = new JFileChooser(ExperimentParams.DEFAULT_RES_PATH);
        fileDialog.setLocation(100, 100);        
        ComplexFileFilter[] fileFilters = new ComplexFileFilter[] {
                new ComplexFileFilter(new String[]{"connections.csv"},"CSV Connections File")
                ,new ComplexFileFilter(new String[]{"xml"},"JGraph XML File")
        };

        for(int i=0;i<fileFilters.length;i++)
            fileDialog.addChoosableFileFilter(fileFilters[i]);

        fileDialog.addChoosableFileFilter(new ComplexFileFilter(new String[]{"csv","xml"},"All Supported Files"));        
    }
    //************************************************
    private void exitProgram()
    {
        this.dispose();
    }
    //******************************************
    private void openFile()
    {
    	int returnVal = fileDialog.showOpenDialog(this);
    	boolean isConnectionFileLoaded=false;

    	if (returnVal == JFileChooser.CANCEL_OPTION)
    		return;            
    	else if (returnVal == JFileChooser.APPROVE_OPTION)
    	{
    		closeFile();
    		File connectionsfile = fileDialog.getSelectedFile();            
    		System.out.println("Loading connections file: " + connectionsfile.getAbsolutePath());
    		String experimentPrefix = MCFSParams.getExperimentName(connectionsfile.getAbsolutePath());
    		isConnectionFileLoaded = false;
    		//System.out.println(experimentPrefix);
    		if(FileUtils.getFileExtension(connectionsfile.getName()).equalsIgnoreCase("csv"))
    			isConnectionFileLoaded = connections.load(connectionsfile.getAbsolutePath());                    
    		else if(FileUtils.getFileExtension(connectionsfile.getName()).equalsIgnoreCase("xml"))
    			isConnectionFileLoaded= graphVisualization.loadGraph(connectionsfile.getAbsolutePath());

    		if (isConnectionFileLoaded)
    		{
    			System.out.println("Connections are loaded.");
    			this.setTitle(GraphViewerBody.GUITitle+" - "+connectionsfile.getName());

    			//load also importance file
    			File importanceFile = new File(experimentPrefix+"_"+MCFSParams.FILESUFIX_RI);
    			System.out.println("Loading importances file: " + importanceFile.getAbsolutePath());
    			if(!toolBar.loadImportanceFile(importanceFile))
    				System.out.println("Lading importances failed.");

    			toolBar.initWeightSlider(connections);
    			toolBar.initTopSetSlider(connections);

    			//TODO additionally load minID from cutoff file
    			//load also topRanking file
    			Ranking topRanking=new Ranking();
    			File topRankingFile = new File(experimentPrefix+MCFSParams.FILESUFIX_TOPRANKING);
    			System.out.println("Loading topRanking file: "+topRankingFile.getAbsolutePath());
    			int topRankingSize = connections.getNodesNumber();
    			float minID = connections.getIDWeight(DEFAULT_EDGES_NUMBER);
    			if(toolBar.isImportanceLoaded()){
    				if(topRanking.load(topRankingFile.getAbsolutePath())){	            	   
    					topRankingSize = topRanking.size();
    					graphVisualization.setMinImportanceValue(topRanking.getWeight(topRanking.size()-1));
    				}else{
    					System.out.println("Lading topRanking failed.");
    					topRankingSize = DEFAULT_NODES_NUMBER;
    				}
    				minID = 0.0f;
    			}
    			
    			toolBar.setTopRankingSize(topRankingSize,true);                   
    			toolBar.setConnectionWeight(minID,true);

    			drawGraph();
    		}
    	}
    }
    //*************************************************************
    private void saveFile()
    {
        int returnVal = fileDialog.showSaveDialog(this);
        boolean isSaved=false;
        
        if (returnVal == JFileChooser.CANCEL_OPTION)
            return;
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fileDialog.getSelectedFile();            
            
            String filePath=file.getAbsolutePath();
            
            if(((ComplexFileFilter)fileDialog.getFileFilter()).accept("csv"))
            {                
                if(!FileUtils.getFileExtension(file.getName()).equalsIgnoreCase("csv"))
                    filePath+=".csv";                    
                
                System.out.println("Saving: " + filePath);
                if(connections.filter(toolBar.getConnectionWeight(),toolBar.getAttributesImportance(),toolBar.getTopSetSize()).save(filePath))
                    isSaved=true;
            }
            else if(((ComplexFileFilter)fileDialog.getFileFilter()).accept("xml"))
            {                
                if(!FileUtils.getFileExtension(file.getName()).equalsIgnoreCase("xml"))
                    filePath+=".xml";                    
                
                System.out.println("Saving: " + filePath);
                if(graphVisualization.saveGraph(filePath))
                    isSaved=true;
            }
            else //if filter is general
            {
                if(FileUtils.getFileExtension(file.getName()).equalsIgnoreCase("csv"))
                {
                    System.out.println("Saving: " + filePath);
                    if(connections.filter(toolBar.getConnectionWeight(),toolBar.getAttributesImportance(),toolBar.getTopSetSize()).save(filePath))
                        isSaved=true;
                }
                else if(FileUtils.getFileExtension(file.getName()).equalsIgnoreCase("xml"))
                {
                    System.out.println("Saving: " + filePath);
                    if(graphVisualization.saveGraph(filePath))
                        isSaved=true;
                }
            }
            
            if(isSaved)
                System.out.println("File is saved.");
        }
    }
    //******************************************
    private void closeFile()    
    {           
        if(connections.getNodesNumber()>0 || graphVisualization.getGraph()!=null)
        {
            System.out.println("Closing file");
            connections.init();
            System.out.println("File is closed.");
            this.setTitle(GraphViewerBody.GUITitle);
            graphVisualization.clean();
            toolBar.cleanGUI();
            drawGraph();
        }  
    }
    //******************************************
    public void run()
    {
        System.out.print((new GraphViewerInfo()).toString());        
        setVisible(true);
    }
    //************************************************

}
