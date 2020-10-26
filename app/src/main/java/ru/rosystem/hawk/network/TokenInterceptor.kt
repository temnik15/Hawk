package ru.rosystem.hawk.network

import okhttp3.Interceptor
import okhttp3.Response

/*
Interceptor for inserting the authorization token
 */
class TokenInterceptor private constructor(): Interceptor {
    companion object{
        val instance by lazy {
            TokenInterceptor()
        }
        private const val HEADER_NAME = "Authorization"
    }

    var token:String = ""
    override fun intercept(chain: Interceptor.Chain): Response {
        val origin = chain.request()
        val localToken = token
        return if (localToken.isNotEmpty()) {
            val request = origin.newBuilder()
                .method(origin.method(), origin.body())
                .addHeader(HEADER_NAME, "Bearer $localToken")
                .build()
            chain.proceed(
                request
            )
        } else {
            chain.proceed(chain.request())
        }
    }
}