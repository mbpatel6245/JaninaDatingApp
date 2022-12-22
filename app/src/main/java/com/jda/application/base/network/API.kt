package com.jda.application.base.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface API {
    @POST
    fun request(@Url url: String?, @Body pRequest: Any?): Call<Any?>?

    @PUT
    fun putRequest(@Url url: String?, @Body pRequest: Any?): Call<Any?>?

    @GET
    fun getRequest(@Url url: String?, @QueryMap options: @JvmSuppressWildcards Map<String, Any>): Call<Any?>?

    @Multipart
    @POST
    fun updateFile(@Url url: String?, @Part image: MultipartBody.Part?): Call<Any?>?

    @POST
    fun updateFile(@Url url: String?, @Body image: RequestBody?): Call<Any?>?

    @DELETE
    fun deleteFile(@Url url: String?,@QueryMap options: @JvmSuppressWildcards Map<String, Any>): Call<Any?>?

    @DELETE
    fun deleteGallery(@Url url: String?): Call<Any?>?
}
