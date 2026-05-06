package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.ItemPedido;
import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.repository.ItemPedidoRepository;
import com.pizzaria.sistema.repository.PedidoRepository;
import com.pizzaria.sistema.repository.PizzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final PizzaRepository pizzaRepository;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository,
                             PedidoRepository pedidoRepository,
                             PizzaRepository pizzaRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.pizzaRepository = pizzaRepository;
    }

    public List<ItemPedido> listarTodos() {
        return itemPedidoRepository.findAll();
    }

    public Optional<ItemPedido> buscarPorId(Long id) {
        return itemPedidoRepository.findById(id);
    }

    public List<ItemPedido> listarPorPedido(Pedido pedido) {
        return itemPedidoRepository.findByPedido(pedido);
    }

    public List<ItemPedido> listarPorPizza(Pizza pizza) {
        return itemPedidoRepository.findByPizza(pizza);
    }

    @Transactional
    public ItemPedido salvar(ItemPedido item) {
        validarItem(item);
        return itemPedidoRepository.save(item);
    }

    @Transactional
    public ItemPedido atualizar(Long id, ItemPedido dados) {
        ItemPedido existente = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ItemPedido não encontrado"));

        if (dados.getQuantidade() != null) existente.setQuantidade(dados.getQuantidade());
        if (dados.getPrecoUnitario() != null) existente.setPrecoUnitario(dados.getPrecoUnitario());

        if (dados.getPedido() != null) {
            Long pedidoId = dados.getPedido().getId();
            if (pedidoId == null || !pedidoRepository.existsById(pedidoId)) {
                throw new IllegalArgumentException("Pedido inválido");
            }
            existente.setPedido(pedidoRepository.findById(pedidoId).orElseThrow());
        }

        if (dados.getPizza() != null) {
            Long pizzaId = dados.getPizza().getId();
            if (pizzaId == null || !pizzaRepository.existsById(pizzaId)) {
                throw new IllegalArgumentException("Pizza inválida");
            }
            existente.setPizza(pizzaRepository.findById(pizzaId).orElseThrow());
        }

        return itemPedidoRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!itemPedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("ItemPedido não encontrado");
        }
        itemPedidoRepository.deleteById(id);
    }

    private void validarItem(ItemPedido item) {
        if (item == null) throw new IllegalArgumentException("ItemPedido não pode ser nulo");
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        if (item.getPrecoUnitario() == null || item.getPrecoUnitario() < 0) {
            throw new IllegalArgumentException("Preço unitário inválido");
        }
        if (item.getPedido() == null || item.getPedido().getId() == null
                || !pedidoRepository.existsById(item.getPedido().getId())) {
            throw new IllegalArgumentException("Pedido inválido");
        }
        if (item.getPizza() == null || item.getPizza().getId() == null
                || !pizzaRepository.existsById(item.getPizza().getId())) {
            throw new IllegalArgumentException("Pizza inválida");
        }
    }
}
