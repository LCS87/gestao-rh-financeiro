package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.Admissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdmissaoRepository extends JpaRepository<Admissao, UUID> {
    Optional<Admissao> findByFuncionarioId(UUID funcionarioId);
    boolean existsByFuncionarioId(UUID funcionarioId);
}
