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
package dmLab.experiment.classification;

import java.util.Random;

import dmLab.DMLabInfo;

public class Classification
{
    //*************************************************
    //*** main method
    public static void main(String[] args)
    {
    	DMLabInfo dmLabInfo = new DMLabInfo();		
		System.out.println(dmLabInfo.toString());

    	if(args.length==0){
            System.err.println("Missing argument - name of classification parameters file! (*.run)");
            return;
        }else{
	        ClassificationBody body=new ClassificationBody(new Random(System.currentTimeMillis()));
	        if(body.loadParameters(args[0]))
	        	body.run();
        }
    }
    //*************************************************
}
