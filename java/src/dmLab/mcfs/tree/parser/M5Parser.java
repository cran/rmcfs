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
package dmLab.mcfs.tree.parser;

public class M5Parser extends TreeParser
{   
    //****************************************    
    public M5Parser(String m5String)
    {
        super(m5String);                
        beginLine = "-------";
        endLine = "LM num:";
    }
    //****************************************
	@Override
	public String lineModifier(String line) {
		int eqIdx = line.indexOf("=");
		int loIdx = line.indexOf("<= 0.5 :");
		int hiIdx = line.indexOf(">  0.5 :");
		int lhIdx = Math.max(loIdx,hiIdx);
		if(eqIdx >-1 && lhIdx >-1){
			if(eqIdx < lhIdx){
				line = line.replaceAll("<= 0.5 :", "");
				line = line.replaceAll(">  0.5 :", "");
			}
			if(hiIdx>-1)
				line = line.replaceAll("=", "!=");			
		}
		return line;
	}
    //****************************************
}
