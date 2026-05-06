package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.model.TamanhoPizza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    List<Pizza> findByTamanho(TamanhoPizza tamanho);
    Page<Pizza> findByTamanho(TamanhoPizza tamanho, Pageable pageable);
    List<Pizza> findByCategoria(Categoria categoria);
    Page<Pizza> findByCategoria(Categoria categoria, Pageable pageable);
}
