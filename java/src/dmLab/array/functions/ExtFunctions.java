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
package dmLab.array.functions;

import java.util.Arrays;
import java.util.Random;

import dmLab.array.Array;
import dmLab.array.FArray;
import dmLab.array.SArray;
import dmLab.array.meta.Attribute;
import dmLab.mcfs.MCFSParams;
import dmLab.utils.ArrayUtils;

public class ExtFunctions {
	
	//****************************************************
	public static void addColumnsBinary(FArray array, String colNamePrefix, int columns)
	{
		addColumnsRandom(array, colNamePrefix, columns, 2);
	}
	//****************************************************
	public static void addColumnsUniform(FArray array, String colNamePrefix, int columns)
	{
		addColumnsRandom(array, colNamePrefix, columns, 0);
	}
	//****************************************************
	private static void addColumnsRandom(FArray array, String colNamePrefix, int columns, int valuesNumber)
	{
		final int rows = array.rowsNumber();
		FArray srcArray = new FArray(columns, rows);
		Random rand = new Random();

		for(int i=0; i<columns; i++){
			srcArray.attributes[i].type = Attribute.NUMERIC;
			srcArray.attributes[i].name = colNamePrefix+i;
			for(int j=0; j<rows; j++){
				if(valuesNumber==0)
					srcArray.writeValue(i, j, rand.nextFloat());
				else
					srcArray.writeValue(i, j, rand.nextInt(valuesNumber));
			}
		}
		array.cbind(srcArray);
	}
	//  ***********************************
	public static void addContrastAttributes(FArray array)
	{		
		boolean[] colMask = new boolean[array.colsNumber()]; 
		Arrays.fill(colMask, true);
		if(array.getDecAttrIdx() != -1)
			colMask[array.getDecAttrIdx()] = false;
		
		FArray srcArray = array.clone(colMask, null);				
		final int srcColumns = srcArray.colsNumber();

		ArrayUtils arrayUtils = new ArrayUtils();
		for(int i=0; i<srcColumns; i++){
			srcArray.attributes[i].name = MCFSParams.CONTRAST_ATTR_NAME + srcArray.attributes[i].name;
			float[] currColumn = srcArray.getColumn(i);
			currColumn = arrayUtils.shuffle(currColumn, 3);
			srcArray.setColumn(i, currColumn);			
		}
		array.cbind(srcArray);		
	}
	//****************************************************	
	public static boolean isContrastAttribute(String colName){
		if(colName.startsWith(MCFSParams.CONTRAST_ATTR_NAME))
			return true;
		else
			return false;
	}
	//****************************************************
    public static void addAttribute(Array array)
    {        
        addAttribute(array, getNewAttributeName(array));
    }
	//****************************************************
    private static String getNewAttributeName(Array array)
    {
        if(array!=null){
            int i=1;
            String name = "";
            while(true){
                name = "attr_" + Integer.toString(i);
                if(array.getColIndex(name) == -1)
                    return name;
                else
                    i++;
            }
        }
        return null;    
    }
	//****************************************************
    public static void addAttribute(Array array, String name)
    {        
        final int rows = array.rowsNumber();

        Array srcArray;
        if (array instanceof FArray)
        	srcArray = new FArray(1,rows);
        else
        	srcArray = new SArray(1,rows);

        srcArray.attributes[0].name=name;
        srcArray.attributes[0].type=Attribute.NUMERIC;
		array.cbind(srcArray);		
    }
	//****************************************************
}
