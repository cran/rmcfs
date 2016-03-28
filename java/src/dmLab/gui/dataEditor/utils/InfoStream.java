/*******************************************************************************
 * #-------------------------------------------------------------------------------
 * # Copyright (c) 2003-2016 IPI PAN.
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
 * # If you want to use dmLab or MCFS/MCFS-ID, please cite the following paper:
 * # M.Draminski, A.Rada-Iglesias, S.Enroth, C.Wadelius, J. Koronacki, J.Komorowski 
 * # "Monte Carlo feature selection for supervised classification", 
 * # BIOINFORMATICS 24(1): 110-117 (2008)
 * #-------------------------------------------------------------------------------
 *******************************************************************************/
package dmLab.gui.dataEditor.utils;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class InfoStream extends OutputStream 
{
    public static int OUT=1;
    public static int ERR=2;
    private int output;
    private Document doc;
    private SimpleAttributeSet attributes;
    
//  *************************************    
    public InfoStream()
    {
        attributes = new SimpleAttributeSet();
        output=OUT;
    }
//  *************************************
    @Override
    public void write(int c) throws IOException
    {        
        if(output==OUT)
            StyleConstants.setForeground(attributes, Color.black);
        else if (output==ERR)
            StyleConstants.setForeground(attributes, Color.red);
        else //in any case
            StyleConstants.setForeground(attributes, Color.blue);
        
        try {
            doc.insertString(doc.getLength(),String.valueOf((char)c), attributes);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
//*************************************
    public void setOutput(JTextPane panel,int output)
    {
        doc=panel.getStyledDocument();
        this.output=output;
    }
//  *************************************    
}
