\name{export.plots}
\alias{export.plots}
\title{Exports MCFS-ID result plots}
\description{
  Saves all MCFS-ID result plots in the specified directory.
}

\usage{export.plots(mcfs_result, data = NULL, idgraph = NULL, 
                    path, label = "mcfs", color = "darkred",
                    size = NA, image_width = 8, image_height = 6, 
                    plot_format = c("pdf","svg","png"), cex = 1)}
\arguments{

  \item{mcfs_result}{result from \code{\link{mcfs}} function.}
  \item{data}{input data frame used to produce \code{mcfs_result}.}
  \item{idgraph}{\emph{idgraph/igraph} S3 object representing feature interdependencies. This object is produced by \code{\link{build.idgraph}} function.}
  \item{path}{path to the where plot files should be saved.}
  \item{label}{a common prefix label of all plot files.}
  \item{color}{it defines main color of all plots.}
  \item{size}{number of features to plot.}
  \item{image_width}{width of plots (in inches).}
  \item{image_height}{height of plots (in inches).}
  \item{plot_format}{image format of plot files - one of the following: "pdf","svg","png".}
  \item{cex}{size of fonts.}
}

\examples{
  \dontrun{###dontrunbegin
  
  # create input data
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 50,
                  finalCV = FALSE, finalRuleset = FALSE, threadsNumber = 2)
  
  # build interdependencies graph for top 6 features 
  # and top 12 interdependencies and plot all nodes
  gid <- build.idgraph(result, size = 6, size_ID = 12, orphan_nodes = TRUE)

  #export plot files
  export.plots(result, adata, idgraph = gid, path = tempdir(), label = "mcfs", color = "darkgreen")

  }###dontrunend
}
