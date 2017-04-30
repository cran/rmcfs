###############################
#plot.mcfs
###############################
plot.mcfs <- function(x, type = c("ri", "id", "distances", "features", "cv", "cmatrix", "heatmap"),
                      size = NA,
                      ri_permutations = c("max", "all", "sorted", "none"),
                      diff_bars = TRUE,
                      features_margin = 10,
                      cv_measure = c("wacc", "acc", "pearson", "MAE", "RMSE", "SMAPE"),
                      heatmap_norm = c('none', 'norm', 'scale'),
                      heatmap_fun = c('median', 'mean'),
                      heatmap_colors = c('white', 'red'),
                      cex = 1, ...){
  mcfs_result <- x
  if(class(mcfs_result)!="mcfs"){
    stop("Input object is not 'mcfs' class.")
  }
  
  size <- ceiling(size)
  
  type <- tolower(type[1])
  if(type == "ri"){
    mcfs.plot.RI(mcfs_result, size = size, ri_permutations, diff_bars, cex = cex)
  }else if(type == "id"){
    mcfs.plot.ID(mcfs_result, size = size, diff_bars, cex = cex)
  }else if(type == "distances"){
    mcfs.plot.distances(mcfs_result, size = size, cex = cex)
  }else if(type == "features"){
    mcfs.plot.features(mcfs_result, size = size, cex = 0.8 * cex, features_margin)
  }else if(type == "cv"){
    mcfs.plot.cv(mcfs_result, cv_measure, cex = cex)
  }else if(type == "cmatrix"){
    mcfs.plot.cmatrix(mcfs_result)
  }else if(type == "heatmap"){
    mcfs.plot.heatmap(mcfs_result, size = size, norm = heatmap_norm, fun = heatmap_fun, colors = heatmap_colors, cex = cex)
  }else{
    mcfs.plot.RI(mcfs_result, size, ri_permutations, diff_bars, cex)
  }
}

###############################
#mcfs.plot.RI
###############################
mcfs.plot.RI <- function(mcfs_result, size = NA, 
                         plot_permutations = c("max", "all", "sorted", "none"),
                         plot_diff_bars = T, cex = 1){
  
  if(is.na(size)){
    size <- min(mcfs_result$cutoff_value * 10, nrow(mcfs_result$RI))
  }
  
  plot_permutations <- tolower(plot_permutations[1])
  if(!plot_permutations %in% c("max", "all", "sorted", "none"))
    plot_permutations <- "max"
      
  if(plot_permutations != "none" & any(names(mcfs_result)=="permutations")){
    mcfs.plot.importances(x = mcfs_result$RI, size,
                          permutations = mcfs_result$permutations, 
                          cutoff_value = mcfs_result$cutoff_value, 
                          plot_permutations, plot_diff_bars, cex)
  }else{
    mcfs.plot.importances(x = mcfs_result$RI, size, 
                          permutations = NULL, 
                          cutoff_value = mcfs_result$cutoff_value,
                          plot_permutations = NA, plot_diff_bars, cex)
  }
}

###############################
#mcfs.plot.ID
###############################
mcfs.plot.ID <- function(mcfs_result, size = NA, plot_diff_bars = T, cex = 1){
  if(all(names(mcfs_result)!="ID")){
    warning("ID-Graph edges are not collected. Object 'mcfs_result$ID' does not exist.")
  }else{
    mcfs.plot.importances(x = mcfs_result$ID, size, 
                          permutations = NULL, cutoff_value = NA, 
                          plot_permutations = NA, plot_diff_bars, cex)
  }
}

