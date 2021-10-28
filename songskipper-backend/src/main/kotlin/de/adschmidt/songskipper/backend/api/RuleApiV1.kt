package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.services.RuleService
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/api/rules/v1")
class RuleApiV1(
    private val userService: UserService,
    private val ruleService: RuleService
) : Loggable {

    @GetMapping("/")
    suspend fun getRules(): List<Rule> {
        return ruleService.getAllRules(userService.verifyUserId())
    }

    @GetMapping("/{ruleId}")
    suspend fun getRule(@PathVariable ruleId: String): Rule? {
        return ruleService.getRule(userService.verifyUserId(), ruleId)
    }

    @PostMapping("/")
    suspend fun addRule(@RequestBody rule: Rule): Rule {
        return ruleService.addRule(userService.verifyUserId(), rule)
    }

    @PutMapping("/{ruleId}")
    suspend fun modifyRule(@RequestBody rule: Rule, @PathVariable ruleId: String): Rule? {
        return ruleService.modifyRule(userService.verifyUserId(), rule, ruleId)
    }

    @DeleteMapping("/{ruleId}")
    suspend fun deleteRule(@PathVariable ruleId: String) {
        ruleService.deleteRule(userService.verifyUserId(), ruleId)
    }

}