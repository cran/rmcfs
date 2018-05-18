###############################
#mcfs
###############################
mcfs <- function(formula, data,
                        projections = 'auto',
                        projectionSize = 'auto',
                        featureFreq = 150,
                        splits = 5,
                        splitSetSize = 1000,
                        balance = 'auto', 
                        cutoffMethod = c("permutations", "criticalAngle", "kmeans", "mean"),
                        cutoffPermutations = 20,
                        buildID = TRUE,
                        finalRuleset = TRUE,
                        finalCV = TRUE,
                        finalCVSetSize = 1000,
                        finalCVRepetitions = 3,
                        seed = NA,
                        threadsNumber = 2)
{
  if(get.JavaVersion() < 1.6) 
    stop("Java 6 or higher is needed for this package but is not available.")
  
  if(!cutoffMethod[1] %in% c("permutations", "criticalAngle", "kmeans", "mean")){
    stop(paste0("Incorrect 'cutoffMethod' = ", cutoffMethod[1]))
  }
  
  start.time <- Sys.time()
  cat("Checking input data...\n")
  label <- "input"
  coldef <- formula.prepare(formula, data)
  target <- coldef$target
  cols <- coldef$cols

  data <- cbind(data[,cols, drop=FALSE], data[,target, drop=FALSE])

  if(length(unique(data[,target])) == 1){
    stop("Decision attribute contains only 1 value.")
  }
  if(any(is.na(data[,target])) == T){
    stop("Decision attribute contains NA values.")
  }
  
  tmp_dir <- tempdir()

  config_file <- file.path(tmp_dir, "mcfs.run")
  input_file_name <- paste0(label, ".adx")
  params <- default.params
  params$inputFileName = input_file_name
  params$inputFilesPATH = paste0(gsub("\\\\", .Platform$file.sep, tmp_dir), .Platform$file.sep)
  params$resFilesPATH = paste0(gsub("\\\\", .Platform$file.sep, tmp_dir), .Platform$file.sep)
  params$mcfs.projections = projections
  params$mcfs.projectionSize = get.projectionSize(length(cols), projectionSize)
  params$mcfs.splits = splits
  params$mcfs.featureFreq = featureFreq
  params$mcfs.balance = balance
  params$mcfs.splitSetSize = splitSetSize
  params$mcfs.cutoffPermutations = cutoffPermutations
  params$mcfs.cutoffMethod = cutoffMethod[1]
  params$mcfs.buildID = buildID
  params$mcfs.finalRuleset = finalRuleset
  params$mcfs.finalCV = finalCV
  params$mcfs.finalCVSetSize = finalCVSetSize
  params$mcfs.finalCVRepetitions = finalCVRepetitions
  params$mcfs.threadsNumber = threadsNumber
  if(is.numeric(seed)){
    params$mcfs.seed = seed
  }else{
    params$mcfs.seed = round(runif(1, 0, 2^24))
  }

  cat("Exporting params...\n")
  save.params.file(params, config_file)
  
  cat("Exporting input data...\n")
  write.adx(data, file=file.path(tmp_dir, input_file_name), target=target)
  
  cat("Running MCFS-ID...\n")
  cdir <- getwd()
  setwd(libdir)
  #jri.prepare()
  .jcall("dmLab/mcfs/MCFS", returnSig="V", "main",
         .jarray(config_file))
  .jcheck(silent = FALSE)
  setwd(cdir)
  
  cat("Reading results...\n")
  mcfsResult <- import.result(tmp_dir, label)
  
  #clean temp files
  #cat("Cleaning temporary files...\n")
  tmp.files <- get.files.names(tmp_dir, ext=c('.zip','.run','.csv','.txt','.adx','.adh'), fullNames=T, recursive=F)
  if(length(tmp.files)>0)
    delete.files(tmp.files)
  
  cat("Done.\n")
  end.time <- Sys.time()
  mcfsResult$exec_time <- end.time - start.time

  class(mcfsResult) <- "mcfs"
  return(mcfsResult)
}

