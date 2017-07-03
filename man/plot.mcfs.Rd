\name{plot.mcfs}
\alias{plot.mcfs}
\title{Plots various MCFS result components}
\description{
  Plots various aspects of the MCFS-ID result.
}
\usage{
  \method{plot}{mcfs}(x, type = c("ri", "id", "distances", "features", "cv", "cmatrix", "heatmap"), 
        size = NA, 
        ri_permutations = c("max", "all", "sorted", "none"),
        diff_bars = TRUE,
        features_margin = 10,
        cv_measure = c("wacc", "acc", "pearson", "MAE", "RMSE", "SMAPE"),
        heatmap_norm = c('none', 'norm', 'scale'),
        heatmap_fun = c('median', 'mean'),
        heatmap_colors = c('white', 'red'),
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
    \item\code{heatmap} plots heatmap results based on top features. Only numeric features can be presented on the heatmap.
  }}
  \item{size}{number of features to plot.}
  \item{ri_permutations}{if \code{type = "ri"} and \code{ri_permutations = "max"}, then it additionally shows horizontal lines that correspond to max RI values obtained from each single permutation experiment.}
  \item{diff_bars}{if \code{type = "ri"} or \code{type = "id"} and \code{diff_bars = T}, then it shows difference values for RI or ID values.}
  \item{features_margin}{if \code{type = "features"}, then it determines the size of the left margin of the plot.}
  \item{cv_measure}{if \code{type = "cv"}, then it determines the type of accuracy shown in the plot: weighted or unweighted accuracy ("wacc" or "acc"). If target attribute is numeric it is possible to review one of the following prediction quality measures: ("pearson", "MAE", "RMSE", "SMAPE")}
  \item{heatmap_norm}{if \code{type = "heatmap"}, then it defines type of input data normalization \code{'none'} - without any normalization, \code{'norm'} - normalization within range [-1,1], \code{'scale'} - standardization/centering by mean and stdev.}
  \item{heatmap_fun}{if \code{type = "heatmap"}, then it determines calculation \code{'mean'} or \code{'median'} within the class to be shown as heatmap color intensity.}
  \item{heatmap_colors}{if \code{type = "heatmap"}, then it defines low and hi colors on the heatmap.}
  \item{cex}{size of fonts.}
  \item{...}{additional plotting parameters.}
}

\examples{

  # Create input data.
  adata <- artificial.data(rnd_features = 10)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure.
  result <- mcfs(class~., adata, cutoffPermutations = 0, featureFreq = 10,
                  finalCV = FALSE, finalRuleset = TRUE, threadsNumber = 2)

  # Plot & print out distances between subsequent projections. 
  # These are convergence MCFS-ID statistics.
  plot(result, type = "distances")
  print(result$distances)
  
  # Plot & print out 50 most important features and show max RI values from 
  # permutation experiment.
  plot(result, type = "ri", size = 50)
  print(head(result$RI, 50))
  
  # Plot & print out 50 strongest feature interdependencies.
  plot(result, type = "id", size = 50)
  print(head(result$ID, 50))
  
  # Plot features ordered by RI_norm. Parameter 'size' is the number of 
  # top features in the chart. By default it is set on cutoff_value + 10%.
  plot(result, type = "features", cex = 1)

  # Here we set 'size' at fixed value 10.
  plot(result, type = "features", size = 10)
  
  # Plot cv classification result obtained on top features.
  # In the middle of x axis red label denotes cutoff_value.
  # plot(result, type = "cv", measure = "wacc", cex = 0.8)
  
  # Plot & print out confusion matrix. This matrix is the result of 
  # all classifications performed by all decision trees on all s*t datasets.
  plot(result, type = "cmatrix")
}