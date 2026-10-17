package br.com.callcenter.modulofinanceiro.application;

import br.com.callcenter.modulofinanceiro.domain.entity.FolhaPagamento;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service para cálculo da folha de pagamento conforme regras da CLT
 */
@Service
@Slf4j
public class CalculoFolhaService {

    private final FuncionarioRepository funcionarioRepository;

    // Alíquotas INSS 2024 (valores aproximados para exemplo)
    private static final BigDecimal[] FAIXAS_INSS = {
        new BigDecimal("1412.00"),
        new BigDecimal("2666.68"),
        new BigDecimal("4000.03"),
        new BigDecimal("7786.02")
    };
    
    private static final BigDecimal[] ALIQUOTAS_INSS = {
        new BigDecimal("0.075"),   // 7,5%
        new BigDecimal("0.09"),    // 9%
        new BigDecimal("0.12"),    // 12%
        new BigDecimal("0.14")     // 14%
    };

    // Alíquotas IRRF (base simplificada)
    private static final BigDecimal[] FAIXAS_IRRF = {
        new BigDecimal("2259.20"),
        new BigDecimal("2826.65"),
        new BigDecimal("3751.05"),
        new BigDecimal("4664.68")
    };
    
    private static final BigDecimal[] ALIQUOTAS_IRRF = {
        BigDecimal.ZERO,
        new BigDecimal("0.075"),   // 7,5%
        new BigDecimal("0.15"),    // 15%
        new BigDecimal("0.225"),   // 22,5%
        new BigDecimal("0.275")    // 27,5%
    };

    private static final BigDecimal DEDUCAO_DEPENDENTE = new BigDecimal("189.59");
    private static final BigDecimal ALIQUOTA_FGTS = new BigDecimal("0.08"); // 8%

    public CalculoFolhaService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public FolhaPagamento calcularFolha(UUID funcionarioId, YearMonth anoMes) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        return FolhaPagamento.builder()
            .funcionarioId(funcionarioId)
            .anoMes(anoMes.format(DateTimeFormatter.ofPattern("yyyy-MM")))
            .salarioBase(funcionario.getSalarioBase() != null ? 
                BigDecimal.valueOf(funcionario.getSalarioBase()) : BigDecimal.ZERO)
            .build();
    }

    public FolhaPagamento calcularINSS(FolhaPagamento folha) {
        BigDecimal salarioBase = folha.getSalarioBase();
        BigDecimal inss = calcularINSS(salarioBase);
        folha.setInss(inss);
        return folha;
    }

    public FolhaPagamento calcularIRRF(FolhaPagamento folha, Integer numeroDependentes) {
        BigDecimal baseCalculo = folha.getSalarioBase()
            .add(folha.getHorasExtras() != null ? folha.getHorasExtras() : BigDecimal.ZERO)
            .add(folha.getAdicionalNoturno() != null ? folha.getAdicionalNoturno() : BigDecimal.ZERO)
            .add(folha.getComissoes() != null ? folha.getComissoes() : BigDecimal.ZERO)
            .add(folha.getBonus() != null ? folha.getBonus() : BigDecimal.ZERO)
            .subtract(folha.getInss() != null ? folha.getInss() : BigDecimal.ZERO)
            .subtract(DEDUCAO_DEPENDENTE.multiply(new BigDecimal(numeroDependentes != null ? numeroDependentes : 0)));

        BigDecimal irrf = calcularIRRF(baseCalculo);
        folha.setIrrf(irrf);
        return folha;
    }

    public FolhaPagamento calcularFGTS(FolhaPagamento folha) {
        BigDecimal baseFGTS = folha.getSalarioBase()
            .add(folha.getHorasExtras() != null ? folha.getHorasExtras() : BigDecimal.ZERO)
            .add(folha.getAdicionalNoturno() != null ? folha.getAdicionalNoturno() : BigDecimal.ZERO);
        
        folha.setFgts(baseFGTS.multiply(ALIQUOTA_FGTS).setScale(2, RoundingMode.HALF_UP));
        return folha;
    }

    private BigDecimal calcularINSS(BigDecimal salario) {
        BigDecimal inss = BigDecimal.ZERO;
        BigDecimal baseCalculo = salario;

        for (int i = 0; i < FAIXAS_INSS.length; i++) {
            if (salario.compareTo(FAIXAS_INSS[i]) <= 0) {
                BigDecimal limiteInferior = i > 0 ? FAIXAS_INSS[i - 1] : BigDecimal.ZERO;
                BigDecimal baseFaixa = salario.subtract(limiteInferior);
                inss = inss.add(baseFaixa.multiply(ALIQUOTAS_INSS[i]));
                break;
            } else {
                BigDecimal limiteInferior = i > 0 ? FAIXAS_INSS[i - 1] : BigDecimal.ZERO;
                BigDecimal baseFaixa = FAIXAS_INSS[i].subtract(limiteInferior);
                inss = inss.add(baseFaixa.multiply(ALIQUOTAS_INSS[i]));
            }
        }

        // Teto INSS
        BigDecimal tetoINSS = new BigDecimal("908.85"); // Valor aproximado 2024
        return inss.min(tetoINSS).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIRRF(BigDecimal baseCalculo) {
        if (baseCalculo.compareTo(FAIXAS_IRRF[0]) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal irrf = BigDecimal.ZERO;
        BigDecimal deducao = BigDecimal.ZERO;

        for (int i = 1; i < FAIXAS_IRRF.length; i++) {
            if (baseCalculo.compareTo(FAIXAS_IRRF[i]) <= 0) {
                BigDecimal baseFaixa = baseCalculo.subtract(FAIXAS_IRRF[i - 1]);
                irrf = irrf.add(baseFaixa.multiply(ALIQUOTAS_IRRF[i]));
                break;
            } else {
                BigDecimal baseFaixa = FAIXAS_IRRF[i].subtract(FAIXAS_IRRF[i - 1]);
                irrf = irrf.add(baseFaixa.multiply(ALIQUOTAS_IRRF[i]));
            }
        }

        // Deduções simplificadas por faixa
        if (baseCalculo.compareTo(FAIXAS_IRRF[1]) > 0 && baseCalculo.compareTo(FAIXAS_IRRF[2]) <= 0) {
            deducao = new BigDecimal("169.44");
        } else if (baseCalculo.compareTo(FAIXAS_IRRF[2]) > 0 && baseCalculo.compareTo(FAIXAS_IRRF[3]) <= 0) {
            deducao = new BigDecimal("381.44");
        } else if (baseCalculo.compareTo(FAIXAS_IRRF[3]) > 0) {
            deducao = new BigDecimal("662.77");
        }

        return irrf.subtract(deducao).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public record ResumoFolha(
        BigDecimal totalProventos,
        BigDecimal totalDescontos,
        BigDecimal totalLiquido,
        Long totalFuncionarios
    ) {}
}
