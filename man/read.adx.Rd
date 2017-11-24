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

  # create artificial data
  adata <- artificial.data(rnd_features = 1000)
  
  write.adx(adata, file = file.path(tempdir(), "adata.adx"), target = "class")
  adata <- read.adx(file = file.path(tempdir(), "adata.adx"))
}