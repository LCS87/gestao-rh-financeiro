package br.com.callcenter.modulorh.integration;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Infraestrutura compartilhada dos testes de integracao: container unico de
 * Postgres 15 (singleton por JVM) + override de propriedades apontando o
 * datasource para o container, com Flyway ligado e Hibernate em modo validate.
 */
final class PostgresIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("gestao_rh_financeiro_test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        POSTGRES.start();
    }

    private PostgresIntegrationTest() {
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRES.getUsername(),
                    "spring.datasource.password=" + POSTGRES.getPassword(),
                    "spring.flyway.enabled=true",
                    "spring.jpa.hibernate.ddl-auto=validate"
            ).applyTo(context.getEnvironment());
        }
    }
}
