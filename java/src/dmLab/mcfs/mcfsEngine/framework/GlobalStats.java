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
package dmLab.mcfs.mcfsEngine.framework;

import java.util.ArrayList;

import dmLab.array.FArray;
import dmLab.classifier.Classifier;
import dmLab.gui.chartPanel.ChartFrame;
import dmLab.gui.chartPanel.DataSeries;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.Ranking;
import dmLab.mcfs.attributesRI.measuresRI.ADXRIMeasure;
import dmLab.mcfs.attributesRI.measuresRI.NodesMeasure;
import dmLab.mcfs.attributesRI.measuresRI.ClassifiersMeasure;
import dmLab.mcfs.attributesRI.measuresRI.ImportanceMeasure;
import dmLab.mcfs.attributesRI.measuresRI.J48RIMeasure;
import dmLab.mcfs.attributesRI.measuresRI.ProjectionMeasure;
import dmLab.mcfs.attributesRI.measuresRI.RINormMeasure;
import dmLab.mcfs.attributesRI.measuresRI.SliqRIMeasure;
import dmLab.mcfs.cutoffMethods.Cutoff;
import dmLab.utils.ArrayUtils;
import dmLab.utils.GeneralUtils;
import dmLab.utils.MathUtils;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.dataframe.Column;
import dmLab.utils.dataframe.DataFrame;
import dmLab.utils.list.FloatList;
import dmLab.utils.statFunctions.LinearRegression;
import dmLab.utils.statList.StatsList;

public class GlobalStats {
    
    private ConfusionMatrix confusionMatrix;
    private ChartFrame distanceChart = null;
    private LinearRegression myLinearRegression;
    private AttributesRI attrRI[];
    private AttributesID attrID;
    private StatsList splitsStats;
    private Cutoff cutoff;
    
    private MCFSParams myMCFSParams;
        
    private FloatList distanceList=new FloatList();
    private FloatList commonPartList=new FloatList();
    private FloatList mAvgList=new FloatList();
    private FloatList betaList=new FloatList();
    private FloatList projectionIdxList=new FloatList();   
    
    private Ranking oldRank=null;
    private Ranking newRank=null;
        
    private int projectionsCounter=0;
    private int calculatedDistances=0;    
    private float[] xArray;

    private String prefix;
    
