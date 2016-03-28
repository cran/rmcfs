\name{print.mcfs}
\alias{print.mcfs}
\title{Prints mcfs result}
\description{
  Prints basic information about the MCFS-ID result: top features, cutoff values, confusion matrix obtained for \eqn{s \cdot t} trees and classification rules obtained by \emph{Ripper} (\emph{jrip}) algorithm.
}
\usage{
  \method{print}{mcfs}(x, \dots)}
\arguments{
  \item{x}{'mcfs' object - result of the MCFS-ID experiment returned by \code{\link{mcfs}} function.}
  \item{...}{additional printing parameters.}
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

  # Print basic information about mcfs result.
  print(result)
}