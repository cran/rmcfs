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
import java.io.*;


/**
 *
 * Class for filtering filename extensions wrt specified regular expression
 * 
 * @author Krzysztof Ciesielski
 */
public class FileExtFilter implements FilenameFilter {
	
	/** regular expression */
	protected String regex;
  
  
	/**
	 * @param regex regular expression for filtering filenames
	 */
	public FileExtFilter(String regex) {
  	 
		this.regex = regex.toLowerCase();
	}
  
	/** 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		
		//String f = new File(name).getName();
		//return f.indexOf(this.filter) != -1;
		
		//do not filter directories
		if ((new File(dir.getAbsolutePath()+"/"+name).isDirectory())) return true;
		
		int extIdx = name.lastIndexOf('.');
		
		//accept filenames without extension
		if (extIdx < 0) return true;
		
		return name.substring(extIdx).toLowerCase().matches(this.regex);
	}
}
