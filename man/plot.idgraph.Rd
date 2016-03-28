\name{plot.idgraph}
\alias{plot.idgraph}
\title{Plots interdependencies graph}
\description{
  Invokes \emph{plot.igraph} with predefined parameters to visualize interdependencies
  graph (ID-Graph). Standard \code{plot} function with custom parameters may be used instead of this one.
}
\usage{
  \method{plot}{idgraph}(x, 
          label.dist = 0.5, 
          cex = 1, \dots)}
\arguments{
  \item{x}{\emph{idgraph/igraph} S3 object representing feature interdependencies. This object is produced by \code{\link{build.idgraph}} function.}
  \item{label.dist}{space between the node's label and the corresponding node in the plot.}
  \item{cex}{size of fonts.}
  \item{...}{additional plotting parameters.}
}

\examples{
  ### Set up java parameter and load rmcfs package
  options(java.parameters = "-Xmx4g")
  library(rmcfs)
  
  # create input data
  adata <- artificial.data(rnd.features = 10)
  info(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, projections = 200, projectionSize = 4, 
                 cutoffPermutations = 5, finalCV = FALSE, finalRuleset = FALSE, 
                 threadsNumber = 2)

  # build interdependencies graph (all default parameters).
  gid <- build.idgraph(result)
  plot(gid)
  
  # build interdependencies graph for top 6 features 
  # and top 12 interdependencies and plot all nodes
  gid <- build.idgraph(result, size = 6, size_ID = 12, plot_all_nodes = TRUE)
  plot(gid, label.dist = 1)
}
