package com.giis.mobileappproto1.data.sources.remote_api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.net.CookieManager

class MyJavaNetCookieJar(apply: CookieManager) { // : CookieJar {
//    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()
//
//    override fun loadForRequest(url: HttpUrl): List<Cookie> {
//        return cookieStore[url.host()] ?: emptyList()
//    }
//
//    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
//        cookieStore[url.host()]?.addAll(cookies) ?: cookieStore.put(
//            url.host(),
//            cookies.toMutableList()
//        )
//    }
}
