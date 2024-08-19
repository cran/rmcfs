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
package dmLab.gui.dataEditor.utils.sortModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;


public class SortFilterModel extends AbstractTableModel
{
    private static final long serialVersionUID = 9095505376199939918L;
    public boolean desc=false;
    private JCheckBox descBox;
//*************************************    
    public SortFilterModel(TableModel m)
    {
        model = m;
        rows = new Row[model.getRowCount()];
        for (int i = 0; i < rows.length; i++)
        {
            rows[i] = new Row();
            rows[i].index = i;
        }
    }
//  *************************************
    public void setDescBox(JCheckBox descBox)
    {
    	this.descBox=descBox;
    }
//  *************************************
    public void sort(int c)
    {
        sortColumn = c;
        Arrays.sort(rows);
        if(desc)//if desc inverse the array
        {
            final int rowsCount=model.getRowCount();
            Row r[]= new Row[rowsCount];        
            for(int i=0;i<rowsCount;i++)
                r[rowsCount-i-1]=rows[i];
            rows=r;      
        }   
        fireTableDataChanged();
    }
//  *************************************
    public void addMouseListener(final JTable table)
    {
        table.getTableHeader().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            { 
                // double click
                //if (event.getClickCount() < 2) return;
                if(event.getButton()== MouseEvent.BUTTON3)
                {
                    if(descBox!=null && descBox.isSelected())
                    	desc=true;
                    else
                    	desc=false;
                }
                else
                    return;
                
                // find column of click and
                int tableColumn = table.columnAtPoint(event.getPoint());

                // translate to table model index and sort
                int modelColumn = table.convertColumnIndexToModel(tableColumn);
                sort(modelColumn);
            }
        });
    }
//  *************************************
    /*
     * compute the moved row for the three methods that access model elements
     */

    public Object getValueAt(int r, int c) {
        return model.getValueAt(rows[r].index, c);
    }
//  *************************************
    @Override
    public boolean isCellEditable(int r, int c) {
        return model.isCellEditable(rows[r].index, c);
    }
//  *************************************
    @Override
    public void setValueAt(Object aValue, int r, int c) {
        model.setValueAt(aValue, rows[r].index, c);
    }
//  *************************************
    /*
     * delegate all remaining methods to the model
     */

    public int getRowCount() {
        return model.getRowCount();
    }
//  *************************************
    public int getColumnCount() {
        return model.getColumnCount();
    }
//  *************************************
    @Override
    public String getColumnName(int c) {
        return model.getColumnName(c);
    }
//  *************************************
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Class getColumnClass(int c)
    {
        return model.getColumnClass(c);
    }

    /*
     * this inner class holds the index of the model row Rows are compared by
     * looking at the model row entries in the sort column
     */
    private class Row implements Comparable<Row> 
    {
        public int index;        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public int compareTo(Row otherRow)
        {            
            Object a = model.getValueAt(index, sortColumn);
            Object b = model.getValueAt(otherRow.index, sortColumn);
            if (a instanceof Comparable)
                return ((Comparable) a).compareTo(b);
            else
                return index - otherRow.index;
        }
    }
//  *************************************
    private TableModel model;
    private int sortColumn;
    private Row[] rows;       
}
