package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.*;
import com.pizzaria.sistema.repository.PedidoRepository;
import com.pizzaria.sistema.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    private PedidoRepository pedidoRepository;
    private UsuarioRepository usuarioRepository;
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pedidoRepository = Mockito.mock(PedidoRepository.class);
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        pedidoService = new PedidoService(pedidoRepository, usuarioRepository);
    }

    private Usuario clienteValido() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setTipo(TipoUsuario.CLIENTE);
        return u;
    }

    private ItemPedido itemValido() {
        ItemPedido item = new ItemPedido();
        item.setQuantidade(1);
        item.setPrecoUnitario(30.0);
        return item;
    }

    private Pedido pedidoValido() {
        Pedido p = new Pedido();
        p.setEnderecoEntrega("Rua A, 123");
        p.setCliente(clienteValido());
        p.setItens(List.of(itemValido()));
        p.setPrecoTotal(30.0);
        return p;
    }

    @Test
    @DisplayName("Deve salvar pedido válido")
    void deveSalvarPedidoValido() {
        Pedido pedido = pedidoValido();
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pedido salvo = pedidoService.salvar(pedido);

        assertNotNull(salvo);
        verify(pedidoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se endereço estiver em branco")
    void deveLancarExcecaoSeEnderecoEmBranco() {
        Pedido pedido = pedidoValido();
        pedido.setEnderecoEntrega("");

        assertThrows(IllegalArgumentException.class, () -> pedidoService.salvar(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se cliente for nulo")
    void deveLancarExcecaoSeClienteNulo() {
        Pedido pedido = pedidoValido();
        pedido.setCliente(null);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.salvar(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se cliente não existir no banco")
    void deveLancarExcecaoSeClienteNaoExistir() {
        Pedido pedido = pedidoValido();
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.salvar(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se lista de itens estiver vazia")
    void deveLancarExcecaoSeItensVazios() {
        Pedido pedido = pedidoValido();
        pedido.setItens(List.of());
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.salvar(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se preço total for negativo")
    void deveLancarExcecaoSePrecoTotalNegativo() {
        Pedido pedido = pedidoValido();
        pedido.setPrecoTotal(-1.0);
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.salvar(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar pedido inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.deletar(99L));
        verify(pedidoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve listar pedidos por status")
    void deveListarPorStatus() {
        Pedido p = pedidoValido();
        p.setStatus(StatusPedido.PENDENTE);
        when(pedidoRepository.findByStatus(StatusPedido.PENDENTE)).thenReturn(List.of(p));

        List<Pedido> resultado = pedidoService.listarPorStatus(StatusPedido.PENDENTE);

        assertEquals(1, resultado.size());
        assertEquals(StatusPedido.PENDENTE, resultado.get(0).getStatus());
    }
}