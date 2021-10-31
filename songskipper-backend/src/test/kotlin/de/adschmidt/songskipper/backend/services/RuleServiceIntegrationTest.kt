package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.SkipperTest
import de.adschmidt.songskipper.backend.TestConfig.Companion.USER_ID
import de.adschmidt.songskipper.backend.api.Album
import de.adschmidt.songskipper.backend.api.Artist
import de.adschmidt.songskipper.backend.api.Rule
import de.adschmidt.songskipper.backend.api.Track
import de.adschmidt.songskipper.backend.events.UserChangedEvent
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUserModel
import de.adschmidt.songskipper.backend.persistence.repo.RuleRepo
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


@SkipperTest
@kotlinx.coroutines.ExperimentalCoroutinesApi
internal class RuleServiceIntegrationTest(
) {
    @Autowired
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var ruleRepo: RuleRepo

    @Autowired
    private lateinit var spotifyUserRepo: SpotifyUserRepo

    @BeforeEach
    fun setup() {
        spotifyUserRepo.save(SpotifyUserModel(USER_ID))
    }

    @AfterEach
    fun cleanUp() {
        ruleRepo.deleteAll()
        spotifyUserRepo.deleteAll()
    }

    @Test
    fun `new user adds default rules`() {
        ruleService.onUserChangedEvent(UserChangedEvent(this, USER_ID, false))
        assertThat(ruleService.getAllRules(USER_ID))
            .hasSize(0)

        ruleService.onUserChangedEvent(UserChangedEvent(this, USER_ID, true))
        assertThat(ruleService.getAllRules(USER_ID))
            .hasSize(2)
    }

    @Test
    fun `regexExpressions are evaluated correctly`() {
        val liveRule = ruleService.addRule(USER_ID, Rule(titleExpression = "r:l[iv]{2}e:"))
        val liveRuleWholeWord = ruleService.addRule(USER_ID, Rule(titleExpression = "r:l[iv]{2}e:b"))
        val liveRuleIgnoreCase = ruleService.addRule(USER_ID, Rule(titleExpression = "r:l[iv]{2}e:i"))
        val liveRuleIgnoreCaseWholeWord = ruleService.addRule(USER_ID, Rule(titleExpression = "r:l[iv]{2}e:bi"))

        assertThat(ruleService.findMatchingRules(USER_ID, track("Foo live")))
            .containsExactlyInAnyOrder(liveRule, liveRuleWholeWord, liveRuleIgnoreCase, liveRuleIgnoreCaseWholeWord)
        assertThat(ruleService.findMatchingRules(USER_ID, track("lvie-Bar")))
            .containsExactlyInAnyOrder(liveRule, liveRuleWholeWord, liveRuleIgnoreCase, liveRuleIgnoreCaseWholeWord)
        assertThat(ruleService.findMatchingRules(USER_ID, track("Ignore Case LiVe Matches")))
            .containsExactlyInAnyOrder(liveRuleIgnoreCase, liveRuleIgnoreCaseWholeWord)
        assertThat(ruleService.findMatchingRules(USER_ID, track("liveshow")))
            .containsExactlyInAnyOrder(liveRule, liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("AliVe")))
            .containsExactlyInAnyOrder(liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("AliV3")))
            .isEmpty()
    }

    @Test
    fun `globExpression are evaluated correctly`() {
        val liveRule = ruleService.addRule(USER_ID, Rule(titleExpression = "g:l??e:"))
        val liveRuleWholeWord = ruleService.addRule(USER_ID, Rule(titleExpression = "g:l??e:b"))
        val liveRuleIgnoreCase = ruleService.addRule(USER_ID, Rule(titleExpression = "g:l*e:i"))
        val liveRuleIgnoreCaseWholeWord = ruleService.addRule(USER_ID, Rule(titleExpression = "g:l?*e:bi"))

        assertThat(ruleService.findMatchingRules(USER_ID, track("Foo live")))
            .containsExactlyInAnyOrder(liveRule, liveRuleWholeWord, liveRuleIgnoreCase, liveRuleIgnoreCaseWholeWord)
        assertThat(ruleService.findMatchingRules(USER_ID, track("lvie-Bar")))
            .containsExactlyInAnyOrder(liveRule, liveRuleWholeWord, liveRuleIgnoreCase, liveRuleIgnoreCaseWholeWord)
        assertThat(ruleService.findMatchingRules(USER_ID, track("Ignore Case LiVe Matches")))
            .containsExactlyInAnyOrder(liveRuleIgnoreCase, liveRuleIgnoreCaseWholeWord)
        assertThat(ruleService.findMatchingRules(USER_ID, track("liveshow")))
            .containsExactlyInAnyOrder(liveRule, liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("AliVE")))
            .containsExactlyInAnyOrder(liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("AliV3")))
            .isEmpty()
    }

    @Test
    fun `regexCharacters in glob are escaped correctly`() {
        val globRule = ruleService.addRule(USER_ID, Rule(titleExpression = "g:T.[abc](x|y){2}e*f:"))
        val regexRule = ruleService.addRule(USER_ID, Rule(titleExpression = "r:T.[abc](x|y){2}e*f:"))

        assertThat(ruleService.findMatchingRules(USER_ID, track("T.[abc](x|y){2}e_random_content_f")))
            .containsExactlyInAnyOrder(globRule)
        assertThat(ruleService.findMatchingRules(USER_ID, track("Txaxxeeeef")))
            .containsExactlyInAnyOrder(regexRule)
        assertThat(ruleService.findMatchingRules(USER_ID, track("T.[abc](x|y){2}e*f")))
            .containsExactlyInAnyOrder(globRule)
        assertThat(ruleService.findMatchingRules(USER_ID, track("T_cyxf")))
            .containsExactlyInAnyOrder(regexRule)
        assertThat(ruleService.findMatchingRules(USER_ID, track("T_cyxe")))
            .isEmpty()
    }

    @Test
    fun `titleExpressions match on track titles`() {
        val liveRule = ruleService.addRule(USER_ID, Rule(titleExpression = "g:live:"))

        assertThat(ruleService.findMatchingRules(USER_ID, track("alive")))
            .containsExactly(liveRule)
        assertThat(ruleService.findMatchingRules(USER_ID, track(artist = "alive")))
            .isEmpty()
        assertThat(ruleService.findMatchingRules(USER_ID, track(album = "alive")))
            .isEmpty()
    }

    @Test
    fun `artistExpressions match on artist titles`() {
        val liveRule = ruleService.addRule(USER_ID, Rule(artistExpression = "g:live:"))

        assertThat(ruleService.findMatchingRules(USER_ID, track("alive")))
            .isEmpty()
        assertThat(ruleService.findMatchingRules(USER_ID, track(artist = "alive")))
            .containsExactly(liveRule)
        assertThat(ruleService.findMatchingRules(USER_ID, track(album = "alive")))
            .isEmpty()
    }

    @Test
    fun `albumExpressions match on album titles`() {
        val liveRule = ruleService.addRule(USER_ID, Rule(albumExpression = "g:live:"))

        assertThat(ruleService.findMatchingRules(USER_ID, track("alive")))
            .isEmpty()
        assertThat(ruleService.findMatchingRules(USER_ID, track(artist = "alive")))
            .isEmpty()
        assertThat(ruleService.findMatchingRules(USER_ID, track(album = "alive")))
            .containsExactly(liveRule)
    }

    private fun track(title: String = "", artist: String = "", album: String = ""): Track {
        return Track(title, "", 0, listOf(Artist(artist, "")), Album(album, "", ""))
    }
}