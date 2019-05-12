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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;



public class GeneralUtils
{

	//**************************************
	/**
	 * method formats double values into nice looking strings
	 * @param number input double
	 * @param precision int how many digits after point
	 * @return String converted double
	 */
	public static String formatFloat(double number, int precision)
	{
		String power="";
		String tempString=Double.toString(number);
		int ePosition=tempString.indexOf("E");
		int dotPosition=tempString.lastIndexOf('.');

		if(ePosition!=-1)
			power=tempString.substring(ePosition,tempString.length());

		if(dotPosition==-1 || dotPosition+precision+1>tempString.length())
			dotPosition=tempString.length();
		else
			dotPosition+=precision+1;      

		return tempString.substring(0,dotPosition)+power;
	}
	//**************************************
	public static String timeIntervalFormat(float seconds){
		float s = seconds;
		
		String retString;
		if(s < 1){
			retString = MathUtils.truncate(seconds, 3) +" s.";			
		}else if(s < 60){
			retString = MathUtils.truncate(seconds, 1) +" s.";
		}else if(s < 3600){			
			retString = MathUtils.truncate(seconds/60, 1) +" min.";
		}else{
			retString = MathUtils.truncate(seconds/(60*60), 1) +" hours";
		}
		return retString;
	}
	//**************************************
	public static String getMemStatus()
	{
		System.gc();
		// Get current size of heap in bytes
		double totalMemory = Runtime.getRuntime().totalMemory()/1000000.0;

		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.
		// Any attempt will result in an OutOfMemoryException.
		double maxMemory = Runtime.getRuntime().maxMemory()/1000000.0;

		// Get amount of free memory within the heap in bytes. This size will increase
		// after garbage collection and decrease as new objects are created.
		double freeMemory = Runtime.getRuntime().freeMemory()/1000000.0;
		double usedMemory=maxMemory-freeMemory;

		return " - MEMORY Status - "+"free: "+GeneralUtils.formatFloat(freeMemory,2)
				+"M used: "+GeneralUtils.formatFloat(usedMemory,2)			
				+"M total: "+GeneralUtils.formatFloat(totalMemory,2)
				+"M max: "+GeneralUtils.formatFloat(maxMemory,2)+"M";
	}
	//******************************
	public static String getCurrDateTime()
	{
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		Calendar cal;
		SimpleDateFormat sdf; 
		cal = Calendar.getInstance(TimeZone.getDefault());
		sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(cal.getTime());	   
	}
	//********************************
}
