###############################
#mcfs
###############################
mcfs <- function(formula, data, 
                 attrWeights = NULL,
                 projections = 'auto',
                 projectionSize = 'auto',
                 featureFreq = 100,
                 splits = 5,
                 splitSetSize = 500,
                 balance = 'auto', 
                 cutoffMethod = c("permutations", "criticalAngle", "kmeans", "mean" , "contrast"),
                 cutoffPermutations = 20,
                 mode = 1,
                 buildID = TRUE,
                 finalRuleset = TRUE,
                 finalCV = TRUE,
                 finalCVSetSize = 1000,
                 seed = NA,
                 threadsNumber = 4)
{
  if(get.JavaVersion() < 7){
    mystop("Java 7 or higher is needed for this package but not available.")
    return(NULL)
  }
  if(!cutoffMethod[1] %in% c("permutations", "criticalAngle", "kmeans", "mean")){
    mystop(paste0("Incorrect 'cutoffMethod' = ", cutoffMethod[1]))
    return(NULL)
  }
  
  cat("Checking input data...\n")
  coldef <- formula.prepare(formula, data)
  target <- coldef$target
  cols <- coldef$cols

  #set up attrWeights
  if(!is.null(attrWeights)){
    if(length(attrWeights) != ncol(data)){
      mystop(paste0("Size (ncol=", ncol(data), ") of input data.frame does not match to length of weights vector(",
                length(attr(data, "attr_weights")),")"))
      return(NULL)
    }
    if(!is.numeric(attrWeights)){
      mystop("Parameter 'attrWeights' does not define numeric weights.")
      return(NULL)
    }
    attrWeights <- matrix(attrWeights, nrow = 1, dimnames = list("1", colnames(data)))
  }
  #set up input data
  data <- data[, c(cols, target), drop=FALSE]
  attr(data, 'target') <- target
  if(!is.null(attrWeights)){
    attr(data, 'attr_weights') <- as.vector(attrWeights[1, c(cols, target)])
  }
  
  #check if data is all right
  if(check_data(data) == FALSE)
    return(NULL)

  #in any case replace slash
  tmp_dir <- fix.path(temp_dir())
  
  #set the label
  #label <- paste0("input_", target)
  label <- "input"
  #set the config file
  config_file <- file.path(tmp_dir, "mcfs.run")

  params <- default.params
  params$inputFileName <- paste0(label, ".adh")
  params$inputFilesPATH <- file.path(tmp_dir, 'rmcfs_data')
  params$resFilesPATH <- file.path(tmp_dir, 'rmcfs_result')
  params$mcfs.projections <- projections
  params$mcfs.projectionSize <- projectionSize
  #params$mcfs.projectionSize <- get.projectionSize(length(cols), projectionSize)
  params$mcfs.splits <- splits
  params$mcfs.featureFreq <- featureFreq
  params$mcfs.balance <- balance
  params$mcfs.splitSetSize <- splitSetSize
  params$mcfs.cutoffPermutations <- cutoffPermutations
  params$mcfs.cutoffMethod <- cutoffMethod[1]
  params$mcfs.buildID <- buildID
  params$mcfs.finalRuleset <- finalRuleset
  params$mcfs.finalCV <- finalCV
  params$mcfs.finalCVSetSize <- finalCVSetSize
  params$mcfs.threadsNumber <- threadsNumber
  params$mcfs.progressInterval <- 10

  if(is.numeric(seed)){
    params$mcfs.seed <- seed
  }else{
    params$mcfs.seed <- round(runif(1, 0, 2^24))
  }
  
  if(!params$mcfs.mode %in% c(1,2)){
    params$mcfs.mode <- 1
    warning("Parameter mode")
  }else{
    params$mcfs.mode <- mode
  }

  dir.create(params$inputFilesPATH, showWarnings = FALSE)
  dir.create(params$resFilesPATH, showWarnings = FALSE)
  clean.dir(params$inputFilesPATH)
  clean.dir(params$resFilesPATH)
  
  #for DEBUG switch these two to TRUE
  #params$verbose <- T
  #params$debug <- T
  
  cat("Exporting params...\n")
  #cat(paste0('File: ', config_file,'\n'))
  save.params.file(params, config_file)
  
  cat("Exporting input data...\n")
  input_file <- file.path(params$inputFilesPATH, params$inputFileName)
  #cat(paste0('File: ', input_file , '\n'))
  write.adh(data, file = input_file, target = target)

  start.time <- Sys.time()
  cat("Running MCFS-ID...\n")
  cdir <- getwd()
  setwd(libdir)
  #jri.prepare()
  .jcall("dmLab/mcfs/MCFS", returnSig="V", "main", .jarray(config_file))
  .jcheck(silent = FALSE)
  setwd(cdir)
  end.time <- Sys.time()
  exec_time <- end.time - start.time
  
  cat("Reading results...\n")
  mcfsResult <- import.result(params$resFilesPATH, label)
  mcfsResult$exec_time <- exec_time


  myRI <- mcfsResult$RI[order(as.numeric(rownames(mcfsResult$RI))),]
  # check if attributes have still the same name
  data_features <- names(data)[!names(data) %in% attr(data, 'target')]
  if(any(as.numeric(rownames(myRI)) != myRI$position)){
    attr_changed <- data.frame(from = myRI$attribute[myRI$attribute != data_features], 
                               to = data_features[myRI$attribute != data_features], stringsAsFactors = F)
    if(nrow(attr_changed) > 0){
      warning(paste0("In the MCFS-ID result the following names of attributes must be fixed/reset: '", paste0(attr_changed$from, collapse = "','"),"'"))
      #fix ranking (RI) names
      myRI$attribute[myRI$attribute != data_features] <- data_features[myRI$attribute != data_features]
      mcfsResult$RI <- myRI[order(as.numeric(myRI$position)),]
      
      #fix ID-graph names - edge_a
      mcfsResult$ID$edge_a <- sapply(mcfsResult$ID$edge_a, function(x, attrChanged){
        change <- (x == attrChanged$from)
        if(any(change)){
          return(as.character(attrChanged$to[change]))
        }else 
          return(x)
      },attrChanged = attr_changed)

      #fix ID-graph names - edge_b
      mcfsResult$ID$edge_b <- sapply(mcfsResult$ID$edge_b, function(x, attrChanged){
        change <- (x == attrChanged$from)
        if(any(change)){
          return(as.character(attrChanged$to[change]))
        }else 
          return(x)
      },attrChanged = attr_changed)
    }
  }
  
  #clean temp files
  tmp.files <- get.files.names(tmp_dir, ext=c('.zip','.run','.csv','.txt','.adx','.adh'), fullNames=T, recursive=F)
  if(length(tmp.files)>0)
    delete.files(tmp.files)
  
  cat("Done.\n")

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
    mystop(paste0("Target feature: '", target, "' does not exist in the data."))
    return(NULL)
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
    mystop(paste0("Features: '", cols.missing.text, "' does not exist in the data."))
    return(NULL)
  }
  retList <- list(target=target, cols=cols)
  return(retList)
}

