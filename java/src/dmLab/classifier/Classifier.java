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
/**
 * 
 */
package dmLab.classifier;

import java.io.IOException;
import java.util.HashSet;

import dmLab.array.FArray;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.utils.cmatrix.ConfusionMatrix;

/**
 * @author mdramins
 *
 */
public abstract class Classifier
{
	public Params params;     
	public String label;
	protected int classifierID;
	
	protected ConfusionMatrix confusionMatrix;
	protected String tmpPath;	
	protected float learningTime;
	protected float testingTime;
    protected int trainSetSize;
    
	protected Prediction predictions[];
    
    protected HashSet<String> attrSet;
    
    public static int ENSEMBLE=0;
    public static int RND=1;
	public static int J48=2;
	public static int ADX=3;
	public static int SLIQ=4;
	public static int RF=5;	
	public static int NB=6;
	public static int SVM=7;
	public static int KNN=8;
	public static int RIPPER=9;
	public static int BNET=10;
	public static int HP=11;
	public static int LOGISTIC=12;
		
	public static String labels[]={"ensemble","rnd","j48","adx","sliq","rf","nb",
	    "svm","knn","ripper","bayesNet","hyperPipes","logistic"};
	
	protected int type;	
	//*****************************************
	public Classifier()
	{
		learningTime=0;
		testingTime=0;
		tmpPath="";
		label="classifier";
		classifierID=0;
	}
	//*****************************************
	public int type()
	{
		return type;
	}
	//*****************************************
	public String label()
	{
		return label;
	}
	//*****************************************
	public void setLabel(String label)
	{
		this.label = label;
	}
//  *************************************
	public void setClassifierId(int id)
	{
		classifierID=id;
	}
	//*****************************************
	public String getMyName()
	{
		return label+"_C"+classifierID;
	}
	//*****************************************
	public void setTempPath(String tempPath)
	{
		tmpPath=tempPath;
	}
	//*****************************************
	public String getTempPath()
	{
		return tmpPath;
	}
	//*****************************************
	public double getLearningTime()
	{
		return learningTime;
	}
	//*****************************************
	public double getTestingTime()
	{
		return testingTime;
	}
	//*****************************************
	public abstract boolean train(FArray trainArray);
	//*****************************************
	public abstract boolean test(FArray testArray);	
	//*****************************************
	public abstract boolean init();
	//*****************************************
	public abstract boolean finish();
    //*****************************************
	@Override
    public abstract String toString();
	//*****************************************
    public String toString(boolean header){
    	return toString();
    }
	//*****************************************
	//each classifier implements saving of its definition
	public abstract boolean saveDefinition(String path,String name)  throws IOException;
	//*****************************************
	//each classifier implements loading of its definition
	public abstract boolean loadDefinition(String path,String name)  throws IOException;
	//*****************************************
	public abstract float classifyEvent(FArray array,int eventIndex);
    //*****************************************
    public abstract boolean addImportances(AttributesRI importances[]);
    //*****************************************
    public boolean addIDependencies(AttributesID attrIDependencies, MCFSParams params)
    {
        if(attrSet==null || attrSet.size()==0)
            return false;
        
        Object attr[]=attrSet.toArray();
        for(int i=0;i<attr.length;i++)
        {
            for(int j=0;j<attr.length;j++)
                if(i!=j)
                    attrIDependencies.addDependency((String)attr[i],(String)attr[j],1.0f);                    
        }
        return true;
    }
    //*****************************************
    public HashSet<String> getModelAttributes()
    {
        return attrSet;
    }
	//*****************************************
    public ConfusionMatrix getConfusionMatrix()
    {
        return confusionMatrix;
    }
    //*****************************************
    public Prediction[] getPredictions()
    {
        return predictions;  
    }
//  *****************************************
    public String predictionsToString()
    {
        StringBuffer tmp=new StringBuffer();
        for(int i=0;i<predictions.length;i++)            
            tmp.append(predictions[i].toString());
        
        return tmp.toString(); 
    }
//  *****************************************
	public static String int2label(int id)
	{
	    if(id<0 || id>=labels.length)
	        return "";
	    else	        
	        return labels[id];	                      
	}
	//*****************************************
	public static int label2int(String label)
	{
		for(int i=0;i<labels.length;i++)		    		    
		    if(label.equalsIgnoreCase(labels[i])) 
		        return i;
		
		return -1;
	}
//  *************************************
}
