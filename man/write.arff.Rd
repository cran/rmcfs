\name{write.arff}
\alias{write.arff}
\title{Writes data to ARFF}
\description{
  Exports data into ARFF format. This format is used by Weka 
  Data Mining software \url{http://www.cs.waikato.ac.nz/ml/weka/}.
}
\usage{write.arff(x, file = "", target = NA, chunk_size=100000, zip = FALSE)}
\arguments{
  \item{x}{data frame with data}
  \item{file}{exported filename}
  \item{target}{sets target attribute in ARFF format. Default value is NA what refers to the last column.}
  \item{chunk_size}{it defines size of chunk (number of cells) that are processed and exported. The bigger the value, the function is faster for small data and slower for big data.}
  \item{zip}{whether to create zip archive.}
}

\examples{

  # create artificial data
  adata <- artificial.data(rnd_features = 1000)
  
  #Fix input data to be consistent with ARFF and ADX formats. 
  #It is not necessary but for some data can help to export in proper format.
  adata <- fix.data(adata)
  write.arff(adata, file = file.path(tempdir(), "adata.arff"), target = "class")
}