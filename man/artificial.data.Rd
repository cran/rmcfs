\name{artificial.data}
\alias{artificial.data}
\title{Creates artificial dataset}
\description{
	Creates \code{data.frame} with artificial data. The last six columns are nominal and highly correlated to feature 'class'. This data set consists of objects from 3 classes, \emph{A}, \emph{B} and \emph{C}, that contain 40, 20, 10 objects, respectively (70 objects altogether). For each object, 6 binary features (\emph{A1}, \emph{A2}, \emph{B1}, \emph{B2}, \emph{C1} and \emph{C2}) are created and they are 'ideally' or 'almost ideally' correlated with \emph{class} feature. If an object's \emph{'class'} equals \emph{'A'}, then its features \emph{A1} and \emph{A2} are set to class value \emph{'A'}; otherwise \emph{A1 = A2 = 0}. If an object's \emph{'class'} is \emph{'B'} or \emph{'C'}, the processing is analogous, but some random corruption is introduced. For 2 observations from class \emph{'B'} and both attributes \emph{B1/B2}, their values \emph{'B'} are replaced by '0'. For 4 observations from class \emph{'C'} and both attributes \emph{C1/C2}, their values \emph{'C'} are replaced by '0'. The number of corrupted values for each class is defined by \code{corruption} parameter. The data also contains additional \code{rnd_features = 500} random numerical features with uniformly [0,1] distributed values.
}

\usage{artificial.data(rnd_features = 500, size = c(40, 20, 10), 
                        corruption = c(0, 2, 4), seed = NA)}
\arguments{
  \item{rnd_features}{number of numerical random features.}
  \item{size}{size of classes \emph{A}, \emph{B}, and \emph{C}.}
  \item{corruption}{defines the number of corrupted values for a pairs of columns \emph{A1/A2}, \emph{B1/B2}, \emph{C1/C2},}
  \item{seed}{seed for random number generator.}
}

\value{data.frame with six important features.}

\examples{
  d <- artificial.data(rnd_features = 500)
  showme(d)
}
