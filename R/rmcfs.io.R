###############################
#read.adx
###############################
# d <- read.adx(paste0(dataPath,"/weather.adx"))
# showme(d)
# summary(d)
read.adx <- function(file = ""){
  data <- read.zip(file, 'adx')
  return(data)
}

###############################
#read.adh
###############################
read.adh <- function(file = ""){
  data <- read.zip(file, 'adh')
  return(data)
}

###############################
#read.zip
###############################
read.zip <- function(file, fileFormat){
  if(File.exists(file)){
    if(file.ext(file) == 'zip'){
      tmp_dir <- temp_dir()
      utils::unzip(file, exdir = tmp_dir)
      file_path <- file.path(tmp_dir, paste0(drop.file.ext(basename(file)), ".", fileFormat))
    }else if(file.ext(file) == fileFormat){
      file_path <- file
    }else{
      stop(paste0("Incorrect file format. File: ", file))
    }
  }else{
    stop(paste0("File does not exist. File: ", file))
  }
  if(tolower(fileFormat) == 'adx'){
    data <- import.adx(file_path)
  }else if(tolower(fileFormat) == 'adh'){
    data <- import.adh(file_path)
  }else{
    data <- NULL
  }
  
  return(data)
}

###############################
#import.adx
###############################
# d <- read.adx(paste0(dataPath,"/weather.adx"))
# showme(d)
# summary(d)
import.adx <- function(file = "")
{
  conn <- file(file, "r", blocking = FALSE)
  dat <- readLines(file, warn = F)
  close(conn)
  dat <- unlist(lapply(dat,strsplit,"\\{"))
  dat <- unlist(lapply(dat,strsplit,"\\}"))
  #dat <- strsplit(dat,"\\{")
  #dat <- strsplit(dat,"\\}")
  dat <- string.trim(dat)
  dat <- dat[!string.starts.with(dat, "#", trim = T)]
  dat <- dat[dat!=""]
  attr_idx <- which(dat == 'attributes')
  event_idx <- which(dat == 'events')
  
  #if broken
  if(length(attr_idx)==0)
    stop(paste0("Attributes section is not defined. Missing 'attributes' keyword."))
  
  if(length(event_idx)==0)
    stop(paste0("Events/Objects section is not defined. Missing 'events' keyword."))
  
  if(attr_idx > event_idx)
    stop(paste0("Events section detected before Attributes section."))
  
  attr <- dat[(attr_idx+1):(event_idx-1)]
  events <- dat[(event_idx+1):length(dat)]
  dat <- NULL

  attr <- import.attributes(attr)
  if(length(events)<1)
    stop(paste0("Events are not defined. Number of events = 0"))

  events <- string.trim(events)
  events <- events[!string.starts.with(events, "#", trim = T)]
  events <- events[events!=""]
  events <- gsub('\t', ' ', events)
  events <- do.call(rbind, strsplit(events,','))
  events[events == "?"] <- NA
  events <- as.data.frame(events, stringsAsFactors = F)
  
  if(ncol(events) != nrow(attr)){
    stop(paste0("Number of columns in events section (",ncol(events),") does not correspond to attributes (",nrow(attr),") definition."))
  }
  
  names(events) <- attr$attr_names
  #and cast to numeric columns
  if(any(attr$numeric_cols)){
    events[attr$numeric_cols] <- apply(events[,attr$numeric_cols, drop = FALSE], 2, function(x) as.numeric(x))
  }
  #and cast to nominal columns
  if(any(attr$nominal_cols)){
    events[attr$nominal_cols] <- apply(events[,attr$nominal_cols, drop = FALSE], 2, function(x) as.character(x))
  }
  
  #move decision to the end if it is defined
  # if(any(attr$decision_cols)){
  #   events <- cbind(events[!attr$decision_cols], events[attr$decision_cols])
  # }
  
  events <- events[, !attr$ignore_cols]
  attr(events, 'attr_weights') <- attr$attr_weights
  attr(events, 'target') <- names(events)[attr$decision_cols]

  return(events)
}

