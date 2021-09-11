package de.adschmidt.songskipper.backend

import com.wrapper.spotify.SpotifyApi
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@SpringBootApplication
@RestController
class SongskipperBackendApplication {
	@GetMapping("/top-artists")
	fun userInfo(user: Principal,
//				 clientService: OAuth2AuthorizedClientService,
				 @RegisteredOAuth2AuthorizedClient authClient : OAuth2AuthorizedClient
	) : String {
//		val token = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
//		val client: OAuth2AuthorizedClient = clientService.loadAuthorizedClient(
//			token.authorizedClientRegistrationId,
//			token.name
//		)
		val topArtists = SpotifyApi.Builder()
			.setAccessToken(authClient.accessToken.tokenValue)
			.build()
			.usersTopArtists.build().execute().items
			.map {it.name}
		return topArtists.toString()
	}
}

fun main(args: Array<String>) {
	runApplication<SongskipperBackendApplication>(*args)
}

