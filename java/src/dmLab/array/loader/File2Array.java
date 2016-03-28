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

import java.io.File;

import dmLab.array.Array;
import dmLab.array.loader.fileLoader.FileLoader;
import dmLab.array.loader.fileLoader.FileLoaderADX;
import dmLab.array.loader.fileLoader.FileLoaderArff;
import dmLab.array.loader.fileLoader.FileLoaderCSV;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.utils.FileUtils;

public class File2Array extends Data2Array
{    
    FileLoader fileLoader;
    public char separatorCSV=',';
    public boolean firstLineContainsAttributesCSV=true;
    public String defaultAttributeNameCSV="attr";
    public boolean trimCommentsCSV=false;
    public boolean consequentSeparatorsTreatAsOneCSV=false;
    //*******************************    
    public File2Array()
    {
        fileLoader=null;
    }
    //*******************************
    public boolean load(Array array, String inputFileName)
    {
        File file=new File(inputFileName);
        String fileExt = FileUtils.getFileExtension(file.getName());
        init(fileExt);
        if(fileLoader==null){
        	System.err.println("\nFormat of input file: "+ inputFileName +" is not recognized. Format: '"+fileExt+"'");
        	return false;
        }
        else
        	return fileLoader.loadFile(array, inputFileName);
    }
    //*******************************
    private void init(String fileType)
    {                       
        int type = FileType.toType(fileType);
        if(type==FileType.ADX)
        {
            fileLoader=new FileLoaderADX();
            fileLoader.init();
        }
        else if(type==FileType.ARFF)
        {
            fileLoader=new FileLoaderArff();
            fileLoader.init();
        }
        else if(type==FileType.CSV)
        {
            fileLoader=new FileLoaderCSV();
            fileLoader.init();
            ((FileLoaderCSV)fileLoader).firstLineContainsAttributes=firstLineContainsAttributesCSV;
            ((FileLoaderCSV)fileLoader).separator=separatorCSV;
            ((FileLoaderCSV)fileLoader).defaultAttributeName=defaultAttributeNameCSV;
            ((FileLoaderCSV)fileLoader).trimComments=trimCommentsCSV;
            ((FileLoaderCSV)fileLoader).consequentSeparatorsTreatAsOne=consequentSeparatorsTreatAsOneCSV;
        }
        else
            fileLoader=null;        
    }
    //*******************************    
}
