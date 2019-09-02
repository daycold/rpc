package com.practice.rpc.model

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

/**
 * @author Stefan Liu
 */
class DefaultHttpClientManager : HttpClientManager {
    override fun getClient(): HttpClient {
        return HttpClients.createMinimal(httpClientConnectionManager)
    }

    companion object {
        val httpClientConnectionManager = PoolingHttpClientConnectionManager()
    }
}