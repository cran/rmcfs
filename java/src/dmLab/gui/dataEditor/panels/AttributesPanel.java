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
package dmLab.gui.dataEditor.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import dmLab.array.domain.SDomain;
import dmLab.array.meta.Attribute;
import dmLab.gui.dataEditor.ContainerOperations;
import dmLab.gui.dataEditor.components.GlyphButton;
import dmLab.gui.dataEditor.utils.AttributeType;
import dmLab.gui.dataEditor.utils.customTable.AttributesTableSelectionListener;
import dmLab.gui.dataEditor.utils.customTable.CustomTableCellRenderer;
import dmLab.gui.dataEditor.utils.intText.IntTextField;
import dmLab.utils.ArrayUtils;

public class AttributesPanel extends JSplitPane 
    implements  ListSelectionListener, ActionListener, DocumentListener,CellEditorListener

{
    //visual components
    private JScrollPane scrollAttrPanel;
    private JScrollPane scrollDomainPanel;
    public JTable attributesTable;
    private JTable domainTable;
    private JComboBox typesComboBox;
    private JTabbedPane tabbedPanel;
    private JPanel attrTools;
    public GlyphButton decisionSetButton;
    private GlyphButton moveAtrributeButton;
    private IntTextField moveByTextField;
    public GlyphButton addNewAttributeButton;
    public GlyphButton removeAttributeButton;
    
    //data associated     
    private SDomain domains[];
    private String attrHeaders[];
    private String domainHeaders[];
    private String decisionAttribute;
    private int moveByValue=1;
    private String originalAttrNames[];
    private HashMap<String,String> namesMap;

    private int attributesTableColumnWidth[];
    private int domainTableColumnWidth[];
    //other
    private AttributesTableSelectionListener listener;
    
    //pointers
    private EventsPanel eventsPanel;
    private ContainerOperations containerOperations;
    
    private final String decisionToolTipText="Decision Attribute";
    private Color decisionAttrColor=new Color(255,128,128);
    
    private static final long serialVersionUID = 6579985375768717355L;
    //*********************************
    public AttributesPanel()
    {
        namesMap=new HashMap<String,String>();
        attrHeaders=new String[]{"#","name","type"};
        domainHeaders=new String[]{"#","domain","freq"};
        
        String tmp[]=Attribute.getSupportedTypes();
        AttributeType[] types=new AttributeType[tmp.length];
        for(int i=0;i<types.length;i++)
            types[i]=new AttributeType(tmp[i]);        
        typesComboBox=new JComboBox(types);

        initPanel();
    }
    //*********************************
    protected void initPanel()
    {                
        scrollAttrPanel=new JScrollPane();
        scrollAttrPanel.setPreferredSize(new java.awt.Dimension(200, 100));
        
        scrollDomainPanel=new JScrollPane();
        scrollDomainPanel.setPreferredSize(new java.awt.Dimension(100, 100));
    	
        tabbedPanel=new JTabbedPane();
        tabbedPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        tabbedPanel.addTab("Domains", null,scrollDomainPanel , null);        

        attrTools=new JPanel();
        attrTools.setLayout(null);
        tabbedPanel.addTab("Tools", null, attrTools , null);

        int defaultMarigin=10;
        decisionSetButton = new GlyphButton("images/decisionTable.jpg","Set Decision");
        decisionSetButton.setToolTipText("Sets Selected Attribute as Decision Attribute.");
        decisionSetButton.setBounds(defaultMarigin, defaultMarigin, 50, 40);
        attrTools.add(decisionSetButton);

        moveAtrributeButton = new GlyphButton("images/move.jpg","Move Attribute");
        moveAtrributeButton.setToolTipText("Move Attribute by x Positions.");        
        moveAtrributeButton.setBounds(2*defaultMarigin+decisionSetButton.getWidth(), defaultMarigin, 50, 40);        
        attrTools.add(moveAtrributeButton);
        moveAtrributeButton.addActionListener(this);        

        //*
        moveByTextField=new IntTextField();
        moveByTextField.setToolTipText("Positions to Move by. Set -1 to move attribute backward by 1 position.");
        attrTools.add(moveByTextField);
        moveByTextField.setBounds(defaultMarigin+moveAtrributeButton.getWidth()+moveAtrributeButton.getX(), moveAtrributeButton.getY(), 40, 20);
        moveByTextField.setText(Integer.toString(moveByValue));
        moveByTextField.getDocument().addDocumentListener(this);
        //*/
        
        addNewAttributeButton = new GlyphButton("images/addNew.jpg","Add Attribute");
        addNewAttributeButton.setToolTipText("Add new Attribute.");        
        addNewAttributeButton.setBounds(defaultMarigin, defaultMarigin+decisionSetButton.getY()+decisionSetButton.getHeight(), 50, 40);        
        attrTools.add(addNewAttributeButton);        
        
        removeAttributeButton = new GlyphButton("images/remove.jpg","Remove Attribute");
        removeAttributeButton.setToolTipText("Remove Attribute.");        
        removeAttributeButton.setBounds(defaultMarigin+addNewAttributeButton.getWidth()+addNewAttributeButton.getX(), defaultMarigin+decisionSetButton.getY()+decisionSetButton.getHeight(), 50, 40);        
        attrTools.add(removeAttributeButton);        
               
        this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.add(scrollAttrPanel, JSplitPane.LEFT);                        
        this.add(tabbedPanel, JSplitPane.RIGHT);              
    }
    //*********************************
    private void getAttributesTableColumnWidth(boolean init)
    {
        attributesTableColumnWidth=new int[attrHeaders.length];        
        for(int i=0;i<attributesTableColumnWidth.length;i++)
        {
            if(init)
            {
                if(i==0)
                    attributesTableColumnWidth[i]=30;
                else
                    attributesTableColumnWidth[i]=80;
            }
            else
                attributesTableColumnWidth[i]=attributesTable.getColumnModel().getColumn(i).getPreferredWidth();            
        }        
    }
    //*********************************
    private void setAttributesTableColumnWidth()
    {
        if(attributesTable!=null)
            for(int i=0;i<attributesTableColumnWidth.length;i++)
                attributesTable.getColumnModel().getColumn(i).setPreferredWidth(attributesTableColumnWidth[i]);                    
    }
    //*********************************
    private void getDomainTableColumnWidth(boolean init)
    {        
        domainTableColumnWidth=new int[domainHeaders.length];        
        int freqIndex=ArrayUtils.indexOf(domainHeaders,"freq");
        
        for(int i=1;i<domainTableColumnWidth.length;i++)
        {
            if(init)
            {
                if(i==0)
                    domainTableColumnWidth[i]=30;
                else if(i==freqIndex)
                    domainTableColumnWidth[i]=40;
                else
                    domainTableColumnWidth[i]=80;
            }
            else
                domainTableColumnWidth[i]=domainTable.getColumnModel().getColumn(i).getPreferredWidth();
        }      
    }        
    //*********************************
    private void setDomainTableColumnWidth()
    {
        if(domainTable!=null)
            for(int i=0;i<domainTableColumnWidth.length;i++)
                domainTable.getColumnModel().getColumn(i).setPreferredWidth(domainTableColumnWidth[i]);                    
    }
    //*********************************
    public void setPointers(ContainerOperations containerOperations,EventsPanel eventsPanel)
    {
        this.containerOperations=containerOperations;
        this.eventsPanel=eventsPanel;
    }
    //*********************************
    public void actionPerformed(ActionEvent event)
    {
        final Object eventObject=event.getSource();        

        if (eventObject == moveAtrributeButton) 
            attributeMoveBy(moveByValue);
    }
//  *********************************
    public void addActionListener(ActionListener actionListener)
    {
        decisionSetButton.addActionListener(actionListener);
        addNewAttributeButton.addActionListener(actionListener);
        removeAttributeButton.addActionListener(actionListener);
    }
//  *********************************
    public void setAttributes(String [][] attributes)
    {
        namesMap.clear();
        int nameIndex=ArrayUtils.indexOf(attrHeaders,"name");
        int typeIndex=ArrayUtils.indexOf(attrHeaders,"type");
        
        Object rows[][];
        if(attributes!=null)
        {
            rows=new Object[attributes[0].length][attrHeaders.length];
            originalAttrNames=new String[attributes[0].length];
            for(int i=0;i<attributes[0].length;i++)
            {
                rows[i][0]=String.valueOf(i+1);
                rows[i][nameIndex]=attributes[0][i];
                originalAttrNames[i]=attributes[0][i];
                rows[i][typeIndex]=new AttributeType(attributes[1][i]);
            }
        }
        else
        {
            rows=new String[1][attrHeaders.length];
            setDomain(null);
            domains=null;
            listener=null;
            eventsPanel.eventsTable=null;
        }
        if(attributesTable==null)
            getAttributesTableColumnWidth(true);
        else
            getAttributesTableColumnWidth(false);
            
        attributesTable = new JTable(rows, attrHeaders);
        scrollAttrPanel.setViewportView(attributesTable);
        attributesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);      
        scrollAttrPanel.setWheelScrollingEnabled(true);
        attributesTable.getTableHeader().setReorderingAllowed(false);        
               	
        //selection
        TableCellEditor editorType = new DefaultCellEditor(typesComboBox);
        attributesTable.getColumnModel().getColumn(typeIndex).setCellEditor(editorType);        
        attributesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attributesTable.getSelectionModel().addListSelectionListener(this);        
        setAttributesTableColumnWidth();
        
        JTextField textField= new JTextField();
        textField.setBorder(null);
        TableCellEditor editorName = new DefaultCellEditor(textField);
        attributesTable.getColumnModel().getColumn(nameIndex).setCellEditor(editorName);        
        editorName.addCellEditorListener(this);
            
        if(attributes!=null)
        {        	
        	listener = new AttributesTableSelectionListener(attributesTable);
        	attributesTable.getSelectionModel().addListSelectionListener(listener);        
        	listener.setEventsTable(eventsPanel.eventsTable);
        	//renderer
        	//as default set the last one        
        	setDecisionAttribute(rows[rows.length-1][nameIndex].toString());   
        	setAttributeTypeRenderer();
        	setAnchorAttributeIndex(0);
        }        	
    }
