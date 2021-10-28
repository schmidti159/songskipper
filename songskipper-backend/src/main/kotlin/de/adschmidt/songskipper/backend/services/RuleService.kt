package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.api.Expression
import de.adschmidt.songskipper.backend.api.ExpressionFlag.IGNORE_CASE
import de.adschmidt.songskipper.backend.api.ExpressionFlag.WHOLE_WORD
import de.adschmidt.songskipper.backend.api.ExpressionType.GLOB
import de.adschmidt.songskipper.backend.api.ExpressionType.REGEX
import de.adschmidt.songskipper.backend.api.Rule
import de.adschmidt.songskipper.backend.api.Track
import de.adschmidt.songskipper.backend.events.UserChangedEvent
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.persistence.model.RuleModel
import de.adschmidt.songskipper.backend.persistence.repo.RuleRepo
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.util.AntPathMatcher

@Service
class RuleService(
    private val ruleRepo: RuleRepo,
    private val antMatcher: AntPathMatcher = AntPathMatcher(),
    private val antMatcherIgnoreCase: AntPathMatcher = AntPathMatcher()
) : Loggable {
    init {
        antMatcher.setCaseSensitive(true)
        antMatcherIgnoreCase.setCaseSensitive(false)
    }

    fun getAllRules(userId: String): List<Rule> {
        return ruleRepo.findByUserId(userId).map { it.toRule() }
    }

    fun getRule(userId: String, ruleId: String): Rule? {
        val ruleModel = ruleRepo.findById(ruleId)
        if (ruleModel.isEmpty || ruleModel.get().userId != userId) {
            logger().warn("rule with id {} does not exist for user {}", ruleId, userId)
            return null
        }
        return ruleModel.get().toRule()
    }

    fun addRule(userId: String, rule: Rule): Rule {
        val ruleModel = RuleModel(rule, userId)
        return ruleRepo.save(ruleModel).toRule()
    }

    fun modifyRule(userId: String, newRule: Rule, ruleId: String): Rule? {
        val rule = getRule(userId, ruleId)
        if (rule == null) {
            logger().warn("cannot modify rule with id {} for user {} (does not exist)", ruleId, userId)
            return null
        }
        val ruleModel = RuleModel(newRule, userId)
        ruleModel.id = ruleId
        return ruleRepo.save(ruleModel).toRule()
    }

    fun deleteRule(userId: String, ruleId: String) {
        val rule = getRule(userId, ruleId)
        if (rule == null) {
            logger().warn("cannot delete rule with id {} for user {} (does not exist)", ruleId, userId)
            return
        }
        ruleRepo.deleteById(ruleId)
    }

    @EventListener
    fun onUserChangedEvent(event: UserChangedEvent) {
        if (event.newUser && event.userId != null) {
            initWithDefaultRules(event.userId)
        }
    }

    private fun initWithDefaultRules(userId: String) {
        val liveTrackRule = RuleModel(null, userId, "r:live:bi", null, null)
        val liveAlbumRule = RuleModel(null, userId, null, null, "r:live:bi")
        ruleRepo.saveAll(listOf(liveTrackRule, liveAlbumRule))
    }

    fun findMatchingRules(userId: String, track: Track): List<Rule> {
        return getAllRules(userId).filter { rule ->
            contains(track.title, rule.titleExpression) ||
                    track.artists.any { artist -> contains(artist.name, rule.artistExpression) } ||
                    contains(track.album.title, rule.albumExpression)
        }
    }

    private fun contains(content: String, expressionString: String?): Boolean {
        val expr = Expression.parse(expressionString)
            ?: return false
        if (expr.type == REGEX) {
            val regexOptions =
                if (expr.hasFlag(IGNORE_CASE)) setOf(RegexOption.IGNORE_CASE)
                else emptySet()
            val regex =
                if (expr.hasFlag(WHOLE_WORD)) "\\b${expr.pattern}\\b"
                else expr.pattern
            return content.contains(regex.toRegex(regexOptions))
        }
        if (expr.type == GLOB) {
            val matcher =
                if (expr.hasFlag(IGNORE_CASE)) antMatcherIgnoreCase
                else antMatcher
            return matcher.match("*${expr.pattern}*", content)
        }
        return false
    }

}