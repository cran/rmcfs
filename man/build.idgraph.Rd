\name{build.idgraph}
\alias{build.idgraph}
\title{Constructs interdependencies graph}
\description{
Constructs the ID-Graph (igraph/idgraph object) from \code{mcfs_result} object returned by \code{\link{mcfs}} function.
The number of top features included and the number of ID-Graph edges can be customized.
}

\usage{build.idgraph(mcfs_result, 
                      size = NA, 
                      size_ID = NA, 
                      self_ID = FALSE,
                      outer_ID = FALSE,
                      orphan_nodes = FALSE, 
                      size_ID_mult = 3, 
                      size_ID_max = 100)}
\arguments{
  \item{mcfs_result}{results returned by \code{\link{mcfs}} function.}
  \item{size}{number of top features to select. If \code{size = NA}, then \code{size} is defined by \code{mcfs_result$cutoff_value} parameter.}
  \item{size_ID}{number of interdependencies (edges in ID-Graph) to be included. If \code{size_ID = NA}, then parameter \code{size_ID} is defined by multiplication \code{size_ID_mult}*\code{size}.}
  \item{self_ID}{if \code{self_ID = TRUE}, then include self-loops from ID-Graph.}
  \item{outer_ID}{if \code{outer_ID = TRUE}, then include include all interactions between a feature from the top set features (defined by \code{size} parameter) with any other feature.}
  \item{orphan_nodes}{if \code{plot_all_nodes = TRUE}, then include all nodes, even if they are not connected to any other node (isolated nodes).}
  \item{size_ID_mult}{If \code{size_ID_mult = 3} there will be 3 times more edges than features (nodes) presented on the ID-Graph. It works only if \code{size = NA} and \code{size_ID = NA}}
  \item{size_ID_max}{maximum number of interactions to be included from ID-Graph (the upper limit).}
}

\value{\emph{igraph/idgraph} S3 object that can be: plotted in R, exported to graphML (XML format) or saved as csv or rds files.}

\examples{
  \dontrun{###dontrunbegin

  # create input data
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 50,
                  buildID = TRUE, finalCV = FALSE, finalRuleset = FALSE,
                  threadsNumber = 2)

  # build interdependencies graph for top 6 features 
  # and top 12 interdependencies and plot all nodes
  gid <- build.idgraph(result, size = 6, size_ID = 12, orphan_nodes = TRUE)
  plot(gid, label_dist = 1)

  # Export graph to graphML (XML structure)
  path <- tempdir()
  igraph::write.graph(gid, file = file.path(path, "artificial.graphml"), 
            format = "graphml", prefixAttr = FALSE)

  }###dontrunend
}
