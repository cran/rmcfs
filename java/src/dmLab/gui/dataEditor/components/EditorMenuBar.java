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
package dmLab.gui.dataEditor.components;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class EditorMenuBar extends JMenuBar
{
    private static final long serialVersionUID = 3556447958584542301L;

    private JMenu fileMenu;
    private JMenu helpMenu;
    private JSeparator jSeparator2;
    
    public JMenuItem pasteMenuItem;
    public JMenuItem copyMenuItem;
    public JMenuItem cutMenuItem;
    public JMenuItem helpMenuItem;
    public JMenuItem aboutMenuItem;
    public JMenuItem exitMenuItem;
    public JMenuItem closeFileMenuItem;
    public JMenuItem saveAsMenuItem;
    //public JMenuItem saveMenuItem;
    public JMenuItem openFileMenuItem;
    public JMenuItem openDBMenuItem;
    public JMenuItem newFileMenuItem;    
    
    public EditorMenuBar()
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

        newFileMenuItem = new JMenuItem();
        fileMenu.add(newFileMenuItem);
        newFileMenuItem.setText("New");

        openDBMenuItem = new JMenuItem();
        fileMenu.add(openDBMenuItem);
        openDBMenuItem.setText("Open DB");
        
        openFileMenuItem = new JMenuItem();
        fileMenu.add(openFileMenuItem);
        openFileMenuItem.setText("Open File");        
        
        //saveMenuItem = new JMenuItem();
        //fileMenu.add(saveMenuItem);
        //saveMenuItem.setText("Save");

        saveAsMenuItem = new JMenuItem();
        fileMenu.add(saveAsMenuItem);
        saveAsMenuItem.setText("Save As ...");
        
        closeFileMenuItem = new JMenuItem();
        fileMenu.add(closeFileMenuItem);
        closeFileMenuItem.setText("Close");        
        
        jSeparator2 = new JSeparator();
        fileMenu.add(jSeparator2);

        exitMenuItem = new JMenuItem();
        fileMenu.add(exitMenuItem);
        exitMenuItem.setText("Exit");
                           
        //*** Edit Menu ***
        /*
        editMenu = new JMenu();
        this.add(editMenu);
        editMenu.setText("Edit");

        cutMenuItem = new JMenuItem();
        editMenu.add(cutMenuItem);
        cutMenuItem.setText("Cut");

        copyMenuItem = new JMenuItem();
        editMenu.add(copyMenuItem);
        copyMenuItem.setText("Copy");

        pasteMenuItem = new JMenuItem();
        editMenu.add(pasteMenuItem);
        pasteMenuItem.setText("Paste");

        jSeparator1 = new JSeparator();
        editMenu.add(jSeparator1);

        deleteMenuItem = new JMenuItem();
        editMenu.add(deleteMenuItem);
        deleteMenuItem.setText("Delete");
         */
        //*** Help Menu ***

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
        openDBMenuItem.addActionListener(actionListener);
        openFileMenuItem.addActionListener(actionListener);        
        saveAsMenuItem.addActionListener(actionListener);
        closeFileMenuItem.addActionListener(actionListener);
        exitMenuItem.addActionListener(actionListener);
        aboutMenuItem.addActionListener(actionListener);
    }
    //*****************************************
}
