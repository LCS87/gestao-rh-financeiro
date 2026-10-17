package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.EquipeDto;
import br.com.callcenter.modulorh.domain.entity.Equipe;
import br.com.callcenter.modulorh.infrastructure.repository.EquipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EquipeService {

    private final EquipeRepository equipeRepository;

    public EquipeService(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
    }

    public List<Equipe> listarTodos() {
        return equipeRepository.findAll();
    }

    public Equipe buscarPorId(UUID id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe não encontrada"));
    }

    public Equipe criar(EquipeDto dto) {
        Equipe equipe = Equipe.builder()
                .nome(dto.getNome())
                .supervisorId(dto.getSupervisorId())
                .departamentoId(dto.getDepartamentoId())
                .build();
        return equipeRepository.save(equipe);
    }

    public Equipe atualizar(UUID id, EquipeDto dto) {
        Equipe existente = buscarPorId(id);
        existente.setNome(dto.getNome());
        existente.setSupervisorId(dto.getSupervisorId());
        existente.setDepartamentoId(dto.getDepartamentoId());
        return equipeRepository.save(existente);
    }

    public void deletar(UUID id) {
        Equipe existente = buscarPorId(id);
        equipeRepository.delete(existente);
    }
}

