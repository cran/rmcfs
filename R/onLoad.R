libdir <- character()

.onLoad <- function(libname, pkgname) {
  .jpackage(pkgname, lib.loc = libname)
  libdir <<- file.path(libname, pkgname)

  .jaddClassPath(system.file("jri",c("JRI.jar"),package="rJava"))

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


.onAttach <- function(libname, pkgname) {
  packageStartupMessage("
  # *******************************
  # Copyright (c) 2003-2016 IPI PAN.
  # *******************************
  # If you want to use dmLab or MCFS-ID in your publication, please cite the following paper:
  # M.Draminski, A.Rada-Iglesias, S.Enroth, C.Wadelius, J. Koronacki, J.Komorowski
  # 'Monte Carlo feature selection for supervised classification',
  # BIOINFORMATICS 24(1): 110-117 (2008)
  # *******************************", domain = NULL, appendLF = TRUE)
  
}