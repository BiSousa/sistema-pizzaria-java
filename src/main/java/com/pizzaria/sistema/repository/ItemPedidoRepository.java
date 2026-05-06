package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.ItemPedido;
import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByPedido(Pedido pedido);
    List<ItemPedido> findByPizza(Pizza pizza);
}
