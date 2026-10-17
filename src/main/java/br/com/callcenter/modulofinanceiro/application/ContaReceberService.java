package br.com.callcenter.modulofinanceiro.application;

import br.com.callcenter.modulofinanceiro.domain.entity.ContaReceber;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.ContaReceberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ContaReceberService {

    private final ContaReceberRepository contaReceberRepository;

    public ContaReceberService(ContaReceberRepository contaReceberRepository) {
        this.contaReceberRepository = contaReceberRepository;
    }

    public List<ContaReceber> listarTodas() {
        return contaReceberRepository.findAll();
    }

    public ContaReceber buscarPorId(UUID id) {
        return contaReceberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta a receber não encontrada"));
    }

    public List<ContaReceber> listarPorStatus(ContaReceber.StatusConta status) {
        return contaReceberRepository.findByStatus(status);
    }

    public List<ContaReceber> listarPorCategoria(ContaReceber.CategoriaReceita categoria) {
        return contaReceberRepository.findByCategoria(categoria);
    }

    public List<ContaReceber> listarPorVencimento(LocalDate inicio, LocalDate fim) {
        return contaReceberRepository.findByVencimentoBetween(inicio, fim);
    }

    public List<ContaReceber> listarVencidas() {
        return contaReceberRepository.findVencidas(LocalDate.now());
    }

    public List<ContaReceber> listarAVencerNoPeriodo(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate fim = hoje.plusDays(dias);
        return contaReceberRepository.findAVencerNoPeriodo(hoje, fim);
    }

    public List<ContaReceber> buscarPorCliente(String cliente) {
        return contaReceberRepository.findByClienteContainingIgnoreCase(cliente);
    }

    public Double calcularTotalPendente() {
        Double total = contaReceberRepository.sumValorRestante();
        return total != null ? total : 0.0;
    }

    public Long contarVencimentosHoje() {
        return contaReceberRepository.countVencimentosHoje(LocalDate.now());
    }

    @Transactional
    public ContaReceber criar(ContaReceber conta) {
        conta.setStatus(ContaReceber.StatusConta.PENDENTE);
        if (conta.getValorRecebido() == null) {
            conta.setValorRecebido(BigDecimal.ZERO);
        }
        return contaReceberRepository.save(conta);
    }

    @Transactional
    public ContaReceber atualizar(UUID id, ContaReceber contaAtualizada) {
        ContaReceber conta = buscarPorId(id);
        
        // Só permite atualizar detalhes principais
        conta.setDescricao(contaAtualizada.getDescricao());
        conta.setCliente(contaAtualizada.getCliente());
        conta.setValorTotal(contaAtualizada.getValorTotal());
        conta.setDataVencimento(contaAtualizada.getDataVencimento());
        conta.setCategoria(contaAtualizada.getCategoria());
        conta.setObservacoes(contaAtualizada.getObservacoes());
        conta.setNumeroDocumento(contaAtualizada.getNumeroDocumento());
        
        return contaReceberRepository.save(conta);
    }

    @Transactional
    public ContaReceber receber(UUID id, BigDecimal valor) {
        ContaReceber conta = buscarPorId(id);
        
        if (conta.getStatus() == ContaReceber.StatusConta.PAGO || 
            conta.getStatus() == ContaReceber.StatusConta.CANCELADO) {
            throw new RuntimeException("Conta possui status " + conta.getStatus() + " e não pode receber pagamentos");
        }
        
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor recebido deve ser maior que zero");
        }

        BigDecimal recebimentoAtual = conta.getValorRecebido() != null ? conta.getValorRecebido() : BigDecimal.ZERO;
        BigDecimal novoRecebimento = recebimentoAtual.add(valor);

        // Não permite receber mais do que o devido
        if (novoRecebimento.compareTo(conta.getValorTotal()) > 0) {
            throw new RuntimeException("Valor de recebimento supera o saldo da conta");
        }

        conta.setValorRecebido(novoRecebimento);
        conta.setDataRecebimento(LocalDate.now());
        
        return contaReceberRepository.save(conta); // o @PreUpdate cuidará do Status
    }

    @Transactional
    public ContaReceber receberTotal(UUID id) {
        ContaReceber conta = buscarPorId(id);
        
        if (conta.getStatus() == ContaReceber.StatusConta.PAGO || 
            conta.getStatus() == ContaReceber.StatusConta.CANCELADO) {
            throw new RuntimeException("Conta não permite quitação");
        }

        conta.setValorRecebido(conta.getValorTotal());
        conta.setDataRecebimento(LocalDate.now());
        
        return contaReceberRepository.save(conta);
    }

    @Transactional
    public void cancelar(UUID id) {
        ContaReceber conta = buscarPorId(id);
        conta.setStatus(ContaReceber.StatusConta.CANCELADO);
        contaReceberRepository.save(conta);
    }

    @Transactional
    public void deletar(UUID id) {
        contaReceberRepository.deleteById(id);
    }
}