###############################
#jri.prepare
###############################
jri.prepare <- function(){
  .jaddClassPath(system.file("jri", c("JRI.jar"), package = "rJava"))
  
  .jcall("java/lang/System", returnSig="V", "setOut",
         .jnew("java/io/PrintStream",
               .jcast(.jnew("org/rosuda/JRI/RConsoleOutputStream",
                            .jengine(TRUE), as.integer(0)),
                      "java/io/OutputStream")))
  
  .jcall("java/lang/System", returnSig="V", "setErr",
         .jnew("java/io/PrintStream",
               .jcast(.jnew("org/rosuda/JRI/RConsoleOutputStream",
                            .jengine(TRUE), as.integer(1)),
                      "java/io/OutputStream")))
}

###############################
#formula.prepare
###############################
formula.prepare <- function(formula, data) {
  target <- as.character(as.list(formula)[[2]])
  if(!target %in% names(data)){
    stop(paste0("Target feature: '", target, "' does not exist in the data."))
  }
  
  #function model.frame crashes under LINUX
  #ndata <- model.frame(formula, data, na.action=NULL)
  #mcfs.default(ndata[,(2:ncol(ndata))], ndata[,1], names(ndata)[1], ...)
  
  #R code below works perfectly under LINUX and other OS and is much faster
  cols <- all.vars(formula)
  cols <- cols[2:length(cols)]
  if(length(cols)==1 && cols=="."){
    cols <- names(data)
    cols <- cols[cols != target]
  }
  var.exists <- cols %in% names(data)
  cols.missing <- cols[!var.exists]
  if(length(cols.missing)>0){
    cols.missing.text <- paste(cols.missing, collapse=", ")
    stop(paste0("Features: '", cols.missing.text, "' does not exist in the data."))
  }
  retList <- list(target=target, cols=cols)
  return(retList)
}

###############################
#default.params
###############################
default.params <- list(verbose = "false", debug = "false", mcfs.progressShow = "false",
                       inputFileName = "", inputFilesPATH = "", resFilesPATH = "",
                       mcfs.projections = 'auto', mcfs.projectionSize = 'auto',
                       mcfs.projectionSizeMax = 1000,
                       mcfs.featureFreq = 100,
                       mcfs.splits = 5, mcfs.splitRatio = 0.66,
                       mcfs.splitSetSize = 1000,
                       mcfs.balance = 'auto',
                       mcfs.mode = 1,
                       mcfs.cutoffMethod = "permutations",
                       mcfs.cutoffPermutations = 20,
                       mcfs.cutoffAlpha = 0.05,
                       mcfs.cutoffAngle = 0.01,
                       mcfs.buildID = "true",
                       mcfs.finalRuleset = "true",
                       mcfs.finalCV = "true",
                       mcfs.finalCVSetSize = 1000,
                       mcfs.finalCVRepetitions = 3,
                       mcfs.model = "auto", mcfs.progressInterval = 10,
                       mcfs.u = 1, mcfs.v = 1,
                       mcfs.contrastAttr = "false",
                       mcfs.contrastAttrThreshold = 1,
                       mcfs.zipResult = F,
                       mcfs.seed = NA,
                       mcfs.threadsNumber = 4,
                       j48.useGainRatio = "true", j48.maxConnectionDepth = 5,
                       adx.useComplexQuality = "true", adx.qMethod = 2, 
                       sliq.useDiversityMeasure = "true"
)

###############################
#save.params.file
###############################
save.params.file <- function(params, config_file) {
  for(i in 1:length(params)) {
    if(typeof(params[[i]]) == "logical") {
      params[[i]] = tolower(as.character(params[[i]]))
    }
  }
  
  if(length(params[["inputFiles"]])>1){
    params[["inputFiles"]] <- paste0('[',paste0('',params[["inputFiles"]],'', collapse=', '),']')
  }
  
  if(is.null(params[["testFileName"]])){
    params[["testFileName"]] <- ""
  }
  
  f <- file(config_file, "w")
  cat(paste(names(params), params, sep="="), file=f, sep="\n")
  close(f)
}

###############################
#fix.matrix
###############################
fix.matrix <- function(m, na.char = '?'){
  m[is.na(m)] <- na.char
  m[is.infinite(m)] <- na.char
  if(class(m[1,1])=="character"){
    m <- string.trim(m)
    m[m=="Inf"] <- na.char
    m[m=="-Inf"] <- na.char
  }  
  return (m)
}

