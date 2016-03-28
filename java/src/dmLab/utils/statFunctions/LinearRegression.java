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
package dmLab.utils.statFunctions;

import dmLab.utils.ArrayUtils;

/*************************************************************************
 *  Compilation:  javac LinearRegression.java StdIn.java
 *  Execution:    java LinearRegression < data.txt
 *  
 *  Reads in a sequence of pairs of real numbers and computes the
 *  best fit (least squares) line y  = ax + b through the set of points.
 *  Also computes the correlation coefficient and the standard errror
 *  of the regression coefficients.
 *
 *  Note: the two-pass formula is preferred for stability.
 *
 *************************************************************************/

public class LinearRegression { 

	//result
	private double beta1;
	private double beta0;	
    private double R2;
    private double svar;
    private double svar1;
    private double svar0;
    private double rss,ssr;
    double xxbar, yybar;
    		
    //internal
    private double sumx2;
    private int n;
	
	//********************************************
	//returns beta1
    public double calc(double[] x, double[] y)
    {        
    	// first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0;
        sumx2 = 0.0;
        n=x.length;
        
        for(int i=0;i<x.length;i++)
        {
            sumx  += x[i];
            sumx2 += x[i] * x[i];
            sumy  += y[i];
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        yybar = 0.0;         
        xxbar = 0.0;
        double xybar = 0.0;
        
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        beta1 = xybar / xxbar;
        beta0 = ybar - beta1 * xbar;


        // analyze results
        int df = n - 2;
        rss = 0.0;      // residual sum of squares
        ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        
        R2    = ssr / yybar;
        svar  = rss / df;
        svar1 = svar / xxbar;
        svar0 = svar/n + xbar*xbar*svar1;

        return beta1;
    }
    //*************************************************************
    public double calc(float[] x, float[] y)
    {        
        double[] xTmp = ArrayUtils.float2double(x);
        double[] yTmp = ArrayUtils.float2double(y);
    	
        return calc(xTmp, yTmp);        
    }    
    //**********************************
    public String toString()
    {
    	StringBuffer tmp=new StringBuffer();    	
        tmp.append("y   = " + beta1 + " * x + " + beta0).append("\n");    	
        tmp.append("R^2                 = " + R2).append("\n");
        tmp.append("std error of beta_1 = " + Math.sqrt(svar1)).append("\n");
        tmp.append("std error of beta_0 = " + Math.sqrt(svar0)).append("\n");
        svar0 = svar * sumx2 / (n * xxbar);
        tmp.append("std error of beta_0 = " + Math.sqrt(svar0)).append("\n");

        tmp.append("SSTO = " + yybar).append("\n");
        tmp.append("SSE  = " + rss).append("\n");
        tmp.append("SSR  = " + ssr).append("\n");
        return tmp.toString();
    }
    //**********************************
    public double getBeta1()
    {
    	return beta1;
    }
    //**********************************
    public double getBeta0()
    {
    	return beta0;
    }
    //**********************************
    public double getR2()
    {
    	return R2;
    }
    //**********************************
    public double getStdErrorBeta1()
    {
    	return svar1;
    }
    //**********************************
    public double getStdErrorBeta0()
    {
    	return svar0;
    }
    //**********************************
    public double getSSTO()
    {
    	return yybar;
    }
    //**********************************
    public double getSSE()
    {
    	return rss;
    }
    //**********************************
    public double getSSR()
    {
    	return ssr;
    }
    //**********************************    
    /*
    public static void main(String[] args) 
    { 
    	double[] x = {0,1,2,3,4,5,6,7,8,9};
    	double[] y = {3,4,6,3,4,5,6,4,3,6};
    	MyLinearRegression lr = new MyLinearRegression();
    	lr.calc(x, y);
    	System.out.println(lr.toString());
    	
    	System.out.println(lr.getBeta1());
    	
    }
    */
    //**********************************    

}
