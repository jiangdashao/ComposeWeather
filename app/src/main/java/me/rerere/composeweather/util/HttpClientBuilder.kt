package me.rerere.composeweather.util

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object HttpClientBuilder {

    // API KEY
    // 前往 https://www.weatherapi.com/ 申请
    private const val API_KEY = "<你的API KEY>"

    private val DEBUG = HttpLoggingInterceptor {
        println(it)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun getHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)

        // 调试拦截器, 打印请求和响应内容
        // .addInterceptor(DEBUG)

        // 拦截器，将全局参数注入到请求体
        .addInterceptor {
            val request = it.request()
            if(request.method == "POST") {
                val requestBuilder = request.newBuilder()
                val body = request.body as FormBody
                val formBodyNew = FormBody.Builder().apply {

                    // 将旧的参数添加到新的FormBody中
                    for (index in 0 until body.size){
                        add(body.name(index), body.value(index))
                    }

                    // 添加 API KEY 参数
                    add("key", API_KEY)

                    // 添加语言参数
                    add("lang", "zh")
                }.build()
                it.proceed(requestBuilder.post(formBodyNew).build())
            }else{
                it.proceed(request)
            }
        }
        .build()
}