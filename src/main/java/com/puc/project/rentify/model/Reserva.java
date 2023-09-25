package com.puc.project.rentify.model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Hidden
    private long id;

    @Column(nullable = false)
    private LocalDate data_inicio;
    @Column(nullable = false)
    private LocalDate data_fim;
    @Column(nullable = false)
    private Double valor_reserva;
    @ManyToOne()
    private Locatario locatario;
    @ManyToOne()
    private Imovel imovel;

}
