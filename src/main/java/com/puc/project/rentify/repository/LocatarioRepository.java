package com.puc.project.rentify.repository;

import com.puc.project.rentify.model.Locatario;
import com.puc.project.rentify.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocatarioRepository extends JpaRepository<Locatario, Long> {
    Boolean existsLocatarioByCpf(String cpf);
    Boolean existsLocatarioByUsuario(Usuario usuario);
}
