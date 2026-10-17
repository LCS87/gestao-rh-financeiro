package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.FeriasDto;
import br.com.callcenter.modulorh.domain.entity.Ferias;
import br.com.callcenter.modulorh.infrastructure.repository.FeriasRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FeriasService {

    private final FeriasRepository feriasRepository;
    private final FuncionarioRepository funcionarioRepository;

    public FeriasService(FeriasRepository feriasRepository, FuncionarioRepository funcionarioRepository) {
        this.feriasRepository = feriasRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Ferias> listarTodas() {
        return feriasRepository.findAll();
    }

    public List<Ferias> listarPorFuncionario(UUID funcionarioId) {
        funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));
        return feriasRepository.findByFuncionarioIdOrderByDataInicioDesc(funcionarioId);
    }

    public Ferias buscarPorId(UUID id) {
        return feriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Férias não encontradas"));
    }

    public Ferias criar(FeriasDto dto) {
        funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        Ferias ferias = Ferias.builder()
                .funcionarioId(dto.getFuncionarioId())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .dias(dto.getDias())
                .tipo(dto.getTipo() != null ? dto.getTipo() : Ferias.TipoFerias.INTEGRAL)
                .status(dto.getStatus() != null ? dto.getStatus() : Ferias.StatusFerias.AGENDADA)
                .observacoes(dto.getObservacoes())
                .build();
        return feriasRepository.save(ferias);
    }

    public Ferias atualizar(UUID id, FeriasDto dto) {
        Ferias existente = buscarPorId(id);
        existente.setDataInicio(dto.getDataInicio());
        existente.setDataFim(dto.getDataFim());
        existente.setDias(dto.getDias());
        existente.setTipo(dto.getTipo() != null ? dto.getTipo() : existente.getTipo());
        existente.setStatus(dto.getStatus() != null ? dto.getStatus() : existente.getStatus());
        existente.setObservacoes(dto.getObservacoes());
        return feriasRepository.save(existente);
    }

    public void deletar(UUID id) {
        Ferias existente = buscarPorId(id);
        feriasRepository.delete(existente);
    }
}
