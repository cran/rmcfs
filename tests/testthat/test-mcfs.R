context("mcfs")

#devtools::use_testthat()
#devtools::use_test("test.mcfs")

test_that("MCFS artificial data", {
  skip_on_cran()
  options(java.parameters = "-Xmx4g")
  require(testthat)
  require(rmcfs)
  
  ####################################
  ######### Artificial data ##########
  ####################################

  # create input data and review it
  adata <- artificial.data(rnd_features = 10, seed = 1)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 3, featureFreq = 50,
                 buildID = TRUE, finalCV = TRUE, finalRuleset = FALSE, 
                 threadsNumber = 1, seed = 1)
  
  expect_that(result, is_a("mcfs"))
  expect_true(all(c('RI','ID') %in% names(result)))
  expect_that(result$params$mcfs.cutoffPermutations, equals(3))
  expect_that(result$cutoff_value, equals(6))
  expect_that(nrow(result$distances), equals(18))
  expect_that(nrow(result$RI), equals(16))
  expect_that(nrow(result$distances), equals(18))
})


test_that("MCFS artificial data broken", {
  skip_on_cran()
  options(java.parameters = "-Xmx4g")
  require(testthat)
  require(rmcfs)
  
  data <- artificial.data(rnd_features = 1000)
  names(data)[1] <- "MCFS,contrast_1attr_abds"
  names(data)[2] <- "MCFS'contrast_2attr_abds"
  names(data)[3] <- "MCFS#contrast_3attr_abds"
  names(data)[4] <- "MCFS()contrast_attr_abds"
  names(data)[5] <- "MCFS[]contrast_attr_abds"
  names(data)[6] <- "MCFS{}contrast_attr_abds"
  names(data)[7] <- "MCFS{}'#contrast,attr.abds"
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., data, featureFreq = 200,
                 cutoffPermutations = 3, mode = 2,
                 buildID = TRUE, finalCV = TRUE, finalRuleset = TRUE,
                 threadsNumber = 6)
  
  expect_null(result)
  
  data <- fix.data(data)

  result <- mcfs(class~., data, featureFreq = 10,
                 cutoffPermutations = 3, mode = 1,
                 buildID = TRUE, finalCV = FALSE, finalRuleset = FALSE,
                 threadsNumber = 6)
  expect_that(length(names(result)), equals(11))
})