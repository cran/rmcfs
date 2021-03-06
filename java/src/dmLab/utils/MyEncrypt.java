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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyEncrypt {

    //******************
    public static String encodePassword(String password)
    {
        if (password != null)
        {
          MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
          digest.update(password.getBytes());          
          byte bytes[] = digest.digest();
          StringBuffer buffer = new StringBuffer();
          for (int i = 0; i < bytes.length; i++)
          {
            int b = bytes[i] & 0xff;
            if (b < 16) {
              buffer.append("0");
            }
            buffer.append(Integer.toHexString(b));
          }
     
          password = buffer.toString();
        }
        return password;
      }
    //******************
}
