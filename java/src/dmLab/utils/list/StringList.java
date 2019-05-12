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
package dmLab.utils.list;


public class StringList
{
    /** init size for array*/
    private int initSize=500;
    /** array with integer values */
    public String list[];
    /** size of the list */
    int size;
    //***********************************
    /**
     * standard constructor initializes array
     */
    public StringList()
    {
        clear();
    }
    //***********************************
    /**
     * constructor that uses initSize as default value of list's length
     * @param initSize int
     */
    public StringList(int initSize)
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
        list=new String[initSize];
    }
    //***********************************
    /**
     * method adds new integer value into the list
     * @param integer int new element
     * @return int current size of the list
     */
    public int add(String object)
    {
        if(size==list.length)
            extend();
        
        list[size++]=object;
        return size;
    }
    //***********************************
    /**
     * returns value from the list
     * @param index int index of the value
     * @return int value from the list
     */
    public String get(int index)
    {
        if(index>=0 && index<size)
            return list[index];
        else
            return null;
    }
    //***********************************
    /**
     * method reallocates the array
     */
    private void extend()
    {
    	String listTmp[]= new String[list.length+initSize];
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
    public String[] toArray()
    {
    	String array[]= new String[size];
        for (int i = 0; i < size; i++)
            array[i]=list[i];
        
        return array;
    }
    //***********************************
    public boolean containsIgnoreCase(String object)
    {
        for(int i=0;i<size;i++)
            if(list[i].equalsIgnoreCase(object))
                return true;
        return false;	
    }
    //***********************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        for(int i=0;i<size;i++)            
            tmp.append(list[i]).append('\n');
        return tmp.toString();
    }
    //***********************************
}
