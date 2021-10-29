package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.SkipperTest
import de.adschmidt.songskipper.backend.TestConfig.Companion.USER_ID
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUserModel
import de.adschmidt.songskipper.backend.persistence.repo.RuleRepo
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.services.RuleService
import de.adschmidt.songskipper.backend.services.UserService
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean


@SkipperTest
@kotlinx.coroutines.ExperimentalCoroutinesApi
internal class RuleApiIntegrationTest(
) {
    @Autowired
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var ruleApi: RuleApiV1

    @Autowired
    private lateinit var ruleRepo: RuleRepo

    @Autowired
    private lateinit var spotifyUserRepo: SpotifyUserRepo

    @MockBean
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        spotifyUserRepo.save(SpotifyUserModel(USER_ID))
        `when`(userService.verifyUserId()).thenReturn(USER_ID)
    }

    @AfterEach
    fun cleanUp() {
        ruleRepo.deleteAll()
        spotifyUserRepo.deleteAll()
    }

    @Test
    fun `rules can be added, modified and deleted`() {
        runBlockingTest {
            val fooRule = ruleApi.addRule(Rule(titleExpression = "fooRule"))
            assertThat(fooRule.titleExpression)
                .isEqualTo("fooRule")
            assertThat(fooRule.albumExpression)
                .isNull()
            assertThat(fooRule.artistExpression)
                .isNull()
            assertThat(ruleApi.getRule(fooRule.id!!))
                .isEqualTo(fooRule)
            assertThat(ruleService.getRule("OTHER_USER", fooRule.id!!))
                .isNull()
            assertThat(ruleApi.getRules())
                .containsExactly(fooRule)
            assertThat(ruleService.getAllRules("OTHER_USER"))
                .isEmpty()

            val barRule = ruleApi.addRule(Rule(albumExpression = "barRule"))
            assertThat(ruleApi.getRules())
                .containsExactlyInAnyOrder(fooRule, barRule)

            val modifiedFooRule = Rule(titleExpression = "modifiedFooExpression")
            val bazRule = ruleApi.modifyRule(modifiedFooRule, fooRule.id!!)
            assertThat(bazRule?.id).isEqualTo(fooRule.id)
            assertThat(ruleApi.getRules())
                .containsExactlyInAnyOrder(bazRule, barRule)

            ruleApi.deleteRule(fooRule.id!!)
            assertThat(ruleApi.getRules())
                .containsExactly(barRule)

            ruleApi.deleteRule(barRule.id!!)
            assertThat(ruleApi.getRules())
                .isEmpty()
        }
    }
}