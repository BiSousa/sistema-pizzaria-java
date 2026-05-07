package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.model.TamanhoPizza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PizzaRepositoryTest {

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria salvarCategoria(String descricao) {
        Categoria c = new Categoria();
        c.setDescricao(descricao);
        return categoriaRepository.save(c);
    }

    private Pizza salvarPizza(TamanhoPizza tamanho, Double preco, Categoria categoria) {
        Pizza p = new Pizza();
        p.setTamanho(tamanho);
        p.setPreco(preco);
        p.setCategoria(categoria);
        return pizzaRepository.save(p);
    }

    @Test
    @DisplayName("findByTamanho retorna apenas pizzas do tamanho informado")
    void findByTamanhoRetornaCorreto() {
        Categoria cat = salvarCategoria("Tradicional");
        salvarPizza(TamanhoPizza.GRANDE, 45.0, cat);
        salvarPizza(TamanhoPizza.BROTO, 25.0, cat);

        List<Pizza> grandes = pizzaRepository.findByTamanho(TamanhoPizza.GRANDE);

        assertThat(grandes).hasSize(1);
        assertThat(grandes.get(0).getTamanho()).isEqualTo(TamanhoPizza.GRANDE);
    }

    @Test
    @DisplayName("findByCategoria retorna apenas pizzas da categoria informada")
    void findByCategoriaRetornaCorreto() {
        Categoria cat1 = salvarCategoria("Tradicional");
        Categoria cat2 = salvarCategoria("Especial");
        salvarPizza(TamanhoPizza.GRANDE, 45.0, cat1);
        salvarPizza(TamanhoPizza.BROTO, 25.0, cat2);

        List<Pizza> tradicionais = pizzaRepository.findByCategoria(cat1);

        assertThat(tradicionais).hasSize(1);
        assertThat(tradicionais.get(0).getCategoria().getDescricao()).isEqualTo("Tradicional");
    }

    @Test
    @DisplayName("findByTamanho retorna lista vazia quando não há pizzas do tamanho")
    void findByTamanhoRetornaVazioQuandoNaoExiste() {
        List<Pizza> resultado = pizzaRepository.findByTamanho(TamanhoPizza.BROTO);

        assertThat(resultado).isEmpty();
    }
}