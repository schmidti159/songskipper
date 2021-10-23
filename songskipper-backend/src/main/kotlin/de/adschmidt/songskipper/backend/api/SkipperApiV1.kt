package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.services.SkipperService
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/skipper/v1")
class SkipperApiV1(
    private val skipperService: SkipperService,
    private val userService: UserService
) : Loggable {

    @GetMapping("/start")
    suspend fun startSkipping() {
        skipperService.setSkipperState(userService.verifyUserId(), true)
    }
    @GetMapping("/stop")
    suspend fun stopSkipping() {
        skipperService.setSkipperState(userService.verifyUserId(), false)
    }
    @GetMapping("/active")
    suspend fun active(): Boolean {
        return skipperService.skipperActive(userService.verifyUserId())
    }

}