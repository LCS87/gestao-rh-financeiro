package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "funcionarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String cpf;
    
    @Column
    private String email;
    
    private String telefone;
    
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    
    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;
    
    @Column(name = "data_demissao")
    private LocalDate dataDemissao;
    
    @Enumerated(EnumType.STRING)
    private StatusFuncionario status;
    
    @Column(name = "departamento_id")
    private UUID departamentoId;
    
    @Column(name = "cargo_id")
    private UUID cargoId;
    
    @Column(name = "equipe_id")
    private UUID equipeId;

    @Column(name = "escala_id")
    private UUID escalaId;
    
    @Column(name = "salario_base")
    private Double salarioBase;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
    public enum StatusFuncionario {
        ATIVO, INATIVO, AFASTADO, FERIAS
    }
}
