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
package dmLab.utils.dirCrawler;

import dmLab.utils.helpers.Props;

/**
 * @author mdramins
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DirCrawlerProps extends Props 
{
	public String rootPath;
	public String filter;
	public long maxFiles;
	public int maxLevel;
    //****************************	
	public DirCrawlerProps(String cfgFileName)
    {
	    super(cfgFileName);
    }
    //****************************
    public DirCrawlerProps()
    {
        super();
    }
    //****************************		
	@Override
    public boolean setDefault()
	{
		//rootPath = "\\\\k31\\Diebold\\DieboldRes\\nutech\\";
        rootPath = "D://TEMP4//";
		filter = "(.htm)|(.html)|(.txt)";
		maxFiles = 100000;
		maxLevel=4;
        return true;
	}
	//****************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("#dir crawler properties").append('\n');
		tmp.append("rootPath = "+rootPath).append('\n');
		tmp.append("filter = "+filter).append('\n');
		tmp.append("maxFiles = "+maxFiles).append('\n');
		tmp.append("maxLevel = "+maxLevel).append('\n');		
		return tmp.toString();		
	}
    //****************************
    @Override
    public boolean updateProperties()
    {
        //useStemming = Boolean.valueOf(prop.getProperty("indexer.useStemming", "true")).booleanValue();    
        rootPath=prop.getProperty("rootPath","D://TEMP4//");
        filter=prop.getProperty("filter","(.htm)|(.html)|(.txt)");
        maxFiles=Integer.valueOf(prop.getProperty("maxFiles", "100000")).intValue();
        maxLevel=Integer.valueOf(prop.getProperty("maxLevel", "4")).intValue();
        return true;
    }
    //****************************
}
