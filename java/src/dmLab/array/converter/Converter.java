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
package dmLab.array.converter;

import dmLab.array.Array;
import dmLab.array.SArray;
import dmLab.array.functions.SelectFunctions;
import dmLab.array.loader.File2Array;
import dmLab.array.saver.Array2File;
import dmLab.utils.FileUtils;

public class Converter {

    protected Array container;
    protected Array2File cont2file;
    protected File2Array file2Cont;
    //*****************************************
    public Converter() 
    {
        container = new SArray();
        cont2file = new Array2File();
        file2Cont = new File2Array();        
    }    
    //*****************************************
    public boolean loadFromFile(String path)
    {
        return file2Cont.load(container, path);
    }
    //*****************************************
    public boolean saveToFile(String path)
    {
        return cont2file.saveFile(container, path);
    }
//  *****************************************
    public boolean filterEvents(String filterString)
    {
        container=SelectFunctions.selectRows(container, filterString);
        if(container==null)
            return false;
        else
            return true;
    }
    //*****************************************
    public boolean determineTypes()
    {
        container.findDomains();    
        final int attributesNumber=container.colsNumber();                    
        for(int i=0;i<attributesNumber;i++)
        {
            short newType=((SArray)container).domains[i].fixAttrTypes();
            if(newType!=-1)
                container.attributes[i].type=newType;
        }
        return true;
    }
    //*****************************************
    public static void main(String[] args)
    {
        if(args.length<2)
        {
            System.out.println("Incorrect number of parameters!");
            System.out.println("Example: weather.csv weather.arff [outlook=sunny]");
            return;            
        }
        Converter converter=new Converter();
        
        converter.loadFromFile(args[0]);       
        if(args.length==3)
            converter.filterEvents(args[2]);
        
        if(FileUtils.getFileExtension(args[0]).equalsIgnoreCase("csv"))
            converter.determineTypes();
        
        converter.saveToFile(args[1]);
    }
    //*****************************************
}
