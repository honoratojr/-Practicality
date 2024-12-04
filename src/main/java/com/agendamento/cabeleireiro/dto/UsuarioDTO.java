package com.agendamento.cabeleireiro.dto;

import java.io.Serializable;

import org.hibernate.validator.constraints.br.CPF;

import com.agendamento.cabeleireiro.model.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    private long id;
    
    @NotNull
    @Size(min = 3, message = "O nome deve ter no mínimo 3 carateres")
    private String nome;

    @CPF(message = "CPF inválido")
    @Size(min = 11, message = "Deve conter 11 números")
    private String cpf;

    @Email(message = "Email inválido")
    @NotEmpty(message = "O campo deve ser preenchido.")
    private String email;

    @NotEmpty(message = "O campo deve ser preenchido.")
    private String telefone;

    @NotEmpty(message = "A senha deve ser informada")
	@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotEmpty(message = "Informe a senha novamente")
    private String confirmarSenha;

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setCpf(this.cpf);
        usuario.setEmail(this.email);
        usuario.setTelefone(this.telefone);
        usuario.setSenha(this.senha);

        return usuario;
    }
    
}
