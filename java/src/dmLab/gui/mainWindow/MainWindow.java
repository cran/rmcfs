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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import dmLab.DMLabInfo;
import dmLab.gui.dataEditor.EditorBody;
import dmLab.gui.graphViewer.GraphViewerBody;

public class MainWindow extends javax.swing.JFrame implements ActionListener
{
    private static final long serialVersionUID = -209380973376767652L;
    private static String GUITitle = "dmLab "+DMLabInfo.VERSION;

    private MainWindowToolBar toolBar;
    private MCFSPanel mcfsPanel;
    private JTabbedPane tabbedPanel;

//  ****************************************
    public MainWindow()
    {
        super();       
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initGUI();
    }
//  ****************************************
    public static void main(String[] args)
    {
        MainWindow body = new MainWindow();
        body.run();        
    }
//  ******************************************
    public void run()
    {
        System.out.print((new DMLabInfo()).toString());
        MainWindow mainPanel = new MainWindow();
        mainPanel.setVisible(true);
    }
//  ****************************************    
    private void initGUI() 
    {
        try
        {
            this.setTitle(MainWindow.GUITitle);     
            this.setResizable(false);
            this.setSize(580, 360); 
            this.setLocation(100, 100);
            tabbedPanel = new JTabbedPane();

            toolBar=new MainWindowToolBar();            
            toolBar.addActionListener(this);

            mcfsPanel=new MCFSPanel();
            
            getContentPane().add(toolBar, BorderLayout.NORTH);
            getContentPane().add(tabbedPanel, BorderLayout.CENTER);
            tabbedPanel.addTab("MCFS-ID",null,mcfsPanel,null);
            //tabbedPanel.addTab("Progress", null, optionsPanel, null);
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    //*****************************************
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        

        if (eventObject == toolBar.dataEditorButton)
        {
            EditorBody body = new EditorBody();
            body.run();
        } else if(eventObject == toolBar.graphViewerButton){
            System.out.print((new DMLabInfo()).toString());
            GraphViewerBody graphViever = new GraphViewerBody();
            graphViever.run();
        }
    }
//******************************************
}
