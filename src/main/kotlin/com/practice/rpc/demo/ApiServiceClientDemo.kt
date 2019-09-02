package com.practice.rpc.demo

import com.practice.rpc.model.DefaultHttpClientManager
import com.practice.rpc.model.DefaultJsonMapper
import com.practice.rpc.model.HttpClientManager
import com.practice.rpc.model.JsonMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

/**
 * 请求端的实现，通过代理实现
 * @author Stefan Liu
 */
@Component
class ApiServiceClientDemo {
    @Autowired
    lateinit var apiService: ApiService

    fun doTest() {
        println(apiService.test())
    }
}

@Component
class ApiServiceClientFactory {
    //  序列化和反序列化方式
    private val jsonMapper: JsonMapper = DefaultJsonMapper()
    // 管理 HttpClient, 可以使用连接池内部实现
    private val httpClientManager: HttpClientManager = DefaultHttpClientManager()
    @Autowired
    lateinit var apiServiceServerSupply: ApiServiceServerSupply

    @Bean
    fun getApiService(): ApiService {
        return apiServiceServerSupply.getApiService(jsonMapper, httpClientManager)
    }
}