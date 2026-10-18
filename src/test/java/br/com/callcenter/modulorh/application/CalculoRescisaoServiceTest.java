package br.com.callcenter.modulorh.application;

import br.com.callcenter.modulorh.domain.entity.Demissao;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CalculoRescisaoServiceTest {

    private CalculoRescisaoService service;
    private Funcionario funcionario;
    private Demissao demissao;

    @BeforeEach
    void setUp() {
        service = new CalculoRescisaoService();
        funcionario = new Funcionario();
        funcionario.setNome("Operador Teste");
        funcionario.setSalarioBase(3000.0);
        funcionario.setDataAdmissao(LocalDate.of(2024, 1, 1));

        demissao = new Demissao();
        demissao.setFuncionarioId(funcionario.getId());
        demissao.setDataDemissao(LocalDate.of(2024, 7, 15)); // 6 meses e 14 dias
        demissao.setTipoDemissao(Demissao.TipoDemissao.SEM_JUSTA_CAUSA);
        demissao.setAvisoPrevioIndenizado(false);
    }

    @Test
    @DisplayName("Rescisao sem justa causa: saldo de salario proporcional aos dias do mes")
    void deveCalcularSaldoDeSalario() {
        service.calcularRescisao(demissao, funcionario);

        // 3000 / 31 dias (arredondado p/ 96.77) * 15 dias trabalhados = 1451.55
        assertEquals(new BigDecimal("1451.55"), demissao.getSaldoSalario());
    }

    @Test
    @DisplayName("Rescisao: ferias proporcionais com 1/3 para menos de 12 meses")
    void deveCalcularFerasProporcionais() {
        service.calcularRescisao(demissao, funcionario);

        // (6/12) * (3000 + 3000*0.3333) = 0.5 * 3999.90 = 1999.95
        assertEquals(new BigDecimal("1999.95"), demissao.getFeriasProporcionais());
        assertEquals(BigDecimal.ZERO, demissao.getFeriasVencidas());
    }

    @Test
    @DisplayName("Rescisao: 13o proporcional (meses/12 * salario)")
    void deveCalcularDecimoTerceiroProporcional() {
        service.calcularRescisao(demissao, funcionario);

        // (6/12) * 3000 = 1500.00
        assertEquals(new BigDecimal("1500.00"), demissao.getDecimoTerceiroProporcional());
    }

    @Test
    @DisplayName("Rescisao: FGTS 8% por mes trabalhado e multa de 40% sem justa causa")
    void deveCalcularFgtsComMulta() {
        service.calcularRescisao(demissao, funcionario);

        // FGTS = 3000 * 0.08 * 6 = 1440.00
        assertEquals(new BigDecimal("1440.00"), demissao.getFgts());
        // Multa = 1440 * 0.40 = 576.00
        assertEquals(new BigDecimal("576.00"), demissao.getMultaFgts());
    }

    @Test
    @DisplayName("Pedido proprio: sem multa de FGTS")
    void pedidoProprioNaoTemMultaFgts() {
        demissao.setTipoDemissao(Demissao.TipoDemissao.PEDIDO);

        service.calcularRescisao(demissao, funcionario);

        assertEquals(BigDecimal.ZERO, demissao.getMultaFgts());
    }

    @Test
    @DisplayName("Aviso previo indenizado adiciona uma remuneracao integral")
    void deveIncluirAvisoPrevioIndenizado() {
        demissao.setAvisoPrevioIndenizado(true);

        service.calcularRescisao(demissao, funcionario);

        assertEquals(new BigDecimal("3000.00"), demissao.getAvisoPrevioIndenizadoValor());
    }

    @Test
    @DisplayName("Status da rescisao fica CALCULADA com data de calculo")
    void deveAtualizarStatusEData() {
        service.calcularRescisao(demissao, funcionario);

        assertEquals(Demissao.StatusDemissao.CALCULADA, demissao.getStatus());
        assertNotNull(demissao.getDataCalculo());
    }

    @Test
    @DisplayName("Valor total = soma das verbas rescisorias")
    void deveSomarValorTotal() {
        service.calcularRescisao(demissao, funcionario);

        BigDecimal esperado = demissao.getSaldoSalario()
                .add(demissao.getFeriasVencidas())
                .add(demissao.getFeriasProporcionais())
                .add(demissao.getDecimoTerceiroProporcional())
                .add(demissao.getAvisoPrevioIndenizadoValor())
                .add(demissao.getFgts())
                .add(demissao.getMultaFgts());

        assertEquals(esperado, demissao.getValorTotalRescisao());
    }

    @Test
    @DisplayName("Salario base invalido lanca IllegalArgumentException")
    void deveRejeitarSalarioInvalido() {
        funcionario.setSalarioBase(0.0);
        assertThrows(IllegalArgumentException.class,
                () -> service.calcularRescisao(demissao, funcionario));
    }

    @Test
    @DisplayName("Data de admissao nula lanca IllegalArgumentException")
    void deveRejeitarSemDataAdmissao() {
        funcionario.setDataAdmissao(null);
        assertThrows(IllegalArgumentException.class,
                () -> service.calcularRescisao(demissao, funcionario));
    }
}
