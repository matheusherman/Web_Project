package com.puc.project.rentify.repository;

import com.puc.project.rentify.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByUsuario(String Usuario);
    Boolean existsUsuarioByUsuario(String Usuario);

    Boolean existsUsuarioByEmail(String Email);
    Usuario findByUsuarioAndSenha(String Usuario,String Senha);

}
