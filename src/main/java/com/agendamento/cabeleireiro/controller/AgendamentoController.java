package com.agendamento.cabeleireiro.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agendamento.cabeleireiro.model.Agendamento;
import com.agendamento.cabeleireiro.model.Cliente;
import com.agendamento.cabeleireiro.model.Usuario;
import com.agendamento.cabeleireiro.repository.AgendamentoRepository;
import com.agendamento.cabeleireiro.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AgendamentoController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    // Página para agendar
    @GetMapping("/profissionais")
    public String listarProfissionais(Model model, HttpSession session) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado != null) {
            List<Usuario> profissionais = usuarioRepository.findAll();
            model.addAttribute("nomeCliente", clienteLogado.getPrimeiroNome());
            model.addAttribute("usuarios", profissionais);
            return "client/lista-profissionais";
        }
        return "redirect:/cliente/login";
    }

    @GetMapping("/agendar/{id}")
    public String getMethodName(@PathVariable Long id, Model model, HttpSession session) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado != null) {
            model.addAttribute("nomeCliente", clienteLogado.getPrimeiroNome());
            // Busca o profissional pelo id
            Optional<Usuario> profissionalOptional = usuarioRepository.findById(id);
            // Se o profissional for encontrado, adiciona ao modelo
            if (profissionalOptional.isPresent()) {
                Usuario profissional = profissionalOptional.get();
                model.addAttribute("profissional", profissional);
                return "client/agendar"; // Retorna a página de agendamento
            } else {
                model.addAttribute("erro", "Profissional não encontrado");
                return "redirect:/home"; // Redireciona caso não encontre o profissional
            }

        }
        return "redirect:/cliente/login";
    }

    // Endpoint para salvar o agendamento
    @PostMapping("/salvarAgendamento")
    public String salvarAgendamento(@RequestParam("profissionalId") long profissionalId,
                                    @RequestParam("dataHora") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora,
                                    @RequestParam String condicoes, @RequestParam String observacoes, 
                                    HttpSession session, RedirectAttributes attributes, Model model) {

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            return "redirect:/cliente/login";
        }
        Usuario profissional = usuarioRepository.findById(profissionalId).orElse(null);
        if (profissional == null) {
            model.addAttribute("erro", "Profissional não encontrado.");
            return "client/agendar";
        }
        // Verificar se a data é retroativa
        if (dataHora.isBefore(LocalDateTime.now())) {
            model.addAttribute("erro", "Não é possível realizar um agendamento em uma data passada.");
            model.addAttribute("profissional", profissional);
            return "client/agendar";
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(clienteLogado);
        agendamento.setProfissional(profissional);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus("Agendado");
        agendamento.setCondicoes(condicoes);
        agendamento.setObservacoes(observacoes);

        agendamentoRepository.save(agendamento);

        attributes.addFlashAttribute("mensagem", "Agendamento realizado com sucesso!");
        return "redirect:/home";
    }


    /*
    @PostMapping("/salvarAgendamento")
    public String salvarAgendamento(@Valid @RequestParam("profissionalId") long profissionalId,
            @RequestParam("dataHora") LocalDateTime dataHora, BindingResult result, HttpSession session, Model model) {

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            return "redirect:/cliente/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("erro", "Erro ao agendar. Verifique os dados e tente novamente.");
            return "client/agendar";
        }

        Usuario profissional = usuarioRepository.findById(profissionalId).orElse(null);
        if (profissional == null) {
            model.addAttribute("erro", "Profissional não encontrado.");
            return "client/agendar";
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(clienteLogado);
        agendamento.setProfissional(profissional);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus("Agendado");

        agendamentoRepository.save(agendamento);

        model.addAttribute("mensagem", "Agendamento realizado com sucesso!");
        return "redirect:/cliente/agendamentos";
    } */

}