###############################
#fix.data
###############################
# x$mydate <- as.Date("2007-06-22")
# x$myposix <- as.POSIXct(x$mydate)
# x$diff <- x$mydate - as.Date("2010-06-22")
# showme(x)
# reshape2::melt(lapply(x, class))
# x <- fix.data(x)
# reshape2::melt(lapply(x, class))
# showme(x) 
fix.data <- function(x, 
                     type = c("all", "names", "values", "types"), 
                     source_chars = c(" ", ",", "/", "|", "#", "-", "(", ")"), 
                     destination_char = "_", 
                     numeric_class = c("difftime"), 
                     nominal_class = c("factor", "logical", "Date", "POSIXct", "POSIXt"))
{
  if (!is.data.frame(x))
    x <- as.data.frame(x)
  
  if(type[1] %in% c("all","names") ){
    cat("Fixing names...\n")
    names(x) <- string.replace(string.trim(names(x)), source_chars, destination_char)
  }
  
  if(type[1] %in% c("all","values") ){
    cat("Fixing values...\n")
    x <- fix.data.values(x, source_chars, destination_char)
  }
  
  if(type[1] %in% c("all","types") ){
    cat("Fixing types...\n")
    x <- fix.data.types(x, numeric_class, nominal_class)
  }  
  return(x) 
}

###############################
#fix.data.values
###############################
# data(alizadeh)
# d <- alizadeh
# d <- alizadeh[4000:ncol(alizadeh)]
# d$art <- rep("aa|bb,cc##|dd",nrow(d))
# d$art2 <- rep("  aac bb  ",nrow(d))
# d$art2[3:13] <- ""
# fix.data.values(d)
fix.data.values <- function(x, 
                            source_chars = c(" ", ",", "/", "|", "#", "-", "(", ")"), 
                            destination_char = "_")
{
  x[x == "?"] <- NA
  nominal_class <- c("character", "factor", "Date", "POSIXct", "POSIXt")
  df.class <- reshape2::melt(lapply(x, class))
  colnames(df.class) <- c("classname", "colname")
  nominalMask <- df.class$classname %in% nominal_class
  
  x.nominal <- x[,nominalMask, drop=F]
  #dplyr version
  #x.nominal <- x.nominal %>% mutate_each(funs(as.character)) %>% 
  #mutate_each(funs(string.trim)) %>% 
  #mutate_each(funs(string.empty.as.na))
  #apply version
  #x.nominal <- apply(x.nominal, c(1,2), FUN = as.character)
  #x.nominal <- apply(x.nominal, c(1,2), FUN = string.trim)
  #x.nominal <- apply(x.nominal, c(1,2), FUN = string.empty.as.na)

  x.nominal <- apply(x.nominal, c(1,2), FUN = string.replace, sourceChars=source_chars, destinationChar=destination_char)
  x[nominalMask] <- x.nominal
  
  return(x)
}

###############################
#fix.data.types
###############################
fix.data.types <- function(x, 
                           numeric_class = c("difftime"), 
                           nominal_class = c("factor", "logical", "Date", "POSIXct", "POSIXt"))
{
  df.class <- reshape2::melt(lapply(x, class))
  colnames(df.class) <- c("classname", "colname")
  fixCols.toNumeric <- unique(df.class$colname[df.class$classname %in% numeric_class])
  fixCols.toNominal <- unique(df.class$colname[df.class$classname %in% nominal_class])
  mask.toNumeric <- df.class$colname %in% fixCols.toNumeric
  mask.toNominal <- df.class$colname %in% fixCols.toNominal
  
  if(any(mask.toNumeric)){
      x.toCast <- x[,mask.toNumeric, drop=F]
      x.toCast <- apply(x.toCast, c(1,2), FUN = as.numeric)
      x[mask.toNumeric] <- x.toCast
  }
  if(any(mask.toNominal)){
      x.toCast <- x[,mask.toNominal, drop=F]
      x.toCast <- apply(x.toCast, c(1,2), FUN = as.character)
      x[mask.toNominal] <- x.toCast
  }
  return(x)
}

###############################
#prune.data
###############################
prune.data <- function(data, mcfs_result, size = NA){
  
  if(class(mcfs_result)!="mcfs")
    stop("Input object is not 'mcfs' class.")
  
  if(is.na(size))
    size <- mcfs_result$cutoff_value
  if(is.null(size) | is.na(size) | size <= 0){
    warning(paste0("Parameter 'size' is NULL, NA or <= 0."))
    return(NULL)
  }
  
  fdata <- data[,names(data) %in% as.character(head(mcfs_result$RI,size)$attribute)]
  target.data.frame <- data.frame(data[,mcfs_result$target])
  if(ncol(target.data.frame)==1){
    names(target.data.frame) <- mcfs_result$target
    fdata <- cbind(fdata, target.data.frame)
  }
  return(fdata)
}

