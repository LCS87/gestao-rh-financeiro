package br.com.callcenter.modulorh.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.callcenter.modulorh.domain.entity.Beneficio;
import br.com.callcenter.modulorh.domain.enums.TipoBeneficio;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, UUID> {

    List<Beneficio> findByAtivoTrue();

    List<Beneficio> findByTipoAndAtivoTrue(TipoBeneficio tipo);

    List<Beneficio> findByObrigatorioTrueAndAtivoTrue();

    Optional<Beneficio> findByNomeAndAtivoTrue(String nome);

    @Query("SELECT b FROM Beneficio b WHERE b.ativo = true AND b.obrigatorio = false")
    List<Beneficio> findBeneficiosOpcionaisAtivos();

    @Query("SELECT COUNT(b) FROM Beneficio b WHERE b.ativo = true")
    Long countBeneficiosAtivos();

    @Query("SELECT b.tipo, COUNT(b) FROM Beneficio b WHERE b.ativo = true GROUP BY b.tipo")
    List<Object[]> countBeneficiosAtivosPorTipo();
}