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

import java.io.IOException;
import java.util.HashSet;

import dmLab.array.FArray;
import dmLab.array.meta.Attribute;
import dmLab.classifier.adx.ADXClassifier;
import dmLab.classifier.bayesNet.BayesNetClassifier;
import dmLab.classifier.ensemble.EnsembleClassifier;
import dmLab.classifier.hyperPipes.HyperPipesClassifier;
import dmLab.classifier.j48.J48Classifier;
import dmLab.classifier.knn.KNNClassifier;
import dmLab.classifier.logistic.LogisticClassifier;
import dmLab.classifier.m5.M5Classifier;
import dmLab.classifier.nb.NBClassifier;
import dmLab.classifier.randomForest.RandomForestClassifier;
import dmLab.classifier.ripper.RipperClassifier;
import dmLab.classifier.rnd.RNDClassifier;
import dmLab.classifier.sliq.SliqClassifier;
import dmLab.classifier.svm.SVMClassifier;
import dmLab.mcfs.MCFSParams;
import dmLab.mcfs.attributesID.AttributesID;
import dmLab.mcfs.attributesRI.AttributesRI;


public abstract class Classifier
{
	public Params params;     
	public String label;
	protected int id;
	
	public static int MODEL_CLASSIFIER = 0; 
	public static int MODEL_PREDICTOR = 1;
	public int modelType = MODEL_CLASSIFIER;
	
	protected PredictionResult predResult;	

	protected String tmpPath;	
	protected float learningTime;
	protected float testingTime;
    protected int trainSetSize;
        
    protected HashSet<String> attrSet;

	protected int model;
    public static int AUTO=0;
    public static int ENSEMBLE=1;
    public static int RND=2;
	public static int J48=3;
	public static int ADX=4;
	public static int SLIQ=5;
	public static int RF=6;	
	public static int NB=7;
	public static int SVM=8;
	public static int KNN=9;
	public static int RIPPER=10;
	public static int BNET=11;
	public static int HP=12;
	public static int LOGISTIC=13;
	public static int M5=14;
				
	public static String labels[]={"auto","ensemble","rnd","j48","adx","sliq","rf","nb",
	    "svm","knn","ripper","bayesNet","hyperPipes","logistic","m5"};
	
	//*****************************************
	public Classifier()
	{
		modelType = MODEL_CLASSIFIER;
		predResult = new PredictionResult(modelType);
		learningTime=0;
		testingTime=0;
		tmpPath="";
		label="classifier";
		id=0;
	}	
	//*****************************************
	public static Classifier getClassifier(int model){
		Classifier classifier = null;
		if(model==Classifier.ENSEMBLE){
			classifier=new EnsembleClassifier();
		}else if(model==Classifier.RND){
			classifier=new RNDClassifier();
		}else if(model==Classifier.J48){
			classifier=new J48Classifier();
		}else if(model==Classifier.ADX){
			classifier=new ADXClassifier();
		}else if(model==Classifier.SLIQ){
			classifier=new SliqClassifier();
		}else if(model==Classifier.RF){
			classifier=new RandomForestClassifier();
		}else if(model==Classifier.NB){
			classifier=new NBClassifier();
		}else if(model==Classifier.KNN){
			classifier=new KNNClassifier();
		}else if(model==Classifier.RIPPER){
			classifier=new RipperClassifier();
		}else if(model==Classifier.SVM){
			classifier=new SVMClassifier();
		}else if(model==Classifier.BNET){
			classifier=new BayesNetClassifier();
		}else if(model==Classifier.HP){
			classifier=new HyperPipesClassifier();
		}else if(model==Classifier.LOGISTIC){
			classifier=new LogisticClassifier();
		}else if(model==Classifier.M5){
			classifier=new M5Classifier();
		}else{
			System.err.println("Error creating the classifier. Incorrect model value.");
			return null;
		}
		return classifier;
	}
	//*****************************************
	public boolean isClassifier(){
		if(modelType == MODEL_CLASSIFIER)
			return true;
		else
			return false;
	}
	//*****************************************
	public int model()
	{
		return model;
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
	public void setId(int id)
	{
		this.id=id;
	}
	//*****************************************
	public String getMyName()
	{
		return label+"_C"+id;
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
	public boolean checkTargetAttr(FArray array){
		int DecAttrType = array.attributes[array.getDecAttrIdx()].type;
		if(DecAttrType == Attribute.NOMINAL && modelType == MODEL_PREDICTOR){
			System.err.println("Model" + getMyName()  + " cannot handle nominal target attribute!");
			return false;
		}
		else if(DecAttrType == Attribute.NUMERIC && modelType == MODEL_CLASSIFIER){
			System.err.println("Model" + getMyName()  + " cannot handle numeric target attribute!");
			return false;			
		}else
			return true;		
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
    public abstract boolean add_RI(AttributesRI importances[]);
    //*****************************************
    public boolean add_ID(AttributesID attrIDependencies, MCFSParams params)
    {
        if(attrSet==null || attrSet.size()==0)
            return false;
        
        Object attr[]=attrSet.toArray();
        for(int i=0;i<attr.length;i++)
        {
            for(int j=0;j<attr.length;j++)
                if(i!=j)
                    attrIDependencies.addID((String)attr[i],(String)attr[j],1.0f);                    
        }
        return true;
    }
    //*****************************************
    public HashSet<String> getModelAttributes()
    {
        return attrSet;
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
	public Params getParams()
	{
		return params;
	}
//  *************************************
	public PredictionResult getPredResult(){
		return predResult;
	}
//  *************************************
}
