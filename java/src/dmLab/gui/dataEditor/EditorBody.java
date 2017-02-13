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
package dmLab.gui.dataEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.table.TableModel;

import dmLab.DMLabInfo;
import dmLab.array.Array;
import dmLab.array.SArray;
import dmLab.array.functions.ExtFunctions;
import dmLab.array.functions.SelectFunctions;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.array.meta.Attribute;
import dmLab.gui.dataEditor.components.EditorMenuBar;
import dmLab.gui.dataEditor.components.EditorToolBar;
import dmLab.gui.dataEditor.panels.AttrFilterPanel;
import dmLab.gui.dataEditor.panels.AttributesPanel;
import dmLab.gui.dataEditor.panels.EventsFilterPanel;
import dmLab.gui.dataEditor.panels.EventsPanel;
import dmLab.gui.dataEditor.panels.OptionsPanel;
import dmLab.gui.dataEditor.panels.ReplacePanel;
import dmLab.gui.dataEditor.utils.InfoStream;
import dmLab.gui.dataEditor.windows.AboutWindow;
import dmLab.gui.dataEditor.windows.DBConnectionWindow;
import dmLab.utils.fileFilters.MyFileFilter;

public class EditorBody extends javax.swing.JFrame implements ActionListener
{
    private static final long serialVersionUID = -1546562029839434530L;
    private static String GUITitle = "dmLab - Data Editor";

    private EditorMenuBar editorMenuBar;
    private EditorToolBar toolBar;
    
    private JSplitPane horizontalSplitPanel;
    private JSplitPane verticalSplitPanel;
    
    private JTabbedPane tabbedPanel;
    private JScrollPane scrollInfoPanel;
    private JTextPane infoPanel;
    private OptionsPanel optionsPanel;
    private ReplacePanel replacePanel;
    private EventsFilterPanel filterPanel;
    private AttrFilterPanel attrFilterPanel;
    
    private AttributesPanel attributesPanel;
    private EventsPanel eventsPanel;
    
    private JProgressBar progressBar;
    private JFileChooser fileDialog;

    private InfoStream outStream;
    private InfoStream errStream;
    private ContainerOperations containerOperations;
    
