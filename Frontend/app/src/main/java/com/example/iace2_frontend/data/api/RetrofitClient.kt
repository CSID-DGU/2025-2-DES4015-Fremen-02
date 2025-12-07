package com.example.iace2_frontend.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 클라이언트 싱글톤
 * 
 * 백엔드 서버 URL은 환경에 따라 변경할 수 있습니다.
 * - 개발 환경: http://10.0.2.2:8080 (에뮬레이터용)
 * - 실제 기기: http://[백엔드 서버 IP]:8080
 * - 프로덕션: 백엔드 팀원과 협의
 */
object RetrofitClient {
    // 실제 배포 서버 주소
    private const val BASE_URL = "http://43.200.113.167:8080/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // 디버그 모드에서는 상세 로그, 릴리즈 모드에서는 로그 없음
        // BuildConfig 대신 빌드 타입에 따라 자동으로 설정됨
        level = HttpLoggingInterceptor.Level.BODY // 개발 중에는 항상 로그 표시
        // 릴리즈 빌드 시에는 Level.NONE으로 변경 권장
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            // 공통 헤더 추가
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(180, TimeUnit.SECONDS)  // 3분
        .readTimeout(180, TimeUnit.SECONDS)     // 3분
        .writeTimeout(180, TimeUnit.SECONDS)    // 3분
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    
    /**
     * 서버 URL 변경 (런타임에 변경 가능)
     */
    fun updateBaseUrl(newBaseUrl: String) {
        // 필요시 동적으로 URL 변경하는 로직 추가
    }
}

