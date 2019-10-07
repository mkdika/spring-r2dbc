package com.github.mkdika.springr2dbc.repository

import com.github.mkdika.springr2dbc.model.Person
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.spi.Connection
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
class PostgresqlPersonRepository(
    private val connectionPool: ConnectionPool
) : PersonRepository {

    override fun findById(id: String): Mono<Person> {
        return connectionPool.create()
            .flatMap { connection ->
                Mono.from(
                    connection
                        .createStatement("SELECT id, firstname, lastname FROM persons WHERE id = $1")
                        .bind("$1", id)
                        .execute()
                ).doFinally {
                    connection.close()
                }
            }
            .map { result ->
                result.map { row, _ ->
                    Person(
                        id = row.get("id", String::class.java).orEmpty(),
                        firstname = row.get("firstname", String::class.java).orEmpty(),
                        lastname = row.get("lastname", String::class.java).orEmpty()
                    )
                }
            }.flatMap { publisher ->
                Mono.from(publisher)
            }
    }


    override fun findAll(): Flux<Person> {
        return connectionPool.create()
            .flatMap { connection ->
                Mono.from(
                    connection
                        .createStatement("SELECT id, firstname, lastname FROM persons")
                        .execute()
                ).doFinally {
                    close(connection)
                }
            }.flatMapMany { result ->
                Flux.from(
                    result.map { row, _ ->
                        Person(
                            id = row.get("id", String::class.java).orEmpty(),
                            firstname = row.get("firstname", String::class.java).orEmpty(),
                            lastname = row.get("lastname", String::class.java).orEmpty()
                        )
                    }
                )
            }
    }

    override fun upsert(person: Person): Mono<Person> {
        val id = if (person.id.isNullOrEmpty()) {
            UUID.randomUUID().toString()
        } else person.id
        return connectionPool.create()
            .flatMap { connection ->
                Mono.from(
                    connection.createStatement("""
                                INSERT INTO persons(id, firstname, lastname)
                                    VALUES ($1, $2, $3) 
                                ON CONFLICT (id) DO 
                                UPDATE SET firstname = $2, 
                                            lastname = $3
                            """.trimIndent())
                        .bind("$1", id)
                        .bind("$2", person.firstname)
                        .bind("$3", person.lastname)
                        .execute()
                ).doFinally {
                    close(connection)
                }
            }
            .map { result ->
                result.map { row, _ ->
                    Person(
                        id = row.get("id", String::class.java).orEmpty(),
                        firstname = row.get("firstname", String::class.java).orEmpty(),
                        lastname = row.get("lastname", String::class.java).orEmpty()
                    )
                }
            }.flatMap { publisher ->
                Mono.from(publisher)
            }
    }

    override fun delete(id: String): Mono<Void> {
        return connectionPool.create()
            .flatMap { connection ->
                Mono.from(connection.beginTransaction())
                    .then(
                        Mono.from(
                            connection.createStatement("DELETE FROM persons WHERE id = $1")
                                .bind("$1", id)
                                .execute()
                        )
                    )
                    .delayUntil {
                        connection.commitTransaction()
                    }
                    .doFinally {
                        close(connection)
                    }
            }.then()
    }

    private fun close(connection: Connection): Mono<Void> {
        return Mono.from(connection.close())
            .then(Mono.empty())
    }
}

