package com.puc.project.rentify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservaRegistro {
    private LocalDate data_inicio;
    private LocalDate data_fim;
    private Double valor_reserva;
    private Long locatario_id;
    private Long imovel_id;
}
