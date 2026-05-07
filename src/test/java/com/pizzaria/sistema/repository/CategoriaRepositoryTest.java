package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria salvarCategoria(String descricao) {
        Categoria c = new Categoria();
        c.setDescricao(descricao);
        return categoriaRepository.save(c);
    }

    @Test
    @DisplayName("findByDescricao retorna categoria quando existe")
    void findByDescricaoRetornaQuandoExiste() {
        salvarCategoria("Tradicional");

        Optional<Categoria> resultado = categoriaRepository.findByDescricao("Tradicional");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescricao()).isEqualTo("Tradicional");
    }

    @Test
    @DisplayName("findByDescricao retorna vazio quando não existe")
    void findByDescricaoRetornaVazioQuandoNaoExiste() {
        Optional<Categoria> resultado = categoriaRepository.findByDescricao("Inexistente");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("existsByDescricao retorna true quando existe")
    void existsByDescricaoRetornaTrueQuandoExiste() {
        salvarCategoria("Especial");

        assertThat(categoriaRepository.existsByDescricao("Especial")).isTrue();
    }

    @Test
    @DisplayName("existsByDescricao retorna false quando não existe")
    void existsByDescricaoRetornaFalseQuandoNaoExiste() {
        assertThat(categoriaRepository.existsByDescricao("Inexistente")).isFalse();
    }
}