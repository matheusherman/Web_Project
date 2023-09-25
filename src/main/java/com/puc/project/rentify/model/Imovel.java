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
public class Imovel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Hidden
    private long id;

    @Column(nullable = false)
    private Double preco_dia;
    @Column(nullable = false)
    private String tipo;
    @Column(nullable = false)
    private Integer num_quartos;
    @Column(nullable = false)
    private Integer num_banheiros;
    @Column(nullable = false)
    private String varanda;
    private String garagem;
    @Column(nullable = false)
    private String imobiliado;
    @Column(nullable = false)
    private String descricao;
    @Column(nullable = false)
    private String cep;
    @Column(nullable = false)
    private String numero;
    private String complemento;
    @ManyToOne()
    private Locadora locadora;
}
