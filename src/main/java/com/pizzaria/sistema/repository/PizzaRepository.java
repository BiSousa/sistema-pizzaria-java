package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.model.TamanhoPizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    List<Pizza> findByCategoria(Categoria categoria);
    Page<Pizza> findByCategoria(Categoria categoria, Pageable pageable);

    List<Pizza> findByTamanhos_Tamanho(TamanhoPizza tamanho);
    Page<Pizza> findByTamanhos_Tamanho(TamanhoPizza tamanho, Pageable pageable);

    // Novo método para carregar pizzas com tamanhos
    @Query("SELECT DISTINCT p FROM Pizza p LEFT JOIN FETCH p.tamanhos")
    List<Pizza> findAllComTamanhos();
}
