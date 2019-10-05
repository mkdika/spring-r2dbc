package com.github.mkdika.springr2dbc.repository

import com.github.mkdika.springr2dbc.model.Person
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface PersonRepository {

    fun findById(id: String): Mono<Person>
    fun findAll(): Flux<Person>
    fun upsert(person: Person): Mono<Person>
    fun delete(id: String): Mono<Void>
}