###############################
#import.adh
###############################
import.adh <- function(file = "")
{
  conn <- file(file, "r", blocking = FALSE)
  dat <- readLines(file, warn = F)
  close(conn)
  attr <- import.attributes(dat)
  
  csvFileName <- paste0(sub('\\.adh', '', file),".csv")
  if(File.exists(csvFileName)){
    events <- load.csv(csvFileName, na.char = c('?', 'NA', 'NaN'))
  }else{
    stop(paste0("File does not exist. File: ", csvFileName))
  }
  if(ncol(events) != nrow(attr)){
    stop(paste0("Number of columns in csv file (",ncol(events),") does not correspond to adh (",nrow(attr),") header."))
  }
  
  names(events) <- attr$attr_names
  #cast to nominal columns
  if(any(attr$nominal_cols)){
    events[attr$nominal_cols] <- apply(events[,attr$nominal_cols, drop = F], 2, function(x) as.character(x))
  }
  
  events <- events[, !attr$ignore_cols]
  attr(events, 'attr_weights') <- attr$attr_weights
  attr(events, 'target') <- names(events)[attr$decision_cols]
  
  return(events)
}

###############################
#import.attributes
###############################
import.attributes <- function(attr)
{
  if(length(attr)<1)
    stop(paste0("Attributes are not defined. Number of attributes = 0"))

  attr <- string.trim(attr)
  attr <- attr[!string.starts.with(attr, "#", trim = T)]
  attr <- attr[attr!=""]
  attr <- gsub('\t',' ', attr)
  
  nominal_cols <- grepl(' nominal', attr)
  numeric_cols <- grepl(' numeric', attr)
  decision_cols <- grepl(' decision', attr)
  ignore_cols <- grepl(' ignore', attr)
  
  if(sum(decision_cols) > 1)
    stop(paste0("Multiple decision attributes detected. One is expected."))
  
  attr_names <- gsub(' nominal','',attr)
  attr_names <- gsub(' numeric','',attr_names)
  attr_names <- gsub(' decision','',attr_names)
  attr_names <- gsub(' ignore','',attr_names)
  attr_names[decision_cols] <- string.trim(unlist(strsplit(attr_names[decision_cols],'\\('))[1])
  attr_names <- gsub('"','',attr_names)
  attr_names <- gsub("'",'',attr_names)

  attr_names_tmp <- strsplit(attr_names,'\\s+')
  attr_names_tmp <- lapply(attr_names_tmp, function(x){if(length(x)<2) return(c(x,NA)) else return(x)})
  attr_names <- unlist(lapply(attr_names_tmp, function(x){string.trim(x[[1]])}))
  weights <- unlist(lapply(attr_names_tmp, function(x){string.trim(x[[2]])}))
  weights <- as.numeric(gsub("\\]",'',gsub('\\[','',weights)))
  weights[is.na(weights)] <- 1

  if(sum(nominal_cols & numeric_cols)>0)
    stop(paste0("Multiple type attributes detected. One type is expected. Attributes: ",
                paste0(attr_names[nominal_cols & numeric_cols],collapse = ', ')))

  if(sum(nominal_cols | numeric_cols) < length(attr))
    stop(paste0("Missing type attributes detected. One type is expected. Attributes: ",
                paste0(attr_names[!(nominal_cols | numeric_cols)], collapse = ', ')))
  
  ret.df <- data.frame(attr_names, nominal_cols, numeric_cols, ignore_cols, decision_cols, attr_weights = weights, 
                       stringsAsFactors = F)
  return(ret.df)
}

###############################
#write.adx
###############################
# data(alizadeh)
# d <- alizadeh
# d[5,1] <- Inf
# d[5,3] <- NA
# write.adx(d, file="~/ali_test.adx", chunk_size=2000)
write.adx <- function(x, file = "", target = NA, chunk_size = 100000, zip = FALSE)
{
  if(file != ""){
    if(file.ext(file) == "zip"){
      file <- paste0(drop.file.ext(file),".adx")
      zip <- TRUE
    }else if(file.ext(file) != "adx"){
      stop(paste0("Incorrect file name. File: ", file, ". Expected: '.adx'"))
    }
  }
  write.zip(x, file, target, chunk_size, zip, "adx")
}

