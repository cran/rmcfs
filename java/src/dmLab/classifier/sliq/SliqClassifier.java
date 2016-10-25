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
package dmLab.classifier.sliq;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import dmLab.array.FArray;
import dmLab.classifier.Classifier;
import dmLab.classifier.Prediction;
import dmLab.classifier.sliq.Tree.Const;
import dmLab.classifier.sliq.Tree.treePruning.mdlTreePruning.MdlTreePruning;
import dmLab.mcfs.attributesRI.AttributesRI;
import dmLab.mcfs.attributesRI.ExperimentIndicators;
import dmLab.utils.cmatrix.ConfusionMatrix;
import dmLab.utils.cmatrix.QualityMeasure;



//**********************************************************
public class SliqClassifier extends Classifier
{
	private AttributeList[] attributesLists; // attribute lists
	private ClassList classList;
	private int trainAttrNumber;
	private SliqTree sliqTree;
	private FArray trainArray;

	private SliqParams cfg;

	//***********************************************
	public SliqClassifier()
	{
		super();
		label="sliq";
		model=SLIQ;
		params=new SliqParams();
		cfg=(SliqParams)params;
	}
	//***********************************************
	@Override
    public boolean saveDefinition(String path, String name) throws IOException
	{
		if(params.verbose) System.out.print("Saving classifier definition...");
		params.save(path,name);
		//this method saves only viualization of the decision tree
		//there is no metod to load such visualization from the file
		FileWriter writer=null;
		writer = new FileWriter(path+"//"+name+".tree");
		writer.write(toString());
		writer.close();
		if(params.verbose) System.out.println(" Done!");
		return true;
	}
	//****************************************************
	@Override
    public boolean loadDefinition(String path, String name) throws IOException
	{
		System.out.println("Loading is not IMPLEMENTED!!!");
		return false;
	}
    //****************************************************
    @Override
    public boolean add_RI(AttributesRI importances[])
    {        
        attrSet=new HashSet<String>();
        
        ExperimentIndicators experimentIndicators=new ExperimentIndicators();
        experimentIndicators.eventsNumber=trainSetSize;
        experimentIndicators.predictionQuality=QualityMeasure.calcWAcc(predResult.confusionMatrix.getMatrix());

        sliqTree.addInfo(trainArray,sliqTree.root,experimentIndicators,importances[0],attrSet);
        
        for(int i=0;i<importances.length;i++)
            importances[i].flushMeasures();
        
        return false;
    }
	//****************************************************
	@Override
    public boolean train(FArray trainArray)
	{
		this.trainArray=trainArray;
        trainSetSize=trainArray.rowsNumber();
		trainAttrNumber = trainArray.colsNumber();//-1;
		attributesLists = new AttributeList[trainAttrNumber];

		long start = System.currentTimeMillis();

		// *****************************************
		// 1. Class Lists declaration
		classList = new ClassList(trainArray);

		// *****************************************
		// 2. Attributes Lists declaration
		final int decisionIndex=trainArray.getDecAttrIdx();
		for (int j=0; j < trainAttrNumber; j++)
			if ( j != decisionIndex )
				attributesLists[j] = new AttributeList(trainArray,j);

		// 3. SLIQ-Tree declaration
		sliqTree = new SliqTree(classList);
		sliqTree.setAttributesNumber(trainAttrNumber);
		// All to ROOT
		classList.initializeClassNodesRefs(sliqTree);

		// *********************************
		//sliqTree.showLeafs();
		int level=0;
		classList.calcClassFrequencies(sliqTree);
		sliqTree.calcFreqClassGiniPurity((SliqNode)sliqTree.getRoot(), classList);
		while ((sliqTree.leafsPurityCheck() == Const.NODE )&&(level<cfg.maxLevel))
		{
			evaluateNodesSplits(trainArray);
			//showAttributesLists();
			//sliqTree.showLeafs();
			sliqTree.createLeafs(classList);
			updateClassList(trainArray);
			//showAttributesLists();
			if(params.verbose) System.out.println("Tree building - level: " + level);
			level++;
		}
		//evaluateNodesSplits(trainArray);
		//sliqTree.showLeafs();

		long stop = System.currentTimeMillis();
		learningTime = ((float)(stop-start))/1000;
		sliqTree.setTreeBuildingTime(super.learningTime);

		if(cfg.mdlPruning)
		{
			start = System.currentTimeMillis();
			prune();
			stop = System.currentTimeMillis();
			learningTime = ((float)(stop-start))/1000;
			sliqTree.setTreePruningTime(super.learningTime);
		}
		return true;
	}
	//****************************************************
	@Override
    public boolean test(FArray testArray)
	{
		long start,stop;
		if(params.verbose) System.out.print("Testing...");
		start=System.currentTimeMillis();
		predResult.confusionMatrix=new ConfusionMatrix(testArray.getColNames(true)[testArray.getDecAttrIdx()],
				testArray.getDecValues(),testArray.getDecValuesStr());
		float predictedDecision;
		float realDecision;
		final int testEventsNumber=testArray.rowsNumber();
		//final int interval=(int)Math.ceil(0.1*testEventsNumber);
		predResult.predictions=new Prediction[testEventsNumber];
        
		final int decAttrIndex=testArray.getDecAttrIdx();

		for(int i=0;i<testEventsNumber;i++)
		{
			predictedDecision=classifyEvent(testArray,i);
			realDecision=testArray.readValue(decAttrIndex,i);
			predResult.confusionMatrix.add(realDecision,predictedDecision);
            
            String realClassName = testArray.dictionary.toString(realDecision);
            String predictedClassName=testArray.dictionary.toString(predictedDecision);            
            predResult.predictions[i]=new Prediction(realClassName, predictedClassName,null);
		}
		stop=System.currentTimeMillis();
		testingTime=(stop-start)/1000.0f;
		if(params.verbose) System.out.println("Testing is finished!");
		return true;
	}
	//****************************************************
	@Override
    public float classifyEvent(FArray events, int eventIndex)
	{
		return sliqTree.classifyEvent(events, eventIndex);
	}
	//****************************************************
	@Override
    public boolean init()
	{
		return true;
	}
    //*****************************************
    @Override
    public boolean finish() {
        return true;
    }
	//****************************************************
	@Override
    public String toString()
	{
		StringBuffer tmp=new StringBuffer();
		tmp.append("### Sliq Classifier ### ").append('\n');
		tmp.append("label="+ label).append('\n');
		tmp.append(sliqTree.toString(trainArray,true));
		return tmp.toString();
	}
	//****************************************************
	//private methods
	private void prune()
	{
		if(params.verbose) System.out.println("Prunning the tree...");
		MdlTreePruning mdlTreePruning = new MdlTreePruning(sliqTree);
		mdlTreePruning.treePrune();
		if(params.verbose) System.out.println("Prunning is finished.");
	}
	//****************************************************
	private void evaluateNodesSplits(FArray trainArray)
	{
		int attrIndex = 0;
		int eventIndex = 0;
		float tmpGoodness = 0;
		int splittingValueProxyIndex;
		SliqNode node;

		//attributes processing
		for (attrIndex=0; attrIndex<trainAttrNumber; attrIndex++)
		{

			if(attributesLists[attrIndex]==null)
				continue;

			attributesLists[attrIndex].initSplitStatus();
			classList.initHistograms(sliqTree, attributesLists[attrIndex]);

			//System.out.println(attributesLists[attrIndex].toString());
			int eventsAttrNumber=attributesLists[attrIndex].getEventsNumber();
			// NUMERIC Attribute
			if (attributesLists[attrIndex].getAttrType() == Const.NUMERIC_ATTR)
			{
                          //  System.out.println("DEBUG: " + attrIndex + " name: " + trainArray.attributes[attrIndex].name + " type: " + trainArray.attributes[attrIndex].type);
				leafsTmpsInit();

				for (eventIndex = 0; eventIndex<eventsAttrNumber; eventIndex++)
				{
					if (classList.getAttrValueLeafPurity(attributesLists[attrIndex],eventIndex) == Const.NODE )
					{
						splittingValueProxyIndex = attributesLists[attrIndex].getAttrProxyIndex(eventIndex);
						node = classList.getAttrValueLeaf(attributesLists[attrIndex], eventIndex);

						if (node.tmpSplittingValueProxyIndexEval == Const.NO_SPLITTING_VALUE_PROXY_INDEX)
						{
							node.tmpSplittingValueProxyIndexEval = splittingValueProxyIndex;
							//System.out.println("Init dla node: " + node);
						}
						if (node.tmpSplittingValueProxyIndexEval != splittingValueProxyIndex)
						{
							tmpGoodness = sliqTree.getGoodnessOfSplit(node, Const.NUMERIC_ATTR);
							//System.out.println("Update dla node: " + node + ", val : " + node.tmpSplittingValueProxyIndex);
							if (tmpGoodness > node.tmpGoodnessOfSplit)
							{
								node.tmpGoodnessOfSplit = tmpGoodness;
								node.tmpSplittingValueProxyIndex = node.tmpSplittingValueProxyIndexEval;
								node.tmpSplittingValueProxyIndexEval = splittingValueProxyIndex;
							}
						}
						classList.updateToLEftAttrValueLeafNumHist(attributesLists[attrIndex],eventIndex);
					}
				}
				splitsUpdate(attributesLists[attrIndex], attrIndex, Const.NUMERIC_ATTR);
			}
			else
			{
				// NOMINAL Attribute
				for (eventIndex = 0; eventIndex<eventsAttrNumber; eventIndex++)
				{
                                    if ((attrIndex==9)&&(eventIndex==8083))
					if (classList.getAttrValueLeafPurity(attributesLists[attrIndex],eventIndex) == Const.NODE)
						classList.incRightAttrValueLeafNomHist(attributesLists[attrIndex], eventIndex);
				}
				splitsUpdate(attributesLists[attrIndex], attrIndex, Const.NOMINAL_ATTR);
			}
		}
		setAttrSplitStatuses();
	}
	//*****************************************************
	private void splitsUpdate(AttributeList attributeList, int splittingAttrIndex, boolean splittingAttrType)
	{
		int leafsNumber = sliqTree.getLeafsSize();
		SliqNode node;
		float[] splittingValues;

		if (splittingAttrType == Const.NUMERIC_ATTR)
		{
			for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++)
			{
				node = sliqTree.getLeaf(leafIndex);
				if ( node.getLeafIndicator() == Const.NODE)
					if ( node.tmpGoodnessOfSplit > node.getGoodnessOfSplit() )
					{
						node.setGoodnessOfSplit(node.tmpGoodnessOfSplit);
						node.setSplittingAttrIndex(attributeList.getAttrIndex());
						node.attrListSplittingAttrIndex = splittingAttrIndex;
						node.setSplittingAttrType(splittingAttrType);
						//node.leftChildPurity = node.tmpLeftChildPurity;
						//node.rightChildPurity = node.tmpRightChildPurity;
						splittingValues = new float[1];
						splittingValues[0] = attributeList.getAttrProxyValue(node.tmpSplittingValueProxyIndex);
						node.setSplittingValues(splittingValues);
					}
			}

		} else {

			for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++) {
				node = sliqTree.getLeaf(leafIndex);

				if ( node.getLeafIndicator() == Const.NODE) {
					splittingValues = sliqTree.findBestSubset(node, attributeList);

					if (node.tmpGoodnessOfSplit > node.getGoodnessOfSplit()) {
						node.setGoodnessOfSplit(node.tmpGoodnessOfSplit);
						node.setSplittingAttrIndex(attributeList.getAttrIndex());
						node.attrListSplittingAttrIndex = splittingAttrIndex;
						node.setSplittingAttrType(splittingAttrType);
						node.setSplittingValues(splittingValues);
					}
				}

			}

		}

	}
	//************************************************
	private void setAttrSplitStatuses()
	{
		int leafsNumber = sliqTree.getLeafsSize();
		SliqNode node;
		for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++) {
			node = sliqTree.getLeaf(leafIndex);
			if (node.attrListSplittingAttrIndex != Const.NO_SPLITTING_ATTR_INDEX)
				attributesLists[node.attrListSplittingAttrIndex].setSplitStatus(Const.ATTR_USED_IN_SPLIT);
		}
	}
	//***********************************************
	private void leafsTmpsInit()
	{
		int leafsNumber = sliqTree.getLeafsSize();
		SliqNode node;
		for (int leafIndex = 0; leafIndex < leafsNumber; leafIndex++) {
			node = sliqTree.getLeaf(leafIndex);
			if (node.getLeafIndicator() == Const.NODE) node.tmpInit();
		}
	}
	//*************************************************
	private void updateClassList(FArray trainArray)
	{
		SliqNode node;
		int classIndex;
		int leafsNumber = sliqTree.getLeafsSize();

		for (int attrIndex=0; attrIndex<trainAttrNumber; attrIndex++)
		{
			if(attributesLists[attrIndex]==null)
				continue;

			if (attributesLists[attrIndex].getSplitStatus() == Const.ATTR_USED_IN_SPLIT)
			{
				//System.out.println("===========");
				//System.out.println("=========== Atrybut: " + attrIndex + ", status: " + attributesLists[attrIndex].getSplitStatus());
				int eventsNumber=attributesLists[attrIndex].getAllEventsNumber();
				for (int eventIndex=0; eventIndex<eventsNumber; eventIndex++)
				{
					classIndex = attributesLists[attrIndex].getAllAttrClassIndex(eventIndex);
					node = classList.getClassLeafRef(classIndex);
					//System.out.println("=========== Atrybut: " + attrIndex + ", event index: " + eventIndex + ", status: " + attributesLists[attrIndex].getSplitStatus() + ", class: " + classIndex);
					if ((node.getLeafIndicator() == Const.NODE)&&(node.attrListSplittingAttrIndex == attrIndex))
					{
						if (sliqTree.applyNodeTest(node, trainArray, classIndex) == Const.LEFT_CHILD)
							classList.setClassLeafRef(classIndex, (SliqNode)node.left);
						else
							classList.setClassLeafRef(classIndex, (SliqNode)node.right);
					}
				}

			}

		}
		classList.calcClassFrequencies(sliqTree);
		for (int leafIndex=0; leafIndex<leafsNumber; leafIndex++) {
			node = sliqTree.getLeaf(leafIndex);
			if (node.getLeafIndicator() == Const.NODE)
				sliqTree.calcFreqClassGiniPurity(node, classList);
		}

	}
	//****************************************
	public void showAttributesLists()
	{
		int j;
		for (int attrIndex=0; attrIndex<trainAttrNumber; attrIndex++)
		{
			System.out.println("** Attribute List *********************************************************");
			System.out.println("Attr. name: " + "Attr type: " + attributesLists[attrIndex].getAttrType() + ", AttrAttr. list index: " + attrIndex +  ", Listed attr. index: " + attributesLists[attrIndex].getAttrIndex());
			System.out.println("---------------------------------------------------------------------------");

			int eventsAttrNumber=attributesLists[attrIndex].getEventsNumber();
			for (j=0; j<eventsAttrNumber; j++)
				System.out.println("value: " + attributesLists[attrIndex].getEventValue(j) + ", class list index: " + attributesLists[attrIndex].getAttrClassIndex(j) +  ", proxy ref: " + attributesLists[attrIndex].getAttrProxyIndex(j));

			System.out.println("--- Proxies ---------------------------------------------------------------");

			for (j=0; j<attributesLists[attrIndex].getProxiesNumber(); j++)
				System.out.println("proxy index: " + j + ", value: " + attributesLists[attrIndex].getAttrProxyValue(j) + ", gini index: " + attributesLists[attrIndex].getAttrProxyGiniIndex(j));

			System.out.println("---------------------------------------------------------------------------");
			System.out.println("");
		}
		System.out.println("");
		System.out.println("** Class List *********************************************************");
		System.out.println("---------------------------------------------------------------------------");

		for (j=0; j<trainSetSize; j++)
			System.out.println("class index: " + classList.getClassIndex(j) + ", class label: " + classList.getClassLabel(j) + ", proxy index: " + classList.getClassProxyIndex(j) + ", leaf: " + classList.getClassLeafRef(j) + ", purity: " + classList.getClassLeafPurity(j));

		System.out.println("--- Proxies ---------------------------------------------------------------");

		for (j=0; j<classList.getProxiesNumber(); j++)
			System.out.println("proxy index: " + j + ", value: " + classList.getClassProxy(j));

	}
//	*********************************************
}