###############################
#mcfs.plot.importances
###############################
mcfs.plot.importances <- function(x, size = NA, permutations = NULL, cutoff_value = NA,
                                  plot_permutations = NA, plot_diff_bars = T, cex = 1){
  if(any(names(x) %in% c("RI_norm"))){
    importance <- x$RI_norm
    attribute <- x$attribute
    mainlabel <- "Relative Importance"
    shortlabel <- "RI"
    abln <- seq(0,1,0.05)
  }else if(any(names(x) %in% c("weight"))){
    importance <- x$weight
    mainlabel <- "Interdependence weight"
    shortlabel <- "ID_value"    
    maxw <- max(x$weight, na.rm = TRUE)
    b <- c(1,5,10,50,100)
    step <- max(tail(b[maxw/b>5],1),1,na.rm=T)
    abln <- seq(0,maxw,step)
  }else{
    stop("Input data.frame is not a 'RI' or 'ID' type. It does not contain columns: 'RI_norm' or 'weight'!")
    return(NULL)    
  }
  
  col_hi <- "red"
  col_low <- colors()[215]
  col_diff <- "blue"
  
  #set importances
  if(is.na(size)){
    #size <- length(importance)
    size <- min(500, nrow(length(importance)))
  }
  
  importance <- head(importance, size)
  y_lim <- c(0, max(importance))
  y_lim[2] <- y_lim[2] + (y_lim[2] * 0.2)
  
  legend_labels <- shortlabel
  legend_colors <- col_hi
  legend_lty <- 1
  legend_lwd <- 3
  
  #plot diff bars
  if(plot_diff_bars){
    diff_RI <- c(0, abs(diff(importance, lag = 1)))
    legend_labels <- c(legend_labels, paste0("diff(",shortlabel,")"))
    legend_colors <- c(legend_colors, col_diff)
    legend_lty <- c(legend_lty, 1)
    legend_lwd <- c(legend_lwd, 3)
    
  }else{
    diff_RI <- rep(NA, length(importance))
  }

  #plot main barplot
  b <- barplot(diff_RI, col = col_diff, axes = T, ylab = shortlabel, xlab = "attribute", main = mainlabel,
               names.arg=(1:length(diff_RI)), ylim=y_lim, cex.axis = cex, cex.names = cex, cex.lab = cex)
  
  #add importances to the plot
  lines(x = b, y = importance, type = "o", col = col_hi, ylim = y_lim)
  points(x = b, y = importance, pch = 19, col = col_hi, ylim = y_lim)
  
  #flag low important points
  if(!is.na(cutoff_value)){
    lowRI <- importance
    lowRIidx <- 1:length(lowRI)          
    lowRI[lowRIidx <= cutoff_value] <- NA
    #lines(x=b, y=lowRI, type="o", col=col_low, ylim=y_lim)
    points(x=b, y=lowRI, pch = 19, col=col_low, ylim=y_lim)
  }
  
  #plot contrast_attr_
  # if(shortlabel=="RI"){
  #   contrastRI <- importance
  #   contrastRI[!(1:length(contrastRI) %in% grep("contrast_attr_", attribute))] <- NA
  #   #points(x=b, y=contrastRI, pch = 20, col="black", ylim=y_lim)    
  #   lines(x=b, y=contrastRI, type="p", col="black", ylim=y_lim)    
  # }
  
  #plot permutations results
  if(!is.null(permutations)){
    #maxRI_color <- "darkgrey"
    maxRI_color <- "#FFD9D9"
    maxRI_lty <- 1
    perm <- permutations[,string.starts.with(names(permutations), "perm_", trim=T, ignore.case=F)]
    
    if(plot_permutations == "max"){
      max_RI <- sapply(perm, max, na.rm = TRUE)
      for(i in 1:length(max_RI)){
        abline(h = max_RI[i], col = maxRI_color, lty = maxRI_lty)
      }
      legend_labels <- c(legend_labels, "max_perm_RI")
    }else if(plot_permutations == "all"){
      for(i in 1:ncol(perm)){
        curr_perm_name <- names(perm)[i]
        curr_perm <- head(perm[, curr_perm_name], length(importance))
        points(x = b, y = curr_perm, col = maxRI_color, pch = 4, ylim = y_lim)
      }
      legend_labels <- c(legend_labels, "all_perm_RI")
    }else if(plot_permutations == "sorted"){
      for(i in 1:ncol(perm)){
        curr_perm_name <- names(perm)[i]
        curr_perm <- head( sort(perm[, curr_perm_name], decreasing = T), length(importance))
        points(x = b, y = curr_perm, col = maxRI_color, pch = 4, ylim = y_lim)
      }
      legend_labels <- c(legend_labels, "sorted_perm_RI")
    }
    
    legend_colors <- c(legend_colors, maxRI_color)
    legend_lty <- c(legend_lty, maxRI_lty)
    legend_lwd <- c(legend_lwd, 3)
  }

  grid()
  legend('topright', legend_labels, lty=legend_lty, lwd=legend_lwd, col=legend_colors)  
}
###############################
#mcfs.plot.distances
###############################
mcfs.plot.distances <- function(mcfs_result, size = NA, cex = 1)
{
  dist <- mcfs_result$distances
  #distance commonPart mAvg beta1
  if(!all(c("distance", "commonPart", "mAvg", "beta1") %in% names(dist))){
    stop("Input data.frame is not a distance data. It does not contain all needed columns: 'distance', 'commonPart', 'mAvg', 'beta1'.")
  }
  
  if(!"projection" %in% names(dist)){
    dist$projection <- head(seq(30, nrow(dist)*20, 10),nrow(dist))
  }
    
  if(is.na(size)){
    distRows <- nrow(dist)
  }else{
    distRows <- length(dist$projection [dist$projection <= size])
  }
    
  if(distRows <= 0)
    stop(paste0("Size is too small: size = ", size))
        
    #I have to find the mcfs.progressInterval
    step <- diff(mcfs_result$distances$projection, lag=1)[1]
    
    distances <- head(dist, distRows)
    x_values <- distances$projection
    projections <- max(x_values)
    
    y_lim_left <- c(0,ceiling(max(distances$distance)))
    if(y_lim_left[2] == 0)
      y_lim_left[2] <- 1
    
    y_lim_right <- c(min(distances$beta1,distances$commonPart), max(distances$beta1,distances$commonPart))
    y_lim_right[1] <- floor(y_lim_right[1] * 10)/10
    y_lim_right[2] <- ceiling(y_lim_right[2] * 10)/10
    y_lim_right[2] <- y_lim_right[2] + 0.1

    main_label <- paste0("MCFS-ID Convergence (s=",projections,")")  
    plot(distances$beta1, col="black", type="l",yaxt="n",xaxt="n", axes=T, ylab="distance value", 
         xlab="projections (s)", main=main_label, ylim=y_lim_right, cex.axis = cex, cex.lab = cex)
  
    #x label
    x_ticks_number <- 10
    x_mult <- ceiling(nrow(distances)/x_ticks_number)
    ticks <- rev(seq(nrow(distances), 1, by = -x_mult))
    labs <- distances$projection[ticks]
    axis(1, at = ticks, labels=labs, las=2, cex.axis=cex)
    
    lines(distances$commonPart , col="blue", type="l")
    distance <- scale.vector(distances$distance,y_lim_right[1],y_lim_right[2])
    lines(distance, col="red", type="l")
    
    #left axis
    labs <- seq(y_lim_left[1], y_lim_left[2], by=1)
    ticks <- scale.vector(labs,y_lim_right[1],y_lim_right[2])
    axis(side=2, at = ticks, labels = labs, cex.axis=cex, tcl = -0.5)
    #right axis
    labs <- seq(y_lim_right[1],y_lim_right[2],by=0.1)
    ticks <- labs
    axis(side=4, at = ticks, labels = labs, cex.axis=cex, tcl = -0.5)
    legend('topright',c("distance", "commonPart", "beta1"),bty='n',horiz=T, lty=c(1,1,1), lwd=c(2.5,2.5,2.5), col=c("red","blue","black"))
    grid()
}

