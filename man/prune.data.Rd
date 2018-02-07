\name{prune.data}
\alias{prune.data}
\title{Filters input data}
\description{
   Selects columns from input data based on the highest RIs of attributes.
}
\usage{prune.data(data, mcfs_result, size = NA)}
\arguments{
  \item{data}{input data.frame.}
  \item{mcfs_result}{result from \code{\link{mcfs}} function.}
  \item{size}{number of top features to select from input data. If \code{size = NA}, then it is defined by 
    \code{mcfs_result$cutoff_value} parameter.}
}
\value{data.frame with selected columns.}

\examples{
  \dontrun{###dontrunbegin

  # create input data
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 10,
                  finalCV = FALSE, finalRuleset = FALSE, threadsNumber = 2)

  head(prune.data(adata, result, size = result$cutoff_value))

  }###dontrunend  
}
