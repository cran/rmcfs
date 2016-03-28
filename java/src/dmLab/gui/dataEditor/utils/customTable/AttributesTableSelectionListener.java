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

public class AttributesTableSelectionListener implements ListSelectionListener
{
	JTable attributesTable;
	JTable eventsTable;
    // It is necessary to keep the table since it is not possible
    // to determine the table from the event's source
    public AttributesTableSelectionListener(JTable table)
    {
        this.attributesTable = table;
    }
//  *************************************
    public void setEventsTable(JTable eventsTable)
    {
        this.eventsTable=eventsTable;
    }
    //*************************************
    public void valueChanged(ListSelectionEvent e)
    {        
        int row=attributesTable.getSelectionModel().getAnchorSelectionIndex();
        
		//int column=attributesTable.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        //System.err.println(" c: "+column+" r: "+row);
        
        if(eventsTable!=null)
        {	
        	eventsTable.getColumnModel().getSelectionModel().setAnchorSelectionIndex(row);
        	eventsTable.repaint();
        }	
    }
    //*************************************
}