###############################
#plot.features
###############################
mcfs.plot.features <- function(mcfs_result, size = NA, cex = 0.8, l_margin = 10)
{    
  if(is.na(size))
    #size <- nrow(mcfs_result$RI)
    size <- min(mcfs_result$cutoff_value * 1.2, nrow(mcfs_result$RI))
  if(size <= 0)
    stop("'size' <= 0")
    
  ranking <- head(mcfs_result$RI, size)
  
  highNum <- size
  if(any(names(mcfs_result) %in% "cutoff_value"))
    highNum  <- mcfs_result$cutoff_value
  if(is.na(highNum))
    highNum <- size
  
  importance <- ranking$RI_norm
  attr <- as.character(ranking$attribute)
  
  t <- as.table(rev(importance))
  names(t) <- rev(attr)
  t <- rbind(t,t)
  rownames(t) <- c("important","not important")

  if(highNum == 0){
    t[1,] <- 0
  }else if(highNum >= size){
    t[2,] <- 0
  }else{
    t[1,1:(ncol(t)-highNum)] <- 0
    t[2,(ncol(t)-highNum+1):ncol(t)] <- 0
  }
  
  par(las=2) # make label text perpendicular to axis
  par(mar=c(5,l_margin,4,2)) # increase y-axis margin.
  hi_col <- "firebrick"
  hi_col <- "red"
  low_col <- "grey"
  
  barplot(t, main="Top Features (RI_norm)", col=c(hi_col, low_col), horiz=TRUE, cex.axis = cex, cex.names = cex, cex.lab = cex)
  grid()
  legend("bottomright", rownames(t), fill=c(hi_col, low_col), cex=cex)
  par(las=0)
  par(mar = c(5.1, 4.1, 4.1, 2.1))
}

