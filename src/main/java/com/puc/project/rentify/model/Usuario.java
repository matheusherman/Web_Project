package com.puc.project.rentify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Usuario{
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Hidden
        private long id;

        @Column(unique=true, nullable = false)
        private String usuario;
        @Column(unique=true, nullable = false)
        private String email;
        @JsonIgnore
        @Column(unique=true, nullable = false)
        private String senha;
}

