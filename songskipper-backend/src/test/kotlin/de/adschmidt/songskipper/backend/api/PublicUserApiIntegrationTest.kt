package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.SkipperTest
import de.adschmidt.songskipper.backend.TestConfig.Companion.USER_ID
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl


@SkipperTest
@kotlinx.coroutines.ExperimentalCoroutinesApi
internal class PublicUserApiIntegrationTest(
) {
    @Autowired
    private lateinit var publicUserApi: PublicUserApiV1

    @Test
    fun `user id is returned when user is logged in`() {
        runBlockingTest {
            val principal = mock(Authentication::class.java)
            `when`(principal.name).thenReturn(USER_ID)
            SecurityContextHolder.setContext(SecurityContextImpl(principal))
            assertThat(publicUserApi.getUserId()).isEqualTo(USER_ID)
        }
    }

    @Test
    fun `empty string is returned if user is not logged in`() {
        runBlockingTest {
            assertThat(publicUserApi.getUserId()).isEqualTo("")
        }
    }
}