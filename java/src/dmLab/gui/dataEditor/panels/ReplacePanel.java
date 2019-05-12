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

import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import dmLab.gui.dataEditor.components.GlyphButton;

public class ReplacePanel extends JPanel
{
    private JList replaceAttributesList;
    private JScrollPane scrollListPanel;
    private JLabel replaceLabel;
    private JTextField destinationValueEdit;
    private JTextField sourceValueEdit;
    private JCheckBox wholeWordCheckBox;

    public GlyphButton replaceButton;

    private static final long serialVersionUID = 7910030654908157575L;
//  *********************************
    public ReplacePanel()
    {
        initReplacePanel();
    }
//  *********************************
    private void initReplacePanel()
    {
        this.setLayout(null);
        
        sourceValueEdit = new JTextField();
        this.add(sourceValueEdit);
        sourceValueEdit.setBounds(10, 10, 120, 20);
        sourceValueEdit.setText("");

        replaceLabel = new JLabel();
        this.add(replaceLabel);
        replaceLabel.setText("replace with");
        replaceLabel.setBounds(140, 10, 70, 20);

        destinationValueEdit = new JTextField();
        this.add(destinationValueEdit);
        destinationValueEdit.setBounds(220, 10, 120, 20);
        destinationValueEdit.setText("");

        replaceAttributesList = new JList();                                
        scrollListPanel=new JScrollPane();
        scrollListPanel.setViewportView(replaceAttributesList);
        scrollListPanel.setBounds(10, 40, 250, 120);
        this.add(scrollListPanel);
                
        replaceButton=new GlyphButton("images/replace.jpg","Replace");
        this.add(replaceButton);
        replaceButton.setToolTipText("Replace Specified Value by Destination Value.");
        replaceButton.setBounds(270, 40, 90, 40);
        
        wholeWordCheckBox=new JCheckBox("Whole Word");
        wholeWordCheckBox.setSelected(true);
        wholeWordCheckBox.setBounds(266, 90, 100, 20);
        this.add(wholeWordCheckBox);
    }
//  *********************************
    public void setAttributes(String [] attributesList)
    {
        if(attributesList==null)
        {   
            ((DefaultListModel)replaceAttributesList.getModel()).clear();
            return;
        }
        
        DefaultListModel model = new DefaultListModel();
        int indexes[]=new int[attributesList.length];
        for(int i=0;i<attributesList.length;i++)
        {
            model.addElement(attributesList[i]);
            indexes[i]=i;
        }       
        replaceAttributesList.setModel(model);        
        replaceAttributesList.setSelectedIndices(indexes);
    }
//  *********************************
    public String getSourceValue()
    {
        return sourceValueEdit.getText();
    }
//  *********************************
    public boolean getWholeWord()
    {
        return wholeWordCheckBox.isSelected();
    }
//  *********************************
    public String getDestinationValue()
    {
        return destinationValueEdit.getText();
    }
//  *********************************
    public boolean[] getSelectionMask()
    {
        boolean mask[]=new boolean [replaceAttributesList.getModel().getSize()];        
        int[] indexes= replaceAttributesList.getSelectedIndices();
        for(int i=0;i<indexes.length;i++)
            mask[indexes[i]]=true;
        return mask;
    }
//  *********************************
    public void addActionListener(ActionListener actionListener)
    {
        replaceButton.addActionListener(actionListener);   
    }
//  *********************************
}
