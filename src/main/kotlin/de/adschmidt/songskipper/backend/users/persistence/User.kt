package de.adschmidt.songskipper.backend.users.persistence

import de.adschmidt.songskipper.backend.persistence.AbstractBaseEntity
import java.util.*
import javax.persistence.Entity

@Entity
class User(
    id: UUID? = null,
    val username : String,
    val password : String? = null,
    val refreshToken : String? = null,
    val expiresIn: Int? = null
): AbstractBaseEntity(id);