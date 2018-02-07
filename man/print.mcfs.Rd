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
  \dontrun{###dontrunbegin

  # create input data
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 10,
                  finalCV = FALSE, finalRuleset = TRUE, threadsNumber = 2)

  # Print basic information about mcfs result.
  print(result)
  
  }###dontrunend
}
