context("mcfs")

#devtools::use_testthat()
#devtools::use_test("test.mcfs")

test_that("read/write adx/adh", {
  skip_on_cran()
  options(java.parameters = "-Xmx4g")
  require(testthat)
  require(rmcfs)
  
  #######################################
  ######### read/write to file ##########
  #######################################

  # create input data and review it
  adata_size <- 30
  adata_ref <- artificial.data(rnd_features = adata_size, seed = 1)
  attr(adata_ref, 'attr_weights') <- round(runif(ncol(adata_ref),min = 1, max = 5))
  attr(adata_ref, 'target') <- 'class'
  
  attributes(adata_ref)
  showme(adata_ref)
  
  path <- tempdir()
  
  file_tmp <- file.path(path, "adx_file.adx")
  file_zip <- file.path(path, "adx_file.zip")
  write.adx(adata_ref, file = file_tmp, target = NA, zip = T)
  adata <- read.adx(file_zip)

  expect_true(all(dim(adata) == dim(adata_ref)))
  expect_true(all(names(adata) == names(adata_ref)))
  expect_true(all(attr(adata, 'attr_weights') == attr(adata_ref, 'attr_weights')))
  expect_true(all(attr(adata, 'target') == attr(adata_ref, 'target')))
  expect_true(all(round(adata[,1:adata_size], digits = 5) == round(adata_ref[,1:adata_size], digits = 5)))
  expect_true(all(adata[,(adata_size+1):(adata_size+5)] == adata_ref[,31:35]))
  
  file_tmp <- file.path(path, "adx_file.adh")
  file_zip <- file.path(path, "adx_file.zip")
  write.adh(adata_ref, file = file_tmp, target = NA, zip = T)
  adata <- read.adh(file_zip)
  
  expect_true(all(dim(adata) == dim(adata_ref)))
  expect_true(all(names(adata) == names(adata_ref)))
  expect_true(all(attr(adata, 'attr_weights') == attr(adata_ref, 'attr_weights')))
  expect_true(all(attr(adata, 'target') == attr(adata_ref, 'target')))
  expect_true(all(round(adata[,1:adata_size], digits = 5) == round(adata_ref[,1:adata_size], digits = 5)))
  expect_true(all(adata[,(adata_size+1):(adata_size+5)] == adata_ref[,31:35]))
  
  file_tmp <- file.path(path, "adx_file.arff")
  file_zip <- file.path(path, "adx_file.zip")
  write.arff(adata_ref, file = file_tmp, target = NA, zip = T)
  
})
