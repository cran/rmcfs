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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import dmLab.gui.dataEditor.utils.AttributeType;

public class CustomTableCellRenderer extends DefaultTableCellRenderer 
{
    private static final long serialVersionUID = -8304505347422268893L;

    private HashMap<String,Color> markedValues;
    private String toolTipText="";
    private Color defaultBackgroundColor=Color.white;	
    public boolean boldText=false;
    public boolean markAnchorRow=false;
    public boolean markAnchorColumn=false;
    //************************************************	
    public void addMarkedValue(String markedValue, Color color)
    {
        if( markedValues==null)
            markedValues=new HashMap<String,Color>();
        markedValues.put(markedValue,color);
    }
    //************************************************
    public void clearMarkedValues()
    {
        markedValues=null;
    }
    //************************************************	
    public void setMyToolTipText(String text)
    {
        toolTipText=text;	
    }
    //************************************************
    public void setDefaultBackgroundColor(Color backgroundColor)
    {
        this.defaultBackgroundColor=backgroundColor;
    }
    //************************************************
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) 
    {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        boolean markBackground=false;
        Color color=defaultBackgroundColor;
                        
        if(markedValues!=null)
        {           
            if( value instanceof String || value instanceof AttributeType)
                if(markedValues.containsKey(value.toString()))
                {
                    color=markedValues.get(value.toString());
                    markBackground=true;
                }
        }
        
        if( markBackground )
        {
            cell.setBackground(color);
            if(boldText)
                cell.setFont(cell.getFont().deriveFont(Font.BOLD));
            setToolTipText(toolTipText);
        }
        else
        {
            cell.setBackground( defaultBackgroundColor );
            cell.setFont(cell.getFont().deriveFont(Font.PLAIN));
            setToolTipText("");
        }
        
        if(markAnchorRow && table.getSelectionModel().getAnchorSelectionIndex()==row)        
            cell.setFont(cell.getFont().deriveFont(Font.BOLD));
        else if(markAnchorColumn && table.getColumnModel().getSelectionModel().getAnchorSelectionIndex()==column)        	        	
        	cell.setFont(cell.getFont().deriveFont(Font.BOLD));        
        else
        	cell.setFont(cell.getFont().deriveFont(Font.PLAIN));        	
        
        return cell;
    }
    //***************************************

}
