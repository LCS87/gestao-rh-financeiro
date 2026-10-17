package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.RegistroPonto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroPontoRepository extends JpaRepository<RegistroPonto, UUID> {

    Optional<RegistroPonto> findByFuncionarioIdAndData(UUID funcionarioId, LocalDate data);

    List<RegistroPonto> findByFuncionarioIdAndDataBetweenOrderByData(UUID funcionarioId, LocalDate inicio, LocalDate fim);

    List<RegistroPonto> findByDataBetweenOrderByData(LocalDate inicio, LocalDate fim);
}
