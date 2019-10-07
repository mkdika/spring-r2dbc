# Spring R2DBC

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](/LICENSE)

Spring Boot 2 WebFlux and Asynchronous RDBMS Connection using R2DBC, with CRUD as usecase.

### Stacks

- [Kotlin 1.3.50](https://blog.jetbrains.com/kotlin/2019/08/kotlin-1-3-50-released/)
- [Spring Boot 2.2.x](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.2-Release-Notes)
- [r2dbc 0.8.0.RC1](https://r2dbc.io/), Reactive Relational Database Connectivity.
- [Netty 4.1](https://netty.io/)
- Postgresql 10
- Maven 3.6.0
- Vagrant + Ansible, for bootstrap & provisioning the development environment.

### Running application

- __Provision dev env with Vagrant__

  ```bash
  # Starting vagrant vm and provision for first time
  vagrant up

  # Re-provisioning vagrant
  vagrant provision
  ```

- __Running application__

  ```bash
  ./mvnw spring-boot:run
  ```

  Url is: `http://localhost:8084/`


### REST API Endpoints

| HTTP Method | Path          | Description                        |
| ----------- | --------------| -----------------------------------|
| GET         | /persons      | Get all existing person data.      |
| GET         | /persons/{id} | Get existing person data by Id.    |
| POST        | /persons      | Insert new person data.            |
| PUT         | /persons/{id} | Update existing person data by Id. |
| DELETE      | /persons/{id} | Delete existing person data by Id. |
| GET         | /ping         | Testing endpoing.                  |


## License

License under the MIT license. See [LICENSE](/LICENSE) file.<Paste>

