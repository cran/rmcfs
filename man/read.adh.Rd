\name{read.adh}
\alias{read.adh}
\title{Reads data from ADH}
\description{
  Imports data from ADH format. This format is based on two files: 'adh' that contains ADX header and 'csv' that contains the data.
}
\usage{read.adh(file = "")}
\arguments{
  \item{file}{exported filename}
}

\examples{
  # Load rmcfs library.
  library(rmcfs)
  
  # Load alizadeh dataset.
  data(alizadeh)
  d <- alizadeh
  
  write.adh(d, file = paste0(tempdir(), "/alizadeh.adh"), target = "class")
  d <- read.adh(file = paste0(tempdir(), "/alizadeh.adh"))
}