    public String[] attrNames;    
    public static int WINDOW_SIZE=20;
    
//  *************************************    
    public GlobalStats()
    {        
    }
//  *************************************
    public boolean init(FArray inputArray, MCFSParams mcfsParams, String experimentName)
    {
        myMCFSParams = mcfsParams;
        prefix = mcfsParams.resFilesPATH + experimentName;

        calculatedDistances = 0;
        projectionsCounter = 0;
        
        attrNames = inputArray.getColNames(true);
        attrRI = initImportances(myMCFSParams, inputArray);
        if(mcfsParams.buildID){        	
        	attrID = new AttributesID(attrNames, true, false);
        }
        if(inputArray.isTargetNominal())
        	confusionMatrix = new ConfusionMatrix(inputArray.getColNames(true)[inputArray.getDecAttrIdx()],inputArray.getDecValuesStr());
		else
			confusionMatrix = null;

        initChartFrame(mcfsParams);        
        initLinearRegression();
        splitsStats = new StatsList();
        
        return true;
    }
//************************************
    private boolean initChartFrame(MCFSParams mcfsParams)
    {
        if(mcfsParams.progressShow){
        	if(distanceChart!=null){
                distanceChart.dispose();
                distanceChart=null;
            }
            distanceChart=new ChartFrame("dmLab","MCFS Calculation Progress");           
        }        
        return true;
    }
//************************************    
    private void initLinearRegression()
    {
    	myLinearRegression = new LinearRegression();
    	
    	xArray = new float [WINDOW_SIZE];
    	for (int i=0; i<xArray.length; i++)
    		xArray[i]=i;
    }
  //************************************
    public AttributesRI[] initImportances(MCFSParams mcfsParams, FArray inputArray)
    {
        String decValues[] = inputArray.getDecValuesStr();
        AttributesRI[] importances = new AttributesRI[1];
        //If a classifier calculates attributesImportance separately for each class - change this
        //only ADX can calculate attributesImportance separately for each class
        //attributesImportance[0] contains general ranking
        if(mcfsParams.model == Classifier.ADX)
        	importances = new AttributesRI[1 + decValues.length];

        for(int i=0;i<importances.length;i++){            
            importances[i]= new AttributesRI(inputArray);            
            importances[i].addMeasure(new ProjectionMeasure(null));            
            importances[i].addMeasure(new ClassifiersMeasure(null));
            importances[i].addMeasure(new NodesMeasure(null));            
            if(mcfsParams.model==Classifier.J48 || mcfsParams.model==Classifier.M5)
                importances[i].addMeasure(new J48RIMeasure(mcfsParams));
            else if(mcfsParams.model==Classifier.SLIQ)
                importances[i].addMeasure(new SliqRIMeasure(mcfsParams));
            else if(mcfsParams.model==Classifier.ADX)
                importances[i].addMeasure(new ADXRIMeasure(mcfsParams));            
            importances[i].addMeasure(new RINormMeasure(mcfsParams));
            
            //set main measure on RI_norm
            importances[i].mainMeasureIdx = importances[i].getMeasureIndex(ImportanceMeasure.MEASURE_RINORM);

            //set label on class name. General ranking have label 'all'
            if(i==0)
                importances[i].label = "";
            else
                importances[i].label = decValues[i-1];
            
            importances[i].initImportances();
        }
        return importances;
    }
//  *************************************
    public synchronized boolean updateSplitsStats(StatsList localSplitsStats){
    	return splitsStats.add(localSplitsStats);
    }
//  *************************************
    public synchronized boolean update(int jobId, ConfusionMatrix localMatrix, AttributesRI localImportances[], AttributesID localAttrID)
    {
        if(projectionsCounter >= myMCFSParams.projections){
            System.out.println("[thread: "+jobId+"] Stop Criterion: projections = "+projectionsCounter);
            return false;
        }
        projectionsCounter = (calculatedDistances +1) * myMCFSParams.progressInterval;

        //update confusionMatrix
        if(confusionMatrix != null)
        	confusionMatrix.add(localMatrix);
        
        if(attrID != null)
        	attrID.addDependencies(localAttrID);
        
        //update and save importances for all classes in [0] and each separated class if initiated
        for(int j=0; j<attrRI.length; j++){
            if(attrRI[j]!=null){
                attrRI[j].sumImportances(localImportances[j]);                
                //calc normalized RI before saving
                attrRI[j].calcNormMeasure(myMCFSParams.splits);
                attrRI[j].save(prefix+"_"+attrRI[j].label+"_"+MCFSParams.FILESUFIX_IMPORTANCES);                
            }          
        }
        
        newRank = attrRI[0].getTopRankingSize(attrRI[0].mainMeasureIdx, myMCFSParams.progressTopMinSize);                        
        if(oldRank!=null){
            float distance = newRank.compare(oldRank);                    
            float commonPart = newRank.commonPart(oldRank);
        	float mAvg = 0;
        	float beta1 = 0;

            if(distanceList.size()>=WINDOW_SIZE){
            	float[] yArray =  distanceList.toArray(distanceList.size()-WINDOW_SIZE,distanceList.size());            	
            	mAvg = (float)MathUtils.mean(ArrayUtils.float2double(yArray));
            	myLinearRegression.calc(xArray, yArray);
            	beta1 = (float) myLinearRegression.getBeta1();
            }

            if(calculatedDistances > 1){
                projectionIdxList.add(projectionsCounter);                
                distanceList.add(distance);
                commonPartList.add(commonPart);
                betaList.add(beta1);
                mAvgList.add(mAvg);
                if(distanceChart!=null){
                    ArrayList<DataSeries> series = new ArrayList<DataSeries>();
                    series.add(new DataSeries("distance",projectionIdxList.toArray(),distanceList.toArray(),true));
                    series.add(new DataSeries("common part",projectionIdxList.toArray(),commonPartList.toArray(),false));
                    series.add(new DataSeries("beta1",projectionIdxList.toArray(),betaList.toArray(),false));
                    distanceChart.setAxisLabels("projections number", "distance value");
                    distanceChart.draw(series);
                }
            }
            
            System.out.println("*** PROJECTION: " + projectionsCounter
            					+ " [thread: " + jobId+"]"
            					+ " *** -> distance: " + GeneralUtils.format(distance, 4) 
            					+ " commonPart: " + GeneralUtils.format(commonPart,4)
            					+" mAvg: " + GeneralUtils.format(mAvg,4)
            					+" beta1: " + GeneralUtils.format(beta1,4));
        }
                        
        oldRank=newRank;
        calculatedDistances++;
        
        return true;
    }
//  *************************************
    public DataFrame getDistances(){        
        DataFrame distancesDF = new DataFrame(distanceList.size(), 5);
        distancesDF.setColNames(new String[]{"projection","distance","commonPart","mAvg","beta1"});
        distancesDF.setColTypes(new short[]{Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC,Column.TYPE_NUMERIC});        
        distancesDF.setColumn(0, projectionIdxList.toArray());
        distancesDF.setColumn(1, distanceList.toArray());
        distancesDF.setColumn(2, commonPartList.toArray());
        distancesDF.setColumn(3, mAvgList.toArray());
        distancesDF.setColumn(4, betaList.toArray());
        return distancesDF;
    }
//  *************************************
    public ConfusionMatrix getConfusionMatrix()
    {
        return confusionMatrix;
    }
//  *************************************
    public AttributesRI[] getAttrImportances()
    {
        return attrRI;
    }
//  *************************************    
    public AttributesID getAttrConnections()
    {
        return attrID;
    }
//  *************************************
    public StatsList getSplitsStats(){
    	return splitsStats;
    }
//  *************************************
    public Cutoff getCutoff(){
    	return cutoff;
    }
//  *************************************
    public void setCutoff(Cutoff cutoff){
    	this.cutoff = cutoff;
    }
//  *************************************
}
