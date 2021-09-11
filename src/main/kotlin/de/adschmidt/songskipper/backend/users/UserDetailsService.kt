package de.adschmidt.songskipper.backend.users

import de.adschmidt.songskipper.backend.users.persistence.User
import de.adschmidt.songskipper.backend.users.persistence.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service


@Service
class UserDetailsService(
    val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username)
        return UserPrincipal(user)
    }

    class UserPrincipal(val user: User) : UserDetails {
        override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
            return mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
        }

        override fun getPassword(): String? {
            return user.password
        }

        override fun getUsername(): String {
            return user.username
        }

        override fun isAccountNonExpired(): Boolean {
            return true
        }

        override fun isAccountNonLocked(): Boolean {
            return true
        }

        override fun isCredentialsNonExpired(): Boolean {
            return true
        }

        override fun isEnabled(): Boolean {
            return true
        }
    }
}


@Component
class DummyUserRunner(
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder) : CommandLineRunner {

    override fun run(vararg args: String?) {
        userRepository.deleteAll()
        val testUser = User(username = "test", password = passwordEncoder.encode("password123"))
        userRepository.save(testUser)
    }

}
