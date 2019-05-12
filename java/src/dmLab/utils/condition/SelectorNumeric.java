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

import dmLab.array.meta.AttributesMetaInfo;
import dmLab.array.meta.Dictionary;

public class SelectorNumeric extends Selector implements Cloneable{

	protected float[] values;

	//********************************
	public SelectorNumeric()
	{
		super();
		values = new float[]{Float.MIN_VALUE, Float.MAX_VALUE};
	}
	//********************************
	public SelectorNumeric(AttributesMetaInfo attributes, Dictionary valDict, Condition condition)
	{
		this();
		attrIndex = attributes.getIndex(condition.attributeName);
		float val = Float.parseFloat(condition.value);
		if(condition.operator.is(Operator.GREATER)){
			values[0] = val;
		}else if(condition.operator.is(Operator.LEQ)){
			values[1] = val;
		}else if(condition.operator.is(Operator.EQUAL)){
			values[0] = values[1] = val;
		}else{
			System.err.println("Operator: " + condition.operator.toString()+ " is not supported by Selector class!");			
		}
	}
	//********************************	
	public boolean covers(float value){
		
		if(value > values[0] && value <= values[1])
			return true;
		else
			return false;
	}
	//	*************************************
	public boolean merge(Selector s){
		if(attrIndex != s.attrIndex){
			System.err.println("Cannot merge selectors - attrIndex do not match!");
			return false;
		}
		values[0] = Math.max(values[0], ((SelectorNumeric)s).values[0]);
		values[1] = Math.min(values[1], ((SelectorNumeric)s).values[1]);
				
		return true;
	}
	//	*************************************
	public String toString(AttributesMetaInfo attributes, Dictionary valDict){
		StringBuffer buff = new StringBuffer();		  
		buff.append((attributes.getAttribute(attrIndex)).name);
		if(values[0] == values[1]){
			buff.append(" = ").append(values[0]);
		}else if(values[0] != Float.MIN_VALUE && values[1] != Float.MAX_VALUE){
			buff.append(" = (").append(values[0]).append("; ").append(values[1]).append("]");		
		}else if(values[0] != Float.MIN_VALUE){
			buff.append(" > ").append(values[0]);
		}else if(values[1] != Float.MAX_VALUE){
			buff.append(" <= ").append(values[1]);			
		}else{
			System.err.println("Cannot print selector!");			
		}		
		return buff.toString();
	}
	//	*************************************
	@Override
	public Selector clone() {
		SelectorNumeric selector = new SelectorNumeric();
		selector.attrIndex = attrIndex;
		selector.values = values.clone();
		return selector;
	}
	//	*************************************
}
