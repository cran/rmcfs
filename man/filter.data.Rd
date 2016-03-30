\name{filter.data}
\alias{filter.data}
\title{Filters input data}
\description{
   Selects columns from input data based on the highest RIs of attributes.
}
\usage{filter.data(data, mcfs_result, size = NA)}
\arguments{
  \item{data}{input data.frame.}
  \item{mcfs_result}{result from \code{\link{mcfs}} function.}
  \item{size}{number of top features to select from input data. If \code{size = NA}, then it is defined by 
    \code{mcfs_result$cutoff_value} parameter.}
}
\value{data.frame with selected columns.}

\examples{
  ### Set up java parameter and load rmcfs package
  options(java.parameters = "-Xmx4g")
  library(rmcfs)
  
  # create input data
  adata <- artificial.data(rnd.features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, projections = 100, projectionSize = 4, 
                 cutoffPermutations = 3, finalCV = FALSE, finalRuleset = FALSE, 
                 threadsNumber = 2)
  
  filter.data(adata, result, size = result$cutoff_value)
}