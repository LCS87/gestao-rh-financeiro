package br.com.callcenter.modulofinanceiro.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulofinanceiro.domain.entity.ContaPagar;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.ContaPagarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ContaPagarService {

    private final ContaPagarRepository contaPagarRepository;

    public ContaPagarService(ContaPagarRepository contaPagarRepository) {
        this.contaPagarRepository = contaPagarRepository;
    }

    public List<ContaPagar> listarTodas() {
        return contaPagarRepository.findAll();
    }

    public ContaPagar buscarPorId(UUID id) {
        return contaPagarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a pagar não encontrada"));
    }

    public List<ContaPagar> listarPorStatus(ContaPagar.StatusConta status) {
        return contaPagarRepository.findByStatus(status);
    }

    public List<ContaPagar> listarPorCategoria(ContaPagar.CategoriaConta categoria) {
        return contaPagarRepository.findByCategoria(categoria);
    }

    public List<ContaPagar> listarPorVencimento(LocalDate inicio, LocalDate fim) {
        return contaPagarRepository.findByVencimentoBetween(inicio, fim);
    }

    public List<ContaPagar> listarVencidas() {
        return contaPagarRepository.findVencidas(LocalDate.now());
    }

    public List<ContaPagar> listarAVencerNoPeriodo(int dias) {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = inicio.plusDays(dias);
        return contaPagarRepository.findAVencerNoPeriodo(inicio, fim);
    }

    public ContaPagar criar(ContaPagar conta) {
        conta.setStatus(ContaPagar.StatusConta.PENDENTE);
        return contaPagarRepository.save(conta);
    }

    public ContaPagar atualizar(UUID id, ContaPagar contaAtualizada) {
        ContaPagar existente = buscarPorId(id);
        
        existente.setDescricao(contaAtualizada.getDescricao());
        existente.setFornecedor(contaAtualizada.getFornecedor());
        existente.setValorTotal(contaAtualizada.getValorTotal());
        existente.setDataVencimento(contaAtualizada.getDataVencimento());
        existente.setCategoria(contaAtualizada.getCategoria());
        existente.setObservacoes(contaAtualizada.getObservacoes());
        existente.setNumeroDocumento(contaAtualizada.getNumeroDocumento());
        
        return contaPagarRepository.save(existente);
    }

    public ContaPagar pagar(UUID id, BigDecimal valorPago) {
        ContaPagar conta = buscarPorId(id);
        
        BigDecimal novoValorPago = conta.getValorPago().add(valorPago);
        conta.setValorPago(novoValorPago);
        conta.setDataPagamento(LocalDate.now());
        
        if (novoValorPago.compareTo(conta.getValorTotal()) >= 0) {
            conta.setStatus(ContaPagar.StatusConta.PAGO);
        } else {
            conta.setStatus(ContaPagar.StatusConta.PARCIAL);
        }
        
        return contaPagarRepository.save(conta);
    }

    public ContaPagar pagarTotal(UUID id) {
        ContaPagar conta = buscarPorId(id);
        return pagar(id, conta.getValorRestante());
    }

    public void cancelar(UUID id) {
        ContaPagar conta = buscarPorId(id);
        conta.setStatus(ContaPagar.StatusConta.CANCELADO);
        contaPagarRepository.save(conta);
    }

    public void deletar(UUID id) {
        ContaPagar existente = buscarPorId(id);
        contaPagarRepository.delete(existente);
    }

    public BigDecimal calcularTotalPendente() {
        Double valor = contaPagarRepository.sumValorRestante();
        return valor != null ? BigDecimal.valueOf(valor) : BigDecimal.ZERO;
    }

    public Long contarVencimentosHoje() {
        return contaPagarRepository.countVencimentosHoje(LocalDate.now());
    }

    public List<ContaPagar> buscarPorFornecedor(String fornecedor) {
        return contaPagarRepository.findByFornecedorContainingIgnoreCase(fornecedor);
    }
}
