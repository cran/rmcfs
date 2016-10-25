\name{mcfs}
\alias{mcfs}
\title{MCFS-ID (Monte Carlo Feature Selection and Interdependency Discovery)}
\description{
Performs Monte Carlo Feature Selection (MCFS-ID) on a given data set. The data set
should define a classification problem with discrete/nominal class labels. This
function returns features sorted by RI as well as cutoff value, ID-Graph edges that denote interdependencies (ID), evaluation of top features and other statistics.
For a detailed description of the MCFS-ID algorithm see citation below. If you want to use dmLab or MCFS-ID in your publication, please cite the paper:

M.Draminski, A.Rada-Iglesias, S.Enroth, C.Wadelius, J. Koronacki, J.Komorowski, 'Monte Carlo feature selection for supervised classification', BIOINFORMATICS 24(1): 110-117 (2008).

}

\usage{
mcfs(formula, data,
    projections = 3000,
    projectionSize = 0.05,
    splits = 5,
    splitRatio = 0.66,
    balanceRatio = 1,
    splitSetSize = 1000,
    cutoffPermutations = 20,
    cutoffMethod = c("permutations", "criticalAngle", "kmeans", "mean"),
    buildID = TRUE,
    finalRuleset = TRUE,
    finalCV = TRUE,
    finalCVSetSize = 1000,
    finalCVRepetitions = 3,
    u = 1,
    v = 1,
    seed = NA,
    threadsNumber = 2)
}
\arguments{
  \item{formula}{specifies decision attribute and relation between class and other attributes (e.g. \code{class~.}). The target attribute can be nominal (then MCFS-ID uses decision tree) or numerical (then MCFS-ID uses regression tree).}
  \item{data}{defines input \emph{data.frame} containing all features with decision attribute included. This data.frame must contain proper types of columns. Columns \code{character}, \code{factor}, \code{Date}, \code{POSIXct}, \code{POSIXt} are treated as nominal/categorical and remaining columns as numerical/continuous. Decision attribute defined by \code{formula} can be nominal or numerical.}
  \item{projections}{defines the number of subsets (projections) with randomly selected features. This parameter is usually set to a few thousands and is denoted in the paper as \emph{s}.}
  \item{projectionSize}{defines the number of features in one subset. It can be defined by an absolute value (e.g. 100 denotes 100 randomly selected features) or by a fraction of input attributes (e.g. 0.05 denotes 5\% of input features). This parameter is denoted in the paper as \emph{m} and by default is set to 5\%. Minimum number of features in one subset is 2.}
  \item{splits}{defines the number of splits of each subset. This parameter is denoted in the paper as \emph{t}.}
  \item{splitRatio}{defines the size of the training set as a fraction of objects in the input subset.}
  \item{balanceRatio}{determines the way to balance classes. It should be set to 2 or higher if input dataset contains heavily unbalanced classes. Each subset \emph{s} will contain all the objects from the least frequent class and randomly selected set of objects from each of the remaining classes. This option helps to select features that are important for discovering a relatively rare class. The parameter defines the maximal imbalance ratio. If the ratio is set to 2, then subset \emph{s} will contain the number of objects from each class (but the least frequent one) proportional to the square root of the class size \eqn{size(c)^{1/2}}. If \code{balanceRatio = 0} then balancing is turned off. If \code{balanceRatio = 1} it is on but does not change the size of classes. Default value is 1.}
  \item{splitSetSize}{determines whether to limit input dataset size. It helps to speedup computation for data sets with a large number of objects. If the parameter is larger than 1, it determines the number of objects that are drawn at random for each of the \eqn{s \cdot t} decision trees. If \code{splitSetSize = 0} then the mcfs uses all objects in each iteration.}
  \item{cutoffPermutations}{determines the number of permutation runs. It needs at least 20 permutations (\code{cutoffPermutations = 20}) for a statistically significant result. Minimum value of this parameter is 3, however if it is 0 then permutations method is turned off.}
 \item{cutoffMethod}{determines the final cutoff method. Default value is 'permutations'.
 The methods of finding cutoff value between important and unimportant attributes are the following:
    \itemize{
      \item\code{mean} - cutoff value is set on mean values obtained from all the implemented methods.
      \item\code{kmeans} - the method is based on clustering the RI values into two clusters by the k-means algorithm. It sets the cutoff where the two clusters are separated. This method is quite valuable when data contains a subset of very informative features.
      \item\code{criticalAngle} - critical angle method is based on the plot of the features' RIs in decreasing order of size, with the corresponding features equally spaced along the abscissa. The plot can be seen as piecewise linear function, where each linear segment joins two neighboring RIs. Roughly speaking, the cutoff (placed on the abscissa) corresponds to this point on the plot where the slope of consecutive segments changes significantly.
      \item\code{permutations} - the method consists in permuting the decision attribute at least 20 times and running the mcfs algorithm for each permutation. The set of the maximal RIs from all these experiments is assumed approximately normally distributed and a critical value based on the the one-sided (upper-tailed) Student's t-test (at 95\% significance level) is provided. A feature is declared informative if its RI in the original ranking (without any permutation) exceeds the obtained critical value. A more detailed description of this method is included in the paper.
      }}
  \item{buildID}{if \code{= TRUE}, Interdependencies Discovery is on and all ID-Graph edges are collected.}
  \item{finalRuleset}{if \code{= TRUE}, classification rules (by \emph{ripper} algorithm) are created on the basis of the final set of features.}
  \item{finalCV}{if \code{= TRUE}, it runs cross validation (cv) experiments on the final set of features. The following set of classifiers is used: C4.5, NB, SVM, kNN, logistic regression and Ripper.}
  \item{finalCVSetSize}{limits the number of objects used in the final cv experiment. For each cv repetition, the objects are selected randomly from the uniform distribution.}
  \item{finalCVRepetitions}{defines the number of repetitions of the cv experiment. The more repetitions, the more stable result.}
  \item{u}{exponent for scaling the influence of tree accuracy on the RI of a feature (see the paper). By taking u = 2, trees with low accuracy are penalized more severely than when taking u = 1. Highly recommended is the default value.}
  \item{v}{exponent for scaling the influence of a number of samples in a node on the RI of a feature (see the paper). By taking v = 2, nodes with smaller number of examples are penalized more severely than when taking v = 1. Highly recommended is the default value.}
  \item{seed}{seed for random number generator in Java. By default the seed is random. Replication of the result is possible only if \code{threadsNumber = 1}.}
  \item{threadsNumber}{number of threads to use in computation. More threads needs more CPU cores as well as memory usage is a bit higher. It is recommended to set this value equal to or less than CPU available cores.}
}

