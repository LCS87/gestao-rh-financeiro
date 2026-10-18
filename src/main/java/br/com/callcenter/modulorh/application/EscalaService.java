package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.EscalaDto;
import br.com.callcenter.modulorh.domain.entity.Escala;
import br.com.callcenter.modulorh.infrastructure.repository.EscalaRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EscalaService {

    private final EscalaRepository escalaRepository;
    private final FuncionarioRepository funcionarioRepository;

    public EscalaService(EscalaRepository escalaRepository,
                         FuncionarioRepository funcionarioRepository) {
        this.escalaRepository = escalaRepository;
        this.funcionarioRepository = funcionarioRepository;
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

    /**
     * Vincula funcionarios a uma escala em lote. Funcionarios inexistentes
     * sao ignorados; retorna a quantidade efetivamente atualizada.
     */
    public int vincularFuncionariosEmLote(UUID escalaId, List<UUID> funcionarioIds) {
        buscarPorId(escalaId); // valida existencia da escala
        int atualizados = 0;
        for (UUID funcId : funcionarioIds) {
            var opt = funcionarioRepository.findById(funcId);
            if (opt.isPresent()) {
                var f = opt.get();
                f.setEscalaId(escalaId);
                funcionarioRepository.save(f);
                atualizados++;
            }
        }
        return atualizados;
    }
}
