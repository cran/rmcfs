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
package dmLab.utils.condition;

public class Operator implements Cloneable 
{	
	public static short EQUAL	= 0;	//'='
	public static short GREATER	= 1;	//'>'
	public static short LESS	= 2;	//'<'
	public static short GEQ		= 3;	//'>='
	public static short LEQ		= 4;	//'<='
	public static short NOTEQUAL= 5;	//'!='
	
	public static short operators[]={EQUAL,GREATER,LESS,GEQ,LEQ,NOTEQUAL};
	public static String labels[]={"=",">","<",">=","<=","!="};
	
	public short op;
	//**************************************
	public Operator()
	{
		op=EQUAL;
	}
	//**************************************
	public Operator(String operator)
	{
		parse(operator);
	}
	//**************************************
	protected Operator(short operator)
	{
		op=operator;
	}
	//**************************************
	protected boolean is(short operator){
		if(op == operator)
			return true;
		else
			return false;			
	}
	//**************************************
	@Override
    public Operator clone()
	{
		return new Operator(op);
	}
	//**************************************
	@Override
    public String toString()
	{			
		return labels[op];
	}
	//**************************************
	public boolean parse(String operator)
	{
		for(int i=0;i<labels.length;i++){			
			if(operator.equalsIgnoreCase(labels[i])){
				op=operators[i];
				return true;
			}
		}
		System.err.println("Unknown operator : '"+operator+"'");
		return false;
	}
	//**************************************
	public boolean compare(String left, String right)
	{
		if(op==EQUAL && left.equalsIgnoreCase(right))
			return true;
		else if(op==NOTEQUAL && !left.equalsIgnoreCase(right))
			return true;
		else
			return false;		
	}
	//**************************************
	public boolean compare(float left, float right)
	{
		// == NaN NaN
		if(op==EQUAL && Float.isNaN(left) && Float.isNaN(right))
			return true;
		// != > < NaN NaN
		else if(Float.isNaN(left) && Float.isNaN(right))
			return false;
		// == 3 NaN
		else if(op==EQUAL && (Float.isNaN(left) || Float.isNaN(right)))
			return false;
		//  != > < 3 NaN
		else if(Float.isNaN(left) || Float.isNaN(right))
			return true;
		else if(op==EQUAL && left == right)
			return true;
		else if(op==GREATER && left > right)//'>'
			return true;
		else if(op==LESS && left < right)//'<'
			return true;
		else if(op==GEQ && left >= right)//'>='
			return true;
		else if(op==LEQ && left <= right)//'<='
			return true;
		else if(op==NOTEQUAL && left != right)//!=
			return true;
		else
			return false;		
	}
	//**************************************
	public boolean isNumericalOnly()
	{
		if(op==GREATER || op==LESS || op==GEQ || op==LEQ)
			return true;
		else
			return false;
	}
//	**************************************
}
