package de.adschmidt.songskipper.backend.persistence.repo

import de.adschmidt.songskipper.backend.persistence.model.RuleModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RuleRepo : JpaRepository<RuleModel, String> {

    fun findByUserId(userId: String): List<RuleModel>
}