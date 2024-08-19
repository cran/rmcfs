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
package dmLab.array.loader.fileLoader;

import dmLab.utils.fileFilters.MyFileFilter;

public class FileType {

	private int fileType;

	public final static int UNKNOWN = -1;
	public final static int ADX = 0;
	public final static int ADH = 1;
	public final static int ARFF = 2;
	public final static int CSV = 3;

	public final static int DTA = 8;
	public final static int VAR = 9;

	private final static MyFileFilter[] supportedFiles = new MyFileFilter[] {
			new MyFileFilter("adx", "dmLab"),
			new MyFileFilter("adh", "csv header file"),
			new MyFileFilter("arff", "WEKA"),
			new MyFileFilter("csv", "CommaSeparated")};

	//  *******************************	
	public FileType()
	{		
		fileType = ADX;
	}
	//  *******************************
	private static boolean isSupported(int type)
	{
		if(type==ADX || type==ADH || type==ARFF || type==CSV)
			return true;
		else
			return false;            
	}
	//	*******************************
	public boolean setType(int type)
	{
		if(isSupported(type))
		{
			this.fileType=type;
			return true;
		}
		else
		{
			System.err.println("Unrecognized FileType: " + type);
			return false;
		}
	}
	//	*******************************	
	public String getTypeStr()
	{
		return toTypeStr(fileType);
	}
	//	*******************************	
	public int getType()
	{
		return fileType;
	}
	//	*******************************
	public static String toTypeStr(int type)
	{
		if(isSupported(type))
			return FileType.supportedFiles[type].getExt();					
		else
			return null;		 
	}		
	//*******************************
	public static int toType(String typeStr)
	{
		for(int i=0;i<FileType.supportedFiles.length;i++)
			if(FileType.supportedFiles[i].extEquals(typeStr))
				return i;
		return -1;
	}
	//	*******************************
	public static MyFileFilter[] getSupportedTypes()
	{
		return supportedFiles;
	}
	//  *******************************
}
