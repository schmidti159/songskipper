package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/public/user/v1/")
class PublicUserApiV1(
    val userService: UserService
) : Loggable {

    @GetMapping("/id")
    suspend fun getUserId(): String {
        return userService.getUserId()
    }

}