###############################
#read.ID (interdependencies)
###############################
read.ID <- function(fileName){
  interdeps <- NULL
  if(File.exists(fileName)){
      file.header <- readLines(fileName, n = 1, warn = FALSE)
      header.vector <- string.replace(unlist(strsplit(file.header, ",")), c("\""),"")
      if(all(c("edge_a","edge_b") %in% header.vector)){
        interdeps <- read.csv.result(fileName)
      }else{
        interdeps <- read.ID.list(fileName)
      }
  }else{
    stop(paste0("File: '",fileName,"' does not exists."))
  }
  
  return(interdeps)
}

###############################
#read.ID.list
###############################
read.ID.list <- function(fileName) {
  f <- file(fileName, "r")
  lines <- readLines(f)
  close(f)
  
  a <- lapply(strsplit(lines, "[,()]"), function(x){x[nchar(x)>0]})
  
  process_row <- function(row) {
    a <- row[1]
    b <- row[2:length(row)]
    dim(b) <- c(2, length(b)/2)
    b <- t(b)
    return(cbind(a, b))
  }
  
  d <- do.call("rbind", lapply(a, process_row))
  if(is.null(d))
    return (NULL)
  if(is.na(d[1,2]))
    return (NULL)

  d <- data.frame(edge_a=d[,1], edge_b=d[,2], weight=as.numeric(d[,3]), stringsAsFactors = FALSE)
  d <- d[order(-d[,3]),]
  d <- data.frame(position = 1:nrow(d), d)
  rownames(d) <- NULL
  
  return(d)
}

###############################
#read.cmatrix
###############################
read.cmatrix <- function(fileName){
  cmatrix <- NULL
  if(File.exists(fileName)){
    cmatrix <- read.table(fileName, sep=",", header = TRUE, stringsAsFactors = FALSE)
    cmatrix <- as.matrix(cmatrix[,2:ncol(cmatrix)])
    if(any(colnames(cmatrix) %in% c("other"))){
      otherIdx <- which(colnames(cmatrix) == c("other"))
      cmatrix <- cmatrix[-otherIdx,-otherIdx]
    }
    rownames(cmatrix) <- as.character(colnames(cmatrix))
    
    class(cmatrix) <- append("cmatrix", class(cmatrix))
  }
  return (cmatrix)
}

###############################
#read.target
###############################
read.target <- function(fileName){
  target <- NULL
  if(File.exists(fileName)){
    matrix <- read.csv.result(fileName)
    target <- colnames(matrix)[1]
  }
  return (target)
}

###############################
#read.RI
###############################
read.RI <- function(fileName){
  ranking <- NULL
  if(File.exists(fileName)){
    ranking <- read.table(fileName, sep=",", header = TRUE, stringsAsFactors = FALSE)
    ranking <- ranking[, names(ranking) %in% c('attribute', 'projections', 'classifiers', 'crudeRI', 'nodes', 'RI_norm')]
    # crudeRI is now nodes
    names(ranking)[names(ranking) %in% c('crudeRI')] <- 'nodes'
    ranking <- ranking[order(-ranking$RI_norm),]
    position <- 1:nrow(ranking)
    ranking <- cbind(position,ranking)
  }else{
    stop(paste0("File: '",fileName,"' does not exists."))
  }
    
  return (ranking)
}

###############################
#read.csv.result
###############################
read.csv.result <- function(fileName){
  resultDataFrame <- NULL
  if(File.exists(fileName))
    resultDataFrame <- read.table(fileName, sep=",", header = TRUE, na.strings = c("NA", "?", "NaN"), stringsAsFactors = FALSE)
  return (resultDataFrame)
}

###############################
#read.jrip
###############################
read.jrip <- function(fileName){
  resultText <- NULL
  if(File.exists(fileName))
    resultText <- readChar(fileName, file.info(fileName)$size)
  
  return(resultText)
}

###############################
#read.params
###############################
read.params <- function(fileName){
  params <- NULL
  if(File.exists(fileName)){
    paramsTXT <- readChar(fileName, file.info(fileName)$size)
    paramsTXT <- string.replace(paramsTXT, c("="), " : ")
    params <- yaml.load(paramsTXT)
  }
  return(params)
}

