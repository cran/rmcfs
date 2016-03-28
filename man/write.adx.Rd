\name{write.adx}
\alias{write.adx}
\title{Writes data to ADX}
\description{
  Exports data into ADX format.
}
\usage{write.adx(x, file="", target = NA, chunk.size=100000)}
\arguments{
  \item{x}{data frame with data}
  \item{file}{exported filename}
  \item{target}{sets target attribute in ADX format. Default value is NA what refers to the last column.}
  \item{chunk.size}{defines size of chunk (number of cells) that are processed and exported. The bigger the value, the function is faster for small data and slower for big data.}
}

\examples{
  # Load rmcfs library.
  library(rmcfs)
  
  # Load alizadeh dataset.
  data(alizadeh)
  d <- alizadeh
  
  #Fix input data to be consistent with ARFF and ADX formats. 
  #It is not necessary but for some data can help to export in proper format.
  d <- fix.data(d)
  write.adx(d, file = paste0(tempdir(), "/alizadeh.adx"), target = "class")
}