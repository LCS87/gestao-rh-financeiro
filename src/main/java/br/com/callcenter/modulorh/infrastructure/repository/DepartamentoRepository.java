package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartamentoRepository extends JpaRepository<Departamento, UUID> {
    java.util.Optional<Departamento> findByNome(String nome);
}

