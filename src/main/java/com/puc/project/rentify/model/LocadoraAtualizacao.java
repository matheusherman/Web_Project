package com.puc.project.rentify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocadoraAtualizacao {
    private String nome_fantasia;
    private String cnpj;
    private String telefone;
    private String cep;
    private String numero;
    private String complemento;
    private Long usuario_id;
}
