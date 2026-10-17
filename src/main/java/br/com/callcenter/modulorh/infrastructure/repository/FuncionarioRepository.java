package br.com.callcenter.modulorh.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.callcenter.modulorh.domain.entity.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {

    Optional<Funcionario> findByCpf(String cpf);

    List<Funcionario> findByStatus(Funcionario.StatusFuncionario status);

    List<Funcionario> findByDepartamentoId(UUID departamentoId);

    List<Funcionario> findByDataAdmissaoBeforeAndStatus(LocalDate dataAdmissao, Funcionario.StatusFuncionario status);

    long countByDataAdmissao(LocalDate dataAdmissao);

    long countByStatus(Funcionario.StatusFuncionario status);

    long countAll();
}
