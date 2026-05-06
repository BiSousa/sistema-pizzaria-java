package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    Optional<Ingrediente> findByNome(String nome);
    boolean existsByNome(String nome);
}
