package com.mindnotix.texibee.data.repository

import com.mindnotix.texibee.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun User_Register() = apiHelper.User_Register()
}