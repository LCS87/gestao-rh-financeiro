package br.com.callcenter.modulofinanceiro.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulofinanceiro.domain.entity.FolhaPagamento;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.FolhaPagamentoRepository;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.*;

@Service
@Slf4j
public class ContrachequeService {

    private final FolhaPagamentoRepository folhaPagamentoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public ContrachequeService(FolhaPagamentoRepository folhaPagamentoRepository,
                               FuncionarioRepository funcionarioRepository) {
        this.folhaPagamentoRepository = folhaPagamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public byte[] gerarContracheque(UUID folhaId) throws Exception {
        FolhaPagamento folha = folhaPagamentoRepository.findById(folhaId)
                .orElseThrow(() -> new ResourceNotFoundException("Folha de pagamento não encontrada"));

        Funcionario funcionario = funcionarioRepository.findById(folha.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FUNCIONARIO_NOME", funcionario.getNome());
        parameters.put("FUNCIONARIO_CPF", funcionario.getCpf());
        parameters.put("FUNCIONARIO_CARGO", "");
        parameters.put("FUNCIONARIO_DEPARTAMENTO", "");
        parameters.put("MES_ANO", formatarMesAno(folha.getAnoMes()));
        parameters.put("DATA_EMISSAO", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parameters.put("SALARIO_BASE", formatarMoeda(folha.getSalarioBase()));

        // Proventos
        parameters.put("HORAS_EXTRAS", formatarMoeda(folha.getHorasExtras()));
        parameters.put("ADICIONAL_NOTURNO", formatarMoeda(folha.getAdicionalNoturno()));
        parameters.put("COMISSOES", formatarMoeda(folha.getComissoes()));
        parameters.put("BONUS", formatarMoeda(folha.getBonus()));
        parameters.put("FERIAS", formatarMoeda(folha.getFerias()));
        parameters.put("DECIMO_TERCEIRO", formatarMoeda(folha.getDecimoTerceiro()));
        parameters.put("OUTROS_PROVENTOS", formatarMoeda(folha.getOutrosProventos()));
        parameters.put("TOTAL_PROVENTOS", formatarMoeda(folha.getTotalProventos()));

        // Descontos
        parameters.put("INSS", formatarMoeda(folha.getInss()));
        parameters.put("IRRF", formatarMoeda(folha.getIrrf()));
        parameters.put("FGTS", formatarMoeda(folha.getFgts()));
        parameters.put("VALE_TRANSPORTE", formatarMoeda(folha.getValeTransporte()));
        parameters.put("VALE_REFEICAO", formatarMoeda(folha.getValeRefeicao()));
        parameters.put("PLANO_SAUDE", formatarMoeda(folha.getPlanoSaude()));
        parameters.put("OUTROS_DESCONTOS", formatarMoeda(folha.getOutrosDescontos()));
        parameters.put("TOTAL_DESCONTOS", formatarMoeda(folha.getTotalDescontos()));

        // Total
        parameters.put("SALARIO_LIQUIDO", formatarMoeda(folha.getSalarioLiquido()));

        // Dados bancários
        if (folha.getBancoPagamento() != null) {
            parameters.put("BANCO_PAGAMENTO", folha.getBancoPagamento());
            parameters.put("AGENCIA_PAGAMENTO", folha.getAgenciaPagamento());
            parameters.put("CONTA_PAGAMENTO", folha.getContaPagamento());
        }

        // Gera o PDF usando JasperReports
        return gerarPdfJasper(parameters);
    }

    private byte[] gerarPdfJasper(Map<String, Object> parameters) throws Exception {
        try {
            // Tenta carregar o arquivo .jasper compilado
            InputStream jasperStream = new ClassPathResource("reports/contracheque.jasper").getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);
            
            // Se não encontrar o .jasper, tenta compilar do .jrxml
        } catch (IOException e) {
            log.info("Arquivo .jasper não encontrado, tentando compilar do .jrxml");
            try {
                InputStream jrxmlStream = new ClassPathResource("reports/contracheque.jrxml").getInputStream();
                JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
                return JasperExportManager.exportReportToPdf(jasperPrint);
            } catch (IOException ex) {
                log.warn("Arquivo .jrxml não encontrado, gerando PDF simplificado");
                return gerarPdfSimplificado(parameters);
            }
        }
    }

    private byte[] gerarPdfSimplificado(Map<String, Object> parameters) {
        // Gera um PDF básico em memória como fallback
        String htmlContent = gerarHtmlContracheque(parameters);
        
        // Aqui você poderia usar uma biblioteca como iText ou Flying Saucer
        // Por enquanto, retorna um placeholder
        String content = "CONTRACHEQUE\n\n" +
                "Funcionário: " + parameters.get("FUNCIONARIO_NOME") + "\n" +
                "CPF: " + parameters.get("FUNCIONARIO_CPF") + "\n" +
                "Mês/Ano: " + parameters.get("MES_ANO") + "\n" +
                "Salário Base: " + parameters.get("SALARIO_BASE") + "\n" +
                "Total Proventos: " + parameters.get("TOTAL_PROVENTOS") + "\n" +
                "Total Descontos: " + parameters.get("TOTAL_DESCONTOS") + "\n" +
                "Salário Líquido: " + parameters.get("SALARIO_LIQUIDO") + "\n";
        
        return content.getBytes();
    }

    private String gerarHtmlContracheque(Map<String, Object> parameters) {
        return "<html><head><title>Contracheque</title></head><body>" +
                "<h1>Contracheque</h1>" +
                "<p><strong>Funcionário:</strong> " + parameters.get("FUNCIONARIO_NOME") + "</p>" +
                "<p><strong>CPF:</strong> " + parameters.get("FUNCIONARIO_CPF") + "</p>" +
                "<p><strong>Mês/Ano:</strong> " + parameters.get("MES_ANO") + "</p>" +
                "<h2>Proventos</h2>" +
                "<p>Salário Base: " + parameters.get("SALARIO_BASE") + "</p>" +
                "<p>Total Proventos: " + parameters.get("TOTAL_PROVENTOS") + "</p>" +
                "<h2>Descontos</h2>" +
                "<p>INSS: " + parameters.get("INSS") + "</p>" +
                "<p>IRRF: " + parameters.get("IRRF") + "</p>" +
                "<p>Total Descontos: " + parameters.get("TOTAL_DESCONTOS") + "</p>" +
                "<h2>Total</h2>" +
                "<p><strong>Salário Líquido: " + parameters.get("SALARIO_LIQUIDO") + "</strong></p>" +
                "</body></html>";
    }

    private String formatarMesAno(String anoMes) {
        if (anoMes == null || anoMes.length() != 7) return "";
        
        YearMonth yearMonth = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                         "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        
        return meses[yearMonth.getMonthValue() - 1] + "/" + yearMonth.getYear();
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return String.format("R$ %,.2f", valor);
    }

    public List<Map<String, Object>> listarContrachequesPorMes(String anoMes) {
        List<FolhaPagamento> folhas = folhaPagamentoRepository.findByAnoMes(anoMes);
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        for (FolhaPagamento folha : folhas) {
            Funcionario funcionario = funcionarioRepository.findById(folha.getFuncionarioId())
                    .orElse(null);
            
            if (funcionario != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", folha.getId());
                item.put("funcionarioNome", funcionario.getNome());
                item.put("funcionarioCpf", funcionario.getCpf());
                item.put("mesAno", formatarMesAno(folha.getAnoMes()));
                item.put("salarioLiquido", folha.getSalarioLiquido());
                item.put("status", folha.getStatus());
                item.put("dataPagamento", folha.getDataPagamento());
                resultado.add(item);
            }
        }
        
        return resultado;
    }
}