//  *********************************
    public void setDomains(SDomain domains[])
    {
        this.domains=domains;
    }
//  *********************************
    private void setDomain(String [][] domain)
    {
        int domainIndex=ArrayUtils.indexOf(domainHeaders,"domain");
        int freqIndex=ArrayUtils.indexOf(domainHeaders,"freq");        
        
        int domainSize=1;
        if(domain!=null)
        	domainSize=domain[0].length;
        
        String rows[][]=new String[domainSize][domainHeaders.length];
        if(domain!=null)
        {
        	
        	for(int i=0;i<domain[0].length;i++)
            {                
                rows[i][0]=String.valueOf(i+1);
                rows[i][domainIndex]=domain[0][i];
                rows[i][freqIndex]=domain[1][i];
            }
        }  
        if(domainTable==null)
            getDomainTableColumnWidth(true);
        else
            getDomainTableColumnWidth(false);
        
        domainTable = new JTable(rows, domainHeaders);
        scrollDomainPanel.setViewportView(domainTable);
        domainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);      
        scrollDomainPanel.setWheelScrollingEnabled(true);
        domainTable.getTableHeader().setReorderingAllowed(false);        

        setDomainTableColumnWidth();
    }
//  *********************************
    private void setAttributeTypeRenderer()
    {
        TableCellRenderer renderer = new CustomTableCellRenderer();
        String[] types=Attribute.getSupportedTypes();
        for(int i=0;i<types.length;i++)
            ((CustomTableCellRenderer)renderer).addMarkedValue(types[i],Attribute.getGUIColor(types[i]));    
        
        int typeIndex=ArrayUtils.indexOf(attrHeaders,"type");
        attributesTable.getColumnModel().getColumn(typeIndex).setCellRenderer(renderer);
    }
