package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.BusinessException;
import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.FuncionarioDto;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    @Cacheable(value = "funcionarios", key = "#id")
    public Funcionario buscarPorId(UUID id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));
    }

    @CacheEvict(value = "funcionarios", allEntries = true)
    public Funcionario criar(FuncionarioDto dto) {
        funcionarioRepository.findByCpf(dto.getCpf()).ifPresent(f -> {
            throw new BusinessException("Já existe funcionário cadastrado com este CPF");
        });

        Funcionario funcionario = mapearParaEntidade(new Funcionario(), dto);
        return funcionarioRepository.save(funcionario);
    }

    @CacheEvict(value = "funcionarios", allEntries = true)
    public Funcionario atualizar(UUID id, FuncionarioDto dto) {
        Funcionario existente = buscarPorId(id);

        if (!existente.getCpf().equals(dto.getCpf())) {
            funcionarioRepository.findByCpf(dto.getCpf()).ifPresent(f -> {
                throw new BusinessException("Já existe funcionário cadastrado com este CPF");
            });
        }

        existente = mapearParaEntidade(existente, dto);
        return funcionarioRepository.save(existente);
    }

    @CacheEvict(value = "funcionarios", allEntries = true)
    public void deletar(UUID id) {
        Funcionario existente = buscarPorId(id);
        funcionarioRepository.delete(existente);
    }

    private Funcionario mapearParaEntidade(Funcionario funcionario, FuncionarioDto dto) {
        funcionario.setNome(dto.getNome());
        // Remove caracteres não numéricos do CPF para caber no VARCHAR(11) do banco
        if (dto.getCpf() != null) {
            funcionario.setCpf(dto.getCpf().replaceAll("\\D", ""));
        }
        funcionario.setEmail(dto.getEmail());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setDataNascimento(dto.getDataNascimento());
        funcionario.setDataAdmissao(dto.getDataAdmissao());
        funcionario.setDataDemissao(dto.getDataDemissao());
        funcionario.setStatus(dto.getStatus() != null ? dto.getStatus() : Funcionario.StatusFuncionario.ATIVO);
        funcionario.setDepartamentoId(dto.getDepartamentoId());
        funcionario.setCargoId(dto.getCargoId());
        funcionario.setEquipeId(dto.getEquipeId());
        funcionario.setEscalaId(dto.getEscalaId());
        funcionario.setSalarioBase(dto.getSalarioBase());
        return funcionario;
    }
}

