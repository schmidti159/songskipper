package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.TestConfig
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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@Import(TestConfig::class)
@ActiveProfiles("test")
internal class RuleServiceIntegrationTest(
) {
    companion object {
        val USER_ID = "user"
    }

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
    fun `rules can be added, modified and deleted`() {
        val fooRule = ruleService.addRule(USER_ID, Rule(titleExpression = "fooRule"))
        assertThat(fooRule.titleExpression)
            .isEqualTo("fooRule")
        assertThat(fooRule.albumExpression)
            .isNull()
        assertThat(fooRule.artistExpression)
            .isNull()
        assertThat(ruleService.getRule(USER_ID, fooRule.id!!))
            .isEqualTo(fooRule)
        assertThat(ruleService.getRule("OTHER_USER", fooRule.id!!))
            .isNull()
        assertThat(ruleService.getAllRules(USER_ID))
            .containsExactly(fooRule)
        assertThat(ruleService.getAllRules("OTHER_USER"))
            .isEmpty()

        val barRule = ruleService.addRule(USER_ID, Rule(albumExpression = "barRule"))
        assertThat(ruleService.getAllRules(USER_ID))
            .containsExactlyInAnyOrder(fooRule, barRule)

        val modifiedFooRule = Rule(titleExpression = "modifiedFooExpression")
        val bazRule = ruleService.modifyRule(USER_ID, modifiedFooRule, fooRule.id!!)
        assertThat(bazRule?.id).isEqualTo(fooRule.id)
        assertThat(ruleService.getAllRules(USER_ID))
            .containsExactlyInAnyOrder(bazRule, barRule)


        ruleService.deleteRule(USER_ID, fooRule.id!!)
        assertThat(ruleService.getAllRules(USER_ID))
            .containsExactly(barRule)

        ruleService.deleteRule(USER_ID, barRule.id!!)
        assertThat(ruleService.getAllRules(USER_ID))
            .isEmpty()
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
        assertThat(ruleService.findMatchingRules(USER_ID, track("Al!Ve")))
            .isEmpty()
    }

    @Test
    fun `globExpression are evaluated correctly`() {
        val liveRule = ruleService.addRule(USER_ID, Rule(titleExpression = "g:l??e:"))
        val liveRuleIgnoreCase = ruleService.addRule(USER_ID, Rule(titleExpression = "r:l*e:i"))

        assertThat(ruleService.findMatchingRules(USER_ID, track("Foo live")))
            .containsExactlyInAnyOrder(liveRule, liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("lvie-Bar")))
            .containsExactlyInAnyOrder(liveRule, liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("Ignore Case LiVe Matches")))
            .containsExactlyInAnyOrder(liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("liveshow")))
            .containsExactlyInAnyOrder(liveRule, liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("AliVE")))
            .containsExactlyInAnyOrder(liveRuleIgnoreCase)
        assertThat(ruleService.findMatchingRules(USER_ID, track("Al!V3")))
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