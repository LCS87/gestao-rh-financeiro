package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.enums.TipoPostagem;
import br.com.callcenter.modulorh.domain.enums.VisibilidadePostagem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação de postagem no mural")
public class PostagemRequestDTO {

    @NotNull(message = "ID do autor é obrigatório")
    @Schema(description = "ID do autor da postagem", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private String autorId;

    @NotBlank(message = "Conteúdo da postagem é obrigatório")
    @Size(max = 500, message = "Conteúdo não pode ter mais de 500 caracteres")
    @Schema(description = "Conteúdo da postagem", example = "Bom dia equipe! Hoje temos reunião às 10h.", required = true)
    private String conteudo;

    @Size(max = 100, message = "Título não pode ter mais de 100 caracteres")
    @Schema(description = "Título da postagem (opcional)", example = "Reunião Diária")
    private String titulo;

    @NotNull(message = "Tipo da postagem é obrigatório")
    @Schema(description = "Tipo da postagem", example = "PUBLICACAO", required = true)
    private TipoPostagem tipo = TipoPostagem.PUBLICACAO;

    @NotNull(message = "Visibilidade da postagem é obrigatória")
    @Schema(description = "Visibilidade da postagem", example = "PUBLICO", required = true)
    private VisibilidadePostagem visibilidade = VisibilidadePostagem.PUBLICO;

    @Schema(description = "URL da imagem (opcional)", example = "https://exemplo.com/imagem.jpg")
    private String urlImagem;

    @Schema(description = "URL do anexo (opcional)", example = "https://exemplo.com/documento.pdf")
    private String urlAnexo;
}