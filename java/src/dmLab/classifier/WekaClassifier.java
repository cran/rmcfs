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
package dmLab.classifier;

import java.io.File;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import dmLab.array.FArray;
import dmLab.array.saver.Array2File;
import dmLab.array.saver.Array2Instances;
import dmLab.utils.cmatrix.ConfusionMatrix;

public abstract class WekaClassifier extends Classifier {

    public int mode = MEMORY;
	protected ArffLoader arffLoader;
    protected Array2File array2File;
    protected Instances wekaTrainInstances;
    
    public static final String ARFF_TRAIN_FILE="tmp_train.arff";
    public static final String ARFF_TEST_FILE="tmp_test.arff";
    
    public static final int MEMORY=0;
    public static final int FILE=1;
    
    protected weka.classifiers.Classifier wekaClassifier;
//******************************************
    protected abstract void setParams();
//******************************************
    @Override
    public boolean train(FArray trainArray) 
    {
    	if(mode == MEMORY)
    		return train_memory(trainArray);
    	else
    		return train_file(trainArray);
    }
  //******************************************    
    @Override
    public boolean test(FArray testArray) 
    {
    	if(mode == MEMORY)
    		return test_memory(testArray);
    	else
    		return test_file(testArray);
    }
  //******************************************    
    protected boolean train_memory(FArray trainArray) 
    {
        setParams();
        double start,stop;
        start=System.currentTimeMillis();
        
        trainSetSize=trainArray.rowsNumber();
        wekaTrainInstances = Array2Instances.convert(trainArray);
        wekaTrainInstances.setClassIndex(trainArray.getDecAttrIdx());            
        //System.out.println("@@@ MDR DEBUG: \n"+wekaTrainInstances.toString()+"\n");
        
        try{
            wekaClassifier.buildClassifier(wekaTrainInstances);
        }
        catch (Exception e){
        	System.err.println(getMyName()+" Error: Training Classifier!");
            e.printStackTrace();
            return false;
        }      
        stop=System.currentTimeMillis();
        learningTime=(float)((stop-start)/1000.0);

        return true;
    }
//******************************************        
    protected boolean train_file(FArray trainArray) 
    {
        setParams();
        double start,stop;
        start=System.currentTimeMillis();

        String trainFilePath = getTmpFilePath(ARFF_TRAIN_FILE);
        array2File.saveFile(trainArray, trainFilePath);
        trainSetSize=trainArray.rowsNumber();
        File trainFile=new File(trainFilePath);
        try{
            arffLoader.setFile(trainFile);
            wekaTrainInstances = arffLoader.getDataSet();
            arffLoader.reset();
            wekaTrainInstances.setClassIndex(trainArray.getDecAttrIdx());
            trainFile.delete();
        }
        catch (Exception e)
		{
        	System.err.println(getMyName()+" Error: Loading Arff files!");
        	e.printStackTrace();
			return false;
		}
        
        try{
            wekaClassifier.buildClassifier(wekaTrainInstances);
        }
        catch (Exception e){
            System.err.println(getMyName()+" Error: Training Classifier!");
            e.printStackTrace();
            return false;
        }      
        stop=System.currentTimeMillis();
        learningTime=(float)((stop-start)/1000.0);
        return true;
    }
//******************************************        
    protected boolean test_memory(FArray testArray) 
    {
        double start,stop;
        start=System.currentTimeMillis();
        setParams();
        Instances wekaTestInstances;
        int testSetSize=testArray.rowsNumber();
        predictions=new Prediction[testSetSize];
               
        wekaTestInstances = Array2Instances.convert(testArray);
        wekaTestInstances.setClassIndex(testArray.getDecAttrIdx());
        //System.out.println("@@@ MDR DEBUG: \n"+wekaTestInstances.toString()+"\n");
        
        confusionMatrix=new ConfusionMatrix(testArray.getColNames(true)[testArray.getDecAttrIdx()],
        		testArray.getDecValuesStr());
        classifyInstances(wekaTestInstances);
        stop=System.currentTimeMillis();
        testingTime=(float)((stop-start)/1000.0);
        return true;
    }
  //******************************************
    protected boolean test_file(FArray testArray) 
    {
        double start,stop;
        start=System.currentTimeMillis();
        setParams();
        Instances wekaTestInstances;
        String testFilePath = getTmpFilePath(ARFF_TEST_FILE);
        array2File.saveFile(testArray, testFilePath);
        int testSetSize=testArray.rowsNumber();
        predictions=new Prediction[testSetSize];
        File testFile=new File(testFilePath);
        try{
            arffLoader.setFile(testFile);
            wekaTestInstances = arffLoader.getDataSet();
            arffLoader.reset();
            wekaTestInstances.setClassIndex(testArray.getDecAttrIdx());
            testFile.delete();
        }
        catch (Exception e){
        	System.err.println(getMyName()+" Error: Loading Arff files!");
            e.printStackTrace();
            return false;
        }
        confusionMatrix=new ConfusionMatrix(testArray.getColNames(true)[testArray.getDecAttrIdx()],
        		testArray.getDecValuesStr());
        classifyInstances(wekaTestInstances);
        stop=System.currentTimeMillis();
        testingTime=(float)((stop-start)/1000.0);
        return true;
    }
  //******************************************
    private boolean classifyInstances(Instances instances){
        final int size=instances.numInstances();
        for(int k=0;k<size;k++)
        {
            try{
                String className = instances.instance(k).stringValue(instances.classIndex());
                //System.out.println(className);                
                //wekaTrainInstances.classAttribute has proper order of values
                //double[] predictedDistribution = wekaClassifier.distributionForInstance(instances.instance(k));
            	//System.out.println(Arrays.toString(predictedDistribution));
                double predictedClassIndex = wekaClassifier.classifyInstance(instances.instance(k));
                String predictedClassName = wekaTrainInstances.classAttribute().value((int)predictedClassIndex);
                //System.out.println(className +" - "+ predictedClassName);
                
                predictions[k] = new Prediction(predictedClassName, null);
                confusionMatrix.add(className, predictedClassName);
            }
            catch(Exception e){
            	e.printStackTrace();
            	System.err.println(getMyName() + " Instance classification Error!");
            	System.err.println("\tInstance: " + instances.instance(k).toString());
            }
        }
        return true;
    }
//******************************************
    @Override
    public float classifyEvent(FArray array, int eventIndex) 
    {
        System.out.println("NOT IMPLEMENTED!!!");
        return 0;
    }
//******************************************
    protected boolean resetARFFLoader() 
    {
        try {
        	arffLoader.setFile(null);
            arffLoader.reset();
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }
//  *************************************
    @Override
    public String toString(boolean header){
    	if(header)
    		return(toString());
    	else
    		return wekaClassifier.toString();    		
    }
//  *************************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();          
        tmp.append(" ### "+label+" Classifier ### "+getMyName()+ " ### ").append('\n');
        tmp.append("label="+ label).append('\n');
        tmp.append(wekaClassifier.toString());
        return tmp.toString();
    }
//**************************************
    private String getTmpFilePath(String fileName)
    {
    	return tmpPath + "C" + classifierID + "_" +fileName;
    }    
//**************************************
    @Override
    public boolean finish() 
    {
        resetARFFLoader();        
        boolean deleted=false;
        String trainFilePath = getTmpFilePath(ARFF_TRAIN_FILE);
        String testFilePath = getTmpFilePath(ARFF_TEST_FILE);
        File trainFile=new File(trainFilePath);
        File testFile=new File(testFilePath);
        
        deleted = trainFile.delete() && testFile.delete();
        
        //System.out.println("Finish Classifier" + classifierID + " deleted tmp files: " + deleted);
        trainFile.deleteOnExit();
        testFile.deleteOnExit();
        return deleted;
    }
  //**************************************
}
