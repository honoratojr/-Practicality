package com.agendamento.cabeleireiro.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_clientes")
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String nome;
    private String cpf;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAniversario;

    private String genero;
    private String email;
    private String telefone;
    private String senha;

    @OneToMany(mappedBy = "cliente")  // Relacionamento com agendamentos realizados pelo cliente
    private List<Agendamento> agendamentos;

    //Busca o primeiro nome do cliente
    public String getPrimeiroNome() {
        if (this.nome != null && !this.nome.isEmpty()) {
            String[] partesNome = this.nome.trim().split("\\s+");
            return partesNome[0];
        }
        return "";
    }

}
