###############################
#get.JavaVersion
###############################
#library(rJava)
#get.JavaVersion()
get.JavaVersion <- function(){
  .jinit()
  jv <- .jcall("java/lang/System", "S", "getProperty", "java.runtime.version")
  #create all possible java versions from 11 to 99
  jversions <- paste0(11:99, "+")
  if(any(startsWith(jv,jversions))){
    jvn <- as.numeric(stringi::stri_replace_all(jversions[startsWith(jv,jversions)], "", fixed="+"))
  }else if(substr(jv, 1L, 2L) == "1.") {
    #looks like its Oracle JDK 1.x
    #jvn <- as.numeric(paste0(strsplit(jv, "[.]")[[1L]][1:2], collapse = "."))
    jvn <- as.numeric(strsplit(jv, "[.]")[[1L]][2])
  }else if(grepl("-internal", jv, fixed = TRUE)){
    #looks like its Open JDK 9 e.g. "9-internal"
    jvn <- as.numeric(strsplit(jv, "[-]")[[1L]][1])
  }else if(as.numeric(strsplit(jv, "[.]")[[1L]][1]) >= 9){
    #looks like its JAVA JDK 9 or above
    jvn <- as.numeric(strsplit(jv, "[.]")[[1L]][1])
  }else{
    warning(paste0("Can't recognize java version. Java: ", jv))
    jvn <- 8
  }
  return(jvn)
}

###############################
#showme
###############################
showme <- function(x, size = 10, show = c("tiles", "head", "tail", "none"))
{
  size <- min(c(size, nrow(x), ncol(x)))
  show <- show[1]
  if(show == "tiles"){
    print(x[1:size,1:size])
    cat("\n\n")
    print(x[(nrow(x)-size):nrow(x), (ncol(x)-size):ncol(x)])
  }else if(show == "head"){
    print(head(x,size))
  }else if(show == "tail"){
    print(tail(x,size))
  }else if(show == "none"){
    #nothing
  }else
    stop(paste0("Parameter 'show' is incorrect: ", show))
  
  cat(paste0("class: '", class(x), "' size: ", nrow(x)," x ", ncol(x)))
}

###############################
#my.seq
###############################
my.seq <- function(from, to, by, add.last.to = F)
{
  if(to <= by){
    s <- c(to)
  }else{
    s <- seq(from, to, by)
    if(add.last.to){
      if(s[length(s)]<to)
        s <- c(s,to)
    }
  }
  return (s)
}

###############################
#df.to.matrix
###############################
#  data(alizadeh)
#  d <- alizadeh
#  print(paste0("class: ", class(d), " size: ", nrow(d)," x ", ncol(d)))
#  d.matrix <- df.to.matrix(x=d, chunk_size=1000, verbose = T)
#  d.matrix <- df.to.matrix(x=d, chunk_size=0, verbose = T)
#  print(paste0("class: ", class(d), " size: ", nrow(d)," x ", ncol(d)))
#  d.matrix[1:10,1:10]
#  d.matrix[1:10,(ncol(d.matrix)-10):ncol(d.matrix)]

df.to.matrix <- function(x, chunk_size=50000, verbose = F)
{
  cols.x <- ncol(x)
  rows.x <- nrow(x)
  
  if(chunk_size<=1){
    x.matrix <- as.matrix(x)
  }else{
    m.steps <- my.seq(chunk_size, cols.x, chunk_size, T)
    begin <- 1
    chunk <- 1
    x.matrix.list <- list()
    for(end in m.steps){
      x.matrix.list[[chunk]] <- as.matrix(x[,begin:end])
      begin <- end + 1
      chunk <- chunk +1
    }
    x.matrix <- do.call("cbind", x.matrix.list)
  }
  return (x.matrix)
}

###############################
#string.empty.as.na
###############################
string.empty.as.na <- function(x) {ifelse(x=="", NA, x)}

###############################
#string.replace
###############################
string.replace <- function(x, sourceChars = c(" "), destinationChar = "_")
{
  myregex <- paste0("[\\", paste0(sourceChars, collapse = '\\'), "]")
  ret <- stringi::stri_replace_all(x, destinationChar, regex = myregex)
  return(ret)
}

###############################
#string.detect
###############################
string.detect <- function(x, sourceChars = c(" "))
{
  myregex <- paste0("[\\", paste0(sourceChars, collapse = '\\'), "]")
  ret <- stringi::stri_detect(x, regex = myregex)
  return(ret)
}
###############################
#string.trim
###############################
#library(microbenchmark)
#microbenchmark("my" = string.trim(str), "stringi" = stri_trim_both(str))
#string.trim <- function(str) gsub("^\\s+|\\s+$", "", str)
string.trim <- function(str){
  stringi::stri_trim_both(str)
}

###############################
#string.starts.with
###############################
# s <- c("x","abc", "xyz", "uxw","jhfjdghj","9hjfjy88hdhs","hfst6","ayi","x ijuhyg","abvghy")
# p <- c("ab", "xy")
# string.starts.with(s,p , FALSE, FALSE)
string.starts.with <- function(str, pattern, trim = FALSE, ignore.case = FALSE)
{
  if(trim)
    str <- string.trim(str)
  if(ignore.case){
    str <- tolower(str)
    pattern <- tolower(pattern)
  }
  ret <- rep(F, length(str))
  for(i in 1:length(pattern)){
    ret <- ret | stringi::stri_startswith_fixed(str, pattern[i])
  }
  return(ret)
}

###############################
#string.combine
###############################
string.combine <- function(..., prefix = "", sep = "") 
{
  paste0(prefix, levels(interaction(..., sep = sep)))
}

###############################
#const.features
###############################
const.features <- function(x){
  same <- sapply(x, function(.col){
    all(is.na(.col))  || all(.col[1L] == .col)
  })
  which(same)
}

