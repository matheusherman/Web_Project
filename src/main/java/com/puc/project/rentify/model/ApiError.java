package com.puc.project.rentify.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Data
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }

    public ApiError(String code){
        super();
        if (code == "500") {
            this.status = HttpStatus.INTERNAL_SERVER_ERROR;
            this.message = "Erro não esperado no servidor";
        } else if (code == "404") {
            this.status = HttpStatus.NOT_FOUND;
            this.message = "ID não encontrado";
        } else if (code == "403") {
            this.status = HttpStatus.FORBIDDEN;
            this.message = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente";
        }
        errors = Arrays.asList(code);
    }

    public ApiError(String code, String message){
        super();
        if (code == "406") {
            this.status = HttpStatus.NOT_ACCEPTABLE;
        }
        this.message = message;
        errors = Arrays.asList(code);
    }
}
