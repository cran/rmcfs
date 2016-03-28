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
package dmLab.mcfs.cutoffMethods;

import java.util.Arrays;

import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.measuresRI.Importance;
import dmLab.utils.MathUtils;
import dmLab.utils.dataframe.DataFrame;
import dmLab.utils.dataframe.Column;
import dmLab.utils.ArrayUtils;

public class Cutoff {
	
	protected CutoffMethod[] cutoff;
	protected DataFrame cutoffTable;

	protected MCFSParams mcfsParams;
	//****************************************
	public Cutoff(MCFSParams mcfsParams){
		
    	cutoffTable = new DataFrame(3,4);
    	cutoffTable.setColNames(new String[]{"method", "minRI", "size", "minID"});
    	cutoffTable.setColTypes(new short[]{Column.TYPE_NOMINAL,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC});
		
		this.mcfsParams=mcfsParams;		
	}
	//****************************************
	public double calcCutoff(AttributesRI attrRI){
				
		cutoff = new CutoffMethod[3];		
		cutoff[0] = new CriticalAngleCutoff(mcfsParams);
		cutoff[1] = new KMeansCutoff(mcfsParams);
		cutoff[2] = new ContrastAttributesCutoff(mcfsParams);
		
		Importance[] importance = attrRI.getImportances(attrRI.mainMeasureIdx);
		Arrays.sort(importance);

		double[] minRI = new double[3];
		double[] attrNumber = new double[3];
		for(int i=0;i<cutoff.length;i++){
			minRI[i] = cutoff[i].getCutoff(importance);
			attrNumber[i] = getImportantAttrNumber(importance,minRI[i]);

			cutoffTable.set(i, cutoffTable.getColIdx("method"), cutoff[i].name);						
			cutoffTable.set(i, cutoffTable.getColIdx("minRI"), minRI[i]);
			cutoffTable.set(i, cutoffTable.getColIdx("size"), attrNumber[i]);			
		}
		double mean_minRI = addMeanValue(attrRI);
		return mean_minRI;
	}
	//****************************************
	public double addMeanValue(AttributesRI attrRI){
		
		Importance[] importance = attrRI.getImportances(attrRI.mainMeasureIdx);
		Arrays.sort(importance);		
		double[] attrNumber = ArrayUtils.Double2double(cutoffTable.getColumn(cutoffTable.getColIdx("size")));
				
		double mean_attrNumber = Math.round(MathUtils.mean(attrNumber));
		double mean_minRI = importance[(int)mean_attrNumber].importance;
		
		DataFrame meanRow = new DataFrame(1, cutoffTable);
		meanRow.set(0, meanRow.getColIdx("method"), "mean");
		meanRow.set(0, meanRow.getColIdx("minRI"), mean_minRI);
		meanRow.set(0, meanRow.getColIdx("size"), mean_attrNumber);
		cutoffTable.rbind(meanRow);
		
		return mean_minRI;
	}
	//****************************************
	private double getImportantAttrNumber(Importance[] importance, double minRI){
		for(int i=0;i<importance.length;i++){
			if(importance[i].importance < minRI)
				return i;
		}
		return Double.NaN;
	}
	//****************************************
	public DataFrame getCutoffTable()
	{
		return cutoffTable;
	}
	//****************************************
	public void setCutoffTable(DataFrame cutoffTable)
	{
		this.cutoffTable = cutoffTable;
	}
	//****************************************
	public String toString(){	
		return cutoffTable.toString();
	}
	//****************************************
	public float getCutoffValue(String method){
		int colIdx = cutoffTable.getColIdx("method");
		int rowIdx = cutoffTable.getFirstRowIdx(colIdx, getMethod(method));
		float cutoff_value = ((Double)cutoffTable.get(rowIdx, cutoffTable.getColIdx("size"))).floatValue();		
		return cutoff_value;
	}
	
	//****************************************	
	public String getMethod(String method){		
		int colIdx = cutoffTable.getColIdx("method");
		int rowIdx = cutoffTable.getFirstRowIdx(colIdx, method);
		String resultMethod = method;
		if(rowIdx==-1){
			resultMethod = "mean";
			//System.out.println("MDR DEBUG: "+method +" ->>> " + resultMethod);			
		}		
		return resultMethod;
	}
	//****************************************
	public double[] getCutoffValues(){
		double[] values = ArrayUtils.Double2double(cutoffTable.getColumn(cutoffTable.getColIdx("size")));
		return values;
	}
	//****************************************

}
