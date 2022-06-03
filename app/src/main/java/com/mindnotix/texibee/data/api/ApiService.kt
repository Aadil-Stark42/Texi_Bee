package com.mindnotix.texibee.data.api

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {

    @FormUrlEncoded
    @POST("user_register")
    suspend fun User_Register(
        @Field("name") name: String?,
        @Field("account_password") account_password: String?,
        @Field("email") email: String?,
        @Field("mcc") mcc: String?,
        @Field("mobile") mobile: String?,
    ): ResponseBody


}