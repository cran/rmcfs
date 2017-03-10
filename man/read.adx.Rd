\name{read.adx}
\alias{read.adx}
\title{Reads data from ADX}
\description{
  Imports data from ADX format.
}
\usage{read.adx(file = "")}
\arguments{
  \item{file}{exported filename}
}

\examples{
  # Load rmcfs library.
  library(rmcfs)
  
  # Load alizadeh dataset.
  data(alizadeh)
  d <- alizadeh
  
  write.adx(d, file = file.path(tempdir(), "alizadeh.adx"), target = "class")
  d <- read.adx(file = file.path(tempdir(), "alizadeh.adx"))
}