###############################
#write.adh
###############################
# data(alizadeh)
# d <- alizadeh
# d[5,1] <- Inf
# d[7,2] <- Inf
# d[5,3] <- NA
# d[7,4] <- NA
# write.adh(d, file="~/ali_test.adh")
write.adh <- function(x, file = "", target = NA, chunk_size = 100000, zip = FALSE)
{
  if(file != ""){
    if(file.ext(file) == "zip"){
      file <- paste0(drop.file.ext(file),".adh")
      zip <- TRUE
    }else if(file.ext(file) != "adh"){
      stop(paste0("Incorrect file name File: ",file, ". Expected: '.adh'"))
    }
  }
  write.zip(x, file, target, chunk_size, zip, "adh")
}

###############################
#write.arff
###############################
# data(alizadeh)
# x <- alizadeh
# x$nominalAttr <- c(rep("val1",nrow(x)/2),rep("val2",nrow(x)))[1:nrow(x)]
# d[5,1] <- Inf
# d[7,2] <- Inf
# d[5,3] <- NA
# d[7,4] <- NA
# write.arff(x, file="~/ali_test.arff", chunk_size=2000)
write.arff <- function(x, file = "", target = NA, chunk_size = 100000, zip = FALSE)
{
  if(file != ""){
    if(file.ext(file) == "zip"){
      file <- paste0(drop.file.ext(file),".arff")
      zip <- TRUE
    }else if(file.ext(file) != "arff")
      stop(paste0("Incorrect file format. File: ",file, ". Expected: '.arff'"))
  }
  write.zip(x, file, target, chunk_size, zip, "arff")
}  

###############################
#write.zip
###############################
write.zip <- function(x, file, target, chunk_size, zip, fileFormat)
{
  fileName <- string.trim(file)
  if(fileName == ""){
    zip <- F
    fileDir <- ""
  }else{
    fileDir <- dirname(file)
  }
  
  if(zip){
    tmp_dir <- temp_dir()
  }else{
    tmp_dir <- fileDir
  }
  
  dataFileName <- file.path(tmp_dir, basename(fileName))
  zipFileName <- file.path(fileDir, paste0(sub(paste0('\\.',fileFormat), '', basename(fileName)), '.zip'))
  
  if(fileFormat == "adx"){
    export.adx(x, dataFileName, target, chunk_size)
  }else if(fileFormat == "arff"){
    export.arff(x, dataFileName, target, chunk_size)
  }else if(fileFormat == "adh"){
    export.adh(x, dataFileName, target, chunk_size)
    csvFileName <- paste0(sub('\\.adh', '', dataFileName),".csv")
    dataFileName <- c(dataFileName, csvFileName)
  }
  
  if(zip){
    files2zip <- normalizePath(dataFileName)
    if(File.exists(zipFileName)){
      delete.files(zipFileName)
    }
    utils::zip(zipFileName, files = file.path(files2zip), flags = "-jq")
    delete.files(files2zip)
  }
}

###############################
#retrieve.attributes
###############################
retrieve.attributes <- function(x, target = NA)
{
  numericCols <- sapply(x[1,], is.numeric)
  
  if(is.na(target) & !is.null(attr(x, 'target')))
    target <- attr(x, 'target')
  if(is.character(target))
    target <- match(target, names(x))
  if(is.na(target) || target < 1 || target > ncol(x)){
    target <- ncol(x)
    warning(paste("Target attribute is not correctly defined - setting the last column as target attribute: ", names(x)[target]))
  }
  
  if( ('attr_weights' %in% names(attributes(x))) & (ncol(x) != length(attr(x, "attr_weights"))) )
    stop(paste0("Size (ncol=", ncol(x), ") of input data.frame does not match to length of weights vector(",
                length(attr(x, "attr_weights")),")"))
    
  attrHeader <- matrix(nrow = ncol(x), ncol = 4, byrow = FALSE)
  attrHeader[,1] <- paste0('"',names(x),'"')
  attrHeader[numericCols, 2] <- "numeric"
  attrHeader[!numericCols, 2] <- "nominal"
  if('attr_weights' %in% names(attributes(x))){
    attrHeader[, 3] <- paste0('[',attr(x, "attr_weights"),']')
  }else{
    attrHeader[, 3] <- rep('[1]', nrow(attrHeader))
  }
  attrHeader[target, 4] <- "decision"
  attrHeader[is.na(attrHeader[,4]),4] <- ""
  colnames(attrHeader) <- c('attribute', 'type', 'weight', 'role')
  return(attrHeader)
}

