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
package dmLab.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import dmLab.utils.helpers.MinMax;


public class ArrayUtils
{
	public Random random;

	//  *****************************************
	public ArrayUtils(){
		random = new Random(System.currentTimeMillis());
		System.err.println("\n\n\n AUTO RND SEED \n\n\n");		
	}	
	//*****************************************
	public ArrayUtils(Random random){
		this.random = random;
	}	
	//*****************************************
	public int[] randomSelect(int array[], int size){
		return randomSelect(array, size, 1, 0);
	}
	//*****************************************
	// used by ADX, train/test split and class balancing task
	public int[] randomSelect(int[] array, int posValuesNumber, int posValue, int negValue)
	{
		int negValuesNumber = array.length-posValuesNumber;		
		if(posValuesNumber < (array.length)/2.0){
			Arrays.fill(array, negValue);
			randomFill(array, posValuesNumber, posValue);
		}else{  
			//If I have to pick less then a half of events
			Arrays.fill(array, posValue);
			randomFill(array, negValuesNumber, negValue);
		}
		return array;
	}
	//*****************************************
	//method fills array randomly with values from range [0,maxValue)
	public void randomFill(int array[], int maxValue)
	{      
		for(int i=0;i<array.length;i++){
			array[i]=random.nextInt(maxValue);
		}
	}
	//*****************************************
	// method randomly fills array
	public void randomFill(int array[], int size, int value)
	{
		int shoot;
		for(long i=0; i<size; i++){
			do{
				shoot = random.nextInt(array.length);
			}while(array[shoot] == value);
			array[shoot] = value;
		}
	}
	//***************************************************
	public float[] shuffle(float[] array, int rep){
		for(int i=0; i<rep; i++)
			shuffle(array);
		return array;
	}
	//***************************************************
	public float[] shuffle(float[] array)
	{
		int index;
		float temp;
		for (int i = array.length - 1; i > 0; i--){
			index = random.nextInt(i + 1);
			temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
		return array;
	}
	//  *********************************************************
	//  **** quick sort of few columns
	public static void qSort(float array[][],int sortIndex)
	{
		if(sortIndex>=array[0].length)
			return;
		else
			qSort(array,sortIndex,0, array.length-1);
	}
	//  *********************************************************
	//  **** quick sort of few columns
	private static void qSort(float array[][],int sortIndex, int lo0, int hi0)
	{
		int lo = lo0;
		int hi = hi0;
		float mid;

		if ( hi0 > lo0)
		{
			mid = array[( lo0 + hi0 ) / 2 ][sortIndex];
			while( lo <= hi )
			{
				while( ( lo < hi0 ) && ( array[lo][sortIndex] < mid || Float.isNaN(array[lo][sortIndex])) )
					++lo;

				while( ( hi > lo0 ) && ( array[hi][sortIndex] > mid ) )
					--hi;

				if( lo <= hi )
				{
					swap(array, lo, hi);

					++lo;
					--hi;
				}
			}

			if( lo0 < hi )
				qSort( array,sortIndex, lo0, hi );

			if( lo < hi0 )
				qSort( array,sortIndex, lo, hi0 );
		}
	}
	//  *****************************************
	//  **** swaps two elements in array[][]
	private static void swap(float array[][],int i, int j)
	{
		float tempArray[]=array[i].clone();
		array[i]=array[j];
		array[j]=tempArray.clone();
	}
	//  *********************************************
	//  **** method counts occurring value in array
	public static int count(double array[], double value)
	{
		int counter=0;
		for(int i=0;i<array.length;i++)
			if(array[i]==value)
				counter++;
		return counter;
	}
	//  *********************************************
	//  **** method counts occurring value in array
	public static int count(float array[], float value)
	{
		int counter=0;
		for(int i=0;i<array.length;i++)
			if(array[i]==value)
				counter++;
		return counter;
	}
	//  *********************************************
	//  **** method counts occurrences of the  value in array
	public static int count(int array[], int value)
	{
		int counter=0;
		for(int i=0;i<array.length;i++)
			if(array[i]==value)
				counter++;
		return counter;
	}
	//  *********************************************
	//  **** method counts occurrences of the value in array
	public static int count(boolean array[], boolean value)
	{
		int counter=0;
		for(int i=0;i<array.length;i++)
			if(array[i]==value)
				counter++;
		return counter;
	}
	//*********************************************
	public static double[] toDoubleArray(String arrayStr) throws NumberFormatException
	{
		double array[];
		int stop,start=0;
		String tempWord;
		ArrayList <String>tempList=new ArrayList<String>();
		while(start < arrayStr.length())
		{
			stop=arrayStr.indexOf(",",start);
			if(stop==-1)
				stop=arrayStr.length();
			tempWord=arrayStr.substring(start,stop).trim();
			if(tempWord.length()!=0)
			{
				tempList.add(tempWord);
				start=stop+1;
			}
			else
			{
				System.err.println("Array is empty");
				return null;
			}
		}
		array=new double[tempList.size()];
		for(int i=0;i<array.length;i++)
			array[i]=Double.parseDouble(tempList.get(i));

		return array;
	}
	//*********************************************
	public static int[] toIntArray(String arrayStr) throws NumberFormatException
	{
		int array[];
		int stop,start=0;
		String tempWord;
		ArrayList <String>tempList=new ArrayList<String>();
		while(start < arrayStr.length())
		{
			stop=arrayStr.indexOf(",",start);
			if(stop==-1)
				stop=arrayStr.length();
			tempWord=arrayStr.substring(start,stop).trim();
			if(tempWord.length()!=0)
			{
				tempList.add(tempWord);
				start=stop+1;
			}
			else
			{
				System.err.println("Array is empty");
				return null;
			}
		}
		array=new int[tempList.size()];
		for(int i=0;i<array.length;i++)
			array[i]=Integer.parseInt(tempList.get(i));

		return array;
	}
	//******************************************
	public static float leaveValue(float array[],float value)
	{
		float otherValue=Float.NaN;
		boolean loaded=false;
		for(int i=0;i<array.length;i++)
		{
			if(array[i]!=value)
			{
				if(loaded==false)
				{
					otherValue=array[i];
					loaded=true;
				}
				array[i]=otherValue;
			}
		}
		return otherValue;
	}
	//******************************************
	public static void leaveValue(float array[],float value,float newValue)
	{
		for(int i=0;i<array.length;i++)
			if(array[i]!=value)
				array[i]=newValue;
	}
	//******************************************
	public static int indexOf(Object[] array, Object o)
	{
		for (int i=0;i<array.length;i++)
			if(array[i].equals(o))
				return i;
		return -1;
	}
	//  *********************************************
	public static int indexOf(int[] array, int value)
	{
		for (int i=0;i<array.length;i++)
			if(array[i]==value)
				return i;
		return -1;
	}
	//  ********************************************* 
	public static int indexOf(float array[], float value, boolean binary)
	{
		if(Float.isNaN(value)){
			return -1;
		}else if(value >= array[array.length-1]){
			return array.length-1;
		}else if(!binary){
			//classic sequential search
			for (int i=0;i<array.length;i++)
				if(value <= array[i])
					return i;
			return -1;
		}else{
			//binary search
			int left=0,right=array.length,mid=0;
			boolean found=false;

			while(left <= right && found!=true){
				mid=(left+right)/2;
				if(mid==0)
					return mid;
				if(value > array[mid-1] && value <= array[mid])
					return mid;
				else{
					if(array[mid]<value)
						left=mid+1;
					else
						right=mid-1;
				}
			}
			return mid;
		}
	}
	//  *********************************************
	//returns -1 if values are equal 
	public static int maxIndex(double array[])
	{
		if(array.length==0)
			return -1;

		int maxIndex=0;
		double maxVal=array[0];

		for(int i=1;i<array.length;i++){
			if(array[i]>maxVal)
			{
				maxVal=array[i];
				maxIndex=i;
			}
			else if(array[i]==maxVal)
				maxIndex=-1;    
		}
		return maxIndex;
	}
	//  *********************************************
	public static int minIndex(int array[])
	{
		if(array.length==0)
			return -1;

		int minVal=array[0];
		int minIndex=0;

		for(int i=1;i<array.length;i++)
			if(array[i]<minVal)
			{
				minVal=array[i];
				minIndex=i;
			}
		return minIndex;
	}
	//***************************************************
	public static MinMax getMinMax(float array[], boolean include_negative)
	{
		if(array.length==0)
			return null;
		
		MinMax minMax = new MinMax();
		
		for(int i=0; i<array.length; i++) {
			if(include_negative || array[i] >= 0) {
				if((Float.isNaN(minMax.minValue) && !Float.isNaN(array[i])) || array[i] < minMax.minValue) {
					minMax.minValue = array[i];
					minMax.minIndex = i;
				}				
				if((Float.isNaN(minMax.maxValue) && !Float.isNaN(array[i])) || array[i] > minMax.maxValue) {
					minMax.maxValue = array[i];
					minMax.maxIndex = i;
				}
			}
		}
		return minMax;
	}
	//***************************************************
	public static float[] scaleArray(float[] array, float min, float max, boolean include_negative) {
		MinMax min_max = getMinMax(array, include_negative);
		for(int i=0;i<array.length;i++) {
			if(include_negative || array[i] >= 0) {
				array[i] = (array[i]-min_max.minValue)/(min_max.maxValue-min_max.minValue);
				array[i] = (array[i] * (max-min))+min;
			}
		}
		return array;		
	}
	//***************************************************
	public static boolean valueIn(int x, int[] array)
	{
		for(int i=0;i<array.length;i++){
			if(x == array[i])
				return true;
		}
		return false;
	}
	//***************************************************
	public static int[] distribution(float array[], float values[])
	{
		int distribution[]=new int[values.length];
		for(int i=0;i<array.length;i++){
			for(int j=0;j<values.length;j++){
				if(array[i]==values[j])
					distribution[j]++;
			}
		}
		return distribution;
	}
	//***************************************************
	public static int[] distribution(float array[], float values[], double ratio)
	{
		int[] distribution = distribution(array, values);
		for(int j=0;j<values.length;j++)
			distribution[j]=(int)Math.floor(distribution[j] * ratio);
		return distribution;
	}
	//***************************************************    
	public static int getRouletteIndex(double array[])
	{
		double sum=MathUtils.sum(array);
		Random random= new Random();
		double r=random.nextDouble()*sum;
		sum=0;
		for(int i=0;i<array.length;i++)
		{
			sum+=array[i];
			if(r<=sum)
				return i;                
		}
		return -1;
	}
	//  *********************************************
	public static float[] unique(float[] array){
		HashSet<Float> uniqValues = new HashSet<Float>();
		for (int i=0; i<array.length; i++){
			uniqValues.add(array[i]);
		}
		Float[] floatArray = new Float[1];
		floatArray = uniqValues.toArray(floatArray);

		return float2Float(floatArray);
	}
	//*********************************************
	public static double[] float2double(float[] array){
		double[] dblArray = new double[array.length];
		for (int i=0; i<array.length; i++){
			dblArray[i] = (double) array[i];
		}
		return dblArray;
	}
	//  *********************************************
	public static double[] Double2double(Object[] array)
	{
		double[] d = new double[array.length];
		for(int i=0; i<array.length; i++){
			if(array[i]==null)
				d[i]=Double.NaN;
			else	
				d[i]=(Double)array[i];
		}
		return d;  		
	}
	//  *********************************************	
	public static double[] Float2double(Object[] array)
	{
		double[] d = new double[array.length];
		for(int i=0; i<array.length; i++){
			d[i]=(Float)array[i];
		}
		return d;  		
	}
	//  *********************************************
	public static Float[] float2Float(float[] array){
		Float[] floatArray = new Float[array.length];
		for (int i=0; i<array.length; i++){
			floatArray[i] = new Float(array[i]);
		}
		return floatArray;
	}
	//  *********************************************
	public static float[] float2Float(Float[] array){
		float[] floatArray = new float[array.length];
		for (int i=0; i<array.length; i++){
			floatArray[i] = array[i].floatValue();
		}
		return floatArray;
	}
	//  *********************************************
	public static String[] float2String(float[] array){
		String[] stringArray = new String[array.length];
		for (int i=0; i<array.length; i++){
			stringArray[i] = Float.toString(array[i]);
		}
		return stringArray;
	}
	//  *********************************************
	public static float[] string2float(String[] array){
		float[] floatArray = new float[array.length];
		for (int i=0; i<array.length; i++){
			try{
				floatArray[i] = Float.parseFloat(array[i]);
			}catch (Exception e){
				System.err.println("Error parsing float value! Value: " + array[i]);
				return null;
			}
		}
		return floatArray;
	}
	//  *********************************************
	public static Integer[] int2Integer(int[] array){
		Integer[] intArray = new Integer[array.length];
		for (int i=0; i<array.length; i++){
			intArray[i] = new Integer(array[i]);
		}
		return intArray;
	}
	//  *********************************************
	public static int[] Integer2int(Integer[] array){
		int[] intArray = new int[array.length];
		for (int i=0; i<array.length; i++){
			intArray[i] = array[i].intValue();
		}
		return intArray;
	}
	//  *********************************************
	public static int[] double2int(double[] array){
		int[] intArray = new int[array.length];
		for (int i=0; i<array.length; i++){
			intArray[i] = (int)(array[i]);
		}
		return intArray;		
	}
	//  *********************************************
	public static double[] int2double(int[] array){
		double[] dblArray = new double[array.length];
		for (int i=0; i<array.length; i++){
			dblArray[i] = (double)(array[i]);
		}
		return dblArray;		
	}
	//  *********************************************
	public static float[] int2float(int[] array){
		float[] fltArray = new float[array.length];
		for (int i=0; i<array.length; i++){
			fltArray[i] = (float)(array[i]);
		}
		return fltArray;		
	}
	//  *********************************************
	public static boolean[] int2boolean(int[] array, int trueValue){
		boolean[] booleanArray = new boolean[array.length];
		for (int i=0; i<array.length; i++){
			if(array[i] == trueValue)
				booleanArray[i] = true;
			else
				booleanArray[i] = false;				
		}
		return booleanArray;		
	}
	//  *********************************************
}
