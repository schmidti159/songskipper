package de.adschmidt.songskipper.backend.api.skipper

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.services.SkipperService
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

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