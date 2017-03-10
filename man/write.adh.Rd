\name{write.adh}
\alias{write.adh}
\title{Writes data to ADH}
\description{
  Exports data into ADH format. This format is based on two files: 'adh' that contains ADX header and 'csv' that contains the data.
}
\usage{write.adh(x, file = "", target = NA, chunk_size = 100000, zip = FALSE)}
\arguments{
  \item{x}{data frame with data}
  \item{file}{exported filename}
  \item{target}{sets target attribute in ADH format. Default value is NA what refers to the last column.}
  \item{chunk_size}{defines size of chunk (number of cells) that are processed and exported. The bigger the value, the function is faster for small data and slower for big data.}
  \item{zip}{whether to create zip archive.}
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
  write.adh(d, file = file.path(tempdir(), "alizadeh.adh"), target = "class")
}