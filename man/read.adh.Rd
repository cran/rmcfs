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

  # create artificial data
  adata <- artificial.data(rnd_features = 1000)

  write.adh(adata, file = file.path(tempdir(), "adata.adh"), target = "class")
  adata <- read.adh(file = file.path(tempdir(), "adata.adh"))
}