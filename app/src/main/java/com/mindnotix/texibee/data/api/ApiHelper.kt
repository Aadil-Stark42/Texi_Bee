package com.mindnotix.texibee.data.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun User_Register() = apiService.User_Register("","","","","")
}