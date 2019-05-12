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
package dmLab.gui.dataEditor.components;

import java.awt.event.ActionListener;

import javax.swing.JToolBar;

public class EditorToolBar extends JToolBar
{
    public GlyphButton dbButton;
    public GlyphButton openButton;
    public GlyphButton saveButton;
    public GlyphButton reloadButton;
    public GlyphButton fixTypesButton;
    
    private static final long serialVersionUID = -79015169667890634L;
    
    public EditorToolBar()
    {
        super();
        initToolBar();
    }
//  *********************************
    private void initToolBar()
    {
        dbButton = new GlyphButton("images/database.jpg","Open DB");
        dbButton.setToolTipText("Open Table from DataBase");
        this.add(dbButton);
        
        openButton = new GlyphButton("images/open.jpg","Open File");
        openButton.setToolTipText("Open File");
        this.add(openButton);
        
        saveButton = new GlyphButton("images/save.jpg","Save File");
        saveButton.setToolTipText("Update and Save to File");
        this.add(saveButton);

        this.add(new Separator());
        
        reloadButton = new GlyphButton("images/reload.jpg","Reload Data");
        reloadButton.setToolTipText("Update changes. Reload Data from GUI into Memory.");
        this.add(reloadButton);

        fixTypesButton = new GlyphButton("images/tools.jpg","Fix Types");
        fixTypesButton.setToolTipText("Fix Types and Values for all Attributes");
        this.add(fixTypesButton);                
    }
//  ********************************* 
    public void addActionListener(ActionListener actionListener)
    {
        dbButton.addActionListener(actionListener);
        reloadButton.addActionListener(actionListener); 
        fixTypesButton.addActionListener(actionListener);
        saveButton.addActionListener(actionListener);
        openButton.addActionListener(actionListener);
    }
//  *********************************
}
