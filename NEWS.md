# rmcfs - changes

Version 1.3.6
=============
* iGraph >2.0.3 compatibility updates 
* various memory optimization changes (mode=2 OutOfMemoryError fixed)
* reference links to publication updated

Version 1.3.5
=============
* fixed a java 17 compatibility issue

Version 1.3.4
=============
* fixed a nullpointerexception for numeric target variable
* fixed a buildId param setting (always True)
* fixed a splitSetSize param setting for numeric target variable

Version 1.3.3
=============
* Extreme speedup and memory optimization for the data with large number of rows
* mcfs help Alizadeh data link updated

Version 1.3.2
=============
* closeAllConnections() removed from the code
* final data pruning is processed on R side (memory optimization)

Version 1.3.1
=============
* fixed an issue in fix.data() - function works faster
* fixed an issue with tempdir on some old R versions and OS

Version 1.3.0
=============
* two-stage feature selection implemented
* function mcfs() has new parameter mode - if mode = 2 mcfs runs 2 stage filtering that is much faster and gives more detailed ID-Graphs
* two-stage feature selection - rankings from both phases are saved () and combined together to create the final one
* in function mcfs there is a new cutoff method cutoffMethod - if cutoffMethod = "contrast" then cutoff value is determined based on contrast attributes
* mcfs now supports weighting of input attributes - new parameter 'attrWeights' in mcfs()
* read/write to/from adx/adh now supports data.frame attributes: 'attr_weights' and 'target'
* in result$RI data.frame column 'RI_norm' is replaced by 'RI'
* all RI data.frames contains normalized values of the following columns: 'RI', 'classifiers' and 'nodes'
* plots 'ri', 'id', 'heatmap', 'cmatrix', 'features' support color parameter
* plot.idgraph supports color parameter
* plots: 'features' and 'cv' are created in ggplot2 by default
* new function export.plots()
* in functions 'import.result()' and 'export.result()' - path can now define full path to the zip result file and label can be set to NA
* function export.result() saves the ranking in original order (as it is in input data)
* updated vignette file to be consistent with JSS article
* function build.idgraph() works much faster now
* fix.data() works much faster now
* data export/import are improved
* some default parameters changed to speed up calculations: featureFreq, splitSetSize, threadsNumber
* removed 'progressInterval' parameter
* removed 'finalCVRepetitions' parameter
* various minor fixes in plots for some extreme situations
* minor fix in internal function drop.file.extension()
* minor fix in recognition of JAVA version (version 9/10)
* minor fixes in plots for numeric decision MCFS result
* minor fix in adx/adh export for big data
* rmcfs is working on JAVA 7 and above
* dmLab.jar version 2.3.0

Version 1.2.15
=============
* fixed an issue for Java 11

Version 1.2.14
=============
* updated onLoad info

Version 1.2.13
=============
* updated vignette

Version 1.2.12
=============
* updated vignette and citation info to JSS article

Version 1.2.11
=============
* function build.idgraph() has a new parameter 'outer_ID'
* in function build.idgraph() parameter 'plot_all_nodes' is now 'orphan_nodes'
* fix and efficiency improvement in importing ID-graph from file

Version 1.2.10
=============
* fix for Open JDK 9

Version 1.2.9
=============
* Java version in description file fixed
* minor fix of input parameter 'seed'
* onLoad info changed 

Version 1.2.8
=============
* vignette - bibliography file removed

Version 1.2.7
=============
* mcfs check whether decision attribute contains NA values
* alizadeh dataset has been removed from the package and can be downloaded from the internet
* function refine.data() is now prune.data()
* new vignette article that has been accepted for publication in Jourlan of Statistical Software

Version 1.2.6
=============
* function filter.data() is now refine.data()
* testthat environment added

Version 1.2.5
=============
* function import.result() is fixed for empty results
* functions import.result() and export.result() have default value for parameer: path = "./"
* heatmaps are more readable now
* parameter projectionSize by default is set on sqrt(d)
* parameter featureFreq by default is set on 150

Version 1.2.4
=============
* minor fix in processing temporary paths

Version 1.2.3
=============
* minor fix in cleaning temporary files

Version 1.2.2
=============
* minor fix in uncompression input zip files

Version 1.2.1
=============
* new functions write.adh() and read.adh()
* functions read.adx() and read.adh() can read zipped files
* functions write.adx(), write.arff(), write.adh() have new parameter 'zip' and they may produce zipped data
* in plot(type = 'features') by default 'size = NA' and it means 'cutoff_value' * 1.2 features
* in plot(type = 'ri') and plot(type = 'id') by default 'size = NA' and it means 'cutoff_value' * 10 features
* few minor fixes
* dmLab.jar version 2.2.1

