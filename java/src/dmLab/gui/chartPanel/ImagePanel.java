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
package dmLab.gui.chartPanel;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	BufferedImage image;
//  ******************************
	public ImagePanel(LayoutManager arg0, boolean arg1)
	{
		super(arg0, arg1);
	}
//  ******************************
	public ImagePanel(LayoutManager arg0)
	{
		super(arg0);
	}
//  ******************************
	public ImagePanel(boolean arg0)
	{
		super(arg0);
	}
//  ******************************
	public ImagePanel()
	{
		super();
	}
//  ******************************    
	@Override
    public void paint(Graphics g)
	{      
		if(image!=null)			
			g.drawImage(image,0,0,this);
	}
//  ******************************
	public BufferedImage getImage()
    {
		return image;
	}
//******************************
	public void setImage(BufferedImage image)
    {
		this.image = image;
	}
//  ******************************
}
