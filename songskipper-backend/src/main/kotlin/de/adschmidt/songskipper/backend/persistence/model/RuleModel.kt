package de.adschmidt.songskipper.backend.persistence.model

import de.adschmidt.songskipper.backend.api.Rule
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "rule")
class RuleModel(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,
    var userId: String? = null,
    var title: String? = null,
    var titleExpression: String? = null,
    var artistExpression: String? = null,
    var albumExpression: String? = null
) {
    constructor(rule: Rule, userId: String) : this(
        rule.id,
        userId,
        rule.title,
        rule.titleExpression,
        rule.artistExpression,
        rule.albumExpression
    )

    fun toRule(): Rule {
        return Rule(id, title, titleExpression, artistExpression, albumExpression)
    }

}