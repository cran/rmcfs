libdir <- character()


.onLoad <- function(libname, pkgname) {
  .jpackage(pkgname, lib.loc = libname)
  libdir <<- file.path(libname, pkgname)
}


.onAttach <- function(libname, pkgname) {
  packageStartupMessage("
  ########################
  # rmcfs version 1.3.3  #
  ########################
  If used please cite the following paper: 
  M. Draminski, J. Koronacki (2018), 
  rmcfs: An R Package for Monte Carlo Feature Selection and Interdependency Discovery,
  Journal of Statistical Software, vol 85(12), 1-28, doi:10.18637/jss.v085.i12.", 
                        domain = NULL, appendLF = TRUE)
}

