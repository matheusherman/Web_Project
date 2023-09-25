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
public class Locatario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Hidden
    private long id;

    @Column(unique=true, nullable = false)
    private String cpf;
    @Column(nullable = false)
    private String celular;
    @Column(nullable = false)
    private LocalDate data_nascimento;
    @Column(nullable = false)
    private String cep;
    @Column(nullable = false)
    private String numero;
    private String complemento;
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;
}
