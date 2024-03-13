package com.giis.mobileappproto1.data.sources.remote_api

import java.io.IOException
import java.lang.Exception
import java.net.ConnectException

class DataBindingException : Exception()
class NoInternetException(message: String?) : IOException(message)
class NoServerFoundException(message: String?) : IOException(message)
class ConnectionFailureException(message: String?) : ConnectException(message)
class HttpFailureException : Exception()
class UserDefinedException(message: String?) : Exception(message)

//is NoInternetException -> {
//    Result.NetworkError
//}
//
//is NoServerFoundException -> {
//    Result.Failure(t.message ?: "Oops something went wrong", true)
//}