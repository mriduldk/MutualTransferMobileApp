package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.utils.Constants
import io.grpc.android.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()

            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build()

            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val authAPI by lazy {
            retrofit.create(RetrofitAuthAPI::class.java)
        }
        val userDetailsAPI by lazy {
            retrofit.create(RetrofitUserDetailsAPI::class.java)
        }
        val paymentAPI by lazy {
            retrofit.create(RetrofitPaymentAPI::class.java)
        }
        val searchAPI by lazy {
            retrofit.create(RetrofitSearchAPI::class.java)
        }
        val walletAPI by lazy {
            retrofit.create(RetrofitWalletAPI::class.java)
        }
        val districtAPI by lazy {
            retrofit.create(RetrofitDistrictAPI::class.java)
        }
        val blockAPI by lazy {
            retrofit.create(RetrofitBlockAPI::class.java)
        }
    }

}