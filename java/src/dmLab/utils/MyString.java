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
package dmLab.utils;


public class MyString implements Comparable<MyString>
{
	/** init size for array */
	private int initSize = 64;
	/** array with integer values */
	public char buffer[]; // for speed
	/** size of the list */
	private int size;

	// ***********************************
	/**
	 * standard constructor initializes array
	 */
	public MyString()
	{
		buffer = new char[initSize];
		clear();
	}

	// ***********************************
	/**
	 * constructor that uses initSize as default value of list's length
	 * 
	 * @param initSize
	 *            int
	 */
	public MyString(int initSize)
	{
		this.initSize = initSize;
		buffer = new char[initSize];
		clear();
	}

	// ***********************************
	public MyString(String s)
	{
		buffer = s.toCharArray();
		size = buffer.length;
	}

	// ***********************************
	/**
	 * method clears an array
	 */
	public void clear()
	{
		size = 0;
	}

	// ***********************************
	/**
	 * method adds new integer value into the list
	 * 
	 * @param integer
	 *            int new element
	 * @return int current size of the list
	 */
	public MyString append(char c)
	{
		if (size == buffer.length)
			extend();

		buffer[size++] = c;
		return this;
	}

	// ***********************************
	/**
	 * method reallocates the array
	 */
	private void extend()
	{
		char tmp[] = new char[buffer.length + initSize];
		System.arraycopy(buffer, 0, tmp, 0, size);
		buffer = tmp;
		tmp = null;
	}

	// ***********************************
	/**
	 * returns size of the list
	 * 
	 * @return int list size
	 */
	public int size()
	{
		return size;
	}

	// ***********************************
	/**
	 * returns array String object
	 * 
	 * @return String
	 */
	@Override
	public String toString()
	{
		return new String(buffer, 0, size);
	}

	// ***********************************
	public MyString replace(char source, char destination)
	{
		for (int i = 0; i < size; i++)
			if (buffer[i] == source)
				buffer[i] = destination;
		return this;
	}

	// ***********************************
	public void remove(char c)
	{
		char buffer2[] = new char[buffer.length];
		int size2 = size;
		int j = 0;
		for (int i = 0; i < size; i++)
		{
			if (buffer[i] != c)
				buffer2[j++] = buffer[i];
			else
				size2--;
		}
		buffer = buffer2;
		size = size2;
	}

	// ***********************************
	public int indexOf(char c)
	{
		for (int i = 0; i < size; i++)
			if (buffer[i] == c)
				return i;
		return -1;
	}

	// ***********************************
	@Override
	public int compareTo(MyString o)
	{
		return toString().compareTo(o.toString());
	}
	// ***********************************
}
