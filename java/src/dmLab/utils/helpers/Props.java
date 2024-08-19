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
package dmLab.utils.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public abstract class Props
{
    protected Properties prop;
    //****************************
    public Props()
    {
        setDefault();
    }
    //***********************************
    public Props(String cfgName)
    {
        load(cfgName);       
        updateProperties();
    }
    //*************************************
    public boolean load(String cfgName)
    {        
        this.prop = new Properties();

        try {
            FileInputStream cfg = new FileInputStream(cfgName);
            prop.load(cfg);
            cfg.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        updateProperties();
        return true;
    }
    //****************************
    public boolean save(String cfgName)
    {
        try {
            FileWriter cfg = new FileWriter(cfgName);
            cfg.write(toString());
            cfg.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;        
    }
    //****************************      
    public abstract boolean setDefault();
    //**********************************  
    public abstract boolean updateProperties();
    //****************************
    @Override
    public abstract String toString();
    //****************************
}
