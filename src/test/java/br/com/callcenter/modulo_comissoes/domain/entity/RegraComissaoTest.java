package br.com.callcenter.modulo_comissoes.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegraComissaoTest {

    @Test
    @DisplayName("Porcentagem sobre venda: 5% de 2000 = 100")
    void porcentagemVenda() {
        RegraComissao regra = new RegraComissao("Padrao", TipoComissao.PORCENTAGEM_VENDA);
        regra.setPorcentagem(new BigDecimal("5"));

        assertEquals(new BigDecimal("100.00"), regra.calcularComissao(new BigDecimal("2000")));
    }

    @Test
    @DisplayName("Valor fixo por venda")
    void valorFixo() {
        RegraComissao regra = new RegraComissao("Fixo", TipoComissao.VALOR_FIXO);
        regra.setValorFixo(new BigDecimal("50"));

        assertEquals(new BigDecimal("50"), regra.calcularComissao(new BigDecimal("9999")));
    }

    @Test
    @DisplayName("Faixa de venda: paga apenas dentro do intervalo [min, max]")
    void faixaVenda() {
        RegraComissao regra = new RegraComissao("Faixa", TipoComissao.FAIXA_VENDA);
        regra.setValorMinimo(new BigDecimal("1000"));
        regra.setValorMaximo(new BigDecimal("5000"));
        regra.setValorFixo(new BigDecimal("120"));

        assertEquals(new BigDecimal("120"), regra.calcularComissao(new BigDecimal("3000")));
        assertEquals(BigDecimal.ZERO, regra.calcularComissao(new BigDecimal("900")));
        assertEquals(BigDecimal.ZERO, regra.calcularComissao(new BigDecimal("6000")));
    }

    @Test
    @DisplayName("Bonus meta: pago quando percentual atinge a meta minima")
    void bonusMetaAtingida() {
        RegraComissao regra = new RegraComissao("Campanha", TipoComissao.BONUS_META);
        regra.setValorFixo(new BigDecimal("500"));
        regra.setMetaMinimaPercentual(new BigDecimal("100"));

        assertEquals(new BigDecimal("500"), regra.simularComissao(BigDecimal.ZERO, new BigDecimal("100")));
        assertEquals(BigDecimal.ZERO, regra.simularComissao(BigDecimal.ZERO, new BigDecimal("99.9")));
    }

    @Test
    @DisplayName("Acelerador: +20% sobre comissao base quando meta batida")
    void aceleradorQuandoMetaBatida() {
        RegraComissao regra = new RegraComissao("Campanha Acel.", TipoComissao.PORCENTAGEM_VENDA);
        regra.setPorcentagem(new BigDecimal("5"));
        regra.setAceleradorPercentual(new BigDecimal("20"));
        regra.setMetaMinimaPercentual(new BigDecimal("100"));

        // base: 5% de 2000 = 100; com acelerador: 120
        assertEquals(new BigDecimal("120.00"), regra.simularComissao(new BigDecimal("2000"), new BigDecimal("110")));
        // sem bater meta: apenas base
        assertEquals(new BigDecimal("100.00"), regra.simularComissao(new BigDecimal("2000"), new BigDecimal("80")));
    }

    @Test
    @DisplayName("calcularComissao simples nao aplica acelerador (meta zero)")
    void calculoSimplesSemAcelerador() {
        RegraComissao regra = new RegraComissao("Com acel", TipoComissao.PORCENTAGEM_VENDA);
        regra.setPorcentagem(new BigDecimal("10"));
        regra.setAceleradorPercentual(new BigDecimal("50"));
        regra.setMetaMinimaPercentual(new BigDecimal("100"));

        assertEquals(new BigDecimal("100.00"), regra.calcularComissao(new BigDecimal("1000")));
    }
}
