package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.Escala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EscalaRepository extends JpaRepository<Escala, UUID> {
}
