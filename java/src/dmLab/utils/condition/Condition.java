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

public class Condition
{
	public String attributeName;
	public String value;
	public Operator operator;
//	*******************************************
	public Condition()
	{
		super();
		operator = new Operator("=");
	}
	//******************************************
	public Condition(String condition)
	{
		this();
		parse(condition);
	}
//	*******************************************
	public boolean parse(String condition)
	{
		int operatorIndex=-1;
		String operatorStr="";
		for(int i=0;i<Operator.labels.length;i++)
		{
			int index=condition.indexOf(Operator.labels[i]);			
			if(index!=-1 && operatorStr.length()<Operator.labels[i].length())
			{
				operatorStr=condition.substring(index, index+Operator.labels[i].length()).trim();
				operatorIndex=index;
			}
		}
		
		if(operatorIndex==-1)//there was not any operator in condition
			return false;
		
		operator.parse(operatorStr);		
		attributeName = condition.substring(0,operatorIndex).trim();
		value = condition.substring(operatorIndex+operatorStr.length(), condition.length()).trim();
		return true;
	}
//*******************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append(attributeName).append(operator.toString()).append(value);
		return tmp.toString();
	}	
//*******************************************
	public static void test()
	{		
		Condition c = new Condition();
		c.parse("a101=19");
		System.out.println(c);
		c.parse("a101!=19");
		System.out.println(c);
		c.parse("hjgjhg >= 19");
		System.out.println(c);
		c.parse("hjhgh < jkkgjhg");
		System.out.println(c);
	}
//	*******************************************
}
