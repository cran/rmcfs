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
package dmLab.classifier.sliq.Tree;


public interface Const {

  boolean
      LEFT_CHILD             = true,
      RIGHT_CHILD            = false,
      NUMERIC_ATTR           = true,
      NOMINAL_ATTR           = false,
      NO_ATTR_TYPE           = false,
      ATTR_USED_IN_SPLIT     = true,
      ATTR_NOT_USED_IN_SPLIT = false,
      NO_CHILD_FOUND         = false,
      CHILD_ACCESSED         = true,
      LEAF                   = true,
      NODE                   = false,
      TREE_BUILT             = true,
      TREE_NOT_BUILT         = false,
      TREE_PRUNED            = true,
      TREE_NOT_PRUNED        = false
      ;

  int
      NO_SPLITTING_ATTR_INDEX  = -1,
      NO_EVENTS                = 0,
      NO_SPLITTING_VALUE_PROXY_INDEX = -1,
      NO_NODE_CLASS = -1,
      ATTR = 0,
      LN = 1
      ;

  float
      NO_NODE_CLASS_FREQUENCY  = -1,
      NO_DIVERSITY_MEASURRE    = -1,
      NO_GOODNESS_OF_SPLIT     = -1,
      NO_GINI                  = -1
      ;
}
