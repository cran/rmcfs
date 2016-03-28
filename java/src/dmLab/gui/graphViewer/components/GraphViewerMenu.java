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
package dmLab.gui.graphViewer.components;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class GraphViewerMenu extends JMenuBar
{

    private static final long serialVersionUID = 1480457240026571951L;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JSeparator jSeparator;
    
    public JMenuItem helpMenuItem;
    public JMenuItem aboutMenuItem;
    public JMenuItem exitMenuItem;
    public JMenuItem closeFileMenuItem;
    public JMenuItem openFileMenuItem;
    public JMenuItem saveFileMenuItem;
    public JMenuItem newFileMenuItem;    
    
    public GraphViewerMenu()
    {
        super();
        initMenuBar();
    }
    //******************************************
    public void initMenuBar()
    {       
        //*** File Menu ***
        fileMenu = new JMenu();
        this.add(fileMenu);
        fileMenu.setText("File");
        fileMenu.setPreferredSize(new java.awt.Dimension(28, 84));
     
        openFileMenuItem = new JMenuItem();
        fileMenu.add(openFileMenuItem);
        openFileMenuItem.setText("Open");        

        saveFileMenuItem = new JMenuItem();
        fileMenu.add(saveFileMenuItem);
        saveFileMenuItem.setText("Save as...");        
        
        closeFileMenuItem = new JMenuItem();
        fileMenu.add(closeFileMenuItem);
        closeFileMenuItem.setText("Close");        
        
        jSeparator = new JSeparator();
        fileMenu.add(jSeparator);

        exitMenuItem = new JMenuItem();
        fileMenu.add(exitMenuItem);
        exitMenuItem.setText("Exit");
                           

        helpMenu = new JMenu();
        this.add(helpMenu);
        helpMenu.setText("Help");

        helpMenuItem = new JMenuItem();
        helpMenu.add(helpMenuItem);
        helpMenuItem.setText("Help");

        aboutMenuItem= new JMenuItem();
        helpMenu.add(aboutMenuItem);
        aboutMenuItem.setText("About...");
        
    }    
    //*****************************************
    public void addActionListener(ActionListener actionListener)
    {
        openFileMenuItem.addActionListener(actionListener);
        saveFileMenuItem.addActionListener(actionListener);
        closeFileMenuItem.addActionListener(actionListener);
        exitMenuItem.addActionListener(actionListener);
        aboutMenuItem.addActionListener(actionListener);
    }
    //*****************************************
}
