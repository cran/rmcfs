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
package dmLab.gui.dataEditor;

import java.sql.ResultSet;

import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;

import dmLab.array.Array;
import dmLab.array.SArray;
import dmLab.array.domain.SDomain;
import dmLab.array.loader.DB2Array;
import dmLab.array.loader.File2Array;
import dmLab.array.meta.Attribute;
import dmLab.array.saver.Array2File;
import dmLab.utils.StringUtils;

public class ContainerOperations 
{
    public Array container;
    private Array2File cont2file;
    private File2Array file2Cont;
    private DB2Array db2Container;
    private boolean isFileOpen=false;
    private String filePath;
    private JProgressBar progressBar;
    
    //***********************************
    public ContainerOperations()
    {
        initContainer();
        cont2file=new Array2File();
        file2Cont=new File2Array();
        db2Container=new DB2Array(); 
    }
    //***********************************
    public void setContainer(Array container)
    {
    	this.container=container;
    }
    //***********************************
    public String getFileName()
    {
        return filePath;
    }
    //***********************************
    public boolean isFileOpen()
    {
        return isFileOpen;
    }
    //***********************************
    public void setDefaultAttributeNameCSV(String defaultAttributeNameCSV)
    {
        file2Cont.defaultAttributeNameCSV=defaultAttributeNameCSV;
    }
    //***********************************
    public void setSeparatorCSV(char separator)
    {
        file2Cont.separatorCSV=separator;
        cont2file.separatorCSV=separator;
    }
    //***********************************
    public void setFirstLineContainsAttributesCSV(boolean firstLineContainsAttributesCSV)
    {
        file2Cont.firstLineContainsAttributesCSV=firstLineContainsAttributesCSV;
    }
    //***********************************
    public void setTrimCommentsCSV(boolean trimCommentsCSV)
    {
        file2Cont.trimCommentsCSV=trimCommentsCSV;
    }
    //***********************************
    public void setConsequentSeparatorsTreatAsOneCSV(boolean consequentSeparatorsTreatAsOne)
    {
        file2Cont.consequentSeparatorsTreatAsOneCSV=consequentSeparatorsTreatAsOne;
    }
    //***********************************
    public DefaultTableModel getTableModel()
    {        
        String headers[];
        Object rows[][];

        if(!isFileOpen)
        {
            headers=new String[]{""};
            rows=new Object[][]{{""}};   
        }
        else
        {    
            final int attributesNumber=container.colsNumber(); 
            final int eventsNumber=container.rowsNumber();         
            headers = new String [attributesNumber];

            if(progressBar!=null) progressBar.setMinimum(0);
            if(progressBar!=null) progressBar.setMaximum(attributesNumber);
            
            for(int i=0;i<attributesNumber;i++)
                headers[i]=container.attributes[i].name;
            
            rows=new Object[eventsNumber][attributesNumber];
            
            for(int i=0;i<attributesNumber;i++)
            {
                for(int j=0;j<eventsNumber;j++)
                {
                    if(container.attributes[i].type==Attribute.NUMERIC)
                    {
                        try{
                            Float f = Float.parseFloat(container.readValueStr(i,j));
                            if(f.intValue()==f.floatValue())
                                rows[j][i]=Integer.valueOf(f.intValue());
                            else
                                rows[j][i]=f;
                        }catch(NumberFormatException e)
                        {
                            rows[j][i]=container.readValueStr(i, j);    
                        }                        
                    }
                    else
                        rows[j][i]=container.readValueStr(i, j);
                }
                if(progressBar!=null) progressBar.setValue(i+1);
            }
        }
        DefaultTableModel model = new DefaultTableModel(rows, headers);
        return model;
    }
    //***********************************
    public boolean loadFromFile(String path)
    {
        initContainer();
        if(file2Cont.load(container, path))
        {
            isFileOpen=true;
            filePath=path;
            return true;
        }
        else
            return false;
    }
    //***********************************
    public boolean loadFromDB(ResultSet resultSet)
    {
        initContainer();
        
        if(db2Container.load(container, resultSet))
        {
            isFileOpen=true;
            filePath="dbTable";
            return true;
        }
        else
            return false;
    }
    //***********************************
    public boolean saveToFile(String path)
    {
        if(cont2file.saveFile(container, path))
            return true;
        else
            return false;
    }
    //***********************************
    public boolean initContainer()
    {
        container=new SArray();
        filePath="";
        isFileOpen=false;
        return true;
    }
    //***********************************
    public String[][] getAttributesList()
    {
        String [][] list=new String [2][container.attributes.length];
        for(int i=0;i<container.attributes.length;i++)
        {
            list[0][i]=container.attributes[i].name;
            list[1][i]=Attribute.convert(container.attributes[i].type);
        }
        return list;
    }
//  ***********************************
    public String[][] getDomain(int attributeIndex)
    {        
        return ((SArray)container).domains[attributeIndex].getDomain();   
    }
//  ***********************************
    public SDomain[] getDomains()
    {
        return ((SArray)container).domains; 
    }
//***********************************
    public void fixAttrTypes()
    {
    	final int attributesNumber=container.colsNumber();
    	if(progressBar!=null) progressBar.setMinimum(0);
    	if(progressBar!=null) progressBar.setMaximum(attributesNumber);
            	
        for(int i=0;i<attributesNumber;i++)
        {
        	short newType=((SArray)container).domains[i].fixAttrTypes();

            //EDITOR handles only two types of attributes but determineType() gives 3
            //if(newType==Attribute.INTEGER)
            //    newType=Attribute.NUMERIC;
        	///
            
            if(newType!=-1)
        		container.attributes[i].type=newType;
        	if(progressBar!=null) progressBar.setValue(i+1);
        }       
    }
    //************************************
    public void fixAttrValues()
    {
    	final int attributesNumber=container.colsNumber();
    	boolean mask[] = new boolean[attributesNumber];
    	for(int i=0; i<attributesNumber; i++)
    	{
    		if(container.attributes[i].type == Attribute.NOMINAL)
    			mask[i]=true;
    		else
    			mask[i]=false;
    	}    	
    	replaceValue(" ","_", mask, false);
    }
    //************************************
    public int replaceValue(String source,String destination, boolean mask[], boolean wholeWord)
    {
        int replacedValues=0;
        final int attributesNumber=container.colsNumber(); 
        final int eventsNumber=container.rowsNumber();

        if(progressBar!=null) progressBar.setMinimum(0);
        if(progressBar!=null) progressBar.setMaximum(attributesNumber);

        for(int i=0;i<attributesNumber;i++)
        {
            if(mask[i])
            {
                for(int j=0;j<eventsNumber;j++)
                {
                    String value=container.readValueStr(i, j);
                    if(!wholeWord)
                    {
                        String replacedValue = StringUtils.replaceAll(value,source,destination);                        
                        if(!replacedValue.equalsIgnoreCase(value))
                        {
                            container.writeValueStr(i, j, replacedValue);
                            replacedValues++;                            
                        }
                    }
                    else if(value.equalsIgnoreCase(source))
                    {
                        container.writeValueStr(i, j, destination);
                        replacedValues++;
                    }

                }
            }
            if(progressBar!=null) progressBar.setValue(i+1);
        }
        return replacedValues;
    }
//***********************************
    public void findDomains()
    {
        container.findDomains();
    }
//*************************************
    public void setProgressBar(JProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }
//  *************************************
}
