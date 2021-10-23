package de.adschmidt.songskipper.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class SongskipperBackendApplication {
}

fun main(args: Array<String>) {
	runApplication<SongskipperBackendApplication>(*args)
}

