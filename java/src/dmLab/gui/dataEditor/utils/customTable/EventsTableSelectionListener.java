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
package dmLab.gui.dataEditor.utils.customTable;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dmLab.gui.dataEditor.panels.AttributesPanel;

public class EventsTableSelectionListener implements ListSelectionListener
{
    JTable eventsTable;
    AttributesPanel attributesPanel;
    // It is necessary to keep the table since it is not possible
    // to determine the table from the event's source
    public EventsTableSelectionListener(JTable table)
    {
        this.eventsTable = table;
    }
//  *************************************
    public void setAttributesPanel(AttributesPanel attributesPanel)
    {
        this.attributesPanel=attributesPanel;
    }
    //*************************************
	public void valueChanged(ListSelectionEvent e)
    {        
		//int row=eventsTable.getSelectionModel().getAnchorSelectionIndex();
        int column=eventsTable.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        if(attributesPanel!=null)
            attributesPanel.setAnchorAttributeIndex(column);
        eventsTable.repaint();
    }
    //*************************************
}
