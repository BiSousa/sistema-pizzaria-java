package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.*;
import com.pizzaria.sistema.repository.ItemPedidoRepository;
import com.pizzaria.sistema.repository.PedidoRepository;
import com.pizzaria.sistema.repository.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemPedidoServiceTest {

    private ItemPedidoRepository itemPedidoRepository;
    private PedidoRepository pedidoRepository;
    private PizzaRepository pizzaRepository;
    private ItemPedidoService itemPedidoService;

    @BeforeEach
    void setUp() {
        itemPedidoRepository = Mockito.mock(ItemPedidoRepository.class);
        pedidoRepository = Mockito.mock(PedidoRepository.class);
        pizzaRepository = Mockito.mock(PizzaRepository.class);
        itemPedidoService = new ItemPedidoService(itemPedidoRepository, pedidoRepository, pizzaRepository);
    }

    private Pedido pedidoValido() {
        Pedido p = new Pedido();
        p.setId(1L);
        return p;
    }

    private Pizza pizzaValida() {
        Pizza p = new Pizza();
        p.setId(1L);
        return p;
    }

    private ItemPedido itemValido() {
        ItemPedido item = new ItemPedido();
        item.setQuantidade(2);
        item.setPrecoUnitario(30.0);
        item.setPedido(pedidoValido());
        item.setPizza(pizzaValida());
        return item;
    }

    @Test
    @DisplayName("Deve salvar item válido")
    void deveSalvarItemValido() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(pizzaRepository.existsById(1L)).thenReturn(true);
        when(itemPedidoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemPedido salvo = itemPedidoService.salvar(itemValido());

        assertNotNull(salvo);
        verify(itemPedidoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se quantidade for zero")
    void deveLancarExcecaoSeQuantidadeZero() {
        ItemPedido item = itemValido();
        item.setQuantidade(0);

        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(pizzaRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> itemPedidoService.salvar(item));
        verify(itemPedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se quantidade for negativa")
    void deveLancarExcecaoSeQuantidadeNegativa() {
        ItemPedido item = itemValido();
        item.setQuantidade(-1);

        assertThrows(IllegalArgumentException.class, () -> itemPedidoService.salvar(item));
        verify(itemPedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se preço unitário for negativo")
    void deveLancarExcecaoSePrecoNegativo() {
        ItemPedido item = itemValido();
        item.setPrecoUnitario(-5.0);

        assertThrows(IllegalArgumentException.class, () -> itemPedidoService.salvar(item));
        verify(itemPedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se pedido não existir no banco")
    void deveLancarExcecaoSePedidoInvalido() {
        when(pedidoRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> itemPedidoService.salvar(itemValido()));
        verify(itemPedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se pizza não existir no banco")
    void deveLancarExcecaoSePizzaInvalida() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(pizzaRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> itemPedidoService.salvar(itemValido()));
        verify(itemPedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar item inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(itemPedidoRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> itemPedidoService.deletar(99L));
        verify(itemPedidoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve deletar item existente")
    void deveDeletarItemExistente() {
        when(itemPedidoRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> itemPedidoService.deletar(1L));
        verify(itemPedidoRepository).deleteById(1L);
    }
}