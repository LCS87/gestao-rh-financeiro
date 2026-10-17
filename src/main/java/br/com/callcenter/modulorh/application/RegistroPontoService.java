package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.BusinessException;
import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.RegistroPontoDto;
import br.com.callcenter.modulorh.domain.entity.RegistroPonto;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import br.com.callcenter.modulorh.infrastructure.repository.RegistroPontoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RegistroPontoService {

    private final RegistroPontoRepository registroPontoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public RegistroPontoService(RegistroPontoRepository registroPontoRepository,
                                FuncionarioRepository funcionarioRepository) {
        this.registroPontoRepository = registroPontoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<RegistroPonto> listarPorFuncionarioEPeriodo(UUID funcionarioId, LocalDate inicio, LocalDate fim) {
        funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));
        return registroPontoRepository.findByFuncionarioIdAndDataBetweenOrderByData(funcionarioId, inicio, fim);
    }

    public RegistroPonto registrarEntrada(UUID funcionarioId, LocalDate data) {
        funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        if (registroPontoRepository.findByFuncionarioIdAndData(funcionarioId, data).isPresent()) {
            throw new BusinessException("Já existe registro de ponto para esta data. Use atualizar.");
        }
        RegistroPonto rp = RegistroPonto.builder()
                .funcionarioId(funcionarioId)
                .data(data)
                .horaEntrada(java.time.LocalTime.now())
                .build();
        return registroPontoRepository.save(rp);
    }

    public RegistroPonto registrarSaida(UUID funcionarioId, LocalDate data) {
        RegistroPonto rp = registroPontoRepository.findByFuncionarioIdAndData(funcionarioId, data)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de ponto não encontrado. Registre a entrada primeiro."));
        rp.setHoraSaida(java.time.LocalTime.now());
        return registroPontoRepository.save(rp);
    }

    public RegistroPonto registrarIntervalo(UUID funcionarioId, LocalDate data, boolean inicioIntervalo) {
        RegistroPonto rp = registroPontoRepository.findByFuncionarioIdAndData(funcionarioId, data)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de ponto não encontrado."));
        java.time.LocalTime now = java.time.LocalTime.now();
        if (inicioIntervalo) {
            rp.setHoraIntervaloInicio(now);
        } else {
            rp.setHoraIntervaloFim(now);
        }
        return registroPontoRepository.save(rp);
    }

    public RegistroPonto salvarCompleto(RegistroPontoDto dto) {
        funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        RegistroPonto rp = registroPontoRepository.findByFuncionarioIdAndData(dto.getFuncionarioId(), dto.getData())
                .orElse(RegistroPonto.builder()
                        .funcionarioId(dto.getFuncionarioId())
                        .data(dto.getData())
                        .build());

        rp.setHoraEntrada(dto.getHoraEntrada());
        rp.setHoraIntervaloInicio(dto.getHoraIntervaloInicio());
        rp.setHoraIntervaloFim(dto.getHoraIntervaloFim());
        rp.setHoraSaida(dto.getHoraSaida());
        rp.setObservacoes(dto.getObservacoes());

        return registroPontoRepository.save(rp);
    }

    public RegistroPonto buscarPorFuncionarioEData(UUID funcionarioId, LocalDate data) {
        return registroPontoRepository.findByFuncionarioIdAndData(funcionarioId, data)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de ponto não encontrado"));
    }
}