###############################
#import.result
###############################
import.result <- function(path = "./", label){
  
  if(!dir.exists(file.path(path))){
    stop(paste0("Path does not exist. Path: ", path))
  }
  
  zip_file <- file.path(path, paste0(label, '.zip'))
  if(File.exists(zip_file)){
    zip <- T
    tmp_dir <- tempdir()
    utils::unzip(zip_file, exdir = tmp_dir)
  }else{
    zip <- F
    tmp_dir <- path
  }
  ri_file <- file.path(tmp_dir, paste0(label, "__importances.csv"))
  if(!File.exists(ri_file))
    ri_file <- file.path(tmp_dir, paste0(label, "__RI.csv"))
  
  id_file <- file.path(tmp_dir, paste0(label, "_connections.csv"))
  if(!File.exists(id_file))
    id_file <- file.path(tmp_dir, paste0(label, "_ID.csv"))
  
  distances_file <- file.path(tmp_dir, paste0(label, "_distances.csv"))
  matrix_file <- file.path(tmp_dir, paste0(label, "_cmatrix.csv"))
  cutoff_file <- file.path(tmp_dir, paste0(label, "_cutoff.csv"))
  cv_file <- file.path(tmp_dir, paste0(label, "_cv_accuracy.csv"))
  permutations_file <- file.path(tmp_dir, paste0(label, "_permutations.csv"))
  topRanking_file <- file.path(tmp_dir, paste0(label, "_topRanking.csv"))
  jrip_file <- file.path(tmp_dir, paste0(label, "_jrip.txt"))
  predictionStats_file <- file.path(tmp_dir, paste0(label, "_predictionStats.csv"))
  data_file <- file.path(tmp_dir, paste0(label, "_data.adh"))
  data_csv_file <- file.path(tmp_dir, paste0(label, "_data.csv"))
  params_file <- file.path(tmp_dir, paste0(label, ".run"))
  
  mcfsResult <- list()
  mcfsResult$data <- NULL
  if(File.exists(data_file)){
    mcfsResult$data <- read.adh(data_file)
  }else if(File.exists(data_csv_file)){
    mcfsResult$data <- utils::read.csv(data_csv_file, header = T, sep=',', na.strings = c("NA", "NaN", "?") , stringsAsFactors = F)
  }

  params <- read.params(params_file)
  if(File.exists(matrix_file)){
    mcfsResult$target <- read.target(matrix_file)
  }else{
    mcfsResult$target <- params$target
  }
  mcfsResult$RI <- read.RI(ri_file)

  if(File.exists(id_file)){
    mcfsResult$ID <- read.ID(id_file)
  }
  mcfsResult$distances <- read.csv.result(distances_file)
  mcfsResult$cmatrix <- read.cmatrix(matrix_file)
  mcfsResult$cutoff <- read.csv.result(cutoff_file)
  topRanking <- read.csv.result(topRanking_file)
  if(is.null(topRanking)){
    mcfsResult$cutoff_value <- 0
  }else if(nrow(topRanking)==0){
    mcfsResult$cutoff_value <- 0
  }else{
    mcfsResult$cutoff_value <- topRanking[nrow(topRanking),]$position
  }
  
  mcfsResult$cv_accuracy <- read.csv.result(cv_file)
  mcfsResult$permutations <- read.csv.result(permutations_file)
  mcfsResult$jrip <- read.jrip(jrip_file)
  mcfsResult$predictionStats <- read.csv.result(predictionStats_file)
  if(!is.null(params)){
    mcfsResult$params <- params
  }
  required.names <- c("RI","distances","cutoff")
  if(all(required.names %in% names(mcfsResult))){
    class(mcfsResult) <- "mcfs"
  }else{
    missing <- paste0(required.names[!required.names %in% names(mcfsResult)], collapse = ', ')
    warning(paste0("Result does not contain all needed data.frames: [",missing,"] are missing."))
  }
  
  # clean temporary files
  if(zip){
    tmp.files <- get.files.names(tmp_dir, filter=label, ext=c('.run','.csv','.txt','.adx','.adh'), fullNames=T, recursive=F)
    delete.files(tmp.files)
  }
  return(mcfsResult)
}