    private AboutWindow aboutWindow;
    private DBConnectionWindow dbConnectionWindow;
//  ************************************
    public void run()
    {    	
        EditorBody editor = new EditorBody();
        editor.setVisible(true);
    }
//  ************************************
    public EditorBody()
    {        
        super();
        
        System.out.print((new DMLabInfo()).toString());
        containerOperations= new ContainerOperations();
        
        progressBar = new JProgressBar();
        containerOperations.setProgressBar(progressBar);
                
        outStream= new InfoStream();
        errStream= new InfoStream();
        
        //this part is good for deploying purposes
        //all error messages are going out from the screen
        /*
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));        
        */
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        aboutWindow=new AboutWindow((new EditorInfo()).toString());
        dbConnectionWindow=new DBConnectionWindow();
        dbConnectionWindow.addActionListener(this);        
        
        initGUI();
    }
//  ************************************
    private void initGUI()
    {
        try
        {
            this.setTitle(EditorBody.GUITitle);		
            initFileDialog(); 
        	createMenu();
        	
        	this.setSize(800, 600);	
            this.setLocation(100, 100);
            horizontalSplitPanel = new JSplitPane();
            verticalSplitPanel = new JSplitPane();
            scrollInfoPanel=new JScrollPane();
            infoPanel = new JTextPane();
            progressBar = new JProgressBar();
            tabbedPanel = new JTabbedPane();
            
            optionsPanel = new OptionsPanel();            
            replacePanel = new ReplacePanel();                        
            attributesPanel = new AttributesPanel();
            filterPanel = new EventsFilterPanel();
            attrFilterPanel = new AttrFilterPanel();
            eventsPanel = new EventsPanel();
            
            toolBar=new EditorToolBar();
            toolBar.addActionListener(this);
            
            getContentPane().add(toolBar, BorderLayout.NORTH);
            getContentPane().add(horizontalSplitPanel, BorderLayout.CENTER);
            getContentPane().add(progressBar, BorderLayout.SOUTH);
            
            horizontalSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
            horizontalSplitPanel.add(verticalSplitPanel, JSplitPane.TOP);                        
            horizontalSplitPanel.add(eventsPanel, JSplitPane.BOTTOM);             
            
            tabbedPanel.addTab("Info",null,scrollInfoPanel,null);
            tabbedPanel.addTab("Options", null, optionsPanel, null);            
            tabbedPanel.addTab("Replace", null, replacePanel, null);
            tabbedPanel.addTab("Filter Events", null, filterPanel, null);            
            tabbedPanel.addTab("Filter Attrs", null, attrFilterPanel, null);
            
            replacePanel.addActionListener(this);
            optionsPanel.addActionListener(this);            
            filterPanel.addActionListener(this);
            attrFilterPanel.addActionListener(this);
            
            infoPanel.setText("Please Load the Data.\n");
            scrollInfoPanel.setViewportView(infoPanel);
            
            attributesPanel.addActionListener(this);
            attributesPanel.setPreferredSize(new java.awt.Dimension(400, 400));
            attributesPanel.setPointers(containerOperations,eventsPanel);
                                   
            verticalSplitPanel.add(attributesPanel, JSplitPane.LEFT);   
            verticalSplitPanel.add(tabbedPanel, JSplitPane.RIGHT);            
            verticalSplitPanel.setPreferredSize(new java.awt.Dimension(800, 400));
            
            outStream.setOutput(infoPanel, InfoStream.OUT);
            errStream.setOutput(infoPanel, InfoStream.ERR);                        
            
            eventsPanel.setPointers(containerOperations, attributesPanel, optionsPanel);
            eventsPanel.fillEventsTable();
            initProgressBar();
                                        
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //*****************************************    
    private void createMenu()
    {
    	editorMenuBar = new EditorMenuBar();
        editorMenuBar.addActionListener(this);
    	this.setJMenuBar(editorMenuBar);
    	editorMenuBar.setPreferredSize(new java.awt.Dimension(392, 18));    	
    }    
    //*****************************************
    private void initFileDialog()
    {
        fileDialog = new JFileChooser("D://JAVA Source//dmLAB//");
        fileDialog.setLocation(100, 100);
        MyFileFilter fileFilters[]=FileType.getSupportedTypes();        
        for(int i=0;i<fileFilters.length;i++)
            fileDialog.addChoosableFileFilter(fileFilters[i]);
        
        fileDialog.addChoosableFileFilter(new MyFileFilter("","All Supported Files"));        
    }
    //******************************************
    private void initProgressBar()
    {
        progressBar.setValue(0);
        progressBar.setForeground(new Color(200,0,0));
        progressBar.setStringPainted(true);
    }
    //******************************************
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        

        if (eventObject == editorMenuBar.openFileMenuItem || eventObject==toolBar.openButton) 
            openFile();
        else if(eventObject == editorMenuBar.openDBMenuItem || eventObject==toolBar.dbButton)
        {            
            if(dbConnectionWindow==null)
                dbConnectionWindow=new DBConnectionWindow();
            dbConnectionWindow.setVisible(true);
        }
        else if(eventObject == editorMenuBar.saveAsMenuItem || eventObject==toolBar.saveButton)
        {
            updateMemory();
            updateGUI();//update and save
            saveAsFile();
        }
        else if(eventObject == editorMenuBar.closeFileMenuItem)
            closeFile();
        else if(eventObject == editorMenuBar.exitMenuItem)
            exitProgram();
        else if(eventObject == editorMenuBar.aboutMenuItem)
        {
            if(aboutWindow==null)
                aboutWindow=new AboutWindow((new EditorInfo()).toString());
            aboutWindow.setVisible(true);
        }
        else if(eventObject == replacePanel.replaceButton)
            replace();
        else if(eventObject == attributesPanel.decisionSetButton)
            setAsDecisionAttribute();
        else if(eventObject == attributesPanel.addNewAttributeButton)
            addNewAttribute();
        else if(eventObject == attributesPanel.removeAttributeButton)
            removeAttribute();
        else if(eventObject == toolBar.fixTypesButton)
            fixAttrTypes();
        else if(eventObject == toolBar.reloadButton)
        {
        	updateMemory();
            updateGUI();
        }
        else if(eventObject == filterPanel.filterButton)
        	filterEvents();
        else if(eventObject == attrFilterPanel.filterButton)
            filterAttributes();
        else if (eventObject == dbConnectionWindow.dbConnectionPanel.runQueryButton)                                                            
            openDB();          
        
    }
    //******************************************
    private void openDB()
    {
        if(containerOperations.loadFromDB(dbConnectionWindow.dbConnectionPanel.getResultSet()))
        {
            System.out.println("Query loaded.");
            containerOperations.findDomains();                
            updateGUI();
            dbConnectionWindow.setVisible(false);
        }
    }
//  ******************************************
    private void openFile()
    {
        int returnVal = fileDialog.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fileDialog.getSelectedFile();            
            System.out.println("Opening: " + file.getName() + ".");
            if(optionsPanel.getSeparatorTab())
                containerOperations.setSeparatorCSV('\t');
            else    
                containerOperations.setSeparatorCSV(optionsPanel.getSeparatorCSV());
            
            containerOperations.setFirstLineContainsAttributesCSV(optionsPanel.getFirstLineContainsAttributesCSV());
            containerOperations.setDefaultAttributeNameCSV(optionsPanel.getDefaultAtributeNameCSV());
            containerOperations.setTrimCommentsCSV(optionsPanel.getTrimCommentsCSV());
            containerOperations.setConsequentSeparatorsTreatAsOneCSV(optionsPanel.getConsequentSeparatorsTreatAsOne());
            
            if(containerOperations.loadFromFile(file.getAbsolutePath())){
                System.out.println("File loaded.");
                this.setTitle(EditorBody.GUITitle+" - "+containerOperations.getFileName());
                containerOperations.findDomains();                
                updateGUI();
            }
        }
    }
    //******************************************
    private void saveAsFile()	
    {	
        int returnVal = fileDialog.showSaveDialog(this);
        char separator;

        if(optionsPanel.getSeparatorTab())
            separator='\t';
        else    
            separator=optionsPanel.getSeparatorCSV();
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            updateMemory();
            File file = fileDialog.getSelectedFile();
            System.out.println("Saving: " + file.getName() + ".");
            containerOperations.setSeparatorCSV(separator);
            String ext="."+((MyFileFilter)fileDialog.getFileFilter()).getExt();
            
            if(ext.equalsIgnoreCase("."))//if havent been selected use path
                ext="";
            
            String path=file.getAbsolutePath()+ext;           
            if(containerOperations.saveToFile(path))
                System.out.println("File is saved.");
        }
    }
    //******************************************
    private void closeFile()    
    {           
        if(containerOperations.isFileOpen())
        {
            System.out.println("Closing: " + containerOperations.getFileName() + ".");
            containerOperations.initContainer();
            System.out.println("File is closed.");
            this.setTitle(EditorBody.GUITitle);
            
            replacePanel.setAttributes(null);
            attributesPanel.setAttributes(null);
            eventsPanel.fillEventsTable();
            attrFilterPanel.init();
        }        
    }
    //******************************************
    private void setAsDecisionAttribute()
    {
        if(containerOperations.isFileOpen())
        { 
	    	String selected=attributesPanel.getAnchorAttributeName();
	    	attributesPanel.setDecisionAttribute(selected);
	    	System.out.println("Attribute "+ selected +" is new Decision Attribute.");
        }
    }
    //******************************************
    private void exitProgram()
    {
        this.dispose();
    }
    //******************************************
    private void replace()
    {
        if(containerOperations.isFileOpen())
        {            
            int replaced = containerOperations.replaceValue(replacePanel.getSourceValue(),replacePanel.getDestinationValue(),
                        replacePanel.getSelectionMask(),replacePanel.getWholeWord());

            System.out.println(""+replaced +" values have been replaced.");
            eventsPanel.fillEventsTable();
            updateGUI();
        }
    }
    //******************************************
    private void fixAttrTypes()
    {
        if(containerOperations.isFileOpen())
        { 	    	
        	containerOperations.fixAttrTypes();
        	containerOperations.fixAttrValues();
	    	System.out.println("Types and Values of Attributes have been fixed.");    	
	    	updateGUI();
        }
    }
    //******************************************
    private void filterEvents()
    {
        if(containerOperations.isFileOpen()){ 
            containerOperations.container = SelectFunctions.selectRows(containerOperations.container, filterPanel.getFilter());
        	containerOperations.findDomains();
        	updateGUI();
        }
    }
    //******************************************
    private void filterAttributes()
    {
        if(attrFilterPanel.importances!=null)
        {
            int measureIndex=attrFilterPanel.importances.getMeasureIndex(attrFilterPanel.getMeasureName());
            boolean[] colMask = attrFilterPanel.importances.getColMask(containerOperations.container, 
                    measureIndex, 
                    attrFilterPanel.getAttrNumber(), attrFilterPanel.inverseFiltering());       
            if(colMask.length == containerOperations.container.colsNumber()){
        		boolean rowMask[] = new boolean [containerOperations.container.rowsNumber()];        
        		Arrays.fill(rowMask, true);
        		containerOperations.container = containerOperations.container.clone(colMask, rowMask);
                updateGUI();
            }else
                System.out.println("Number of attributes in histogram file does not match to current data set.");
        }
    }
    //******************************************
    private void updateMemory()
    {
    	String attr[][]=attributesPanel.getAttributes();    	
    	int attributesNumber=attr[0].length;    	
    	TableModel model=eventsPanel.getModel();    	
    	int columnsNumber=model.getColumnCount();
    	int rowsNumber=model.getRowCount();
    	
    	Array container=new SArray();
    	container.init(attributesNumber, rowsNumber);
    	//setting attributes
    	for(int i=0;i<attributesNumber;i++)
    	{
    		container.attributes[i].name=attr[0][i];
    		container.attributes[i].type=Attribute.type2Int(attr[1][i]);
    	}
    	//setting mapping
    	HashMap<String,Integer> map=new HashMap<String,Integer>();
    	for(int i=0;i<columnsNumber;i++)
    		map.put(attributesPanel.getNewName(model.getColumnName(i)), new Integer(i));
    	    	
    	progressBar.setMinimum(0);
    	progressBar.setValue(0);
    	progressBar.setMaximum(attributesNumber*rowsNumber);
    	int v=0;
    	//setting events
    	for(int i=0;i<attributesNumber;i++)
    	{
    		for(int j=0;j<rowsNumber;j++)
    		{
    			int column=map.get(container.attributes[i].name);
    			container.writeValueStr(i, j, model.getValueAt(j, column).toString());
    			progressBar.setValue(v++);
    		}
    	}
    	//setting decision        
        String decName = attributesPanel.getNewName(attributesPanel.getDecisionAttributeName());        
    	container.setDecAttrIdx(container.getColIndex(decName));
    	container.setAllDecValues();
    	containerOperations.setContainer(container);
    	containerOperations.findDomains();    	
    	System.out.println("Container has been successfully reloaded.");  
    }
    //******************************************
    public void updateGUI()
    {        
        eventsPanel.fillEventsTable();
        String [][] attributes=containerOperations.getAttributesList();
        attributesPanel.setAttributes(attributes);
        attributesPanel.setDomains(containerOperations.getDomains());
        int decIndex=containerOperations.container.getDecAttrIdx();
        if(decIndex!=-1)
        	attributesPanel.setDecisionAttribute(containerOperations.container.attributes[decIndex].name);
        attributesPanel.refreshDomains();
        replacePanel.setAttributes(attributes[0]);
    }
    //******************************************
    private void addNewAttribute()
    {
        if(containerOperations.isFileOpen()){ 
            ExtFunctions.addAttribute(containerOperations.container);
            updateGUI();
        }
    }
    //******************************************
    private void removeAttribute()
    {
        if(containerOperations.isFileOpen() 
                && containerOperations.container.colsNumber()>1)
        { 
            int index=attributesPanel.getAnchorAttributeIndex();
            containerOperations.container = SelectFunctions.removeColumn(containerOperations.container, index);
            containerOperations.findDomains();            
            updateGUI();
            attributesPanel.setAnchorAttributeIndex(index);
            attributesPanel.refreshDomains();
        }
    }
    //******************************************
}
