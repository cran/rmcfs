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
package dmLab.gui.dataEditor.windows;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dmLab.gui.dataEditor.panels.DBConnectionPanel;

public class DBConnectionWindow  extends javax.swing.JFrame implements ActionListener
{
    private static final long serialVersionUID = 2632048312518408153L;
    public DBConnectionPanel dbConnectionPanel;
    //***********************************
    public DBConnectionWindow()
    {
        super();        
        initGUI();
    }
    //***********************************
    public void actionPerformed(ActionEvent arg0)
    {

    }
//  *********************************
    public void addActionListener(ActionListener actionListener)
    {
        dbConnectionPanel.addActionListener(actionListener);
    }
    //***********************************
    private void initGUI() 
    {
        try {
            this.setSize(390, 300);
            this.setLocation(200, 200);
            this.setTitle("DB Connection");
            dbConnectionPanel=new DBConnectionPanel();
            getContentPane().add(dbConnectionPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //***********************************
}
