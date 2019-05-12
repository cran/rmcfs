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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dmLab.experiment.ExperimentParams;
import dmLab.gui.graphViewer.GraphViewerBody;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.utils.FileUtils;
import dmLab.utils.fileFilters.ComplexFileFilter;

public class GraphViewerToolBar extends JToolBar implements ChangeListener, ActionListener
{       
    protected GraphViewerBody graphViever;
    
    protected DoubleJSlider connectionWeightSlider;
    protected JTextField connectionWeightLabel;    
    protected DoubleJSlider topSetSizeSlider;    
    protected JTextField topSetSizeLabel;
    
    protected JFileChooser fileDialog;
    protected JFileChooser saveBitmapFileDialog;
    
    protected JCheckBox instantRedrawCheckBox;
    protected JButton saveBitmapButton;    
    protected JButton drawButton;
    protected JButton loadImportanceButton;
    
    protected AttributesRI attributesImportance;    
    private static final long serialVersionUID = -79015169667890634L;
    
//  *********************************    
    public GraphViewerToolBar(GraphViewerBody graphViever)
    {
        super();
        this.graphViever=graphViever;
        initToolBar();
        initFileDialog();
        initSaveBitampFileDialog();
        cleanGUI();
    }
//  *********************************
    public void cleanGUI()
    {
        loadImportanceButton.setToolTipText("Load importance file...");
        attributesImportance=null;
        connectionWeightSlider.init();
        setConnectionWeight(0,true);
        topSetSizeSlider.init();
        setTopRankingSize(0,true);
    }
//  *********************************
    public void initToolBar()
    {
        this.setLayout(null);
        this.setPreferredSize(new java.awt.Dimension(700, 100));

        connectionWeightSlider=new DoubleJSlider();
        this.add(connectionWeightSlider);        
        connectionWeightSlider.setBounds(10, 5, 450, 40);
        connectionWeightSlider.setLayout(null);
        connectionWeightSlider.addChangeListener(this);
        connectionWeightSlider.setToolTipText("minID (weight of connection)");
        
        connectionWeightLabel=new JTextField();
        this.add(connectionWeightLabel);
        connectionWeightLabel.setBounds(460, 10, 80, 30);
        connectionWeightLabel.setLayout(null);
        connectionWeightLabel.setText("");
        connectionWeightLabel.addActionListener(this);        

        topSetSizeSlider=new DoubleJSlider();   
        this.add(topSetSizeSlider);        
        topSetSizeSlider.setBounds(10, 50, 450, 40);
        topSetSizeSlider.addChangeListener(this);
        topSetSizeSlider.setToolTipText("number of top features");
        
        topSetSizeLabel=new JTextField();
        this.add(topSetSizeLabel);
        topSetSizeLabel.setBounds(460, 55, 80, 30);
        topSetSizeLabel.setText("");
        topSetSizeLabel.addActionListener(this); 
                
        loadImportanceButton=new JButton();
        this.add(loadImportanceButton);
        loadImportanceButton.setText("Load");
        loadImportanceButton.setBounds(545, 55, 90, 30);
        loadImportanceButton.setToolTipText("Load importance file...");       
        loadImportanceButton.addActionListener(this);
        
        drawButton = new JButton();
        this.add(drawButton);
        drawButton.setText("Draw");
        drawButton.setToolTipText("Draw graph");
        drawButton.setBounds(545, 10, 90, 30);
        drawButton.addActionListener(this);
        
        saveBitmapButton = new JButton();
        this.add(saveBitmapButton);
        saveBitmapButton.setText("Save bitmap");
        saveBitmapButton.setToolTipText("Save image of the graph.");
        saveBitmapButton.setBounds(650, 55, 90, 30);
        saveBitmapButton.addActionListener(this);

        instantRedrawCheckBox = new JCheckBox();
        this.add(instantRedrawCheckBox);
        instantRedrawCheckBox.setText("Instant Drawing");
        instantRedrawCheckBox.setBounds(650, 10, 120, 30);             
    }
    //************************************************
    private void initSaveBitampFileDialog()
    {        
        saveBitmapFileDialog = new JFileChooser(ExperimentParams.DEFAULT_RES_PATH);
        saveBitmapFileDialog.setLocation(100, 100);        
        ComplexFileFilter[] fileFilters = new ComplexFileFilter[] {                
                new ComplexFileFilter(new String[]{"jpg"},"jpg bitmap"),
                new ComplexFileFilter(new String[]{"png"},"png bitmap"),
        };
        for(int i=0;i<fileFilters.length;i++)
            saveBitmapFileDialog.addChoosableFileFilter(fileFilters[i]);        
    }
    //************************************************
    private void initFileDialog()
    {        
        fileDialog = new JFileChooser(ExperimentParams.DEFAULT_RES_PATH);
        fileDialog.setLocation(100, 100);        
        ComplexFileFilter[] fileFilters = new ComplexFileFilter[] {
                new ComplexFileFilter(new String[]{"csv"},"Comma Separated Histogram File"),
        };

        for(int i=0;i<fileFilters.length;i++)
            fileDialog.addChoosableFileFilter(fileFilters[i]);

        fileDialog.addChoosableFileFilter(new ComplexFileFilter(new String[]{"csv"},"All Supported Files"));        
    }
//  ************************************************    
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        
        
        if (eventObject == loadImportanceButton) 
        {
            loadImportanceFile();
        }
        else if(eventObject == drawButton)
        {
            graphViever.drawGraph();
        }
        else if(eventObject == saveBitmapButton)
        {
           saveBitmap(); 
        }
        else if(eventObject == topSetSizeLabel && isImportanceLoaded())
        {
            topSetSizeSlider.setScaledValue(getTopSetSize());
        }
        else if(eventObject == connectionWeightLabel)
        {
            connectionWeightSlider.setScaledValue(getConnectionWeight());
        }   
    }
