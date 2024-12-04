package com.agendamento.cabeleireiro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.agendamento.cabeleireiro.model.Cliente;

import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController {

    @GetMapping("/home")
    public String paginaIniciaç(Model model, HttpSession session) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado != null) {
            model.addAttribute("nomeCliente", clienteLogado.getPrimeiroNome());
            return "index";
        }
        return "index";
    }
    
    @GetMapping("/servicos")
    public String paginaServicos() {
        return "pagina";
    }

    @GetMapping("/agendamento") 
    public String paginaAgendamento() {
        return "pagina";
    }
    
    @GetMapping("/login")
    public String loginUsuario() {
        return "client/login-cliente";
    
    }
    @GetMapping("/curiosidades")
    public String curiosidadesCabelos() {
        return "public/curiosidades";
    }

}
