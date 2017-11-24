\name{showme}
\alias{showme}
\title{Basic data information}
\description{
	Prints basic information about the data.frame.
}

\usage{showme(x, size = 10, show = c("tiles", "head", "tail", "none"))}
\arguments{
  \item{x}{input data frame.}
  \item{size}{number of rows/columns to be printed.}
  \item{show}{parameters that controls print content.
  \itemize{
      \item\code{tiles} - shows top left and bottom right cells (size of both subsets is controlled by \code{size} parameter)
      \item\code{head} - shows top \code{size} rows
      \item\code{tail} - shows bottom \code{size} rows
      \item\code{none} - does not show the content
  }}
}

\examples{

  # create artificial data
  adata <- artificial.data(rnd_features = 1000)
  showme(adata)
}
