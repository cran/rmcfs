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

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import dmLab.array.loader.fileLoader.NullLabels;
import dmLab.gui.dataEditor.ContainerOperations;
import dmLab.gui.dataEditor.utils.customTable.CustomTableCellRenderer;
import dmLab.gui.dataEditor.utils.customTable.EventsTableSelectionListener;
import dmLab.gui.dataEditor.utils.sortModel.SortFilterModel;


public class EventsPanel extends JScrollPane {

    private static final long serialVersionUID = 1503647080073154316L;

    //private objects
    protected JTable eventsTable;
    
    
    //pointers
    private AttributesPanel attributesPanel;
    private OptionsPanel optionsPanel;
    private ContainerOperations containerOperations;

    //*********************************    
    public EventsPanel()
    {
        initPanel();
    }    
    //*********************************
    protected void initPanel()
    {
    }
    //*********************************    
    public void setPointers(ContainerOperations containerOperations,AttributesPanel attributesPanel,OptionsPanel optionsPanel)
    {
        this.containerOperations=containerOperations;
        this.optionsPanel=optionsPanel;
        this.attributesPanel=attributesPanel;
    }
//  *********************************
    public TableModel getModel()
    {
        return eventsTable.getModel();
    }
    //*********************************
    public void fillEventsTable()
    {                                   
        DefaultTableModel model=containerOperations.getTableModel();
        SortFilterModel sorter = new SortFilterModel(model);
        sorter.setDescBox(optionsPanel.descEventsOrder);
        eventsTable=new JTable(sorter);
        eventsTable.setColumnSelectionAllowed(true);
        eventsTable.setCellSelectionEnabled(true);
        
        EventsTableSelectionListener listener = new EventsTableSelectionListener(eventsTable);
        listener.setAttributesPanel(attributesPanel);        
        eventsTable.getSelectionModel().addListSelectionListener(listener);
        eventsTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        //attributesPanel.setPointers(this);
        
        ((SortFilterModel)eventsTable.getModel()).desc=true;
        sorter.addMouseListener(eventsTable);
        setViewportView(eventsTable);
        eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setWheelScrollingEnabled(true);
        eventsTable.getTableHeader().setReorderingAllowed(false);
        //http://javaalmanac.com/egs/javax.swing.table/AnchorEdit.html        
        
        TableCellRenderer renderer = new CustomTableCellRenderer();
        ((CustomTableCellRenderer)renderer).markAnchorRow=true;
        ((CustomTableCellRenderer)renderer).markAnchorColumn=true;        
        ((CustomTableCellRenderer)renderer).addMarkedValue(NullLabels.defaultLabel,new Color(255,230,230));
        ((CustomTableCellRenderer)renderer).setMyToolTipText("Missing value.");
        
        int columns = eventsTable.getColumnCount();
        for(int i=0;i<columns;i++)
            eventsTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        
        eventsTable.getColumnModel().getSelectionModel().setAnchorSelectionIndex(attributesPanel.getAnchorAttributeIndex());
    }
    //*********************************
    
}
