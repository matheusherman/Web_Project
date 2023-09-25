package com.puc.project.rentify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImovelRegistro {

    private Double preco_dia;
    private String tipo;
    private Integer num_quartos;
    private Integer num_banheiros;
    private String varanda;
    private String garagem;
    private String imobiliado;
    private String descricao;
    private String cep;
    private String numero;
    private String complemento;
    private Long locadora_id;
}
