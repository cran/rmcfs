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
package dmLab.discretizer.chiMerge;

public class ChiMerge
{
	protected int numberOfValues;
	protected int groupsTable[];
	protected int currentGroupsNumber;
	protected float couplesTable[][];
	protected float minChi;
	protected int minChiPointer;
	//  *******************************************************
	public float getMinChi()
	{
		return minChi;
	}
	//  *******************************************************
	//  **** this is engine of chi merge algorithm
	public ChiMerge(float attributeArray[])
	{
		numberOfValues=attributeArray.length;
		currentGroupsNumber=numberOfValues;
		groupsTable = new int[numberOfValues];//it will be for captions of groups
		couplesTable = new float[numberOfValues-1][3];        //I have to create table for couples and calculations.
		for(int i=0;i<numberOfValues;i++)
			groupsTable[i]=i; //for start I have as many groups as values
	}
	//  *******************************************************
	//  **** method creates Couples of values
	public void createCouples()
	{
		int CouplesTablePointer=0;
		int PreviousValue=0;

		couplesTable[CouplesTablePointer][0]=groupsTable[0];
		PreviousValue=groupsTable[0];
		for(int i=1;i<numberOfValues;i++)
		{
			if(PreviousValue!=groupsTable[i])
				couplesTable[CouplesTablePointer++][1]=groupsTable[i];

			if(CouplesTablePointer<numberOfValues-1)
			{
				couplesTable[CouplesTablePointer][0]=groupsTable[i];
				PreviousValue=groupsTable[i];
			}
		}
		currentGroupsNumber=CouplesTablePointer;
	}
	//  *******************************************************
	//  ***** method calculates chi2
	public float calculateChi2(int NumberOfPositives[],int NumberOfNegatives[])
	{
		float PositivesForFirstGroup,NegativesForFirstGroup;
		float PositivesForSecondGroup,NegativesForSecondGroup;
		float e1First;
		float e0First;
		float e1Second;
		float e0Second;
		float all=0;
		minChi=1;
		for(int i=0;i<currentGroupsNumber;i++)//for each couple
		{
			PositivesForFirstGroup=0;
			NegativesForFirstGroup=0;
			PositivesForSecondGroup=0;
			NegativesForSecondGroup=0;
			couplesTable[i][2]=0;
			for(int j=0;j<numberOfValues;j++)
			{
				if(groupsTable[j]==couplesTable[i][0])
				{
					PositivesForFirstGroup+=NumberOfPositives[j];
					NegativesForFirstGroup+=NumberOfNegatives[j];
				}
				else
					if(groupsTable[j]==couplesTable[i][1])
					{
						PositivesForSecondGroup+=NumberOfPositives[j];
						NegativesForSecondGroup+=NumberOfNegatives[j];
					}
					else
						if(groupsTable[j] > couplesTable[i][1])
							break;
			}
			all = PositivesForFirstGroup+NegativesForFirstGroup+PositivesForSecondGroup+NegativesForSecondGroup;
			e1First = (PositivesForFirstGroup+NegativesForFirstGroup)*(PositivesForFirstGroup+PositivesForSecondGroup)/all;
			if(e1First==0) e1First=0.1f;
			e0First = (PositivesForFirstGroup+NegativesForFirstGroup)*(NegativesForFirstGroup+NegativesForSecondGroup)/all;
			if(e0First==0) e0First=0.1f;
			e1Second = (PositivesForSecondGroup+NegativesForSecondGroup)*(PositivesForFirstGroup+PositivesForSecondGroup)/all;
			if(e1Second==0) e1Second=0.1f;
			e0Second = (PositivesForSecondGroup+NegativesForSecondGroup)*(NegativesForFirstGroup+NegativesForSecondGroup)/all;
			if(e0Second==0) e0Second=0.1f;

			couplesTable[i][2]+= Math.pow(PositivesForFirstGroup-e1First,2.0)/e1First;
			couplesTable[i][2]+= Math.pow(NegativesForFirstGroup-e0First,2.0)/e0First;
			couplesTable[i][2]+= Math.pow(PositivesForSecondGroup-e1Second,2.0)/e1Second;
			couplesTable[i][2]+= Math.pow(NegativesForSecondGroup-e0Second,2.0)/e0Second;
			if(couplesTable[i][2] < minChi)
			{
				minChi=couplesTable[i][2];
				minChiPointer=i;
			}
		}
		return minChi;
	}
	//  *******************************************************
	//  ***** method merges two groups into one
	public int defineNewGroup()
	{
		for(int j=0;j<numberOfValues;j++)
		{
			if(groupsTable[j]==(int)couplesTable[minChiPointer][1])
			{
				groupsTable[j]=(int)couplesTable[minChiPointer][0];//adds two groups
			}
			else
				if(groupsTable[j]>couplesTable[minChiPointer][1])
					break; //for performance becouse GroupsTable is sorted
		}
		return --currentGroupsNumber;//one group less
	}
	//  *******************************************************
	//  method Sets final Ranges
	public float[] setRanges(float[] discTable, float[] attributeArray)
	{
		int prevNumber = 0;
		int whichRange=0;
		for(int i=0;i<numberOfValues;i++)
		{
			if(groupsTable[i]!=prevNumber)
			{
				//discTable[whichRange++] = attributeArray[i-1];
				discTable[whichRange++] = (attributeArray[i] + attributeArray[i-1])/2;

				prevNumber=groupsTable[i];
			}
		}
		return discTable;
	}
	//  *******************************************************

}
