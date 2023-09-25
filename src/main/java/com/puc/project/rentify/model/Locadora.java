package com.puc.project.rentify.model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Locadora {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Hidden
    private long id;

    @Column(nullable = false)
    private String nome_fantasia;
    @Column(unique=true, nullable = false)
    private String cnpj;
    @Column(nullable = false)
    private String telefone;
    @Column(nullable = false)
    private String cep;
    @Column(nullable = false)
    private String numero;
    private String complemento;
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;
}
