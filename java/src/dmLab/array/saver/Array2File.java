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
package dmLab.array.saver;

import dmLab.array.Array;
import dmLab.array.loader.fileLoader.FileType;
import dmLab.utils.FileUtils;

public class Array2File
{
	public FileType fileType;
	private Array2String container2String;	
    public char separatorCSV = ',';
    
//****************************************
	public Array2File()
	{		
		fileType=new FileType();
	}
//	****************************************
	public void setFormat(int format)
	{
		fileType.setType(format);
	}
//	****************************************
	public String toString(Array container)
	{		        
		if(fileType.getType()==FileType.ADX)
			container2String=new Array2ADX();
		else if(fileType.getType()==FileType.ADH)
			container2String=new Array2ADH();
		else if(fileType.getType()==FileType.ARFF)
			container2String=new Array2Arff();
		else if(fileType.getType()==FileType.CSV){
			container2String=new Array2CSV();
            container2String.separator = separatorCSV;
        }
        else if(fileType.getType()==FileType.DTA)          
            container2String=new Array2DTA();
        else if(fileType.getType()==FileType.VAR)          
            container2String=new Array2VAR();        
		else
			return null;
		
		return container2String.toString(container);
	}
//	************************************************
	public boolean saveFile(Array container, String outFileName)
	{      
				
		String fileName;    
		String ext=FileUtils.getFileExtension(outFileName);
        //extension in outFileName determines type of saved file
        int type=FileType.toType(ext);
        if(type!=-1)
        {
            fileType.setType(type);
            fileName=outFileName;
        }
		else
			fileName=outFileName+"."+fileType.getTypeStr();
		
		return FileUtils.saveString(fileName, toString(container));
	}
//************************************************	
}
