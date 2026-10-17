package br.com.callcenter.core.repository;

import br.com.callcenter.core.domain.entity.ParametroSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, String> {
}
