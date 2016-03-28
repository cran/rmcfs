\name{plot.mcfs}
\alias{plot.mcfs}
\title{Plots various MCFS result components}
\description{
  Plots various aspects of the MCFS-ID result.
}
\usage{
  \method{plot}{mcfs}(x, type = c("ri", "id", "distances", "features", "cv", "cmatrix"), 
        size = NA, 
        plot_permutations = F, 
        measure = c("acc", "wacc"),
        l_margin = 10,
        cex = 1, \dots)}
\arguments{
    \item{x}{'mcfs' S3 object - result of the MCFS-ID experiment returned by \code{\link{mcfs}} function.}
    \item{type}{
    \itemize{
    \item\code{ri} {plots top features set with their RIs as well as max RI obtained from permutation experiments. Red color denotes important features.}
    \item\code{id} plots top ID values obtained from the MCFS-ID.
    \item\code{distances} plots distances (convergence diagnostics of the algorithm) between subsequent feature rankings obtained during the MCFS-ID experiment.
    \item\code{features} plots top features set along with their RI. It is a horizontal barplot that shows important features in red color and unimportant in grey.
    \item\code{cv} plots cross validation results based on top features.
    \item\code{cmatrix} plots the confusion matrix obtained on all \eqn{s \cdot t} trees.
  }}
  \item{size}{number of features to plot.}
  \item{plot_permutations}{if \code{type = "ri"} and \code{plot_permutations = T}, then it additionally shows horizontal lines that correspond to max RI values obtained from each single permutation experiment.}
  \item{measure}{if \code{type="cv"}, then it determines the type of accuracy shown in the plot: weighted or unweighted accuracy ("wacc" or "acc").}
  \item{cex}{size of fonts.}
  \item{l_margin}{if \code{type = "features"}, then it determines the size of the left margin of the plot.}
  \item{...}{additional plotting parameters.}
}

\examples{
  ### Set up java parameter and load rmcfs package
  options(java.parameters = "-Xmx4g")
  library(rmcfs)
  
  # create input data
  adata <- artificial.data(rnd.features = 10)
  info(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, projections = 300, projectionSize = 4, 
                 cutoffPermutations = 5, finalCV = TRUE, finalRuleset = TRUE, 
                 threadsNumber = 2)

  # Plot & print out distances between subsequent projections. 
  # These are convergence MCFS-ID statistics.
  plot(result, type="distances")
  print(result$distances)
  
  # Plot & print out 50 most important features.
  plot(result, type="ri", size = 50)
  # Show max RI values from permutation experiment.
  plot(result, type = "ri", size = 50, plot_permutations = TRUE)
  print(head(result$RI, 50))
  
  # Plot & print out 50 strongest feature interdependencies.
  plot(result, type = "id", size = 50)
  print(head(result$ID, 50))
  
  # Plot features ordered by RI_norm. Parameter 'size' is the number of 
  # top features in the chart. We set this parameter a bit larger than cutoff_value.
  plot(result, type = "features", size = result$cutoff_value * 1.1, cex = 1)
  # Here we set 'size' at fixed value 10.
  plot(result, type = "features", size = 10)
  
  # Plot cv classification result obtained on top features.
  # In the middle of x axis red label denotes cutoff_value.
  plot(result, type = "cv", measure = "wacc", cex = 0.8)
  
  # Plot & print out confusion matrix. This matrix is the result of 
  # all classifications performed by all decision trees on all s*t datasets.
  plot(result, type = "cmatrix")
}