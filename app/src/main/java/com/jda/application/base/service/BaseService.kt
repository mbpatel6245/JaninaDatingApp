package com.jda.application.base.service

//import io.reactivex.schedulers.Schedulers
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import android.os.Bundle
import android.util.Log
import com.jda.application.BuildConfig
import com.jda.application.acivities.JDAApplication
import com.jda.application.utils.Constants
import com.jda.application.utils.FirebaseLogsUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BaseService {
    private var mRetrofit: Retrofit? = null
    private var mAuthorization: String = Constants.sEmpty_String

    private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(URLs.Retrofit.sTime.toLong(), TimeUnit.SECONDS)
            .readTimeout(URLs.Retrofit.sTime.toLong(), TimeUnit.SECONDS)
            .writeTimeout(URLs.Retrofit.sTime.toLong(), TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder =
                        original.newBuilder()
                                .header(URLs.Retrofit.sAuthorizationHeader, mAuthorization)
                                .addHeader(URLs.Retrofit.sContentType, URLs.Retrofit.sContentTypeJson)
                                .addHeader("app_version_name", BuildConfig.VERSION_NAME)
                                .addHeader("app_version_code", "${BuildConfig.VERSION_CODE}")
                                .addHeader("app_device_type", "0")
                                .method(original.method(), original.body())
                val request = requestBuilder.build()
                Log.e(Constants.Logger.sUrlRequest, request.url().toString())
                Log.i(Constants.LOG_TAG, "UrlRequest:" + request.url().toString())
                val params = Bundle().apply {
                    putString("UrlRequest", request.url().toString())
                }
                FirebaseLogsUtil.setLogs(Constants.LOG_TAG, params)
                chain.proceed(request)
            }

    private val okHttpClientForFacebookProfilePic = OkHttpClient.Builder()
            .connectTimeout(URLs.Retrofit.sTime.toLong(), TimeUnit.SECONDS)
            .readTimeout(URLs.Retrofit.sTime.toLong(), TimeUnit.SECONDS)
            .writeTimeout(URLs.Retrofit.sTime.toLong(), TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder =
                        original.newBuilder()
                                .addHeader(URLs.Retrofit.sContentType, URLs.Retrofit.sContentTypeJson)
                                .addHeader(URLs.Retrofit.sXRequestWith, URLs.Retrofit.sXmlHttpRequest)
                                .method(original.method(), original.body())
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()


    fun getClient(): Retrofit {
        val baseUrl: String = if (URLs.Retrofit.sUSE_LIVE_SERVER) URLs.Retrofit.sAPI_LIVE_BASE_URL
        else URLs.Retrofit.sAPI_LOCAL_BASE_URL
//                URLs.Retrofit.sAPI_BASE_LIVE_URL

        val auth = JDAApplication.mInstance.getProfile()
        if (auth != null) {
            setAuthorization(auth.result?.authToken!!)
//            setAuthorization("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDhmN2IyMzc5OTlmZTIyYThkNGU4OGEiLCJpYXQiOjE2MjAyMjI3ODAsImV4cCI6MTYyMDMwOTE4MH0.YLDh3aFlPKKVMOeUwT--3-LOnXn5c1HRtGdtuO-Vj9s")
//            setAuthorization("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDkxMjBkNGE2OGU0NzZhZjg3YTNhMTkiLCJpYXQiOjE2MjAxMjM4NjAsImV4cCI6MTYyMDIxMDI2MH0.axWMi5fLhMN-sT8BYULhZQCm-Tgf0Rtl6qMcPs01sBw")
//            setAuthorization("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDkxMDdjNTVhMmU1ZjRkZGRhOTYwMDYiLCJpYXQiOjE2MjAxMTc0NDUsImV4cCI6MTYyMDIwMzg0NX0.L-JFzrakfHKYkgAU9BosQDzJFiz96957I1_L0gZXiGw")
            Log.e("Token", auth.result?.authToken!!)
            Log.i(Constants.LOG_TAG, "Token:" + auth.result?.authToken!!)
            val params = Bundle().apply {
                putString("Token", auth.result?.authToken!!)
            }
            FirebaseLogsUtil.setLogs(Constants.LOG_TAG, params)
        }

        if (mRetrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            mRetrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .apply {
                        okHttpClient.addInterceptor(logging)
                        client(okHttpClient.build())
                    }
                    .build()
        }
        return mRetrofit!!
    }


    fun getClientForFacebookProfilePic(): Retrofit {
        val baseUrl: String = URLs.Retrofit.sFacebookProfilePicUrl
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .apply {
                    client(okHttpClientForFacebookProfilePic)
                }
                .build()
    }

//    fun getClient(): Retrofit {
//        val baseUrl: String = if (URLs.Retrofit.sUSE_LOCAL_SERVER) {
//            URLs.Retrofit.sAPI_BASE_URL
//        } else {
//            URLs.Retrofit.sAPI_BASE_URL_LIVE
//        }
//        if (mRetrofit == null) {
//            mRetrofit = Retrofit.Builder()
//                .baseUrl(baseUrl)
////                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(io.reactivex.schedulers.Schedulers.io()))
//                .addConverterFactory(GsonConverterFactory.create()) //GsonBuilder().serializeNulls().create() for sending null in body params
//                .client(okHttpClient)
//                .build()
//        }
//        return mRetrofit!!
//    }

    fun setAuthorization(pAuthorization: String) {
        mAuthorization = pAuthorization
    }
}