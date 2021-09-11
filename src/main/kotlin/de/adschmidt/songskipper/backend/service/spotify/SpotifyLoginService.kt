package de.adschmidt.songskipper.backend.service.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.net.URI
import java.util.*


@Service
class SpotifyLoginService {
    private val clientId = ""
    private val clientSecret = "" // TODO use external config
    private val redirectUri: URI = SpotifyHttpManager.makeUri("http://localhost:8080/spotify-redirect")

    private val scopes = "user-read-playback-state user-modify-playback-state user-read-currently-playing"

    val api = SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(redirectUri)
        .build();

    fun authCodeUri() : Mono<URI> {
        val state = UUID.randomUUID().toString();

        val authCodeUriRequest = api.authorizationCodeUri()
            .scope(scopes)
            .state(state)
            .build()

        return authCodeUriRequest.executeAsync().toMono();
    }

    suspend fun authTokens(code: String) {
        val authCodeRequest = api.authorizationCode(code)
            .build()
        val credentials = authCodeRequest.executeAsync().await()


    }
}
