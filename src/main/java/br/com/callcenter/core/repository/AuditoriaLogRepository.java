package br.com.callcenter.core.repository;

import br.com.callcenter.core.domain.entity.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, UUID> {
    
    // Lista os últimos logs ordenador pelo mais recente
    List<AuditoriaLog> findTop50ByOrderByCreatedAtDesc();
}
