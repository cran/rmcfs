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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import dmLab.gui.dataEditor.dbConnector.DBConnector;
import dmLab.gui.dataEditor.dbConnector.DBProps;

public class DBConnectionPanel extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 4419405928186136265L;    
//  *************************************
    private JComboBox dbTypeComboBox;

    private JTextField userTextField;
    private JTextField dbNameTextField;
    private JPasswordField passwordField;
    private JTextField hostTextField;

    private JLabel userLabel;
    private JLabel dbNameLabel;
    private JLabel hostLabel;
    private JLabel passwordLabel;

    private JButton saveConfigButton;
    private JButton checkConnButton;
    public JButton runQueryButton;
    private JScrollPane queryScrollPane;
    private JTextArea queryTextArea;
    
    private DBConnector dbConnector;
    private ResultSet resultSet;

    //ORACLE=0; POSTGRES=1; MYSQL=2; MSSQL=3;
    private int currentDBType; 
    private String[] databases;
    private DBProps[] dbProps;
    
    public String cfgPath="cfg/";
    
    //*********************************
    public DBConnectionPanel()
    {
        dbConnector = new DBConnector();
        
        databases = new String[] {"ORACLE", "POSTGRES","MySQL","MS SQL"};        
        dbProps=new DBProps[databases.length];        

        for(int i=0;i<dbProps.length;i++)
        {
            dbProps[i]=new DBProps();
            dbProps[i].load(cfgPath+DBConnector.DB_CFG_FILE_NAME[i]);
            dbProps[i].setDBType(i);       
        }        
        initDBPanel();
        currentDBType=dbTypeComboBox.getSelectedIndex();
        setGUIProps(dbProps[currentDBType]);        
    }
    //*********************************
    protected void initDBPanel()
    {
        this.setLayout(null);
        //Labels
        userLabel = new JLabel();
        this.add(userLabel);
        userLabel.setText("User");
        userLabel.setBounds(28, 84, 63, 21);
        
        hostLabel = new JLabel();
        this.add(hostLabel);
        hostLabel.setText("Host");
        hostLabel.setBounds(29, 39, 63, 21);

        dbNameLabel = new JLabel();
        this.add(dbNameLabel);
        dbNameLabel.setText("DBName");
        dbNameLabel.setBounds(196, 42, 63, 14);       

        passwordLabel = new JLabel();
        this.add(passwordLabel);
        passwordLabel.setText("Password");
        passwordLabel.setBounds(196, 84, 84, 21);
        
        //ComboBox        
        this.setPreferredSize(new java.awt.Dimension(385, 266));
        ComboBoxModel dbTypeComboBoxModel = new DefaultComboBoxModel(databases);
        dbTypeComboBox = new JComboBox();
        this.add(dbTypeComboBox);
        dbTypeComboBox.setModel(dbTypeComboBoxModel);
        dbTypeComboBox.setBounds(28, 7, 161, 28);
        dbTypeComboBox.addActionListener(this);
        
        //ComboBox
        hostTextField = new JTextField();
        this.add(hostTextField);
        hostTextField.setText("hostTextField");
        hostTextField.setBounds(28, 56, 147, 28);

        dbNameTextField = new JTextField();
        this.add(dbNameTextField);
        dbNameTextField.setText("dbName");
        dbNameTextField.setBounds(196, 56, 154, 28);

        userTextField = new JTextField();
        this.add(userTextField);
        userTextField.setText("user");
        userTextField.setBounds(28, 105, 147, 28);

        passwordField = new JPasswordField();
        this.add(passwordField);
        passwordField.setText("password");
        passwordField.setBounds(196, 105, 154, 28);

        //SQL QUERY AREA        
        queryTextArea = new JTextArea();        
        queryTextArea.setText("");
        queryTextArea.setToolTipText("User's SQL Query");
        queryTextArea.setBackground(new java.awt.Color(255,255,255));
        
        queryScrollPane=new JScrollPane();
        queryScrollPane.setBounds(28, 140, 322, 77);
        this.add(queryScrollPane);
        queryScrollPane.setViewportView(queryTextArea);

        
        //buttons
        checkConnButton = new JButton();
        this.add(checkConnButton);
        checkConnButton.setText("Check Connection");
        checkConnButton.setBounds(0, 224, 147, 35);
        checkConnButton.addActionListener(this);
        
        saveConfigButton = new JButton();
        this.add(saveConfigButton);
        saveConfigButton.setText("Save Config");
        saveConfigButton.setBounds(161, 224, 105, 35);
        saveConfigButton.addActionListener(this);
        
        runQueryButton = new JButton();
        this.add(runQueryButton);
        runQueryButton.setText("Run Query");
        runQueryButton.setBounds(273, 224, 105, 35);        
    }
//  *********************************
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        
        if (eventObject == dbTypeComboBox)
        {            
            setMemProps(dbProps[currentDBType]);
            setGUIProps(dbProps[dbTypeComboBox.getSelectedIndex()]);
            currentDBType=dbTypeComboBox.getSelectedIndex();
        }            
        else if (eventObject == checkConnButton)
        {
            if(connect2DB())
                System.out.println("Connection Successful!!");
        }
        else if (eventObject == saveConfigButton)
        {
            int selectedDB=dbTypeComboBox.getSelectedIndex();
            DBProps dbPropsTmp=dbProps[selectedDB];
            dbPropsTmp.setDBType(selectedDB);                
            setMemProps(dbPropsTmp);
            dbProps[selectedDB].save(cfgPath+DBConnector.DB_CFG_FILE_NAME[selectedDB]);                        
        }        
    }
//  *********************************
    public void addActionListener(ActionListener actionListener)
    {
        runQueryButton.addActionListener(actionListener);
    }
//  *********************************
    public ResultSet getResultSet()
    {
        if(queryTextArea.getText()==null)
            return null;
        else{             
            connect2DB();
            resultSet= dbConnector.executeQuery(queryTextArea.getText());
        }
        return resultSet;
    }
//  *********************************
    public boolean connect2DB()
    {
        //set dbProps here
        int selectedDB=dbTypeComboBox.getSelectedIndex();
        DBProps dbPropsTmp=dbProps[selectedDB];
        dbPropsTmp.setDBType(selectedDB);                
        setMemProps(dbPropsTmp);
        //connect
        return dbConnector.connect(dbPropsTmp);  
    }
//  *********************************
    private void setMemProps(DBProps dbProps)
    {
        dbProps.setDbName(dbNameTextField.getText().trim());
        dbProps.setHost(hostTextField.getText().trim());
        dbProps.setUser(userTextField.getText().trim());
        dbProps.setPassword(new String(passwordField.getPassword()));        
    }
//  *********************************
    private void setGUIProps(DBProps dbProps)
    {
        dbNameTextField.setText(dbProps.getDbName());
        hostTextField.setText(dbProps.getHost());
        userTextField.setText(dbProps.getUser());
        passwordField.setText(dbProps.getPassword());
    }
//  *********************************    
}
