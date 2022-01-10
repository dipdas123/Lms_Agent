package tech.redltd.lmsAgent.network

import okhttp3.Interceptor
import okhttp3.Response
import tech.redltd.lmsAgent.utils.UnAuthorizeException

object AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .addHeader("Module", "JW9tc0ByZWRsdGQl")
            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdG5hbWUiOiJjdXN0b21lcjEiLCJsYXN0bmFtZSI6Imxhc3QgTmFtZSIsImVtYWlsIjoiY3VzdG9tZXJAZ21haWwuY29tIiwibXNpc2RuIjoiODgwMTg0NDUyNTEyMiIsImFnZW50X2lkIjoyMSwiYWdlbnRfZW1haWwiOiJjdXN0b21lci5sbXMucm9iaXNob3BAcm9iaS5jb20uYmQiLCJhZ2VudF9wYXNzd29yZCI6IjEyMzQ1IiwibG9naW5fZGF0ZXRpbWUiOiIyMDIwLTA2LTI4VDA1OjQ3OjIwLjQ4N1oiLCJpYXQiOjE1OTMzMjMyNDB9.ITxm6TwJmmYxqEo-BghKVtIMmMOxz4rZ65mYzPjZ_Sw")
            .build()
        return chain.proceed(request)
    }
}

object AspInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val original  = chain.request()
        val request = original.newBuilder()
            .addHeader("Module","c2hhcmlmdWw=")
            .addHeader("Content-Type","application/json")
            .addHeader("Authorization","Basic MTg1NDc4NDUxMjpGYTEyMzQ1Njc4OQ==")
            .addHeader("x-Auth-Token","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZ2VudElEIejoyLCJtb2JpbGUiOiIxOTEyNjEwODk5IiwiZmlyc3RuYW1lIjoiU2hhcmlmdWwiLCJvdHAiOjMwMTk1OCwiaWF0IjoxNTcwODU5NTYwfQ.k8ICcAyzAOqlbmgW0N-kr8lUMwcMLDOnTIFEbku0PAs")
            .build()
        return chain.proceed(request)
    }

}

object UnAuthorizeInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401){
            throw UnAuthorizeException("UnAuthorize Please Login")
        }

        return response
    }

}
