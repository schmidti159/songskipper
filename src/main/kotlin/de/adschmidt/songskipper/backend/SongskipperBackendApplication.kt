package de.adschmidt.songskipper.backend

import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

@SpringBootApplication
class SongskipperBackendApplication {

}

fun main(args: Array<String>) {
	runApplication<SongskipperBackendApplication>(*args)
}

