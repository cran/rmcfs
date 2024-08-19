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
package dmLab.utils.fileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import dmLab.array.loader.fileLoader.FileType;
import dmLab.utils.FileUtils;

public class MyFileFilter extends FileFilter
{
    String ext = "";
    String comment = "";

    // ********************************
    public MyFileFilter(String extension, String comment)
    {        
        ext = extension;
        this.comment = comment;
    }
    // ********************************
    @Override
    public boolean accept(File file)
	{
        if (file.isDirectory())
            return true;

        if(ext.equalsIgnoreCase(""))//if all supported files
        {
            String extension = FileUtils.getFileExtension(file.getName());
            if (extension != null && FileType.toType(extension)!=-1)
                return true;
            else
                return false;    
        }
        else
        {
            String filename = file.getName();
            return filename.endsWith("."+ext);
        }
	}
    // ********************************
    // The description of this filter
    @Override
    public String getDescription()
    {
        if(ext.equalsIgnoreCase(""))
            return comment;
        else
            return "*." + ext + " (" + comment + ")";
    }
    // ********************************
    public String getComment()
    {
        return comment;
    }
    // **************************
    public String getExt()
    {
        return ext;
    }

    // **************************
    public boolean extEquals(String ext)
    {
        return this.ext.equalsIgnoreCase(ext);
    }
    // **************************
}
