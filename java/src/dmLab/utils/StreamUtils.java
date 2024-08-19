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
package dmLab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	//****************************************
	//convertStream - converts input stream to byte array.	 
	public static byte[] convert2bytes(InputStream OInput) throws IOException
	{
		try {
			int n = 0;
			int nContentLength = 0;
			int nBlkSize = 1024;
			byte[] cContent = new byte[0];
			byte[] cTemp = new byte[nBlkSize];

			while ((n = OInput.read(cTemp, 0, nBlkSize)) > 0)
			{
				byte[] cBuf = new byte[n + nContentLength];

				System.arraycopy(cContent, 0, cBuf, 0, nContentLength);
				System.arraycopy(cTemp, 0, cBuf, nContentLength, n);
				cContent = cBuf;
				nContentLength += n;
			}
			OInput.close();
			return cContent;
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	//****************************************
	//convertStream - converts input stream to byte array.
	public static byte[] convert2bytes(File file) throws IOException
	{
		byte doc[]=new byte[(int) file.length()];
		FileInputStream fileIS=new FileInputStream(file);
		fileIS.read(doc);
		fileIS.close();       
		return doc;
	}
	//****************************************
}
