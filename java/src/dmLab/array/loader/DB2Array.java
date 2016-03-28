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
package dmLab.array.loader;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import dmLab.array.Array;
import dmLab.array.loader.fileLoader.NullLabels;
import dmLab.array.meta.Attribute;

public class DB2Array extends Data2Array
{
    //*******************************    
    public DB2Array()
    {

    }
    //*******************************
    public boolean load(Array container, ResultSet resultSet)
    {
        if(resultSet==null)
            return false;

        int eventsNumber=0;
        int attributesNumber=0;

        // Get result set meta data
        ResultSetMetaData rsmd;
        try {
            rsmd = resultSet.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        try{
            attributesNumber = rsmd.getColumnCount();        
            while (resultSet.next())
                eventsNumber++;        
            //set on the first one
            resultSet.first();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if(eventsNumber<1)
        {
            System.err.println("Number of Events is 0.");
            return false;
        }
        if(attributesNumber<1)
        {
            System.err.println("Number of Attributes is 0.");
            return false;
        }
        
        container.init(attributesNumber,eventsNumber);
        try{
            //load attributes
            for (int i=0; i<attributesNumber; i++)
            { 
                container.attributes[i].name=rsmd.getColumnName(i+1);
                container.attributes[i].type=Attribute.NOMINAL;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        int row=0;
        if(progressBar!=null) progressBar.setMinimum(0);
        if(progressBar!=null) progressBar.setMaximum(eventsNumber);
        try{        
            //load events
            do{
                for (int i=0; i<attributesNumber; i++)
                {
                    String tmpStr=resultSet.getString(i+1);
                    if(tmpStr==null)
                        tmpStr=NullLabels.defaultLabel;
                    container.writeValueStr(i, row, tmpStr);
                }
                row++;
                if(progressBar!=null) progressBar.setValue(row);                
            }while(resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        container.setDecAttrIdx(attributesNumber-1);//set the last one
        container.setAllDecValues();

        return true;
    }
    //*******************************


}
