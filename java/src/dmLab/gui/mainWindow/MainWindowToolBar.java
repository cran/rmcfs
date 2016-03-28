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
package dmLab.gui.mainWindow;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class MainWindowToolBar extends JToolBar
{
    /**
     * 
     */
    private static final long serialVersionUID = 1960036165370834233L;
    public JButton dataEditorButton;
    public JButton graphViewerButton;
    
//  *********************************    
    public MainWindowToolBar()
    {
        super();
        initToolBar();
    }
//  *********************************
    private void initToolBar()
    {
        setFloatable(false);
    	dataEditorButton = new JButton("Data Editor");
        dataEditorButton.setToolTipText("Run Data Editor");
        this.add(dataEditorButton);        
        this.addSeparator();
        graphViewerButton = new JButton("Graph Viewer");
        graphViewerButton.setToolTipText("Run Graph Viewer");        
        this.add(graphViewerButton);
        
    }
//  ********************************* 
    public void addActionListener(ActionListener actionListener)
    {
        dataEditorButton.addActionListener(actionListener);
        graphViewerButton.addActionListener(actionListener);
    }
//  *********************************
}
