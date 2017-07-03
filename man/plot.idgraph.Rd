\name{plot.idgraph}
\alias{plot.idgraph}
\title{Plots interdependencies graph}
\description{
  Invokes \emph{plot.igraph} with predefined parameters to visualize interdependencies
  graph (ID-Graph). Standard \code{plot} function with custom parameters may be used instead of this one.
}
\usage{
  \method{plot}{idgraph}(x, 
          label_dist = 0.5, 
          cex = 1, \dots)}
\arguments{
  \item{x}{\emph{idgraph/igraph} S3 object representing feature interdependencies. This object is produced by \code{\link{build.idgraph}} function.}
  \item{label_dist}{space between the node's label and the corresponding node in the plot.}
  \item{cex}{size of fonts.}
  \item{...}{additional plotting parameters.}
}

\examples{
  \dontrun{
  # create input data
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 50,
                  finalCV = FALSE, finalRuleset = FALSE, threadsNumber = 2)

  # build interdependencies graph for top 6 features 
  # and top 12 interdependencies and plot all nodes
  gid <- build.idgraph(result, size = 6, size_ID = 12, plot_all_nodes = TRUE)
  plot(gid, label_dist = 1)
  
  }
}
