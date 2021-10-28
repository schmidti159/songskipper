package de.adschmidt.songskipper.backend.spotify

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.UserChangedEvent
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUserModel
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.time.temporal.ChronoUnit.MINUTES
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
    private val clientService: OAuth2AuthorizedClientService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : GenericFilterBean(), Loggable {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        val userName = SecurityContextHolder.getContext()?.authentication?.name
        if(userName != null) {
            val oldUser = spotifyUserRepo.findById(userName)
            val user = oldUser.orElse(SpotifyUserModel(userName))
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication is OAuth2AuthenticationToken) {
                // the user is authenticated
                val client: OAuth2AuthorizedClient = clientService.loadAuthorizedClient(
                    authentication.authorizedClientRegistrationId,
                    authentication.name
                )
                // refresh 10 min before it expires
                val newRefreshAt = client.accessToken.expiresAt?.minus(10, MINUTES)
                if (newRefreshAt != null && (user.refreshAt == null || user.refreshAt!!.isBefore(newRefreshAt))) {
                    user.accessToken = client.accessToken.tokenValue
                    user.refreshAt = newRefreshAt
                    user.refreshToken = client.refreshToken?.tokenValue ?: user.refreshToken
                    spotifyUserRepo.save(user)
                    applicationEventPublisher.publishEvent(UserChangedEvent(this, user.id, oldUser.isEmpty))
                    //logger().info("updated user from context: {}, access token will be refreshed at: {}", user.id, user.refreshAt)
                }
            }
        }
        chain.doFilter(request, response)
    }
}