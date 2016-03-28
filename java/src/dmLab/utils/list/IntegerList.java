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
package dmLab.utils.list;


public class IntegerList
{
  /** init size for array*/
  private int initSize=500;
  /** array with integer values */
  public int list[]; //for speed
  /** size of the list */
  int size;
  //***********************************
   /**
    * standard constructor initializes array
    */
   public IntegerList()
  {
    clear();
  }
  //***********************************
   /**
    * constructor that uses initSize as default value of list's length
    * @param initSize int
    */
   public IntegerList(int initSize)
  {
    this.initSize=initSize;
    clear();
  }
  //***********************************
   /**
    * method clears an array
    */
   public void clear()
  {
    size=0;
    list=new int[initSize];
  }
  //***********************************
  /**
  * method adds new integer value into the list
  * @param integer int new element
  * @return int current size of the list
  */
  public int add(int integer)
  {
    if(size==list.length)
      extend();

    list[size++]=integer;
    return size;
  }
  //***********************************
  /**
  * returns value from the list
  * @param index int index of the value
  * @return int value from the list
  */
  public int get(int index)
  {
    if(index>=0 && index<size)
      return list[index];
    else
      return -1;
  }
  //***********************************
  /**
  * method reallocates the array
  */
  private void extend()
  {
    int listTmp[]= new int[list.length+initSize];
    System.arraycopy(list,0,listTmp,0,size);
    list=listTmp;
    listTmp=null;
  }
  //***********************************
  /**
  * returns size of the list
  * @return int list size
  */
  public int size()
  {
    return size;
  }
  //***********************************
  /**
  * returns array as double values
  * @return double[] array as doubles
  */
  public double[] toDblArray()
  {
    double array[]= new double[size];
    for (int i = 0; i < size; i++)
      array[i]=list[i];

    return array;
  }
  /**
   * returns array as double values
   * @return double[] array as doubles
   */
   public int[] toIntArray()
   {
     int array[]= new int[size];
     for (int i = 0; i < size; i++)
       array[i]=list[i];

     return array;
   }
  //***********************************
  public boolean contains(int value)
  {
  	for(int i=0;i<size;i++)
  		if(list[i]==value)
  			return true;
  	return false;	
  }
  //***********************************
}