###############################
#scale.vector
###############################
#scale.vector(c(-0.4,0,1,10),-10,5)
#scale.vector(c(-0.4,0,1,10))
scale.vector<-function(x, min = 0, max = 1)
{
  minTmp <- min(x)
  maxTmp <- max(x)
  xTmp <- (x-minTmp)/(maxTmp-minTmp)
  xTmp <- (xTmp*(max-min))+min
  return(xTmp)
}

###############################
#normalize data - all columns (0,1)
###############################
normalize <- function(data, min = 0, max = 1)
{
  if(!is.data.frame(data))
    stop("Only data frames are handled")
  apply(data, 2, scale.vector, min = min, max = max)
}

###############################
#get.files.names
###############################
get.files.names <- function(path, filter="*", ext=c('.csv','.rds'), fullNames=F, recursive=T)
{
  files <- NULL
  if(!File.exists(path)){
    stop(paste0("Directory: '",path,"' does not exist!"))
  }else{ 
    if(is.null(ext)){
      files <- list.files(path, pattern = NULL, full.names=fullNames, recursive=recursive, include.dirs=F)
    }else{
      for(i in 1:length(ext)){
        files <- c(files, list.files(path, pattern = paste0('\\',ext[i],'$'), full.names=fullNames, recursive=recursive, include.dirs=F ))
      }
    }
    #filter files
    filter <- gsub("([*])\\1+", "\\1", filter)  
    files <- files[files %in% files[grep(filter,files)]]    
  }
  return (files)
}

###############################
#File.exists
###############################
File.exists <- function(x) 
{ 
  if(.Platform$OS == "windows" && grepl("[/\\]$", x)) {
    file.exists(dirname(x)) 
  } else file.exists(x) 
}

###############################
#file.ext
###############################
# path <- "//Users\\mdr.am.ins.ki\\Dropbox/DOCUM.ENTS//Money//ghfdjkhkj.hkfjdhk.EXD"
# file.ext(path)
file.ext <- function(x)
{
  ext <- unlist(strsplit(basename(x), '[.]'))
  if(length(ext) > 1)
    ext <- tolower(tail(ext, 1))
  else
    ext <- ''
  return (ext)
}
###############################
#drop.file.ext
###############################
# drop.file.ext("c:\\pathtofile//path/file.txt")
# drop.file.ext("c:\\pathtofile//path/file.TXT")
# drop.file.ext("//Users\\mdr.am.ins.ki\\Dropbox/DOCUM.ENTS//Money//ghfdjkhkj.hkfjdhk.EXD")
# drop.file.ext("//Users\\mdr.am.ins.ki\\Dropbox/DOCUM.ENTS//Money//ghfdjkhkj.hkfjdhk")
# drop.file.ext("file.txt")
drop.file.ext <- function(x){
  dir <- dirname(x)
  file <- unlist(strsplit(basename(x), '[.]'))
  if(length(file) > 1)
    file <- paste0(file[-length(file)], collapse = '.')
  if(dir != ".")
    file <- file.path(dir, file)
  return(file)
}
###############################
#open.plot.file
###############################
open.plot.file <- function(filename, width = 10, height = 6, res = 72)
{
  dev.flush()
  ext <- file.ext(filename)
  if (ext == "png") {
    png(filename, width=width, height = height, units = 'in', res = 72)
  } else if (ext == "pdf") {
    # pdf size is set by default
    pdf(filename, width = width, height = height)
  } else if (ext == "svg") {
    # pdf size is set by default
    svg(filename, width = width, height = height)
  } else{ # pdf by default
    pdf(filename, width = width, height = height)
  }
  return(T)
}

###############################
#delete.files
###############################
#files <- get.files.names("~/TEMP2/", filter="*", ext=c('.jpg','.rds'), fullNames=T, recursive=T)
#delete.files(files)
delete.files <- function(files){
  ret <- 0
  if(length(files)>0){
    files <- files[sapply(files, File.exists)]
    if(length(files)>0)
      ret <- sum(sapply(files, file.remove))
  }
  return(ret)
}

###############################
#mystop
###############################
mystop <- function(error_message){
  cat(paste0("Error: ", error_message,"\n"))
  return(FALSE)
}

###############################
#save.csv
###############################
save.csv <- function(x, file, row.names = FALSE, col.names = TRUE, na.char = 'NA', ...){
  data.table::fwrite(data.table::as.data.table(x), file = file, 
                     row.names = row.names, col.names = col.names, 
                     na = na.char, ...)
}

###############################
#load.csv
###############################
load.csv <-  function(file, na.char = c('?', 'NA', 'NaN'), ...){
  df <- as.data.frame(data.table::fread(input = file, na.strings = na.char, ...))
  return(df)
}

###############################
#clean.dir
###############################
clean.dir <- function(path){
  do.call(file.remove, list(list.files(path, full.names = TRUE)))
}

###############################
#fix.path
###############################
fix.path <- function(x){
  x <- stringi::stri_replace_all(x, "/", fixed = "\\\\")
  x <- stringi::stri_replace_all(x, "/", fixed = "\\")
  return(x)
}

###############################
#split_path
###############################
split_path <- function(path) {
  setdiff(strsplit(path,"/|\\\\")[[1]], "")
} 

###############################
#temp_dir
###############################
temp_dir <- function(){
  tmp_path <- tempdir()
  tmp_split <- split_path(tmp_path)
  if(startsWith(tmp_split[length(tmp_split)], "Rtmp")){
    return(tmp_path)
  }else{
    tmp_path <- file.path(tmp_path, paste0("Rtmp",Sys.getpid()))
    dir.create(tmp_path, showWarnings = FALSE)
    return(tmp_path)
  }
}
