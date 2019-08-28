package com.practice.rpc.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author Stefan Liu
 */
@RequestMapping("test")
interface ApiService {
    @GetMapping("")
    fun test(): String
}