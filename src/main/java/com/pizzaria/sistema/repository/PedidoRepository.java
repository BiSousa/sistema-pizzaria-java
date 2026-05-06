package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.StatusPedido;
import com.pizzaria.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(Usuario cliente);
    List<Pedido> findByStatus(StatusPedido status);
}
