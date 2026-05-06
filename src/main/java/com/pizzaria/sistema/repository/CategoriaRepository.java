package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByDescricao(String descricao);
    boolean existsByDescricao(String descricao);
}
