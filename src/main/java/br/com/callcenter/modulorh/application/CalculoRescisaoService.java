package br.com.callcenter.modulorh.application;

import br.com.callcenter.modulorh.domain.entity.Demissao;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CalculoRescisaoService {

    private static final BigDecimal VALOR_DIA_TRABALHO = BigDecimal.valueOf(30);
    private static final BigDecimal PERCENTUAL_FERIAS = BigDecimal.valueOf(0.3333); // 1/3
    private static final BigDecimal PERCENTUAL_DECIMO_TERCEIRO = BigDecimal.valueOf(0.0833); // 1/12
    private static final BigDecimal PERCENTUAL_FGTS = BigDecimal.valueOf(0.08); // 8%
    private static final BigDecimal PERCENTUAL_MULTA_FGTS = BigDecimal.valueOf(0.40); // 40% sobre FGTS

    public void calcularRescisao(Demissao demissao, Funcionario funcionario) {
        if (funcionario.getSalarioBase() == null || funcionario.getSalarioBase() <= 0) {
            throw new IllegalArgumentException("Salário base do funcionário não pode ser nulo ou zero");
        }

        BigDecimal salarioBase = BigDecimal.valueOf(funcionario.getSalarioBase()).setScale(2, RoundingMode.HALF_UP);
        LocalDate dataAdmissao = funcionario.getDataAdmissao();
        LocalDate dataDemissao = demissao.getDataDemissao();

        if (dataAdmissao == null) {
            throw new IllegalArgumentException("Data de admissão do funcionário não pode ser nula");
        }

        // Calcular dias trabalhados
        long diasTrabalhados = ChronoUnit.DAYS.between(dataAdmissao, dataDemissao);
        long mesesTrabalhados = ChronoUnit.MONTHS.between(dataAdmissao, dataDemissao);

        // Saldo de salário (proporcional aos dias trabalhados no mês)
        BigDecimal saldoSalario = calcularSaldoSalario(salarioBase, dataDemissao);

        // Férias vencidas (se tiver mais de 12 meses)
        BigDecimal feriasVencidas = calcularFeriasVencidas(salarioBase, mesesTrabalhados);

        // Férias proporcionais
        BigDecimal feriasProporcionais = calcularFeriasProporcionais(salarioBase, mesesTrabalhados);

        // 13º salário proporcional
        BigDecimal decimoTerceiroProporcional = calcularDecimoTerceiroProporcional(salarioBase, mesesTrabalhados);

        // Aviso prévio indenizado
        BigDecimal avisoPrevioIndenizado = BigDecimal.ZERO;
        if (demissao.getAvisoPrevioIndenizado() != null && demissao.getAvisoPrevioIndenizado()) {
            avisoPrevioIndenizado = salarioBase; // 30 dias de salário
        }

        // FGTS
        BigDecimal fgts = calcularFGTS(salarioBase, mesesTrabalhados);

        // Multa do FGTS (40% sobre o FGTS) - apenas para demissão sem justa causa
        BigDecimal multaFgts = BigDecimal.ZERO;
        if (demissao.getTipoDemissao() == Demissao.TipoDemissao.SEM_JUSTA_CAUSA) {
            multaFgts = fgts.multiply(PERCENTUAL_MULTA_FGTS).setScale(2, RoundingMode.HALF_UP);
        }

        // Valor total da rescisão
        BigDecimal valorTotal = saldoSalario
                .add(feriasVencidas)
                .add(feriasProporcionais)
                .add(decimoTerceiroProporcional)
                .add(avisoPrevioIndenizado)
                .add(fgts)
                .add(multaFgts);

        // Atualizar demissão com os valores calculados
        demissao.setSaldoSalario(saldoSalario);
        demissao.setFeriasVencidas(feriasVencidas);
        demissao.setFeriasProporcionais(feriasProporcionais);
        demissao.setDecimoTerceiroProporcional(decimoTerceiroProporcional);
        demissao.setAvisoPrevioIndenizadoValor(avisoPrevioIndenizado);
        demissao.setFgts(fgts);
        demissao.setMultaFgts(multaFgts);
        demissao.setValorTotalRescisao(valorTotal);
        demissao.setDataCalculo(LocalDate.now());
        demissao.setStatus(Demissao.StatusDemissao.CALCULADA);
    }

    private BigDecimal calcularSaldoSalario(BigDecimal salarioBase, LocalDate dataDemissao) {
        int diasNoMes = dataDemissao.lengthOfMonth();
        int diasTrabalhadosNoMes = dataDemissao.getDayOfMonth();
        
        return salarioBase
                .divide(BigDecimal.valueOf(diasNoMes), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(diasTrabalhadosNoMes))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularFeriasVencidas(BigDecimal salarioBase, long mesesTrabalhados) {
        if (mesesTrabalhados < 12) {
            return BigDecimal.ZERO;
        }
        // Férias vencidas = salário + 1/3
        return salarioBase.add(salarioBase.multiply(PERCENTUAL_FERIAS))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularFeriasProporcionais(BigDecimal salarioBase, long mesesTrabalhados) {
        if (mesesTrabalhados < 12) {
            // Férias proporcionais = (meses/12) * (salário + 1/3)
            BigDecimal proporcao = BigDecimal.valueOf(mesesTrabalhados)
                    .divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
            return salarioBase.add(salarioBase.multiply(PERCENTUAL_FERIAS))
                    .multiply(proporcao)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calcularDecimoTerceiroProporcional(BigDecimal salarioBase, long mesesTrabalhados) {
        if (mesesTrabalhados == 0) {
            return BigDecimal.ZERO;
        }
        // 13º proporcional = (meses/12) * salário
        BigDecimal proporcao = BigDecimal.valueOf(mesesTrabalhados)
                .divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
        return salarioBase.multiply(proporcao).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularFGTS(BigDecimal salarioBase, long mesesTrabalhados) {
        // FGTS = 8% do salário * meses trabalhados
        BigDecimal fgtsMensal = salarioBase.multiply(PERCENTUAL_FGTS);
        return fgtsMensal.multiply(BigDecimal.valueOf(mesesTrabalhados))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
