package com.puc.project.rentify.repository;

import com.puc.project.rentify.model.Locadora;
import com.puc.project.rentify.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocadoraRepository extends JpaRepository<Locadora, Long> {
    Boolean existsLocadoraByCnpj(String cnpj);
    Boolean existsLocadoraByUsuario(Usuario usuario);
}
