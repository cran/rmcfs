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

import java.util.ArrayList;

import dmLab.array.meta.Attribute;
import dmLab.array.meta.AttributesMetaInfo;
import dmLab.array.meta.Dictionary;

public class Rule implements Cloneable{
	
	protected ArrayList<Selector> selectors;
	protected AttributesMetaInfo attributes;
	protected Dictionary valDict;
	public String decisionClass;
	
	public float classProb;
	public float cov;
	public float quality;
	public float weight;

	//********************************
	public Rule(Ruleset ruleset){
		selectors = new ArrayList<Selector>();
		this.attributes = ruleset.attributes;
		this.valDict = ruleset.valDict;
		this.valDict.locked = false;
	}
	//********************************
	public Rule clone(){
		Rule rule = new Rule(attributes, valDict);
		rule.selectors = new ArrayList<Selector>();
		for(int i=0; i<selectors.size(); i++)
			rule.selectors.add(selectors.get(i).clone());
		rule.classProb = classProb;
		rule.cov = cov;
		rule.quality = quality;
		rule.weight = weight;
		return rule;
	}
	//********************************
	public Rule(AttributesMetaInfo attributes, Dictionary valDict){
		selectors = new ArrayList<Selector>();		
		this.attributes = attributes;
		this.valDict = valDict;
		this.valDict.locked = false;
	}
	//********************************	
	public void addCondition(Condition condition){
		int attrIndex = attributes.getIndex(condition.attributeName);
		Attribute attr = attributes.getAttribute(condition.attributeName);
		
		Selector s;
		if (attr.type == Attribute.NOMINAL)
			s = new SelectorNominal(attributes, valDict, condition);
		else
			s = new SelectorNumeric(attributes, valDict, condition);
			
		boolean merged = false;
		for(int i=0; i<selectors.size();i++){
			if(selectors.get(i).attrIndex == attrIndex){
				selectors.get(i).merge(s);
				merged = true;
				break;
			}
		}
		if(!merged){
			selectors.add(s);
		}		
	}
	//********************************
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("IF ");
		for(int i=0; i<selectors.size(); i++){
			if(i>0)
				buff.append(" AND ");
			buff.append(selectors.get(i).toString(attributes, valDict));
		}
		buff.append(" THEN class is ").append(decisionClass);				
		return buff.toString();
	}
	//********************************
	public String toStringIndicators(){		
		StringBuffer buff = new StringBuffer();		
		buff.append(String.format("%.3f", classProb)).append(", ");
		buff.append(String.format("%.3f", cov)).append(", ");
		buff.append(String.format("%.3f", quality)).append(", ");
		buff.append(String.format("%.3f", weight));		
		return buff.toString();
	}
	//********************************
	public static void main(String[] args)
	{
		Attribute[] attributes = new Attribute[3];
		attributes[0] = new Attribute("wzrost",Attribute.NOMINAL); 
		attributes[1] = new Attribute("waga",Attribute.NUMERIC); 
		attributes[2] = new Attribute("plec",Attribute.NOMINAL); 
		AttributesMetaInfo attrs = new AttributesMetaInfo(attributes);
		Dictionary valDict = new Dictionary();
		
		Rule r = new Rule(attrs, valDict);
		r.addCondition(new Condition("wzrost = wysoki"));
		r.addCondition(new Condition("plec = m"));
		r.addCondition(new Condition("waga > 80"));
		r.addCondition(new Condition("waga <= 100"));
		r.addCondition(new Condition("wzrost = sredni"));		
		r.decisionClass = "normalny";
		
		System.out.println(r.toString());		
	}
	//********************************

}
