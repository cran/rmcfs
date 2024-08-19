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
package dmLab.classifier;

public class Prediction {

	protected String real;
	protected String predicted;
	protected double scores[];

	protected float realValue;
	protected float predictedValue;

	//****************************************
	public Prediction(String real, String predicted, double scores[])
	{
		this.real = real;
		this.predicted = predicted;
		this.scores = scores;
	}
	//****************************************
	public Prediction(float realValue, float predictedValue)
	{
		this.realValue = realValue;
		this.predictedValue = predictedValue;

		this.real = null;
		this.predicted = null;
		this.scores = null;
	}
	//****************************************
	public float getRealValue()
	{
		return realValue;
	}
	//****************************************
	public float getPredictedValue()
	{
		return predictedValue;
	}
	//****************************************
	public String getReal()
	{
		return real;
	}
	//****************************************
	public String getPredicted()
	{
		return predicted;
	}
	//  ****************************************    
	public double[] getScores()
	{
		return scores;
	}
	//  ****************************************    
	@Override
	public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		if(predicted!=null)
			tmp.append(predicted);
		else        	
			tmp.append(predictedValue);

		if(scores!=null)            
			for(int i=0;i<scores.length;i++)
				tmp.append(',').append(scores[i]);

		return tmp.toString();
	}
	//  ****************************************    
	public boolean isNumeric()
	{
		if(predicted==null)
			return true;
		else
			return false;
	}
	//  ****************************************
}
