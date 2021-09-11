package de.adschmidt.songskipper.backend.controller
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.service.spotify.SpotifyLoginService
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/login")
class LoginController(
    val spotifyLoginService: SpotifyLoginService
) : Loggable {

    @GetMapping("/secure")
    fun secureEndpoint() : String {
        logger().info("called secure")
        return "authenticated successfully"
    }

    @GetMapping("/authenticate-spotify")
    suspend fun startSpotifyAuth(response: ServerHttpResponse) {
        // redirect to the correct uri for spotify auth
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT)
        response.headers.location = spotifyLoginService.authCodeUri().awaitFirst()
    }

    @GetMapping("/spotify-redirect")
    suspend fun spotifyRedirect(@RequestParam code: String, @RequestParam state: String, @RequestParam error: String?) : String {
        if(error != null) {
            return "Authentication was aborted: "+error+"\n" +
                    "Please try again!";
        }
        spotifyLoginService.authTokens(code)
        return ""
    }

}