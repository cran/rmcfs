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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class StringUtils {
	//  ************************************************
	//TODO replace by tokenize?
	public static String[] tokenizeString(String s, char[] separators, boolean mergeSeparators)
	{
		if(s==null || s.length()==0)
			return null;
		
		ArrayList<String> list=new ArrayList<String>();
		int start=0,stop=0;
		String value;        
		int lineSize=s.length();
		
		while(start < lineSize)
		{
			stop=minIndexOf(s,start,separators);
			if(stop==-1)
				stop=s.length();
			value=s.substring(start,stop).trim();

			if(value.length()!=0)
				list.add(value);
			else if(!mergeSeparators)
				list.add("");

			start=stop+1;
		}
		String[] array = new String[1];
		array = list.toArray(array);
		return array;
	}
	//********************************
	//helper for tokenizeString
	private static int minIndexOf(String s, int start, char[] chars)
	{
		int minIndex=s.length()+1;
		for(int i=0;i<chars.length;i++){
			int currIndex = s.indexOf(chars[i], start);
			if(currIndex<minIndex && currIndex!=-1)
				minIndex = currIndex;
		}
		if(minIndex==s.length()+1)
			return -1;
		else
			return minIndex;       
	}
	//  ************************************************
	public static String[] tokenize(String s, char[] separators, char[] quoteChar)
	{
		ArrayList<String> tokens = new ArrayList<String>();
		
		StringReader reader = new StringReader(s);
		StreamTokenizer st = new StreamTokenizer(reader);
		st.resetSyntax();
		st.wordChars(32, 254);
		
		for(int i=0;i<separators.length;i++){
			st.whitespaceChars(separators[i], separators[i]);
		}
		for(int i=0;i<quoteChar.length;i++){
			st.quoteChar(quoteChar[i]);
		}

		boolean eof = false;
		do {
			int token = StreamTokenizer.TT_EOF;
			try {
				token = st.nextToken();
			} catch (IOException e) {
				e.printStackTrace();
			}

			switch (token) {
			case StreamTokenizer.TT_EOF:
				eof = true;
				break;
			case StreamTokenizer.TT_EOL:
				break;
			case StreamTokenizer.TT_WORD:
				tokens.add(st.sval.trim());
				break;
			case StreamTokenizer.TT_NUMBER:
				tokens.add(Double.toString(st.nval).trim());
				break;
			default:
				tokens.add(st.sval);			
				if (token == '!') {
					eof = true;
				}
			}

		} while (!eof);
		String[] tokensArray = new String[1];
		return tokens.toArray(tokensArray);	
	}
	//  ************************************************
	public static String[] tokenizeArray(String stringArray){
		if(stringArray.trim().length() == 0)
			return null;
		char delims[] = new char[] {',',';'};
		char quoteChar[] = new char[] {'\"','\''};		
		String[] tokens = StringUtils.tokenize(stringArray, delims, quoteChar);
		String[] output = StringUtils.removeItems(tokens,new String[] {"[","]",""});
		int first = 0;
		int last = output.length - 1;
		output[first] = output[first].trim();
		output[last] = output[last].trim();

		if(output[first].startsWith("["))
			output[first] = output[first].substring(1, output[first].length()).trim();

		if(output[last].endsWith("]"))
			output[last] = output[last].substring(0, output[last].length()-1).trim();

		return output;
	}
	//********************************
	public static boolean charEquals(char c, char[] chars){
		for(int i=0;i < chars.length; i++)
			if(chars[i]==c)
				return true;
		return false;
	}
	//********************************
	public static String trimQuotation(String s)
	{
		String result;
		result = s.replaceAll("^\"+|\"+$", "");
		result = result.replaceAll("^[\"']+|[\"']+$", "");		
		return result;
	}
	//********************************
	//removes chars from begining and end of string s
	public static String trimChars(String s, char[] chars)
	{
		StringBuffer tmp=new StringBuffer(s.trim());
		if(charEquals(tmp.charAt(0),chars))
			tmp.setCharAt(0,' ');

		final int index= tmp.length()-1;
		if(charEquals(tmp.charAt(index),chars))
			tmp.setCharAt(index,' ');

		return tmp.toString().trim();
	}
	//********************************
	public static String replaceAll(String s, String source, String destination)
	{
		StringBuffer tmp=new StringBuffer(s);
		int start=tmp.indexOf(source);

		while(start!=-1){
			tmp.replace(start, start+source.length(), destination);
			start=tmp.indexOf(source,start+destination.length());
		}

		return tmp.toString(); 
	}
	//*******************************************
	public static String[][] transpose (String [][] ss)
	{
		String destination[][]=new String[ss[0].length][ss.length];
		for(int i=0;i<ss.length;i++)
			for(int j=0;j<ss[0].length;j++)
				destination[j][i]=ss[i][j];

		return destination;
	}
	//********************************
	public static char[] toCharArray(String s, int size)
	{
		char[] charArray = new char[size];
		Arrays.fill(charArray,' ');
		int stop=s.length();
		if(stop>size) 
			stop=size;       
		s.getChars(0, stop, charArray, 0);
		return charArray;
	}
	//********************************
	public static boolean equalsTo(String string1, String string2)
	{
		if(string1.length()!=string2.length())
			return false;

		char str1[]=string1.toCharArray();
		char str2[]=string2.toCharArray();
		for(int i=0;i<str1.length;i++)
			if(str1[i]!=str2[i])
				return false;

		return true;
	}
	//*******************************
	public static boolean equalsToAny(String inputString, String[] items)
	{
	    for(int i=0; i < items.length; i++){
	        if(inputString.equalsIgnoreCase(items[i])){
	            return true;
	        }
	    }
	    return false;
	}
	//********************************
	public static boolean allin(String[] s1, String s2[], boolean ignoreCase, boolean trim){		
		if(trim){
			for(int i=0; i<s1.length; i++){
				s1[i] = StringUtils.trimChars(s1[i], new char[]{'"','\''}).trim();
				if(ignoreCase)
					s1[i] = s1[i].toLowerCase();	
			}
			for(int j=0; j<s2.length; j++){
				s2[j] = StringUtils.trimChars(s2[j], new char[]{'"','\''}).trim();
				if(ignoreCase)
					s2[j] = s2[j].toLowerCase();
			}
		}

		for(int i=0; i<s1.length; i++){
			boolean current = false;
			for(int j=0; j<s2.length; j++){
				if(s1[i].compareTo(s2[j])==0){
					current = true;
					break;
				}
			}
			if(current == false)
				return false;
		}
		return true;			
	}
	//********************************
	public static boolean matchesPattern(String s, String pattern)
	{
		String myPattern = pattern.trim().toLowerCase();
		String myString = s.trim().toLowerCase();
		
		if(myPattern==null || myPattern.length()==0 || myPattern.equalsIgnoreCase("*"))
			return true;
		
		int starIndex = myPattern.indexOf('*');
		if(starIndex == -1)
			return myString.equalsIgnoreCase(myPattern);
		else{
			String prefix = myPattern.substring(0,starIndex);
			starIndex = myPattern.lastIndexOf('*');
			String suffix = myPattern.substring(starIndex+1, myPattern.length());
						
			if(prefix.length() > 0 && suffix.length() > 0)
				return myString.startsWith(prefix) && myString.endsWith(suffix);
			else if(prefix.length()>0)
				return myString.startsWith(prefix);
			else if(suffix.length()>0)
				return myString.endsWith(suffix);
			else
				return true;			
		}				
	}
	//*******************************
	public static boolean matchesPatterns(String s, String[] patterns)
	{
		if(patterns==null || patterns.length==0)
			return false;
		
		final int size = patterns.length;
		
		for(int i=0; i<size; i++){			 
			if(matchesPattern(s, patterns[i]))
				return true;					
		}
		return false;		
	}
	//*******************************
	public static String[] removeItems(String[] input, String[] items){
		HashSet<String> itemsSet = new HashSet<String>();
		for(int i=0;i<items.length;i++)
			itemsSet.add(items[i]);
				
		ArrayList<String> outputList = new ArrayList<String>();		
		for(int i=0;i<input.length;i++){
			if(!itemsSet.contains(input[i])){
				outputList.add(input[i]);
			}
		}
		String [] outputArray = new String[1];
		outputArray = outputList.toArray(outputArray);
		return outputArray;
	}
	//*******************************
	public static String charRepeat(char c, int repeat){
		char[] tmp = new char[repeat];
		Arrays.fill(tmp, c);		  
		String tmpString = new String(tmp);
		return tmpString;
	}
	//*******************************
	public static Float myParseFloat(String value) throws NumberFormatException{
		if(value.equals("?")) {
			return Float.NaN;
		}else {
			return Float.parseFloat(value);
		}		
	}
	//*******************************
	public static String getRandomString(int length) {
	    final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	    StringBuffer randStr = new StringBuffer();
	    for(int i=0; i<length; i++){
	        int number = (int)Math.floor(chars.length() * Math.random());	        
	        char ch = chars.charAt(number);
	        randStr.append(ch);
	    }
	    return randStr.toString();		
	}
	// ***********************************    	

}
