package de.adschmidt.songskipper.backend

import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Import(TestConfig::class)
@ActiveProfiles("test")
class SongskipperBackendApplicationTests {

	@Test
	fun contextLoads() {
	}

}
