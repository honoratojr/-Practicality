package com.agendamento.cabeleireiro.model;

import java.io.Serializable;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_usuarios")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String senha;

    @OneToMany(mappedBy = "profissional")  // Relacionamento com os agendamentos atribuídos ao profissional
    private List<Agendamento> agendamentos;

    public String getPrimeiroNome() {
        if (this.nome != null && !this.nome.isEmpty()) {
            String[] partesNome = this.nome.trim().split("\\s+");
            return partesNome[0];
        }
        return "";
    }

}
