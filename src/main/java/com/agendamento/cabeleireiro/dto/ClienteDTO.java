package com.agendamento.cabeleireiro.dto;

import java.io.Serializable;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import com.agendamento.cabeleireiro.model.Cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    
    @NotNull
    @Size(min = 7, message = "Deve possuir nome e sobrenome!")
    private String nome;

    @CPF(message = "CPF inválido!")
    @Size(min = 11, message = "Deve conter 11 números.")
    private String cpf;

    @NotNull(message = "Informe seu aniversário.")
    private LocalDate dataAniversario;

    @NotEmpty(message = "Informe seu gênero.")
    private String genero;

    @Email(message = "Email inválido!")
    @NotEmpty(message = "O campo deve ser preenchido.")
    private String email;


    private String telefone;

    @NotEmpty(message = "A senha deve ser informada!")
	@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;

    @NotEmpty(message = "Confirme a senha.")
    private String confirmarSenha;

    public Cliente toCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setCpf(this.cpf);
        cliente.setDataAniversario(this.dataAniversario);
        cliente.setGenero(this.genero);
        cliente.setEmail(this.email);
        cliente.setTelefone(this.telefone);
        cliente.setSenha(this.senha);

        return cliente;
    }
    
}
