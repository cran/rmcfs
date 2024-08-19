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

import dmLab.utils.FileUtils;

public class ComplexFileFilter extends FileFilter
{
    String extensions[] = null;
    String comment = "";

    // ********************************
    public ComplexFileFilter(String extension, String comment)
    {                
        extensions = new String[]{extension};
        this.comment = comment;
    }
    // ********************************
    public ComplexFileFilter(String extensions[], String comment)
    {        
        this.extensions = extensions;
        this.comment = comment;
    }
    // ********************************
    public boolean accept(String fileExtension)
    {
        for(int i=0;i<extensions.length;i++)
            if(extensions[i].equalsIgnoreCase(fileExtension))
                return true;
        
        return false;
    }
    // ********************************
    @Override
    public boolean accept(File file)
    {
        if (file.isDirectory())
            return true;

        String fileExtension = FileUtils.getFileExtension(file.getName());

        return accept(fileExtension);
    }
    // ********************************
    // The description of this filter
    @Override
    public String getDescription()
    {
        String description=comment;
        if(extensions!=null)
        {
            description+=" (";
            for(int i=0;i<extensions.length;i++)
            {
                description+="*."+extensions[i];
                if(i<extensions.length-1)
                    description+=" ";
            }       

            description+=")";    
        }
        return description;
    }
    // ********************************
    public String getComment()
    {
        return comment;
    }
    // **************************
}
