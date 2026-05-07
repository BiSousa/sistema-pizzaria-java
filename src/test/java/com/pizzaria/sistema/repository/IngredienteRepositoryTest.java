package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Ingrediente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IngredienteRepositoryTest {

    @Autowired
    private IngredienteRepository ingredienteRepository;

    private Ingrediente salvarIngrediente(String nome) {
        Ingrediente i = new Ingrediente();
        i.setNome(nome);
        return ingredienteRepository.save(i);
    }

    @Test
    @DisplayName("findByNome retorna ingrediente quando existe")
    void findByNomeRetornaQuandoExiste() {
        salvarIngrediente("Queijo");

        Optional<Ingrediente> resultado = ingredienteRepository.findByNome("Queijo");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Queijo");
    }

    @Test
    @DisplayName("findByNome retorna vazio quando não existe")
    void findByNomeRetornaVazioQuandoNaoExiste() {
        Optional<Ingrediente> resultado = ingredienteRepository.findByNome("Inexistente");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("existsByNome retorna true quando existe")
    void existsByNomeRetornaTrueQuandoExiste() {
        salvarIngrediente("Mussarela");

        assertThat(ingredienteRepository.existsByNome("Mussarela")).isTrue();
    }

    @Test
    @DisplayName("existsByNome retorna false quando não existe")
    void existsByNomeRetornaFalseQuandoNaoExiste() {
        assertThat(ingredienteRepository.existsByNome("Inexistente")).isFalse();
    }
}