//  *********************************
    public void stateChanged(ChangeEvent event)
    {               
        final Object eventObject=event.getSource();        

        if(eventObject == connectionWeightSlider)
        {   
            setConnectionWeight(connectionWeightSlider.getScaledValue(),false);
            if(instantRedrawCheckBox.isSelected())                
                graphViever.drawGraph();
        }
        else if(eventObject == topSetSizeSlider && isImportanceLoaded())
        {  
        	setTopRankingSize((int)topSetSizeSlider.getScaledValue(),false);
            if(instantRedrawCheckBox.isSelected())
                graphViever.drawGraph();
        }        
    }    
//  ********************************* 
    public void addActionListener(ActionListener actionListener)
    {
        
    }
//  *********************************
    public void setConnectionWeight(float weight, boolean toSlider)
    {
    	DecimalFormat df=new DecimalFormat("0.0");
    	String weightFormatted = df.format(weight);    	
        connectionWeightLabel.setText(weightFormatted);
        if(toSlider)
        	connectionWeightSlider.setScaledValue(weight);
    }    
//  *********************************
    public float getConnectionWeight()
    {
        String text=connectionWeightLabel.getText();
        float weight=0;

        try{
            weight=Float.parseFloat(text);
        }catch(NumberFormatException e){
            weight=connectionWeightSlider.getScaledMinimum(); 
        }        
        if(weight < connectionWeightSlider.getScaledMinimum() 
                || weight > connectionWeightSlider.getScaledMaximum())
            weight=connectionWeightSlider.getScaledMinimum();
        
        return weight;
    }
//  *********************************    
    public void setTopRankingSize(int size, boolean toSlider)
    {    	
        topSetSizeLabel.setText(Integer.toString(size));
        if(toSlider)
        	topSetSizeSlider.setScaledValue(size);
    }
//  *********************************    
    public int getTopSetSize()
    {
        String text=topSetSizeLabel.getText();
        int size=0;

        try{
            size=Integer.parseInt(text);
        }catch(NumberFormatException e){
            size=(int)topSetSizeSlider.getScaledMaximum(); 
        }
        if(size<0 || size>(int)topSetSizeSlider.getScaledMaximum())
            size=(int)topSetSizeSlider.getScaledMaximum();
        
        return size;
    }     
//  *********************************
    public void loadImportanceFile()
    {
        if (graphViever.connections.getNodesNumber()==0)
            return;
        
        int returnVal = fileDialog.showOpenDialog(this);
        
        if (returnVal == JFileChooser.CANCEL_OPTION)
            return;            
        else if (returnVal == JFileChooser.APPROVE_OPTION)
            loadImportanceFile(fileDialog.getSelectedFile());                  
    }    
//  *********************************
    public boolean loadImportanceFile(File file)
    {                         
        attributesImportance=new AttributesRI();
        if(!attributesImportance.load(file.getAbsolutePath())){
            attributesImportance=null;
            return false;
        }else{            
            loadImportanceButton.setToolTipText(""+file.getName());
            return true;
        }
    }
//  ********************************* 
    public boolean isImportanceLoaded()
    {
        if(attributesImportance==null)
            return false;
        else
            return true;
    }
//  *********************************
    public void initWeightSlider(AttributesID connections)
    {
        if(connections==null || connections.getNodesNumber()==0){
            connectionWeightSlider.init();
        }else{            
        	connectionWeightSlider.init(0, connections.getMaxID(), 0, 0.1f);
        }
    }
//  *********************************
    public void initTopSetSlider(AttributesID connections)
    {
        if(connections==null || connections.getNodesNumber()==0){
        	topSetSizeSlider.init();
        }else{
        	if(isImportanceLoaded()){
        		int maxSliderValue = attributesImportance.getAttributesNumber();
        		if(maxSliderValue>GraphViewerBody.TOP_SET_MAX_SLIDER_VALUE)
        			maxSliderValue=GraphViewerBody.TOP_SET_MAX_SLIDER_VALUE;
    			topSetSizeSlider.init(0, maxSliderValue, 1, 1f);
        	}
        	else
        		topSetSizeSlider.init();
        }
    }
//  *********************************
    public AttributesRI getAttributesImportance()
    {
    	return attributesImportance;
    }
//  *********************************
    public void saveBitmap()
    {       
        saveBitmapFileDialog.setSelectedFile(new File("_n"+topSetSizeLabel.getText()+"_w"+connectionWeightLabel.getText()+".png"));

        int returnVal = saveBitmapFileDialog.showSaveDialog(this);
        boolean isSaved=false;

        if (returnVal == JFileChooser.CANCEL_OPTION)
            return;

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = saveBitmapFileDialog.getSelectedFile();            
            BufferedImage image=graphViever.graphVisualization.getImage();
                        
            if(((ComplexFileFilter)saveBitmapFileDialog.getFileFilter()).accept("png"))
                isSaved=saveBitmap("png",image,file);
            else if(((ComplexFileFilter)saveBitmapFileDialog.getFileFilter()).accept("jpg"))
                isSaved=saveBitmap("jpg",image,file);

            if(isSaved)
                System.out.println("File is saved.");
        }
    }
//  *********************************
    private boolean saveBitmap(String format,BufferedImage image,File file)
    {
        String filePath=file.getAbsolutePath();
        if(!FileUtils.getFileExtension(file.getName()).equalsIgnoreCase(format))
            filePath+="."+format;                    

        System.out.println("Saving: " + filePath);
        
        try {
            ImageIO.write(image, format , new File(filePath));
        } catch (IOException e) 
        {
            System.err.println("Error saving the file. File: "+filePath);
            return false;
        }
        return true;
    }
//  *********************************   
}