###############################
#export.result
###############################
export.result <- function(mcfs_result, path = "./", label = "rmcfs", zip = TRUE){

  if(class(mcfs_result) != "mcfs")
    stop("Input object is not 'mcfs' class.")
  
  #in any case create the directory if it does not exist
  dir.create(file.path(path), showWarnings = F, recursive = T)
  if(zip){
    tmp_dir <- tempdir()
  }else{
    tmp_dir <- path
  }

  ri_file <- file.path(tmp_dir, paste0(label, "__RI.csv"))
  id_file <- file.path(tmp_dir, paste0(label, "_ID.csv"))
  distances_file <- file.path(tmp_dir, paste0(label, "_distances.csv"))
  matrix_file <- file.path(tmp_dir, paste0(label, "_cmatrix.csv"))
  cutoff_file <- file.path(tmp_dir, paste0(label, "_cutoff.csv"))
  cv_file <- file.path(tmp_dir, paste0(label, "_cv_accuracy.csv"))
  permutations_file <- file.path(tmp_dir, paste0(label, "_permutations.csv"))    
  topRanking_file <- file.path(tmp_dir, paste0(label, "_topRanking.csv"))
  jrip_file <- file.path(tmp_dir, paste0(label, "_jrip.txt"))
  predictionStats_file <- file.path(tmp_dir, paste0(label, "_predictionStats.csv"))
  data_file <- file.path(tmp_dir, paste0(label, "_data.csv"))
  params_file <- file.path(tmp_dir, paste0(label, ".run"))
    
  write.csv(mcfs_result$RI, file=ri_file, row.names = F)
  write.csv(mcfs_result$ID, file=id_file, row.names = F)
  write.csv(mcfs_result$distances, file=distances_file, row.names = F)
  #save cmatrix
  if(any(names(mcfs_result)=="cmatrix")){
    cmatrix <- as.data.frame(mcfs_result$cmatrix)
    cmatrix <- cbind(rownames(cmatrix),cmatrix)
    colnames(cmatrix)[1] <- mcfs_result$target
    write.csv(cmatrix, file=matrix_file, row.names = F)
  }
  #save cmatrix
  if(any(names(mcfs_result)=="predictionStats")){
    write.csv(mcfs_result$predictionStats, file=predictionStats_file, row.names = F)
  }
  #save cutoff
  write.csv(mcfs_result$cutoff, file=cutoff_file, row.names = F)
  #save cv_accuracy
  if(any(names(mcfs_result)=="cv_accuracy")){
    write.csv(mcfs_result$cv_accuracy, file=cv_file, row.names = F)
  }    
  #save permutations
  if(any(names(mcfs_result)=="permutations")){
    write.csv(mcfs_result$permutations, file=permutations_file, row.names = F)
  }
  #save top ranking
  topRanking <- head(mcfs_result$RI, mcfs_result$cutoff_value)
  if(!any(names(topRanking) %in% "position") & nrow(topRanking) > 0){
    position <- 1:nrow(topRanking)
    topRanking <- cbind(position, topRanking)
  }
  write.csv(topRanking, file=topRanking_file, row.names = F)
  #save jrip rules
  if(any(names(mcfs_result)=="jrip")){
    writeChar(mcfs_result$jrip, jrip_file)
  }
  #save params
  if(any(names(mcfs_result)=="params")){
    save.params.file(mcfs_result$params, params_file)
  }
  #save data
  if(any(names(mcfs_result)=="data")){
    write.csv(mcfs_result$data, file=data_file, row.names = F)
  }
  
  if(zip){
      zip_file <- file.path(path, paste0(label, '.zip'))
      tmp.files <- get.files.names(tmp_dir, filter=label, ext=c('.run','.csv','.txt','.adx','.adh'), fullNames=T, recursive=F)
      utils::zip(zip_file, files = tmp.files, flags = "-jq")
      delete.files(tmp.files)
  }
}

