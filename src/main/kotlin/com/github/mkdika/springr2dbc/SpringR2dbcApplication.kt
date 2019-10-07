package com.github.mkdika.springr2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringR2dbcApplication

fun main(args: Array<String>) {
	runApplication<SpringR2dbcApplication>(*args)
}
