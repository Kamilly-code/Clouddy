package com.clouddy.application.data.network


import javax.inject.Inject

class ApiClient @Inject constructor(private val api: ApiService) {
    fun getApi(): ApiService = api
}