package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.StatusPedido;
import com.pizzaria.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(Usuario cliente);
    List<Pedido> findByStatus(StatusPedido status);

    @Query("SELECT i.pizza.id, i.pizza.categoria.descricao, SUM(i.quantidade) as total " +
            "FROM ItemPedido i GROUP BY i.pizza.id, i.pizza.categoria.descricao " +
            "ORDER BY total DESC")
    List<Object[]> findPizzasMaisVendidas();

    @Query("SELECT p.cliente.id, p.cliente.nome, COUNT(p) as total " +
            "FROM Pedido p GROUP BY p.cliente.id, p.cliente.nome " +
            "ORDER BY total DESC")
    List<Object[]> findClientesQueMailsCompram();

    @Query("SELECT p.status, COUNT(p) as total FROM Pedido p GROUP BY p.status")
    List<Object[]> findPedidosPorStatus();
}
