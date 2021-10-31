package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.api.Expression
import de.adschmidt.songskipper.backend.api.ExpressionFlag.IGNORE_CASE
import de.adschmidt.songskipper.backend.api.ExpressionFlag.WHOLE_WORD
import de.adschmidt.songskipper.backend.api.ExpressionType.GLOB
import de.adschmidt.songskipper.backend.api.Rule
import de.adschmidt.songskipper.backend.api.Track
import de.adschmidt.songskipper.backend.events.UserChangedEvent
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.persistence.model.RuleModel
import de.adschmidt.songskipper.backend.persistence.repo.RuleRepo
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class RuleService(
    private val ruleRepo: RuleRepo
) : Loggable {

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
        // make sure to generate a unique id for this rule
        ruleModel.id = null
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
        val liveTrackRule = RuleModel(null, userId, "Live Tracks", "g:live:bi", null, null)
        val liveAlbumRule = RuleModel(null, userId, "Live Albums", null, null, "g:live:bi")
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
        var regexPattern = expr.pattern
        if (expr.type == GLOB) {
            regexPattern = globToRegex(regexPattern)
        }
        if (expr.hasFlag(WHOLE_WORD)) {
            regexPattern = "\\b${regexPattern}\\b"
        }
        val regexOptions =
            if (expr.hasFlag(IGNORE_CASE))
                setOf(RegexOption.IGNORE_CASE)
            else emptySet()
        return content.contains(regexPattern.toRegex(regexOptions))
    }

    private fun globToRegex(pattern: String): String {
        val result = StringBuilder()
        var isIgnoring = false
        for (char in pattern.toCharArray()) {
            if (char == '?') {
                if (isIgnoring) {
                    result.append("\\E")
                    isIgnoring = false
                }
                result.append('.')
            } else if (char == '*') {
                if (isIgnoring) {
                    result.append("\\E")
                    isIgnoring = false
                }
                result.append(".*")
            } else {
                if (!isIgnoring) {
                    result.append("\\Q")
                    isIgnoring = true
                }
                result.append(char)
            }
        }
        if (isIgnoring) {
            result.append("\\E")
        }
        return result.toString()
    }

}