###############################
#mcfs.plot.cmatrix
###############################
mcfs.plot.cmatrix <- function(mcfs_result){
  
  if(all(names(mcfs_result)!="cmatrix")){
    warning("Confusion Matrix is not calculated. Object 'mcfs_result$cmatrix' does not exist.")
    return(NULL)
  }
  
  cmatrix <- mcfs_result$cmatrix
  #corrplot(cmatrix/sum(cmatrix), is.corr = FALSE, method = "square",cl.lim = c(0, 1))  
  
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
  
  # render plot
  # we use three different layers
  # first we draw tiles and fill color based on percentage of test cases
  tile <- ggplot() +
    geom_tile(aes(y=actual, x=predicted, fill=percent), data=confusion, color="black", size=0.1) +
    labs(y="Actual",x="Predicted") + theme(axis.text=element_text(size=12), axis.title=element_text(size=14,face="bold"))
  
  tile <- tile + 
    geom_text(aes(y=actual, x=predicted, label=sprintf("%.1f %%", percent)), data=confusion, size=5, colour="black") +
    scale_fill_gradient(low="white", high="red")
  
  # lastly we draw diagonal tiles. We use alpha = 0 so as not to hide previous layers but use size=0.3 to highlight border
  tile <- tile + 
    geom_tile(aes(y=actual, x=predicted), data=subset(confusion, as.character(actual)==as.character(predicted)), 
              color="black",size=0.3, fill="black", alpha=0)
  #render
  plot(tile)
}

