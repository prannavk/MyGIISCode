package com.giis.mobileappproto1.data.sources.remote_api

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Request
import okio.Timeout
import org.json.JSONObject
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

sealed class Result<out T> {
    data class Success<T>(val data: T?) : Result<T>()
    data class Failure(val message: String, val isApiError: Boolean) : Result<Nothing>()
    object NetworkError : Result<Nothing>()
}


abstract class RetrofitCall<I, O>(
    protected val retrofitCall: Call<I>
) : Call<O> {
    override fun execute(): Response<O> = throw NotImplementedError()
    final override fun enqueue(callback: Callback<O>) = enqueueImpl(callback)
    final override fun clone(): Call<O> = cloneImpl()

    override fun cancel() = retrofitCall.cancel()
    override fun request(): Request = retrofitCall.request()
    override fun isExecuted() = retrofitCall.isExecuted
    override fun isCanceled() = retrofitCall.isCanceled

    abstract fun enqueueImpl(callback: Callback<O>)
    abstract fun cloneImpl(): Call<O>
}

class ResultCall<T>(retrofitCall: Call<T>) : RetrofitCall<T, Result<T>>(retrofitCall) {
    override fun enqueueImpl(callback: Callback<Result<T>>) =
        retrofitCall.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val result = if (response.isSuccessful && response.code() in 200..210) {
                    val body = response.body()
                    try {
                        val responseObject = JSONObject(Gson().toJson(body))

                        val code = if (responseObject.has("statusCode")) {
                            responseObject.getString("statusCode").toInt()
                        } else {
                            response.code()
                        }

                        if (code in 200..210) {
                            val successResult: Result<T> = Result.Success(body)
                            successResult
                        } else {
                            val message =
                                if (JSONObject(Gson().toJson(body)).has("statusMessage")) {
                                    JSONObject(Gson().toJson(body)).getString("statusMessage")
                                } else {
                                    JSONObject(Gson().toJson(body)).getString("error")
                                }
                            val failureResult: Result<T> = Result.Failure(message, false)
                            failureResult
                        }
                    } catch (exception: Exception) {
                        val successResult: Result<T> = Result.Success(body)
                        successResult
                    }
                } else {
                    try {
                        val data = response.errorBody()?.string() ?: ""
                        try {
                            val jObjError = JSONObject(data)
                            if (jObjError.has("statusMessage")) {
                                val errorMsg: String = jObjError.getString("statusMessage")
                                Result.Failure(errorMsg, false)
                            } else if (jObjError.has("auth")) {
                                val isAuth: Boolean = jObjError.getBoolean("auth")
                                if (!isAuth) {
                                    Result.Failure("There has been an Authentication Error", false)
                                } else {
                                    if (jObjError.has("message")) {
                                        val errorMsg: String = jObjError.getString("message")
                                        Result.Failure(errorMsg, false)
                                    } else {
                                        Result.Failure("Oops something went wrong", true)
                                    }
                                }
                            } else if (jObjError.has("message")) {
                                Log.d("Results::", jObjError.toString())
                                Result.Failure(jObjError.getString("message"), true)
                            } else {
                                Log.d("Results::", jObjError.toString())
                                if (data.isNotEmpty()) {
                                    Result.Failure(data, true)
                                } else {
                                    Result.Failure("Oops something went wrong", true)
                                }
                            }
                        } catch (e: Exception) {
                            if (response.code() in 500..510) {
                                if (data.isNotEmpty()) {
                                    Result.Failure(data, true)
                                } else {
                                    Result.Failure(
                                        "Oops something went wrong" +
                                                "\n" + "Error code: " + response.code(), true
                                    )
                                }
                            } else {
                                Result.Failure(data, false)
                            }
                        }
                    } catch (e: Exception) {
                        Result.Failure("Oops something went wrong", true)
                    }
                }
                callback.onResponse(this@ResultCall, Response.success(result))
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onFailure(call: Call<T>, t: Throwable) {
                if (call.isCanceled) {
                    val result = Result.Failure("Oops something went wrong", true)
                    callback.onResponse(this@ResultCall, Response.success(result))
                } else {
                    val result = when (t) {

                        is ConnectionFailureException -> {
                            Result.Failure(t.message ?: "Oops something went wrong", true)
                        }

                        is IOException -> {
                            Result.Failure(
                                "Something went wrong, Maybe IOException -> ${t.message}",
                                true
                            )
                        }

                        is JsonSyntaxException -> {
                            t.printStackTrace()
                            Result.Failure(
                                "Something went wrong, Maybe IOException -> ${t.message}",
                                true
                            )
                        }

                        else -> {
                            t.printStackTrace()
                            t.message?.let { Result.Failure(it, true) }
                        }
                    }
                    t.printStackTrace()
                    callback.onResponse(this@ResultCall, Response.success(result))
                }
            }
        })

    override fun cloneImpl() = ResultCall(retrofitCall.clone())
    override fun timeout(): Timeout {
        return retrofitCall.timeout()
    }
}

class RetrofitAdapter(
    private val type: Type
) : CallAdapter<Type, Call<Result<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)
}

class RetrofitAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                Result::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    RetrofitAdapter(resultType)
                }

                else -> null
            }
        }

        else -> null
    }
}