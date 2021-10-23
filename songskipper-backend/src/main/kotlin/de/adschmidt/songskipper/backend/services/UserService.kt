package de.adschmidt.songskipper.backend.services

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService {

    fun verifyUserId() : String {
        val userId = getUserId()
        if(userId.isBlank()) {
            throw IllegalStateException("user is not logged in!")
        }
        return userId
    }

    fun getUserId(): String {
        val principal = SecurityContextHolder.getContext().authentication
        if(principal == null || principal is AnonymousAuthenticationToken) {
            return ""
        }
        return principal.name
    }
}