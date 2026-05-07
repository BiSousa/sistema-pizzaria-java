package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario salvarCliente(String email) {
        Usuario u = new Usuario();
        u.setNome("Cliente");
        u.setEmail(email);
        u.setSenha("senha");
        u.setTipo(TipoUsuario.CLIENTE);
        return usuarioRepository.save(u);
    }

    private Pedido salvarPedido(Usuario cliente, StatusPedido status) {
        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setEnderecoEntrega("Rua A, 123");
        p.setStatus(status);
        p.setPrecoTotal(30.0);
        return pedidoRepository.save(p);
    }

    @Test
    @DisplayName("findByCliente retorna pedidos do cliente informado")
    void findByClienteRetornaCorreto() {
        Usuario cliente = salvarCliente("cliente@email.com");
        salvarPedido(cliente, StatusPedido.PENDENTE);

        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);

        assertThat(pedidos).hasSize(1);
        assertThat(pedidos.get(0).getCliente().getEmail()).isEqualTo("cliente@email.com");
    }

    @Test
    @DisplayName("findByStatus retorna apenas pedidos com o status informado")
    void findByStatusRetornaCorreto() {
        Usuario cliente = salvarCliente("cliente2@email.com");
        salvarPedido(cliente, StatusPedido.PENDENTE);
        salvarPedido(cliente, StatusPedido.CONFIRMADO);

        List<Pedido> pendentes = pedidoRepository.findByStatus(StatusPedido.PENDENTE);

        assertThat(pendentes).hasSize(1);
        assertThat(pendentes.get(0).getStatus()).isEqualTo(StatusPedido.PENDENTE);
    }

    @Test
    @DisplayName("findByStatus retorna lista vazia quando não há pedidos com o status")
    void findByStatusRetornaVazioQuandoNaoExiste() {
        List<Pedido> resultado = pedidoRepository.findByStatus(StatusPedido.REJEITADO);

        assertThat(resultado).isEmpty();
    }
}