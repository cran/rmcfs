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
package dmLab.gui.dataEditor.components;

import java.awt.MediaTracker;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class GlyphButton extends JButton
{

	private static final long serialVersionUID = 7533220037274351463L;

	//*********************************
	public GlyphButton(String glyphPath,String label)	
	{
        super(new ImageIcon(glyphPath));
		ImageIcon icon=new ImageIcon(glyphPath);
        if(icon.getImageLoadStatus()==MediaTracker.ERRORED)
            this.setText(label);                

	}
	

}
