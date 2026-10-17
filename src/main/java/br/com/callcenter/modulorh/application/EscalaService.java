package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.EscalaDto;
import br.com.callcenter.modulorh.domain.entity.Escala;
import br.com.callcenter.modulorh.infrastructure.repository.EscalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EscalaService {

    private final EscalaRepository escalaRepository;

    public EscalaService(EscalaRepository escalaRepository) {
        this.escalaRepository = escalaRepository;
    }

    public List<Escala> listarTodas() {
        return escalaRepository.findAll();
    }

    public Escala buscarPorId(UUID id) {
        return escalaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada"));
    }

    public Escala criar(EscalaDto dto) {
        Escala escala = Escala.builder()
                .nome(dto.getNome())
                .tipo(dto.getTipo())
                .descricao(dto.getDescricao())
                .horasSemanais(dto.getHorasSemanais() != null ? dto.getHorasSemanais() : 44.0)
                .build();
        return escalaRepository.save(escala);
    }

    public Escala atualizar(UUID id, EscalaDto dto) {
        Escala existente = buscarPorId(id);
        existente.setNome(dto.getNome());
        existente.setTipo(dto.getTipo());
        existente.setDescricao(dto.getDescricao());
        existente.setHorasSemanais(dto.getHorasSemanais() != null ? dto.getHorasSemanais() : 44.0);
        return escalaRepository.save(existente);
    }

    public void deletar(UUID id) {
        Escala existente = buscarPorId(id);
        escalaRepository.delete(existente);
    }
}
