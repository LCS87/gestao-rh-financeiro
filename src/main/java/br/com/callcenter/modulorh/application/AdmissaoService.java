package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.BusinessException;
import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.AdmissaoDto;
import br.com.callcenter.modulorh.domain.entity.Admissao;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.AdmissaoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AdmissaoService {

    private final AdmissaoRepository admissaoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AdmissaoService(AdmissaoRepository admissaoRepository, FuncionarioRepository funcionarioRepository) {
        this.admissaoRepository = admissaoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Admissao> listarTodas() {
        return admissaoRepository.findAll();
    }

    public Admissao buscarPorId(UUID id) {
        return admissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admissão não encontrada"));
    }

    public Admissao buscarPorFuncionarioId(UUID funcionarioId) {
        return admissaoRepository.findByFuncionarioId(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Admissão não encontrada para o funcionário"));
    }

    public Admissao criar(AdmissaoDto dto) {
        // Verificar se funcionário existe
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        // Verificar se já existe admissão para este funcionário
        if (admissaoRepository.existsByFuncionarioId(dto.getFuncionarioId())) {
            throw new BusinessException("Já existe admissão cadastrada para este funcionário");
        }

        Admissao admissao = mapearParaEntidade(new Admissao(), dto);
        admissao.setStatus(Admissao.StatusAdmissao.PENDENTE);
        
        // Atualizar data de admissão do funcionário
        funcionario.setDataAdmissao(dto.getDataAdmissao());
        funcionario.setStatus(Funcionario.StatusFuncionario.ATIVO);
        funcionarioRepository.save(funcionario);

        return admissaoRepository.save(admissao);
    }

    public Admissao atualizar(UUID id, AdmissaoDto dto) {
        Admissao existente = buscarPorId(id);
        
        // Verificar se funcionário existe
        funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        existente = mapearParaEntidade(existente, dto);
        
        // Se todos os documentos foram entregues e contrato assinado, marcar como concluída
        if (existente.isCompleta() && existente.getStatus() != Admissao.StatusAdmissao.CONCLUIDA) {
            existente.setStatus(Admissao.StatusAdmissao.CONCLUIDA);
        } else if (!existente.isCompleta() && existente.getStatus() == Admissao.StatusAdmissao.PENDENTE) {
            existente.setStatus(Admissao.StatusAdmissao.EM_ANDAMENTO);
        }

        return admissaoRepository.save(existente);
    }

    public Admissao concluirAdmissao(UUID id) {
        Admissao admissao = buscarPorId(id);
        
        if (!admissao.isCompleta()) {
            throw new BusinessException("Não é possível concluir admissão: documentos pendentes");
        }

        admissao.setStatus(Admissao.StatusAdmissao.CONCLUIDA);
        return admissaoRepository.save(admissao);
    }

    public void deletar(UUID id) {
        Admissao existente = buscarPorId(id);
        admissaoRepository.delete(existente);
    }

    private Admissao mapearParaEntidade(Admissao admissao, AdmissaoDto dto) {
        admissao.setFuncionarioId(dto.getFuncionarioId());
        admissao.setDataAdmissao(dto.getDataAdmissao());
        if (dto.getStatus() != null) {
            admissao.setStatus(dto.getStatus());
        }
        admissao.setRgEntregue(dto.getRgEntregue());
        admissao.setCpfEntregue(dto.getCpfEntregue());
        admissao.setCtpsEntregue(dto.getCtpsEntregue());
        admissao.setComprovanteResidenciaEntregue(dto.getComprovanteResidenciaEntregue());
        admissao.setTituloEleitorEntregue(dto.getTituloEleitorEntregue());
        admissao.setCertificadoReservista(dto.getCertificadoReservista());
        admissao.setExameMedicoRealizado(dto.getExameMedicoRealizado());
        admissao.setExameMedicoValidade(dto.getExameMedicoValidade());
        admissao.setContratoAssinado(dto.getContratoAssinado());
        admissao.setDataAssinaturaContrato(dto.getDataAssinaturaContrato());
        admissao.setObservacoes(dto.getObservacoes());
        admissao.setResponsavelAdmissaoId(dto.getResponsavelAdmissaoId());
        return admissao;
    }
}