###############################
#mcfs.plot.cv
###############################
#plot(result, type = "cv", cv_measure = "wacc", cex = 0.8)
#mcfs_result <- result
mcfs.plot.cv <- function(mcfs_result, cv_measure = c("wacc", "acc", "pearson", "MAE", "RMSE", "SMAPE"), cex = 1){
  
    if(all(names(mcfs_result)!="cv_accuracy")){
      warning("Final CV have not been performed. Object 'mcfs_result$cv_accuracy' does not exist.")  
    }else{
      measure <- cv_measure[1]
      if(is.null(mcfs_result$params$mcfs.model) || mcfs_result$params$mcfs.model %in% c("j48")){
        ylim <- c(0,100)
        measure_sufix <- " [%]"
        measure_mult <- 100
        if(!tolower(measure) %in% c("acc","wacc")){
          measure <- "wacc"
        }
      }
      if(!is.null(mcfs_result$params$mcfs.model) && mcfs_result$params$mcfs.model %in% c("m5")){
        measure_sufix <- ""
        measure_mult <- 1
        if(!tolower(measure) %in% tolower(c("pearson", "MAE", "RMSE", "SMAPE")))
            measure <- "pearson"
        if(tolower(measure) == tolower("pearson")){
            ylim <- c(-1,1)
        }else if(tolower(measure) == tolower("SMAPE")){
            ylim <- c(0,1)
        }else{
            ylim <- NULL
        }
    }

    cv_quality_TMP <- mcfs_result$cv_accuracy
    measure_mask <- tolower(names(cv_quality_TMP)) %in% tolower(measure)
    cv_quality_TMP[,measure_mask] <- measure_mult * cv_quality_TMP[,measure_mask]
    measure <- names(cv_quality_TMP)[measure_mask]
    pivot <- reshape2::acast(cv_quality_TMP, algorithm ~ label, value.var=measure)
    
    #library(RColorBrewer)
    #darkcols <- brewer.pal(8, "Dark2")
    plot.colors <- c("dodgerblue","#7570B3", "firebrick", "forestgreen", "gold", "#D95F02", "#1B9E77", "#E7298A", "#A6761D", "#666666")
    plot.colors <- head(plot.colors, nrow(pivot))
    
    if("cutoff_value" %in% names(mcfs_result)){
      xaxt<-'n'
    }else{
      xaxt<-'s'
    }
    barplot(pivot, main=paste0("Cross Validation Results (",measure,")"), xlab= "top n attributes", ylab=paste0(measure,measure_sufix), beside=T, col=plot.colors, 
            cex.axis = cex, cex.names = cex, cex.lab = cex, ylim=ylim, xaxt=xaxt)
    
    #colorize value at mcfs_result$cutoff_value  
    if("cutoff_value" %in% names(mcfs_result)){
      xlabels <- as.numeric(colnames(pivot)[1:length(colnames(pivot))])
      #determine ticks
      at <- 1:length(xlabels) * (nrow(pivot)+1)
      at <- at - nrow(pivot)/2
      
      #black labels
      xlabels_black <- xlabels
      xlabels_black[xlabels %in% mcfs_result$cutoff_value] <- ""
      axis(1, at=at, labels=xlabels_black, cex.axis=cex, tick=F)
      
      #red labels
      xlabels_red <- xlabels
      xlabels_red[!xlabels %in% mcfs_result$cutoff_value] <- ""
      axis(1, at=at, labels=xlabels_red, cex.axis=cex, font.axis = 2, col.axis = "red", tick=F)
    }
    
    legend('bottom', legend=as.character(rownames(pivot)), fill=plot.colors, horiz=T, cex=cex)
    grid()
  }
}

