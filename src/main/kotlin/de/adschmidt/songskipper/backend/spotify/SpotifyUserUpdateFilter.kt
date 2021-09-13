package de.adschmidt.songskipper.backend.spotify

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUser
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * This filter reads the authenticated user and his tokens from the context and
 * updates them in DB to used by the background processes.
 */
@Component
class SpotifyUserUpdateFilter(
    private val spotifyUserRepo: SpotifyUserRepo,
    private val clientService: OAuth2AuthorizedClientService
) : GenericFilterBean(), Loggable {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        val userName = SecurityContextHolder.getContext()?.authentication?.name
        if(userName != null) {
            val user = spotifyUserRepo.findById(userName).orElse(SpotifyUser(userName))
            val authentication = SecurityContextHolder.getContext().authentication
            if(authentication is OAuth2AuthenticationToken) {
                // the user is authenticated
                val client: OAuth2AuthorizedClient = clientService.loadAuthorizedClient(
                    authentication.authorizedClientRegistrationId,
                    authentication.name
                )
                if (user.accessTokenExpiresAt == null || user.accessTokenExpiresAt!!.isBefore(client.accessToken.expiresAt)) {
                    user.accessToken = client.accessToken.tokenValue
                    user.accessTokenExpiresAt = client.accessToken.expiresAt
                    user.refreshToken = client.refreshToken?.tokenValue ?: user.refreshToken
                    spotifyUserRepo.save(user)
                    logger().info(
                        "updated user from context: {}, access token expires at: {}",
                        user.id,
                        user.accessTokenExpiresAt
                    )
                }
            }
        }
        chain.doFilter(request, response)
    }
}