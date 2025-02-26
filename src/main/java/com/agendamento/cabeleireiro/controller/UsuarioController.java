package com.agendamento.cabeleireiro.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agendamento.cabeleireiro.dto.UsuarioDTO;
import com.agendamento.cabeleireiro.model.Agendamento;
import com.agendamento.cabeleireiro.model.Usuario;
import com.agendamento.cabeleireiro.repository.AgendamentoRepository;
import com.agendamento.cabeleireiro.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
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
        return "admin/cadastrar";
    }

    @PostMapping("/salvar")
    public String salvarCadastro(@Valid UsuarioDTO usuarioDTO, BindingResult result, RedirectAttributes attributes,
            Model model) {
        if (result.hasErrors()) {
            return "admin/cadastrar";
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
        return "redirect:/admin/agendamentos";
    }

    @GetMapping("/agendamentos")
    public String listarAgendamentosProfissional(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado != null) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            // Recupera os agendamentos ordenados do mais recente para o mais antigo
            List<Agendamento> agendamentos = agendamentoRepository.findByProfissionalOrderByDataHoraDesc(usuarioLogado);

            // Formatando a data e hora
            agendamentos.forEach(
                    agendamento -> agendamento.setDataHoraFormatada(agendamento.getDataHora().format(formatter)));

            model.addAttribute("agendamentos", agendamentos);
            model.addAttribute("nomeUsuario", usuarioLogado.getPrimeiroNome());
            model.addAttribute("usuario", usuarioLogado);
            return "user/agendamentos";
        }
        return "redirect:/admin/login";
    }

    // Endpoint para alterar o status do agendamento
    @PostMapping("/alterarStatus")
    public String alterarStatus(@RequestParam Long id, @RequestParam String novoStatus, RedirectAttributes attributes) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado!"));

        agendamento.setStatus(novoStatus);
        agendamentoRepository.save(agendamento);
        attributes.addFlashAttribute("mensagem", "Status alterado com sucesso!");

        // Redireciona de volta à página de agendamentos
        return "redirect:agendamentos";
    }

    // Endpoint para visualizar os detalhes do agendamento
    @GetMapping("/verDetalhes")
    public String detalhesAgendamento(@RequestParam Long id, Model model) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado!"));
    // Formatar a data e hora
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String dataHoraFormatada = agendamento.getDataHora().format(formatter);

    // Adicionar a data formatada ao modelo
    model.addAttribute("dataHoraFormatada", dataHoraFormatada);

        model.addAttribute("agendamento", agendamento);
        return "user/detalhes";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

}