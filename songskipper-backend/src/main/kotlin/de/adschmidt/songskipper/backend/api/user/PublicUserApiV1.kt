package de.adschmidt.songskipper.backend.api.user

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.services.SkipperService
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.servlet.http.HttpServletResponse

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