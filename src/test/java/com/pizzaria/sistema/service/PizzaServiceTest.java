package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.*;
import com.pizzaria.sistema.repository.CategoriaRepository;
import com.pizzaria.sistema.repository.IngredienteRepository;
import com.pizzaria.sistema.repository.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PizzaServiceTest {

    private PizzaRepository pizzaRepository;
    private CategoriaRepository categoriaRepository;
    private IngredienteRepository ingredienteRepository;
    private PizzaService pizzaService;

    @BeforeEach
    void setUp() {
        pizzaRepository = Mockito.mock(PizzaRepository.class);
        categoriaRepository = Mockito.mock(CategoriaRepository.class);
        ingredienteRepository = Mockito.mock(IngredienteRepository.class);
        pizzaService = new PizzaService(pizzaRepository, categoriaRepository, ingredienteRepository);
    }

    private Categoria categoriaValida() {
        Categoria c = new Categoria();
        c.setId(1L);
        c.setDescricao("Tradicional");
        return c;
    }

    private Pizza pizzaValida() {
        Pizza p = new Pizza();
        p.setTamanho(TamanhoPizza.GRANDE);
        p.setPreco(45.0);
        p.setCategoria(categoriaValida());
        return p;
    }

    @Test
    @DisplayName("Deve salvar pizza válida")
    void deveSalvarPizzaValida() {
        Pizza pizza = pizzaValida();
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaValida()));
        when(pizzaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pizza salva = pizzaService.salvar(pizza);

        assertNotNull(salva);
        verify(pizzaRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se tamanho for nulo")
    void deveLancarExcecaoSeTamanhoNulo() {
        Pizza pizza = pizzaValida();
        pizza.setTamanho(null);

        assertThrows(IllegalArgumentException.class, () -> pizzaService.salvar(pizza));
        verify(pizzaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se preço for negativo")
    void deveLancarExcecaoSePrecoNegativo() {
        Pizza pizza = pizzaValida();
        pizza.setPreco(-10.0);

        assertThrows(IllegalArgumentException.class, () -> pizzaService.salvar(pizza));
        verify(pizzaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se preço for nulo")
    void deveLancarExcecaoSePrecoNulo() {
        Pizza pizza = pizzaValida();
        pizza.setPreco(null);

        assertThrows(IllegalArgumentException.class, () -> pizzaService.salvar(pizza));
        verify(pizzaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se categoria não existir no banco")
    void deveLancarExcecaoSeCategoriaInvalida() {
        Pizza pizza = pizzaValida();
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pizzaService.salvar(pizza));
        verify(pizzaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve salvar pizza com ingredientes válidos")
    void deveSalvarPizzaComIngredientes() {
        Ingrediente ing = new Ingrediente();
        ing.setId(1L);
        ing.setNome("Queijo");

        Pizza pizza = pizzaValida();
        pizza.setIngredientes(List.of(ing));

        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaValida()));
        when(ingredienteRepository.existsById(1L)).thenReturn(true);
        when(ingredienteRepository.findAllById(any())).thenReturn(List.of(ing));
        when(pizzaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pizza salva = pizzaService.salvar(pizza);

        assertNotNull(salva);
        assertEquals(1, salva.getIngredientes().size());
    }

    @Test
    @DisplayName("Deve lançar exceção se ingrediente não existir no banco")
    void deveLancarExcecaoSeIngredienteInvalido() {
        Ingrediente ing = new Ingrediente();
        ing.setId(99L);

        Pizza pizza = pizzaValida();
        pizza.setIngredientes(List.of(ing));

        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaValida()));
        when(ingredienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pizzaService.salvar(pizza));
        verify(pizzaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar pizza inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(pizzaRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pizzaService.deletar(99L));
        verify(pizzaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve listar pizzas por tamanho")
    void deveListarPorTamanho() {
        Pizza pizza = pizzaValida();
        when(pizzaRepository.findByTamanho(TamanhoPizza.GRANDE)).thenReturn(List.of(pizza));

        List<Pizza> resultado = pizzaService.listarPorTamanho(TamanhoPizza.GRANDE);

        assertEquals(1, resultado.size());
        assertEquals(TamanhoPizza.GRANDE, resultado.get(0).getTamanho());
    }

    @Test
    @DisplayName("Deve atualizar tamanho e preço da pizza")
    void deveAtualizarPizza() {
        Pizza existente = pizzaValida();
        existente.setId(1L);

        Pizza dados = new Pizza();
        dados.setTamanho(TamanhoPizza.BROTO);
        dados.setPreco(25.0);

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pizzaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pizza atualizada = pizzaService.atualizar(1L, dados);

        assertEquals(TamanhoPizza.BROTO, atualizada.getTamanho());
        assertEquals(25.0, atualizada.getPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar pizza inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        when(pizzaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> pizzaService.atualizar(99L, pizzaValida()));
    }

    @Test
    @DisplayName("Deve listar todas as pizzas")
    void deveListarTodasAsPizzas() {
        when(pizzaRepository.findAll()).thenReturn(List.of(pizzaValida(), pizzaValida()));

        List<Pizza> resultado = pizzaService.listarTodos();

        assertEquals(2, resultado.size());
    }
}