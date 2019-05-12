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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class IntTextDocument extends PlainDocument
{
    private static final long serialVersionUID = 5890797272696875884L;
    //****************************************
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
    {
        if (str == null)
            return;
        String oldString = getText(0, getLength());
        String newString = oldString.substring(0, offs) + str
        + oldString.substring(offs);
        try {
            Integer.parseInt(newString + "0");
            super.insertString(offs, str, a);
        } catch (NumberFormatException e) {}
    }
    //****************************************
}