###############################
#artificial.data
###############################
artificial.data <- function(rnd_features = 500, size = c(40, 20, 10), corruption = c(0,2,4), seed = NA){
  if(length(size) != 3){
    warning("Length of 'size' parameter does not equal to 3. Default values c(40, 20, 10) are used.")
    corruption <- c(40, 20, 10)
  }
  if(length(corruption) != 3){
    warning("Length of 'corruption' parameter does not equal to 3. Default values c(0, 2, 4) are used.")
    corruption <- c(0, 2, 4)
  }
  
  if(is.numeric(seed))
    set.seed(seed)
  class <- c(rep("A",40), rep("B",20), rep("C",10))
  A <- B <- C <- rep("0",length(class))
  A[class=="A"] <- "A"
  B[class=="B"] <- "B"
  C[class=="C"] <- "C"
  rnd <- runif(length(class))
  A[class=="A"][rnd[class=="A"]<=(sort(rnd[class=="A"]))[corruption[1]]] <- "0"
  B[class=="B"][rnd[class=="B"]<=(sort(rnd[class=="B"]))[corruption[2]]] <- "0"
  C[class=="C"][rnd[class=="C"]<=(sort(rnd[class=="C"]))[corruption[3]]] <- "0"
  d <- data.frame(matrix(runif(rnd_features*length(class)), ncol=rnd_features))
  d <- cbind(d,data.frame(A1=A, A2=A, B1=B, B2=B, C1=C, C2=C, class))
  return(d)
}

###############################
#build.idgraph
###############################
build.idgraph <- function(mcfs_result, size = NA, size_ID = NA, 
                          self_ID = FALSE, outer_ID = FALSE, orphan_nodes = FALSE, 
                          size_ID_mult = 3, size_ID_max = 100) {

  if(class(mcfs_result)!="mcfs")
    stop("Input object is not 'mcfs' class.")
  
  if(all(names(mcfs_result)!="ID")){
    stop("ID-Graph edges are not collected. Object 'mcfs_result$ID' does not exist.")
  }
  
  if(is.na(size))
    size <- mcfs_result$cutoff_value
  if(is.null(size) | is.na(size) | size <= 0){
    warning(paste0("Parameter 'size' is NULL, NA or <= 0."))
  }
  
  plot_minW <- 1
  plot_maxW <- 7  
  vertexMinSize <- 3
  vertexMaxSize <- 12
  
  #add weightNorm and color columns to ranking
  ranking <- mcfs_result$RI
  ranking$attribute <- as.character(ranking$attribute)
  ranking$color <- scale.vector(ranking$RI_norm,0,1)
  ranking$color <- abs(ranking$color-1)
  
  #add weightNorm and color columns to interdeps
  #interdeps <- mcfs_result$ID
  interdeps <- mcfs_result$ID[!is.na(mcfs_result$ID$weight),]

  if(is.null(interdeps)){
    warning("ID-Graph is empty. Change input parameters and try to build it again.")
    return (NULL)
  }
  
  if(!self_ID)
    interdeps <- interdeps[interdeps$edge_a != interdeps$edge_b,]
  
  interdeps$weightNorm <- scale.vector(interdeps$weight,plot_minW,plot_maxW)
  interdeps$color <- scale.vector(interdeps$weight,0,1)
  interdeps$color <- abs(interdeps$color-1)

  #select interdeps to plot
  top_nodes <- head(ranking, size)
  #interdeps <- interdeps[is.element(interdeps$edge_a, top_nodes$attribute),]
  #interdeps <- interdeps[is.element(interdeps$edge_b, top_nodes$attribute),]
  if(outer_ID){
    interdeps <- interdeps[is.element(interdeps$edge_a, top_nodes$attribute) |
                             is.element(interdeps$edge_b, top_nodes$attribute),]
  }else{
    interdeps <- interdeps[is.element(interdeps$edge_a, top_nodes$attribute) & 
                             is.element(interdeps$edge_b, top_nodes$attribute),]
  }
  if(is.na(size_ID)){
    size_ID <- min(size * size_ID_mult, size_ID_max, nrow(interdeps))
  }
  #min_ID <- get.min.ID(mcfs_result, size, size_ID, size_ID_mult, size_ID_max)
  min_ID <- sort(interdeps$weight, decreasing=T)[size_ID]
  interdeps <- interdeps[as.numeric(interdeps$weight) >= min_ID,]
  nodes_to_keep <- ranking[ranking$attribute %in% unique(c(top_nodes$attribute, interdeps$edge_a, interdeps$edge_b)),]
  
  #select ranking to plot
  gNodes <- nodes_to_keep
  if(orphan_nodes == FALSE){
    gNodes <- unique(c(interdeps$edge_a,interdeps$edge_b))
    gNodes <- nodes_to_keep[nodes_to_keep$attribute %in% gNodes,]
  }
  cat(paste0("Selected ",nrow(gNodes)," nodes and ", nrow(interdeps)," edges.\n"))
  g <- igraph::graph.empty()
  if(nrow(interdeps) > 0) {    
    #add nodes
    for(i in 1:nrow(gNodes)){
      c <- rgb(1, gNodes$color[i], gNodes$color[i])
      g <- g + igraph::vertices(gNodes$attribute[i], shape="circle", color=c, size=10)
    }
    #add edges
    for(i in 1:nrow(interdeps)){
      c <- rgb(interdeps$color[i], interdeps$color[i], interdeps$color[i])
      g <- g + igraph::edge(interdeps$edge_a[i],interdeps$edge_b[i], weight=interdeps$weightNorm[i],
                            width=interdeps$weightNorm[i], color=c)
    }
    
    from <- NULL
    to <- NULL
    vertex.attributes <- NULL
    #set size of the nodes based on number of connections
    #V(g)[3]
    #E(g)[from(3)]
    #E(g)[to(3)]
    m <- 1:length(igraph::V(g))
    vertexSizeFrom <- sapply(m, function(x) length(igraph::E(g)[from(x)]))
    vertexSizeTo <- sapply(m, function(x) length(igraph::E(g)[to(x)]))
    vertexSize <- vertexSizeFrom + vertexSizeTo

    V(g)$size <- scale.vector(vertexSize,vertexMinSize,vertexMaxSize)
    #vertex.attributes(g)$size <- scale.vector(vertexSize,vertexMinSize,vertexMaxSize)
  }
  
  class(g) <- append("idgraph", class(g))
  return(g)  
}

