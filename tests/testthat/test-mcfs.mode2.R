context("mcfs")

#devtools::use_testthat()
#devtools::use_test("test.mcfs")

test_that("MCFS mode 2", {
  skip_on_cran()
  options(java.parameters = "-Xmx4g")
  require(testthat)
  require(rmcfs)
  
  ####################################
  #########   MCFS mode 2   ##########
  ####################################

  # create input data and review it
  adata <- artificial.data(rnd_features = 100, seed = 1)
  showme(adata)
  
  # Parametrize and run MCFS-ID procedure
  result <- mcfs(class~., adata, cutoffPermutations = 3, mode = 2, featureFreq = 20,
                 buildID = TRUE, finalCV = TRUE, finalRuleset = FALSE, 
                 threadsNumber = 1, seed = 1)

  expect_that(result, is_a("mcfs"))
  expect_true(all(c('RI','ID', 'RI_mode_2') %in% names(result)))
  expect_that(nrow(result$RI_mode_2$phase_1), equals(117))
  #expect_that(nrow(result$RI_mode_2$phase_2), equals(25))
  expect_that(nrow(result$RI_mode_2$phase_1[startsWith(result$RI_mode_2$phase_1$attribute, "mcfs_contrast_attr"),]),equals(11))
  expect_that(nrow(result$RI_mode_2$phase_2[startsWith(result$RI_mode_2$phase_2$attribute, "mcfs_contrast_attr"),]),equals(0))
  expect_that(result$params$mcfs.cutoffPermutations, equals(3))
  #expect_that(result$cutoff_value, equals(6))
  expect_that(nrow(result$RI), equals(106))
})
