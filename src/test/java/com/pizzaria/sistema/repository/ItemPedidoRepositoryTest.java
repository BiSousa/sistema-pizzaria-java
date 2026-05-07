package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemPedidoRepositoryTest {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    private Usuario salvarCliente() {
        Usuario u = new Usuario();
        u.setNome("Cliente");
        u.setEmail("cliente@email.com");
        u.setSenha("senha");
        u.setTipo(TipoUsuario.CLIENTE);
        return usuarioRepository.save(u);
    }

    private Pedido salvarPedido(Usuario cliente) {
        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setEnderecoEntrega("Rua A, 123");
        p.setStatus(StatusPedido.PENDENTE);
        p.setPrecoTotal(30.0);
        return pedidoRepository.save(p);
    }

    private Pizza salvarPizza() {
        Pizza p = new Pizza();
        p.setTamanho(TamanhoPizza.GRANDE);
        p.setPreco(45.0);
        return pizzaRepository.save(p);
    }

    private ItemPedido salvarItem(Pedido pedido, Pizza pizza) {
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setPizza(pizza);
        item.setQuantidade(2);
        item.setPrecoUnitario(45.0);
        return itemPedidoRepository.save(item);
    }

    @Test
    @DisplayName("findByPedido retorna itens do pedido informado")
    void findByPedidoRetornaCorreto() {
        Usuario cliente = salvarCliente();
        Pedido pedido = salvarPedido(cliente);
        Pizza pizza = salvarPizza();
        salvarItem(pedido, pizza);

        List<ItemPedido> itens = itemPedidoRepository.findByPedido(pedido);

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getPedido().getId()).isEqualTo(pedido.getId());
    }

    @Test
    @DisplayName("findByPizza retorna itens da pizza informada")
    void findByPizzaRetornaCorreto() {
        Usuario cliente = salvarCliente();
        Pedido pedido = salvarPedido(cliente);
        Pizza pizza = salvarPizza();
        salvarItem(pedido, pizza);

        List<ItemPedido> itens = itemPedidoRepository.findByPizza(pizza);

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getPizza().getId()).isEqualTo(pizza.getId());
    }

    @Test
    @DisplayName("findByPedido retorna lista vazia quando pedido não tem itens")
    void findByPedidoRetornaVazioQuandoSemItens() {
        Usuario cliente = salvarCliente();
        Pedido pedido = salvarPedido(cliente);

        List<ItemPedido> itens = itemPedidoRepository.findByPedido(pedido);

        assertThat(itens).isEmpty();
    }
}