//  *********************************
    public void setDecisionAttribute(String decisionAttr)
    {
        this.decisionAttribute=decisionAttr;        
        TableCellRenderer renderer = new CustomTableCellRenderer();
        ((CustomTableCellRenderer)renderer).addMarkedValue(decisionAttribute,decisionAttrColor);
        ((CustomTableCellRenderer)renderer).setMyToolTipText(decisionToolTipText);        
        ((CustomTableCellRenderer)renderer).markAnchorRow=true;

        int nameIndex=ArrayUtils.indexOf(attrHeaders,"name");      
        attributesTable.getColumnModel().getColumn(nameIndex).setCellRenderer(renderer);
        
        attributesTable.repaint();            
    }
//  *********************************
    public String getDecisionAttributeName()
    {        
        return decisionAttribute;
    }
//  *********************************
    public String getAnchorAttributeName()
    {
        return getAttrName(getAnchorAttributeIndex());        
    }
//  *********************************
    public int getAnchorAttributeIndex()
    {        
        if(attributesTable!=null)
            return attributesTable.getSelectionModel().getAnchorSelectionIndex();
        else
            return -1;
        //or getMinSelectionIndex
    }
//  *********************************
    public void setAnchorAttributeIndex(int index)
    {
        int attrIndex;
        if(index<0)
            attrIndex=0;
        else if(index>=attributesTable.getRowCount())
            attrIndex=attributesTable.getRowCount()-1;
        else
            attrIndex=index;
        
        attributesTable.getSelectionModel().setAnchorSelectionIndex(attrIndex);        
        refreshDomains();
    }
//  *********************************   
    public void valueChanged(ListSelectionEvent event)
    {
        final Object eventObject=event.getSource();        

        if (eventObject == attributesTable.getSelectionModel()) 
        {                       
            ListSelectionModel lsm = (ListSelectionModel)event.getSource();
            if (!lsm.isSelectionEmpty())
            	refreshDomains();                          
        }
    }
//  *********************************
    public void refreshDomains()
    {
        int index=getAnchorAttributeIndex();
    	if(index==-1)
    		index=0;
        if(domains!=null)
            setDomain(domains[index].getDomain()); 
    }
