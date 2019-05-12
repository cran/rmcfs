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

import java.util.HashSet;

import dmLab.array.meta.AttributesMetaInfo;
import dmLab.array.meta.Dictionary;
import dmLab.utils.StringUtils;

public class SelectorNominal extends Selector implements Cloneable{

	protected HashSet<Float> values;

	//********************************
	public SelectorNominal()
	{
		super();
		values = new HashSet<Float>();
	}
	//********************************
	public SelectorNominal(AttributesMetaInfo attributes, Dictionary valDict, Condition condition)
	{
		this();
		attrIndex = attributes.getIndex(condition.attributeName);
		String[] str_values = StringUtils.tokenizeArray(condition.value);
		for(int i=0; i < str_values.length; i++){
			values.add(valDict.toFloat(str_values[i]));			
		}
	}
	//********************************	
	public boolean covers(float value){
		
		if(values.contains(value))
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
		SelectorNominal snom = (SelectorNominal)s;
		Float[] fvalues = new Float[1]; 
		fvalues = snom.values.toArray(fvalues);		
		for(int i=0; i<fvalues.length; i++){
			if(!values.contains(fvalues[i]))
				values.add(fvalues[i]);
		}
				
		return true;
	}
	//	*************************************
	public String toString(AttributesMetaInfo attributes, Dictionary valDict){
		StringBuffer buff = new StringBuffer();
		StringBuffer val_buff = new StringBuffer();
		
		buff.append((attributes.getAttribute(attrIndex)).name).append(" = ");		
		Float[] fvalues = new Float[1]; 		
		fvalues = values.toArray(fvalues);		
		for(int i=0; i<fvalues.length; i++){
			if(i>0)
				val_buff.append("; ");
			val_buff.append(valDict.toString(fvalues[i]));
		}
		if(fvalues.length>1)			
			buff.append("[").append(val_buff).append("]");
		else
			buff.append(val_buff);
		
		return buff.toString();
	}
	//	*************************************
	@Override
	public Selector clone() {
		SelectorNominal selector = new SelectorNominal();
		selector.attrIndex = attrIndex;
		selector.values.addAll(values);
		return selector;
	}
	//	*************************************
}
