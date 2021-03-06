\name{write.adx}
\alias{write.adx}
\title{Writes data to ADX}
\description{
  Exports data into ADX format.
}
\usage{write.adx(x, file = "", target = NA, chunk_size = 100000, zip = FALSE)}
\arguments{
  \item{x}{data frame with data}
  \item{file}{exported filename}
  \item{target}{sets target attribute in ADX format. Default value is NA what refers to the last column.}
  \item{chunk_size}{defines size of chunk (number of cells) that are processed and exported. The bigger the value, the function is faster for small data and slower for big data.}
  \item{zip}{whether to create zip archive.}
}

\examples{
  \dontrun{###dontrunbegin

  # create artificial data
  adata <- artificial.data(rnd_features = 1000)
  
  #Fix input data to be consistent with ARFF and ADX formats. 
  #It is not necessary but for some data can help to export in proper format.
  adata <- fix.data(adata)
  write.adx(adata, file = file.path(tempdir(), "adata.adx"), target = "class")
  
  }###dontrunend
}