###############################
#get.min.ID
###############################
get.min.ID <- function(mcfs_result, size = NA, size_ID = NA, size_ID_mult = 3, size_ID_max = 100){

  if(is.na(size))
    size <- mcfs_result$cutoff_value
  if(is.null(size) | is.na(size) | size <= 0){
    warning(paste0("Parameter 'size' is NULL, NA or <= 0."))
    return(0)
  }

  top_attributes <- head(mcfs_result$RI,size)$attribute
  mask_attributes <- mcfs_result$ID$edge_a %in% top_attributes & mcfs_result$ID$edge_b %in% top_attributes
  
  if(is.na(size_ID)){
    top_ID <- head(mcfs_result$ID[mask_attributes,], min(size*size_ID_mult, size_ID_max))
  }else{
    top_ID <- head(mcfs_result$ID[mask_attributes,], size_ID)
  }
  
  if(nrow(top_ID) == 0){
    min_ID <- 0
  }else{
    min_ID <- min(top_ID$weight)
  }
  return(min_ID)
}

###############################
#print.mcfs
###############################
print.mcfs <- function(x, ...){
  mcfs_result <- x
  if(class(mcfs_result)!="mcfs")
    stop("Input object is not 'mcfs' class.")

  writeLines(paste0("##### MCFS-ID result (s = ", mcfs_result$params$mcfs.projections,", t = ",mcfs_result$params$mcfs.splits, ", m = ",mcfs_result$params$mcfs.projectionSize, ") #####"))
  writeLines(paste0("Target feature: '",mcfs_result$target,"'"))
  writeLines("")
  writeLines(paste0("Top ",mcfs_result$cutoff_value," features:"))
  print(head(mcfs_result$RI[,c("position", "attribute", "RI_norm")], mcfs_result$cutoff_value), row.names = F)
  writeLines("")
  writeLines("#################################")
  writeLines("Cutoff values:")
  print(mcfs_result$cutoff, row.names = F)
  writeLines("")
  if(any(names(mcfs_result) %in% c("cmatrix"))){
    writeLines("#################################")
    writeLines("Confusion matrix obtained on randomly selected (st) datasets:")
    print.cmatrix(mcfs_result$cmatrix)
    writeLines("")
  }
  if(any(names(mcfs_result) %in% c("predictionStats"))){
    writeLines("#################################")
    writeLines("Basic prediction statistics obtained on randomly selected (st) datasets:")
    print(summary(mcfs_result$predictionStats))
    writeLines("")
  }
  if(any(names(mcfs_result) %in% c("jrip"))){
    writeLines("#################################")
    writeLines(paste0("JRIP classification rules created on top ", mcfs_result$cutoff_value," features:"))
    writeLines(mcfs_result$jrip)
    writeLines("")
  }
  writeLines("#################################")
  writeLines(paste0("MCFS-ID execution time: ", format(mcfs_result$exec_time, digits=1),""))
}
