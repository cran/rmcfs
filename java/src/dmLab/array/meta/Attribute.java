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
package dmLab.array.meta;

import java.awt.Color;


public class Attribute implements Cloneable
{
  public static final short UNKNOWN = 0;
  public static final short NOMINAL = 1;
  public static final short NUMERIC = 2;
  public static final short INTEGER = 3;
  
  public String name;
  public short type;
  
//****************************************
  public Attribute()
  {
    name = null;
    type = UNKNOWN;    
  }
//****************************************
   public static String type2String(short type)
   {
     if(type == NOMINAL)
       return "nominal";
     else if (type == NUMERIC)
       return "numeric";
     else if (type == INTEGER)
       return "integer";
     else
       return null;
   }
//****************************************
   public static short type2Int(String type)
   {
     if(type.equalsIgnoreCase("nominal"))
       return NOMINAL;
     else if (type.equalsIgnoreCase("numeric"))
       return NUMERIC;
     else if (type.equalsIgnoreCase("integer"))
         return INTEGER;
     else
       return -1;
   }
//********************************************
   public static String[] getSupportedTypes()
   {
       String types[]=new String[]{"nominal","numeric","integer"};
       return types;
   }
// ********************************************
   public static Color getGUIColor(String type)
   {      
       Color colors[]=new Color[]{
    		   new Color(255,255,255),//UNKNOWN=0
               new Color(213,255,213),//NOMINAL=1
               new Color(213,213,255),//NUMERIC=2
               new Color(255,255,213)};//INTEGER=3
       return colors[type2Int(type)];
   }
// ********************************************
   @Override
   public Attribute clone()
   {
	   Attribute a = new Attribute();
	   a.name = name;
	   a.type = type;
	   return a; 
   }
// ********************************************
   @Override
   public String toString()
   {
       return name+" "+type;
   }
// ********************************************
}
