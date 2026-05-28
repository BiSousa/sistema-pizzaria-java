package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.ItemPedido;
import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByPedido(Pedido pedido);
    List<ItemPedido> findByPizza(Pizza pizza);

    @Query("SELECT ing.nome, SUM(i.quantidade) as total " +
            "FROM ItemPedido i JOIN i.pizza.ingredientes ing " +
            "GROUP BY ing.nome ORDER BY total DESC")
    List<Object[]> findIngredientesMaisUtilizados();

    @Query("SELECT t.tamanho, SUM(i.quantidade) as total " +
            "FROM ItemPedido i " +
            "JOIN i.pizza p " +
            "JOIN p.tamanhos t " +
            "GROUP BY t.tamanho ORDER BY total DESC")
    List<Object[]> findPedidosPorTamanho();
}
