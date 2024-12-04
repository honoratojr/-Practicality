package com.agendamento.cabeleireiro.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agendamento.cabeleireiro.dto.ClienteDTO;
import com.agendamento.cabeleireiro.model.Agendamento;
import com.agendamento.cabeleireiro.model.Cliente;
import com.agendamento.cabeleireiro.repository.AgendamentoRepository;
import com.agendamento.cabeleireiro.repository.ClienteRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    @GetMapping("/cadastrar")
    public String cadastroCliente(Model model) {
        model.addAttribute("clienteDTO", new ClienteDTO());
        return "client/cadastro-cliente";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@Valid ClienteDTO clienteDTO, BindingResult result, RedirectAttributes attributes,
            Model model) {
        if (result.hasErrors()) {
            return "client/cadastro-cliente";
        }
        if (!clienteDTO.getSenha().equals(clienteDTO.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "erro.senha", "As senhas não coincidem!");
            return "client/cadastro-cliente";
        }

        Cliente cliente = clienteDTO.toCliente();
        clienteRepository.save(cliente);
        attributes.addFlashAttribute("mensagem", "Conta criada com sucesso!");
        return "client/login-cliente";
    }

    @GetMapping("/login")
    public String loginCliente() {
        return "client/login-cliente";
    }
    // Faz a validação e autenticação do Cliente.
    // Após esse ponto, todas as operações são realizadas, apenas com o cliente
    // logado no sistema.

    @PostMapping("/checked")
    public String clienteAutenticado(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes,
            HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("erroLogin", "ERRO: Credenciais inválidas, tente novamente!");
            return "client/login-cliente";
        }
        Cliente clienteLogin = clienteRepository.findByEmail(cliente.getEmail());

        if (clienteLogin == null /*
                                  * || !new BCryptPasswordEncoder().matches(cliente.getSenha(),
                                  * clienteLogin.getSenha())
                                  */ ) {
            model.addAttribute("erroLogin", "ERRO: Verifique os dados inseridos e tente novamente.");
            return "client/login-cliente";
        }
        session.setAttribute("clienteLogado", clienteLogin);
        // model.addAttribute("nomeCliente", clienteLogin.getPrimeiroNome());
        return "redirect:/home";
    }

    // Consultar os agendamentos - Cliente
    @GetMapping("/meusAgendamentos")
    public String listarAgendamentos(HttpSession session, Model model) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            return "redirect:/cliente/login";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Agendamento> agendamentos = agendamentoRepository.findByCliente(clienteLogado);
        // Formatando a data e hora
        agendamentos
                .forEach(agendamento -> agendamento.setDataHoraFormatada(agendamento.getDataHora().format(formatter)));
        model.addAttribute("nomeCliente", clienteLogado.getPrimeiroNome());
        model.addAttribute("agendamentos", agendamentos);
        return "client/lista-agendamento";
    }

    // Busca os dados pessoais do cliente logado, e exibe na tela
    @GetMapping("/dadosCliente")
    public String meusDados(HttpSession session, Model model) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado != null) {
            model.addAttribute("nomeCliente", clienteLogado.getPrimeiroNome());
            model.addAttribute("cliente", clienteLogado);
            return "/client/dados-cliente";
        }
        return "redirect:/login";
    }

    @GetMapping("/sair")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

}
