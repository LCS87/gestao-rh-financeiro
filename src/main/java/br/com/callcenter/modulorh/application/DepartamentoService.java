package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.DepartamentoDto;
import br.com.callcenter.modulorh.domain.entity.Departamento;
import br.com.callcenter.modulorh.infrastructure.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    public List<Departamento> listarTodos() {
        return departamentoRepository.findAll();
    }

    public Departamento buscarPorId(UUID id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));
    }

    public Departamento criar(DepartamentoDto dto) {
        Departamento departamento = Departamento.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .build();
        return departamentoRepository.save(departamento);
    }

    public Departamento atualizar(UUID id, DepartamentoDto dto) {
        Departamento existente = buscarPorId(id);
        existente.setNome(dto.getNome());
        existente.setDescricao(dto.getDescricao());
        return departamentoRepository.save(existente);
    }

    public void deletar(UUID id) {
        Departamento existente = buscarPorId(id);
        departamentoRepository.delete(existente);
    }
}

