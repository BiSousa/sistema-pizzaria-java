package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByTipo(TipoUsuario tipo);
}
