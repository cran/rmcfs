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
package dmLab.gui.dataEditor.panels;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsPanel extends JPanel 
{
    private JLabel separatorLabel;
    private JLabel defaultAttributeNameLabel;
    
    private JCheckBox attributesInFirstLine; 
    public JTextField separatorEdit;
    public JCheckBox separatorTab;
    public JCheckBox consequentSeparatorsTreatAsOne;
    private JTextField defaultAtributeNameEdit;
    private JCheckBox trimComments;
    public JCheckBox descEventsOrder;
    
    private static final long serialVersionUID = -7552862525278650260L;
    //*********************************
    public OptionsPanel()
    {
        initOptionsPanel();
    }
    //*********************************
    protected void initOptionsPanel()
    {
        this.setLayout(null);
        int defaultHeight=20;
        int defaultMarigin=12;
        
        attributesInFirstLine=new JCheckBox("CSV - First Line Contains Attributes");
        attributesInFirstLine.setSelected(true);
        attributesInFirstLine.setBounds(defaultMarigin, defaultMarigin+0*defaultHeight, 250, defaultHeight);
        this.add(attributesInFirstLine);
        
        trimComments=new JCheckBox("CSV - Trim Comments");
        trimComments.setToolTipText("Check it if Loader has to Ignore Charakters After '#'");
        trimComments.setSelected(true);
        trimComments.setBounds(defaultMarigin,defaultMarigin+1*defaultHeight, 200, defaultHeight);        
        this.add(trimComments);
        
        separatorEdit = new JTextField();
        this.add(separatorEdit);
        separatorEdit.setText(",");
        separatorEdit.setBounds(defaultMarigin,defaultMarigin+2*defaultHeight, 20, defaultHeight);        
        separatorLabel = new JLabel();
        separatorLabel.setBounds(separatorEdit.getX()+separatorEdit.getWidth()+10, separatorEdit.getY(), 200, defaultHeight);
        this.add(separatorLabel);
        separatorLabel.setText("CSV - Separator Between Values");
        
        separatorTab=new JCheckBox("CSV - separator is tabulation");
        separatorTab.setToolTipText("Check it if separator is TAB");
        separatorTab.setSelected(false);
        separatorTab.setBounds(defaultMarigin,defaultMarigin+3*defaultHeight, 200, defaultHeight);        
        this.add(separatorTab);

        consequentSeparatorsTreatAsOne=new JCheckBox("CSV - consequent Separators treat as one");
        consequentSeparatorsTreatAsOne.setToolTipText("consequent Separators treat as one");
        consequentSeparatorsTreatAsOne.setSelected(false);
        consequentSeparatorsTreatAsOne.setBounds(defaultMarigin,defaultMarigin+4*defaultHeight, 270, defaultHeight);        
        this.add(consequentSeparatorsTreatAsOne);
        
        defaultAtributeNameEdit = new JTextField();
        this.add(defaultAtributeNameEdit);
        defaultAtributeNameEdit.setBounds(defaultMarigin,defaultMarigin+5*defaultHeight, 70, defaultHeight);
        defaultAtributeNameEdit.setText("attr");        
        defaultAttributeNameLabel = new JLabel();
        defaultAttributeNameLabel.setBounds(defaultAtributeNameEdit.getX()+defaultAtributeNameEdit.getWidth()+10, 
                    defaultAtributeNameEdit.getY(), 170, defaultHeight);
        this.add(defaultAttributeNameLabel);
        defaultAttributeNameLabel.setText("CSV - Default Attribute Name");
             
        descEventsOrder=new JCheckBox("Sort Events Descendently");
        descEventsOrder.setToolTipText("True if Events have to be Sorted Descendently");
        descEventsOrder.setSelected(false);
        descEventsOrder.setBounds(defaultMarigin,defaultMarigin+6*defaultHeight, 200, defaultHeight);        
        this.add(descEventsOrder);        
    }
    //*********************************
    public char getSeparatorCSV()
    {
        return separatorEdit.getText().charAt(0);
    }
    //*********************************
    public boolean getFirstLineContainsAttributesCSV()
    {
        return attributesInFirstLine.isSelected();
    }
    //*********************************
    public boolean getTrimCommentsCSV()
    {
        return trimComments.isSelected();
    }
    //*********************************
    public boolean getSeparatorTab()
    {
        return separatorTab.isSelected();
    }
    //*********************************
    public boolean getConsequentSeparatorsTreatAsOne()
    {
        return consequentSeparatorsTreatAsOne.isSelected();
    }    
    //*********************************
    public String getDefaultAtributeNameCSV()
    {
        return defaultAtributeNameEdit.getText();
    }
    //*********************************
    public void addActionListener(ActionListener actionListener)
    {        
    }
//  *********************************
}
