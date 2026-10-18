package br.com.callcenter.modulofinanceiro.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulofinanceiro.domain.entity.FolhaPagamento;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.FolhaPagamentoRepository;
import br.com.callcenter.modulorh.domain.entity.Cargo;
import br.com.callcenter.modulorh.domain.entity.Departamento;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.CargoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.DepartamentoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
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
    private final CargoRepository cargoRepository;
    private final DepartamentoRepository departamentoRepository;

    public ContrachequeService(FolhaPagamentoRepository folhaPagamentoRepository,
                               FuncionarioRepository funcionarioRepository,
                               CargoRepository cargoRepository,
                               DepartamentoRepository departamentoRepository) {
        this.folhaPagamentoRepository = folhaPagamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.cargoRepository = cargoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    public byte[] gerarContracheque(UUID folhaId) throws Exception {
        FolhaPagamento folha = folhaPagamentoRepository.findById(folhaId)
                .orElseThrow(() -> new ResourceNotFoundException("Folha de pagamento não encontrada"));

        Funcionario funcionario = funcionarioRepository.findById(folha.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FUNCIONARIO_NOME", funcionario.getNome());
        parameters.put("FUNCIONARIO_CPF", funcionario.getCpf());
        parameters.put("FUNCIONARIO_CARGO", resolverCargo(funcionario));
        parameters.put("FUNCIONARIO_DEPARTAMENTO", resolverDepartamento(funcionario));
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

    private String resolverCargo(Funcionario funcionario) {
        if (funcionario.getCargoId() == null) return "";
        try {
            return cargoRepository.findById(funcionario.getCargoId())
                    .map(Cargo::getNome)
                    .orElse("");
        } catch (Exception e) {
            log.warn("Não foi possível resolver o cargo do funcionário {}", funcionario.getId(), e);
            return "";
        }
    }

    private String resolverDepartamento(Funcionario funcionario) {
        if (funcionario.getDepartamentoId() == null) return "";
        try {
            return departamentoRepository.findById(funcionario.getDepartamentoId())
                    .map(Departamento::getNome)
                    .orElse("");
        } catch (Exception e) {
            log.warn("Não foi possível resolver o departamento do funcionário {}", funcionario.getId(), e);
            return "";
        }
    }

    private byte[] gerarPdfSimplificado(Map<String, Object> parameters) throws Exception {
        // Fallback: gera um PDF real em memória construindo o relatório dinamicamente
        // via JasperReports (sem depender de arquivo .jasper/.jrxml no classpath).
        JasperDesign design = new JasperDesign();
        design.setName("ContrachequeFallback");
        design.setPageWidth(595);   // A4 em pontos
        design.setPageHeight(842);
        design.setColumnWidth(555);
        design.setLeftMargin(20);
        design.setRightMargin(20);
        design.setTopMargin(20);
        design.setBottomMargin(20);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(30);
        titleBand.addElement(staticText(0, 5, 555, 20, "CONTRACHEQUE - DEMONSTRATIVO DE PAGAMENTO", true));
        design.setTitle(titleBand);

        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(16);
        int y = 0;
        String[][] secoes = {
                {"IDENTIFICACAO", "FUNCIONARIO_NOME", "FUNCIONARIO_CPF", "FUNCIONARIO_CARGO",
                        "FUNCIONARIO_DEPARTAMENTO", "MES_ANO", "DATA_EMISSAO"},
                {"PROVENTOS", "SALARIO_BASE", "HORAS_EXTRAS", "ADICIONAL_NOTURNO", "COMISSOES",
                        "BONUS", "FERIAS", "DECIMO_TERCEIRO", "OUTROS_PROVENTOS", "TOTAL_PROVENTOS"},
                {"DESCONTOS", "INSS", "IRRF", "FGTS", "VALE_TRANSPORTE", "VALE_REFEICAO",
                        "PLANO_SAUDE", "OUTROS_DESCONTOS", "TOTAL_DESCONTOS"},
                {"LIQUIDO", "SALARIO_LIQUIDO"}
        };
        for (String[] secao : secoes) {
            detailBand.addElement(staticText(0, y, 555, 14, secao[0], true));
            y += 14;
            for (int i = 1; i < secao.length; i++) {
                Object v = parameters.get(secao[i]);
                if (v != null && !v.toString().isEmpty()) {
                    detailBand.addElement(staticText(10, y, 545, 14,
                            rotulo(secao[i]) + ": " + v, false));
                    y += 14;
                }
            }
            y += 6;
        }
        detailBand.setSplitType(SplitTypeEnum.STRETCH);

        // JasperReports 6.x não expõe setDetailSection público no JasperDesign;
        // usamos reflection para injetar a seção de detalhe (fallback controlado).
        JRDesignSection detailSection = new JRDesignSection(null);
        detailSection.addBand(detailBand);
        java.lang.reflect.Field detailField =
                net.sf.jasperreports.engine.base.JRBaseReport.class.getDeclaredField("detailSection");
        detailField.setAccessible(true);
        detailField.set(design, detailSection);

        // Os valores já estão embutidos como staticText no band, então o preenchimento
        // usa fonte vazia e produz um PDF real do demonstrativo.
        JasperReport jasperReport = JasperCompileManager.compileReport(design);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JREmptyDataSource(1));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private JRDesignStaticText staticText(int x, int y, int width, int height, String text, boolean bold) {
        JRDesignStaticText staticText = new JRDesignStaticText();
        staticText.setX(x);
        staticText.setY(y);
        staticText.setWidth(width);
        staticText.setHeight(height);
        staticText.setText(text);
        staticText.setFontSize(bold ? 11f : 9f);
        if (bold) {
            staticText.setBold(Boolean.TRUE);
        }
        return staticText;
    }

    private String rotulo(String chave) {
        switch (chave) {
            case "FUNCIONARIO_NOME": return "Funcionario";
            case "FUNCIONARIO_CPF": return "CPF";
            case "FUNCIONARIO_CARGO": return "Cargo";
            case "FUNCIONARIO_DEPARTAMENTO": return "Departamento";
            case "MES_ANO": return "Mes/Ano";
            case "DATA_EMISSAO": return "Emissao";
            case "SALARIO_BASE": return "Salario Base";
            case "HORAS_EXTRAS": return "Horas Extras";
            case "ADICIONAL_NOTURNO": return "Adicional Noturno";
            case "COMISSOES": return "Comissoes";
            case "BONUS": return "Bonus";
            case "FERIAS": return "Ferias";
            case "DECIMO_TERCEIRO": return "13o Salario";
            case "OUTROS_PROVENTOS": return "Outros Proventos";
            case "TOTAL_PROVENTOS": return "Total Proventos";
            case "INSS": return "INSS";
            case "IRRF": return "IRRF";
            case "FGTS": return "FGTS";
            case "VALE_TRANSPORTE": return "Vale Transporte";
            case "VALE_REFEICAO": return "Vale Refeicao";
            case "PLANO_SAUDE": return "Plano de Saude";
            case "OUTROS_DESCONTOS": return "Outros Descontos";
            case "TOTAL_DESCONTOS": return "Total Descontos";
            case "SALARIO_LIQUIDO": return "SALARIO LIQUIDO";
            default: return chave;
        }
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