###############################
#plot.heatmap
###############################
mcfs.plot.heatmap <- function(mcfs_result, size = NA, 
                              norm = c('none', 'norm', 'scale'),
                              fun = c('median', 'mean'),
                              colors = c('white', 'red'),
                              cex = 1)
{
  if(is.na(size))
    size <- mcfs_result$cutoff_value
  if(is.null(size) | is.na(size) | size <= 0){
    warning(paste0("Parameter 'size' is NULL, NA or <= 0."))
    return(NULL)
  }

  if("data" %in% names(mcfs_result) & !is.null(mcfs_result$data)){
    data <- mcfs_result$data
  } else {
    stop("mcfs_result does not contain 'data' data.frame. It is possible to set it manually: 'mcfs_result$data <- my.input.data'.")
  }
  
  if(size > (ncol(data)-1)){
    size <- ncol(data)-1
  }
    
  if(!all(head(mcfs_result$RI$attribute,size) %in% names(data)))
    stop("Input data does not match mcfs_result. Columns names are different.")
  
  data.scaled <- filter.data(data, mcfs_result, size)
  numericCols <- sapply(data.scaled, is.numeric)
  
  if(!any(numericCols)){
    warning("Data does not contain any numerical values. Heatmap cannot be created.")
    return(NULL)
  }
  
  if(norm[1]=='norm'){
    data.scaled[, numericCols] <- normalize(data.scaled[, numericCols])
  }else if(norm[1]=='scale'){
    data.scaled[, numericCols] <- scale(data.scaled[, numericCols])
  }else{
    data.scaled <- data.scaled
  }
  #set NA on all nominal input columns
  mask <- !numericCols
  mask[names(mask) %in% mcfs_result$target] <- F
  data.scaled[, mask] <- NA
  
  heat.data <- reshape2::melt(data.scaled, id=mcfs_result$target)
  heat.data <- stats::aggregate(as.numeric(heat.data$value), by=list(heat.data[,1], heat.data[,2]), FUN=fun[1], na.rm=TRUE)
  names(heat.data) <- c('target','feature','heat.value')
  #this is only to avoid R check warrnings 
  #"no visible global function definition for predicted"
  # because of this statement ggplot aes(x=predicted) 
  target <- NULL
  feature <- NULL
  heat.value <- NULL
  
  #heat.data <- ddply(heat.data, .(feature), transform, heat.value = rescale(heat.value, to=c(-1,1)))
  yaxis.labels <- head(mcfs_result$RI$attribute, size)
  heatmap <- ggplot(heat.data, aes(target, feature)) + geom_tile(aes(fill = heat.value), colour = "white") +
    scale_fill_gradient(low = colors[1], high = colors[2]) + xlab(mcfs_result$target) +
    scale_y_discrete(limits=rev(yaxis.labels)) +
    theme(axis.title.y = element_blank()) +
    theme(axis.title.x = element_text(size = rel(cex), colour = "black")) +
    theme(axis.text.x = element_text(size = rel(cex), colour = "black", face = "bold")) +
    theme(axis.text.y = element_text(size = rel(cex), colour = "black")) +
    theme(legend.title = element_text(size = rel(cex))) +
    theme(legend.text = element_text(size = rel(cex))) +
    labs(fill='color key') 
  plot(heatmap)
}

###############################
#plot.idgraph
###############################
plot.idgraph <- function(x, label_dist = 0.5, cex = 1, ...) {
  
  idgraph <- x
  if(!any(class(idgraph) %in% "idgraph"))
    stop("Input object is not 'idgraph' class.")
  
  class(idgraph) <- "igraph"
  if(length(V(idgraph))==0){
      warning("Graph is empty!")
  }else{
      vsize <- V(idgraph)$size
      vsize[is.na(vsize)] <- 3
      curves <- autocurve.idgraph(idgraph)
      opar <- par()$mar
      par(mar=rep(0, 4)) #Give the graph lots of room
      plot(idgraph, vertex.shape="circle", vertex.label.dist=label_dist, layout=igraph::layout.fruchterman.reingold,
           vertex.size=vsize, edge.curved=curves, edge.width=E(idgraph)$width,
           edge.arrow.size=cex, vertex.label.cex=cex)
      par(mar=opar)
      #plot(idgraph, vertex.shape="rectangle", vertex.size=2*vsize, vertex.size2=vsize, edge.arrow.size=0.5, edge.curved=curves, vertex.label.cex=cex)
  }
}

###############################
#autocurve.idgraph
###############################
autocurve.idgraph <- function(idgraph, start = 0.5)
{
  cm <- igraph::count.multiple(idgraph)
  mut <- igraph::is.mutual(idgraph)  #are connections mutual?
  el <- apply(igraph::get.edgelist(idgraph, names = FALSE), 1, paste, collapse = ":")
  ord <- order(el)
  res <- numeric(length(ord))
  p <- 1
  while (p <= length(res)) {
    m <- cm[ord[p]]
    mut.obs <-mut[ord[p]] #are the connections mutual for this point?
    idx <- p:(p + m - 1)
    if (m == 1 & mut.obs==FALSE) { #no mutual conn = no curve
      r <- 0
    }
    else {
      r <- seq(-start, start, length = m)
    }
    res[ord[idx]] <- r
    p <- p + m
  }
  res
}
