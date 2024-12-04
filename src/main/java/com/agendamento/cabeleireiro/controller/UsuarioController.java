package com.agendamento.cabeleireiro.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agendamento.cabeleireiro.dto.UsuarioDTO;
import com.agendamento.cabeleireiro.model.Agendamento;
import com.agendamento.cabeleireiro.model.Usuario;
import com.agendamento.cabeleireiro.repository.AgendamentoRepository;
import com.agendamento.cabeleireiro.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admim")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    @GetMapping("/index")
    public String primeiraPagina() {
        return "index";
    }

    @GetMapping("/cadastrar")
    public String cadastrarUsuario(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        return "admim/cadastrar";
    }

    @PostMapping("/salvar")
    public String salvarCadastro(@Valid UsuarioDTO usuarioDTO, BindingResult result, RedirectAttributes attributes,
            Model model) {
        if (result.hasErrors()) {
            return "admim/cadastrar";
        }
        if (!usuarioDTO.getSenha().equals(usuarioDTO.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "erro.senha", "As senhas não coincidem!");
            return "admin/cadastrar";
        }
        Usuario usuario = usuarioDTO.toUsuario();
        usuarioRepository.save(usuario);
        attributes.addFlashAttribute("mensagem", "Usuario salvo com sucesso!");
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String loginUsuario() {
        return "user/login-user";
    }

    @PostMapping("/logado")
    public String usuarioLogado(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes,
            HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mensagem", "ERRO: Credenciais inválidas, tente novamente!");
            return "user/login-user";
        }
        Usuario usuarioLogin = usuarioRepository.findByEmail(usuario.getEmail());

        if (usuarioLogin == null) {
            model.addAttribute("mensagem", "ERRO LOGIN: Verifique os dados inseridos e tente novamente.");
            return "user/login-user";
        }
        session.setAttribute("usuarioLogado", usuarioLogin);
        return "redirect:/admim/agendamentos";
    }

    @GetMapping("/agendamentos")
    public String listarAgendamentosProfissional(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado != null) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            List<Agendamento> agendamentos = agendamentoRepository.findByProfissional(usuarioLogado);
    
            // Formatando a data e hora
            agendamentos
                    .forEach(agendamento -> agendamento.setDataHoraFormatada(agendamento.getDataHora().format(formatter)));
    
            model.addAttribute("agendamentos", agendamentos);
            model.addAttribute("usuario", usuarioLogado);
            return "user/agendamentos";
        }
        return "redirect:/admim/login";
    }

    // Endpoint para alterar o status do agendamento
    @GetMapping("/alterarStatus/{status}/{id}")
    public String alterarStatus(@PathVariable String status, @PathVariable Long id, Model model) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado!"));

        agendamento.setStatus(status);
        agendamentoRepository.save(agendamento);

        // Redireciona de volta à página de agendamentos
        return "redirect:/agendamentos";
    }

    // Endpoint para visualizar os detalhes do agendamento
    @GetMapping("/detalhesAgendamento/{id}")
    public String detalhesAgendamento(@PathVariable Long id, Model model) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado!"));

        model.addAttribute("agendamento", agendamento);
        return "detalhesAgendamento"; // Nome do template para os detalhes
    }

}