###############################
#export.adh
###############################
export.adh <- function(x, file = '', target = NA, chunk_size = 100000)
{
  if(file == ""){
    file <- stdout()
    adhFileName <- ''
    csvFileName <- ''
  }else if(is.character(file)) {
    if(tolower(file.ext(file)) != "adh")
      stop("Incorrect file extension *.adh expected.")
    adhFileName <- file
    csvFileName <- paste0(sub('\\.adh', '', adhFileName),".csv")
    file <- file(adhFileName, open = "wb")
  }
  
  if(!inherits(file, "connection"))
    stop("Argument 'file' must be a character string or connection.")
  
  if (!is.data.frame(x))
    x <- data.frame(x)
  
  verbose <- F
  if(as.numeric(ncol(x)) * nrow(x) > chunk_size)
    verbose <- T
  
  if(verbose)
    cat(paste0("Retrieving attributes meta info...\n"))
  attrHeader <- retrieve.attributes(x, target)
  attrHeader[,1] <- paste0(" ", attrHeader[,1])
  l <- apply(attrHeader, 1, paste, collapse = " ")
  
  ##### write attributes #####
  if(verbose)
    cat(paste0("Saving header...\n"))
  writeLines(l, file)
  closeAllConnections()
  
  ##### write events #####
  if(verbose)
    cat(paste0("Saving data...\n"))
  save.csv(x, csvFileName, na.char = 'NA')
  
  if(verbose){
    cat("Data has been saved.\n")
  }
  return(TRUE)
}

###############################
#export.adx
###############################
export.adx <- function(x, file = '', target = NA, chunk_size = 100000)
{
  if(file == ''){
    file <- stdout()
  }else if(is.character(file)) {
    if(tolower(file.ext(file)) != "adx")
      stop("Incorrect file extension *.adx expected.")
    
    fileName <- file
    file <- file(file, open = "wb")
    on.exit(close(file))
  }
  
  if(!inherits(file, "connection"))
    stop("Argument 'file' must be a character string or connection.")
  
  if (!is.data.frame(x))
    x <- data.frame(x)
  
  verbose <- F
  if(as.numeric(ncol(x)) * nrow(x) > chunk_size)
    verbose <- T

  if(verbose)
    cat(paste0("Retrieving attributes meta info...\n"))
  attrHeader <- retrieve.attributes(x, target)
  attrHeader[,1] <- paste0(" ", attrHeader[,1])
  l <- apply(attrHeader, 1, paste, collapse = " ")
  
  ##### write attributes #####
  if(verbose)
    cat(paste0("Saving header...\n"))
  writeLines("attributes", file)
  writeLines("{", file)
  writeLines(l, file)
  writeLines("}", file)

  ###### write events ######
  writeLines("events", file)
  writeLines("{", file)
  export.events(x, file, header = FALSE, na.char = '?', chunk_size, verbose)
  writeLines("}", file)

  if(verbose){
    cat("\nData has been saved.\n")
  }
  return(TRUE)
}

