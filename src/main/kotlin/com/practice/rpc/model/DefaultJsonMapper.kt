package com.practice.rpc.model

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream
import java.lang.reflect.Type

/**
 * @author Stefan Liu
 */
class DefaultJsonMapper : JsonMapper {
    override fun writeArrayAsBytes(obj: Any): ByteArray {
        return objectMapper.writeValueAsBytes(obj)
    }

    override fun <T> deserializeInputStream(value: InputStream, type: Type): T {
        return objectMapper.readValue(value, objectMapper.typeFactory.constructType(type))
    }

    companion object {
        val objectMapper = ObjectMapper()
    }
}