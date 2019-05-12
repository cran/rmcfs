#####################  
#' build.cmatrix
#' 
#' Function builds confusion matrix.
#' 
#' @param real vector of real values
#' @param predicted vector of predicted values
#' @param levels a vector with all possible values that defines order of the values on the plot (optional)
#' 
#' @return cmatrix data.frame of cmatrix class
#'
#' @examples
#' x1 <- round(runif(100, 0.0, 3.0))
#' x2 <- round(runif(100, 0.0, 3.0))
#' build.cmatrix(x1,x2)
#' 
#' @import dplyr ggplot2
#' @export
#' 
build.cmatrix <- function(real, 
                          predicted, 
                          levels = NULL)
{
  real <- as.character(real)
  predicted <- as.character(predicted)
  if(is.null(levels)){
    levels <- as.character(unique(c(real, predicted)))
  }
  if(length(levels)<2){
    warning(paste0("Cannot build confusion matrix, levels = ", levels))
    return(NULL)
  }
  cmatrix <- table(factor(real, levels), factor(predicted, levels))
  cmatrix <- as.data.frame.matrix(cmatrix)
  cmatrix <- as.matrix(cmatrix)
  colnMat <-  colnames(cmatrix)
  rownMat <- rownames(cmatrix)
  cmatrix <- cmatrix[,match(rownMat,colnMat)]
  colnames(cmatrix) <- rownMat
  rownames(cmatrix) <- rownMat
  cmatrix[is.na(cmatrix)] <- 0
  
  class(cmatrix) <- append("cmatrix", class(cmatrix))
  return(cmatrix)
}

#####################  
#' print.cmatrix
#' 
#' Function prints the stats of confusion matrix.
#' 
#' @param x cmatrix class object
#' @param ... other arguments
#' 
#' @return None
#' 
#' @examples
#' x1 <- round(runif(100, 0.0, 3.0))
#' x2 <- round(runif(100, 0.0, 3.0))
#' cmat <- build.cmatrix(x1,x2)
#' print(cmat)
#' 
#' @export
#' 
print.cmatrix <- function(x, ...)
{
  cmatrix <- x
  if(!any(class(cmatrix) %in% "cmatrix"))
    stop("Input object is not 'cmatrix' class.")
  
  dg <- diag(cmatrix)
  TPR <- 100 * dg / rowSums(cmatrix)
  TPR <- round(TPR, digits = 1)
  acc <- round(100 * calc.acc(cmatrix), digits = 1)
  wacc <- round(100 * calc.wacc(cmatrix), digits = 1)
  conf_matrix <- cmatrix / sum(cmatrix) * 100
  conf_matrix <- round(cmatrix, digits=1)
  writeLines("Confusion Matrix:\n")
  class(conf_matrix) <- "matrix"
  print(conf_matrix)
  writeLines("")
  writeLines("TPR (sensitivity/recall):\n")
  print(data.frame(TPR = paste(TPR,"%")))
  writeLines("")  
  writeLines(paste("Accuracy:",acc,"%"))
  writeLines(paste("wAccuracy:",wacc,"%"))
}

#####################  
#' calc.acc
#' 
#' Function calculates prediction accuracy.
#' 
#' @param cmatrix cmatrix class object
#' 
#' @return prediction accuracy
#' 
#' @examples
#' x1 <- round(runif(100, 0.0, 3.0))
#' x2 <- round(runif(100, 0.0, 3.0))
#' cmat <- build.cmatrix(x1,x2)
#' calc.acc(cmat)
#' 
#' @export
#' 
calc.acc <- function(cmatrix)
{
  if(!any(class(cmatrix) %in% "cmatrix"))
    stop("Input object is not 'cmatrix' class.")
  
  dg <- diag(cmatrix)
  acc <- sum(dg) / sum(cmatrix)
  return(acc)
}

#####################  
#' calc.wacc
#' 
#' Function calculates prediction weighted/balanced accuracy.
#' 
#' @param cmatrix cmatrix class object
#' @return prediction weighted/balanced accuracy
#' 
#' @examples
#' x1 <- round(runif(100, 0.0, 3.0))
#' x2 <- round(runif(100, 0.0, 3.0))
#' cmat <- build.cmatrix(x1,x2)
#' calc.wacc(cmat)
#' 
#' @export
#' 
calc.wacc <- function(cmatrix){
  if(!any(class(cmatrix) %in% "cmatrix"))
    stop("Input object is not 'cmatrix' class.")
  
  dg <- diag(cmatrix)
  TPR <- dg / rowSums(cmatrix)
  wacc <- mean(TPR)
  return(wacc)
}

#####################  
#' plot.cmatrix
#' 
#' Function plots confusion matrix.
#' 
#' @param x cmatrix class object
#' @param color fill color on the cells
#' @param ... other arguments
#' 
#' @return ggplot2 object
#' 
#' @examples
#' x1 <- round(runif(100, 0.0, 3.0))
#' x2 <- round(runif(100, 0.0, 3.0))
#' cmat <- build.cmatrix(x1,x2)
#' plot(cmat)
#' 
#' @import ggplot2
#' @export
#' 
plot.cmatrix <- function(x, color = 'darkred', ...)
{
  cmatrix <- x
  
  actual <- as.data.frame(rowSums(cmatrix))
  actual <- cbind(actual=rep("",nrow(actual)), actual)
  actual$actual <- as.character(rownames(actual))
  names(actual)[2] <- "actualFreq"
  rownames(actual) <- 1:nrow(cmatrix)
  
  confusion <- as.data.frame(as.table(cmatrix))
  names(confusion) <- c("actual","predicted","freq")
  
  #calculate percentage of test cases based on actual frequency
  confusion <- merge(confusion, actual, by=c("actual"))
  confusion$percent <- confusion$freq/confusion$actualFreq*100
  
  #this is only to avoid R check warrnings 
  #"no visible global function definition for predicted"
  # because of this statement ggplot aes(x=predicted) 
  predicted <- NULL
  percent <- NULL
  
  hi_col <- color
  low_col <- 'white'
  
  # render plot
  # we use three different layers
  # first we draw tiles and fill color based on percentage of test cases
  tile <- ggplot() +
    geom_tile(aes(y=actual, x=predicted, fill=percent), data=confusion, color = "black", size=0.2) +
    labs(y="Actual",x="Predicted") + 
    theme(axis.text=element_text(size=12, color = "black"), axis.title=element_text(size=14, face="bold")) +
    geom_text(aes(y=actual, x=predicted, label=sprintf("%.1f %%", percent)), data=confusion, size=5, color="black") +
    scale_fill_gradient(low=low_col, high = hi_col) +
    geom_tile(aes(y=actual, x=predicted), data=subset(confusion, as.character(actual)==as.character(predicted)), color="black", size=0.2, fill="black", alpha=0) +
    scale_x_discrete(limits=levels(confusion$actual)) + 
    scale_y_discrete(limits=rev(levels(confusion$actual)))
  return(tile)
}
