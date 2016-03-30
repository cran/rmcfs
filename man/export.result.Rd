\name{export.result}
\alias{export.result}
\title{Saves MCFS-ID result into set csv files}
\description{
  Saves csv files with result obtained by the MCFS-ID.
}
\usage{export.result(mcfs_result, path, label = "rmcfs", save.rds = FALSE)}
\arguments{  
  \item{mcfs_result}{result of the MCFS-ID experiment returned by \code{\link{mcfs}} function.}
  \item{path}{path to the MCFS-ID result *.csv files.}
    \item{label}{label of the experiment and common name for output files.}
  \item{save.rds}{if \code{= TRUE}, saves all results data as additional *.rds file.}
}

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

  # Export and import R result to/from files
  path <- tempdir()
  export.result(result, path = path, label = "artificial", save.rds = FALSE)
  result <- import.result(path = path, label = "artificial")
}


