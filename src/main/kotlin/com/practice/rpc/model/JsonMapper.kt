package com.practice.rpc.model

import java.io.InputStream
import java.lang.reflect.Type

/**
 * @author Stefan Liu
 */
interface JsonMapper {
    fun <T> deserializeInputStream(value: InputStream, type: Type): T

    fun writeArrayAsBytes(obj: Any): ByteArray
}