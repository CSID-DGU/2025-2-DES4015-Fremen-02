package com.example.iace2_frontend.util

import java.util.regex.Pattern

/**
 * 개인정보 마스킹 유틸리티
 * 
 * 전화번호, 주민번호, 계좌번호 등 민감한 정보를 마스킹 처리합니다.
 */
object DataMaskingUtil {
    
    /**
     * 전화번호 패턴 (010-1234-5678, 01012345678 등)
     */
    private val PHONE_PATTERN = Pattern.compile(
        "(01[0-9])[-\\s]?(\\d{3,4})[-\\s]?(\\d{4})"
    )
    
    /**
     * 주민등록번호 패턴 (123456-1234567)
     */
    private val RRN_PATTERN = Pattern.compile(
        "(\\d{6})[-]?(\\d{7})"
    )
    
    /**
     * 계좌번호 패턴 (숫자로만 구성된 10자리 이상)
     */
    private val ACCOUNT_PATTERN = Pattern.compile(
        "\\d{10,}"
    )
    
    /**
     * 이메일 패턴
     */
    private val EMAIL_PATTERN = Pattern.compile(
        "([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
    )
    
    /**
     * 카드번호 패턴 (4자리-4자리-4자리-4자리)
     */
    private val CARD_PATTERN = Pattern.compile(
        "(\\d{4})[-\\s]?(\\d{4})[-\\s]?(\\d{4})[-\\s]?(\\d{4})"
    )
    
    /**
     * 메시지 전체에서 개인정보를 마스킹 처리
     * 
     * @param message 원본 메시지
     * @return 마스킹된 메시지
     */
    fun maskSensitiveData(message: String): String {
        var maskedMessage = message
        
        // 전화번호 마스킹
        maskedMessage = maskPhoneNumbers(maskedMessage)
        
        // 주민등록번호 마스킹
        maskedMessage = maskRRN(maskedMessage)
        
        // 계좌번호 마스킹
        maskedMessage = maskAccountNumbers(maskedMessage)
        
        // 카드번호 마스킹
        maskedMessage = maskCardNumbers(maskedMessage)
        
        // 이메일 마스킹 (선택적 - 필요시 주석 해제)
        // maskedMessage = maskEmails(maskedMessage)
        
        return maskedMessage
    }
    
    /**
     * 전화번호 마스킹 (010-1234-5678 -> 010-****-5678)
     */
    private fun maskPhoneNumbers(message: String): String {
        val matcher = PHONE_PATTERN.matcher(message)
        val result = StringBuffer()
        
        while (matcher.find()) {
            val fullMatch = matcher.group(0)
            val firstPart = matcher.group(1) // 010
            val middlePart = matcher.group(2) // 1234
            val lastPart = matcher.group(3) // 5678
            
            // 중간 부분만 마스킹
            val masked = "$firstPart-${"*".repeat(middlePart.length)}-$lastPart"
            matcher.appendReplacement(result, masked)
        }
        matcher.appendTail(result)
        
        return result.toString()
    }
    
    /**
     * 주민등록번호 마스킹 (123456-1234567 -> 123456-*******)
     */
    private fun maskRRN(message: String): String {
        val matcher = RRN_PATTERN.matcher(message)
        val result = StringBuffer()
        
        while (matcher.find()) {
            val firstPart = matcher.group(1) // 앞 6자리
            val masked = "$firstPart-*******"
            matcher.appendReplacement(result, masked)
        }
        matcher.appendTail(result)
        
        return result.toString()
    }
    
    /**
     * 계좌번호 마스킹 (앞 4자리와 뒤 4자리만 남기고 중간 마스킹)
     */
    private fun maskAccountNumbers(message: String): String {
        val matcher = ACCOUNT_PATTERN.matcher(message)
        val result = StringBuffer()
        
        while (matcher.find()) {
            val accountNumber = matcher.group(0)
            
            // 전화번호나 카드번호 패턴과 겹치지 않는 경우만 처리
            if (!PHONE_PATTERN.matcher(accountNumber).matches() && 
                !CARD_PATTERN.matcher(accountNumber).matches()) {
                
                if (accountNumber.length >= 10) {
                    val prefix = accountNumber.substring(0, 4)
                    val suffix = accountNumber.substring(accountNumber.length - 4)
                    val masked = "$prefix${"*".repeat(accountNumber.length - 8)}$suffix"
                    matcher.appendReplacement(result, masked)
                }
            }
        }
        matcher.appendTail(result)
        
        return result.toString()
    }
    
    /**
     * 카드번호 마스킹 (1234-5678-9012-3456 -> 1234-****-****-3456)
     */
    private fun maskCardNumbers(message: String): String {
        val matcher = CARD_PATTERN.matcher(message)
        val result = StringBuffer()
        
        while (matcher.find()) {
            val firstPart = matcher.group(1) // 앞 4자리
            val lastPart = matcher.group(4) // 뒤 4자리
            val masked = "$firstPart-****-****-$lastPart"
            matcher.appendReplacement(result, masked)
        }
        matcher.appendTail(result)
        
        return result.toString()
    }
    
    /**
     * 이메일 마스킹 (test@example.com -> t***@example.com)
     */
    private fun maskEmails(message: String): String {
        val matcher = EMAIL_PATTERN.matcher(message)
        val result = StringBuffer()
        
        while (matcher.find()) {
            val localPart = matcher.group(1)
            val domain = matcher.group(2)
            
            val maskedLocal = if (localPart.length > 1) {
                "${localPart[0]}${"*".repeat(localPart.length - 1)}"
            } else {
                "*"
            }
            
            val masked = "$maskedLocal@$domain"
            matcher.appendReplacement(result, masked)
        }
        matcher.appendTail(result)
        
        return result.toString()
    }
}

