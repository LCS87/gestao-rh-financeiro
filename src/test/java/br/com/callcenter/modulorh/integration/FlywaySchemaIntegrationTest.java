package br.com.callcenter.modulorh.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;

/**
 * Integracao real: sobe Postgres 15 via Testcontainers, aplica as migracoes
 * Flyway V1-V17 e valida o mapeamento JPA com hibernate ddl-auto=validate.
 * E pulado automaticamente onde nao ha Docker (DockerAvailableCondition).
 */
@ExtendWith(DockerAvailableCondition.class)
@DataJpaTest
@ContextConfiguration(initializers = PostgresIntegrationTest.Initializer.class)
class FlywaySchemaIntegrationTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Test
    @DisplayName("Flyway + Hibernate validate: entidades devem casar com o schema versionado")
    void contextoCarregaComSchemaDoFlyway() {
        // Se chegamos aqui, o Flyway rodou e o ddl-auto=validate passou
        // para TODAS as entidades do monolito (folha, ponto, escalas, comissoes...).
        assertThat(funcionarioRepository.count()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("CRUD de Funcionario contra Postgres real")
    void crudFuncionarioEmPostgresReal() {
        Funcionario f = new Funcionario();
        f.setNome("Operador Teste");
        f.setCpf("12345678900");
        f.setEmail("operador.teste@callcenter.local");
        f.setDataAdmissao(LocalDate.of(2026, 1, 5));
        f.setSalarioBase(2500.0);
        f.setStatus(Funcionario.StatusFuncionario.ATIVO);

        Funcionario salvo = funcionarioRepository.saveAndFlush(f);
        assertThat(salvo.getId()).isNotNull();
        assertThat(funcionarioRepository.findByCpf("12345678900")).isPresent();
        assertThat(funcionarioRepository.countByStatus(Funcionario.StatusFuncionario.ATIVO)).isEqualTo(1);

        UUID id = salvo.getId();
        funcionarioRepository.deleteById(id);
        funcionarioRepository.flush();
        assertThat(funcionarioRepository.findById(id)).isEmpty();
    }
}
