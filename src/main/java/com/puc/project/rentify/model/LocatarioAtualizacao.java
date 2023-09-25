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
public class LocatarioAtualizacao {
    private String cpf;
    private String celular;
    private LocalDate data_nascimento;
    private String cep;
    private String numero;
    private String complemento;
    private Long usuario_id;
}