###############################
#default.params
###############################
default.params <- list(verbose = "false", debug = "false", mcfs.progressShow = "false",
                       inputFileName = "", inputFilesPATH = "", resFilesPATH = "",
                       mcfs.projections = 'auto', 
                       mcfs.projectionSize = 'auto',
                       mcfs.projectionSizeMax = 1000,
                       mcfs.featureFreq = 100,
                       mcfs.splits = 5, 
                       mcfs.splitRatio = 0.66,
                       mcfs.splitSetSize = 500,
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
                       mcfs.finalCVfolds = 10,
                       mcfs.model = "auto", 
                       mcfs.progressInterval = 10,
                       mcfs.u = 1, mcfs.v = 1,
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
#check_data
###############################
check_data <- function(data)
{
  retVal <- TRUE
  if(!inherits(data, "data.frame")){
    retVal <- mystop("Input data object is not a data.frame.")
  }
  
  if('target' %in% names(attributes(data))){
    target <- attr(data, 'target')
    if(!target %in% names(data)){
      retVal <- mystop(paste0("Target attribute: '", target,"' does not exist in the input data."))
    }
  }else{
    retVal <- mystop(paste0("Target attribute is not defined."))
  }
  if(length(unique(data[,target])) == 1){
    retVal <- mystop("Target attribute contains only 1 value.")
  }
  if(any(is.na(data[,target])) == T){
    retVal <- mystop("Target attribute contains NA values.")
  }
  if(any(startsWith(tolower(names(data)),"mcfs_contrast_attr")) == T){
    retVal <- mystop("Attribute name that starts with 'mcfs_contrast_attr' is forbidden.")
  }
  #data.frame attributes
  if( ('attr_weights' %in% names(attributes(data))) & (ncol(data) != length(attr(data, "attr_weights"))) ){
    retVal <- mystop(paste0("Size (ncol=", ncol(data), ") of input data.frame does not match to length of weights vector(",
                  length(attr(data, "attr_weights")),")"))
  }
  source_chars <- c(" ", "'", ",", "/", "|", "#", "-", "(", ")", "[", "]", "{", "}")
  forb_attr <- string.detect(names(data), source_chars)
  if(any(forb_attr)){
    retVal <- mystop(paste0("The names of the following attributes: \n",paste0(names(data)[forb_attr], collapse = ",\n "),
                            "\n contain forbidden characters. Please run fix.data() function before running mcfs()."))
  }
  
  return(retVal)
}

###############################
#fix.matrix
###############################
fix.matrix <- function(m, na.char = '?'){
  m[is.na(m)] <- na.char
  m[is.infinite(m)] <- na.char
  if(class(m[1,1])=="character"){
    m[] <- string.trim(m)
    m[m=="Inf"] <- na.char
    m[m=="-Inf"] <- na.char
  }  
  return (m)
}

###############################
#fix.data
###############################
fix.data <- function(x, 
                     type = c("all", "names", "values", "types"),
                     source_chars = c(" ", "'", ",", "/", "|", "#", 
                                      "-", "(", ")", "[", "]", "{", "}"),
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
# alizadeh <- read.csv(file="http://www.ipipan.eu/staff/m.draminski/files/data/alizadeh.csv", stringsAsFactors = FALSE)
# d <- alizadeh[4000:ncol(alizadeh)]
# d$art <- rep("aa|bb,cc##|dd",nrow(d))
# d$art2 <- rep("  aac bb  ",nrow(d))
# d$art2[3:13] <- ""
# fix.data.values(d)
fix.data.values <- function(x, 
                            source_chars = c(" ", ",", "/", "|", "#", "-", "(", ")"), 
                            destination_char = "_",
                            nominal_class = c("character", "factor", "Date", "POSIXct", "POSIXt"))
{
  x[x == "?" | x == ""] <- NA
  nominalMask <- as.character(lapply(x, class)) %in% nominal_class
  x.nominal <- x[,nominalMask, drop=F]
  x[nominalMask] <- as.data.frame(lapply(x.nominal, function(x) {string.replace(x, source_chars, destination_char)}), stringsAsFactors = F)
  return(x)
}

###############################
#fix.data.types
###############################
# alizadeh <- read.csv(file="http://www.ipipan.eu/staff/m.draminski/files/data/alizadeh.csv", stringsAsFactors = FALSE)
# d <- alizadeh
# d$art <- as.Date(rep("2020-01-01",nrow(d)))
# d$art2 <- as.logical(round(runif(nrow(d))))
# d$art3 <- as.factor(as.logical(round(runif(nrow(d)))))
# d[,1] <- as.difftime(d[,1], units = "mins")
# str(d[,c(1:5,4020:ncol(d))])
# d <- fix.data.types(d)
# str(d[,c(1:5,4020:ncol(d))])
fix.data.types <- function(x, 
                           numeric_class = c("difftime"), 
                           nominal_class = c("factor", "logical", "Date", "POSIXct", "POSIXt"))
{
  numericMask <- as.character(lapply(x, class)) %in% numeric_class
  nominalMask <- as.character(lapply(x, class)) %in% nominal_class
  
  if(any(numericMask)){
    x.numeric <- x[,numericMask, drop=F]
    x[numericMask] <- as.data.frame(lapply(x.numeric, as.numeric), stringsAsFactors = F)
  }
  if(any(nominalMask)){
    x.nominal <- x[,nominalMask, drop=F]
    x[nominalMask] <- as.data.frame(lapply(x.nominal, as.character), stringsAsFactors = F)
  }
  return(x)
}

###############################
#prune.data
###############################
prune.data <- function(x, mcfs_result, size = NA){
  
  if(class(mcfs_result)!="mcfs"){
    mystop("Input object is not 'mcfs' class.")
    return(NULL)
  }
  
  if(is.na(size))
    size <- mcfs_result$cutoff_value
  if(is.null(size) | is.na(size) | size <= 0){
    mystop(paste0("Parameter 'size' is NULL, NA or <= 0."))
    return(NULL)
  }
  
  target <- attr(x, 'target')
  if(is.null(target))
    target <- mcfs_result$target
  
  pruned_data <- x[,names(x) %in% c(as.character(head(mcfs_result$RI$attribute,size)),target), drop = F]
  attr(pruned_data, 'attr_weights') <- attr(x, 'attr_weights')[names(x) %in% c(as.character(head(mcfs_result$RI$attribute,size)),target)]
  attr(pruned_data, 'target') <- attr(x, 'target')
  
  return(pruned_data)
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
    mystop(paste0("File: '",fileName,"' does not exists."))
    return(NULL)
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
    ranking <- ranking[, names(ranking) %in% c('attribute', 'projections', 'classifiers', 'crudeRI', 'nodes', 'RI', 'RI_norm')]
    # crudeRI is now nodes
    names(ranking)[names(ranking) %in% c('crudeRI')] <- 'nodes'
    #if there is RI_norm remove RI and replace RI_norm by RI
    if('RI_norm' %in% names(ranking)){
      ranking <- ranking[, !names(ranking) %in% c('RI')]
      names(ranking)[names(ranking) == 'RI_norm'] <- 'RI'
    }
    rownames(ranking) <- 1:nrow(ranking)
    ranking <- ranking[order(-ranking$RI),]
    position <- 1:nrow(ranking)
    ranking <- cbind(position, ranking)
  }else{
    mystop(paste0("File: '",fileName,"' does not exists."))
    return(NULL)
  }
    
  return (ranking)
}

###############################
#write.RI
###############################
write.RI <- function(ri, fileName){
  myRI <- ri[order(as.numeric(rownames(ri))),]
  save.csv(myRI, file=fileName, row.names = F)
}

###############################
#read.csv.result
###############################
read.csv.result <- function(fileName){
  resultDataFrame <- NULL
  if(File.exists(fileName))
    resultDataFrame <- load.csv(fileName, na.char = c('?', 'NA', 'NaN'))
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
import.result <- function(path = "./", label = NA){

  if(!dir.exists(dirname(path))){
    mystop(paste0("Path does not exist. Path: ", path))
    return(NULL)
  }
  
  if(tolower(file.ext(basename(path))) == 'zip'){
    zip_file <- path
    label <- drop.file.ext(basename(path))
  }else if(is.na(label) || is.null(label)){
    mystop(paste0("Result label is not defined."))
    return(NULL)
  }else{
    zip_file <- file.path(path, paste0(label, '.zip'))
  }
  
  if(File.exists(zip_file)){
    zip <- T
    tmp_dir <- temp_dir()
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
  
  ri_file_phase_1 <- file.path(tmp_dir, paste0(label, "__RI_phase_1.csv"))
  ri_file_phase_2 <- file.path(tmp_dir, paste0(label, "__RI_phase_2.csv"))
  distances_file <- file.path(tmp_dir, paste0(label, "_distances.csv"))
  matrix_file <- file.path(tmp_dir, paste0(label, "_cmatrix.csv"))
  matrix_top_file <- file.path(tmp_dir, paste0(label, "_cmatrix_top.csv"))
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
    mcfsResult$data <- load.csv(data_csv_file, na.char = c('?', 'NA', 'NaN'))
  }
  
  params <- read.params(params_file)
  if(File.exists(matrix_file)){
    mcfsResult$target <- read.target(matrix_file)
  }else{
    mcfsResult$target <- params$target
  }
  
  mcfsResult$RI <- read.RI(ri_file)
  if(File.exists(ri_file_phase_1) & File.exists(ri_file_phase_2)){
    mcfsResult$RI_mode_2 <- list()
    mcfsResult$RI_mode_2$phase_1 <- read.RI(ri_file_phase_1)
    mcfsResult$RI_mode_2$phase_2 <- read.RI(ri_file_phase_2)
  }
  
  if(File.exists(id_file)){
    mcfsResult$ID <- read.ID(id_file)
  }
  mcfsResult$distances <- read.csv.result(distances_file)
  
  if(File.exists(matrix_file)){
    mcfsResult$cmatrix <- list()
    mcfsResult$cmatrix$rnd_features <- read.cmatrix(matrix_file)
    if(File.exists(matrix_top_file)){
      mcfsResult$cmatrix$top_features <- read.cmatrix(matrix_top_file)
    }
  }

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

  if(class(mcfs_result) != "mcfs"){
    mystop("Input object is not 'mcfs' class.")
    return(NULL)
  }
  
  if(tolower(file.ext(basename(path))) == 'zip'){
    zip <- TRUE
    label <- drop.file.ext(basename(path))
    path <- dirname(path)
  }
  
  #in any case create the directory if it does not exist
  if(!dir.exists(path))
    dir.create(path, showWarnings = F, recursive = T)
    
  if(zip){
    tmp_dir <- temp_dir()
  }else{
    tmp_dir <- path
  }

  ri_file <- file.path(tmp_dir, paste0(label, "__RI.csv"))
  ri_file_phase_1 <- file.path(tmp_dir, paste0(label, "__RI_phase_1.csv"))
  ri_file_phase_2 <- file.path(tmp_dir, paste0(label, "__RI_phase_2.csv"))
  id_file <- file.path(tmp_dir, paste0(label, "_ID.csv"))
  distances_file <- file.path(tmp_dir, paste0(label, "_distances.csv"))
  matrix_file <- file.path(tmp_dir, paste0(label, "_cmatrix.csv"))
  matrix_top_file <- file.path(tmp_dir, paste0(label, "_cmatrix_top.csv"))
  cutoff_file <- file.path(tmp_dir, paste0(label, "_cutoff.csv"))
  cv_file <- file.path(tmp_dir, paste0(label, "_cv_accuracy.csv"))
  permutations_file <- file.path(tmp_dir, paste0(label, "_permutations.csv"))    
  topRanking_file <- file.path(tmp_dir, paste0(label, "_topRanking.csv"))
  jrip_file <- file.path(tmp_dir, paste0(label, "_jrip.txt"))
  predictionStats_file <- file.path(tmp_dir, paste0(label, "_predictionStats.csv"))
  data_file <- file.path(tmp_dir, paste0(label, "_data.adh"))
  params_file <- file.path(tmp_dir, paste0(label, ".run"))
  
  write.RI(mcfs_result$RI, fileName=ri_file)
  if(!is.null(mcfs_result$RI_mode_2$phase_1) & !is.null(mcfs_result$RI_mode_2$phase_2)){
    write.RI(mcfs_result$RI_mode_2$phase_1, fileName=ri_file_phase_1)
    write.RI(mcfs_result$RI_mode_2$phase_2, fileName=ri_file_phase_2)
  }
  
  data.table::fwrite(as.data.table(mcfs_result$ID), file=id_file, row.names = F)
  data.table::fwrite(as.data.table(mcfs_result$distances), file=distances_file, row.names = F)

  #save cmatrix
  if(any(names(mcfs_result)=="cmatrix")){
    cmatrix <- as.data.frame(mcfs_result$cmatrix$rnd_features)
    cmatrix <- cbind(rownames(cmatrix),cmatrix)
    colnames(cmatrix)[1] <- mcfs_result$target
    save.csv(cmatrix, file=matrix_file, row.names = F)
    
    if(!is.null(mcfs_result$cmatrix$top_features)){
      cmatrix <- as.data.frame(mcfs_result$cmatrix$top_features)
      cmatrix <- cbind(rownames(cmatrix),cmatrix)
      colnames(cmatrix)[1] <- mcfs_result$target
      save.csv(cmatrix, file=matrix_top_file, row.names = F)
    }
  }
  
  #save predictionStats
  if(any(names(mcfs_result)=="predictionStats")){
    save.csv(mcfs_result$predictionStats, file=predictionStats_file, row.names = F)
  }
  #save cutoff
  save.csv(mcfs_result$cutoff, file=cutoff_file, row.names = F)
  #save cv_accuracy
  if(any(names(mcfs_result)=="cv_accuracy")){
    save.csv(mcfs_result$cv_accuracy, file=cv_file, row.names = F)
  }    
  #save permutations
  if(any(names(mcfs_result)=="permutations")){
    save.csv(mcfs_result$permutations, file=permutations_file, row.names = F)
  }
  #save top ranking
  topRanking <- head(mcfs_result$RI, mcfs_result$cutoff_value)
  if(!any(names(topRanking) %in% "position") & nrow(topRanking) > 0){
    position <- 1:nrow(topRanking)
    topRanking <- cbind(position, topRanking)
  }
  save.csv(topRanking, file=topRanking_file, row.names = F)
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
    #save.csv(mcfs_result$data, file=data_file, row.names = F)
    write.adh(mcfs_result$data, file=data_file, target = mcfs_result$target, zip = F)
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

  if(class(mcfs_result)!="mcfs"){
    mystop("Input object is not 'mcfs' class.")
    return(NULL)
  }
  
  if(all(names(mcfs_result)!="ID")){
    mystop("ID-Graph edges are not collected. Object 'mcfs_result$ID' does not exist.")
    return(NULL)
  }
  
  if(is.na(size))
    size <- mcfs_result$cutoff_value
  if(is.null(size) | is.na(size) | size <= 0){
    mystop(paste0("Parameter 'size' is NULL, NA or <= 0."))
    return(NULL)
  }
  
  plot_minW <- 1
  plot_maxW <- 5
  vertexMinSize <- 3
  vertexMaxSize <- 12
  
  #add weightNorm and color columns to ranking
  ranking <- mcfs_result$RI  
  ranking$attribute <- as.character(ranking$attribute)  
  ranking$color <- scale.vector(ranking$RI, 0, 1)
  ranking$color <- abs(ranking$color-1)
  
  interdeps <- mcfs_result$ID[!is.na(mcfs_result$ID$weight),]
  
  if(is.null(interdeps) | nrow(interdeps)==0){
    warning("ID-Graph is empty. Change input parameters and try to build it again.")
    return (NULL)
  }
  
  if(!self_ID)
    interdeps <- interdeps[interdeps$edge_a != interdeps$edge_b,]
  
  interdeps$weightNorm <- scale.vector(interdeps$weight, plot_minW, plot_maxW)
  interdeps$color <- scale.vector(interdeps$weight, 0, 1)
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
    size_ID <- min(size * size_ID_mult, size_ID_max)
  }
  size_ID <- min(size_ID, nrow(interdeps))
  min_ID <- sort(interdeps$weight, decreasing=T)[size_ID]
  interdeps <- interdeps[as.numeric(interdeps$weight) >= min_ID,]
  nodes_to_keep <- ranking[ranking$attribute %in% unique(c(top_nodes$attribute, interdeps$edge_a, interdeps$edge_b)),]
  
  #select ranking to plot
  gNodes <- nodes_to_keep
  if(orphan_nodes == FALSE){
    gNodes <- unique(c(interdeps$edge_a, interdeps$edge_b))
    gNodes <- nodes_to_keep[nodes_to_keep$attribute %in% gNodes,]
  }
  cat(paste0("Selected ",nrow(gNodes)," nodes and ", nrow(interdeps)," edges.\n"))
  g <- igraph::graph.empty()
  if(nrow(gNodes) == 0){
    return(g)
  }
  
  if(nrow(interdeps) > 0) {
    #create nodes df
    nodes_df <- data.frame(name = gNodes$attribute, color = rgb(1, gNodes$color, gNodes$color), shape = "circle", size = 10, sat = gNodes$color)
    #print(nodes_df)

    #create edges df
    interdeps$color <- rgb(interdeps$color, interdeps$color, interdeps$color)
    interdeps <- interdeps[,-1]
    names(interdeps) <- c("from","to","weight","width","color")
    #print(interdeps)
    
    #create graph based on nodes and edges data.frames
    g <- igraph::graph_from_data_frame(interdeps, directed=TRUE, vertices=nodes_df)
    
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
    #V(g)$edges <- vertexSize
    V(g)$size <- scale.vector(vertexSize, vertexMinSize, vertexMaxSize)
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
  if(class(mcfs_result)!="mcfs"){
    stop("Input object is not 'mcfs' class.")
    return(NULL)
  }

  writeLines(paste0("##### MCFS-ID result (s = ", mcfs_result$params$mcfs.projections,", t = ",mcfs_result$params$mcfs.splits, ", m = ",mcfs_result$params$mcfs.projectionSize, ") #####"))
  writeLines(paste0("Target feature: '",mcfs_result$target,"'"))
  writeLines("")
  writeLines(paste0("Top ",mcfs_result$cutoff_value," features:"))
  print(head(mcfs_result$RI[,c("position", "attribute", "RI")], mcfs_result$cutoff_value), row.names = F)
  writeLines("")
  writeLines("#################################")
  writeLines("Cutoff values:")
  print(mcfs_result$cutoff, row.names = F)
  writeLines("")
  if(any(names(mcfs_result) %in% c("cmatrix"))){
    writeLines("#################################")
    writeLines("Confusion matrix obtained on randomly selected (st) datasets:")
    print.cmatrix(mcfs_result$cmatrix$rnd_features)
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
