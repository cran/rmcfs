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
package dmLab.gui.dataEditor.panels;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dmLab.array.loader.fileLoader.FileType;
import dmLab.gui.dataEditor.components.GlyphButton;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.utils.fileFilters.MyFileFilter;

public class AttrFilterPanel extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 3396505699929045384L;
    private JButton browseButton;
    private JTextField filePath;
    public GlyphButton filterButton;
    private JComboBox measureName;
    private JTextField attrNumber;
    private JCheckBox inverseCheckBox;

    private JFileChooser fileDialog;
    public AttributesRI importances;
    
    //*********************************
    public AttrFilterPanel()
    {
        initFilterPanel();
    }
    //*********************************
    protected void initFilterPanel()
    {        
        this.setPreferredSize(new java.awt.Dimension(301, 119));
        this.setLayout(null);

        fileDialog=new JFileChooser();
        MyFileFilter fileFilters[]=FileType.getSupportedTypes();        
        for(int i=0;i<fileFilters.length;i++)
            fileDialog.addChoosableFileFilter(fileFilters[i]);
        
        inverseCheckBox = new JCheckBox();
        this.add(inverseCheckBox);                
        inverseCheckBox.setText("Inverse");
        inverseCheckBox.setBounds(91, 84, 70, 28);
        inverseCheckBox.setSelected(false);
        
        attrNumber = new JTextField();
        this.add(attrNumber);
        attrNumber.setText("30");
        attrNumber.setBounds(20, 84, 63, 28);

        browseButton = new JButton();
        this.add(browseButton);
        browseButton.setText("Browse");
        browseButton.setBounds(210, 7, 91, 28);
        
        filePath = new JTextField();
        this.add(filePath);
        filePath.setText("Histogram file path");
        filePath.setBounds(15, 10, 190, 30);

        filterButton=new GlyphButton("images/filter.jpg","Filter Attributes");
        filterButton.setToolTipText("Filter Attributes.");
        this.add(filterButton);            
        filterButton.setBounds(210, 50, 90, 40);
        browseButton.addActionListener(this);
        
        measureName = new JComboBox();
        this.add(measureName);
        measureName.setModel(new DefaultComboBoxModel(new String[] { "RI"}));
        measureName.setBounds(15, 50, 190, 30);
        init();
    }
    //*********************************
    public void addActionListener(ActionListener actionListener)
    {
        filterButton.addActionListener(actionListener);        
    }
    //********************************************
    private void openFile()
    {
        int returnVal = fileDialog.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fileDialog.getSelectedFile();            
            System.out.println("Opening: " + file.getName());            
            importances=new AttributesRI();

            if(importances.load(file.getAbsolutePath()))
            {
                System.out.println("Histogram file loaded.");
                filePath.setText(file.getAbsolutePath());
                ComboBoxModel measureNameModel = new DefaultComboBoxModel(importances.getMeasuresNames() );
                measureName.setModel(measureNameModel);
                String lastColumnname=importances.getMeasuresNames()[importances.getMeasuresNames().length-1];                
                measureNameModel.setSelectedItem(lastColumnname);
            }
            else
                System.err.println("Error loading histogram file.");
        }
    }
    //********************************************
    public boolean inverseFiltering()
    {
        return inverseCheckBox.isSelected();
    }
    //********************************************
    public String getMeasureName()
    {        
        return (String)measureName.getModel().getSelectedItem();
    }
    //********************************************
    public int getAttrNumber()
    {
        return Integer.parseInt(attrNumber.getText());
    }
    //********************************************
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        

        if (eventObject == browseButton) 
            openFile();
    }
    //********************************************
    public void init()
    {
        importances=null;
        attrNumber.setText("30");
        filePath.setText("Histogram file path");
        measureName.setModel(new DefaultComboBoxModel(new String[] { "RI_u1.0_v1.0"}));
        inverseCheckBox.setSelected(false);
    }
    //********************************************
}
