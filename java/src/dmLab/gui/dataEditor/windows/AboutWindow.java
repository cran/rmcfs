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

import javax.swing.JTextArea;

public class AboutWindow  extends javax.swing.JFrame implements ActionListener
{
    private static final long serialVersionUID = 2632048312518408153L;
    private JTextArea aboutTextArea;
    //***********************************
    public AboutWindow(String text)
    {
        super();        
        initGUI(text);
    }
    //***********************************
    public void actionPerformed(ActionEvent arg0)
    {        
    }
    //***********************************
    private void initGUI(String text) 
    {
        try {
            this.setSize(350, 250);
            this.setLocation(200, 200);
            this.setTitle("About");
            aboutTextArea = new JTextArea();
            getContentPane().add(aboutTextArea, BorderLayout.CENTER);
            aboutTextArea.setText(text);
            aboutTextArea.setEditable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //***********************************
}
