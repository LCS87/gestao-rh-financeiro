package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.application.ParametroSistemaService;
import br.com.callcenter.modulorh.api.dto.HistoricoVendasDto;
import br.com.callcenter.modulorh.api.dto.RankingVendedorDto;
import br.com.callcenter.modulorh.api.dto.ResumoVendasDto;
import br.com.callcenter.modulorh.api.dto.VendaDto;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.domain.entity.Venda;
import br.com.callcenter.modulorh.infrastructure.repository.DepartamentoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import br.com.callcenter.modulorh.infrastructure.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ParametroSistemaService parametroService;

    @Transactional
    public VendaDto registrarVenda(UUID funcionarioId, BigDecimal valor) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado"));

        Venda venda = Venda.builder()
                .vendedor(funcionario)
                .valor(valor)
                .dataVenda(LocalDateTime.now())
                .build();

        Venda salva = vendaRepository.save(venda);

        return mapToDto(salva);
    }

    @Transactional(readOnly = true)
    public List<VendaDto> listarVendas() {
        return vendaRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResumoVendasDto getResumoVendas() {
        LocalDateTime hoje = LocalDate.now().atStartOfDay();
        LocalDateTime inicioSemana = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimDoDia = hoje.plusDays(1).minusNanos(1);

        Double diario = vendaRepository.sumValorByPeriodo(hoje, fimDoDia);
        Double semanal = vendaRepository.sumValorByPeriodo(inicioSemana, fimDoDia);
        Double mensal = vendaRepository.sumValorByPeriodo(inicioMes, fimDoDia);

        return ResumoVendasDto.builder()
                .totalDiario(BigDecimal.valueOf(diario))
                .totalSemanal(BigDecimal.valueOf(semanal))
                .totalMensal(BigDecimal.valueOf(mensal))
                .build();
    }

    @Transactional(readOnly = true)
    public List<RankingVendedorDto> getRankingDiario() {
        LocalDateTime inicioHoje = LocalDate.now().atStartOfDay();
        LocalDateTime fimHoje = inicioHoje.plusDays(1).minusNanos(1);
        
        LocalDateTime inicioSemana = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // Filtra apenas funcionários do departamento "Comercial"
        List<Funcionario> vendedores = departamentoRepository.findByNome("Comercial")
                .map(dept -> funcionarioRepository.findByDepartamentoId(dept.getId()))
                .orElse(new ArrayList<>());

        List<RankingVendedorDto> rankingList = new ArrayList<>();
        
        // Pega do Parâmetro ouFallback 1400.00
        BigDecimal metaDiaria = new BigDecimal(
            parametroService.getValorParametro("META_VENDAS_DIARIA", "1400.00")
        );

        for (Funcionario f : vendedores) {
            Double diarioNum = vendaRepository.sumValorByVendedorIdAndPeriodo(f.getId(), inicioHoje, fimHoje);
            Double semanalNum = vendaRepository.sumValorByVendedorIdAndPeriodo(f.getId(), inicioSemana, fimHoje);
            Double mensalNum = vendaRepository.sumValorByVendedorIdAndPeriodo(f.getId(), inicioMes, fimHoje);
            
            BigDecimal diario = BigDecimal.valueOf(diarioNum);
            
            // Vamos adicionar ao ranking, mesmo 0. Depois o front-end ordena.
            rankingList.add(RankingVendedorDto.builder()
                    .vendedorId(f.getId())
                    .vendedorNome(f.getNome())
                    .vendasDiarias(diario)
                    .vendasSemanais(BigDecimal.valueOf(semanalNum))
                    .vendasMensais(BigDecimal.valueOf(mensalNum))
                    .metaDiaria(metaDiaria)
                    .build());
        }

        // Ordenar decrescente por vendas diárias
        rankingList.sort((a, b) -> b.getVendasDiarias().compareTo(a.getVendasDiarias()));

        return rankingList;
    }

    @Transactional(readOnly = true)
    public List<HistoricoVendasDto> getHistoricoGlobal(int dias) {
        LocalDateTime inicio = LocalDate.now().minusDays(dias).atStartOfDay();
        List<Object[]> results = vendaRepository.findHistoricoGlobal(inicio);
        
        // Pega do Parâmetro ouFallback 1400 * numVendedores (mock para a global goal)
        // O Dashboard assume 4500 daily goal approx no mock, let's use globalGoal / 30
        BigDecimal globalGoal = new BigDecimal(parametroService.getValorParametro("globalGoal", "150000.00"));
        BigDecimal metaDiariaGlobal = globalGoal.divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP);
        
        return processarHistorico(results, dias, metaDiariaGlobal);
    }

    @Transactional(readOnly = true)
    public List<HistoricoVendasDto> getHistoricoPessoal(UUID vendedorId, int dias) {
        LocalDateTime inicio = LocalDate.now().minusDays(dias).atStartOfDay();
        List<Object[]> results = vendaRepository.findHistoricoByVendedor(vendedorId, inicio);
        
        // Meta diária individual
        BigDecimal metaDiaria = new BigDecimal(parametroService.getValorParametro("META_VENDAS_DIARIA", "1400.00"));
        
        return processarHistorico(results, dias, metaDiaria);
    }

    private List<HistoricoVendasDto> processarHistorico(List<Object[]> results, int dias, BigDecimal metaDiaria) {
        Map<String, BigDecimal> vendasPorDia = new HashMap<>();
        DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Object[] row : results) {
            String dbDateStr = String.valueOf(row[0]);
            try {
                // Tenta extrair apenas yyyy-MM-dd se vier com time do DBeaver
                if (dbDateStr.length() > 10) dbDateStr = dbDateStr.substring(0, 10);
                BigDecimal total = new BigDecimal(String.valueOf(row[1]));
                vendasPorDia.put(dbDateStr, total);
            } catch (Exception e) {}
        }
        
        // Preencher buracos para os últimos 'dias' dias (ordem cronológica)
        List<HistoricoVendasDto> historico = new ArrayList<>();
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM");
        
        for (int i = dias - 1; i >= 0; i--) {
            LocalDate dia = LocalDate.now().minusDays(i);
            String key = dia.format(sqlFormatter);
            String label = dia.format(displayFormatter);
            
            BigDecimal vendas = vendasPorDia.getOrDefault(key, BigDecimal.ZERO);
            historico.add(new HistoricoVendasDto(label, vendas, metaDiaria));
        }
        return historico;
    }

    private VendaDto mapToDto(Venda venda) {
        return VendaDto.builder()
                .id(venda.getId())
                .vendedorId(venda.getVendedor().getId())
                .vendedorNome(venda.getVendedor().getNome())
                .valor(venda.getValor())
                .dataVenda(venda.getDataVenda())
                .build();
    }
}
