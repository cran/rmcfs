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

import dmLab.array.meta.AttributesMetaInfo;
import dmLab.array.meta.Dictionary;

public class Ruleset {
	protected ArrayList<Rule> rules;
	protected AttributesMetaInfo attributes;
	protected Dictionary valDict;
	
	//********************************	
	public Ruleset(AttributesMetaInfo attributes, Dictionary valDict){
		rules = new ArrayList<Rule>();		
		this.attributes = attributes;
		this.valDict = valDict;
		this.valDict.locked = false;
	}
	//********************************
	public String toString(){
        StringBuffer tmp = new StringBuffer();
        for(int i=0; i<rules.size(); i++){
        	Rule r = rules.get(i);
        	tmp.append(r.toString());
        	tmp.append(",").append(r.decisionClass);
        	tmp.append(",").append(r.toStringIndicators());
        	tmp.append("\n");
        }
        return tmp.toString();
	}
	//********************************
	public void add(Rule rule){
		rules.add(rule);
	}
	//********************************
}
