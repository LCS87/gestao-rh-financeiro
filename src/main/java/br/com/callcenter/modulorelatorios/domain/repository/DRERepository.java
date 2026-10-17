package br.com.callcenter.modulorelatorios.domain.repository;

import br.com.callcenter.modulorelatorios.domain.entity.DRE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DRERepository extends JpaRepository<DRE, UUID> {

    Optional<DRE> findByPeriodo(String periodo);

    List<DRE> findByPeriodoBetweenOrderByPeriodoDesc(String inicio, String fim);
}
