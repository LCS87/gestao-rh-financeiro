package br.com.callcenter.core.application;

import br.com.callcenter.core.domain.entity.ParametroSistema;
import br.com.callcenter.core.dto.ParametroSistemaDto;
import br.com.callcenter.core.repository.ParametroSistemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParametroSistemaService {

    private final ParametroSistemaRepository parametroRepository;

    @Transactional(readOnly = true)
    public List<ParametroSistemaDto> listarTodos() {
        return parametroRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "parametros", key = "#chave")
    @Transactional(readOnly = true)
    public String getValorParametro(String chave, String valorPadrao) {
        return parametroRepository.findById(chave)
                .map(ParametroSistema::getValor)
                .orElse(valorPadrao);
    }

    @CacheEvict(value = "parametros", key = "#dto.chave")
    @Transactional
    public ParametroSistemaDto salvarParametro(ParametroSistemaDto dto) {
        ParametroSistema parametro = parametroRepository.findById(dto.getChave())
                .orElseGet(() -> ParametroSistema.builder()
                        .chave(dto.getChave())
                        .tipo(dto.getTipo() != null ? dto.getTipo() : "STRING")
                        .build());

        parametro.setValor(dto.getValor());
        if (dto.getDescricao() != null) {
            parametro.setDescricao(dto.getDescricao());
        }

        ParametroSistema salvo = parametroRepository.save(parametro);
        log.info("Parâmetro atualizado: {} = {}", salvo.getChave(), salvo.getValor());
        
        return mapToDto(salvo);
    }
    
    @CacheEvict(value = "parametros", allEntries = true)
    public void limparCache() {
        log.info("Cache de parâmetros limpo");
    }

    private ParametroSistemaDto mapToDto(ParametroSistema parametro) {
        return ParametroSistemaDto.builder()
                .chave(parametro.getChave())
                .valor(parametro.getValor())
                .descricao(parametro.getDescricao())
                .tipo(parametro.getTipo())
                .build();
    }
}
