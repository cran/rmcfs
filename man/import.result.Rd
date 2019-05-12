\name{import.result}
\alias{import.result}
\title{Reads csv result files produced by the MCFS-ID Java module}
\description{
  Reads csv result files produced by the MCFS-ID Java module.
}
\usage{import.result(path = "./", label = NA)}
\arguments{  
  \item{path}{path to the MCFS-ID results files. This parameter can also point to the zip result file.}
  \item{label}{experiment label for results files (name of the data).}
}
\value{the result of the MCFS-ID experiment returned by \code{\link{mcfs}} function.}

\examples{
  \dontrun{###dontrunbegin

  # create input data
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 10,
                  finalCV = FALSE, finalRuleset = FALSE, threadsNumber = 2)

  # Export and import R result to/from files
  path <- file.path(tempdir(), "artificial.zip")
  export.result(result, path = path)
  result <- import.result(path = path)

  
  }###dontrunend
}
