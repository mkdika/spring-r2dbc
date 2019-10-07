package com.github.mkdika.springr2dbc.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import java.time.Duration
import javax.annotation.PreDestroy

@Configuration
class DatasourceConfig {

    @Autowired
    private lateinit var pool: ConnectionPool

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun databaseConnectionFactory(
        @Value("\${r2dbc.host}") databaseHost: String,
        @Value("\${r2dbc.port}") databasePort: Int,
        @Value("\${r2dbc.name}") databaseName: String,
        @Value("\${r2dbc.username}") databaseUsername: String,
        @Value("\${r2dbc.password}") databasePassword: String
    ): ConnectionFactory {
        return PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(databaseHost)
                .port(databasePort)
                .database(databaseName)
                .username(databaseUsername)
                .password(databasePassword)
                .build())
    }

    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun connectionPooling(
        connectionFactory: ConnectionFactory,
        @Value("\${r2dbc.pool.initial-size}") initialSize: Int,
        @Value("\${r2dbc.pool.max-size}") maxSize: Int,
        @Value("\${r2dbc.pool.max-idle-time}") maxIdleTime: Long,
        @Value("\${r2dbc.pool.validation-query}") validationQuery: String
    ): ConnectionPool {
        val configuration: ConnectionPoolConfiguration = ConnectionPoolConfiguration.builder(connectionFactory)
            .maxIdleTime(Duration.ofMinutes(maxIdleTime))
            .initialSize(initialSize)
            .maxSize(20)
            .validationQuery(validationQuery)
            .build()
        return ConnectionPool(configuration)
    }

    @PreDestroy
    fun destroy() {
        // Destroy the connection pool at apps graceful shutdown
        pool?.let { if (!it.isDisposed) it.dispose() }
    }
}