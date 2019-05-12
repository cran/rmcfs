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
package dmLab.gui.graphViewer;

public class GraphViewerInfo {
//  *************************************************
    public GraphViewerInfo()
    {
    } 
//  *************************************************
    @Override
    public String toString()
    {    
        StringBuffer tmp=new StringBuffer();
        tmp.append("*******************************").append('\n');
        tmp.append("*****  dmLab Graph Viewer  ****").append('\n');
        tmp.append("*****     version 2.0      ****").append('\n');
        tmp.append("*****      2015.03.12      ****").append('\n');
        tmp.append("*******************************").append('\n');
        tmp.append("*******************************").append('\n');
		tmp.append("Created by Michal Draminski [mdramins@ipipan.waw.pl]").append('\n');
		tmp.append("http://www.ipipan.eu/staff/m.draminski/").append('\n');
		tmp.append("Polish Academy of Sciences - Institute of Computer Science").append('\n');
		tmp.append("Department of Artificial Intelligence").append('\n');
		tmp.append("*******************************").append('\n');
		tmp.append("********************************").append('\n');        
        return tmp.toString();
    }
//  *************************************************
}
