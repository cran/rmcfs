\name{export.result}
\alias{export.result}
\title{Saves MCFS-ID result into set csv files}
\description{
  Saves csv files with result obtained by the MCFS-ID.
}
\usage{export.result(mcfs_result, path = "./", label = "rmcfs", zip = TRUE)}
\arguments{  
  \item{mcfs_result}{result of the MCFS-ID experiment returned by \code{\link{mcfs}} function.}
  \item{path}{path to the MCFS-ID result *.csv files.}
    \item{label}{label of the experiment and common name for output files.}
  \item{zip}{if \code{= TRUE}, saves all results data as one zip file.}
}

\examples{
  \dontrun{###dontrunbegin
  
  # create input data
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 10,
                  finalCV = FALSE, finalRuleset = FALSE, threadsNumber = 2)

  # Export and import R result to/from files
  path <- tempdir()
  export.result(result, path = path, label = "artificial")
  result <- import.result(path = path, label = "artificial")
  
  }###dontrunend
}
