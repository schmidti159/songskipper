package de.adschmidt.songskipper.backend.users.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    @Query("SELECT u from User u WHERE u.username = :username")
    fun findByUsername(username: String): User;
}