Version 1.2.0
=============
* new featureFreq parameter - it determines how many times each input feature must be randomly selected when projections = 'auto'.
* parameter 'balanceRatio' is now 'balance'
* parameters balance, projectionSize, projections are set on 'auto' value by default
* in function plot.mcfs() has new updated parameters: plot_permutations, plot_diff_bars, cv_measure
* in function plot.idgraph() parameter 'label.dist' is now 'label_dist'
* in function write.adx() parameter 'chunk.size' is now 'chunk_size'
* in function write.arff() parameter 'chunk.size' is now 'chunk_size'
* in function fix.data() parameter 'source.chars' is now 'source_chars', 'destination.char'->'destination_char', 'numeric.class'->'numeric_class', 'nominal.class'->'nominal_class'
* in function export.result() parameter 'save.rds' is now 'save_rds'
* in function artificial.data() parameter 'rnd.features' is now 'rnd_features'
* new function read.adx()
* fixed some small problems when important features set is empty
* mcfs_result contains input data data.frame
* new plot type: plot(type='heatmap')
* import and export of zipped result
* dmLab.jar version 2.2.0

Version 1.1.2
=============
* MCFS-ID works on numeric target (new classifier M5 (regression tree) is implemented in MCFS-ID)
* fix.data replaces value '?' by NA
* fix in write.arff
* dmLab.jar version 2.1.2

Version 1.1.1
=============
* function info() is now showme()
* minor fixes
* dmLab.jar version 2.1.1

Version 1.1.0
=============
* first official CRAN release
* functionality of the package is highly simplified - available 12 basic functions
* new names of functions: save.result()/read.result() are now export.result()/import.result(); build.ID.graph() is now build.idgraph()
* removed useles prefix 'mcfs.' from input params in function mcfs()
* function fix.data() combines all functionality of: fix.data.values(), fix.data.names() and fix.data.types()
* function mcfc() returns 'mcfs' object - one plot() function (parameter 'type') and one print() function for 'mcfs' object
* function build.idgraph() returns 'idgraph' object - new plot() function for 'idgraph' object
* function plot(type="ri") implements plot.permutations functionality - now it shows maxRI values(if 'plot_permutations' = TRUE)
* fixed margins in plot.idgraph() - now idgraph uses entire plot space
* removed curved_edges param from plot.idgraph() - curved_edges are always on
* plot.idgraph() has new parameter label.dist() that defines distance of labels to corresponding nodes
* build.idgraph() implements get.min.ID() functionality - get.min.ID() is not visible
* new function 'artificial.data(1000)' that creates example data used in JSS paper
* many minor fixes to meet CRAN rules
* RD files updated by artificial.data example
* function mcfc() has new seed parameter - now it is possible to replicate the result
* dmLab.jar version 2.1.0

Version 1.0.6
=============
* function write.adx is reiplemented and now it uses a smart exporting (chunk based) for huge datasets
* function write.arff is reiplemented and now it uses a smart exporting (chunk based) for huge datasets
* function info() extended and changed - better for huge data.frames
* function fix.data.names added (colNames are cleaned from various unwanted chars e.g. "|", "#", ",")
* fix.data.types and fix.data.values works much faster on huge data
* fixed plot.distances (x axis shows correct values projections(s))
* dmLab.jar version 2.0.6

Version 1.0.5
=============
* useless parameter 'iType' is removed from plot.ID.graph function
* in mcfs function removed parameter 'splitSetSizeLimit' splitSetSize does the job
* new parameter in mcfs function 'cutoffMethod'
* fix of helps (mcfs, build.ID.graph, plot.ID.graph)
* running mcfs on one class data is not allowed
* function 'model.frame' replaced by faster and more stable own implementation
* updated and improved help \*.rd files
* dmLab.jar version 2.0.5

Version 1.0.4
=============
* new parameter in plot.permutations (parameter 'type')
* cleaning of temporary files after reading result by mcfs function
* dmLab.jar version 2.0.4

Version 1.0.3
=============
* parameters u & v are available in mcfs function

Version 1.0.2
=============
* new parameter in plot.ID.graph (curved.edges=T)
* refactoring: importances -> RI, interactions -> ID [plot.ID.graph, build.ID.graph, plot.RI, plot.ID]

Version 1.0.1
=============
* new function build.rules
* updated help \*.rd files (in mcfs introduced parameters s,t according to official papers about MCFS-ID)

Version 1.0.0
=============
* first version of rmcfs
