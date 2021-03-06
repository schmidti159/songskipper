package de.adschmidt.songskipper.backend.persistence.repo

import de.adschmidt.songskipper.backend.persistence.model.SpotifyUserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpotifyUserRepo : JpaRepository<SpotifyUserModel, String> {
}