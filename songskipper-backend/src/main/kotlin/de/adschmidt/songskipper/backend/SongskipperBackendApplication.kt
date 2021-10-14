package de.adschmidt.songskipper.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@SpringBootApplication
class SongskipperBackendApplication {
}

fun main(args: Array<String>) {
	runApplication<SongskipperBackendApplication>(*args)
}

