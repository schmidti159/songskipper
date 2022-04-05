package de.adschmidt.songskipper.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class SongskipperBackendApplication {
}

fun main(args: Array<String>) {
	runApplication<SongskipperBackendApplication>(*args)
}

