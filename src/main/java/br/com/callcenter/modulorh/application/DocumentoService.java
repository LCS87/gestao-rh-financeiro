package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.BusinessException;
import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.DocumentoDto;
import br.com.callcenter.modulorh.domain.entity.Documento;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.DocumentoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    
    @Value("${app.documentos.diretorio:./documentos}")
    private String diretorioDocumentos;

    public DocumentoService(DocumentoRepository documentoRepository, FuncionarioRepository funcionarioRepository) {
        this.documentoRepository = documentoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Documento> listarTodos() {
        return documentoRepository.findAll();
    }

    public List<Documento> listarPorFuncionario(UUID funcionarioId) {
        return documentoRepository.findByFuncionarioId(funcionarioId);
    }

    public Documento buscarPorId(UUID id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado"));
    }

    public Documento uploadDocumento(UUID funcionarioId, Documento.TipoDocumento tipoDocumento, 
                                     MultipartFile arquivo, LocalDate dataEmissao, 
                                     LocalDate dataValidade, String observacoes, UUID uploadedById) {
        // Verificar se funcionário existe
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        try {
            // Criar diretório se não existir
            Path diretorio = Paths.get(diretorioDocumentos, funcionarioId.toString());
            Files.createDirectories(diretorio);

            // Gerar nome único para o arquivo
            String nomeArquivo = tipoDocumento.name() + "_" + UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path caminhoArquivo = diretorio.resolve(nomeArquivo);

            // Salvar arquivo
            Files.copy(arquivo.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

            // Criar entidade Documento
            Documento documento = Documento.builder()
                    .funcionarioId(funcionarioId)
                    .tipoDocumento(tipoDocumento)
                    .nomeArquivo(arquivo.getOriginalFilename())
                    .caminhoArquivo(caminhoArquivo.toString())
                    .tamanhoArquivo(arquivo.getSize())
                    .tipoMime(arquivo.getContentType())
                    .dataEmissao(dataEmissao)
                    .dataValidade(dataValidade)
                    .dataUpload(LocalDate.now())
                    .observacoes(observacoes)
                    .uploadedById(uploadedById)
                    .build();

            documento.atualizarStatus();

            return documentoRepository.save(documento);
        } catch (IOException e) {
            throw new BusinessException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    public Documento atualizar(UUID id, DocumentoDto dto) {
        Documento existente = buscarPorId(id);
        
        existente.setTipoDocumento(dto.getTipoDocumento());
        existente.setDataEmissao(dto.getDataEmissao());
        existente.setDataValidade(dto.getDataValidade());
        existente.setObservacoes(dto.getObservacoes());
        
        existente.atualizarStatus();

        return documentoRepository.save(existente);
    }

    public void deletar(UUID id) {
        Documento documento = buscarPorId(id);
        
        try {
            // Deletar arquivo físico
            Path caminhoArquivo = Paths.get(documento.getCaminhoArquivo());
            if (Files.exists(caminhoArquivo)) {
                Files.delete(caminhoArquivo);
            }
        } catch (IOException e) {
            // Log do erro, mas continua com a exclusão do registro
            System.err.println("Erro ao deletar arquivo físico: " + e.getMessage());
        }
        
        documentoRepository.delete(documento);
    }

    public List<Documento> buscarDocumentosAVencer(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);
        return documentoRepository.findDocumentosAVencer(hoje, limite);
    }

    public List<Documento> buscarDocumentosVencidos() {
        return documentoRepository.findDocumentosVencidos(LocalDate.now());
    }

    public byte[] downloadDocumento(UUID id) {
        Documento documento = buscarPorId(id);
        
        try {
            Path caminhoArquivo = Paths.get(documento.getCaminhoArquivo());
            if (!Files.exists(caminhoArquivo)) {
                throw new ResourceNotFoundException("Arquivo físico não encontrado");
            }
            
            return Files.readAllBytes(caminhoArquivo);
        } catch (IOException e) {
            throw new BusinessException("Erro ao ler arquivo: " + e.getMessage());
        }
    }
}
