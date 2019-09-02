package com.practice.rpc.demo

import com.practice.rpc.RpcBaseProxy
import com.practice.rpc.model.HttpClientManager
import com.practice.rpc.model.JsonMapper
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.Proxy

/**
 * 服务器端的实现，注册成控制器
 * @author Stefan Liu
 */
@RestController
class ApiServiceServerDemo : ApiService {
    override fun test(): String {
        return "test"
    }
}

/**
 * 服务方提供给请求发的 ApiService 实现
 * @author Stefan Liu
 */
@Component
class ApiServiceServerSupply {
    // 服务方的域名, 可以使用局域网来保证安全性
    private val host = "http://localhost:1234"

    /**
     * 可以添加一个公钥做参数，添加对公钥的验证，来保证安全性
     * @param jsonMapper  序列化和反序列化方式
     * @param httpClientManager 管理 HttpClient, 可以使用连接池内部实现
     */
    fun getApiService(jsonMapper: JsonMapper, httpClientManager: HttpClientManager): ApiService {
        return Proxy.newProxyInstance(RpcBaseProxy::class.java.classLoader,
                arrayOf(ApiService::class.java),
                RpcBaseProxy(ApiService::class.java, httpClientManager, jsonMapper, host)) as ApiService
    }
}