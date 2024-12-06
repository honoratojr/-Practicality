package com.agendamento.cabeleireiro.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_agendamentos")
public class Agendamento implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Usuario profissional;

    private LocalDateTime dataHora;

    private String status;  // Ex: "Agendado", "Concluído", "Cancelado"

    private String condicoes; // Ex: "TEA, PCD, Fobia social, Outros"
    
    private String observacoes; // Detalhes adicionais fornecidos pelo cliente

    private String dataHoraFormatada; // Formatar data e hora nas páginas

    

}