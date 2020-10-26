package ru.rosystem.hawk.network

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient
/*
  Client to communicate with API
 */
object NetworkState {
    private val instance by lazy {
        val okHttp = OkHttpClient
            .Builder()
            .addInterceptor(TokenInterceptor.instance)
            .build()

         ApolloClient.builder()
            .serverUrl("https://api.beta.hawk.so/graphql")
            .okHttpClient(okHttp)
            .build()
    }
    fun getApolloClient(): ApolloClient {
        return instance
    }
}