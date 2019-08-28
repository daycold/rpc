package com.practice.rpc

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.HttpClient

/**
 * @author Stefan Liu
 */
interface RpcConfig {
    fun getHttpClient(): HttpClient

    fun getObjectMapper(): ObjectMapper
}