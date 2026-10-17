package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.BusinessException;
import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.DemissaoDto;
import br.com.callcenter.modulorh.domain.entity.Demissao;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.DemissaoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DemissaoService {

    private final DemissaoRepository demissaoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final CalculoRescisaoService calculoRescisaoService;

    public DemissaoService(DemissaoRepository demissaoRepository, 
                          FuncionarioRepository funcionarioRepository,
                          CalculoRescisaoService calculoRescisaoService) {
        this.demissaoRepository = demissaoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.calculoRescisaoService = calculoRescisaoService;
    }

    public List<Demissao> listarTodas() {
        return demissaoRepository.findAll();
    }

    public Demissao buscarPorId(UUID id) {
        return demissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demissão não encontrada"));
    }

    public Demissao buscarPorFuncionarioId(UUID funcionarioId) {
        return demissaoRepository.findByFuncionarioId(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Demissão não encontrada para o funcionário"));
    }

    public Demissao criar(DemissaoDto dto) {
        // Verificar se funcionário existe
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        // Verificar se já existe demissão para este funcionário
        if (demissaoRepository.existsByFuncionarioId(dto.getFuncionarioId())) {
            throw new BusinessException("Já existe demissão cadastrada para este funcionário");
        }

        // Verificar se funcionário está ativo
        if (funcionario.getStatus() != Funcionario.StatusFuncionario.ATIVO) {
            throw new BusinessException("Funcionário não está ativo para ser demitido");
        }

        Demissao demissao = mapearParaEntidade(new Demissao(), dto);
        demissao.setStatus(Demissao.StatusDemissao.INICIADA);

        // Atualizar funcionário
        funcionario.setDataDemissao(dto.getDataDemissao());
        funcionario.setStatus(Funcionario.StatusFuncionario.INATIVO);
        funcionarioRepository.save(funcionario);

        return demissaoRepository.save(demissao);
    }

    public Demissao calcularRescisao(UUID id) {
        Demissao demissao = buscarPorId(id);
        Funcionario funcionario = funcionarioRepository.findById(demissao.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        if (demissao.getStatus() == Demissao.StatusDemissao.CALCULADA) {
            throw new BusinessException("Rescisão já foi calculada");
        }

        // Calcular rescisão
        calculoRescisaoService.calcularRescisao(demissao, funcionario);

        return demissaoRepository.save(demissao);
    }

    public Demissao atualizar(UUID id, DemissaoDto dto) {
        Demissao existente = buscarPorId(id);
        
        // Verificar se funcionário existe
        funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        existente = mapearParaEntidade(existente, dto);
        return demissaoRepository.save(existente);
    }

    public Demissao finalizarDemissao(UUID id) {
        Demissao demissao = buscarPorId(id);
        
        if (demissao.getStatus() != Demissao.StatusDemissao.CALCULADA) {
            throw new BusinessException("Não é possível finalizar demissão: rescisão não foi calculada");
        }

        demissao.setStatus(Demissao.StatusDemissao.FINALIZADA);
        return demissaoRepository.save(demissao);
    }

    public void deletar(UUID id) {
        Demissao existente = buscarPorId(id);
        demissaoRepository.delete(existente);
    }

    private Demissao mapearParaEntidade(Demissao demissao, DemissaoDto dto) {
        demissao.setFuncionarioId(dto.getFuncionarioId());
        demissao.setDataDemissao(dto.getDataDemissao());
        demissao.setTipoDemissao(dto.getTipoDemissao());
        if (dto.getStatus() != null) {
            demissao.setStatus(dto.getStatus());
        }
        demissao.setAvisoPrevioTrabalhado(dto.getAvisoPrevioTrabalhado());
        demissao.setAvisoPrevioIndenizado(dto.getAvisoPrevioIndenizado());
        demissao.setExameDemissionalRealizado(dto.getExameDemissionalRealizado());
        demissao.setDataExameDemissional(dto.getDataExameDemissional());
        demissao.setMotivo(dto.getMotivo());
        demissao.setObservacoes(dto.getObservacoes());
        demissao.setResponsavelDemissaoId(dto.getResponsavelDemissaoId());
        return demissao;
    }
}
