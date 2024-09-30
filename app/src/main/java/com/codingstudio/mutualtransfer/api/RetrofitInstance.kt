package com.codingstudio.mutualtransfer.api

import android.util.Log
import com.codingstudio.mutualtransfer.BuildConfig
import com.codingstudio.mutualtransfer.utils.Constants
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object{

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()

            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val interceptor = object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request().newBuilder().addHeader("Authorization", getAuthorizationToken()).build()
                    return chain.proceed(request = request)
                }
            }


            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(interceptor)
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

        private fun getAuthorizationToken() : String {

            val millis = System.currentTimeMillis()
            val seconds = millis / 1000
            val iat = seconds

            val jwt = Jwts.builder()
                .claim("iss", "localhost")
                .claim("iat", iat)
                .claim("nbf", iat)
                .claim("exp", iat + 60)
                .claim("aud", "AccessToken")
                .signWith(SignatureAlgorithm.HS512, BuildConfig.JWT_SECRET_KEY.toByteArray())
                .compact()

            Log.e("getAuthorizationToken", "getAuthorizationToken: $jwt")

            return "Bearer $jwt"
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