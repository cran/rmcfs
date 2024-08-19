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
package dmLab.gui.dataEditor.utils.intText;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class IntTextField extends JTextField
{
    private static final long serialVersionUID = -3136449006544571914L;
    private boolean initiated=false;
    //**********************************************
    public IntTextField()
    {
        super();
        initiated=true;
    }
    //**********************************************
    @Override
    protected Document createDefaultModel()
    {
        return new IntTextDocument();
    }
    //**********************************************
    @Override
    public boolean isValid()
    {
    	if(initiated)
    	{
	        try {        	
	            Integer.parseInt(getText());
	            return true;
	        } catch (NumberFormatException e) {
	            return false;
	        }
    	}
    	return true;
    }
    //**********************************************
    public int getValue()
    {
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    //**********************************************
}
