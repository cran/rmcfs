\name{info}
\alias{info}
\title{Basic data information}
\description{
	Prints basic information about the data.frame.
}

\usage{info(x, size = 10, show = c("cells", "head", "tail", "none"))}
\arguments{
  \item{x}{input data frame.}
  \item{size}{number of rows/columns to be printed.}
  \item{show}{parameters that controls print content.
  \itemize{
      \item\code{cells} - shows top left and bottom right cells (size of both subsets is controlled by \code{size} parameter)
      \item\code{head} - shows top \code{size} rows
      \item\code{tail} - shows bottom \code{size} rows
      \item\code{none} - does not show the content
  }}
}

\examples{
  # Load rmcfs library.
  library(rmcfs)
  
  # Load alizadeh dataset.
  data(alizadeh)
  info(alizadeh)
}