\value{
  \item{target}{decision attribute name.}
  \item{RI}{\emph{data.frame} that contains all features with relevance scores sorted from the most relevant to the least relevant. This is the ranking of features.}
  \item{ID}{\emph{data.frame} that contains features interdependencies as graph edges. It can be converted into a graph object by \code{\link{build.idgraph}} function.}
  \item{distances}{\emph{data.frame} that contains convergence statistics of subsequent projections.}
  \item{cmatrix}{confusion matrix obtained from all \eqn{s \cdot t} decision trees.}
  \item{cutoff}{\emph{data.frame} that contains cutoff values obtained by the  following methods: mean, kmeans, criticalAngle, permutations (max RI). Disregard the forth value (contrastAttributes), since its calculation is not fully developed.}
 \item{cutoff_value}{the number of features chosen as informative by the method defined by parameter \code{cutoffMethod}.}
 \item{cv_accuracy}{\emph{data.frame} that contains classification results obtained by cross validation performed on \code{cutoff_value} features. This \emph{data.frame} exists if \code{finalCV = T}.} 
 \item{permutations}{this \emph{data.frame} contains the following results of permutation experiments: 
 \itemize{
 \item\code{perm_x} all RI values obtained from all permutation experiments;
 \item\code{RI_norm} RI obtained for reference MCFS experiment (i.e, the experiment on the original data); p-values from Anderson-Darling normality test applied separately for each feature to the \code{cutoffPermutations} RI set;
 \item\code{t_test_p} \eqn{p}-values from Student-t test applied separately for each feature to the \code{cutoffPermutations} RI vs. reference RI. This \emph{data.frame} exists if parameter \code{cutoffPermutations > 0}.}}
 \item{jrip}{classification rules produced by \emph{ripper} algorithm and related cross validation result obtained for top features.}
 \item{exec_time}{execution time of MCFS-ID.}
}

