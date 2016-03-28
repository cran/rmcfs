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

import javax.swing.JPanel;
import javax.swing.JTextField;

import dmLab.gui.dataEditor.components.GlyphButton;

public class EventsFilterPanel extends JPanel 
{	
	private JTextField filterEdit;
	public GlyphButton filterButton;
	
	private static final long serialVersionUID = 4419405928186136265L;    
    //*********************************
    public EventsFilterPanel()
    {
        initFilterPanel();
    }
    //*********************************
    protected void initFilterPanel()
    {
        this.setLayout(null);
             
        filterEdit = new JTextField();
        this.add(filterEdit);
        filterEdit.setBounds(10, 10, 200, 20);
        filterEdit.setToolTipText("Specify filter condition (e.g attributeName [operator] value) [operator '=','>','<','>=','<=','!=']");
        
        filterButton=new GlyphButton("images/filter.jpg","Filter Events");
        filterButton.setToolTipText("Filter events. Removes events that meet the criterion.");
        this.add(filterButton);
        filterButton.setBounds(220, 10, 90, 40);
        //DEBUG
        filterEdit.setText("windy=true");        
    }
    //*********************************
    public String getFilter()
    {
        return filterEdit.getText();
    }
    //*********************************
    public void addActionListener(ActionListener actionListener)
    {
    	filterButton.addActionListener(actionListener);
    }
//  *********************************
}