//  *********************************
    public String[][] getAttributes()
    {
        TableModel model=attributesTable.getModel();
        final int attrNumber=model.getRowCount();

        int nameIndex=ArrayUtils.indexOf(attrHeaders,"name");
        int typeIndex=ArrayUtils.indexOf(attrHeaders,"type");
        int attributeFeatures=2;

        String[][] attr=new String[attributeFeatures][attrNumber];

        for(int i=0;i<attrNumber;i++)
        {
            attr[0][i]=model.getValueAt(i, nameIndex).toString();
            attr[1][i]=model.getValueAt(i, typeIndex).toString();
        }
        return attr;	
    }
//  *********************************
    protected int getAttrIndex(String attrName)
    {
        TableModel model=attributesTable.getModel();
        int nameIndex=ArrayUtils.indexOf(attrHeaders,"name");
        final int attrNumber=model.getRowCount();

        for(int i=0;i<attrNumber;i++)
            if(model.getValueAt(i, nameIndex).toString().equalsIgnoreCase(attrName))
                return i; 
        return -1;                        
    }
//  *********************************
    protected String getAttrName(int attrIndex)
    {
        TableModel model=attributesTable.getModel();
        int nameIndex=ArrayUtils.indexOf(attrHeaders,"name");        
        return model.getValueAt(attrIndex, nameIndex).toString();
    }
    //******************************
    private boolean attributeMoveBy(int move)
    {
        attributesTable.setIgnoreRepaint(true);        
        TableModel model=attributesTable.getModel();
        int attributeIndex = getAnchorAttributeIndex();

        boolean up=true;
        if(move>0)
            up=false;

        System.out.println("Moving attribute "+ getAttrName(attributeIndex)+" by "+move+" Positions.");
        while(move!=0)
        {
            if(!moveByOne(attributeIndex, model, up))
                move=0;
            else if(up)
            {
                move++;
                attributeIndex--;
            }
            else
            {
                move--;  
                attributeIndex++;
            }
        }        
        attributesTable.setIgnoreRepaint(false);
        eventsPanel.fillEventsTable();
        return true;
    }
//  *********************************
    private boolean moveByOne(int attributeIndex, TableModel model, boolean up)
    {
        int move=1;
        if(up)
            move=-1;

        if(attributeIndex==0 && up)
            return false;
        if( attributeIndex==model.getRowCount()-1 && !up )
            return false;

        int nameIndex=ArrayUtils.indexOf(attrHeaders,"name");
        int typeIndex=ArrayUtils.indexOf(attrHeaders,"type");          
        String tmpName=model.getValueAt(attributeIndex, nameIndex).toString();
        String tmpType=model.getValueAt(attributeIndex, typeIndex).toString();
        model.setValueAt(model.getValueAt(attributeIndex+move, nameIndex), attributeIndex, nameIndex);
        model.setValueAt(model.getValueAt(attributeIndex+move, typeIndex), attributeIndex, typeIndex);        
        model.setValueAt(tmpName, attributeIndex+move, nameIndex);
        model.setValueAt(tmpType, attributeIndex+move, typeIndex);
        
        String tmpOriginalName=originalAttrNames[attributeIndex];
        originalAttrNames[attributeIndex]=originalAttrNames[attributeIndex+move];
        originalAttrNames[attributeIndex+move]=tmpOriginalName;
        
        attributesTable.getSelectionModel().setAnchorSelectionIndex(attributeIndex+move);
        
        containerOperations.container.swapColumns(attributeIndex, attributeIndex+move);
        
        return true;
    }
//  *********************************
    public String getNewName(String originalName)
    {
        String newName=namesMap.get(originalName);
        if(newName==null)
            newName=originalName;
        
        return newName;
    }
//  *********************************
    public void insertUpdate(DocumentEvent e) 
    {
        if(moveByTextField.getDocument()==e.getDocument())        
            setLabel();
    }
//  *********************************
    public void removeUpdate(DocumentEvent e)
    {
        if(moveByTextField.getDocument()==e.getDocument())
            setLabel();
    }
//  *********************************
    public void changedUpdate(DocumentEvent e)
    {
    }
//  *********************************
    public void setLabel()
    {
        if (moveByTextField.isValid())
            moveByValue=moveByTextField.getValue();
    }
//  *********************************    
    public void editingCanceled(ChangeEvent arg0)
    {                  
    }
//  *********************************
    public void editingStopped(ChangeEvent arg0)
    {
        TableCellEditor editor=(TableCellEditor)arg0.getSource();
        System.out.println("Old Attribute Name: "+originalAttrNames[getAnchorAttributeIndex()]);
        System.out.println("New Attribute Name: "+editor.getCellEditorValue());
        namesMap.put(originalAttrNames[getAnchorAttributeIndex()], editor.getCellEditorValue().toString());
    }
//  *********************************
}
