package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.AfastamentoDto;
import br.com.callcenter.modulorh.domain.entity.Afastamento;
import br.com.callcenter.modulorh.infrastructure.repository.AfastamentoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AfastamentoService {

    private final AfastamentoRepository afastamentoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AfastamentoService(AfastamentoRepository afastamentoRepository,
                              FuncionarioRepository funcionarioRepository) {
        this.afastamentoRepository = afastamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Afastamento> listarTodos() {
        return afastamentoRepository.findAll();
    }

    public List<Afastamento> listarPorFuncionario(UUID funcionarioId) {
        funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));
        return afastamentoRepository.findByFuncionarioIdOrderByDataInicioDesc(funcionarioId);
    }

    public Afastamento buscarPorId(UUID id) {
        return afastamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Afastamento não encontrado"));
    }

    public Afastamento criar(AfastamentoDto dto) {
        funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        Afastamento afastamento = Afastamento.builder()
                .funcionarioId(dto.getFuncionarioId())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .tipo(dto.getTipo())
                .motivo(dto.getMotivo())
                .observacoes(dto.getObservacoes())
                .build();
        return afastamentoRepository.save(afastamento);
    }

    public Afastamento atualizar(UUID id, AfastamentoDto dto) {
        Afastamento existente = buscarPorId(id);
        existente.setDataInicio(dto.getDataInicio());
        existente.setDataFim(dto.getDataFim());
        existente.setTipo(dto.getTipo());
        existente.setMotivo(dto.getMotivo());
        existente.setObservacoes(dto.getObservacoes());
        return afastamentoRepository.save(existente);
    }

    public void deletar(UUID id) {
        Afastamento existente = buscarPorId(id);
        afastamentoRepository.delete(existente);
    }
}