###############################
#export.arff
###############################
export.arff <- function(x, file = "", target = NA, chunk_size = 100000)
{
  if(file == ""){
    file <- stdout()
  }else if(is.character(file)) {
    file <- file(file, "wb")
    on.exit(close(file))
  }
  
  if(!inherits(file, "connection"))
    stop("Argument 'file' must be a character string or connection.")
  
  #if (!is.data.frame(x) && !is.matrix(x))
  if (!is.data.frame(x))
    x <- data.frame(x)
  
  if(is.character(target)) 
    target <- match(target, names(x))
  if(is.na(target) || target < 1 || target > ncol(x)) 
    target <- ncol(x)
  
  if(target!=ncol(x)){
    #move decision column to the end to meet ARFF specification
    decisionName <- names(x)[target]
    x <- cbind(x[,-target], x[,target])
    target <- ncol(x)
    names(x)[target] <- decisionName
  }  

  verbose <- F
  if(as.numeric(ncol(x)) * nrow(x) > chunk_size)
    verbose <- T
  
  if(verbose)
    cat(paste0("Retrieving attributes meta info...\n"))
  numeric.cols <- sapply(x[1,], is.numeric)
  
  ## Write Attributes
  if(verbose)
    cat(paste0("Saving header...\n"))
  writeLines(paste0("@relation ",'"',names(x)[target],'"'), file)  
  writeLines("", file)
  
  attrHeader <- matrix(nrow = length(colnames(x)), ncol = 3, byrow = FALSE)
  attrHeader[,1] <- "@attribute"
  attrHeader[,2] <- paste0(" '", names(x), "'")
  attrHeader[numeric.cols, 3] <- " real"
  if(any(!numeric.cols)){
    attrHeader[!numeric.cols, 3] <- sapply(x[,!numeric.cols, drop=F], function(x) paste0(" {", paste(unique(x), collapse=","),"}"))
  }
  attrHeader[is.na(attrHeader[,3]),3] <- ""
  l <- apply(attrHeader, 1, paste, collapse="")
  cat(l, file=file, sep="\n")
  
  # convert input data to matrix
  x <- df.to.matrix(x, chunk_size, verbose)
  
  ## Write Events
  writeLines("", file)
  writeLines("@data", file)
  writeLines("", file)
  export.events(x, file, header = FALSE, na.char = '?', chunk_size, verbose)
  writeLines("", file)
  
  if(verbose){
    cat("\nData has been saved.\n")
  }
  return(TRUE)
}

###############################
#export.events
###############################
export.events <- function(x, file = '', header = FALSE, na.char = '?', chunk_size = 100000, verbose = FALSE)
{
  if(file == ''){
    file <- stdout()
  }else if(is.character(file)) {
    file <- file(file, "wb")
    on.exit(close(file))
  }

  if(!inherits(file, "connection"))
    stop("Argument 'file' must be a character string or connection.")
  
  if (!is.data.frame(x))
    x <- data.frame(x)
  
  cols.x <- as.numeric(ncol(x))
  rows.x <- as.numeric(nrow(x))
  
  # convert input data to matrix
  if(verbose)
    cat("Conversion of input data...\n") 
  x <- df.to.matrix(x, chunk_size, verbose)
  
  col.steps <- my.seq(chunk_size, cols.x, chunk_size, T)
  rstep <-  ceiling(chunk_size / cols.x)
  row.steps <- my.seq(rstep, rows.x, rstep, T)
  chunks <- length(col.steps) * length(row.steps)

  if(verbose)
    cat(paste0("Saving data...\n"))
  #write header
  if(header)
    cat(paste0(colnames(x), collapse=','), file=file, sep="\n")
  
  #write events
  chunk <- 1
  row.begin <- 1
  if(chunks > 1)
    pb <- utils::txtProgressBar(min = 1, max = chunks, style = 3)
  for(i in row.steps) {
    col.begin <- 1
    for(j in col.steps) {
      # m must be matrix type
      m <- x[row.begin:i, col.begin:j, drop=FALSE]
      m <- fix.matrix(m, na.char = na.char)
      l <- apply(m, 1, paste, collapse=",")
      
      if(j > col.steps[1])
        cat(',', file = file, sep="")
      
      if(length(l)>1 | j == cols.x){
        cat(l, file = file, sep="\n")
      }else{
        cat(l, file = file, sep="")
      }
      rm(m, l)
      col.begin <- j + 1
      chunk <- chunk + 1
    }
    row.begin <- i + 1

    #set the Progress Bar
    if(chunk >1 & verbose)
      utils::setTxtProgressBar(pb, chunk)
  }
  
  return(TRUE)
}