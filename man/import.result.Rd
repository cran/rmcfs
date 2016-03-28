\name{import.result}
\alias{import.result}
\title{Reads csv result files produced by the MCFS-ID Java module}
\description{
  Reads csv result files produced by the MCFS-ID Java module.
}
\usage{import.result(path, label)}
\arguments{  
  \item{path}{path to the MCFS-ID results *.csv files.}
  \item{label}{experiment label for results files (name of the data).}
}
\value{the result of the MCFS-ID experiment returned by \code{\link{mcfs}} function.}

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

  # Export and import R result to/from files
  path <- tempdir()
  export.result(result, path = path, label = "artificial", save.rds = FALSE)
  result <- import.result(path = path, label = "artificial")
}



