package com.agendamento.cabeleireiro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agendamento.cabeleireiro.model.Agendamento;
import com.agendamento.cabeleireiro.model.Cliente;
import com.agendamento.cabeleireiro.model.Usuario;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Método para encontrar agendamentos por cliente
    List<Agendamento> findByCliente(Cliente cliente);
    List<Agendamento> findByProfissional(Usuario profissional);
    List<Agendamento> findByProfissionalOrderByDataHoraDesc(Usuario profissional);

    
}
