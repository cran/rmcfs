\name{fix.data}
\alias{fix.data}
\title{Fixes input data values, column names and attributes types}
\description{
	Fixes any input data to prepare them to export to ARFF/ADX formats. If after exporting data to ARFF/ADX formats there are some problems in running Java MCFS or WEKA, try to use this function before. This function fixes data values (e.g. space " " is replaced by "_") and data types (e.g. all Date columns converted to character in R).
}
\usage{fix.data(x, 
          type = c("all", "names", "values", "types"), 
          source_chars = c(" ", ",", "/", "|", "#", "-", "(", ")"), 
          destination_char = "_", 
          numeric_class = c("difftime"), 
          nominal_class = c("factor", "logical", "Date", "POSIXct", "POSIXt"))}
\arguments{
  \item{x}{input data frame to be fixed.}
  \item{type}{
    \itemize{
    \item\code{all} - fixes: column names, data values, data types.
    \item\code{names} - fixes only column names. All characters determined by \code{source_chars} parameter are replaced by \code{destination_char} (e.g. space " " is replaced by "_").
    \item\code{values} - fixes only data values. All characters determined by \code{source_chars} parameter are replaced by \code{destination_char} (e.g. space " " is replaced by "_").
    \item\code{types} - fixes only data types (e.g. all possible nominal columns as (Date or logical) converted to character).
  }}
  \item{source_chars}{characters that will be replaced in column names and data values.}
  \item{destination_char}{character that will be inserted in column names and data values.}
  \item{numeric_class}{vector of class labels to be casted \code{as.numeric}.}
  \item{nominal_class}{vector of class labels to be casted \code{as.character}.}
}
\value{data.frame with fixed values and types (depends on \code{type} parameter).}

\examples{

  # Load alizadeh dataset.
  data(alizadeh)
  alizadeh <- alizadeh[1:500]
  
  # Fix data types and data values - remove "," " " "/" from values and fix data types
  # This function may help if mcfs has any problems with input data
  alizadeh.fixed <- fix.data(alizadeh)
}