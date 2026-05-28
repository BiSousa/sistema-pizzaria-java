package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.PizzaTamanho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaTamanhoRepository extends JpaRepository<PizzaTamanho, Long> {
}