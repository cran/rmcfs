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
package dmLab.classifier.ensemble;

import java.io.IOException;

import dmLab.array.FArray;
import dmLab.classifier.Classifier;
import dmLab.classifier.Prediction;
import dmLab.classifier.bayesNet.BayesNetClassifier;
import dmLab.classifier.hyperPipes.HyperPipesClassifier;
import dmLab.classifier.j48.J48Classifier;
import dmLab.classifier.knn.KNNClassifier;
import dmLab.classifier.nb.NBClassifier;
import dmLab.classifier.randomForest.RandomForestClassifier;
import dmLab.classifier.ripper.RipperClassifier;
import dmLab.classifier.sliq.SliqClassifier;
import dmLab.classifier.svm.SVMClassifier;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.list.FloatList;
import dmLab.utils.list.ObjectList;

public class EnsembleClassifier extends Classifier {
    
    ObjectList en;
    FloatList weights;
    private EnsembleParams cfg;
//  ****************************************************
    public EnsembleClassifier()
    {
        super();
        label=labels[ENSEMBLE];
        type=ENSEMBLE;        
        en=new ObjectList();
        weights=new FloatList();
        params=new EnsembleParams();
        cfg=(EnsembleParams)params;
        init();
    }
//  ****************************************************
    @Override
    public boolean init()
    {
        en=new ObjectList();
        en.add(new J48Classifier());
        en.add(new SliqClassifier());
        en.add(new RandomForestClassifier());
        //en.add(new ADXClassifier());
        en.add(new NBClassifier());      
        en.add(new SVMClassifier());
        en.add(new KNNClassifier());
        en.add(new RipperClassifier());       
        en.add(new BayesNetClassifier());       
        en.add(new HyperPipesClassifier());
        
        setParams();
        normalizeWeights();
        //MDR DEBUG
        //System.out.println("INIT:        \n"+ weightsToString());
        return true;
    }
//  ****************************************************
    public String getSubClassifierName(int i)
    {
        if(i>=0 &&i <en.size())
            return ((Classifier)en.get(i)).label;
        else
            return null;
    }
//  ****************************************************
    public float normalizeWeights()
    {
        int size=weights.size();
        float sum=0f;
        float newSum=0f;
        for(int i=0;i<size;i++)        
            sum+=weights.get(i);
        
        for(int i=0;i<size;i++)
        {
            if(weights.get(i)!=0)
                weights.set(i,weights.get(i)/sum);
            newSum+=weights.get(i);
        }
        return newSum;
    }
//  ****************************************************
    public int size()
    {
        return en.size();
    }
//  ****************************************************
    public boolean setWeights(float[] _weights)
    {
        if (_weights.length!=en.size())
            return false;
        
        weights=new FloatList();
        for(int i=0;i<_weights.length;i++)
            weights.add(_weights[i]);

        normalizeWeights();
            
        return true;
    }
//  ****************************************************
    public String weightsToString()
    {
        StringBuffer tmp=new StringBuffer();
        int size = en.size();
        for(int i=0;i<size;i++)        
            tmp.append(((Classifier)en.get(i)).label).append(" = ").append(weights.get(i)).append('\n');
        
        tmp.append('\n').append("ensemble.weights=[");
        for(int i=0;i<size;i++)
        {
            tmp.append(weights.get(i));
            if(i<size-1)
                tmp.append(',');    
        }
        tmp.append(']').append('\n');
        return tmp.toString();   
    }
//  ****************************************************
    @Override
    public String toString() 
    {
        return weightsToString();
    }
//  ****************************************************
    @Override
    public boolean train(FArray trainArray) 
    {
        if(params.verbose) System.out.println("Training...");
        
        //MDR DEBUG
        //System.out.println("TRAIN:        \n"+ weightsToString());
        
        final int classifiersNum=en.size();
        for(int i=0;i<classifiersNum;i++)
        {
            if(weights.get(i)!=0)
            {
                ((Classifier)en.get(i)).train(trainArray);
                if(params.verbose) 
                    System.out.println("*** Classifier: "+ ((Classifier)en.get(i)).label+" is trained ***");
            }
        }
        return true;
    }
//  ****************************************************
    @Override
    public boolean test(FArray testArray) 
    {               
        long start,stop;
        if(params.verbose) System.out.println("Testing...");                
        start=System.currentTimeMillis();        
        confusionMatrix=new ConfusionMatrix(testArray.getColNames(true)[testArray.getDecAttrIdx()],
        		testArray.getDecValues(),testArray.getDecValuesStr());
        float predictedDecision=-1f;
        float realDecision;
        final int testEventsNumber=testArray.rowsNumber();
        final int interval=(int)Math.ceil(0.1*testEventsNumber);
        int threshold=interval;
        predictions=new Prediction[testEventsNumber];        
        EnsembleDecisionWeights ensembleDecisions=new EnsembleDecisionWeights(testArray.getDecValues());  
        
        final int classifiersNum=en.size();
        
        for(int i=0;i<classifiersNum;i++)
        {
            if(weights.get(i)!=0)
            {
                ((Classifier)en.get(i)).test(testArray);
                if(params.verbose) System.out.println("*** Classifier: "+ ((Classifier)en.get(i)).label+" is tested ***");
            }
        }
        
        final int decAttrIndex=testArray.getDecAttrIdx();
        float currDecision=0;
        for(int i=0;i<testEventsNumber;i++)
        {
            realDecision=testArray.readValue(testArray.getDecAttrIdx(), i);
            ensembleDecisions.cleanWeights();
            for(int j=0;j<classifiersNum;j++)
            {
                if(weights.get(j)!=0)
                {
                    String currlabel = ((Classifier)en.get(j)).getPredictions()[i].getLabel();
                    currDecision = testArray.dictionary.toFloat(currlabel);
                    float currWeight=weights.get(j);
                    ensembleDecisions.add(currDecision, currWeight);
                }
            }            
            predictedDecision=ensembleDecisions.getMaxDecision();
            
            //MDR DEBUG
            //System.out.println(tmp+" :: "+predictedDecision);
            //System.out.println(ensembleDecisions.toString());
                
            realDecision=testArray.readValue(decAttrIndex,i);
            confusionMatrix.add(realDecision,predictedDecision);

            String predictedClassName=testArray.dictionary.toString(predictedDecision);
            
            predictions[i]=new Prediction(predictedClassName,null);
            
            if(i>threshold && threshold!=0)
            {
                if(params.verbose) System.out.print(""+ (int)(100.0*i/testEventsNumber) + "% ");
                threshold+=interval;
            }
        }
        if(params.verbose) System.out.print("100% ");
        stop=System.currentTimeMillis();
        testingTime=(stop-start)/1000.0f;
        if(params.verbose) System.out.println(" Done!");
        return true;

    }
//  ****************************************************
    protected void setParams()
    {               
        setWeights(cfg.weights);
    }
//  ****************************************************    
    @Override
    public boolean addImportances(AttributesRI[] importances) {
        return false;
    }
//  ****************************************************
    @Override
    public float classifyEvent(FArray array, int eventIndex) {
        return 0;
    }
//  ****************************************************
    @Override
    public boolean loadDefinition(String path, String name) throws IOException {
        return false;
    }
//  ****************************************************
    @Override
    public boolean saveDefinition(String path, String name) throws IOException {
        return false;
    }
    //*****************************************
    @Override
    public boolean finish() {
        return true;
    }
//  ****************************************************
    
}
