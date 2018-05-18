libdir <- character()


.onLoad <- function(libname, pkgname) {
  .jpackage(pkgname, lib.loc = libname)
  libdir <<- file.path(libname, pkgname)
}


.onAttach <- function(libname, pkgname) {
  packageStartupMessage("
  ########################
  # rmcfs version 1.2.11 #
  ########################
  If used please cite the following paper: 
  M.Draminski, A.Rada-Iglesias, S.Enroth, C.Wadelius, J. Koronacki, J.Komorowski
  Monte Carlo feature selection for supervised classification,
  BIOINFORMATICS 24(1): 110-117 (2008)", domain = NULL, appendLF = TRUE)
}

