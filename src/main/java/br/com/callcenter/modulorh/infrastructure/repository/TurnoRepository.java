package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TurnoRepository extends JpaRepository<Turno, UUID> {

    List<Turno> findByEscalaIdOrderByDiaSemana(UUID escalaId);
}
