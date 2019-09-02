package com.practice.rpc.model

import org.apache.http.client.HttpClient

/**
 * @author Stefan Liu
 */
interface HttpClientManager {
    fun getClient(): HttpClient
}