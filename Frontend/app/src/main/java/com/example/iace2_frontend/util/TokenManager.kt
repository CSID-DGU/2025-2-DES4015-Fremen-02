package com.example.iace2_frontend.util

import android.content.Context
import android.content.SharedPreferences

/**
 * 인증 토큰 관리 유틸리티
 * 
 * SharedPreferences를 사용하여 토큰을 저장하고 관리합니다.
 */
object TokenManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "auth_token"
    
    private var prefs: SharedPreferences? = null
    
    /**
     * 초기화 (Application 클래스나 MainActivity에서 호출)
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * 토큰 저장
     */
    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }
    
    /**
     * 토큰 조회
     */
    fun getToken(): String? {
        return prefs?.getString(KEY_TOKEN, null)
    }
    
    /**
     * 토큰 삭제
     */
    fun clearToken() {
        prefs?.edit()?.remove(KEY_TOKEN)?.apply()
    }
}

