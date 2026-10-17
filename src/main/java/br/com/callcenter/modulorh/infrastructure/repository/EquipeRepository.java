package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EquipeRepository extends JpaRepository<Equipe, UUID> {
}

