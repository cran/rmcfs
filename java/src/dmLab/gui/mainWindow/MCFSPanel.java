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
package dmLab.gui.mainWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import dmLab.gui.dataEditor.components.GlyphButton;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.mcfsEngine.MCFSAutoParams;
import dmLab.mcfs.mcfsEngine.MCFSExperiment;
import dmLab.utils.fileFilters.MyFileFilter;

public class MCFSPanel extends JPanel implements ActionListener
{
    private JButton browseButton;
    private JTextField filePath;
    private JFileChooser fileDialog;
    public  GlyphButton startButton; 

    private JLabel jLabel7;
    private JLabel jLabel6;
    private JLabel jLabel5;
    private JLabel jLabel4;
    private JLabel jLabel3;
    private JLabel jLabel2;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
        
    private JTextField splits;
    private JTextField projections;
    private JTextField projectionSize;
    private JTextField balance;
    private JTextField threads;
    private JTextField splitSetSize;
    private JTextField cutoffPermutations;
    
    private MCFSParams mcfsParams;
    
    private static final long serialVersionUID = -5499838815923813527L;

//  *********************************
    public MCFSPanel()
    {
        mcfsParams = new MCFSParams();
        setDefaultMCFSParameters(mcfsParams);
        initGUI();
    }
    //*********************************
    protected void initGUI()
    {        
        this.setLayout(null);
        
        browseButton = new JButton();
        browseButton.setBounds(10, 0, 180, 45);
        browseButton.setText("Browse");
        browseButton.setToolTipText("Select Input File");
        browseButton.setPreferredSize(new java.awt.Dimension(70, 42));
        this.add(browseButton);
        
        filePath = new JTextField();
        filePath.setBounds(200, 10, 355, 25);
        filePath.setText("./data/AlizadehData.adx");
        filePath.setToolTipText("Path to Input File");
        filePath.setPreferredSize(new java.awt.Dimension(238, 28));
        this.add(filePath);

        fileDialog=new JFileChooser();
        fileDialog.addChoosableFileFilter(new MyFileFilter("arff","weka"));
        fileDialog.addChoosableFileFilter(new MyFileFilter("adx","dmLab"));        
        fileDialog.setCurrentDirectory(new File(".//data//"));
        
        jLabel1 = new JLabel();
        jLabel1.setBounds(10, 50, 150, 25);        
        jLabel1.setText("projections (s):");
        jLabel1.setPreferredSize(new java.awt.Dimension(70, 28));
        jLabel1.setFont(new java.awt.Font("Dialog",1,12));
        this.add(jLabel1);
        
        projections = new JTextField();
        projections.setBounds(170, 50, 70, 25);
        projections.setText(MCFSAutoParams.valueToString(mcfsParams.projections));
        projections.setPreferredSize(new java.awt.Dimension(63, 21));
        projections.setToolTipText("in most cases a few thousands");
        this.add(projections);
                
        jLabel2 = new JLabel();
        jLabel2.setBounds(10, 80, 150, 25);        
        jLabel2.setText("projection size (m):");
        jLabel2.setFont(new java.awt.Font("Dialog",1,12));
        jLabel2.setPreferredSize(new java.awt.Dimension(91, 28));
        this.add(jLabel2);

        projectionSize = new JTextField();
        projectionSize.setBounds(170, 80, 70, 25);        
        projectionSize.setText(MCFSAutoParams.valueToString(mcfsParams.projectionSize));
        projectionSize.setPreferredSize(new java.awt.Dimension(63, 21));
        projectionSize.setToolTipText("<html>if <1 then fraction of original number of attributes is selected (e.g. 0.1 denotes 10% of input attributes)<br> if >1 then absolute number of randomly selected attributes</html>");
        this.add(projectionSize);
        
        jLabel3 = new JLabel();
        jLabel3.setBounds(10, 110, 150, 25);
        jLabel3.setText("splits (t):");
        jLabel3.setFont(new java.awt.Font("Dialog",1,12));
        jLabel3.setPreferredSize(new java.awt.Dimension(91, 28));
        this.add(jLabel3);
        
        splits = new JTextField();
        splits.setBounds(170, 110, 70, 25);
        splits.setText(Integer.toString(mcfsParams.splits));
        splits.setPreferredSize(new java.awt.Dimension(63, 21));
        splits.setToolTipText("in most cases it is small number (e.g. from 1-10)");
        this.add(splits);
        		
        jLabel4 = new JLabel();
        jLabel4.setBounds(10, 140, 150, 25);
        jLabel4.setText("balance:");
        jLabel4.setFont(new java.awt.Font("Dialog",1,12));
        jLabel4.setPreferredSize(new java.awt.Dimension(91, 28));
        this.add(jLabel4);

        balance = new JTextField();
        balance.setBounds(170, 140, 70, 25);
        balance.setText(Float.toString(mcfsParams.splitRatio));        
        balance.setText(MCFSAutoParams.valueToString(mcfsParams.balance));
        balance.setPreferredSize(new java.awt.Dimension(63, 21));
        balance.setToolTipText("for highly imbalanced data set it on 2 or higher");
        this.add(balance);
        
        jLabel6 = new JLabel();
        jLabel6.setBounds(10, 170, 150, 25);
        jLabel6.setText("cutoff permutations:");
        jLabel6.setFont(new java.awt.Font("Dialog",1,12));
        jLabel6.setPreferredSize(new java.awt.Dimension(77, 16));
        this.add(jLabel6);
        
        cutoffPermutations = new JTextField();
        cutoffPermutations.setBounds(170, 170, 70, 25);
        cutoffPermutations.setText(Integer.toString(mcfsParams.cutoffPermutations));
        cutoffPermutations.setPreferredSize(new java.awt.Dimension(63, 20));
        cutoffPermutations.setToolTipText("number of permutations experiments minimum suggested value is 20");
        this.add(cutoffPermutations);

        //balance panel                 
        jPanel1 = new JPanel();
        jPanel1.setBounds(250, 50, 300, 50);
        jPanel1.setLayout(null);
        jPanel1.setPreferredSize(new java.awt.Dimension(156, 55));
        jPanel1.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        this.add(jPanel1);

        jLabel5 = new JLabel();
        jLabel5.setBounds(10, 10, 150, 25);
        jLabel5.setText("Threads:");
        jLabel5.setFont(new java.awt.Font("Dialog",1,12));
        jLabel5.setPreferredSize(new java.awt.Dimension(80, 16));
        jPanel1.add(jLabel5);
        
        threads = new JTextField();
        threads.setBounds(170, 10, 70, 25);
        threads.setText(Integer.toString(mcfsParams.threadsNumber));
        threads.setPreferredSize(new java.awt.Dimension(37, 16));
        jPanel1.add(threads);

        jPanel2 = new JPanel();
        jPanel2.setBounds(250, 110, 300, 50);
        jPanel2.setLayout(null);
        jPanel2.setPreferredSize(new java.awt.Dimension(156, 50));
        jPanel2.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        this.add(jPanel2);
        		
        jLabel7 = new JLabel();
        jLabel7.setBounds(10, 10, 150, 25);
        jLabel7.setText("split set - size:");
        jLabel7.setFont(new java.awt.Font("Dialog",1,12));
        jLabel7.setPreferredSize(new java.awt.Dimension(76, 15));
        jPanel2.add(jLabel7);
        		
        splitSetSize = new JTextField();
        splitSetSize.setBounds(170, 10, 70, 25);
        splitSetSize.setText(Integer.toString(mcfsParams.splitSetSize));
        splitSetSize.setPreferredSize(new java.awt.Dimension(35, 21));
        jPanel2.add(splitSetSize);
        
        startButton = new GlyphButton("images/start.jpg", "Run MCFS");
        startButton.setBounds(400, 170, 150, 100);
        startButton.setToolTipText("Start MCFS Processing");
        startButton.setPreferredSize(new java.awt.Dimension(105, 53));
        this.add(startButton);
        		
        startButton.addActionListener(this);
        browseButton.addActionListener(this);
    }
    //*********************************
    public void addActionListener(ActionListener actionListener)
    {
        startButton.addActionListener(actionListener);        
    }
    //********************************************
    private void openFile()
    {
        if (fileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {            
            File file = fileDialog.getSelectedFile();
            filePath.setText(file.getAbsolutePath());            
            //System.out.println("DEBUG: Opening: " + file.getName());            
        }
    }
    //********************************************
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        

        if (eventObject == browseButton) 
            openFile();        
        else if(eventObject == startButton)
            startMCFS();
    }
    //********************************************
    public void startMCFS()
    {
        setMCFSParameters(mcfsParams);
    	//mcfsParams.seed = System.currentTimeMillis();
    	mcfsParams.seed = (new Random(System.currentTimeMillis())).nextInt();
        System.out.println(mcfsParams.toString());        
    	MCFSExperiment mcfs = new MCFSExperiment(mcfsParams);

        Thread t = new Thread(mcfs);
        t.start();        
    }
///***********************************************    
    private boolean setMCFSParameters(MCFSParams mcfsParams)    
    {
        File file=new File(filePath.getText());
        
        if(!file.exists()){
            System.err.println("File: '"+filePath.getText()+"' does not exist.");
            return false;
        }else{
            mcfsParams.inputFilesPATH = file.getPath().substring(0,file.getPath().indexOf(file.getName()));
            mcfsParams.inputFiles = new String[]{file.getName()};
            mcfsParams.inputFileName = file.getName();
        }

        try{
            mcfsParams.projectionSize = MCFSAutoParams.valueToFloat("projectionSize", projectionSize.getText());            
        }
        catch(NumberFormatException e){
            System.err.println("Error parsing projectionSize: "+projectionSize.getText());
            return false;
        }
        try{
            mcfsParams.projections = (int)MCFSAutoParams.valueToFloat("projections", projections.getText());            
        }
        catch(NumberFormatException e){
            System.err.println("Error parsing projections: "+projections.getText());
            return false;
        }
        try{
            mcfsParams.splits = (int)Float.parseFloat(splits.getText());            
        }
        catch(NumberFormatException e){
            System.err.println("Error parsing: "+splits.getText());
            return false;
        }

        try{
            mcfsParams.balance = MCFSAutoParams.valueToFloat("balance", balance.getText());            
        }
        catch(NumberFormatException e){
            System.err.println("Error parsing balance: "+balance.getText());
            return false;
        }
        
        try{
            mcfsParams.threadsNumber = Integer.parseInt(threads.getText());            
        }
        catch(NumberFormatException e){
            System.err.println("Error parsing threads: "+threads.getText());
            return false;
        }
        
        try{
            mcfsParams.splitSetSize=Integer.parseInt(splitSetSize.getText());
        }
        catch(NumberFormatException e){
            System.err.println("Error parsing splitSetSize: "+splitSetSize.getText());
            return false;
        }
        
        try{
            mcfsParams.cutoffPermutations=(int)Float.parseFloat(cutoffPermutations.getText());                
        }
        catch(NumberFormatException e){
            System.err.println("Error parsing: "+cutoffPermutations.getText());
            return false;
        }

        return true;
    }
    //********************************************
    private void setDefaultMCFSParameters(MCFSParams mcfsParams)
    {
    	mcfsParams.setDefault();
    }
    //********************************************
}
