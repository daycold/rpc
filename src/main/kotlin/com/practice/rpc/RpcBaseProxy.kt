package com.practice.rpc

import com.practice.rpc.model.HttpClientManager
import com.practice.rpc.model.JsonMapper
import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.io.InputStream
import java.io.StringWriter
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.Type
import java.net.URI

/**
 * @author Stefan Liu
 */
class RpcBaseProxy(clazz: Class<*>, private val httpClientManager: HttpClientManager,
        private val jsonMapper: JsonMapper, endPoint: String) : InvocationHandler {
    private val basePath: String = appendPath(endPoint,
            clazz.getAnnotation(RequestMapping::class.java)?.path?.get(0) ?: "")

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any?>?): Any {
        val request = buildBaseRequest(method, args)
        val response = httpClientManager.getClient().execute(request)
        return parseResponse(response, Unit.javaClass)
    }

    private fun buildBaseRequest(method: Method, args: Array<out Any?>?): HttpRequestBase {
        val parameters = method.parameters
        val pathVariables = getPathVariableMap(parameters, args)
        val requestParams = getRequestParamMap(parameters, args)
        val httpAnnotation = getRequestMappingAnnotation(method)
        val httpRequestBase = when (httpAnnotation) {
            is GetMapping -> HttpGet(buildUrl(httpAnnotation.name, pathVariables, requestParams))
            is PostMapping -> HttpPost(buildUrl(httpAnnotation.name, pathVariables, requestParams))
            is PutMapping -> HttpPut(buildUrl(httpAnnotation.name, pathVariables, requestParams))
            is PatchMapping -> HttpPatch(buildUrl(httpAnnotation.name, pathVariables, requestParams))
            is DeleteMapping -> HttpDelete(buildUrl(httpAnnotation.name, pathVariables, requestParams))
            is RequestMapping -> when (httpAnnotation.method[0]) {
                RequestMethod.GET -> HttpGet(buildUrl(httpAnnotation.name, pathVariables, requestParams))
                RequestMethod.POST -> HttpPost(buildUrl(httpAnnotation.name, pathVariables, requestParams))
                RequestMethod.PUT -> HttpPut(buildUrl(httpAnnotation.name, pathVariables, requestParams))
                RequestMethod.PATCH -> HttpPatch(buildUrl(httpAnnotation.name, pathVariables, requestParams))
                RequestMethod.DELETE -> HttpDelete(buildUrl(httpAnnotation.name, pathVariables, requestParams))
                else -> HttpGet()
            }
            else -> HttpGet()
        }
        if (httpRequestBase is HttpEntityEnclosingRequestBase) {
            httpRequestBase.entity = getRequestBody(parameters, args)
        }
        return httpRequestBase
    }

    private fun getRequestMappingAnnotation(method: Method): Annotation {
        return method.getAnnotation(GetMapping::class.java)
                ?: method.getAnnotation(PostMapping::class.java)
                ?: method.getAnnotation(PutMapping::class.java)
                ?: method.getAnnotation(DeleteMapping::class.java)
                ?: method.getAnnotation(PatchMapping::class.java)
                ?: method.getAnnotation(RequestMapping::class.java)!!
    }

    private fun parseResponse(response: HttpResponse, returnType: Type): Any {
        val entity = response.entity
        return when (returnType) {
            java.lang.Void::class.java,
            Void.TYPE -> Unit
            String::class,
            String::class.java -> entity.content.convertToString()
//            else -> objectMapper.readValue(entity.content, objectMapper.typeFactory.constructType(returnType))
            else -> jsonMapper.deserializeInputStream(entity.content, returnType)
        }
    }

    private fun getPathVariableMap(parameters: Array<Parameter>, args: Array<out Any?>?): Map<String, Any> {
        if (args == null) return mapOf()
        val map = mutableMapOf<String, Any>()
        for (i in parameters.indices) {
            val annotation = parameters[i].getAnnotation(PathVariable::class.java) ?: continue
            map[annotation.value] = args[i]!!
        }
        return map
    }

    private fun getRequestParamMap(parameters: Array<Parameter>, args: Array<out Any?>?): Map<String, Any> {
        if (args == null) return mapOf()
        val map = mutableMapOf<String, Any>()
        for (i in parameters.indices) {
            val annotation = parameters[i].getAnnotation(RequestParam::class.java) ?: continue
            map[annotation.value] = args[i] ?: continue
        }
        return map
    }

    private fun getRequestBody(parameters: Array<Parameter>, args: Array<out Any?>?): HttpEntity? {
        if (args == null) return null
        for (i in parameters.indices) {
            if (parameters[i].getAnnotation(RequestBody::class.java) != null) {
                val arg = args[i]
                return when (arg) {
                    null -> null
                    is String -> ByteArrayEntity(arg.toByteArray(Charsets.UTF_8), ContentType.APPLICATION_JSON)
                    else -> ByteArrayEntity(jsonMapper.writeArrayAsBytes(arg), ContentType.APPLICATION_JSON)
                }
            }
        }
        return null
    }

    private fun buildUrl(pathTemplate: String, pathVariables: Map<String, Any>,
            requestParams: Map<String, Any>): URI {
        var path = appendPath(basePath, pathTemplate)
        pathVariables.forEach { t, u ->
            path = path.replace("{$t}", u.toString())
        }
        if (!requestParams.isEmpty()) {
            val builder = StringBuilder(path).append('?')
            requestParams.forEach { t, u ->
                builder.append(t).append('=').append(u.toString())
            }
            path = builder.toString()
        }
        return URI.create(path)
    }

    private fun appendPath(path1: String, path2: String): String {
        return if (path1.endsWith("/")) {
            if (path2.startsWith("/")) {
                path1 + path2.substring(1)
            } else {
                path1 + path2
            }
        } else {
            if (path2.startsWith("/")) {
                path1 + path2
            } else {
                "$path1/$path2"
            }
        }
    }

    private fun InputStream.convertToString(): String {
        val writer = StringWriter()
        IOUtils.copy(this, writer, "UTF-8")
        return writer.toString()
    }
}