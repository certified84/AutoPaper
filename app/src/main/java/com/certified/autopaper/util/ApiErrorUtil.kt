package com.certified.autopaper.util

import com.certified.autopaper.data.model.APIError
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

class ApiErrorUtil @Inject constructor(private val retrofit: Retrofit) {

    @Throws(IOException::class)
    fun parseError(response: Response<*>): APIError? {
        val converter = retrofit.responseBodyConverter<APIError>(
            APIError::class.java, arrayOfNulls(0)
        )
        val error: APIError? = if (response.errorBody() != null) {
            converter.convert(response.errorBody()!!)
        } else {
            APIError(400, "Unknown error")
        }
        return error
    }
}