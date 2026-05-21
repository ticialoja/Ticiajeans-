package com.example

import org.junit.Assert.*
import org.junit.Test
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun fetchStoreHtml() {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://ticia.lojavirtualnuvem.com.br/")
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .build()
    
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: ""
    println("Response Code: ${response.code}")
    println("Body empty? ${body.isEmpty()}")
    if (body.isNotEmpty()) {
        File("html_dump.txt").writeText(body)
        println("HTML dumped successfully, size: ${body.length}")
    }
  }

  @Test
  fun fetchStoreProdutosHtml() {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://ticia.lojavirtualnuvem.com.br/produtos/")
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .build()
    
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: ""
    println("Response Code: ${response.code}")
    println("Body empty? ${body.isEmpty()}")
    if (body.isNotEmpty()) {
        File("html_produtos_dump.txt").writeText(body)
        println("HTML produtos dumped successfully, size: ${body.length}")
    }
  }
}