\examples{
  ####################################
  ######### Artificial data ##########
  ####################################
  
  ### Set up java parameter and load rmcfs package
  options(java.parameters = "-Xmx4g")
  library(rmcfs)

  # create input data
  adata <- artificial.data(rnd.features = 10)
  showme(adata)
  
  result <- mcfs(class~., adata, projections = 100, projectionSize = 4, 
                 cutoffPermutations = 3, finalCV = FALSE, finalRuleset = TRUE, 
                 threadsNumber = 2)

  # Print basic information about mcfs result.
  print(result)
  names(result)

  # Review cutoff values for all methods
  print(result$cutoff)
  
  # Review cutoff value used in plots
  print(result$cutoff_value)
  
  # Plot & print out distances between subsequent projections. 
  # These are convergence MCFS-ID statistics.
  plot(result, type="distances")
  print(result$distances)
  
  # Plot & print out 50 most important features.
  plot(result, type="ri", size = 50)
  # Show max RI values from permutation experiment.
  plot(result, type = "ri", size = 50, plot_permutations = TRUE)
  print(head(result$RI, 50))
  
  # Plot & print out 50 strongest feature interdependencies.
  plot(result, type = "id", size = 50)
  print(head(result$ID, 50))
  
  # Plot features ordered by RI_norm. Parameter 'size' is the number of 
  # top features in the chart. We set this parameter a bit larger than cutoff_value.
  plot(result, type = "features", size = result$cutoff_value * 1.1, cex = 1)
  # Here we set 'size' at fixed value 10.
  plot(result, type = "features", size = 10)
  
  # Plot cv classification result obtained on top features.
  # In the middle of x axis red label denotes cutoff_value.
  # plot(result, type = "cv", measure = "wacc", cex = 0.8)
  
  # Plot & print out confusion matrix. This matrix is the result of 
  # all classifications performed by all decision trees on all s*t datasets.
  plot(result, type = "cmatrix")
  
  # build interdependencies graph (all default parameters).
  gid <- build.idgraph(result)
  plot(gid)
  
  # build interdependencies graph for top 6 features 
  # and top 12 interdependencies and plot all nodes
  gid <- build.idgraph(result, size = 6, size_ID = 12, plot_all_nodes = TRUE)
  plot(gid, label.dist = 1)

  # Export graph to graphML (XML structure)
  path <- tempdir()
  igraph::write.graph(gid, file = paste0(path, "/artificial.graphml"), 
              format = "graphml", prefixAttr = FALSE)
  
  # Export and import results to/from csv files
  export.result(result, path = path, label = "artificial", save.rds = FALSE)
  result <- import.result(path = path, label = "artificial")

  \dontrun{
  ####################################
  ########## Alizadeh data ###########
  ####################################
  # Load Alizadeh dataset.
  data(alizadeh)
  showme(alizadeh)
  
  # Fix data types and data values - replace characters such as "," " " "/" etc. 
  # from values and column names and fix data types
  # This function may help if mcfs has any problems with input data
  alizadeh <- fix.data(alizadeh)
  
  # Parametrize and run MCFS-ID procedure, projectionSize (m) is set at 5 percent 
  # of input columns. For larger data (thousands of features) default settings are good enough.
  # This example may take few minutes but this one is a real dataset
  result <- mcfs(class~., alizadeh, projections = 2000, projectionSize = 0.05, 
                 cutoffPermutations = 10, finalCV = TRUE, finalRuleset = TRUE,
                 threadsNumber = 4)
  
  # Print basic information about mcfs result.
  print(result)
  
  # Plot & print out distances between subsequent projections. 
  plot(result, type="distances")
  
  # Show max RI values from permutation experiment.
  plot(result, type = "ri", size = 500, plot_permutations = TRUE)
  
  # build interdependencies graph.
  gid <- build.idgraph(result, size = 20)
  plot.idgraph(gid, label.dist = 0.3)
  
  # Plot cv classification result obtained on top features.
  # In the middle of x axis red label denotes cutoff_value.
  plot(result, type = "cv", measure = "wacc", cex = 0.8)
  }
}
